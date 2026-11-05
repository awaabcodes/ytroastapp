package com.ytroast.backend.auth.client;

import com.ytroast.backend.auth.dto.GoogleTokenResponse;
import com.ytroast.backend.auth.exception.OAuthException;
import com.ytroast.backend.config.GoogleProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

public class GoogleOAuthClient {
    private static final Logger log = LoggerFactory.getLogger(GoogleOAuthClient.class);
    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";

    private final RestClient restClient;
    GoogleProps props;

    public GoogleOAuthClient(RestClient restClient, GoogleProps props) {
        this.restClient = restClient;
        this.props = props;
    }

    public GoogleTokenResponse exchangeCode(String code){
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("code", code);
        form.add("client_id", props.clientId());
        form.add("client_secret", props.clientSecret());
        form.add("redirect_uri", props.redirectUri());
        form.add("grant_type", "authorization_code");

        try{
            GoogleTokenResponse response = restClient.post()
                    .uri(TOKEN_URL)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(GoogleTokenResponse.class);
            if (response == null || response.accessToken() == null) {
                throw new OAuthException("Google returned an empty token response", null);
            }
            return response;
        }
        catch(RestClientResponseException e){
            log.warn("Token exchange rejected by Google: status={} body={}", e.getStatusCode().value(), e.getResponseBodyAsString() );
            throw new OAuthException("Google rejected the login (HTTP " + e.getStatusCode().value() + ")", e);
        } catch (RestClientException e) {
            log.warn("Could not reach Google token endpoint", e);
            throw new OAuthException("Could not reach Google", e);
        }
    }
}
