package com.shop.SweetStory.owner.calendar.repository;

import com.shop.SweetStory.owner.calendar.entity.DayOff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DayOffRepository extends JpaRepository<DayOff, Long> {
    List<DayOff> findAllByOrderByDateAsc();

    List<DayOff> findAllByDateBetweenOrderByDateAsc(LocalDate from, LocalDate to);

    boolean existsByDate(LocalDate date);

    Optional<DayOff> findByDate(LocalDate date);
}
