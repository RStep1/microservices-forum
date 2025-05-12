package com.rstep1.user_service.config.auth;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockUserPrincipalSecurityContextFactory.class)
public @interface WithMockUserPrincipal {
    long id() default 1L;
    String username() default "testuser";
    String password() default "password";
    String[] authorities() default {"ROLE_USER"};
}
