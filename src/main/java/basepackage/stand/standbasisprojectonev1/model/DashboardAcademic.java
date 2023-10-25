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
@Table(name = "dashboard_academic")
public class DashboardAcademic {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dashId;
	
	@NotNull
    private Integer _year;
	
	private String graph_link;
	
	private Integer _transitionIndex_term_one; 
	private Integer _transitionIndex_term_two; 
	private Integer _transitionIndex_term_three; 
	private Integer _transitionIndex_external;
	
	private Integer _dragIndex_term_one; 
	private Integer _dragIndex_term_two; 
	private Integer _dragIndex_term_three; 
	private Integer _dragIndex_external; 
	
	private String _grade_term_one;
	private String _grade_term_two;
	private String _grade_term_three;
	private String _grade_external;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sch_id", nullable = false)
    private School school;
	
	public DashboardAcademic() {
    	
	}
}
