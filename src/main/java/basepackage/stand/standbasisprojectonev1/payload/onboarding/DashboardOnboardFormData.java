package basepackage.stand.standbasisprojectonev1.payload.onboarding;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class DashboardOnboardFormData {
	 	
	    @NotBlank
	    @Size(max = 30)
	    private String standbasis_unique_number;
	    
	    @NotBlank
	    @Size(max = 50)
	    private String school_name;
	    
	    @NotBlank
	    @Size(max = 30)
	    private String school_email;
	    
	    @NotBlank
	    @Size(max = 100)
	    private String school_physical_address;
	    
	    @NotBlank
	    @Size(max = 30)
	    private String school_telephone_number;
	    
	    @NotBlank
	    @Size(max = 50)
	    private String contact_person_name;
	    
	    @NotBlank
	    @Size(max = 30)
	    private String designation;
}
