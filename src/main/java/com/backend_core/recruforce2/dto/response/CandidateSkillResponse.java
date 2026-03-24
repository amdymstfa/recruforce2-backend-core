package com.backend_core.recruforce2.dto.response;
import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CandidateSkillResponse {
    private Long id;
    private String name;
    private String type;
    private String masteryLevel;
}
