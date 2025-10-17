package org.maxsid.work.core.service;

import org.maxsid.work.core.entity.UserSettings;
import org.maxsid.work.core.route.RouteRequest;

public interface UserSettingsService {

    void saveUserSettingsForBot(RouteRequest request);
}
