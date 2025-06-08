package com.scrappy.scrappy.service;

import com.scrappy.scrappy.controller.dto.HolidayDTO;
import com.scrappy.scrappy.domain.Holiday;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class HolidayMapper {

    public HolidayDTO toDto(Holiday holiday) {
        HolidayDTO dto = new HolidayDTO();
        dto.setId(holiday.getId());
        dto.setDate(holiday.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        dto.setName(holiday.getName());
        dto.setType(holiday.getType());
        dto.setDescription(holiday.getDescription());
        return dto;
    }
}