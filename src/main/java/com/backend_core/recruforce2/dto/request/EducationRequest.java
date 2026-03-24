package com.backend_core.recruforce2.dto.request;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EducationRequest {
    private String degree;
    private String institution;
    private String field;
    @JsonProperty("start_date") private String startDate;
    @JsonProperty("end_date") private String endDate;
}
