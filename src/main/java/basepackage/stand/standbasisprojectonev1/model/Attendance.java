package basepackage.stand.standbasisprojectonev1.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Transient;

import basepackage.stand.standbasisprojectonev1.model.audit.DateAudit;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter 
@Setter
@Entity
@Table(name = "attendances")
public class Attendance extends DateAudit{

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attId;
    
    @NotNull    
    @Column(name="attendance_date",columnDefinition="datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date _date;    
 
    @Column(name="_period",columnDefinition="char(1)")
    private String period;
    
    @Size(max = 500)
    private String image;
    
    @NotNull
    private Integer done; // 0- not done, 1- done, -1 voided, 2 - late
    
    private Long delegated; // ID of teacher that is assigned to use this Attendance
    
    @Size(max = 500)
    private String _desc;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timetable_id", nullable = false)
    private TimeTable timetable;
    
    //Though Redundant, but i included it here just to help in queries since it is generated
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id", nullable = false)
    private Calendar calendar;    
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher; //Used as the actual teacher that taught that class, irrespective of what the timetable says
    
    public Attendance() {
    	
    }
    
    @Transient
    public String getPhotoPath() {
        if (image == null) return null;
         
        return "/teacher-attendance/" + attId + "/" + image;
    }
}
