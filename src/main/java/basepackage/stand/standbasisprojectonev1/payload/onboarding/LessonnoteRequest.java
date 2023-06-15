package basepackage.stand.standbasisprojectonev1.payload.onboarding;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;


@Getter 
@Setter
public class LessonnoteRequest {

	    private String title; 

	    private Integer class_index;
	    
	    @Size(max = 500)
	    private String comment_principal;
	    
	    @Size(max = 500)
	    private String comment_admin;
	    
	    private Integer week; 
	    
	    @NotNull
	    private String action; //submit,resubmit,revert, approval,launch, closure
	    
	    private Integer classwork, homework, test, midterm, finalexam; 
	    
	    private String uploadFile; 
	    
	   /* private Timestamp submission; 
	    
	    private Timestamp resubmission; 
	    
	    private Timestamp revert;
	    
	    private Timestamp approval; 
	    
	    private Timestamp closure; 
	    
	    private Timestamp principal_closure;
	    
	    private Timestamp launch;*/ 	
	     
	    private Integer cycle_count;
	    
	    private Long tea_id;	    
	    
	    private Long sub_id;
	    
	    private Long calendar_id;
	
}
