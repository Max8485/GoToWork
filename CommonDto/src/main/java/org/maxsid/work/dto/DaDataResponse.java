package org.maxsid.work.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DaDataResponse {

    @JsonProperty("suggestions")
    private List<Suggestion> suggestions;

    @lombok.Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Suggestion {
        private String value;

        @JsonProperty("data")
        private AddressData addressData;
    }

    @lombok.Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AddressData {
        @JsonProperty("geo_lat")
        private String geoLat;

        @JsonProperty("geo_lon")
        private String geoLon;

        private String timezone;
    }
}
