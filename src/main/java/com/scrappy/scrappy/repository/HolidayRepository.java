package com.scrappy.scrappy.repository;

import com.scrappy.scrappy.domain.HolidayEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayRepository extends JpaRepository<HolidayEntity, Long> {
}