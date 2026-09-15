package com.viajajunto.api.integrations.google;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlacesResponseDTO {
    private List<PlaceResult> results;
    private String status;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlaceResult {
        private String placeId;
        private String name;
        private String formattedAddress;
        private Double rating;
        private Integer userRatingsTotal;
        private List<String> types;
        private Geometry geometry;
        private List<Photo> photos;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Geometry {
        private Location location;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Location {
        private Double lat;
        private Double lng;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Photo {
        private String photoReference;
        private Integer height;
        private Integer width;
    }
}
