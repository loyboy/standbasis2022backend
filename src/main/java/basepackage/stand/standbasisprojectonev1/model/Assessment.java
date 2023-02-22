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

import basepackage.stand.standbasisprojectonev1.model.audit.DateAudit;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter 
@Setter
@Entity
@Table(name = "assessments")
public class Assessment extends DateAudit{

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assessId;
    
    @NotNull    
    private String title; 
    
    @NotNull    
    @Column(name="assessment_type",columnDefinition="char(5)")
    private String _type; // clw, tst, hwk, mid, final 

    private Integer actual;
    
    private Integer max;
    
    private Integer score;
    
    @Column(name="assessment_date",columnDefinition="datetime")
    private Timestamp _date;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrol_id", nullable = false)
    private Enrollment enroll;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lsn_id", nullable = false)
    private Lessonnote lsn;
    
    public Assessment() {}
   
}
