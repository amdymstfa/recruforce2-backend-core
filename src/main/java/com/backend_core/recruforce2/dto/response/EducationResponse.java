package com.backend_core.recruforce2.dto.response;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EducationResponse {
    private Long id;
    private String degree;
    private String institution;
    private String field;
    private String startDate;
    private String endDate;
}
