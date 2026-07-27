package org.maxsid.work.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GraphHopperResponse {

    @JsonProperty("paths")
    private List<Path> paths;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Path {

        @JsonProperty("time")
        private Long time;

        @JsonProperty("distance")
        private Double distance;

        @JsonProperty("weight")
        private Double weight;
    }
}
