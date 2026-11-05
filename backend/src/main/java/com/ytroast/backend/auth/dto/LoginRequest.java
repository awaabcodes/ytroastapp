package com.ytroast.backend.auth.dto;

import java.net.URI;

public record LoginRequest(URI authorizationUri, String state) {
}
