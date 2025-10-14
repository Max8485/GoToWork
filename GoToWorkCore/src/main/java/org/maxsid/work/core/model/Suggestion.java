package org.maxsid.work.core.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Suggestion {

    private String value;
    private AddressData data;
}
