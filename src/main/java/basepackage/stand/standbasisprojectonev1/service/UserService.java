package basepackage.stand.standbasisprojectonev1.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
import org.springframework.stereotype.Service;

import basepackage.stand.standbasisprojectonev1.exception.BadRequestException;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;
import basepackage.stand.standbasisprojectonev1.model.Teacher;
import basepackage.stand.standbasisprojectonev1.model.User;
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
	
	@Autowired
    private UserRepository userRepository;	
	
	@Autowired		
    private SchoolRepository schRepository;
	
	@Autowired
    private SchoolgroupRepository schgroupRepository;
	
	@Autowired		
    private TimetableRepository timeRepository;
	
	@Autowired		
    private TeacherRepository teaRepository;
	
	public Boolean checkUsername( CheckUserRequest checkuser ) {
		Optional<User> u = userRepository.findByUsernameAndStatus( checkuser.getUsername(), 1 );
		
		return u.isPresent();
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
	
}
