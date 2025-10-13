package org.example.model;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Data
public class GraphHopperResponse {

    private List<Path> paths;
    private String message;

    @Data
    public static class Path {
        private Double distance;
        private Double time; // milliseconds
        private List<Point> points;

        @Data
        public static class Point {
            private Double lat;
            private Double lon;
        }
    }
}
