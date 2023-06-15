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

import basepackage.stand.standbasisprojectonev1.model.audit.DateAudit;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter 
@Setter
@Entity
@Table(name = "lessonnote_managements")
public class LessonnoteManagement extends DateAudit{

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lsnmanId;
     
    private Integer quality; //not used
    
    private Integer sub_perf_classwork; 
    
    private Integer sub_perf_homework; 
    
    private Integer sub_perf_test; 
    
    private Integer management; //used for quality of Lessonnote
    
    private Integer score; 
    
    private String action; // submitted, resubmitted, reverted, launched, approved, closure, closed
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lsn_id", nullable = false)
    private Lessonnote lsn_id;
    
    public LessonnoteManagement() {
    	
    }
    
}
