package com.ytroast.backend.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleTokenResponse(
        @JsonProperty("acess_token") String accessToken,
        @JsonProperty("expires_in") Long expiresIn,
        @JsonProperty("refresh_token")String refreshToken,
        @JsonProperty("scope")String scope,
        @JsonProperty("token_type")String tokenType,
        @JsonProperty("id_token")String idToken) {
    public String toString(){
        return "GoogleTokenResponse [redacted]";
    }
}
