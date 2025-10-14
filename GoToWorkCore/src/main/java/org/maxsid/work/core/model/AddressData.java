package org.maxsid.work.core.model;

import lombok.*;

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
