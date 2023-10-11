package basepackage.stand.standbasisprojectonev1.payload;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class LoginResponse{

	
	private String username;
	private String email;
	private String access_token;
	private String school_date;
	private Long school_id;
	private Long calendar_id;
	private String calendar_text;
	private String school_name;
	private String role;
	private Long data_id;
	private String permissions;
	private Long id;
	private String code;
	
	public LoginResponse() {
		
	}
}
