package org.maxsid.work.core.service;

import org.maxsid.work.core.coordinates.Coordinates;

public interface GeocodeService{

    Coordinates geocodeAddress(String address);

//    String detectTimezone(Coordinates coordinate);
}
