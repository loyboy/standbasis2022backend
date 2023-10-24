package basepackage.stand.standbasisprojectonev1.payload.onboarding;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class DashboardSsisRequest {
    //DashboardAcademicRequest
    private Integer _min;
	
    private Integer _value;
    
    private String graph_link;
	
    private Integer _year;
	
	private Integer _one; //Teaching P.
	private Integer _two; //Teacher Resource
	private Integer _three; //Learning Env.
	private Integer _four; //Sustainable 
	private Integer _five; //Student develop
	private Integer _six; //Academic Performance
	private Integer _seven; //safety Health
	
	private Long sch_id;
}
