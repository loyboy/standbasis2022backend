package basepackage.stand.standbasisprojectonev1.payload.onboarding;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class LessonnoteManagementRequest {

	private Long lsnmanagementId;
	
	private Integer quality;
    
    private Integer sub_perf_classwork; 
    
    private Integer sub_perf_homework; 
    
    private Integer sub_perf_test; 
    
    private Integer management; 
    
    private Integer score;
    
    private String action; //revert/approved/re-submitted/submit
    
    private Long lsn_id;
}
