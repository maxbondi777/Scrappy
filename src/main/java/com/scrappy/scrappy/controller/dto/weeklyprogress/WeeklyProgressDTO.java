package com.scrappy.scrappy.controller.dto.weeklyprogress;

import lombok.Data;

@Data
public class WeeklyProgressDTO {
    private DayProgressDTO monday;
    private DayProgressDTO tuesday;
    private DayProgressDTO wednesday;
    private DayProgressDTO thursday;
    private DayProgressDTO friday;
    private DayProgressDTO saturday;
    private DayProgressDTO sunday;

    @Data
    public static class DayProgressDTO {
        private long completed;
        private long total;
    }
}