package org.maxsid.work.core.model;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class DaDataResponse {

    private List<Suggestion> suggestions;
}
