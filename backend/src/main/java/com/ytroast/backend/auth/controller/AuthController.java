package com.ytroast.backend.auth.controller;

import com.ytroast.backend.config.GoogleProps;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    GoogleProps props;

    public AuthController(GoogleProps props)
    {
        this.props = props;
    }

    public ResponseEntity<Void> login(HttpSession session){
        String state = UUID.randomUUID().toString();
        session.setAttribute("oauth_state",state);


    }

}
