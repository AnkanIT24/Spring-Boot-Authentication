package net.ankan.ems.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateNameRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;
}