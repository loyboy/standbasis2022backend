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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import basepackage.stand.standbasisprojectonev1.model.audit.DateAudit;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter 
@Setter
@Entity
@Table(name = "pupils")
public class Student extends DateAudit{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pupId;
    
    @NotNull    
    private String id;
    
    @NotBlank
    @NotNull
    @Size(max = 100)
    private String name;
    
    @Column(name="entry",columnDefinition="date")
    private String entry;
    
    @NotBlank
    @NotNull
    @Column(name="gender",columnDefinition="char(1)")
    private String gender;
    
    private Integer status = 1;
    
    private Integer guardian = 0;
    
    @Size(max = 100)
    private String reg_no;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sch_id", nullable = false)
    private School school;
    
    public Student() {}
}
