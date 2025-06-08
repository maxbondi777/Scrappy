package com.scrappy.scrappy.repository;

import com.scrappy.scrappy.domain.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {
}