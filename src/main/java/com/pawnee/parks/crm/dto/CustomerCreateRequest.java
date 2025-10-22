package com.pawnee.parks.crm.dto;

import com.pawnee.parks.crm.domain.enums.CustomerStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CustomerCreateRequest {

    @NotBlank private String firstName;
    @NotBlank private String lastName;

    @NotBlank @Email private String email;

    private String phone;
    private String organization;
    private List<String> tags;
    private CustomerStatus status; // optional; default ACTIVE in service if null

    private Instant lastInteractionAt;
    private String notes;
    private AddressDto address;
}
