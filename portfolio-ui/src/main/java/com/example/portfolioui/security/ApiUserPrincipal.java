package com.example.portfolioui.security;

import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.List;

public record ApiUserPrincipal(String username, String accessToken, List<GrantedAuthority> authorities) implements Serializable {

    public ApiUserPrincipal {
        authorities = List.copyOf(authorities);
    }

    @Override
    public String toString() {
        return "ApiUserPrincipal[username=" + username + "]";
    }
}
