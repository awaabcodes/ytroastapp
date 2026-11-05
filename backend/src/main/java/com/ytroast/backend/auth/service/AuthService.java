package com.ytroast.backend.auth.service;

import com.ytroast.backend.auth.client.GoogleOAuthClient;
import com.ytroast.backend.auth.dto.GoogleTokenResponse;
import com.ytroast.backend.auth.dto.LoginRequest;
import com.ytroast.backend.auth.exception.AuthDeniedException;
import com.ytroast.backend.auth.exception.InvalidCallBackException;
import com.ytroast.backend.config.GoogleProps;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Service
public class AuthService {

    private static final String AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String SCOPES =
            "openid email profile https://www.googleapis.com/auth/youtube.readonly";


    private final GoogleProps props;
    private final GoogleOAuthClient oauthClient;

    public AuthService(GoogleProps props, GoogleOAuthClient oauthClient) {
        this.props = props;
        this.oauthClient = oauthClient;
    }

    public LoginRequest createLoginRequest()     {
        String state = UUID.randomUUID().toString();

        URI uri = UriComponentsBuilder.fromUriString(AUTH_URL)
                .queryParam("client_id", props.clientId())
                .queryParam("redirect_uri", props.redirectUri())
                .queryParam("response_type", "code")
                .queryParam("scope", SCOPES)
                .queryParam("state", state)
                .queryParam("access_type", "offline")
                .queryParam("prompt", "consent")
                .build().encode().toUri();

        return new LoginRequest(uri, state);
    }
    public GoogleTokenResponse completeLogin(String code, String state, String error, String expectedState) {
        if (error != null) {
            throw new AuthDeniedException("Google login was not completed: " + error);
        }
        if (expectedState == null || !expectedState.equals(state)) {
            throw new InvalidCallBackException("Invalid or missing OAuth state");
        }
        if (code == null || code.isBlank()) {
            throw new InvalidCallBackException("Missing authorization code");
        }
        return oauthClient.exchangeCode(code);
    }

    public String callback(String code, String state, String error, HttpSession session) {
        if (error != null) return "Google said no: " + error;

        String expected = (String) session.getAttribute("oauth_state");
        if (expected == null || !expected.equals(state)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid state");
        }
        session.removeAttribute("oauth_state");

        return "Got a code (length " + code.length() + "). Next step: swap it for tokens.";
    }
}
