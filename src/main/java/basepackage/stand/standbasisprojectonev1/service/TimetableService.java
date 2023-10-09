package basepackage.stand.standbasisprojectonev1.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
import basepackage.stand.standbasisprojectonev1.model.TimeTable;
import basepackage.stand.standbasisprojectonev1.model.Calendar;
import basepackage.stand.standbasisprojectonev1.model.ClassStream;
import basepackage.stand.standbasisprojectonev1.model.Enrollment;
import basepackage.stand.standbasisprojectonev1.model.Rowcall;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;
import basepackage.stand.standbasisprojectonev1.model.Subject;
import basepackage.stand.standbasisprojectonev1.model.Teacher;
import basepackage.stand.standbasisprojectonev1.payload.TimetableRequestTwo;
import basepackage.stand.standbasisprojectonev1.repository.CalendarRepository;
import basepackage.stand.standbasisprojectonev1.repository.ClassStreamRepository;
import basepackage.stand.standbasisprojectonev1.repository.SchoolRepository;
import basepackage.stand.standbasisprojectonev1.repository.SchoolgroupRepository;
import basepackage.stand.standbasisprojectonev1.repository.SubjectRepository;
import basepackage.stand.standbasisprojectonev1.repository.TeacherRepository;
import basepackage.stand.standbasisprojectonev1.repository.TimetableRepository;
import basepackage.stand.standbasisprojectonev1.util.AppConstants;
import basepackage.stand.standbasisprojectonev1.util.CommonActivity;

@Service
public class TimetableService {

	@Autowired		
    private TimetableRepository timeRepository;
	
	@Autowired		
    private TeacherRepository teaRepository;
	
	@Autowired		
    private CalendarRepository calRepository;
	
	@Autowired		
    private ClassStreamRepository clsRepository;
	
	@Autowired		
    private SubjectRepository subRepository;
	
	@Autowired		
    private SchoolRepository schRepository;
	
	@Autowired
    private SchoolgroupRepository schgroupRepository;
	
	public List<TimeTable> findAllBySchool(Long id) {
		
		Optional<School> sch = schRepository.findById(id);
		if (sch.isPresent()) {
			School schval = sch.get();			
			return timeRepository.findBySchool(schval);
		}
		return null;
		
	}
	
	public List<TimeTable> findClassTaught(Long tea, Long cal) {
		
		Optional<Teacher> teaobj = teaRepository.findById(tea);
		Optional<Calendar> calobj = calRepository.findById(cal);
		if ( teaobj.isPresent() && calobj.isPresent()) {
			Teacher teaval = teaobj.get();
			Calendar calval = calobj.get();
			return timeRepository.findByClassTaught(teaval, calval);
		}
		return null;
		
	}
	
public List<TimeTable> findClassOffered(Long classstream, Long cal) {
		
		Optional<ClassStream> clsobj = clsRepository.findById(classstream);
		Optional<Calendar> calobj = calRepository.findById(cal);
		if ( clsobj.isPresent() && calobj.isPresent()) {
			ClassStream clsval = clsobj.get();
			Calendar calval = calobj.get();
			return timeRepository.findClassOffered(clsval, calval);
		}
		return null;
		
	}
	
	public List<TimeTable> findAll() {
		
		return timeRepository.findAll();
	}
	
	public TimeTable findTimeTable(long id) {
		
		Optional<TimeTable> enc = timeRepository.findById(id);
		if (enc.isPresent()) {
			TimeTable timeval = enc.get();
			
			return timeval;
		}
		return null;
	}
	
	public List<TimeTable> getTimetablesByCalendar(Long cal){
		Optional<Calendar> existing = calRepository.findById(cal);
		if (existing.isPresent()) {
			List<TimeTable> enc = timeRepository.findByCalendar(existing.get());
			
			return enc;
		}
		return null;		
	}
	
	public Map<String, Object> getPaginatedTimeTables(int page, int size, String query, Optional<Long> ownerval, Optional<Long> groupval, Optional<Long> teacherval) {
        CommonActivity.validatePageNumberAndSize(page, size);
        
        Long owner = ownerval.orElse(null);
        Long group = groupval.orElse(null);
        Long teacher = teacherval.orElse(null);
        
        // Retrieve TimeTables
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<TimeTable> schs = null;
        
        if ( query.equals("") || query == null ) {
        	if ( owner == null && teacher == null && group == null  ) {
        		schs = timeRepository.findAll(pageable);
        	}
        	else {
        		//System.out.println("TImetable teachei is here" +  teacher);
        		
        		Optional<School> schownerobj = null;
        		Optional<SchoolGroup> schgroupobj = null ;
        		Optional<Teacher> teacherownerobj = null;
        		
        		if(owner != null) { schownerobj = schRepository.findById( owner );  } 
        		if(teacher != null) { teacherownerobj = teaRepository.findById( teacher );  }
        		if(group != null) { schgroupobj = schgroupRepository.findById( group );  }
        		
        		//System.out.println("TImetable teachei is here 2" +  teacherownerobj.get() );
        		
        		schs = timeRepository.findBySchoolAndTeacherPage( 
        				schownerobj == null ? null : schownerobj.get(), 
        				schgroupobj == null ? null : schgroupobj.get(), 		
        				teacherownerobj == null ? null : teacherownerobj.get(), 
                		pageable
        		);
        	}        	
        }
        else {
        	if ( owner == null && teacher == null && group == null ) {
        		schs = timeRepository.filter("%"+ query + "%",  pageable);
        	}
        	else {    
        		Optional<School> schownerobj = null;
        		Optional<SchoolGroup> schgroupobj = null ;
        		Optional<Teacher> teacherownerobj = null;
        		
        		if(owner != null) { schownerobj = schRepository.findById( owner );  } 
        		if(teacher != null) { teacherownerobj = teaRepository.findById( teacher );  }
        		if(group != null) { schgroupobj = schgroupRepository.findById( group );  }
        		
        		schs = timeRepository.findFilterBySchool( 
        				"%"+ query + "%", 
        				schownerobj == null ? null : schownerobj.get(), 
        				schgroupobj == null ? null : schgroupobj.get(),
        				teacherownerobj == null ? null : teacherownerobj.get(), 
                        pageable
        		);
        	}
        }

        if(schs.getNumberOfElements() == 0) {
        	Map<String, Object> responseEmpty = new HashMap<>();
        	responseEmpty.put("timetables", Collections.emptyList());
        	responseEmpty.put("currentPage", schs.getNumber());
        	responseEmpty.put("totalItems", schs.getTotalElements());
        	responseEmpty.put("totalPages", schs.getTotalPages());
        	
        	return responseEmpty;
        }
        
        List<TimeTable> teaarray = new ArrayList<TimeTable>();
        
        teaarray = schs.getContent();
        
        Map<String, Object> response = new HashMap<>();
        response.put("timetables", teaarray);
        response.put("currentPage", schs.getNumber());
        response.put("totalItems", schs.getTotalElements());
        response.put("totalPages", schs.getTotalPages());
        response.put("isLast", schs.isLast());
        
       // long active = 1; long inactive = 0;
      /*  long sriTimeTables = schRepository.countBySri(active);
        long nonSriTimeTables = schRepository.countBySri(inactive);
        long inactiveTimeTables = schRepository.countByStatus(inactive);*/
        
        long activeTimeTables = teaarray.stream().filter(sch -> sch.getStatus() == 1).count();       
        long inactiveTimeTables = teaarray.stream().filter(sch -> sch.getStatus() == 0).count();
        
        response.put("totalActive", activeTimeTables);
        response.put("totalInactive", inactiveTimeTables);
        return response;
    }
	
	public Map<String, Object> getOrdinaryTimeTables(String query, Optional<Long> ownerval, Optional<Long> groupval) {
       // CommonActivity.validatePageNumberAndSize(page, size);
        
        Long owner = ownerval.orElse(null);
        Long group = groupval.orElse(null);
        
        List<TimeTable> timetables = null;
        
        if ( query.equals("") || query == null ) {
        	if ( group == null  ) {
        		timetables = timeRepository.findAll();
        	}
        	else {
        		//System.out.println("TImetable teachei is here" +  teacher);
        		
        		Optional<School> schownerobj = null;
        		Optional<SchoolGroup> schgroupobj = null ;
        		        		
        		if(owner != null) { schownerobj = schRepository.findById( owner );  } 
        		if(group != null) { schgroupobj = schgroupRepository.findById( group );  }
        		
        		//System.out.println("TImetable teachei is here 2" +  teacherownerobj.get() );
        		
        		timetables = timeRepository.findBySchoolAndGroupPage( 
        				schownerobj == null ? null : schownerobj.get(), 
        				schgroupobj == null ? null : schgroupobj.get()				
        		);
        	}        	
        }
        else {
        	if ( group == null ) {
        		timetables = timeRepository.filterAll("%"+ query + "%");
        	}
        	else {    
        		Optional<School> schownerobj = null;
        		Optional<SchoolGroup> schgroupobj = null ;
        		
        		if(owner != null) { schownerobj = schRepository.findById( owner );  } 
        		if(group != null) { schgroupobj = schgroupRepository.findById( group );  }
        		
        		timetables = timeRepository.findFilterBySchoolAndGroupPage( 
        				"%"+ query + "%", 
        				schownerobj == null ? null : schownerobj.get(), 
        				schgroupobj == null ? null : schgroupobj.get()
        		);
        	}
        }

        if(timetables.size() == 0) {
        	Map<String, Object> responseEmpty = new HashMap<>();
        	responseEmpty.put("timetables", Collections.emptyList());        	
        	
        	return responseEmpty;
        }
        
        List<TimeTable> timearray = new ArrayList<TimeTable>(timetables);
        
        Map<String, Object> response = new HashMap<>();
        response.put("timetables", timearray);
        
       
        
       // long active = 1; long inactive = 0;
      /*  long sriTimeTables = schRepository.countBySri(active);
        long nonSriTimeTables = schRepository.countBySri(inactive);
        long inactiveTimeTables = schRepository.countByStatus(inactive);*/
        
       
        return response;
    }
	
	public TimeTable update(TimetableRequestTwo timeRequest,long id) {
		Optional<TimeTable> existing = timeRepository.findById(id);
		if (existing.isPresent()) {		
			TimeTable timeval = existing.get();
			if (timeRequest.getTeacher() != null) {
				Optional<Teacher> existingTeacher = teaRepository.findById(timeRequest.getTeacher());
				timeval.setTeacher(existingTeacher.get());
			}
			if (timeRequest.getClass_stream()!= null) {
				Optional<ClassStream> existingClass = clsRepository.findById(timeRequest.getClass_stream());
				timeval.setClass_stream(existingClass.get());
			}
			
			if (timeRequest.getSubject() != null) {
				Optional<Subject> existingSubject = subRepository.findById(timeRequest.getSubject());
				timeval.setSubject(existingSubject.get());
			}
			
			if (timeRequest.getTime_of() != null) {				
				timeval.setTime_of( timeRequest.getTime_of() );
			}
			
			if (timeRequest.getDay_of() != null) {				
				timeval.setDay_of(timeRequest.getDay_of());
			}
			
			if (timeRequest.getStatus() != null) {
				timeval.setStatus(timeRequest.getStatus());
			}
			
			TimeTable savedTimeval = timeRepository.save(timeval);
			
			savedTimeval.setSub_name( savedTimeval.getSubject().getName() );
			savedTimeval.setTea_name( savedTimeval.getTeacher().getFname() + " " + savedTimeval.getTeacher().getLname() );
			savedTimeval.setClass_name( savedTimeval.getClass_stream().getTitle() );
			
			return timeRepository.save(savedTimeval); 
		}	   
		
		return null;
	}
	
	public TimeTable delete(Long id) {
		Optional<TimeTable> tea = timeRepository.findById(id);
		if (tea.isPresent()) {
			TimeTable timeval = tea.get();
			timeval.setStatus(-1);
			timeRepository.save(timeval);
			return timeval;
		}
		return null;
	}	
	
}
