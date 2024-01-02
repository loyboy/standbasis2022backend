package basepackage.stand.standbasisprojectonev1.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import basepackage.stand.standbasisprojectonev1.exception.BadRequestException;
import basepackage.stand.standbasisprojectonev1.model.RoleName;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;
import basepackage.stand.standbasisprojectonev1.model.Teacher;
import basepackage.stand.standbasisprojectonev1.model.User;
import basepackage.stand.standbasisprojectonev1.payload.ApiResponse;
import basepackage.stand.standbasisprojectonev1.payload.Permissions;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.CheckUserPasswordRequest;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.CheckUserRequest;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.UserAccountRequest;
import basepackage.stand.standbasisprojectonev1.repository.SchoolRepository;
import basepackage.stand.standbasisprojectonev1.repository.SchoolgroupRepository;
import basepackage.stand.standbasisprojectonev1.repository.TeacherRepository;
import basepackage.stand.standbasisprojectonev1.repository.TimetableRepository;
import basepackage.stand.standbasisprojectonev1.repository.UserRepository;
import basepackage.stand.standbasisprojectonev1.util.AppConstants;
import basepackage.stand.standbasisprojectonev1.util.CommonActivity;

//
@Service
public class UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);
	
	Gson gsonObj = new Gson();
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
    private JavaMailSender mailSender;
	
	@Autowired
    AuthenticationManager authenticationManager;
	
	@Autowired
    private UserRepository userRepository;	
	
	@Autowired		
    private SchoolRepository schRepository;
	
	@Autowired
    private SchoolgroupRepository schgroupRepository;
	
	public Boolean checkUsername( CheckUserRequest checkuser ) {
		Optional<User> u = userRepository.findByUsernameAndStatus( checkuser.getUsername(), 1 );
		
		return u.isPresent();
	}
	
	public Boolean changePassword( CheckUserPasswordRequest passworduser ) {
		try{ 
			Authentication authentication = authenticationManager.authenticate(        
	                new UsernamePasswordAuthenticationToken(
	                		passworduser.getUsername(),
	                		passworduser.getOldpassword()
	                )
	        );
			
			if (authentication != null) {
				Optional<User> existing = userRepository.findByUsername(passworduser.getUsername());
				if (existing.isPresent()) {
					User userval = existing.get();
					userval.setPassword( passwordEncoder.encode(passworduser.getNewpassword()) );
					userRepository.save(userval);
					return true;
				}
			}
			else {
				return false;
			}
			return false;
		}
	    catch (BadCredentialsException ex) {
	        return false;
	    }	
		
	}
	
	
	public List<User> findAll() {
		
		return userRepository.findAll();
	}
	
	public User findUser(long id) {		
		Optional<User> enc = userRepository.findById(id);
		if (enc.isPresent()) {
			User timeval = enc.get();			
			return timeval;
		}
		return null;
	}
	
	/*public User findUserByRoleAndSchool(RoleName r, School sch) {		
		Optional<User> user = userRepository.findByRoleAndSchool(r, sch);
		if (user.isPresent()) {
			User userval = user.get();			
			return userval;
		}
		return null;		
	}*/
	
	public Map<String, Object> getPaginatedUsers(int page, int size, String query, Optional<Long> ownerval, Optional<Long> groupval ) {
		CommonActivity.validatePageNumberAndSize(page, size);
            
		Long owner = ownerval.orElse(null);
        Long group = groupval.orElse(null);
       // Long teacher = teacherval.orElse(null);
        
        // Retrieve Users
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<User> schs = null;
        
        if ( query.equals("") || query == null ) { 
        	if ( owner == null  && group == null  ) {
        		schs = userRepository.findAll(pageable);
        	}
        	else {
        		Optional<School> schobj = null;
        		Optional<SchoolGroup> schgroupobj = schgroupRepository.findById( group );
        		if(owner != null) { schobj = schRepository.findById( owner );  }
        		
        		schs = userRepository.findBySchool( 
        				schobj == null ? null : schobj.get(), 
        				schgroupobj == null ? null : schgroupobj.get(), 		
        				
                		pageable
        		);
        	}
        }
        else {        	
        	if ( owner == null &&  group == null  ) {
        		schs = userRepository.filter("%"+ query + "%",  pageable); 
        	}
        	else {
        		Optional<School> schobj = null;
        		Optional<SchoolGroup> schgroupobj = schgroupRepository.findById( group );
        		if(owner != null) { schobj = schRepository.findById( owner );  }
        		
        		schs = userRepository.findFilterBySchool( 
        				"%"+ query + "%",
        				schobj == null ? null : schobj.get(), 
        				schgroupobj == null ? null : schgroupobj.get(), 		
        				
                		pageable
        		);
        	}
        }

        if(schs.getNumberOfElements() == 0) {
        	Map<String, Object> responseEmpty = new HashMap<>();
        	responseEmpty.put("users", Collections.emptyList());
        	responseEmpty.put("currentPage", schs.getNumber());
        	responseEmpty.put("totalItems", schs.getTotalElements());
        	responseEmpty.put("totalPages", schs.getTotalPages());
        	
        	return responseEmpty;
        }
        
        List<User> userarray = new ArrayList<User>();
        
        userarray = schs.getContent();
        
        Map<String, Object> response = new HashMap<>();
        response.put("users", userarray);
        response.put("currentPage", schs.getNumber());
        response.put("totalItems", schs.getTotalElements());
        response.put("totalPages", schs.getTotalPages());
        response.put("isLast", schs.isLast());
        
       // long active = 1; long inactive = 0;
      /*  long sriUsers = schRepository.countBySri(active);
        long nonSriUsers = schRepository.countBySri(inactive);
        long inactiveUsers = schRepository.countByStatus(inactive);*/
        
        long activeUsers = userarray.stream().filter(sch -> sch.getStatus() == 1).count();       
        long inactiveUsers = userarray.stream().filter(sch -> sch.getStatus() == 0).count();
        
        response.put("totalActive", activeUsers);
        response.put("totalInactive", inactiveUsers);
        return response;
    }
	
	public User update(UserAccountRequest acctRequest,long id) {
		Optional<User> existing = userRepository.findById(id);
		if (existing.isPresent()) {
			User acctval = existing.get();
			CommonActivity.copyNonNullProperties(acctRequest, acctval);
			return userRepository.save(acctval);
		}	   
		
		return null;
	}
	
	public User delete(Long id) {
		Optional<User> tea = userRepository.findById(id);
		if (tea.isPresent()) {
			User timeval = tea.get();
			timeval.setStatus(-1);
			userRepository.save(timeval);
			return timeval;
		}
		return null;
	}
	
	public User createSchoolViaDashboard(String standbasis_unique_number,String school_name, String school_email, String school_physical_address, String school_telephone_number, String contact_person_name, String designation ) {
		
		Optional<SchoolGroup> sg = schgroupRepository.findById( (long) 2 );
		
		School sch = new School();
		sch.setOwner(sg.get());
		sch.setName(school_name);
		sch.setType_of("unknown");
		sch.setState("unknown");
		sch.setResidence("unknown");
		sch.setOperator("unknown");
		sch.setLga("unknown");
		sch.setEmail(school_email);
		sch.setPhone(school_telephone_number);
		
		School savedSchool = schRepository.save(sch);
		
		String from = "info@standbasis.com";
		String to = school_email;
		 
		SimpleMailMessage message = new SimpleMailMessage();
		String specialIdUser2 = createUuid("user-", savedSchool.getSchId() );
		String specialIdUsername = createUuidUsername("dashboard");
		String specialPassword = createUuidPassword();
		
			User _u = new User();
		
			_u.setId(specialIdUser2);
    		_u.setUsername(specialIdUsername);
    		_u.setStatus(1);
    		_u.setEmail( school_email );//change
    		_u.setName( school_name );
    		_u.setRole(RoleName.DASHBOARDUSER);
    		_u.setPassword( passwordEncoder.encode( specialPassword ) );
    		_u.setSchool(savedSchool);
    		
    		Map<String, Object> _attributes = new HashMap<>();
	    	
	    	_attributes.put("school",  new Permissions( true, false, false, false));
	    	_attributes.put("teacher", new Permissions( false, false, false, false));
	    	_attributes.put("enrollment", new Permissions( false, false, false, false));
	    	_attributes.put("classroom", new Permissions( false, false, false, false));
	    	_attributes.put("calendar", new Permissions( false, false, false, false));
	    	_attributes.put("timetable", new Permissions( false, false, false, false));
	    	_attributes.put("user", new Permissions( false, false, false, false));
	    	_attributes.put("attendance", new Permissions( false, false, false, false));
	    	_attributes.put("lessonnote", new Permissions( false, false, false, false));
	    	
	    	String jsonStr2 = gsonObj.toJson(_attributes);		
	    	
	    	_u.setPermissionsJSON(jsonStr2);
	    	
	    	message.setFrom(from);
    		message.setTo(to);
    		message.setSubject("Welcome to Standbasis :: You are the Contact Person from " + savedSchool.getName() + " school" );
    		message.setText("Hello sir/mrs! This is to congratulate you on your successful dashboard onboarding process into the Standbasis school standards management system. Your login details are: " + System.lineSeparator() + "Username: " + specialIdUsername +  System.lineSeparator() + "Password: " + specialPassword + System.lineSeparator() + "This is a temporary password that you will need to change as soon as possible." );
    		mailSender.send(message);
    		
    		User savedUser = userRepository.save(_u);
    		//TimeUnit.SECONDS.sleep(1);
		
		return savedUser;
		
	}
	
	private String createUuid( String type, Long schId ) {
    	String uuid = UUID.randomUUID().toString();
    	String[] uniqueCode = uuid.split("-");    	
    	String baseId = type + schId.toString() + "-" + uniqueCode[4].substring(6);
    	return baseId;
    }
    
    private String createUuidUsername( String type ) {
    	String uuid = UUID.randomUUID().toString();
    	String[] uniqueCode = uuid.split("-");    	
    	String baseId = type + "-" + uniqueCode[4].substring(4);
    	return baseId;
    }
    
    private String createUuidPassword() {
    	String uuid = UUID.randomUUID().toString();
    	String[] uniqueCode = uuid.split("-");    	
    	String newPass =  uniqueCode[4].substring(5);
    	return newPass;
    }
	
}
