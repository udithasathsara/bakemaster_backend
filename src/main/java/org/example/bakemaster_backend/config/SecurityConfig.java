package org.example.bakemaster_backend.config;

import org.example.bakemaster_backend.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()

                        // Dashboard - all authenticated
                        .requestMatchers(HttpMethod.GET, "/api/dashboard").authenticated()

                        // Products & Recipes
                        .requestMatchers(HttpMethod.GET, "/api/products/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN")

                        // Inventory - read all, write admin+
                        .requestMatchers(HttpMethod.GET, "/api/inventory/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/inventory").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/inventory/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/inventory/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/inventory/generate-po").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN")

                        // Food Waste Logs
                        .requestMatchers(HttpMethod.GET, "/api/waste-logs/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/waste-logs/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER")

                        // Orders - all authenticated
                        .requestMatchers("/api/orders/**").authenticated()

                        // Suppliers
                        .requestMatchers(HttpMethod.GET, "/api/suppliers/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/suppliers").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/suppliers/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/suppliers/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN")

                        // Customers
                        .requestMatchers(HttpMethod.GET, "/api/customers/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/customers").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/customers/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/customers/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN")

                        // Purchase Orders
                        .requestMatchers(HttpMethod.GET, "/api/purchase-orders/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/purchase-orders/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/purchase-orders/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER")

                        // Staff Management
                        .requestMatchers(HttpMethod.GET, "/api/staff/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/staff").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/staff/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/staff/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN")

                        // Production Tasks
                        .requestMatchers(HttpMethod.GET, "/api/production-tasks/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/production-tasks").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/production-tasks/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/production-tasks/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN")

                        // User Management - SUPER_ADMIN and ADMIN
                        .requestMatchers("/api/users/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN")

                        // All other requests
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:5174"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}