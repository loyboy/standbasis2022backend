package basepackage.stand.standbasisprojectonev1.model;

import java.math.BigDecimal;
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
@Table(name = "dashboard_rating_input")
public class DashboardRatingInput {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dashId;
	
	@NotNull
    private Integer _year;

    private BigDecimal teaching_processes;
    private BigDecimal teacher_resources;
    private BigDecimal learning_environment;
    private BigDecimal sustainability;
    private BigDecimal student_development;
    private BigDecimal academic_performance;
    private BigDecimal sshe;

    @ManyToOne(fetch = FetchType.LAZY,  cascade = CascadeType.REMOVE)
    @JoinColumn(name = "sch_id", nullable = false)
    private School school;
	
	public DashboardRatingInput() {
	    	
	}
}
