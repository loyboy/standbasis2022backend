package basepackage.stand.standbasisprojectonev1.payload;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class TimetableRequestTwo {
	 	@NotNull
	    private Long class_stream, teacher, subject;
	    
	    private String time_of;
	    
	    private Integer day_of;
	    
	    private Integer status;
}
