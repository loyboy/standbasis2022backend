package basepackage.stand.standbasisprojectonev1.model;

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

import basepackage.stand.standbasisprojectonev1.model.audit.DateAudit;
import lombok.Getter;
import lombok.Setter;


@SuppressWarnings("serial")
@Getter 
@Setter
@Entity
@Table(name = "attendance_managements")
public class AttendanceManagement extends DateAudit {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attmanId;
    
    @NotNull   
    private Integer timing;//100 - good, 50 - late , 0 - voided
    
    @NotNull   
    private Integer class_perf;    
    
    @NotNull  
    private Integer completeness; //100 - good, 50 incomplete
    
    @NotNull
    private Integer score;  
    
    @NotNull
    private Integer action; // 0 - unattended, 1 - Approved, 2 - There is an issue, 3 - resolved
    
    private String comment;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "att_id", nullable = false)
    private Attendance att_id;
    
    public AttendanceManagement() {
    	
    }
}
