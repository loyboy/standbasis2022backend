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
@Table(name = "attendance_activities")
public class AttendanceActivity extends DateAudit{

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attactId;
       
    private Long owner; // user id of that Person
    
    @NotNull   
    private String ownertype;  // Principal/ Teacher   
    
    @NotNull  
    private Timestamp expected;     
    
    private Timestamp actual;  
    
    @NotNull
    private Integer slip; // 0 - No, 1 - Yes
    
    private String activity; // Expected to approve Calendar activity
    
    private String action; // queried/approved
    
    private String comment_query; // Optional if queried
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "att_id", nullable = false)
    private Attendance att_id;
    
    public AttendanceActivity() {
    	
    }
}
