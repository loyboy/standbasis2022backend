package basepackage.stand.standbasisprojectonev1.payload.onboarding;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class DashboardCurriculumRequest {

	 	private Integer _week;
	    
	    private String graph_link;
		
	    private Integer _year;
		
		private Integer _one; //teaching compliance
		private Integer _two; //teaching Process Administration
		private Integer _three; //Assessment Performance
		private Integer _four; //Student Socio-Motor Indicators
		private Integer _five; //Quality Assurance interventions
		private Integer _six; //Teaching resources capacity
		
		private Long sch_id;
}
