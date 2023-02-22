package basepackage.stand.standbasisprojectonev1.payload.onboarding;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class EventManagerRequest {

	@NotBlank
    @Size(max = 60)
    private String module;
    
    private String action;
    
    private String comment;
    
    private String dateofevent;
    
    private Long user_id;
    
    private Long sch_id;
    
}
