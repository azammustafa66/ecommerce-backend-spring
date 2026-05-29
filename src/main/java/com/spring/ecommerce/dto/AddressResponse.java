package com.spring.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {
    private List<AddressDTO> addresses;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean lastPage;
}
