package com.example.api.application.pipeline;

import com.example.common.cqrs.CqrsMessage;
import com.example.common.cqrs.markers.SecuredCommand;
import com.example.common.cqrs.pipeline.PipelineBehavior;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.function.Supplier;

public class SecurityBehavior implements PipelineBehavior<CqrsMessage> {
    @Override
    public Object apply(CqrsMessage message, Supplier<Object> next) {
        if (!(message instanceof SecuredCommand secured)) return next.get();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities() == null) throw new AccessDeniedException("Forbidden");

        String required = secured.requiredRole();
        boolean has = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals(required) || a.equals("ROLE_" + required));

        if (!has) throw new AccessDeniedException("Forbidden");
        return next.get();
    }
}
