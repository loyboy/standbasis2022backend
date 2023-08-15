package basepackage.stand.standbasisprojectonev1.payload.onboarding;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class AttendanceRequest {	
	 
	private String usertype;
	
    private String _date;
    
    private String period;
    
    private String image;
    
    private Integer done;
    
    private Integer principal_action;
    
    private Integer delegated;
    
    private String _desc;
    
    private Long timetable_id;	
	
	private Long calendar_id;	
	
	private Long tea_id;
}
