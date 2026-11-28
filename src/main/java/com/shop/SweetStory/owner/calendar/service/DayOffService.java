package com.shop.SweetStory.owner.calendar.service;

import com.shop.SweetStory.catalog.generalDto.ActionMessageResponse;
import com.shop.SweetStory.owner.calendar.dto.CreateDayOffRequest;
import com.shop.SweetStory.owner.calendar.dto.DayOffResponse;
import com.shop.SweetStory.owner.calendar.entity.DayOff;
import com.shop.SweetStory.owner.calendar.repository.DayOffRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class DayOffService {

    private final DayOffRepository dayOffRepository;

    public DayOffService(DayOffRepository dayOffRepository) {
        this.dayOffRepository = dayOffRepository;
    }

    public List<DayOffResponse> getDaysOff(LocalDate from, LocalDate to) {
        List<DayOff> daysOff;
        if (from != null && to != null) {
            if (from.isAfter(to)) {
                throw new IllegalArgumentException("Дата 'from' не может быть позже 'to'");
            }
            daysOff = dayOffRepository.findAllByDateBetweenOrderByDateAsc(from, to);
        } else {
            daysOff = dayOffRepository.findAllByOrderByDateAsc();
        }

        return daysOff.stream()
                .map(this::toResponse)
                .toList();
    }

    public boolean isDayOff(LocalDate date) {
        return date != null && dayOffRepository.existsByDate(date);
    }

    @Transactional
    public DayOffResponse addDayOff(CreateDayOffRequest request) {
        if (dayOffRepository.existsByDate(request.date())) {
            throw new IllegalStateException("Эта дата уже отмечена как выходной: " + request.date());
        }

        DayOff dayOff = new DayOff();
        dayOff.setDate(request.date());

        DayOff saved = dayOffRepository.save(dayOff);
        return toResponse(saved);
    }

    @Transactional
    public ActionMessageResponse removeDayOffById(Long id) {
        DayOff dayOff = dayOffRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Выходной не найден с id: " + id));

        dayOffRepository.delete(dayOff);
        return new ActionMessageResponse("success", "Выходной день удален");
    }

    @Transactional
    public ActionMessageResponse removeDayOffByDate(LocalDate date) {
        DayOff dayOff = dayOffRepository.findByDate(date)
                .orElseThrow(() -> new NoSuchElementException("Выходной не найден на дату: " + date));

        dayOffRepository.delete(dayOff);
        return new ActionMessageResponse("success", "Выходной день удален");
    }

    private DayOffResponse toResponse(DayOff dayOff) {
        return new DayOffResponse(
                dayOff.getId(),
                dayOff.getDate()
        );
    }
}