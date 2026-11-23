package com.shop.SweetStory.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA-сущность пользователя системы, отображаемая на таблицу {@code users}.
 * Хранит учётные данные, контактную информацию и признак прав администратора.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    /**
     * Первичный ключ пользователя, генерируемый базой данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Отображаемое имя пользователя.
     */
    @Column(nullable = false, name = "name")
    private String name;

    /**
     * Уникальная электронная почта пользователя (используется для аутентификации).
     */
    @Column(nullable = false, unique = true, length = 100, name = "email")
    private String email;

    /**
     * Захэшированный пароль пользователя.
     */
    @Column(nullable = false, name = "password_hash")
    private String passwordHash;

    /**
     * Контактный номер телефона пользователя.
     */
    @Column(nullable = false, name = "phone")
    private String phone;

    /**
     * Идентификатор или URL аватара пользователя (может отсутствовать).
     */
    @Column(nullable = true, name = "avatar")
    private String avatar;

    /**
     * Признак наличия прав администратора; по умолчанию {@code false}.
     */
    @Column(nullable = false, name = "is_admin")
    private Boolean isAdmin = false;

}