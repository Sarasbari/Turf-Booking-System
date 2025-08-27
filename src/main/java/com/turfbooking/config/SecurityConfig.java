package com.turfbooking.config;

import com.turfbooking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * Security configuration for the Turf Booking System
 * Handles OAuth2 Google authentication and regular form-based authentication
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private UserService userService;
    
    /**
     * Password encoder bean for encrypting passwords
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * Security filter chain configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for API endpoints
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/", "/home", "/login", "/register").permitAll()
                .requestMatchers("/api/auth/me").permitAll()
                .requestMatchers("/api/auth/promote").hasRole("ADMIN")
                .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                .requestMatchers("/api/turfs/search", "/api/turfs/*/details", "/api/turfs/featured").permitAll()
                .requestMatchers("/api/tournaments/upcoming", "/api/reviews/*/public").permitAll()
                .requestMatchers("/search", "/turf-details/**", "/tournaments", "/about").permitAll()
                
                // Admin endpoints
                .requestMatchers("/admin", "/admin/**", "/api/admin/**").hasRole("ADMIN")
                
                // Authenticated user endpoints
                .requestMatchers("/dashboard", "/profile", "/bookings/**", "/api/bookings/**").authenticated()
                .requestMatchers("/api/reviews/**", "/api/tournaments/register/**").authenticated()
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/api/auth/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=oauth")
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService())
                )
                .successHandler(oauth2AuthenticationSuccessHandler())
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            );
        
        return http.build();
    }
    
    /**
     * Custom OAuth2 user service to handle Google authentication
     */
    @Bean
    public DefaultOAuth2UserService customOAuth2UserService() {
        return new DefaultOAuth2UserService() {
            @Override
            public OAuth2User loadUser(OAuth2UserRequest userRequest) {
                OAuth2User oauth2User = super.loadUser(userRequest);
                
                // Extract user information from Google
                String email = oauth2User.getAttribute("email");
                String name = oauth2User.getAttribute("name");
                String googleId = oauth2User.getAttribute("sub");
                String profilePicture = oauth2User.getAttribute("picture");
                
                // Create or update user in database
                userService.createOrUpdateGoogleUser(name, email, googleId, profilePicture);
                
                return oauth2User;
            }
        };
    }
    
    /**
     * OAuth2 authentication success handler
     */
    @Bean
    public AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            String email = oauth2User.getAttribute("email");
            
            // Store user information in session
            request.getSession().setAttribute("userEmail", email);
            request.getSession().setAttribute("userName", oauth2User.getAttribute("name"));
            request.getSession().setAttribute("userPicture", oauth2User.getAttribute("picture"));
            
            // Redirect to dashboard
            response.sendRedirect("/dashboard");
        };
    }
}
