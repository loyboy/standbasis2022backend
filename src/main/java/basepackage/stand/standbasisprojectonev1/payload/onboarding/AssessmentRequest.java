package basepackage.stand.standbasisprojectonev1.payload.onboarding;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class AssessmentRequest {
	 	   
	    private String title; 
	    
	    private String _type; // clw, tst, hwk, mid, final 

	    private Integer actual;
	    
	    private Integer max;
	    
	    private Integer score;
	    
	    private String date;
	    
	    private Long enrol;
	    
	    private Long lsn_id;
}
