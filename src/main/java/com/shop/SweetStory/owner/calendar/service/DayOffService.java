package com.shop.SweetStory.owner.calendar.service;

import com.shop.SweetStory.owner.calendar.repository.DayOffRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DayOffService {

    private final DayOffRepository dayOffRepository;

    public DayOffService(DayOffRepository dayOffRepository) {
        this.dayOffRepository = dayOffRepository;
    }

    public

}
