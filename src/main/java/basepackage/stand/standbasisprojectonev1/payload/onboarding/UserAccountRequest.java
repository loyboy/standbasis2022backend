package basepackage.stand.standbasisprojectonev1.payload.onboarding;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
@Getter 
@Setter
public class UserAccountRequest {

	 	@NotBlank
	    @Size(max = 40)
	    private String name;
	 	
	 	@Email
	    private String email;

	    @NotBlank
	    @Size(max = 30)
	    private String username;

	    @Size(max = 100)
	    private String password;
	    
	    private Integer role;
	    
	    private Integer status;
	    
	    private String permissionsJSON;
}
