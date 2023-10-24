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
@Table(name = "dashboard_curriculum")
public class DashboardCurriculum {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dashId;
	
    private Integer _week;
    
    private String graph_link;
	
	@NotNull
    private Integer _year;
	
	private Integer _one; //teaching compliance
	private Integer _two; //teaching Process Administration
	private Integer _three; //Assessment Performance
	private Integer _four; //Student Socio-Motor Indicators
	private Integer _five; //Quality Assurance interventions
	private Integer _six; //Teaching resources capacity
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sch_id", nullable = false)
    private School school;
	
	public DashboardCurriculum() {
    	
	}
	
}
