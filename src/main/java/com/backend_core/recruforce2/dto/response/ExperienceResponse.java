package com.backend_core.recruforce2.dto.response;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ExperienceResponse {
    private Long id;
    private String position;
    private String company;
    private String description;
    private String startDate;
    private String endDate;
    private Boolean isCurrent;
}
