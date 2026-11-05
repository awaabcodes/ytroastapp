package com.ytroast.backend.auth.controller;

import com.ytroast.backend.auth.dto.GoogleTokenResponse;
import com.ytroast.backend.auth.dto.LoginRequest;
import com.ytroast.backend.auth.service.AuthService;
import com.ytroast.backend.config.GoogleProps;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final String STATE_KEY = "oauth_state";
    private static final String TOKENS_KEY = "google_tokens";

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public ResponseEntity<Void> login(HttpSession session) {
        LoginRequest request = authService.createLoginRequest();
        session.setAttribute(STATE_KEY, request.state());
        return ResponseEntity.status(HttpStatus.FOUND).location(request.authorizationUri()).build();
    }

    @GetMapping("/callback")
    public Map<String, Object> callback(@RequestParam(required = false) String code,
                                        @RequestParam(required = false) String state,
                                        @RequestParam(required = false) String error,
                                        HttpSession session) {
        String expectedState = (String) session.getAttribute(STATE_KEY);
        session.removeAttribute(STATE_KEY); // single use, even if validation fails below

        GoogleTokenResponse tokens = authService.completeLogin(code, state, error, expectedState);
        session.setAttribute(TOKENS_KEY, tokens);

        // Never return the tokens themselves, only harmless facts about them.
        return Map.<String, Object>of(
                "loggedIn", true,
                "accessTokenExpiresInSeconds", tokens.expiresIn(),
                "refreshTokenReceived", tokens.refreshToken() != null);
    }
}
