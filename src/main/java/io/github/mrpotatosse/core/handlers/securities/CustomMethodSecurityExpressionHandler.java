package io.github.mrpotatosse.core.handlers.securities;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

public class CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {
    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(
            Authentication authentication, MethodInvocation invocation) {

        CustomMethodSecurityExpressionRoot root = new CustomMethodSecurityExpressionRoot(authentication);
        root.setTarget(invocation.getThis());
        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setTrustResolver(getTrustResolver());
        root.setRoleHierarchy(getRoleHierarchy());
        return root;
    }
}
