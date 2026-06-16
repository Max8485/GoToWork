package org.maxsid.work.core.repository;

import org.maxsid.work.core.entity.UserSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserScheduleRepository extends JpaRepository<UserSchedule, Long> {
    Optional<UserSchedule> findByUserId(Long userId);

    List<UserSchedule> findByEnabledTrue();
}
