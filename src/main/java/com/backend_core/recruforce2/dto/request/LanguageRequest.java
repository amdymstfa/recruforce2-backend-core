package com.backend_core.recruforce2.dto.request;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LanguageRequest {
    private String name;
    private String level;
}
