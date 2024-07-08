package basepackage.stand.standbasisprojectonev1.model;

import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import basepackage.stand.standbasisprojectonev1.model.audit.DateAudit;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter 
@Setter
@Entity
@Table(name = "eventmanager")
public class EventManager extends DateAudit{

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;
    
    @Column(name="module")
    private String module; // attendance, lesson note, dashboarduser
    
    @Column(name="action")
    private String action; // create, edit, delete, update
    
    @Column(name="comment")
    private String comment; //The Teacher <Name> did xxx on yyy module
    
    @NotNull    
    @Column(name="dateofevent",columnDefinition="datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateofevent; 
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE )
    @JoinColumn(name = "sch_id", nullable = false)
    private School school;
    
	 public EventManager() {}
}
