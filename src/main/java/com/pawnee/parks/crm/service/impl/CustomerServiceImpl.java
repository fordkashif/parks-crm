package com.pawnee.parks.crm.service.impl;

import com.pawnee.parks.crm.domain.entity.Customer;
import com.pawnee.parks.crm.domain.enums.CustomerStatus;
import com.pawnee.parks.crm.dto.CustomerCreateRequest;
import com.pawnee.parks.crm.dto.CustomerResponse;
import com.pawnee.parks.crm.dto.CustomerUpdateRequest;
import com.pawnee.parks.crm.dto.PageResponse;
import com.pawnee.parks.crm.exception.ConflictException;
import com.pawnee.parks.crm.exception.NotFoundException;
import com.pawnee.parks.crm.mapper.CustomerMapper;
import com.pawnee.parks.crm.repository.CustomerRepository;
import com.pawnee.parks.crm.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;
    private final CustomerMapper mapper;

    @Override
    public CustomerResponse create(CustomerCreateRequest request) {
        repository.findByEmail(request.getEmail())
                .ifPresent(c -> { throw new ConflictException("Customer with email already exists"); });

        Customer entity = mapper.toEntity(request);
        if (entity.getStatus() == null) {
            entity.setStatus(CustomerStatus.ACTIVE);
        }
        Customer saved = repository.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse get(UUID id) {
        Customer c = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        if (c.getDeletedAt() != null) {
            throw new NotFoundException("Customer not found");
        }
        return mapper.toDto(c);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CustomerResponse> list(CustomerStatus status, String search, Pageable pageable) {
        var page = (status != null)
                ? repository.findByStatusAndDeletedAtIsNull(status, pageable)
                : (search != null && !search.isBlank())
                ? repository.searchActive(search, pageable)
                : repository.findAllByDeletedAtIsNull(pageable);

        var items = page.getContent().stream().map(mapper::toDto).toList();
        return PageResponse.<CustomerResponse>builder()
                .items(items)
                .page(pageable.getPageNumber() + 1)
                .pageSize(pageable.getPageSize())
                .total(page.getTotalElements())
                .build();
    }


    @Override
    public CustomerResponse update(UUID id, CustomerUpdateRequest req) {
        var c = repository.findById(id).orElseThrow(() -> new NotFoundException("Customer not found"));

        if (req.getFirstName() != null) c.setFirstName(req.getFirstName());
        if (req.getLastName() != null)  c.setLastName(req.getLastName());
        if (req.getPhone() != null)     c.setPhone(req.getPhone());
        if (req.getOrganization() != null) c.setOrganization(req.getOrganization());
        if (req.getTags() != null)      c.setTags(req.getTags());
        if (req.getStatus() != null)    c.setStatus(req.getStatus());
        if (req.getLastInteractionAt() != null) c.setLastInteractionAt(req.getLastInteractionAt());
        if (req.getNotes() != null)     c.setNotes(req.getNotes());
        if (req.getAddress() != null) {
            c.setAddress(new com.pawnee.parks.crm.domain.entity.Address(
                    req.getAddress().getLine1(),
                    req.getAddress().getLine2(),
                    req.getAddress().getCity(),
                    req.getAddress().getState(),
                    req.getAddress().getPostalCode()
            ));
        }
        return mapper.toDto(c);
    }


    @Override
    public void delete(UUID id) {
        var c = repository.findById(id).orElseThrow(() -> new NotFoundException("Customer not found"));
        if (c.getDeletedAt() != null) {
            return;
        }
        c.setDeletedAt(java.time.Instant.now());
        c.setStatus(CustomerStatus.INACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public String insight(UUID id) {
        Customer c = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        Instant last = c.getLastInteractionAt();
        if (last == null) {
            if (c.getTags() != null && c.getTags().contains("vendor")) {
                return "New vendor; send onboarding packet and Parks & Rec guidelines.";
            }
            return "No interactions recorded; schedule a first touch-point with community liaison.";
        }

        long days = Duration.between(last, Instant.now()).toDays();
        if (days >= 90) {
            return "This customer has not interacted with Parks & Rec in a while. Consider re-engagement with a local initiative.";
        }

        return "Steady engagement; invite them to upcoming community events and volunteer programs.";
    }
}
