package org.maxsid.work.core.service;

import org.maxsid.work.core.coordinates.Coordinates;

public interface RouteService {

    Long calculateTravelTimeToWork(Coordinates start, Coordinates end);
}
