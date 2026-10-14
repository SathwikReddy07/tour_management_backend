package com.ysr.config;

import com.ysr.model.Users;
import com.ysr.repository.UserRepo;
import com.ysr.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.UUID;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepo userRepo;
    private final JwtService jwtService;

    public OAuth2SuccessHandler(UserRepo userRepo, JwtService jwtService) {
        this.userRepo = userRepo;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        OAuth2User user = (OAuth2User) authentication.getPrincipal();
        String email = user.getAttribute("email");
        String name = user.getAttribute("name");
        if (email == null || email.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email not found in OAuth2 user attributes");
            return;
        }
        Users existingUser = userRepo.findByEmail(email);
        if (existingUser == null) {
            Users newUser = new Users();
            String username = name != null ? name : "GoogleUser";
            newUser.setEmail(email);
            newUser.setName(username);
            newUser.setPhone(null);
            newUser.setRole("ROLE_CUSTOMER");
            newUser.setPassword(UUID.randomUUID().toString());
            existingUser = userRepo.save(newUser);
        }
        String token = jwtService.generateToken(existingUser.getEmail());
        String redirectUrl = "http://localhost:5173/authSuccess";
        String finalRedirectUrl = UriComponentsBuilder.fromUriString(redirectUrl)
                .queryParam("token", token)
                .build().toUriString();
        getRedirectStrategy().sendRedirect(request, response, finalRedirectUrl);
    }
}
