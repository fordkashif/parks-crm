package com.pawnee.parks.crm.service;

import com.pawnee.parks.crm.domain.enums.CustomerStatus;
import com.pawnee.parks.crm.dto.CustomerCreateRequest;
import com.pawnee.parks.crm.dto.CustomerResponse;
import com.pawnee.parks.crm.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CustomerService {
    CustomerResponse create(CustomerCreateRequest request);
    CustomerResponse get(UUID id);
    PageResponse<CustomerResponse> list(CustomerStatus status, String search, Pageable pageable);
    CustomerResponse update(java.util.UUID id, com.pawnee.parks.crm.dto.CustomerUpdateRequest request);
    void delete(UUID id);
    String insight(UUID id);
}
