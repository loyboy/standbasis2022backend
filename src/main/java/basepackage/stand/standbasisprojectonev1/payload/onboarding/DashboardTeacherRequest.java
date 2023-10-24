package basepackage.stand.standbasisprojectonev1.payload.onboarding;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class DashboardTeacherRequest {

	private Integer qualify;
	
    private Integer _year;
	
	private String graph_link;
	
	private Integer _one; //tq gen.
	private Integer _two; //tq gen p
	private Integer _three; //tq sc gen
	private Integer _four; //tq sc p
	private Integer _five; //tq ao gen
	private Integer _six; //tq ao p
	
	private Long sch_id;
}
