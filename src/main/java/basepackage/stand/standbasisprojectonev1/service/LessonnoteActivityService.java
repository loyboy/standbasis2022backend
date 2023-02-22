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

import basepackage.stand.standbasisprojectonev1.model.Attendance;
import basepackage.stand.standbasisprojectonev1.model.AttendanceActivity;
import basepackage.stand.standbasisprojectonev1.model.Calendar;
import basepackage.stand.standbasisprojectonev1.model.Lessonnote;
import basepackage.stand.standbasisprojectonev1.model.LessonnoteActivity;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;
import basepackage.stand.standbasisprojectonev1.model.Subject;
import basepackage.stand.standbasisprojectonev1.model.Teacher;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.LessonnoteActivityRequest;
import basepackage.stand.standbasisprojectonev1.repository.CalendarRepository;
import basepackage.stand.standbasisprojectonev1.repository.LessonnoteActivityRepository;
import basepackage.stand.standbasisprojectonev1.repository.LessonnoteRepository;
import basepackage.stand.standbasisprojectonev1.repository.SchoolRepository;
import basepackage.stand.standbasisprojectonev1.repository.SchoolgroupRepository;
import basepackage.stand.standbasisprojectonev1.repository.SubjectRepository;
import basepackage.stand.standbasisprojectonev1.repository.TeacherRepository;
import basepackage.stand.standbasisprojectonev1.util.CommonActivity;

@Service
public class LessonnoteActivityService {

	@Autowired		
    private LessonnoteActivityRepository lsnactivityRepository;	
	
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
	
	@Autowired		
    private LessonnoteRepository lsnRepository;
	
	public List<LessonnoteActivity> findAll() {		
		return lsnactivityRepository.findAll();
	}
	
	public LessonnoteActivity saveOne(LessonnoteActivityRequest lsnmanageRequest, Lessonnote lsn) {
		 ModelMapper modelMapper = new ModelMapper();   
		 LessonnoteActivity val = modelMapper.map(lsnmanageRequest, LessonnoteActivity.class); 
		 val.setLsn_id(lsn);			
		 return lsnactivityRepository.save(val);
	}
	
	public LessonnoteActivity findLessonnoteActivity(Long id) {		
		Optional<LessonnoteActivity> lsn = lsnactivityRepository.findById(id);
		if (lsn.isPresent()) {
			LessonnoteActivity lsnval = lsn.get();			
			return lsnval;
		}
		return null;
	}
	
	public LessonnoteActivity findLessonnoteActivityByLessonnote(Long id) {		
		Optional<LessonnoteActivity> lsn = lsnactivityRepository.findByLessonnote(id);
		if (lsn.isPresent()) {
			LessonnoteActivity lsnval = lsn.get();			
			return lsnval;
		}
		return null;
	}
	
	public LessonnoteActivity update(LessonnoteActivityRequest lsnRequest, long id) {
		Optional<LessonnoteActivity> existing = lsnactivityRepository.findById(id);
		if (existing.isPresent()) {
			 LessonnoteActivity lsnval = existing.get();
			 
			 lsnRequest.setActual( ( CommonActivity.parseTimestamp( CommonActivity.todayDate() ) ) ) ;
			 if ( lsnRequest.getActual().compareTo( lsnval.getExpected() ) > 0) {
				 lsnRequest.setSlip(1);
			 }
			 else {
				 lsnRequest.setSlip(0);
			 } 
			 
			CommonActivity.copyNonNullProperties(lsnRequest, lsnval);
			return lsnactivityRepository.save(lsnval);
		} 
		return null;
	}
	
public Map<String, Object> getPaginatedTeacherLessonnotes(int page,int size, String query, Optional<Long> schgroupId, Optional<Long> schId, Optional<Integer> classId, Optional<Long> calendarId, Optional<Long> teacherId, Optional<Long> subjectId, Optional<String> status, Optional<Integer> slip, Optional<Long> lessonnote,  Optional<Timestamp> datefrom, Optional<Timestamp> dateto  ) {
        
        Long schgroup = schgroupId.orElse(null);
        Long schowner = schId.orElse(null);
        Integer classowner = classId.orElse(null);
        Long teacherowner = teacherId.orElse(null);     
        Long calendarowner = calendarId.orElse(null);
        Long subjectowner = subjectId.orElse(null);
        String statusowner = status.orElse(null);
        Integer slipowner = slip.orElse(null);
        Long lsnowner = lessonnote.orElse(null);
        
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<LessonnoteActivity> lessonnotes = null;
        
        if ( query.equals("") || query == null ) {
        	if ( schgroup == null ) {
        		lessonnotes = lsnactivityRepository.findAll(pageable);
        	}
        	else {
        		Optional<SchoolGroup> schgroupobj = groupRepository.findById( schgroup );
        		Optional<School> schownerobj = null;
        		Optional<Teacher> teacherownerobj = null;
        		Optional<Calendar> calendarownerobj = null;
        		Optional<Subject> subjectownerobj = null;
        		Optional<Lessonnote> lsnownerobj = null;
        		
        		if(schowner != null) { schownerobj = schRepository.findById( schowner );  } 
        		if(teacherowner != null) { teacherownerobj = teaRepository.findById( teacherowner );  }        		
        		if(calendarowner != null) { calendarownerobj = calRepository.findById( calendarowner );  }
        		if(subjectowner != null) { subjectownerobj = subRepository.findById( subjectowner );  }
        		if(lsnowner != null) { lsnownerobj = lsnRepository.findById( lsnowner );  }
        		
        		lessonnotes = lsnactivityRepository.findByTeacherSchoolgroupPage(        				
        				schgroupobj == null ? null : schgroupobj.get(), 
                        schownerobj == null ? null : schownerobj.get(), 
                        classowner,                                
                        teacherownerobj == null ? null : teacherownerobj.get(),
                        calendarownerobj == null ? null : calendarownerobj.get(),
                        subjectownerobj == null ? null : subjectownerobj.get(),
                        statusowner,
                        slipowner,
                        lsnownerobj == null ? null : lsnownerobj.get(),
                        datefrom.isEmpty() ? null : datefrom.get(),
                        dateto.isEmpty() ? null : dateto.get(),
                        pageable
        		);
        	}        	
        }
        else {
        	if ( schgroup == null ) {
        		lessonnotes = lsnactivityRepository.filter("%"+ query + "%", pageable);
        	}
        	else {    
        		Optional<SchoolGroup> schgroupobj = groupRepository.findById( schgroup );
        		Optional<School> schownerobj = null;
        		Optional<Teacher> teacherownerobj = null;
        		Optional<Calendar> calendarownerobj = null;
        		Optional<Subject> subjectownerobj = null;
        		Optional<Lessonnote> lsnownerobj = null;
        		
        		if(schowner != null) { schownerobj = schRepository.findById( schowner );  } 
        		if(teacherowner != null) { teacherownerobj = teaRepository.findById( teacherowner );  } 
        		if(calendarowner != null) { calendarownerobj = calRepository.findById( calendarowner );  } 
        		if(subjectowner != null) { subjectownerobj = subRepository.findById( subjectowner );  }
        		if(lsnowner != null) { lsnownerobj = lsnRepository.findById( lsnowner );  }
        		
        		lessonnotes = lsnactivityRepository.findFilterByTeacherSchoolgroupPage( 
        				"%"+ query + "%", 
        				schgroupobj == null ? null : schgroupobj.get(), 
                        schownerobj == null ? null : schownerobj.get(), 
                        classowner,                                
                        teacherownerobj == null ? null : teacherownerobj.get(),
                        calendarownerobj == null ? null : calendarownerobj.get(),
                        subjectownerobj == null ? null : subjectownerobj.get(),
                        statusowner,
                        slipowner,
                        lsnownerobj == null ? null : lsnownerobj.get(),		
                        datefrom.isEmpty() ? null : datefrom.get(),
                        dateto.isEmpty() ? null : dateto.get() ,
                        pageable		
        		);
        	}
        }
        
        if(lessonnotes.getNumberOfElements() == 0) {
        	Map<String, Object> responseEmpty = new HashMap<>();
        	responseEmpty.put("Lessonnoteactivity", Collections.emptyList());
        	responseEmpty.put("currentPage", lessonnotes.getNumber());
        	responseEmpty.put("totalItems", lessonnotes.getTotalElements());
        	responseEmpty.put("totalPages", lessonnotes.getTotalPages());        	
        	return responseEmpty;
        }
        
        List<LessonnoteActivity> calarray = new ArrayList<LessonnoteActivity>();
        calarray = lessonnotes.getContent();
        
        Map<String, Object> response = new HashMap<>();
        response.put("Lessonnoteactivity", calarray);
        
        response.put("currentPage", lessonnotes.getNumber());
        response.put("totalItems", lessonnotes.getTotalElements());
        response.put("totalPages", lessonnotes.getTotalPages());
        response.put("isLast", lessonnotes.isLast());
        
        long slipLessonnotes = calarray.stream().filter(sch -> sch.getSlip() == 1).count();       
        long notslipLessonnotes = calarray.stream().filter(sch -> sch.getSlip() == 0).count();
        
        response.put("totalSlip", slipLessonnotes);
        response.put("totalNotSlip", notslipLessonnotes);
  
        return response;
    }
	
	
	public Map<String, Object> getOrdinaryTeacherLessonnotes(String query, Optional<Long> schgroupId, Optional<Long> schId, Optional<Integer> classId, Optional<Long> calendarId, Optional<Long> teacherId, Optional<Timestamp> datefrom, Optional<Timestamp> dateto  ) {
        
        Long schgroup = schgroupId.orElse(null);
        Long schowner = schId.orElse(null);
        Integer classowner = classId.orElse(null);
        Long teacherowner = teacherId.orElse(null);     
        Long calendarowner = calendarId.orElse(null);
        
        List<LessonnoteActivity> lessonnotes = null;
        
        if ( query.equals("") || query == null ) {
        	if ( schgroup == null ) {
        		lessonnotes = lsnactivityRepository.findAll();
        	}
        	else {
        		Optional<SchoolGroup> schgroupobj = groupRepository.findById( schgroup );
        		Optional<School> schownerobj = null;
        		Optional<Teacher> teacherownerobj = null;
        		Optional<Calendar> calendarownerobj = null;
        		
        		if(schowner != null) { schownerobj = schRepository.findById( schowner );  } 
        		if(teacherowner != null) { teacherownerobj = teaRepository.findById( teacherowner );  }        		
        		if(calendarowner != null) { calendarownerobj = calRepository.findById( calendarowner );  } 
        		
        		lessonnotes = lsnactivityRepository.findByTeacherSchoolgroup(        				
        				schgroupobj == null ? null : schgroupobj.get(), 
                        schownerobj == null ? null : schownerobj.get(), 
                        classowner,                                
                        teacherownerobj == null ? null : teacherownerobj.get(),
                        calendarownerobj == null ? null : calendarownerobj.get(),
                        datefrom.isEmpty() ? null : datefrom.get(),
                        dateto.isEmpty() ? null : dateto.get() 
        		);
        	}        	
        }
        else {
        	if ( schgroup == null ) {
        		lessonnotes = lsnactivityRepository.filterAll("%"+ query + "%");
        		//lessonnotes = lsnmanageRepository.findAll();
        	}
        	else {    
        		Optional<SchoolGroup> schgroupobj = groupRepository.findById( schgroup );
        		Optional<School> schownerobj = null;
        		Optional<Teacher> teacherownerobj = null;
        		Optional<Calendar> calendarownerobj = null;
        		
        		if(schowner != null) { schownerobj = schRepository.findById( schowner );  } 
        		if(teacherowner != null) { teacherownerobj = teaRepository.findById( teacherowner );  } 
        		if(calendarowner != null) { calendarownerobj = calRepository.findById( calendarowner );  } 
        		
        		lessonnotes = lsnactivityRepository.findFilterByTeacherSchoolgroup( 
        				"%"+ query + "%", 
        				schgroupobj == null ? null : schgroupobj.get(), 
                        schownerobj == null ? null : schownerobj.get(), 
                        classowner,                                
                        teacherownerobj == null ? null : teacherownerobj.get(),
                        calendarownerobj == null ? null : calendarownerobj.get(),
                        datefrom.isEmpty() ? null : datefrom.get(),
                        dateto.isEmpty() ? null : dateto.get() 
        		);
        	}
        }

        if(lessonnotes.size() == 0) {
        	Map<String, Object> responseEmpty = new HashMap<>();
        	responseEmpty.put("lessonnotes", Collections.emptyList());
        	        	
        	return responseEmpty;
        }
        
        List<LessonnoteActivity> calarray = new ArrayList<LessonnoteActivity>(lessonnotes);
        
        Map<String, Object> response = new HashMap<>();
        response.put("Lessonnoteactivity", calarray);
  
        return response;
    }
	
	
	
}
