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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Transient;

import javax.validation.constraints.Email;
import basepackage.stand.standbasisprojectonev1.model.audit.DateAudit;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter 
@Setter
@Entity
@Table(name = "teachers")
public class Teacher extends DateAudit{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teaId;
    
    @NotNull    
    private String id;
    
    @NotBlank
    @NotNull
    @Size(max = 60)
    private String lname, fname;
    
    @NotBlank
    @NotNull
    @Column(name="gender",columnDefinition="char(3)")
    private String gender;
    
    @Column(name="qualification_education",columnDefinition="char(3)")
    private String qualification_education; //it is a Yes or No option allowed here
    
    @Size(max = 100)
    private String agerange, bias, coursetype, qualification, office;
    
    @NotNull
    @Email
    private String email;//
    
    private Integer experience;
    
    private Integer status = 1;
    
    private Integer type_of = 0;
    
    @Column(name="photo",columnDefinition="text")
    private String photo;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "sch_id", nullable = false)
    private School school;
    
    public Teacher() {}
    
    @Transient
    public String getPhotoPath() {
        if (photo == null || teaId == null) return null;
         
        return "/teacher-photo/" + teaId + "/" + photo;
    }
}
