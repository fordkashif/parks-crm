package com.pawnee.parks.crm.controller;

import com.pawnee.parks.crm.domain.enums.CustomerStatus;
import com.pawnee.parks.crm.dto.CustomerCreateRequest;
import com.pawnee.parks.crm.dto.CustomerResponse;
import com.pawnee.parks.crm.dto.PageResponse;
import com.pawnee.parks.crm.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Customers", description = "Customer management API")
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @Operation(summary = "Create new customer")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse create(@Valid @RequestBody CustomerCreateRequest request) {
        return service.create(request);
    }

    @Operation(summary = "Get customer by id")
    @GetMapping("/{id}")
    public CustomerResponse get(@PathVariable UUID id) {
        return service.get(id);
    }

    @Operation(summary = "List customers")
    @GetMapping
    public PageResponse<CustomerResponse> list(
            @RequestParam(required = false) CustomerStatus status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), pageSize);
        return service.list(status, search, pageable);
    }

    @Operation(summary = "Update (partial) customer")
    @PatchMapping("/{id}")
    public CustomerResponse update(
            @PathVariable java.util.UUID id,
            @RequestBody com.pawnee.parks.crm.dto.CustomerUpdateRequest request
    ) {
        return service.update(id, request);
    }


    @Operation(summary = "Delete customer")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    @Operation(summary = "Get mocked insight for a customer")
    @GetMapping("/{id}/insight")
    public com.pawnee.parks.crm.dto.InsightResponse insight(@PathVariable java.util.UUID id) {
        return com.pawnee.parks.crm.dto.InsightResponse.builder()
                .insight(service.insight(id))
                .build();
    }

}
