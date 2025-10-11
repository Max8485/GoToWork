package org.example.service;

import org.example.coordinates.Coordinates;

public interface RouteService {

    Integer calculateTravelTimeToWork(Coordinates start, Coordinates end);
}
