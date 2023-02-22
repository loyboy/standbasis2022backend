package basepackage.stand.standbasisprojectonev1.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import basepackage.stand.standbasisprojectonev1.model.EventManager;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;
import basepackage.stand.standbasisprojectonev1.model.User;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.EventManagerRequest;
import basepackage.stand.standbasisprojectonev1.repository.EventManagerRepository;
import basepackage.stand.standbasisprojectonev1.repository.SchoolRepository;
import basepackage.stand.standbasisprojectonev1.repository.SchoolgroupRepository;
import basepackage.stand.standbasisprojectonev1.repository.UserRepository;
import basepackage.stand.standbasisprojectonev1.util.CommonActivity;

@Service
public class EventManagerService {

private static final Logger logger = LoggerFactory.getLogger(EventManagerService.class);
	
	@Autowired		
    private EventManagerRepository eventmanagerRepository;
	
	@Autowired		
    private SchoolRepository schRepository;
	
	@Autowired		
    private UserRepository userRepository;	
	
	@Autowired
    private SchoolgroupRepository groupRepository;

	public List<EventManager> findAll() {		
		return eventmanagerRepository.findAll();
	}
	
	public List<EventManager> findAllBySchool(Long id) {		
		Optional<School> sch = schRepository.findById(id);
		if (sch.isPresent()) {
			School schval = sch.get();			
			return eventmanagerRepository.findBySchool(schval);
		}
		return null;		
	}	
	
	public EventManager findEvent(long id) {		
		Optional<EventManager> evt = eventmanagerRepository.findById(id);
		if (evt.isPresent()) {
			EventManager eventval = evt.get();			
			return eventval;
		}
		return null;
	}
	
	public EventManager saveOne(EventManagerRequest evtRequest) {
		 ModelMapper modelMapper = new ModelMapper();   
		 EventManager evt = modelMapper.map(evtRequest, EventManager.class);
		 
		 Optional<School> s = schRepository.findById( evtRequest.getSch_id() );
		 Optional<User> u = userRepository.findById( evtRequest.getUser_id() );
		 
		 if ( s.isPresent() && u.isPresent() ) {
			 evt.setSchool( s.get() );
			 evt.setUser( u.get() );
			 
			 return eventmanagerRepository.save(evt);
		 }
		 return null;
	}
	
	public Map<String, Object> getPaginatedEvents(int page, int size, String query, String module, Optional<Long> schId, Optional<Long> schgroupId) {
		CommonActivity.validatePageNumberAndSize(page, size);
		
		Long schowner = schId.orElse(null);
		Long schgroup = schgroupId.orElse(null);

        // Retrieve group of Schools
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<EventManager> events = null;
       
        if ( query.equals("") || query == null ) {
        	if ( schowner == null && schgroup == null ) {
        		events = eventmanagerRepository.findAll(pageable);
        	}else {
        		Optional<School> schownerobj = null;
        		Optional<SchoolGroup> schgroupobj = null;
        		
        		if(schowner != null) { schownerobj = schRepository.findById( schowner );  }
        		if(schgroup != null) { schgroupobj = groupRepository.findById( schgroup );  }
        		
        		events = eventmanagerRepository.findByEventSchoolPage(       				
                        schownerobj == null ? null : schownerobj.get(), 
                        schgroupobj == null ? null : schgroupobj.get(), 
        				pageable
        		);
        	}
        }
        else {
        	if ( schowner == null && schgroup == null ) {
        		events = eventmanagerRepository.filter("%"+ query + "%",  pageable);
        	}
        	else {
        		Optional<School> schownerobj = null;
        		Optional<SchoolGroup> schgroupobj = null;
        		
        		if(schowner != null) { schownerobj = schRepository.findById( schowner );  }
        		if(schgroup != null) { schgroupobj = groupRepository.findById( schgroup );  }        		
        		
        		events = eventmanagerRepository.findFilterByEventSchoolPage(  
        				"%"+ query + "%",
                        schownerobj == null ? null : schownerobj.get(), 
                        schgroupobj == null ? null : schgroupobj.get(),
                        module == null ? null : module,
        				pageable
        		);
        	}        	
        }

        if(events.getNumberOfElements() == 0) {
        	Map<String, Object> responseEmpty = new HashMap<>();
        	responseEmpty.put("events", Collections.emptyList());
        	responseEmpty.put("currentPage", events.getNumber());
        	responseEmpty.put("totalItems", events.getTotalElements());
        	responseEmpty.put("totalPages", events.getTotalPages());
        	
        	return responseEmpty;
        }
        
        List<EventManager> evtarray = new ArrayList<EventManager>();
        
        evtarray = events.getContent();
        
        Map<String, Object> response = new HashMap<>();
        
        response.put("events", evtarray);
        response.put("currentPage", events.getNumber());
        response.put("totalItems", events.getTotalElements());
        response.put("totalPages", events.getTotalPages());
        response.put("isLast", events.isLast());
        
        return response;
    }
	
	
}
