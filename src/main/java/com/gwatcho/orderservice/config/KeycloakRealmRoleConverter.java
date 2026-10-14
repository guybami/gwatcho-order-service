package com.gwatcho.orderservice.config;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

public class KeycloakRealmRoleConverter
        implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final JwtGrantedAuthoritiesConverter scopeConverter =
            new JwtGrantedAuthoritiesConverter();

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Preserve standard OAuth2 scope authorities, e.g. SCOPE_profile.
        Collection<GrantedAuthority> scopes = scopeConverter.convert(jwt);
        if (scopes != null) {
            authorities.addAll(scopes);
        }

        // Extract Keycloak realm roles.
        Object realmAccessClaim = jwt.getClaims().get("realm_access");

        if (realmAccessClaim instanceof Map<?, ?> realmAccess) {
            Object rolesClaim = realmAccess.get("roles");

            if (rolesClaim instanceof Collection<?> roles) {
                for (Object role : roles) {
                    if (role instanceof String roleName) {
                        authorities.add(
                                new SimpleGrantedAuthority("ROLE_" + roleName));
                    }
                }
            }
        }

        return authorities;
    }
}