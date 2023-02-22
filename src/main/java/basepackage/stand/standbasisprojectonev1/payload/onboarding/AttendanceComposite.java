package basepackage.stand.standbasisprojectonev1.payload.onboarding;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class AttendanceComposite {

	private AttendanceRequest attendance;
	private AttendanceManagementRequest management;
	private AttendanceActivityRequest activity;
	
	public AttendanceComposite() {}
	
}
