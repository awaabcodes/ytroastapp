package com.ytroast.backend.auth.service;

import com.ytroast.backend.config.GoogleProps;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @Autowired
    private final GoogleProps props;

    public AuthService(GoogleProps props){
        this.props = props;
    }

    public ResponseEntity<Void> login(HttpSession session) {
        String state = UUID.randomUUID().toString();
        session.setAttribute("oauth_state", state);
        URI uri = UriComponentsBuilder.fromUriString(AUTH_URL)
                .queryParam("client_id", props.clientId())
                .queryParam("redirect_uri", props.redirectUri())
                .queryParam("response_type", "code")
                .queryParam("scope", SCOPES)
                .queryParam("access_type", "offline")
                .queryParam("prompt", "consent")
                .build().encode().toUri();

        return ResponseEntity.status(HttpStatus.FOUND).location(uri).build();
    }

    public String  callback(String code, String state, String error, HttpSession session) {
        if (error != null) return "Google said no: " + error;

        String expected = (String) session.getAttribute("oauth_state");
        if (expected == null || !expected.equals(state)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid state");
        }
        session.removeAttribute("oauth_state");

        return "Got a code (length " + code.length() + "). Next step: swap it for tokens.";
    }
}
