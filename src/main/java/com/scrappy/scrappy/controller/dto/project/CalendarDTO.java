package com.scrappy.scrappy.controller.dto.project;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CalendarDTO {
    private DateRange dateRange;
    private List<EmployeeShiftDTO> shifts;

    @Data
    public static class DateRange {
        private LocalDate start;
        private LocalDate end;
    }

    @Data
    public static class EmployeeShiftDTO {
        private String employeeId;
        private String name;
        private String position;
        private String role;
        private List<ShiftDTO> shifts;
    }
}