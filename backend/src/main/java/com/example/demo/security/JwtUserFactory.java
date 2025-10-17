package com.example.demo.security;

import com.example.demo.model.JwtUser;
import com.example.demo.model.Role;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.stream.Collectors;

@NoArgsConstructor
public final class JwtUserFactory {

    public static <JwtUser> JwtUser create(com.example.demo.model.User user) {
        return new JwtUser(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                mapToGrantedAuthorities(List.of(user.getRoles())),
                user.getEnabled()
        );
    }
    private static List<GrantedAuthority> mapToGrantedAuthorities(List<Role> userRoles) {
        return userRoles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleEnum().name()))
                .collect(Collectors.toList());
    }
}