package basepackage.stand.standbasisprojectonev1.payload.onboarding;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class DashboardAcademicRequest {

	 	private Integer _term; // 1-3, -1 = WAEC
		
	    private Integer _year;
		
		private String graph_link;
		
		private Integer _transitionIndex; 
		private Integer _dragIndex; 
		private Integer _grade;
		
		private Long sch_id;
}
