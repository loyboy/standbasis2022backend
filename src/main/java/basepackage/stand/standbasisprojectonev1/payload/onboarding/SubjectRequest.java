package basepackage.stand.standbasisprojectonev1.payload.onboarding;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class SubjectRequest {
    
    @NotBlank
    @Size(max = 100)
    private String name, category;
    
    @Size(max = 100)
    private String sch_id;
    
    public SubjectRequest(String name, String category) {
        this.name = name;
        this.category = category;
    }
}