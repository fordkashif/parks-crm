package com.pawnee.parks.crm.exception;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ErrorResponse {
    private String code;
    private String message;
    private String requestId; // for tracing (optional to fill)
}
