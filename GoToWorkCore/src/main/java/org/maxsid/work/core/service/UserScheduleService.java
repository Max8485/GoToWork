package org.maxsid.work.core.service;

import org.maxsid.work.core.entity.UserSchedule;

public interface UserScheduleService {
    UserSchedule enableNotifications(Long userId, boolean enabled);
}
