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
@Table(name = "attendance_managements")
public class AttendanceManagement extends DateAudit {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attmanId;
    
    @NotNull   
    private Integer timing;
    
    @NotNull   
    private Integer class_perf;    
    
    @NotNull  
    private Integer completeness; 
    
    @NotNull
    private Integer score;  
    
    @NotNull
    private Integer action; // 0 - unattended, 1 - Approved, 2 - There is an issue
    
    private String comment;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "att_id", nullable = false)
    private Attendance att_id;
    
    public AttendanceManagement() {
    	
    }
}
