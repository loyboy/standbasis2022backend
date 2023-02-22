package basepackage.stand.standbasisprojectonev1.payload.onboarding;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SchoolRequest {

	    @NotBlank
	    @Size(max = 60)
	    private String name;

	    @NotBlank
	    @Size(max = 30)
	    private String type_of; // Secondary or Primary	    
	    
	    @Size(max = 50)
	    private String town;
	    
	    @NotBlank
	    @Size(max = 100)
	    private String lga, state;
	    
	    private Long owner;
	    
	    @Size(max = 50)
	    private String faith;
	    
	    @NotBlank
	    @Size(max = 100)
	    private String operator;
	    
	    @Size(max = 10)
	    private String gender;	    
	    
	    @NotBlank
	    @Size(max = 50)
	    private String residence;	    
	    
	    private Integer population;
	    
	    @Size(max = 100)
	    private String logo, location, email, phone;
	    
	    private Integer sri, status;
	    
	    @Size(max = 100)
	    private String mission, rating, tour, Calendar_upload, teachers_upload, students_upload, classroom_upload, timetable_upload;
	    
}