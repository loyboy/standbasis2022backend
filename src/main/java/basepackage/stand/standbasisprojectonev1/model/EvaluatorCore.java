package basepackage.stand.standbasisprojectonev1.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import basepackage.stand.standbasisprojectonev1.model.audit.DateAudit;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter 
@Setter
@Entity
@Table(name = "evaluation_core")
public class EvaluatorCore extends DateAudit{

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobId;
	
	@NotNull    
    private String area;
	
	@NotNull    
    private String subarea;
	
	@NotNull    
    private String element;
	
	@NotNull    
    private String inquiry;
	
	private String how;
	
	private Integer factor;
	
	private String optionOne, optionTwo, optionThree, optionFour, optionFive;
	
	
}
