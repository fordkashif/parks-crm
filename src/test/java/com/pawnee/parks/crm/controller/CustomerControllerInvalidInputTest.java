package com.pawnee.parks.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
class CustomerControllerInvalidInputTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void missing_required_fields_returns_422() throws Exception {
        var bad = CustomerCreateRequest.builder()
                .firstName("")        // @NotBlank fails
                .lastName("")         // @NotBlank fails
                .email("not-an-email")// @Email fails
                .build();

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("validation_error"))
                .andExpect(jsonPath("$.message", containsString("firstName")))
                .andExpect(jsonPath("$.message", containsString("lastName")));
    }

    @Test
    void duplicate_email_returns_conflict_409() throws Exception {
        var good = CustomerCreateRequest.builder()
                .firstName("Ann")
                .lastName("Perkins")
                .email("ann.perkins@pawnee.gov")
                .build();

        // first create ok
        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(good)))
                .andExpect(status().isCreated());

        // second create -> 409
        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(good)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("conflict"));
    }

    @Test
    void get_unknown_id_returns_404() throws Exception {
        mockMvc.perform(get("/api/v1/customers/{id}", "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("resource_not_found"));
    }

    @Test
    void delete_unknown_id_returns_404() throws Exception {
        mockMvc.perform(delete("/api/v1/customers/{id}", "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("resource_not_found"));
    }

    @Test
    void insight_returns_json_object_with_insight_field() throws Exception {
        // create a real customer
        var req = CustomerCreateRequest.builder()
                .firstName("Ron")
                .lastName("Swanson")
                .email("ron.swanson@pawnee.gov")
                .build();

        var res = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        var id = objectMapper.readTree(res.getResponse().getContentAsString()).get("id").asText();

        // ensure /insight returns JSON with "insight"
        mockMvc.perform(get("/api/v1/customers/{id}/insight", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.insight").isString());
    }
}
