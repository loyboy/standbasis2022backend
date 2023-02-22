package basepackage.stand.standbasisprojectonev1.payload.onboarding;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class AttendanceManagementRequest {

		private Long att_id;
	
	    private Integer timing;	    
	     
	    private Integer class_perf;    
	      
	    private Integer completeness; 
	    
	    private Integer score;  
	    
	    private Integer action; // 0 - unattended, 1 - Approved, 2 - There is an issue
	    
	    private String comment;
}
