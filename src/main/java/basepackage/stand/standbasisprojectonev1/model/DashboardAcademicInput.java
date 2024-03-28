package basepackage.stand.standbasisprojectonev1.model;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter 
@Setter
@Entity
@Table(name = "dashboard_academic_input")
public class DashboardAcademicInput {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dashId;
	
	@NotNull
    private Integer _year;
	
	@NotNull
    private String _type;
	
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
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sch_id", nullable = false)
    private School school;
	
	public DashboardAcademicInput() {
    	
	}
}
