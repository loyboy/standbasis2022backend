package basepackage.stand.standbasisprojectonev1.model;

import java.sql.Timestamp;

import javax.persistence.CascadeType;
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

import basepackage.stand.standbasisprojectonev1.model.audit.DateAudit;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter 
@Setter
@Entity
@Table(name = "calendars")
public class Calendar extends DateAudit{

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long CalendarId;
    
    @NotNull    
    private String id;    

    @NotNull   
    private Integer term; // -99 means it was switched to new term
    
    @Size(max = 500)
    private String holiday;
    
    @Column(name="start_date",columnDefinition="date")
    private Timestamp startdate;
    
    @Column(name="end_date",columnDefinition="date")
    private Timestamp enddate;
    
    @Column(name="lsn_start_date",columnDefinition="date")
    private Timestamp lsnstartdate;//The date when the official Lesson note processes will begin(2 week from startDate)
    
    private Integer lnstart; //trigger to do lessonnotes or not 1, 0
    
    @NotNull
    private Integer status; // 0- inactive, 1- active 
    
    @Size(max = 50)
    private String session;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "sch_id", nullable = false)
    private School school;
    
    public Calendar() {
    	
    }
    
}
