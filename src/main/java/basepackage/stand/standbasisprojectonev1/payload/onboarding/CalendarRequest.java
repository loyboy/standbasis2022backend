package basepackage.stand.standbasisprojectonev1.payload.onboarding;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
@Getter 
@Setter
public class CalendarRequest {
	@NotBlank
    @Size(max = 60)
    private String session;
    
    @Size(max = 100)
    private String holiday;
    
    private Integer status;
    
    private Integer term;
    
    private String startdate;
    
    private String enddate;
    
    private String lsnstartdate;
    
}
