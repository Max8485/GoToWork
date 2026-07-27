package org.maxsid.work.core.repository;

import org.maxsid.work.core.entity.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
    Optional<UserSettings> findByUserId(Long userId);

    @Query("SELECT us.userId FROM UserSettings  us " +
            "WHERE NOT EXISTS (SELECT 1 FROM UserSchedule ushed " +
            "WHERE ushed.userId = us.userId)")
    List<Long> findUsersWithoutSchedule();

    boolean existsByUserId(Long userId);

}
