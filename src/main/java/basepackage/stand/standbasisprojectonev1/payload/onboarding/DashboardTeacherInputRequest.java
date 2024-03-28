package basepackage.stand.standbasisprojectonev1.payload.onboarding;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class DashboardTeacherInputRequest {

	private String  _type; 	
    private Integer _year;
	private String level_option;
	private String trcc_option;
	private String academic_option;
	private String qualification_in_education_option;
	private String type_of_engagement_option;
	private String discipline_option;
	private String highest_experience_option;
	private Long   sch_id;
}
