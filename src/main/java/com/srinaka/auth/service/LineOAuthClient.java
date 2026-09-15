package com.srinaka.auth.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.srinaka.auth.dto.LineProfile;
import com.srinaka.common.error.BusinessException;
import com.srinaka.common.error.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class LineOAuthClient {

    private static final String TOKEN_URL = "https://api.line.me/oauth2/v2.1/token";
    private static final String PROFILE_URL = "https://api.line.me/v2/profile";

    private final RestClient restClient = RestClient.create();

    @Value("${app.line.channel-id}")
    private String channelId;

    @Value("${app.line.channel-secret}")
    private String channelSecret;

    public LineProfile fetchProfile(String authorizationCode, String redirectUri) {
        String accessToken = exchangeCodeForAccessToken(authorizationCode, redirectUri);
        return fetchProfile(accessToken);
    }

    private String exchangeCodeForAccessToken(String authorizationCode, String redirectUri) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("code", authorizationCode);
        form.add("redirect_uri", redirectUri);
        form.add("client_id", channelId);
        form.add("client_secret", channelSecret);

        try {
            TokenResponse response = restClient.post()
                    .uri(TOKEN_URL)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(TokenResponse.class);
            if (response == null || response.accessToken() == null) {
                throw new BusinessException(ErrorCode.LINE_AUTH_FAILED);
            }
            return response.accessToken();
        } catch (RestClientException ex) {
            throw new BusinessException(ErrorCode.LINE_AUTH_FAILED);
        }
    }

    private LineProfile fetchProfile(String accessToken) {
        try {
            ProfileResponse response = restClient.get()
                    .uri(PROFILE_URL)
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .body(ProfileResponse.class);
            if (response == null || response.userId() == null) {
                throw new BusinessException(ErrorCode.LINE_AUTH_FAILED);
            }
            return new LineProfile(response.userId(), response.displayName());
        } catch (RestClientException ex) {
            throw new BusinessException(ErrorCode.LINE_AUTH_FAILED);
        }
    }

    private record TokenResponse(
            @JsonProperty("access_token") String accessToken
    ) {
    }

    private record ProfileResponse(
            @JsonProperty("userId") String userId,
            @JsonProperty("displayName") String displayName
    ) {
    }
}
