package basepackage.stand.standbasisprojectonev1.payload.onboarding;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class RowcallRequest {

		@NotNull    
	    private String pupil_fullname;     
	       
	    private String remark; 
	
	    private Integer status;  
	    
	    private Long stu_id;    
	    
	    private Long att_id;
}
