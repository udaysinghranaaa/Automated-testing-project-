package com.shopflow.models;

public record CheckoutInformation(String firstName, String lastName, String postalCode) {
    public CheckoutInformation {
        firstName = firstName == null ? "" : firstName;
        lastName = lastName == null ? "" : lastName;
        postalCode = postalCode == null ? "" : postalCode;
    }
}
