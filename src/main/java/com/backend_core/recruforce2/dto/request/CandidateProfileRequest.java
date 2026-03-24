package com.backend_core.recruforce2.dto.request;
import lombok.*;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CandidateProfileRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String parsedCvId;
    private List<String> skills;
    private List<ExperienceRequest> experiences;
    private List<EducationRequest> educations;
    private List<LanguageRequest> languages;
}
