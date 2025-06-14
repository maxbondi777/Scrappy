package com.scrappy.scrappy.service.holiday;

import com.scrappy.scrappy.controller.dto.holiday.HolidayDTO;
import com.scrappy.scrappy.domain.HolidayEntity;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class HolidayMapper {

    public HolidayDTO toDto(HolidayEntity holiday) {
        HolidayDTO dto = new HolidayDTO();
        dto.setId(holiday.getId());
        dto.setDate(holiday.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        dto.setName(holiday.getName());
        dto.setType(holiday.getType());
        dto.setDescription(holiday.getDescription());
        return dto;
    }
}