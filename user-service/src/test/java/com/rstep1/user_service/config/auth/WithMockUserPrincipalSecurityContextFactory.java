package com.rstep1.user_service.config.auth;

import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.rstep1.user_service.model.User;
import com.rstep1.user_service.security.auth.UserPrincipal;

public class WithMockUserPrincipalSecurityContextFactory implements WithSecurityContextFactory<WithMockUserPrincipal> {

    @Override
    public SecurityContext createSecurityContext(WithMockUserPrincipal annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        
        User user = new User();
        user.setId(annotation.id());
        user.setUsername(annotation.username());
        user.setPassword(annotation.password());
        
        UserDetails principal = new UserPrincipal(user);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            principal,
            null,
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        context.setAuthentication(auth);
        return context;
    }
}
