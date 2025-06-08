package com.scrappy.scrappy.service;

import com.scrappy.scrappy.controller.dto.HolidayDTO;
import com.scrappy.scrappy.repository.HolidayRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HolidayService {

    private static final Logger logger = LoggerFactory.getLogger(HolidayService.class);
    private final HolidayRepository holidayRepository;
    private final HolidayMapper holidayMapper;

    public HolidayService(HolidayRepository holidayRepository, HolidayMapper holidayMapper) {
        this.holidayRepository = holidayRepository;
        this.holidayMapper = holidayMapper;
    }

    @Transactional(readOnly = true)
    public List<HolidayDTO> getAllHolidays() {
        logger.debug("Fetching all holidays");
        return holidayRepository.findAll().stream()
                .map(holidayMapper::toDto)
                .collect(Collectors.toList());
    }
}