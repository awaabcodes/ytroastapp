package com.ytroast.backend.auth.controller;

import com.ytroast.backend.auth.service.AuthService;
import com.ytroast.backend.config.GoogleProps;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    public ResponseEntity<Void> login(HttpSession session){
        return authService.login(session);
    }
    public String callback(@RequestParam(required = false) String code,
                           @RequestParam(required = false) String state,
                           @RequestParam(required = false) String error,
                           HttpSession session) {
        return authService.callback( code, state, error, session);
    }
}
