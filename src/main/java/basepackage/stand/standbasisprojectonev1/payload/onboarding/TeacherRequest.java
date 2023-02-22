package basepackage.stand.standbasisprojectonev1.payload.onboarding;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter

public class TeacherRequest {
    
    @NotBlank
    @Size(max = 60)
    private String lname, fname;
    
    @NotBlank
    private String gender;
    
    @Size(max = 100)
    private String agerange, bias, coursetype, qualification, office;
    
    @Email
    private String email;
    
    private Integer experience;
    
    private Integer status;
    
    private Integer type_of;
    
    private String photo;
    
    private Long sch_id;
}


