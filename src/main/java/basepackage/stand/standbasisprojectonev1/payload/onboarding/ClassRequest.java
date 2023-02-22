package basepackage.stand.standbasisprojectonev1.payload.onboarding;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class ClassRequest {
    
    @NotBlank
    @Size(max = 60)
    private String title;
    
    @Size(max = 30)
    private String ext;
    
    @NotNull
    private Integer class_index;
    
    private Integer status;    
    
    private Long sch_id;
}
