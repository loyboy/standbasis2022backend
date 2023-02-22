package basepackage.stand.standbasisprojectonev1.payload;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class ApiContentResponse<T> {
	  	private Boolean success;
	    private String message;
	    private List<T> data;

	    public ApiContentResponse(Boolean success, String message, List<T> l) {
	        this.success = success;
	        this.message = message;
	        this.data = l;
	    }
	  
}
