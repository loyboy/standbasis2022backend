package basepackage.stand.standbasisprojectonev1.model;

import javax.persistence.Column;
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
@Table(name = "enrollments")
public class Enrollment extends DateAudit{

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long enrolId;
    
    @NotNull    
    private String id;

    private Integer session_count;
    
    @Column(name="enrol_date",columnDefinition="date")
    private String enrol_date;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pup_id", nullable = false)
    private Student student;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassStream classstream;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id", nullable = true)
    private Calendar calendar;
    
    private Integer status; // 1, 0, -1
    
    public Enrollment() {}
    public Enrollment(String id, Student s, ClassStream c, Calendar cal, Integer status  ) {
    	this.id = id;
    	this.student = s;
    	this.classstream = c;
    	this.calendar = cal;
    	this.status = status;
    }
}
