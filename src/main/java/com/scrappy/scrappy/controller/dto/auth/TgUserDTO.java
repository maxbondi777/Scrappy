package com.scrappy.scrappy.controller.dto.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scrappy.scrappy.domain.Subscription;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TgUserDTO {
    @NotNull
    @JsonProperty("auth_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime authDate;

    @JsonProperty("hash")
    private String hash;

    @NotBlank
    @JsonProperty("query_id")
    private String queryId;

    @JsonProperty("signature")
    private String signature;

    @NotNull
    private User user;

    private Subscription subscription;

    @Data
    public static class User {
        @JsonProperty("allows_write_to_pm")
        private Boolean allowsWriteToPm;

        @NotBlank
        @JsonProperty("first_name")
        private String firstName;

        @NotNull
        private Long id;

        @JsonProperty("last_name")
        private String lastName;

        @JsonProperty("language_code")
        private String languageCode;

        @JsonProperty("photo_url")
        private String photoUrl;

        private String username; // Без @NotBlank, как предложено ранее
    }
}