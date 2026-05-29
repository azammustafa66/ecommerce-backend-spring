package com.spring.ecommerce.dto;

import com.spring.ecommerce.models.AppRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
    private UUID roleId;

    @NotNull(message = "Role must be specified")
    private AppRole role;
}
