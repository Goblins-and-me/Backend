package com.shop.SweetStory.auth.repository;

import com.shop.SweetStory.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Репозиторий Spring Data JPA для доступа к сущностям пользователей {@link User}
 * и выполнения стандартных и производных запросов к таблице {@code users}.
 */
public interface UserRepository extends JpaRepository<User,Long> {

    /**
     * Находит пользователя по адресу электронной почты.
     *
     * @param email адрес электронной почты пользователя
     * @return {@link Optional} с найденным пользователем или пустой {@link Optional}, если запись отсутствует
     */
    Optional<User> findByEmail(String email);

    /**
     * Проверяет существование пользователя с указанным адресом электронной почты.
     *
     * @param email адрес электронной почты пользователя
     * @return {@code true}, если пользователь с таким email уже зарегистрирован, иначе {@code false}
     */
    boolean existsByEmail(String email);

    /**
     * Удаляет запись пользователя по адресу электронной почты.
     *
     * @param email адрес электронной почты пользователя
     */
    void deleteByEmail(String email);

}