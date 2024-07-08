package basepackage.stand.standbasisprojectonev1.model;

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
@Table(name = "timetables")
public class TimeTable extends DateAudit {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long timeId;
    
    @NotNull    
    private String id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "sch_id", nullable = false)
    private School school;
    
   
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tea_id", nullable = false)
    private Teacher teacher;
    
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassStream class_stream;
    
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sub_id", nullable = false)
    private Subject subject;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "calendar_id", nullable = false)
    private Calendar calendar;
    
    @Size(max = 100)
    private String class_name, sub_name, tea_name;
    
    @NotNull
    @Column(name="time_of",columnDefinition="time")
    private String time_of;
    
    @NotNull
    private Integer day_of;
    
    @NotNull
    private Integer status; // 1, 0, -1
    
    public TimeTable() {}
}
