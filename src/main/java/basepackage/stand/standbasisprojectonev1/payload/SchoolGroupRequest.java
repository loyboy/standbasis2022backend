package basepackage.stand.standbasisprojectonev1.payload;

import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class SchoolGroupRequest {

	    @Size(max = 100)
	    private String name, createdat, updatedat;
}
