package com.investmentapp.investment_app.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.investmentapp.investment_app.model.Country;
import com.investmentapp.investment_app.model.Language;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private UUID id;
    private String username;
    private String email;

    private String firstName;
    private String lastName;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("birthday")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate birthday;

    @JsonProperty("country_id")
    private Long countryId;
    private String country;

    @JsonProperty("language_id")
    private Long languageId;
    private String language;

    @JsonProperty("profile_pictureurl")
    private String profilePictureUrl;

    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updatedAt;

    private List<String> roles;

    public static UserResponse fromEntity(com.investmentapp.investment_app.model.User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .birthday(user.getBirthday())
                .countryId(Optional.ofNullable(user.getCountry()).map(Country::getId).orElse(null))
                .country(Optional.ofNullable(user.getCountry()).map(Country::getName).orElse(null))
                .languageId(Optional.ofNullable(user.getLanguage()).map(Language::getId).orElse(null))
                .language(Optional.ofNullable(user.getLanguage()).map(Language::getName).orElse(null))
                .profilePictureUrl(user.getProfilePictureUrl())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .roles(user.getRolesAsString())
                .build();
    }
}
