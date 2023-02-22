package basepackage.stand.standbasisprojectonev1.payload.onboarding;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class StudentRequest {
    
    @NotBlank
    @Size(max = 100)
    private String name, class_name, arm;
    
    @NotBlank
    @Size(max = 1)
    private String gender;
    
    @Size(max = 100)
    private String regno;
      
    private Long sch;
    
}
