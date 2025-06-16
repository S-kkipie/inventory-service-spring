package unsa.sistemas.inventoryservice.Config.Context;

public class OrgContext {
    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    public static void setOrgCode(String orgCode) {
        CONTEXT.set(orgCode);
    }

    public static String getOrgCode() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
