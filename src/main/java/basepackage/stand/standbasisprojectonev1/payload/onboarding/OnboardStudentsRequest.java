package basepackage.stand.standbasisprojectonev1.payload.onboarding;

import javax.validation.Valid;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter 
@Setter
public class OnboardStudentsRequest {
    
    @Valid
	private List<StudentRequest> pupRequest;

    public OnboardStudentsRequest() {}
	
	public OnboardStudentsRequest( List<StudentRequest> pup  ) {
	
		this.pupRequest = pup;
		
	}
}
