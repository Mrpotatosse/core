package io.github.mrpotatosse.core.handlers.securities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

public class CustomMethodSecurityExpressionRoot
        extends SecurityExpressionRoot
        implements MethodSecurityExpressionOperations {
    @Getter
    @Setter
    private Object target;

    @Getter
    @Setter
    private Object filterObject;

    @Getter
    @Setter
    private Object returnObject;

    public CustomMethodSecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }

    @Override
    public Object getThis() {
        return this.target;
    }
}
