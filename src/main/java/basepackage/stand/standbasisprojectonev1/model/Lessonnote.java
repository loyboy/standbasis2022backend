package basepackage.stand.standbasisprojectonev1.model;

import java.sql.Timestamp;

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
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Transient;

import basepackage.stand.standbasisprojectonev1.model.audit.DateAudit;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter 
@Setter
@Entity
@Table(name = "lessonnotes")
public class Lessonnote extends DateAudit{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lessonnoteId;
    
    @NotNull    
    private String title;    

    @NotNull   
    private Integer class_index; // 7 -Js1, 8 - JS2, 9 - JS3, 10 - SS1, 11 - SS2, 12 - SS3
    
    @Size(max = 500)
    private String comment_principal;
    
    @Size(max = 500)
    private String comment_admin;
    
    @NotNull
    private Integer week; // 1 - 
    
    private Integer classwork, homework, test, midterm, finalexam; // 1 - yes, 0 - no
    
    private Integer delaythis; //use this to control those weeks that have issues
    
    @NotNull
    private String _file; 
    
    @Column(name="submission",columnDefinition="datetime")
    private Timestamp submission; //teacher only
    
    @Column(name="resubmission",columnDefinition="datetime")
    private Timestamp resubmission; //teacher only
    
    @Column(name="revert",columnDefinition="datetime")
    private Timestamp revert; //principal only
    
    @Column(name="approval",columnDefinition="datetime")
    private Timestamp approval; //principal only
    
    @Column(name="closure",columnDefinition="datetime")
    private Timestamp closure; //teacher only
    
    @Column(name="principal_closure",columnDefinition="datetime")
    private Timestamp principal_closure; // principal only
    
    @Column(name="launch",columnDefinition="datetime")
    private Timestamp launch; // approved i done
     
    private Integer cycle_count; 
 
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tea_id", nullable = false)
    private Teacher teacher;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_id", nullable = false)
    private Subject subject;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id", nullable = false)
    private Calendar calendar;
    
    @Transient
    public String getLsnPath() {
        if (_file == null) return null;
         
        return "/teacher-lessonnote/" + lessonnoteId + "/" + _file;
    }
}

/**
 * private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
 
private final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
 
private java.sql.Date parseDate(String date) {
    try {
        return new Date(DATE_FORMAT.parse(date).getTime());
    } catch (ParseException e) {
        throw new IllegalArgumentException(e);
    }
}
 
private java.sql.Timestamp parseTimestamp(String timestamp) {
    try {
        return new Timestamp(DATE_TIME_FORMAT.parse(timestamp).getTime());
    } catch (ParseException e) {
        throw new IllegalArgumentException(e);
    }
}
 * 
 * **/
 