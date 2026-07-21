package org.maxsid.work.core.repository;

import org.maxsid.work.core.entity.UserSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserScheduleRepository extends JpaRepository<UserSchedule, Long> {
    Optional<UserSchedule> findByUserId(Long userId);

    List<UserSchedule> findByEnabledTrue();

    @Query("SELECT us FROM UserSchedule us WHERE us.enabled = true AND us.notificationTime = :time")
    List<UserSchedule> findByEnabledTrueAndNotificationTime(@Param("time") LocalTime time);
}
