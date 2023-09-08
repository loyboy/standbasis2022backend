package basepackage.stand.standbasisprojectonev1.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import basepackage.stand.standbasisprojectonev1.model.Calendar;
import basepackage.stand.standbasisprojectonev1.model.Lessonnote;
import basepackage.stand.standbasisprojectonev1.model.LessonnoteManagement;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;
import basepackage.stand.standbasisprojectonev1.model.Subject;
import basepackage.stand.standbasisprojectonev1.model.Teacher;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.LessonnoteManagementRequest;
import basepackage.stand.standbasisprojectonev1.repository.LessonnoteManagementRepository;
import basepackage.stand.standbasisprojectonev1.repository.CalendarRepository;
import basepackage.stand.standbasisprojectonev1.repository.LessonnoteRepository;
import basepackage.stand.standbasisprojectonev1.repository.SchoolRepository;
import basepackage.stand.standbasisprojectonev1.repository.SchoolgroupRepository;
import basepackage.stand.standbasisprojectonev1.repository.SubjectRepository;
import basepackage.stand.standbasisprojectonev1.repository.TeacherRepository;
import basepackage.stand.standbasisprojectonev1.util.CommonActivity;

@Service
public class LessonnoteManagementService {

	@Autowired		
    private LessonnoteRepository lsnRepository;	
	
	@Autowired		
    private LessonnoteManagementRepository lsnmanageRepository;	
	
	@Autowired
    private SchoolgroupRepository groupRepository;
	
	@Autowired		
    private SchoolRepository schRepository;
	
	@Autowired		
    private TeacherRepository teaRepository;
	
	@Autowired		
    private CalendarRepository calRepository;
	
	@Autowired		
    private SubjectRepository subRepository;
	
	public List<LessonnoteManagement> findAll() {		
		return lsnmanageRepository.findAll();
	}
	
	public LessonnoteManagement saveOne(LessonnoteManagementRequest lsnmanageRequest, Lessonnote lsn) {
		 ModelMapper modelMapper = new ModelMapper();   
		 LessonnoteManagement val = modelMapper.map(lsnmanageRequest, LessonnoteManagement.class);
		// Optional<Lessonnote> lsnmanage = lsnRepository.findById( lsnmanageRequest.getLsn_id() );
		 val.setLsn_id(lsn);			
		 return lsnmanageRepository.save(val);	 
	}
	
	public LessonnoteManagement findLessonnoteManagement(Long id) {		
		Optional<LessonnoteManagement> lsn = lsnmanageRepository.findById(id);
		if (lsn.isPresent()) {
			LessonnoteManagement lsnval = lsn.get();			
			return lsnval;
		}
		return null;
	}
	
	public List<LessonnoteManagement> findByLessonnote(Long id) {		
		Optional<Lessonnote> lsn = lsnRepository.findById(id);
		if (lsn.isPresent()) {
			Lessonnote lsnval = lsn.get();
			List<LessonnoteManagement> lsnmanage = new ArrayList<LessonnoteManagement>();
			lsnmanage.add( lsnmanageRepository.findByLessonnote(lsnval).isPresent() ? lsnmanageRepository.findByLessonnote(lsnval).get() : null  );
			return lsnmanage;
		}
		return null;		
	}
	
	public LessonnoteManagement update(LessonnoteManagementRequest attRequest, long id) {
		Optional<Lessonnote> existing = lsnRepository.findById(id);
		if (existing.isPresent()) {
			Lessonnote lsnval = existing.get();
			Optional<LessonnoteManagement> lsnmanage = lsnmanageRepository.findByLessonnote(lsnval);
			if (!attRequest.getAction().equals("closure") && !attRequest.getAction().equals("closed") ) {
				if ( lsnval.getCycle_count() == 2) {
					lsnmanage.get().setManagement(100);
				}
				else if ( lsnval.getCycle_count() == 3 || lsnval.getCycle_count() == 4) {
					lsnmanage.get().setManagement(60);
				}
				else if ( lsnval.getCycle_count() == 5 || lsnval.getCycle_count() == 6) {
					lsnmanage.get().setManagement(50);
				}
				else if ( lsnval.getCycle_count() > 6) {
					lsnmanage.get().setManagement(30);
				}
				
				CommonActivity.copyNonNullProperties(attRequest, lsnmanage.get());
				return lsnmanageRepository.save(lsnmanage.get());
			}
			
		} 
		return null;
	}
	
	public Map<String, Object> getPaginatedTeacherLessonnotes(int page, int size, String query, Optional<Long> schgroupId, Optional<Long> schId, Optional<Integer> classId,  Optional<Integer> week, Optional<Long> calendarId, Optional<Long> teacherId, Optional<Long> subjectId, Optional<String> status,  Optional<Timestamp> datefrom, Optional<Timestamp> dateto ) {
		CommonActivity.validatePageNumberAndSize(page, size);
        
        Long schgroup = schgroupId.orElse(null);
        Long schowner = schId.orElse(null);
        Integer classowner = classId.orElse(null);
        Integer weeknow = week.orElse(null);
        String statusnow = status.orElse(null);
        Long subjectowner = subjectId.orElse(null);
        Long teacherowner = teacherId.orElse(null);
        Long calendarowner = calendarId.orElse(null);
        
        // Retrieve Lessonnotes
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<LessonnoteManagement> Lessonnotes = null;
        
        if ( query == null || query.equals("") ) {
        	if ( schgroup == null ) {
        		Lessonnotes = lsnmanageRepository.findAll(pageable);
        	}
        	else {
        		Optional<SchoolGroup> schgroupobj = groupRepository.findById( schgroup );
        		Optional<School> schownerobj = null;
        		
        		Optional<Teacher> teacherownerobj = null;
        		Optional<Calendar> calendarownerobj = null;
        		Optional<Subject> subjectownerobj = null;
        		
        		if(schowner != null) { schownerobj = schRepository.findById( schowner );  } 
        		if(teacherowner != null) { teacherownerobj = teaRepository.findById( teacherowner );  } 
        		if(calendarowner != null) { calendarownerobj = calRepository.findById( calendarowner );  } 
        		if(subjectowner != null) { subjectownerobj = subRepository.findById( subjectowner );  } 
        		
        		Lessonnotes = lsnmanageRepository.findByTeacherSchoolgroupPage(
        				schgroupobj == null ? null : schgroupobj.get(), 
                		schownerobj == null ? null : schownerobj.get(), 
                		classowner,
                		weeknow,
                		teacherownerobj == null ? null : teacherownerobj.get(),
                		subjectownerobj == null ? null : subjectownerobj.get(),
                		statusnow,
                		calendarownerobj == null ? null : calendarownerobj.get(),
                		datefrom.isEmpty() ? null : datefrom.get(),
                		dateto.isEmpty() ? null : dateto.get(),                				
        				pageable
        		);
        	}        	
        }
        else {
        	if ( schgroup == null ) {
        		Lessonnotes = lsnmanageRepository.filter("%"+ query + "%",  pageable);
        	}
        	else {    
        		Optional<SchoolGroup> schgroupobj = groupRepository.findById( schgroup );
        		Optional<School> schownerobj = null;
        		
        		Optional<Teacher> teacherownerobj = null;
        		Optional<Calendar> calendarownerobj = null;
        		Optional<Subject> subjectownerobj = null;
        		
        		if(schowner != null) { schownerobj = schRepository.findById( schowner );  } 
        		if(teacherowner != null) { teacherownerobj = teaRepository.findById( teacherowner );  } 
        		if(calendarowner != null) { calendarownerobj = calRepository.findById( calendarowner );  } 
        		if(subjectowner != null) { subjectownerobj = subRepository.findById( subjectowner );  } 
        		
        		Lessonnotes = lsnmanageRepository.findFilterByTeacherSchoolgroupPage( 
        				"%"+ query + "%",         				
        				schgroupobj == null ? null : schgroupobj.get(), 
                        schownerobj == null ? null : schownerobj.get() , 
                        classowner,
                        weeknow,
                        teacherownerobj == null ? null : teacherownerobj.get(),
                        subjectownerobj == null ? null : subjectownerobj.get(),
                        statusnow,
                        calendarownerobj == null ? null : calendarownerobj.get(),
                        datefrom.isEmpty() ? null : datefrom.get(),
                        dateto.isEmpty() ? null : dateto.get(), 
        				pageable
        		);
        	}
        }
        
        

        if(Lessonnotes.getNumberOfElements() == 0) {
        	Map<String, Object> responseEmpty = new HashMap<>();
        	responseEmpty.put("Lessonnotes", Collections.emptyList());
        	responseEmpty.put("currentPage", Lessonnotes.getNumber());
        	responseEmpty.put("totalItems", Lessonnotes.getTotalElements());
        	responseEmpty.put("totalPages", Lessonnotes.getTotalPages());
        	
        	return responseEmpty;
        }
        
        List<LessonnoteManagement> calarray = new ArrayList<LessonnoteManagement>();
        
        calarray = Lessonnotes.getContent();
        
        Map<String, Object> response = new HashMap<>();
        response.put("lessonnotemanagement", calarray);
     
        return response;
    }
	
	
	public Map<String, Object> getOrdinaryTeacherLessonnotes(String query, Optional<Long> schgroupId, Optional<Long> schId, Optional<Integer> classId,  Optional<Integer> week, Optional<String> year, Optional<Integer> term, Optional<Long> teacherId, Optional<Long> subjectId, Optional<Timestamp> datefrom, Optional<Timestamp> dateto  ) {
        
        Long schgroup = schgroupId.orElse(null);
        Long schowner = schId.orElse(null);
        Integer classowner = classId.orElse(null);
        Integer weeknow = week.orElse(null);
        Long subjectowner = subjectId.orElse(null);
        Long teacherowner = teacherId.orElse(null);     
        Integer termval = term.orElse(null);
        String yearval = year.orElse(null);
        
        List<LessonnoteManagement> lessonnotes = null;
        
        if ( query == null || query.equals("") ) {
        	if ( schgroup == null ) {
        		lessonnotes = lsnmanageRepository.findAll();
        	}
        	else {
        		Optional<SchoolGroup> schgroupobj = groupRepository.findById( schgroup );
        		Optional<School> schownerobj = null;
        		Optional<Teacher> teacherownerobj = null;
        		Optional<Calendar> calendarownerobj = null;
        		Optional<Subject> subjectownerobj = null;
        		
        		if(schowner != null) { schownerobj = schRepository.findById( schowner );  } 
        		if(teacherowner != null) { teacherownerobj = teaRepository.findById( teacherowner );  }        		
        		//if(calendarowner != null) { calendarownerobj = calRepository.findById( calendarowner );  } 
        		if(subjectowner != null) { subjectownerobj = subRepository.findById( subjectowner );  } 
        		
        		lessonnotes = lsnmanageRepository.findByTeacherSchoolgroup(    				
        				schgroupobj == null ? null : schgroupobj.get(), 
                		schownerobj == null ? null : schownerobj.get() , 
                		classowner == null ? null : classowner, 
                		weeknow,
                		teacherownerobj == null ? null : teacherownerobj.get(),
                		subjectownerobj == null ? null : subjectownerobj.get(),		
                		termval,
                        yearval,
                		datefrom.isEmpty() ? null : datefrom.get(),
                        dateto.isEmpty() ? null : dateto.get()
        		);
        	}        	
        }
        else {
        	if ( schgroup == null ) {
        		lessonnotes = lsnmanageRepository.filterAll("%"+ query + "%");
        	}
        	else {    
        		Optional<SchoolGroup> schgroupobj = groupRepository.findById( schgroup );
        		Optional<School> schownerobj = null;
        		Optional<Teacher> teacherownerobj = null;
        		Optional<Calendar> calendarownerobj = null;
        		Optional<Subject> subjectownerobj = null;
        		
        		if(schowner != null) { schownerobj = schRepository.findById( schowner );  } 
        		if(teacherowner != null) { teacherownerobj = teaRepository.findById( teacherowner );  } 
        		//if(calendarowner != null) { calendarownerobj = calRepository.findById( calendarowner );  } 
        		if(subjectowner != null) { subjectownerobj = subRepository.findById( subjectowner );  }         		
        		
        		lessonnotes = lsnmanageRepository.findFilterByTeacherSchoolgroup( 
        				"%"+ query + "%", 
        				schgroupobj == null ? null : schgroupobj.get(), 
                        schownerobj == null ? null : schownerobj.get() , 
                        classowner == null ? null : classowner, 
                        weeknow,
                        teacherownerobj == null ? null : teacherownerobj.get(),
                        subjectownerobj == null ? null : subjectownerobj.get(),			
                        termval,
                        yearval,
                        datefrom.isEmpty() ? null : datefrom.get(),
                        dateto.isEmpty() ? null : dateto.get()
        		);
        	}
        }

        if(lessonnotes.size() == 0) {
        	Map<String, Object> responseEmpty = new HashMap<>();
        	responseEmpty.put("lessonnotemanagement", Collections.emptyList());        	        	
        	return responseEmpty;
        }
        
        List<LessonnoteManagement> calarray = new ArrayList<LessonnoteManagement>(lessonnotes);
        
        Map<String, Object> response = new HashMap<>();
        response.put("lessonnotemanagement", calarray);
  
        return response;
    }
	
	
}
