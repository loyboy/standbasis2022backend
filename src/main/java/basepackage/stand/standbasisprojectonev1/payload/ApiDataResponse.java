package basepackage.stand.standbasisprojectonev1.payload;

import lombok.Getter;
import lombok.Setter;
@Getter 
@Setter
public class ApiDataResponse {
	private Boolean success;
    private String message;
    private Object data;

    public ApiDataResponse(Boolean success, String message, Object l) {
        this.success = success;
        this.message = message;
        this.data = l;
    }
}
