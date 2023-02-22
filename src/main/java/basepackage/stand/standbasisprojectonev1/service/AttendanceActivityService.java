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
import basepackage.stand.standbasisprojectonev1.model.ClassStream;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;
import basepackage.stand.standbasisprojectonev1.model.Subject;
import basepackage.stand.standbasisprojectonev1.model.Teacher;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.AttendanceActivityRequest;
import basepackage.stand.standbasisprojectonev1.repository.AttendanceActivityRepository;
import basepackage.stand.standbasisprojectonev1.repository.AttendanceRepository;
import basepackage.stand.standbasisprojectonev1.repository.CalendarRepository;
import basepackage.stand.standbasisprojectonev1.repository.ClassStreamRepository;
import basepackage.stand.standbasisprojectonev1.repository.SchoolRepository;
import basepackage.stand.standbasisprojectonev1.repository.SchoolgroupRepository;
import basepackage.stand.standbasisprojectonev1.repository.SubjectRepository;
import basepackage.stand.standbasisprojectonev1.repository.TeacherRepository;
import basepackage.stand.standbasisprojectonev1.util.CommonActivity;

@Service
public class AttendanceActivityService {

	@Autowired		
    private AttendanceActivityRepository attactivityRepository;	
	
	@Autowired		
    private AttendanceRepository attRepository;	
	
	@Autowired
    private SchoolgroupRepository groupRepository;
	
	@Autowired		
    private SchoolRepository schRepository;
	
	@Autowired		
    private ClassStreamRepository clsRepository;
	
	@Autowired		
    private TeacherRepository teaRepository;
	
	@Autowired		
    private CalendarRepository calRepository;
	
	@Autowired		
    private SubjectRepository subRepository;	
	
	public List<AttendanceActivity> findAll() {		
		return attactivityRepository.findAll();
	}
	
	public AttendanceActivity saveOne(AttendanceActivityRequest attactRequest, Attendance att) {
		 ModelMapper modelMapper = new ModelMapper();   
		 AttendanceActivity val = modelMapper.map(attactRequest, AttendanceActivity.class);		
		 val.setAtt_id(att);			
		 return attactivityRepository.save(val);			 
	}
	
	public AttendanceActivity findAttendanceActivity(Long id) {		
		Optional<AttendanceActivity> att = attactivityRepository.findById(id);
		if (att.isPresent()) {
			AttendanceActivity attval = att.get();			
			return attval;
		}
		return null;
	}
	
	//findAttendanceActivityByAttendance
	public AttendanceActivity findAttendanceActivityByAttendance(Long id) {		
		Optional<AttendanceActivity> att = attactivityRepository.findByAttendance(id);
		if (att.isPresent()) {
			AttendanceActivity attval = att.get();			
			return attval;
		}
		return null;
	}
	
	public AttendanceActivity update(AttendanceActivityRequest attRequest, long id) {
		Optional<AttendanceActivity> existing = attactivityRepository.findById(id);
		if (existing.isPresent()) {
			
			AttendanceActivity attval = existing.get();
			attRequest.setActual( CommonActivity.parseTimestamp( CommonActivity.todayDate() ) );
			 if ( attRequest.getActual().compareTo( attval.getExpected() ) > 0) {
				 attRequest.setSlip(1);
			 }
			 else {
				 attRequest.setSlip(0);
			 }
			 CommonActivity.copyNonNullProperties(attRequest, attval);
			return attactivityRepository.save(attval);
		} 
		return null;
	}
	
	public Map<String, Object> getPaginatedTeacherAttendances(int page, int size, String query, Optional<Long> schgroupId, Optional<Long> schId, Optional<Long> classId, Optional<Long> calendarId, Optional<Long> teacherId, Optional<Long> subjectId,  Optional<String> status, Optional<Integer> slip, Optional<Long> attendance,  Optional<Timestamp> datefrom, Optional<Timestamp> dateto ) {
		CommonActivity.validatePageNumberAndSize(page, size);
        
        Long schgroup = schgroupId.orElse(null);
        Long schowner = schId.orElse(null);
        Long classowner = classId.orElse(null);
        Long teacherowner = teacherId.orElse(null);
        Long calendarowner = calendarId.orElse(null);
        Long subjectowner = subjectId.orElse(null);
        String statusowner = status.orElse(null);
        Integer slipowner = slip.orElse(null);
        Long attowner = attendance.orElse(null);
        
        // Retrieve Attendances
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<AttendanceActivity> attendances = null;
        
        if ( query.equals("") || query == null ) {
        	if ( schgroup == null ) {
        		attendances = attactivityRepository.findAll(pageable);
        	}
        	
        	else {
        		Optional<SchoolGroup> schgroupobj = groupRepository.findById( schgroup );
        		Optional<School> schownerobj = null;
        		Optional<ClassStream> classownerobj = null ;
        		Optional<Teacher> teacherownerobj = null;
        		Optional<Calendar> calendarownerobj = null;
        		Optional<Subject> subjectownerobj = null;
        		Optional<Attendance> attownerobj = null;
        		
        		if(schowner != null) { schownerobj = schRepository.findById( schowner );  } 
        		if(teacherowner != null) { teacherownerobj = teaRepository.findById( teacherowner );  } 
        		if(classowner != null) { classownerobj = clsRepository.findById( classowner );  } 
        		if(calendarowner != null) { calendarownerobj = calRepository.findById( calendarowner );  } 
        		if(subjectowner != null) { subjectownerobj = subRepository.findById( subjectowner );  }
        		if(attowner != null) { attownerobj = attRepository.findById( attowner );  }
        		
        		attendances = attactivityRepository.findByTeacherSchoolgroupPage( 
        				schgroupobj == null ? null : schgroupobj.get(), 
                		schownerobj == null ? null : schownerobj.get() , 
                		classownerobj == null ? null : classownerobj.get(), 
                		teacherownerobj == null ? null : teacherownerobj.get(),
                		calendarownerobj == null ? null : calendarownerobj.get(),
                		subjectownerobj == null ? null : subjectownerobj.get(),
                		statusowner,
                		slipowner,
                		attownerobj == null ? null : attownerobj.get(),
                		datefrom.isEmpty() ? null : datefrom.get(),
                        dateto.isEmpty() ? null : dateto.get(),
        				pageable
        		);
        	}        	
        }
        else {
        	if ( schgroup == null ) {
        		attendances = attactivityRepository.filter("%"+ query + "%",  pageable);
        	}
        	else {    
        		Optional<SchoolGroup> schgroupobj = groupRepository.findById( schgroup );
        		Optional<School> schownerobj = null;
        		Optional<ClassStream> classownerobj = null;
        		Optional<Teacher> teacherownerobj = null;
        		Optional<Calendar> calendarownerobj = null;
        		Optional<Subject> subjectownerobj = null;
        		Optional<Attendance> attownerobj = null;
        		
        		if(schowner != null) { schownerobj = schRepository.findById( schowner );  } 
        		if(teacherowner != null) { teacherownerobj = teaRepository.findById( teacherowner );  } 
        		if(classowner != null) { classownerobj = clsRepository.findById( classowner );  }
        		if(calendarowner != null) { calendarownerobj = calRepository.findById( calendarowner );  } 
        		if(subjectowner != null) { subjectownerobj = subRepository.findById( subjectowner );  }
        		if(attowner != null) { attownerobj = attRepository.findById( attowner );  }
        		
        		attendances = attactivityRepository.findFilterByTeacherSchoolgroupPage( 
        				"%"+ query + "%", 
        				schgroupobj == null ? null : schgroupobj.get(), 
                        schownerobj == null ? null : schownerobj.get() , 
                        classownerobj == null ? null : classownerobj.get(), 
                        teacherownerobj == null ? null : teacherownerobj.get(),
                        calendarownerobj == null ? null : calendarownerobj.get(),
                        subjectownerobj == null ? null : subjectownerobj.get(),
                        statusowner,
                        slipowner,
                        attownerobj == null ? null : attownerobj.get(),
                        datefrom.isEmpty() ? null : datefrom.get(),
                        dateto.isEmpty() ? null : dateto.get(),
        				pageable
        		);
        	}
        }

        if(attendances.getNumberOfElements() == 0) {
        	Map<String, Object> responseEmpty = new HashMap<>();
        	responseEmpty.put("attendanceactivity", Collections.emptyList());
        	responseEmpty.put("currentPage", attendances.getNumber());
        	responseEmpty.put("totalItems", attendances.getTotalElements());
        	responseEmpty.put("totalPages", attendances.getTotalPages());        	
        	return responseEmpty;
        }
        
        List<AttendanceActivity> calarray = new ArrayList<AttendanceActivity>();
        
        calarray = attendances.getContent();
        
        Map<String, Object> response = new HashMap<>();
        response.put("attendanceactivity", calarray);
        response.put("currentPage", attendances.getNumber());
        response.put("totalItems", attendances.getTotalElements());
        response.put("totalPages", attendances.getTotalPages());
        response.put("isLast", attendances.isLast());
        
        long slipAttendances = calarray.stream().filter(sch -> sch.getSlip() == 1).count();       
        long notslipAttendances = calarray.stream().filter(sch -> sch.getSlip() == 0).count();
        
        response.put("totalSlip", slipAttendances);
        response.put("totalNotSlip", notslipAttendances);
       
        return response;
    }
	
	public Map<String, Object> getOrdinaryTeacherAttendances(String query, Optional<Long> schgroupId, Optional<Long> schId, Optional<Long> classId, Optional<Long> calendarId, Optional<Long> teacherId, Optional<Timestamp> datefrom, Optional<Timestamp> dateto  ) {
        
        Long schgroup = schgroupId.orElse(null);
        Long schowner = schId.orElse(null);
        Long classowner = classId.orElse(null);
        Long teacherowner = teacherId.orElse(null);     
        Long calendarowner = calendarId.orElse(null);
        
        List<AttendanceActivity> attendances = null;
        
        if ( query.equals("") || query == null ) {
        	if ( schgroup == null ) {
        		attendances = attactivityRepository.findAll();
        	}
        	else {
        		Optional<SchoolGroup> schgroupobj = groupRepository.findById( schgroup );
        		Optional<School> schownerobj = null;
        		Optional<ClassStream> classownerobj = null ;
        		Optional<Teacher> teacherownerobj = null;
        		Optional<Calendar> calendarownerobj = null;
        		
        		if(schowner != null) { schownerobj = schRepository.findById( schowner );  } 
        		if(teacherowner != null) { teacherownerobj = teaRepository.findById( teacherowner );  } 
        		if(classowner != null) { classownerobj = clsRepository.findById( classowner );  } 
        		if(calendarowner != null) { calendarownerobj = calRepository.findById( calendarowner );  } 
        		
        		attendances = attactivityRepository.findByTeacherSchoolgroup( 
        				schgroupobj == null ? null : schgroupobj.get(), 
                        schownerobj == null ? null : schownerobj.get() , 
                        classownerobj == null ? null : classownerobj.get(), 
                        teacherownerobj == null ? null : teacherownerobj.get(),
                        calendarownerobj == null ? null : calendarownerobj.get(),
                        datefrom.isEmpty() ? null : datefrom.get(),
                        dateto.isEmpty() ? null : dateto.get()
        		);
        	}        	
        }
        else {
        	if ( schgroup == null ) {
        		attendances = attactivityRepository.findAll();
        	}
        	else {    
        		Optional<SchoolGroup> schgroupobj = groupRepository.findById( schgroup );
        		Optional<School> schownerobj = null;
        		Optional<ClassStream> classownerobj = null;
        		Optional<Teacher> teacherownerobj = null;
        		Optional<Calendar> calendarownerobj = null;
        		
        		if(schowner != null) { schownerobj = schRepository.findById( schowner );  } 
        		if(teacherowner != null) { teacherownerobj = teaRepository.findById( teacherowner );  } 
        		if(classowner != null) { classownerobj = clsRepository.findById( classowner );  }
        		if(calendarowner != null) { calendarownerobj = calRepository.findById( calendarowner );  } 
        		
        		attendances = attactivityRepository.findFilterByTeacherSchoolgroup( 
        				"%"+ query + "%", 
        				schgroupobj == null ? null : schgroupobj.get(), 
                        schownerobj == null ? null : schownerobj.get() , 
                        classownerobj == null ? null : classownerobj.get(), 
                        teacherownerobj == null ? null : teacherownerobj.get(),
                        calendarownerobj == null ? null : calendarownerobj.get(),
                        datefrom.isEmpty() ? null : datefrom.get(),
                        dateto.isEmpty() ? null : dateto.get()
        		);
        	}
        }

        if(attendances.size() == 0) {
        	Map<String, Object> responseEmpty = new HashMap<>();
        	responseEmpty.put("attendanceactivity", Collections.emptyList());
        	        	
        	return responseEmpty;
        }
        
        List<AttendanceActivity> calarray = new ArrayList<AttendanceActivity>(attendances);
        
        Map<String, Object> response = new HashMap<>();
        response.put("attendanceactivity", calarray);        
        
        return response;
    }
	
	
	
	
	
}
