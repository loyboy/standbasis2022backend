package basepackage.stand.standbasisprojectonev1.payload.onboarding;

import javax.validation.Valid;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter 
@Setter
public class OnboardRequest {
	
	@Valid
	private SchoolRequest schRequest;
	@Valid
	private List<TeacherRequest> teaRequest;
	@Valid
	private List<StudentRequest> pupRequest;
	@Valid
	private List<ClassRequest> classRequest;
	@Valid
	private List<TimetableRequest> timeRequest;
	@Valid
	private UserAccountRequest accountRequest;
	
	public OnboardRequest() {}
	
	public OnboardRequest(SchoolRequest sch,List<TeacherRequest>  tea,List<StudentRequest> pup,List<ClassRequest> cls, List<TimetableRequest> time, UserAccountRequest acct   ) {
		this.schRequest = sch;
		this.teaRequest = tea;
		this.pupRequest = pup;
		this.classRequest = cls;
		this.timeRequest = time;
		this.accountRequest = acct;
	}
	
}
