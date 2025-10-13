package org.example.model;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class AddressData {

    private String geo_lat;
    private String geo_lon;
    private String timezone;
}
