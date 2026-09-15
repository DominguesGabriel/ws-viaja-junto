package com.viajajunto.api.modules.destination.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitedCountryDTO {
    private List<String> visitedIsoCodes;
    private int totalVisited;
}
