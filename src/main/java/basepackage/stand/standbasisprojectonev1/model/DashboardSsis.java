package basepackage.stand.standbasisprojectonev1.model;

import java.util.Date;
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
@Table(name = "dashboard_ssis")
public class DashboardSsis {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dashId;
	
    private Integer _value;
    
    private String graph_link;
	
	@NotNull
    private Integer _year;
	
	private Integer _one; //Teaching P.
	private Integer _one_min;
	private Integer _two; //Teacher Resource
	private Integer _two_min;
	private Integer _three; //Learning Env.
	private Integer _three_min;
	private Integer _four; //Sustainable 
	private Integer _four_min;
	private Integer _five; //Student develop
	private Integer _five_min;
	private Integer _six; //Academic Performance
	private Integer _six_min;
	private Integer _seven; //safety Health
	private Integer _seven_min;
	
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "sch_id", nullable = false)
    private School school;
	
	public DashboardSsis() {
	    	
	}
	    
	
}
