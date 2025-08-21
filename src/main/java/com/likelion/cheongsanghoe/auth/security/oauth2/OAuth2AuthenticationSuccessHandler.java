package com.likelion.cheongsanghoe.auth.security.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.cheongsanghoe.auth.security.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        Object principal = authentication.getPrincipal();


        String email = extractEmail(principal);
        String name  = extractName(principal);
        String role  = extractRole(authentication, principal);

        if (email == null || email.isBlank()) {

            writeJson(response, HttpServletResponse.SC_BAD_REQUEST, Map.of(
                    "success", false,
                    "error", "EMAIL_NOT_FOUND",
                    "message", "OAuth2 provider did not return an email."
            ));
            return;
        }


        String accessToken = jwtTokenProvider.createToken(email, role != null ? role : "ROLE_USER");


        Map<String, Object> userPayload = new LinkedHashMap<>();
        userPayload.put("email", email);
        if (name != null) userPayload.put("name", name);

        Map<String, Object> responseData = new LinkedHashMap<>();
        responseData.put("success", true);
        responseData.put("accessToken", accessToken);
        responseData.put("user", userPayload);

        writeJson(response, HttpServletResponse.SC_OK, responseData);
    }



    private String extractEmail(Object principal) {

        if (principal instanceof UserPrincipal up) {
            return up.getEmail();
        }

        if (principal instanceof OidcUser oidcUser) {

            String email = oidcUser.getEmail();
            if (email != null) return email;

            Object e = oidcUser.getAttributes().get("email");
            return e != null ? String.valueOf(e) : null;
        }

        if (principal instanceof OAuth2User oauth2User) {
            Object e = oauth2User.getAttributes().get("email");
            return e != null ? String.valueOf(e) : null;
        }

        return null;
    }

    private String extractName(Object principal) {
        if (principal instanceof UserPrincipal up) {
            return up.getName();
        }
        if (principal instanceof OidcUser oidcUser) {

            String name = oidcUser.getFullName();
            if (name != null && !name.isBlank()) return name;

            Object n = oidcUser.getAttributes().get("name");
            if (n != null) return String.valueOf(n);

            String given = safeAttr(oidcUser, "given_name");
            String family = safeAttr(oidcUser, "family_name");
            if (given != null || family != null) {
                return List.of(Optional.ofNullable(given).orElse(""),
                                Optional.ofNullable(family).orElse(""))
                        .stream().filter(s -> !s.isBlank()).collect(Collectors.joining(" "));
            }
            return null;
        }
        if (principal instanceof OAuth2User oauth2User) {
            Object n = oauth2User.getAttributes().get("name");
            return n != null ? String.valueOf(n) : null;
        }
        return null;
    }

    private String extractRole(Authentication authentication, Object principal) {

        if (principal instanceof UserPrincipal up) {
            String r = up.getRole();
            if (r != null && !r.isBlank()) return r;
        }

        if (authentication != null && authentication.getAuthorities() != null) {
            Optional<String> firstRole = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(a -> a != null && a.startsWith("ROLE_"))
                    .findFirst();
            if (firstRole.isPresent()) return firstRole.get();
        }

        return "ROLE_USER";
    }

    private String safeAttr(OAuth2User user, String key) {
        Object v = user.getAttributes() != null ? user.getAttributes().get(key) : null;
        return v != null ? String.valueOf(v) : null;
    }

    private void writeJson(HttpServletResponse response, int status, Map<String, Object> body) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(body));
        response.getWriter().flush();
    }
}
