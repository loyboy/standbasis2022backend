package basepackage.stand.standbasisprojectonev1.payload.onboarding;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class EvaluationValuesRequest {

	 	private Long jobvalueId;
	   
		private Integer round;
		
		private String roundId;
		
	    private Integer value;
		  
	    private Integer performance;
	    
	    private Integer complete;
	    
	    private Long school_id;
	    
	    private Long user_id;
	    
	    private Long evaluation_id;
}
