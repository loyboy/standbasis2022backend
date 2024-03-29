package basepackage.stand.standbasisprojectonev1.controller;

import basepackage.stand.standbasisprojectonev1.model.RoleName;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.User;
import basepackage.stand.standbasisprojectonev1.model.Calendar;
import basepackage.stand.standbasisprojectonev1.model.EventManager;
import basepackage.stand.standbasisprojectonev1.payload.ApiContentResponse;
import basepackage.stand.standbasisprojectonev1.payload.ApiResponse;
import basepackage.stand.standbasisprojectonev1.payload.LoginRequest;
import basepackage.stand.standbasisprojectonev1.payload.LoginResponse;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.CheckUserPasswordRequest;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.CheckUserRequest;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.DashboardOnboardRequest;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.OnboardRequest;
import basepackage.stand.standbasisprojectonev1.repository.EventManagerRepository;
import basepackage.stand.standbasisprojectonev1.repository.UserRepository;
import basepackage.stand.standbasisprojectonev1.security.JwtTokenProvider;
import basepackage.stand.standbasisprojectonev1.security.UserPrincipal;
import basepackage.stand.standbasisprojectonev1.service.OnboardingService;
import basepackage.stand.standbasisprojectonev1.service.CalendarService;
import basepackage.stand.standbasisprojectonev1.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

/**
 * Created by Loy from August 2022..
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Autowired
	private EventManagerRepository eventRepository;

    @Autowired
    JwtTokenProvider tokenProvider;
    
    @Autowired
    private OnboardingService boardService;
    
    @Autowired
    private CalendarService calService;
    
    //
    @Autowired
    private UserService userService;
    
    //private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        try{ 
        	Authentication authentication = authenticationManager.authenticate(        
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        	);

		        SecurityContextHolder.getContext().setAuthentication(authentication);
		
		        String jwt = tokenProvider.generateToken(authentication);
		        
		        User user = userRepository.findByUsernameAndStatus( loginRequest.getUsername(), 1 )
		                .orElseThrow(() ->
		                        new UsernameNotFoundException("User not found with username  : " +  loginRequest.getUsername() )
		        );        
		      
		        Long realId = null;	        
		        
		        LoginResponse lgres = new LoginResponse();
		        
		        if ( user.getRole() == RoleName.TEACHER) {
		        	Calendar foundCal = calService.findAllByStatus( user.getSchool().getSchId() , 1).get();
		        	if (foundCal == null) {
		        		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse(false, "Login has failed due to Calendar expiration."));   
		        	}
		        	
		        	LocalDate stDate = foundCal.getStartdate().toInstant().atZone(ZoneId.of("UTC")).toLocalDate();
					LocalDate endDate = foundCal.getEnddate().toInstant().atZone(ZoneId.of("UTC")).toLocalDate();
					LocalDate todayDate = LocalDate.now();
					int weekNumber = calculateWeekNumber(todayDate, stDate, endDate);
					
		        	realId = user.getUserId();		        	
		        	lgres.setPermissions(user.getPermissionsJSON());
		        	lgres.setUsername(user.getName());
		            lgres.setAccess_token(jwt);
		            lgres.setSchool_date( new Date( foundCal.getStartdate().getTime() ).toLocaleString() );
		            lgres.setSchool_name( user.getSchool().getName() );
		            lgres.setCalendar_id( foundCal.getCalendarId() );
		            lgres.setCalendar_text( "Week "+weekNumber + " | " + foundCal.getSession() + " | " + "Term " + foundCal.getTerm() );
		            //lgres.setSchool_date( "2023-01-01" );
		            lgres.setEmail(user.getEmail());
		            lgres.setRole("teacher");
		            lgres.setData_id(user.getTeacher_id());
		            lgres.setId(realId);
		        }
		        
		        if ( user.getRole() == RoleName.PRINCIPAL) {
		        	Calendar foundCal = calService.findAllByStatus( user.getSchool().getSchId() , 1).get();
		        	realId = user.getUserId();
		        	if (foundCal == null) {
		        		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse(false, "Login has failed due to Calendar expiration."));   
		        	}
		        	
		        	LocalDate stDate = foundCal.getStartdate().toInstant().atZone(ZoneId.of("UTC")).toLocalDate();
					LocalDate endDate = foundCal.getEnddate().toInstant().atZone(ZoneId.of("UTC")).toLocalDate();
					LocalDate todayDate = LocalDate.now();
					int weekNumber = calculateWeekNumber(todayDate, stDate, endDate);
					
		        	lgres.setPermissions(user.getPermissionsJSON());
		        	lgres.setUsername(user.getName());
		        	lgres.setSchool_name( user.getSchool().getName() );
		        	lgres.setCalendar_id( foundCal == null ? null : foundCal.getCalendarId() );
		        	lgres.setCalendar_text( "Week "+weekNumber + " | " + foundCal.getSession() + " | " + "Term " + foundCal.getTerm() );
			          
		            lgres.setAccess_token(jwt);
		            lgres.setEmail(user.getEmail());
		            lgres.setRole("principal");
		            lgres.setData_id(user.getPrincipal_id());
		            lgres.setId(realId);
		        }
		        
		        if ( user.getRole() == RoleName.PROPRIETOR) {
		        	realId = user.getUserId();
		        	
		        	lgres.setPermissions(user.getPermissionsJSON());
		        	lgres.setUsername(user.getName());
		            lgres.setAccess_token(jwt);
		            lgres.setEmail(user.getEmail());
		            lgres.setRole("proprietor");
		            lgres.setData_id(user.getProprietor_id());
		            lgres.setId(realId);
		            
		            System.out.println( " >> " + lgres );
		        }
		        
		        if ( user.getRole() == RoleName.SUPERADMIN) {
		        	realId = user.getUserId();//xxxx  bbbb
		        	
		        	lgres.setPermissions(user.getPermissionsJSON());
		        	lgres.setUsername(user.getUsername());
		            lgres.setAccess_token(jwt);
		            lgres.setEmail(user.getEmail());
		            lgres.setRole("admin");
		            lgres.setData_id(null);
		            lgres.setId(realId);
		        }   
		        
		        if ( user.getRole() == RoleName.EVALUATOR) {
		        	realId = user.getUserId();//xxxx
		        	
		        	lgres.setPermissions(null);
		        	lgres.setUsername(user.getName());
		            lgres.setAccess_token(jwt);
		            lgres.setEmail(user.getEmail());
		            lgres.setSchool_id( user.getSchool().getSchId() );
		            lgres.setRole("evaluator");
		            lgres.setData_id(null);
		            lgres.setId(realId);
		        } 
		        
		        if ( user.getRole() == RoleName.SUPERVISOR) {
		        	realId = user.getUserId();//xxxx
		        	
		        	lgres.setPermissions(user.getPermissionsJSON());
		        	lgres.setUsername(user.getName());
		            lgres.setAccess_token(jwt);
		            lgres.setEmail(user.getEmail());
		            lgres.setSchool_id( null );
		            lgres.setRole("supervisor");
		            lgres.setData_id(null);
		            lgres.setId(realId);
		            lgres.setCode( user.getSupervisor_id() );
		        } 
		        
		        if ( user.getRole() == RoleName.GUARDIAN) {
		        	realId = user.getUserId();//xxxx
		        	
		        	lgres.setPermissions(user.getPermissionsJSON());
		        	lgres.setUsername(user.getName());
		            lgres.setAccess_token(jwt);
		            lgres.setEmail(user.getEmail());
		            lgres.setSchool_id( null );
		            lgres.setRole("guardian");
		            lgres.setData_id(null);
		            lgres.setId(realId);
		            lgres.setCode( user.getGuardian_id() );
		        } 
		        
		        if ( user.getRole() == RoleName.DASHBOARDUSER) {
		        	realId = user.getUserId();
		        	
		        	lgres.setPermissions(user.getPermissionsJSON());
		        	lgres.setUsername(user.getUsername());
		            lgres.setAccess_token(jwt);
		            lgres.setEmail(user.getEmail());
		            lgres.setSchool_id( user.getSchool().getSchId() );
		            lgres.setSchool_name( user.getSchool().getName() );
		            lgres.setRole("dashboarduser");
		            lgres.setData_id(null);
		            lgres.setId(realId);
		           
		        } 
		        
		       // System.out.println( " Ending >> " + lgres.getId() );
		        return ResponseEntity.ok().body(lgres);        
        }
        catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "Login has failed due to invalid user credentials."));
        }
    }
   
	@PostMapping("/onboard")
    public ResponseEntity<?> onboardUser(@Valid @RequestBody OnboardRequest simpleRequest) {
		
		/**/
		if ( simpleRequest.getSchRequest() != null && 
				 simpleRequest.getTeaRequest() != null && 
				 simpleRequest.getPupRequest() != null &&
				 simpleRequest.getClassRequest() != null &&
				 simpleRequest.getTimeRequest() != null && 
				 simpleRequest.getAccountRequest() != null ) {
			
			boolean status = boardService.onboardNewSchool( 
					simpleRequest.getSchRequest(), 
					simpleRequest.getTeaRequest(),
					simpleRequest.getPupRequest(),
					simpleRequest.getClassRequest(),
					simpleRequest.getTimeRequest(),
					simpleRequest.getAccountRequest() );
			
			if (status) {
				return ResponseEntity.ok().body(new ApiResponse(true, "School Onboarding successfully"));
			}
			else {
				return ResponseEntity.status(500).body(new ApiResponse(false, "School Onboarding failed"));
			}
			
		} 
		
		return ResponseEntity.ok( "cannot Onboard" ); 
		
	}
	
	@PostMapping("/checkUsername")
    public ResponseEntity<?> checkUsernameExists(@Valid @RequestBody CheckUserRequest checkuser) {
		
		boolean status = userService.checkUsername( checkuser );
		if (status) {
			return ResponseEntity.ok().body(new ApiResponse(true, "true" ));
		}
		else {
			return ResponseEntity.ok().body(new ApiResponse(false, "false"));
		}
	}
	
	@PostMapping("/uploadDashboardOnboard")
    public ResponseEntity<?> uploadDashboardOnboard(@Valid @RequestBody DashboardOnboardRequest dashboard) {
		
		User newuser = userService.createSchoolViaDashboard( 
				dashboard.getFormData().getStandbasis_unique_number(),
				dashboard.getFormData().getSchool_name(),
				dashboard.getFormData().getSchool_email(),
				dashboard.getFormData().getSchool_physical_address(),
				dashboard.getFormData().getSchool_telephone_number(),
				dashboard.getFormData().getContact_person_name(),
				dashboard.getFormData().getDesignation()
		);
		if (newuser != null) {
			return ResponseEntity.ok().body(new ApiResponse(true, "true" ));
		}
		else {
			return ResponseEntity.ok().body(new ApiResponse(false, "false"));
		}
	}
	
	@PostMapping("/changePassword")
	public ResponseEntity<?> changePasswords(@AuthenticationPrincipal UserPrincipal userDetails, @Valid @RequestBody CheckUserPasswordRequest user) {
		boolean status = userService.changePassword( user );
		if (status) {
			
			
		    
			Optional<User> u = userRepository.findById( userDetails.getId() );
				
			//------------------------------------
			saveEvent("dashboarduser", "edit", "The User with name: " + u.get().getUsername() + " has edited a Password instance. ", 
					 new Date(), u.get(), u.get().getSchool()
			);
			 
			return ResponseEntity.ok().body(new ApiResponse(true, "Password Changed" ));
		}
		else {
			return ResponseEntity.ok().body(new ApiResponse(false, "Password Not Changed"));
		}	
		
	}
	
	
	
	public static int calculateWeekNumber(LocalDate today, LocalDate startDate, LocalDate endDate) {
	        long daysBetween = ChronoUnit.DAYS.between(startDate, today);
	        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);

	        if (daysBetween < 0 || daysBetween > totalDays) {
	            throw new IllegalArgumentException("Today's date is outside the specified date range");
	        }

	        // Adding 1 to start from Week 1 instead of Week 0
	        return (int) (daysBetween / 7) + 1;
	 }
	
	private EventManager saveEvent( String module, String action, String comment, Date d, User u, School sch ) {		 
	 	
	 	EventManager _event = new EventManager();
	 	
	 	_event.setModule(module);
 		_event.setAction(action);
 		_event.setComment(comment);
 		_event.setDateofevent(d);	
 		_event.setUser(u);
 		_event.setSchool(sch);
 		
 		return eventRepository.save(_event);
	}
}
