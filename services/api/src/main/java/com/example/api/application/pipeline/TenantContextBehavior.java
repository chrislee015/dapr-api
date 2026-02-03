package com.example.api.application.pipeline;

import com.example.common.cqrs.Request;
import com.example.common.cqrs.markers.TenantAware;
import com.example.common.cqrs.pipeline.PipelineBehavior;
import com.example.common.tenant.TenantContext;

import java.util.function.Supplier;

public class TenantContextBehavior implements PipelineBehavior<Request> {
    @Override
    public Object apply(Request message, Supplier<Object> next) {
        if (message instanceof TenantAware ta) TenantContext.set(ta.tenantId());
        try { return next.get(); }
        finally { TenantContext.clear(); }
    }
}
