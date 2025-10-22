package com.pawnee.parks.crm.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AddressDto {
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String postalCode;
}
