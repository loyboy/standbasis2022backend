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
@Table(name = "dashboard_teacher")
public class DashboardTeacher {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dashId;
	
    private Integer _qualify;
	
	@NotNull
    private Integer _year;
	
	private String graph_link;
	
	private Integer _one; //tq gen.
	private Integer _two; //tq gen p
	private Integer _three; //tq sc gen
	private Integer _four; //tq sc p
	private Integer _five; //tq ao gen
	private Integer _six; //tq ao p
	
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sch_id", nullable = false)
    private School school;
	
	public DashboardTeacher() {
	    	
	}
}
