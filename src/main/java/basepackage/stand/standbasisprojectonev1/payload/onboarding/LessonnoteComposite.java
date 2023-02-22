package basepackage.stand.standbasisprojectonev1.payload.onboarding;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class LessonnoteComposite {

	private LessonnoteRequest lessonnote;
	private LessonnoteActivityRequest activity;
	private LessonnoteManagementRequest management;
	
	public LessonnoteComposite() {}
}
