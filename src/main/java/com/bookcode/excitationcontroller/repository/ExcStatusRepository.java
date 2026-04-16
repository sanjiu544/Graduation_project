package com.bookcode.excitationcontroller.repository;

import com.bookcode.excitationcontroller.entity.ExcStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExcStatusRepository extends JpaRepository<ExcStatus, Long> {
}