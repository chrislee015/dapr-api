package com.app.springsandboxone.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record User(
    @JsonProperty("id") Integer id,
    @JsonProperty("name") String name,
    @JsonProperty("username") String username,
    @JsonProperty("email") String email,
    @JsonProperty("address") Address address,
    @JsonProperty("phone") String phone,
    @JsonProperty("website") String website,
    @JsonProperty("company") Company company
) {
    public record Address(
        @JsonProperty("street") String street,
        @JsonProperty("suite") String suite,
        @JsonProperty("city") String city,
        @JsonProperty("zipcode") String zipcode,
        @JsonProperty("geo") Geo geo
    ) {
        public record Geo(
            @JsonProperty("lat") String lat,
            @JsonProperty("lng") String lng
        ) {}
    }
    
    public record Company(
        @JsonProperty("name") String name,
        @JsonProperty("catchPhrase") String catchPhrase,
        @JsonProperty("bs") String bs
    ) {}
}
