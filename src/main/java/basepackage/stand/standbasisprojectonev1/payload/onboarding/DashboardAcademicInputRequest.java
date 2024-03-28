package basepackage.stand.standbasisprojectonev1.payload.onboarding;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class DashboardAcademicInputRequest {
	private String  _type; 	
    private Integer _year;
	private Integer enrollee_count;
	private Integer enrollment_count;
	private Integer a1_grade_count;
	private Integer b2_grade_count;
	private Integer b3_grade_count;
	private Integer c4_grade_count;
	private Integer c5_grade_count;
	private Integer c6_grade_count;
	private Integer d7_grade_count;
	private Integer e8_grade_count;
	private Integer f9_grade_count;
	private Integer absent_count;
	private Integer english_pass_count;
	private Integer maths_pass_count;
	private Long    sch_id;
}
