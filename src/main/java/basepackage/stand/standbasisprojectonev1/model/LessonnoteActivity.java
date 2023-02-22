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
@Table(name = "lessonnote_activities")
public class LessonnoteActivity extends DateAudit{

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lsnactId;
       
    private Long owner; // user id of that Person
    
    @NotNull   
    private String ownertype;  // Principal/ Teacher   
    
    @NotNull  
    private Timestamp expected;     
    
    private Timestamp actual;  
    
    private Integer slip; // 0 - No, 1 - Yes
    
    private String activity; // Expected to approve Calendar activity
    
    private String action; // revert/approved/re-submitted/submitted
    
    private String comment_query; // Optional if queried
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lsn_id", nullable = false)
    private Lessonnote lsn_id;
    
    public LessonnoteActivity() {
    	
    }
}
