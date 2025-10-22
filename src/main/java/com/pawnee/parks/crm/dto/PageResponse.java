package com.pawnee.parks.crm.dto;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PageResponse<T> {
    private List<T> items;
    private int page;
    private int pageSize;
    private long total;
}
