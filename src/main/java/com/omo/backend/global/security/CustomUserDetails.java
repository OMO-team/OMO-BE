package com.omo.backend.global.security;

import java.util.Collection;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomUserDetails implements UserDetails {

    private final Long memberId;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;

    public static CustomUserDetails of(Long memberId, String email, Collection<? extends GrantedAuthority> authorities) {
        return CustomUserDetails.builder()
                .memberId(memberId)
                .email(email)
                .authorities(authorities)
                .build();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return "";
    }
}
