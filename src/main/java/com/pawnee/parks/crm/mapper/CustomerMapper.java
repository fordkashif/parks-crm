package com.pawnee.parks.crm.mapper;

import com.pawnee.parks.crm.domain.entity.Address;
import com.pawnee.parks.crm.domain.entity.Customer;
import com.pawnee.parks.crm.dto.AddressDto;
import com.pawnee.parks.crm.dto.CustomerCreateRequest;
import com.pawnee.parks.crm.dto.CustomerResponse;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public Customer toEntity(CustomerCreateRequest req) {
        if (req == null) return null;
        return Customer.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .email(req.getEmail())
                .phone(req.getPhone())
                .organization(req.getOrganization())
                .tags(req.getTags())
                .status(req.getStatus())
                .lastInteractionAt(req.getLastInteractionAt())
                .notes(req.getNotes())
                .address(toEmbeddable(req.getAddress()))
                .build();
    }

    public CustomerResponse toDto(Customer c) {
        if (c == null) return null;
        return CustomerResponse.builder()
                .id(c.getId())
                .firstName(c.getFirstName())
                .lastName(c.getLastName())
                .email(c.getEmail())
                .phone(c.getPhone())
                .organization(c.getOrganization())
                .tags(c.getTags())
                .status(c.getStatus())
                .lastInteractionAt(c.getLastInteractionAt())
                .notes(c.getNotes())
                .address(toDto(c.getAddress()))
                .createdBy(c.getCreatedBy())
                .updatedBy(c.getUpdatedBy())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }

    private Address toEmbeddable(AddressDto dto) {
        if (dto == null) return null;
        return Address.builder()
                .line1(dto.getLine1())
                .line2(dto.getLine2())
                .city(dto.getCity())
                .state(dto.getState())
                .postalCode(dto.getPostalCode())
                .build();
    }

    private AddressDto toDto(Address a) {
        if (a == null) return null;
        return AddressDto.builder()
                .line1(a.getLine1())
                .line2(a.getLine2())
                .city(a.getCity())
                .state(a.getState())
                .postalCode(a.getPostalCode())
                .build();
    }
}
