package com.example.common.tenant;

/**
 * Minimal tenant context holder.
 * Services can populate this via pipeline behavior or HTTP filter.
 */
public final class TenantContext {
    private static final ThreadLocal<String> TENANT = new ThreadLocal<>();

    private TenantContext() {}

    public static void set(String tenantId) { TENANT.set(tenantId); }
    public static String get() { return TENANT.get(); }
    public static void clear() { TENANT.remove(); }
}
