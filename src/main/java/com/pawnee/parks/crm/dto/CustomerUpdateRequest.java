package com.pawnee.parks.crm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pawnee.parks.crm.domain.enums.CustomerStatus;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerUpdateRequest {
    private String firstName;
    private String lastName;
    private String phone;
    private String organization;
    private List<String> tags;
    private CustomerStatus status;
    private Instant lastInteractionAt;
    private String notes;
    private AddressDto address;
}
