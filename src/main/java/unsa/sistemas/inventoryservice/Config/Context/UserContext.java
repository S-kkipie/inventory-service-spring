package unsa.sistemas.inventoryservice.Config.Context;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserContext {
    public static final Class<UserContext> KEY = UserContext.class;

    private String username;
    private String role;
}
