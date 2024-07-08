package basepackage.stand.standbasisprojectonev1.model;
import javax.persistence.CascadeType;
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
@Table(name = "dashboard_teacher_input")
public class DashboardTeacherInput {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dashId;
	
	@NotNull
    private Integer _year;
	
	@NotNull
    private String _type;
	
	private String level_option;
	private String trcc_option;
	private String academic_option;
	private String qualification_in_education_option;
	private String type_of_engagement_option;
	private String discipline_option;
	private String highest_experience_option;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "sch_id", nullable = false)
    private School school;
	
	public DashboardTeacherInput() {
    	
	}
}
