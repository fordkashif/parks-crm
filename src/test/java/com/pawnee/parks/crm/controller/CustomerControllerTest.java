package com.pawnee.parks.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawnee.parks.crm.domain.enums.CustomerStatus;
import com.pawnee.parks.crm.dto.CustomerCreateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void create_get_list_delete_flow() throws Exception {
        var req = CustomerCreateRequest.builder()
                .firstName("Leslie")
                .lastName("Knope")
                .email("leslie.knope@pawnee.gov")
                .status(CustomerStatus.ACTIVE)
                .build();

        // create
        var createRes = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("leslie.knope@pawnee.gov"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists())
                .andExpect(jsonPath("$.createdBy").value(not(isEmptyOrNullString())))
                .andExpect(jsonPath("$.updatedBy").value(not(isEmptyOrNullString())))
                .andReturn();

        var json = createRes.getResponse().getContentAsString();
        var id = objectMapper.readTree(json).get("id").asText();

        // get
        mockMvc.perform(get("/api/v1/customers/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Leslie"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists())
                .andExpect(jsonPath("$.createdBy").value(not(isEmptyOrNullString())))
                .andExpect(jsonPath("$.updatedBy").value(not(isEmptyOrNullString())));

        // list
        mockMvc.perform(get("/api/v1/customers?page=1&pageSize=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.total", greaterThanOrEqualTo(1)));

        // insight
        mockMvc.perform(get("/api/v1/customers/{id}/insight", id))
                .andExpect(status().isOk());

        // delete
        mockMvc.perform(delete("/api/v1/customers/{id}", id))
                .andExpect(status().isNoContent());
    }
}
