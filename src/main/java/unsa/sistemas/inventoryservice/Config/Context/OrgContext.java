package unsa.sistemas.inventoryservice.Config.Context;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrgContext {
    @SuppressWarnings("unused")
    public static final String KEY = "ORG";

    private static final ThreadLocal<String> orgContext = new ThreadLocal<>();
    public static void setOrg(String org) { orgContext.set(org); }
    public static String getOrg() { return orgContext.get(); }
    public static void clear() { orgContext.remove(); }
}
