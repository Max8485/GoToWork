package org.example.service;

import org.example.coordinates.Coordinates;

public interface GeocodeService{

    Coordinates geocodeAddress(String address);

    String detectTimezone(Coordinates coordinate);
}
