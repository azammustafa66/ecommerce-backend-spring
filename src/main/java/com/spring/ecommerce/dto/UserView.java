package com.spring.ecommerce.dto;

import com.spring.ecommerce.models.AppRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserView {
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private Set<AppRole> roles;
    private List<AddressDTO> addresses;
}
