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

import basepackage.stand.standbasisprojectonev1.model.Assessment;
import basepackage.stand.standbasisprojectonev1.model.Calendar;
import basepackage.stand.standbasisprojectonev1.model.ClassStream;
import basepackage.stand.standbasisprojectonev1.model.Enrollment;
import basepackage.stand.standbasisprojectonev1.model.Lessonnote;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;
import basepackage.stand.standbasisprojectonev1.model.Student;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.AssessmentRequest;
import basepackage.stand.standbasisprojectonev1.repository.AssessmentRepository;
import basepackage.stand.standbasisprojectonev1.repository.CalendarRepository;
import basepackage.stand.standbasisprojectonev1.repository.ClassStreamRepository;
import basepackage.stand.standbasisprojectonev1.repository.EnrollmentRepository;
import basepackage.stand.standbasisprojectonev1.repository.LessonnoteRepository;
import basepackage.stand.standbasisprojectonev1.repository.SchoolRepository;
import basepackage.stand.standbasisprojectonev1.repository.SchoolgroupRepository;
import basepackage.stand.standbasisprojectonev1.repository.StudentRepository;
import basepackage.stand.standbasisprojectonev1.util.CommonActivity;

@Service
public class AssessmentService {

	@Autowired		
    private AssessmentRepository assessRepository;	
	
	@Autowired
    private SchoolgroupRepository groupRepository;
	
	@Autowired		
    private SchoolRepository schRepository;
	
	@Autowired		
    private ClassStreamRepository clsRepository;
	
	@Autowired		
    private LessonnoteRepository lsnRepository;
	
	@Autowired		
    private CalendarRepository calRepository;
	
	@Autowired		
    private StudentRepository pupRepository;
	
	@Autowired		
    private EnrollmentRepository enrolRepository;
	
	public List<Assessment> findAll() {		
		return assessRepository.findAll();
	}
	
	public Assessment saveOne(AssessmentRequest assessRequest) {
		 ModelMapper modelMapper = new ModelMapper();   
		 Assessment val = modelMapper.map(assessRequest, Assessment.class);
		 Optional<Lessonnote> lsn = lsnRepository.findById( assessRequest.getLsn_id() );
		 Optional<Enrollment> enrol = enrolRepository.findById( assessRequest.getEnrol() );
		 
		 if ( lsn.isPresent() && enrol.isPresent() ) {
			 val.setLsn( lsn.get() );	
			 val.setEnroll( enrol.get() );
			 return assessRepository.save(val);
		 }
		 return null;		 
	}
	
	public Assessment findAssessment(Long id) {		
		Optional<Assessment> ass = assessRepository.findById(id);
		if (ass.isPresent()) {
			Assessment assval = ass.get();			
			return assval;
		}
		return null;
	}
	
	public Assessment update(AssessmentRequest assessRequest, long id) {
		Optional<Assessment> existing = assessRepository.findById(id);
		if (existing.isPresent()) {
			Assessment assval = existing.get();
			CommonActivity.copyNonNullProperties(assessRequest, assval);
			return assessRepository.save(assval);
		} 
		return null;
	}
	
	public Map<String, Object> getPaginatedStudentlessonnotes(int page, int size, String query, Optional<Long> schgroupId, Optional<Long> schId, Optional<Long> classId, Optional<Integer> week, Optional<Long> calendarId, Optional<Long> studentId, Optional<Timestamp> datefrom, Optional<Timestamp> dateto  ) {
        CommonActivity.validatePageNumberAndSize(page, size);
        
        Long schgroup = schgroupId.orElse(null);
        Long schowner = schId.orElse(null);
        Long classowner = classId.orElse(null);
        Integer weeknow = week.orElse(null);
        Long studentowner = studentId.orElse(null);
        Long calendarowner = calendarId.orElse(null);     
        
        // Retrieve lessonnotes
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Assessment> lessonnotes = null;
        
        if ( query == null || query.equals("") ) {
        	if ( schgroup == null ) {
        		lessonnotes = assessRepository.findAll(pageable);
        	}
        	else {
        		Optional<SchoolGroup> schgroupobj = groupRepository.findById( schgroup );
        		Optional<School> schownerobj = null;
        		Optional<ClassStream> classownerobj = null ;
        		Optional<Student> studentownerobj = null;
        		Optional<Calendar> calendarownerobj = null;
        		
        		if(schowner != null) { schownerobj = schRepository.findById( schowner );  } 
        		if(studentowner != null) { studentownerobj = pupRepository.findById( studentowner );  } 
        		if(classowner != null) { classownerobj = clsRepository.findById( classowner );  }
        		if(calendarowner != null) { calendarownerobj = calRepository.findById( calendarowner );  }
        		
        		lessonnotes = assessRepository.findByStudentSchoolgroupPage(        				
        				
        				schgroupobj == null ? null : schgroupobj.get(), 
                        schownerobj == null ? null : schownerobj.get() , 
                        classownerobj == null ? null : classownerobj.get(),
                        weeknow,
                        studentownerobj == null ? null : studentownerobj.get(),
                        calendarownerobj == null ? null : calendarownerobj.get(),
                        datefrom.isEmpty() ? null : datefrom.get(),
                        dateto.isEmpty() ? null : dateto.get(),
        				pageable
        		);
        	}        	
        }
        else {
        	if ( schgroup == null ) {
        		lessonnotes = assessRepository.filter("%"+ query + "%",  pageable);
        	}
        	else {    
        		Optional<SchoolGroup> schgroupobj = groupRepository.findById( schgroup );
        		Optional<School> schownerobj = null;
        		Optional<ClassStream> classownerobj = null;
        		Optional<Student> studentownerobj = null;
        		Optional<Calendar> calendarownerobj = null;
        		
        		if(schowner != null) { schownerobj = schRepository.findById( schowner );  } 
        		if(studentowner != null) { studentownerobj = pupRepository.findById( studentowner );  } 
        		if(classowner != null) { classownerobj = clsRepository.findById( classowner );  }
        		if(calendarowner != null) { calendarownerobj = calRepository.findById( calendarowner );  }
        		
        		lessonnotes = assessRepository.findFilterByStudentSchoolgroupPage( 
        				"%"+ query + "%", 
        				schgroupobj == null ? null : schgroupobj.get(), 
                        schownerobj == null ? null : schownerobj.get() , 
                        classownerobj == null ? null : classownerobj.get(),
                        weeknow,
                        studentownerobj == null ? null : studentownerobj.get(),
                        calendarownerobj == null ? null : calendarownerobj.get(),
                        datefrom.isEmpty() ? null : datefrom.get(),
                        dateto.isEmpty() ? null : dateto.get(),
        				pageable
        		);
        	}
        }

        if(lessonnotes.getNumberOfElements() == 0) {
        	Map<String, Object> responseEmpty = new HashMap<>();
        	responseEmpty.put("assessments", Collections.emptyList());
        	responseEmpty.put("currentPage", lessonnotes.getNumber());
        	responseEmpty.put("totalItems", lessonnotes.getTotalElements());
        	responseEmpty.put("totalPages", lessonnotes.getTotalPages());
        	
        	return responseEmpty;
        }
        
        List<Assessment> calarray = new ArrayList<Assessment>();
        
        calarray = lessonnotes.getContent();
        
        Map<String, Object> response = new HashMap<>();
        response.put("assessments", calarray);
        response.put("currentPage", lessonnotes.getNumber());
        response.put("totalItems", lessonnotes.getTotalElements());
        response.put("totalPages", lessonnotes.getTotalPages());
        response.put("isLast", lessonnotes.isLast());
        
        return response;
    }
	
	public Map<String, Object> getOrdinaryStudentlessonnotes(String query, Optional<Long> schgroupId, Optional<Long> schId, Optional<Long> classId, Optional<Integer> week, Optional<String> year, Optional<Integer> term, Optional<Long> calendarId, Optional<Long> studentId, Optional<Timestamp> datefrom, Optional<Timestamp> dateto  ) {
                
        Long schgroup = schgroupId.orElse(null);
        Long schowner = schId.orElse(null);
        Long classowner = classId.orElse(null);
        Integer weeknow = week.orElse(null);
        Long studentowner = studentId.orElse(null);
        Long calendarowner = calendarId.orElse(null); 
        Integer termval = term.orElse(null);
        String yearval = year.orElse(null);
        
        List<Assessment> lessonnotes = null;
        
        if ( query == null || query.equals("") ) {
        	if ( schgroup == null ) {
        		lessonnotes = assessRepository.findAll();
        	}
        	else {
        		Optional<SchoolGroup> schgroupobj = groupRepository.findById( schgroup );
        		Optional<School> schownerobj = null;
        		Optional<ClassStream> classownerobj = null ;
        		Optional<Student> studentownerobj = null;
        		Optional<Calendar> calendarownerobj = null;
        		
        		if(schowner != null) { schownerobj = schRepository.findById( schowner );  } 
        		if(studentowner != null) { studentownerobj = pupRepository.findById( studentowner );  } 
        		if(classowner != null) { classownerobj = clsRepository.findById( classowner );  } 
        		if(calendarowner != null) { calendarownerobj = calRepository.findById( calendarowner );  } 
        		
        		lessonnotes = assessRepository.findByStudentSchoolgroup( 
        				schgroupobj == null ? null : schgroupobj.get(), 
                        schownerobj == null ? null : schownerobj.get() , 
                        classownerobj == null ? null : classownerobj.get(),
                        weeknow,
                        studentownerobj == null ? null : studentownerobj.get(),
                        calendarownerobj == null ? null : calendarownerobj.get(),
                        termval,
                        yearval,
                        datefrom.isEmpty() ? null : datefrom.get(),
                        dateto.isEmpty() ? null : dateto.get()
        		);
        	}        	
        }
        else {
        	if ( schgroup == null ) {
        		lessonnotes = assessRepository.filterAll("%"+ query + "%");
        	}
        	else {    
        		Optional<SchoolGroup> schgroupobj = groupRepository.findById( schgroup );
        		Optional<School> schownerobj = null;
        		Optional<ClassStream> classownerobj = null;
        		Optional<Student> studentownerobj = null;
        		Optional<Calendar> calendarownerobj = null;
        		
        		if(schowner != null) { schownerobj = schRepository.findById( schowner );  } 
        		if(studentowner != null) { studentownerobj = pupRepository.findById( studentowner );  } 
        		if(classowner != null) { classownerobj = clsRepository.findById( classowner );  }
        		if(calendarowner != null) { calendarownerobj = calRepository.findById( calendarowner );  } 
        		
        		lessonnotes = assessRepository.findFilterByStudentSchoolgroup( 
        				"%"+ query + "%", 
        				schgroupobj == null ? null : schgroupobj.get(), 
                        schownerobj == null ? null : schownerobj.get() , 
                        classownerobj == null ? null : classownerobj.get(),
                        weeknow,
                        studentownerobj == null ? null : studentownerobj.get(),
                        calendarownerobj == null ? null : calendarownerobj.get(),
                        termval,
                        yearval,
                        datefrom.isEmpty() ? null : datefrom.get(),
                        dateto.isEmpty() ? null : dateto.get()
        		);
        	}
        }

        if(lessonnotes.size() == 0) {
        	Map<String, Object> responseEmpty = new HashMap<>();
        	responseEmpty.put("assessments", Collections.emptyList());
        	        	
        	return responseEmpty;
        }
        
        List<Assessment> calarray = new ArrayList<Assessment>(lessonnotes);
        
        Map<String, Object> response = new HashMap<>();
        response.put("assessments", calarray);
  
        return response;
    }
	

	
}
