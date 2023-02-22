package basepackage.stand.standbasisprojectonev1.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
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
@Table(name = "schools", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
            "email"
        })
        ,
        @UniqueConstraint(columnNames = {
            "phone"
        })
})
public class School extends DateAudit {
	 	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long schId;
	        
	    private String id;

	    @NotBlank
	    @NotNull
	    @Size(max = 60)
	    private String name;

	    @NotBlank
	    @NotNull
	    @Size(max = 30)
	    private String type_of; // Secondary or Primary	     
	    
	    @Size(max = 50)
	    private String town;
	    
	    @NotBlank
	    @NotNull
	    @Size(max = 100)
	    private String lga, state;	    
	   
	    
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "owner", nullable = false)
	    private SchoolGroup owner;
	    
	    @Size(max = 50)
	    private String faith;
	    
	    @NotBlank
	    @NotNull
	    @Size(max = 100)
	    private String operator;
	    
	    @Size(max = 10)
	    private String gender = "all";	    
	    
	    @NotBlank
	    @NotNull
	    @Size(max = 50)
	    private String residence;	    
	    
	    private Integer population;
	    
	    @Size(max = 100)
	    private String logo, location, email, phone;
	    
	    private Integer sri, status = 0;
	    
	    @Size(max = 100)
	    private String mission, rating, tour, Calendar_upload, teachers_upload, students_upload, classroom_upload, timetable_upload;
	    
	    public School() {
	    	
	    }
	    
	    @Transient
	    public String getLogoPath() {
	        if (logo == null || schId == null) return null;
	         
	        return "/school-logo/" + schId + "/" + logo;
	    }
	    
}
