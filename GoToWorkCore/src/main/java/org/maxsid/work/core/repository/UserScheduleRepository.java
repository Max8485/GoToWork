package org.maxsid.work.core.repository;

import org.maxsid.work.core.entity.UserSchedule;
import org.maxsid.work.dto.UserNotificationData;
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

    @Query("SELECT new org.maxsid.work.dto.UserNotificationData(" +
            "us.userId, " +
            "u.homeAddress, " +
            "u.workAddress, " +
            "u.arrivalTimeToWork, " +
            "us.notificationTime, " +
            "us.lastNotificationDate, " +
            "us.enabled)" +

            " FROM UserSchedule us JOIN UserSettings u ON us.userId = u.userId" +
            " WHERE us.enabled = true AND us.notificationTime = :time")
    List<UserNotificationData> findUsersToNotify(@Param("time") LocalTime time);
}
