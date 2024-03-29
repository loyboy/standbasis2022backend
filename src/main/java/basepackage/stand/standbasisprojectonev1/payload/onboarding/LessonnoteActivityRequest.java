package basepackage.stand.standbasisprojectonev1.payload.onboarding;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class LessonnoteActivityRequest {
   
	private Long lsnactivityId;
	
    private Long owner; // user id of that Person
       
    private String ownertype;  // Principal/ Teacher   
      
    private Timestamp expected;     
    
    private Timestamp actual;  
    
    private Integer slip; // 0 - No, 1 - Yes
    
    private String activity; // Expected to approve Calendar activity
    
    private String action; // revert/approved/re-submitted/submit
    
    private String comment_query;
    
    private Integer principal_query_arrangement; // 1 - Satisfied, 0 - Bad
    private Integer principal_query_grammar; // 1 - Satisfied, 0 - Bad
    private Integer principal_query_subjectmatter;
    private Integer principal_query_incomplete;
    
    private Long lsn_id;
}
