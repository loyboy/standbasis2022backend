package basepackage.stand.standbasisprojectonev1.payload.onboarding;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class CheckUserPasswordRequest {

	 	@NotBlank
	    @Size(max = 30)
	    private String username;

	 	@NotBlank
	    @Size(max = 100)
	    private String oldpassword;
	 	
	 	@NotBlank
	    @Size(max = 100)
	    private String newpassword;
}
