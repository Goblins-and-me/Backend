package com.shop.SweetStory.auth.service;

import com.shop.SweetStory.auth.dto.AuthResponse;
import com.shop.SweetStory.auth.dto.LoginRequest;
import com.shop.SweetStory.auth.dto.RegisterRequest;
import com.shop.SweetStory.auth.dto.UpdateProfileRequest;
import com.shop.SweetStory.auth.dto.UserProfileDto;
import com.shop.SweetStory.auth.entity.User;
import com.shop.SweetStory.auth.repository.UserRepository;
import com.shop.SweetStory.common.security.JwtService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис регистрации, аутентификации и управления профилем пользователя.
 * Отвечает за нормализацию email, хэширование паролей через {@link PasswordEncoder},
 * генерацию JWT-токенов через {@link JwtService}, работу с сущностями {@link User}
 * через {@link UserRepository} и управление транзакциями.
 * По умолчанию класс помечен как {@code @Transactional(readOnly = true)}, а методы,
 * изменяющие состояние в БД, явно переопределяют аннотацию на {@code @Transactional}.
 */
@Service
@Transactional(readOnly = true)
public class AuthService {

    // Репозиторий для чтения, сохранения и удаления пользователей в БД
    private final UserRepository userRepository;
    // Сервис выпуска и обновления JWT-токенов сессии
    private final JwtService jwtService;
    // Кодировщик для безопасного хэширования и проверки паролей
    private final PasswordEncoder passwordEncoder;

    /**
     * Создаёт сервис аутентификации с внедрёнными зависимостями.
     *
     * @param userRepository репозиторий пользователей
     * @param jwtService сервис генерации JWT-токенов
     * @param passwordEncoder кодировщик паролей
     */
    public AuthService(
            UserRepository userRepository,
            JwtService jwtService,
            PasswordEncoder passwordEncoder
    ) {
        // Сохраняем ссылки на зависимости компонента
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Вспомогательная запись (record), объединяющая готовый DTO ответа с профилем пользователя
     * и сгенерированный JWT-токен для установки в cookie на уровне контроллера.
     *
     * @param response DTO ответа с данными профиля пользователя
     * @param token сгенерированный JWT-токен сессии
     */
    public record AuthSession(AuthResponse response, String token) {
    }

    /**
     * Регистрирует нового пользователя и создаёт для него сессию с JWT-токеном.
     * Выполняется в транзакции на запись, проверяет уникальность нормализованного email,
     * хэширует пароль, назначает роль обычного пользователя ({@code isAdmin = false}),
     * сохраняет запись в БД и выпускает токен.
     *
     * @param request данные регистрации
     * @return сессия с профилем пользователя и токеном
     * @throws IllegalArgumentException если пользователь с таким email уже зарегистрирован
     */
    @Transactional
    public AuthSession register(RegisterRequest request) {
        // Приводим email к единому виду: убираем пробелы по краям и переводим в нижний регистр
        String normalizedEmail = request.email().trim().toLowerCase();

        // Проверяем, не занят ли указанный email уже существующим пользователем
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("user with this email already exists");
        }

        // Создаём новую сущность пользователя и заполняем её поля
        User user = new User();
        // Очищаем имя от крайних пробелов
        user.setName(request.name().trim());
        // Записываем нормализованный email
        user.setEmail(normalizedEmail);
        // Хэшируем пароль перед сохранением, чтобы не хранить его в открытом виде
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        // Очищаем телефон от крайних пробелов
        user.setPhone(request.phone().trim());
        // Явно запрещаем права администратора при самостоятельной регистрации
        user.setIsAdmin(false);

        // Сохраняем нового пользователя в БД и получаем сохранённую сущность с присвоенным id
        User saved = userRepository.save(user);
        // Генерируем JWT-токен для созданного пользователя
        String token = jwtService.generateToken(saved);

        // Возвращаем объект сессии с DTO профиля и токеном
        return new AuthSession(new AuthResponse(toUserProfileDto(saved)), token);
    }

    /**
     * Аутентифицирует пользователя по email и паролю и выдаёт JWT-токен.
     * Нормализует email, ищет пользователя в БД, проверяет соответствие пароля хэшу
     * и при успехе формирует сессию с токеном.
     *
     * @param request учётные данные для входа
     * @return сессия с профилем пользователя и токеном
     * @throws IllegalArgumentException если email не найден или пароль не совпадает
     */
    public AuthSession login(LoginRequest request) {
        // Нормализуем email так же, как при регистрации, чтобы поиск находил совпадения независимо от регистра и пробелов
        String normalizedEmail = request.email().trim().toLowerCase();

        // Ищем пользователя по email; при отсутствии выбрасываем единое сообщение об ошибке без раскрытия деталей
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("incorrect email or password"));

        // Проверяем введённый пароль против сохранённого хэша
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("incorrect email or password");
        }

        // Генерируем JWT-токен для аутентифицированной сессии
        String token = jwtService.generateToken(user);
        // Возвращаем сессию с профилем пользователя и токеном
        return new AuthSession(new AuthResponse(toUserProfileDto(user)), token);
    }

    /**
     * Частично обновляет профиль пользователя и перевыпускает JWT-токен сессии.
     * Изменяет только переданные поля {@code name}, {@code phone} и {@code avatar},
     * сохраняет сущность в БД и выпускает обновлённый токен.
     *
     * @param email email текущего аутентифицированного пользователя
     * @param request поля для обновления профиля
     * @return обновлённая сессия с профилем и новым токеном
     * @throws IllegalArgumentException если пользователь не найден
     * @throws IllegalStateException если отсутствует email текущего пользователя
     */
    @Transactional
    public AuthSession updateProfile(String email, UpdateProfileRequest request) {
        // Находим текущего пользователя по нормализованному email
        User user = findByNormalizedEmail(email);

        // Обновляем имя только если оно передано и не состоит из одних пробелов
        if (request.name() != null && !request.name().isBlank()) {
            user.setName(request.name().trim());
        }
        // Обновляем телефон только если он передан и не пустой
        if (request.phone() != null && !request.phone().isBlank()) {
            user.setPhone(request.phone().trim());
        }
        // Если поле avatar передано: пустую/пробельную строку превращаем в null, иначе сохраняем обрезанное значение
        if (request.avatar() != null) {
            user.setAvatar(request.avatar().isBlank() ? null : request.avatar().trim());
        }

        // Сохраняем обновлённую сущность пользователя в БД
        User saved = userRepository.save(user);
        // Перевыпускаем JWT-токен на случай, если данные токена зависят от актуального состояния пользователя
        String refreshedToken = jwtService.generateToken(saved);
        // Возвращаем обновлённую сессию с актуальным профилем и новым токеном
        return new AuthSession(new AuthResponse(toUserProfileDto(saved)), refreshedToken);
    }

    /**
     * Удаляет учётную запись текущего пользователя с немедленным сбросом изменений в БД.
     * Вызывает {@code flush()} внутри транзакции, чтобы сразу перехватить нарушение внешнего ключа
     * (например, если у пользователя есть связанные заказы) и вернуть понятное исключение.
     *
     * @param email email текущего аутентифицированного пользователя
     * @throws IllegalArgumentException если пользователь не найден
     * @throws IllegalStateException если отсутствует email или у пользователя есть связанные заказы
     */
    @Transactional
    public void deleteCurrentAccount(String email) {
        // Находим пользователя, чей аккаунт требуется удалить
        User user = findByNormalizedEmail(email);
        try {
            // Помечаем сущность на удаление и сразу сбрасываем SQL-команды в БД
            userRepository.delete(user);
            userRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            // Перехватываем ошибку целостности данных (например, при наличии связанных заказов)
            throw new IllegalStateException("Cannot delete account while user has associated orders");
        }
    }

    /**
     * Проверяет наличие email, нормализует его и загружает соответствующую сущность {@link User}.
     *
     * @param email исходный email пользователя
     * @return найденная сущность пользователя
     * @throws IllegalStateException если email не передан или пуст
     * @throws IllegalArgumentException если пользователь с таким email не найден
     */
    private User findByNormalizedEmail(String email) {
        // Убеждаемся, что email присутствует в контексте аутентификации
        if (email == null || email.isBlank()) {
            throw new IllegalStateException("authenticated user email is missing");
        }
        // Приводим email к нижнему регистру без лишних пробелов
        String normalizedEmail = email.trim().toLowerCase();
        // Ищем пользователя в репозитории или выбрасываем исключение
        return userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
    }

    /**
     * Преобразует сущность БД {@link User} в безопасный DTO {@link UserProfileDto}
     * (без поля хэша пароля и с гарантированным булевым значением {@code isAdmin}).
     *
     * @param user сущность пользователя
     * @return DTO профиля пользователя для отправки клиенту
     */
    private UserProfileDto toUserProfileDto(User user) {
        return new UserProfileDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getAvatar(),
                // Безопасно приводим Boolean к примитиву/объекту без риска NullPointerException
                Boolean.TRUE.equals(user.getIsAdmin())
        );
    }
}