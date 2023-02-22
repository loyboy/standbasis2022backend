package basepackage.stand.standbasisprojectonev1.payload;

import javax.validation.constraints.NotBlank;

/**
 * Created by Loy from August 2022.
 */
public class LoginRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String usern) {
        this.username = usern;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String pass) {
        this.password = pass;
    }
}
