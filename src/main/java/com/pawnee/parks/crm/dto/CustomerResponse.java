package com.pawnee.parks.crm.dto;

import com.pawnee.parks.crm.domain.enums.CustomerStatus;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CustomerResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String organization;
    private List<String> tags;
    private CustomerStatus status;
    private Instant lastInteractionAt;
    private String notes;
    private AddressDto address;
    private String createdBy;
    private String updatedBy;
    private Instant createdAt;
    private Instant updatedAt;
}
