package basepackage.stand.standbasisprojectonev1.payload.onboarding;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class EvaluationRequest {

	  	@NotNull
	    private Long school;
	      
	    @NotNull
	    private Long userid;
}
