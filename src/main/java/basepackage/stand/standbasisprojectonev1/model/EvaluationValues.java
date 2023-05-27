package basepackage.stand.standbasisprojectonev1.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import basepackage.stand.standbasisprojectonev1.model.audit.DateAudit;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter 
@Setter
@Entity
@Table(name = "evaluation_values")
public class EvaluationValues extends DateAudit{

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobvalueId;
	   
	private Integer round;
	
	private String roundId;
	
    private Integer value;
	  
    private Integer performance;
    
    private Integer complete;
	
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
	
	@NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_id", nullable = false)
    private EvaluatorCore evaluation;
	
	
	
}