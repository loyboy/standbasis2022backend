package basepackage.stand.standbasisprojectonev1.payload.onboarding;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class AttendanceActivityRequest {

	private Long att_id;	
	   
    private Long owner; // user id of that Person
      
    private String ownertype;  // Principal/ Teacher   
      
    private Timestamp expected;     
    
    private Timestamp actual;  
    
    private Integer slip; // 0 - No, 1 - Yes
    
    private String activity; // Expected to approve Calendar activity
    
    private String action; // queried/approved
    
    private String comment_query; 
}
