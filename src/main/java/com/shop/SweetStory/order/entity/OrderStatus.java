package com.shop.SweetStory.order.entity;

import java.util.Locale;

/**
 * Перечисление возможных статусов заказа в системе SweetStory.
 * Поддерживает преобразование между строковыми значениями API (в нижнем регистре)
 * и константами перечисления.
 */
public enum OrderStatus {
    /**
     * Заказ создан и ожидает обработки.
     */
    PENDING,
    /**
     * Заказ находится в процессе приготовления или выполнения.
     */
    IN_PROGRESS,
    /**
     * Заказ выполнен и завершён.
     */
    COMPLETED,
    /**
     * Заказ отменён.
     */
    CANCELLED;

    /**
     * Преобразует строковое значение статуса из внешнего API в соответствующую константу {@link OrderStatus}.
     * Игнорирует пробелы по краям и регистр символов.
     *
     * @param value строковое представление статуса из запроса API
     * @return соответствующая константа {@link OrderStatus}
     * @throws IllegalArgumentException если строка пуста, равна {@code null} или не соответствует ни одному статусу
     */
    public static OrderStatus fromApiValue(String value){
        // Проверяем, что переданное значение не пустое
        if(value == null || value.isBlank()){
            throw new IllegalArgumentException("Order status must not be empty");
        }
        // Нормализуем строку (убираем пробелы и приводим к верхнему регистру в локали ROOT) и сопоставляем с enum
        return OrderStatus.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }

    /**
     * Преобразует текущую константу статуса в строковое представление для ответа API в нижнем регистре.
     *
     * @return имя константы в нижнем регистре
     */
    public String toApiValue(){
        // Приводим имя константы к нижнему регистру с использованием Locale.ROOT для независимости от локали системы
        return name().toLowerCase(Locale.ROOT);
    }

}