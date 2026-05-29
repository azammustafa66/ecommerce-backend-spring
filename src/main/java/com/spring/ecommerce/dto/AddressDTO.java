package com.spring.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
    private UUID addressId;

    @NotBlank(message = "Street cannot be empty")
    @Size(min = 3, max = 100, message = "Street must be between 3 and 100 characters")
    private String street;

    @NotBlank(message = "Building cannot be empty")
    @Size(min = 1, max = 50, message = "Building must be between 1 and 50 characters")
    private String building;

    @NotBlank(message = "City cannot be empty")
    @Size(min = 2, max = 50, message = "City must be between 2 and 50 characters")
    private String city;

    @Positive(message = "Postal code must be a positive number")
    private long postalCode;

    @NotBlank(message = "Country cannot be empty")
    @Size(min = 2, max = 50, message = "Country must be between 2 and 50 characters")
    private String country;

    @Positive(message = "Contact number must be a positive number")
    private long contactNumber;
}
