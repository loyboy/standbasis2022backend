package basepackage.stand.standbasisprojectonev1.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import basepackage.stand.standbasisprojectonev1.model.Attendance;
import basepackage.stand.standbasisprojectonev1.model.Calendar;
import basepackage.stand.standbasisprojectonev1.model.ClassStream;
import basepackage.stand.standbasisprojectonev1.model.Rowcall;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;
import basepackage.stand.standbasisprojectonev1.model.Student;
import basepackage.stand.standbasisprojectonev1.model.Subject;
import basepackage.stand.standbasisprojectonev1.model.Teacher;
import basepackage.stand.standbasisprojectonev1.model.TimeTable;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.AttendanceRequest;
import basepackage.stand.standbasisprojectonev1.repository.AttendanceActivityRepository;
import basepackage.stand.standbasisprojectonev1.repository.AttendanceManagementRepository;
import basepackage.stand.standbasisprojectonev1.repository.AttendanceRepository;
import basepackage.stand.standbasisprojectonev1.repository.CalendarRepository;
import basepackage.stand.standbasisprojectonev1.repository.ClassStreamRepository;
import basepackage.stand.standbasisprojectonev1.repository.RowcallRepository;
import basepackage.stand.standbasisprojectonev1.repository.SchoolRepository;
import basepackage.stand.standbasisprojectonev1.repository.SchoolgroupRepository;
import basepackage.stand.standbasisprojectonev1.repository.StudentRepository;
import basepackage.stand.standbasisprojectonev1.repository.SubjectRepository;
import basepackage.stand.standbasisprojectonev1.repository.TeacherRepository;
import basepackage.stand.standbasisprojectonev1.repository.TimetableRepository;
import basepackage.stand.standbasisprojectonev1.util.CommonActivity;

@Service
public class AttendanceService {
	
	private static final Logger logger = LoggerFactory.getLogger(OnboardingService.class);
	
	@Autowired
    private SchoolgroupRepository groupRepository;
	
	@Autowired		
    private SchoolRepository schRepository;
	
	@Autowired		
    private ClassStreamRepository clsRepository;
	
	@Autowired		
    private TeacherRepository teaRepository;
	
	@Autowired		
    private StudentRepository pupRepository;
	
	@Autowired		
    private AttendanceRepository attRepository;	
	
	@Autowired		
    private AttendanceManagementRepository attmanagementRepository;	 
	
	@Autowired		
    private AttendanceActivityRepository attactivityRepository;	
	
	@Autowired		
    private TimetableRepository timetableRepository;
	
	@Autowired		
    private CalendarRepository calRepository;

	@Autowired		
    private RowcallRepository rowRepository;
	
	@Autowired		
    private SubjectRepository subRepository;
	
	public List<Attendance> findAllByTimetable(Long id) {
		
		Optional<TimeTable> time = timetableRepository.findById(id);
		if (time.isPresent()) {
			TimeTable timeval = time.get();			
			return attRepository.findByTimetable(timeval);
		}
		return null;		
	}
	
	public Rowcall findAllByRowcall(Long id) {		
		Optional<Attendance> att = attRepository.findById(id);
		if (att.isPresent()) {
			Attendance attval = att.get();			
			return attRepository.findByRowcall(attval);
		}
		return null;		
	}
	
	public Rowcall findRowcall(Long id) {		
		Optional<Rowcall> rc = rowRepository.findById(id);
		if (rc.isPresent()) {
			Rowcall rcval = rc.get();			
			return rcval;
		}
		return null;
	}
	
	public List<Attendance> findAll() {		
		return attRepository.findAll();
	}
	
	public List<Rowcall> saveRowCall( List<Rowcall> rc  ) {		
		return rowRepository.saveAll(rc);
	}
	
	public Attendance saveOne(AttendanceRequest attRequest) {
		 ModelMapper modelMapper = new ModelMapper();   
		 Attendance att = modelMapper.map(attRequest, Attendance.class);
		 
		 Optional<Teacher> t = teaRepository.findById( attRequest.getTea_id() );
		 Optional<TimeTable> tr = timetableRepository.findById( attRequest.getTimetable_id() );
		 Optional<Calendar> c = calRepository.findById( attRequest.getCalendar_id() );
		 
		 if ( t.isPresent() && tr.isPresent() && c.isPresent() ) {
			 att.setTeacher(t.get());
			 att.setTimetable(tr.get());
			 att.setCalendar(c.get());
			 return attRepository.save(att);
		 }
		 return null;
	}
	
	public String updatePhoto(Long id, String fileName) {
		Optional<Attendance> existing = attRepository.findById(id);
		if (existing.isPresent()) {
			Attendance attval = existing.get();		
			attval.setImage(fileName);
			Attendance filledLessonnote = attRepository.save(attval);
			return filledLessonnote.getImage();
		}  
		return null;
	}
	
	public Attendance findAttendance(Long id) {
		
		Optional<Attendance> att = attRepository.findById(id);
		if (att.isPresent()) {
			Attendance attval = att.get();			
			return attval;
		}
		return null;
	}
	
	public Map<String, Object> getPaginatedTeacherAttendances(int page, int size, String query, Optional<Long> schgroupId, Optional<Long> schId, Optional<Long> classId, Optional<Long> calendarId, Optional<Long> teacherId, Optional<Long> subject, Optional<Integer> status, Date datefrom, Date dateto ) {
		CommonActivity.validatePageNumberAndSize(page, size);
        
        Long schgroup = schgroupId.orElse(null);
        Long schowner = schId.orElse(null);
        Long classowner = classId.orElse(null);
        Long teacherowner = teacherId.orElse(null);
        Long calendarowner = calendarId.orElse(null);
        Integer statusnow = status.orElse(null);
        Long subjectowner = subject.orElse(null);
        
        // Retrieve Attendances
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Attendance> attendances = null;
        
        if ( query == null || query.equals("") ) {
        	if ( schgroup == null ) {
        		attendances = attRepository.findAll(pageable);
        	}
        	else {
        		Optional<SchoolGroup> schgroupobj = groupRepository.findById( schgroup );
        		Optional<School> schownerobj = null;
        		Optional<ClassStream> classownerobj = null ;
        		Optional<Teacher> teacherownerobj = null;
        		Optional<Calendar> calendarownerobj = null;
        		Optional<Subject> subjectownerobj = null;
        		
        		if(schowner != null) { schownerobj = schRepository.findById( schowner );  } 
        		if(teacherowner != null) { teacherownerobj = teaRepository.findById( teacherowner );  } 
        		if(classowner != null) { classownerobj = clsRepository.findById( classowner );  } 
        		if(calendarowner != null) { calendarownerobj = calRepository.findById( calendarowner );  } 
        		if(subjectowner != null) { subjectownerobj = subRepository.findById( subjectowner );  }
        		
        		attendances = attRepository.findByTeacherSchoolgroupPage( 
        				schgroupobj == null ? null : schgroupobj.get(), 
        				schownerobj == null ? null : schownerobj.get(), 
        				classownerobj == null ? null : classownerobj.get(), 
        				teacherownerobj == null ? null : teacherownerobj.get(),
        				calendarownerobj == null ? null : calendarownerobj.get(),
        				subjectownerobj == null ? null : subjectownerobj.get(),
        				statusnow,			
        				datefrom == null ? null : datefrom,
        				dateto == null ? null : dateto,
        				pageable
        		);
        	}        	
        }
        else {
        	if ( schgroup == null ) {
        		attendances = attRepository.filter("%"+ query + "%",  pageable);
        	}
        	else {    
        		Optional<SchoolGroup> schgroupobj = groupRepository.findById( schgroup );
        		Optional<School> schownerobj = null;
        		Optional<ClassStream> classownerobj = null;
        		Optional<Teacher> teacherownerobj = null;
        		Optional<Calendar> calendarownerobj = null;
        		Optional<Subject> subjectownerobj = null;
        		
        		if(schowner != null) { schownerobj = schRepository.findById( schowner );  } 
        		if(teacherowner != null) { teacherownerobj = teaRepository.findById( teacherowner );  } 
        		if(classowner != null) { classownerobj = clsRepository.findById( classowner );  }
        		if(calendarowner != null) { calendarownerobj = calRepository.findById( calendarowner );  }
        		if(subjectowner != null) { subjectownerobj = subRepository.findById( subjectowner );  }
        		
        		attendances = attRepository.findFilterByTeacherSchoolgroupPage( 
        						"%"+ query + "%", 
        						schgroupobj == null ? null : schgroupobj.get(), 
                				schownerobj == null ? null : schownerobj.get() , 
                				classownerobj == null ? null : classownerobj.get(), 
                				teacherownerobj == null ? null : teacherownerobj.get(),
                				calendarownerobj == null ? null : calendarownerobj.get(),
                				subjectownerobj == null ? null : subjectownerobj.get(),
                		        statusnow,
                				datefrom == null ? null : datefrom,
                		        dateto == null ? null : dateto,
                				pageable
        		);
        	}
        }

        if(attendances.getNumberOfElements() == 0) {
        	Map<String, Object> responseEmpty = new HashMap<>();
        	responseEmpty.put("attendances", Collections.emptyList());
        	responseEmpty.put("currentPage", attendances.getNumber());
        	responseEmpty.put("totalItems", attendances.getTotalElements());
        	responseEmpty.put("totalPages", attendances.getTotalPages());
        	
        	return responseEmpty;
        }
        
        List<Attendance> calarray = new ArrayList<Attendance>();
        
        calarray = attendances.getContent();       
  
      //  List<Attendance> donelist = calarray.stream().filter(sch -> sch.getDone() == 1).collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("attendances", calarray);
        response.put("currentPage", attendances.getNumber());
        response.put("totalItems", attendances.getTotalElements());
        response.put("totalPages", attendances.getTotalPages());
        response.put("isLast", attendances.isLast());
        
       // long active = 1; long inactive = 0;
      /*  long sriAttendances = schRepository.countBySri(active);
        long nonSriAttendances = schRepository.countBySri(inactive);
        long inactiveAttendances = schRepository.countByStatus(inactive);*/
        
        long doneAttendances = calarray.stream().filter(sch -> sch.getDone() == 1).count();       
        long notdoneAttendances = calarray.stream().filter(sch -> sch.getDone() == 0).count();
        //collect(Collectors.toList());
        
        response.put("totalDone", doneAttendances);
        response.put("totalNotDone", notdoneAttendances);
        return response;
    }
	
	public Map<String, Object> getOrdinaryTeacherAttendances(String query, Optional<Long> schgroupId, Optional<Long> schId, Optional<Long> classId, Optional<Long> calendarId, Optional<Long> teacherId, Optional<Long> subject, Optional<Timestamp> datefrom, Optional<Timestamp> dateto  ) {
        
        Long schgroup = schgroupId.orElse(null);
        Long schowner = schId.orElse(null);
        Long classowner = classId.orElse(null);
        Long teacherowner = teacherId.orElse(null);     
        Long calendarowner = calendarId.orElse(null);
        Long subjectowner = subject.orElse(null);
        
        List<Attendance> attendances = null;
        
        if (  query == null || query.equals("") ) {
        	if ( schgroup == null ) {
        		attendances = attRepository.findAll();
        	}
        	else {
        		Optional<SchoolGroup> schgroupobj = groupRepository.findById( schgroup );
        		Optional<School> schownerobj = null;
        		Optional<ClassStream> classownerobj = null ;
        		Optional<Teacher> teacherownerobj = null;
        		Optional<Calendar> calendarownerobj = null;
        		Optional<Subject> subjectownerobj = null;
        		
        		if(schowner != null) { schownerobj = schRepository.findById( schowner );  } 
        		if(teacherowner != null) { teacherownerobj = teaRepository.findById( teacherowner );  } 
        		if(classowner != null) { classownerobj = clsRepository.findById( classowner );  } 
        		if(calendarowner != null) { calendarownerobj = calRepository.findById( calendarowner );  } 
        		if(subjectowner != null) { subjectownerobj = subRepository.findById( subjectowner );  }
        		
        		attendances = attRepository.findByTeacherSchoolgroup( 
        						schgroupobj == null ? null : schgroupobj.get(), 
                				schownerobj == null ? null : schownerobj.get() , 
                				classownerobj == null ? null : classownerobj.get(), 
                				teacherownerobj == null ? null : teacherownerobj.get(),
                				calendarownerobj == null ? null : calendarownerobj.get(),
                				subjectownerobj == null ? null : subjectownerobj.get(),
                				datefrom.isEmpty() ? null : datefrom.get(),
                		        dateto.isEmpty() ? null : dateto.get()
        		);
        	}        	
        }
        else {
        	if ( schgroup == null ) {
        		attendances = attRepository.filterAll("%"+ query + "%");
        	}
        	else {    
        		Optional<SchoolGroup> schgroupobj = groupRepository.findById( schgroup );
        		Optional<School> schownerobj = null;
        		Optional<ClassStream> classownerobj = null;
        		Optional<Teacher> teacherownerobj = null;
        		Optional<Calendar> calendarownerobj = null;
        		Optional<Subject> subjectownerobj = null;
        		
        		if(schowner != null) { schownerobj = schRepository.findById( schowner );  } 
        		if(teacherowner != null) { teacherownerobj = teaRepository.findById( teacherowner );  } 
        		if(classowner != null) { classownerobj = clsRepository.findById( classowner );  }
        		if(calendarowner != null) { calendarownerobj = calRepository.findById( calendarowner );  } 
        		if(subjectowner != null) { subjectownerobj = subRepository.findById( subjectowner );  }
        		
        		attendances = attRepository.findFilterByTeacherSchoolgroup( 
        				"%"+ query + "%", 
        						schgroupobj == null ? null : schgroupobj.get(), 
                				schownerobj == null ? null : schownerobj.get() , 
                				classownerobj == null ? null : classownerobj.get(), 
                				teacherownerobj == null ? null : teacherownerobj.get(),
                				calendarownerobj == null ? null : calendarownerobj.get(),
                				subjectownerobj == null ? null : subjectownerobj.get(),		
                				datefrom.isEmpty() ? null : datefrom.get(),
                		        dateto.isEmpty() ? null : dateto.get()
        		);
        	}
        }

        if(attendances.size() == 0) {
        	Map<String, Object> responseEmpty = new HashMap<>();
        	responseEmpty.put("attendances", Collections.emptyList());
        	        	
        	return responseEmpty;
        }
        
        List<Attendance> calarray = new ArrayList<Attendance>(attendances);
        
        Map<String, Object> response = new HashMap<>();
        response.put("attendances", calarray);
  
        
       // long active = 1; long inactive = 0;
      /*  long sriAttendances = schRepository.countBySri(active);
        long nonSriAttendances = schRepository.countBySri(inactive);
        long inactiveAttendances = schRepository.countByStatus(inactive);*/
        
        long doneAttendances = calarray.stream().filter(att -> att.getDone() == 1).count();       
        long notdoneAttendances = calarray.stream().filter(att -> att.getDone() == 0).count();
        
        response.put("totalDone", doneAttendances);
        response.put("totalNotDone", notdoneAttendances);
        return response;
    }
	
	//For mobile app 
	public Map<String, Object> getTeacherClassesToday( Optional<Long> teacherId, Date today ){
		
			Long teacherowner = teacherId.orElse(null); 
			Optional<Teacher> teacherownerobj = null;
			List<Attendance> attendances = null;
			 
			if( teacherowner != null ) { 
				teacherownerobj = teaRepository.findById( teacherowner );  
			}
		
			attendances = attRepository.findByTeacherTodayClass( 				
				teacherownerobj == null ? null : teacherownerobj.get(),				
				today.equals(null) ? null : today
			);
			
			//System.out.println(teacherownerobj.get().getEmail() + " --- " + today.getDay());
			
		 	List<Attendance> calarray = new ArrayList<Attendance>(attendances);
	        
	        Map<String, Object> response = new HashMap<>();//
	        response.put("attendances", calarray);
	        response.put("amount", calarray.size());
	        return response;
	}
	
	public Map<String, Object> getPaginatedStudentAttendances(int page, int size, String query, Optional<Long> schgroupId, Optional<Long> schId, Optional<Long> classId, Optional<Long> calendarId, Optional<Long> teacherId, Optional<Long> subjectId,  Optional<Integer> status, Optional<Long> studentId,  Optional<Long> attId,  Optional<Timestamp> datefrom, Optional<Timestamp> dateto  ) {
		CommonActivity.validatePageNumberAndSize(page, size);
        
        Long schgroup = schgroupId.orElse(null);
        Long schowner = schId.orElse(null);
        Long classowner = classId.orElse(null);
        Long studentowner = studentId.orElse(null);
        Long calendarowner = calendarId.orElse(null);
        Long teacherowner = teacherId.orElse(null);        
        Long subjectowner = subjectId.orElse(null);
        Integer statusowner = status.orElse(null);
        Long attendanceowner = attId.orElse(null);
        
        // Retrieve Attendances
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Rowcall> attendances = null;
        
        if (  query == null || query.equals("") ) {
        	if ( schgroup == null ) {
        		attendances = rowRepository.findAll(pageable);
        	}
        	else {
        		Optional<SchoolGroup> schgroupobj = groupRepository.findById( schgroup );
        		Optional<School> schownerobj = null;
        		Optional<ClassStream> classownerobj = null ;
        		Optional<Student> studentownerobj = null;
        		Optional<Calendar> calendarownerobj = null;
        		Optional<Teacher> teacherownerobj = null;
        		Optional<Subject> subjectownerobj = null;
        		Optional<Attendance> attownerobj = null;
        		
        		if(schowner != null) { schownerobj = schRepository.findById( schowner );  } 
        		if(studentowner != null) { studentownerobj = pupRepository.findById( studentowner );  } 
        		if(classowner != null) { classownerobj = clsRepository.findById( classowner );  }
        		if(calendarowner != null) { calendarownerobj = calRepository.findById( calendarowner );  }
        		if(teacherowner != null) { teacherownerobj = teaRepository.findById( teacherowner );  }
        		if(subjectowner != null) { subjectownerobj = subRepository.findById( subjectowner );  }
        		if(attendanceowner != null) { attownerobj = attRepository.findById( attendanceowner );  }
        		
        		attendances = attRepository.findByStudentSchoolgroupPage(         				
        						schgroupobj == null ? null : schgroupobj.get(), 
                				schownerobj == null ? null : schownerobj.get() , 
                				classownerobj == null ? null : classownerobj.get(), 
                				studentownerobj == null ? null : studentownerobj.get(),
                				calendarownerobj == null ? null : calendarownerobj.get(),
                				teacherownerobj == null ? null : teacherownerobj.get(),
                				subjectownerobj == null ? null : subjectownerobj.get(),
                				attownerobj == null ? null : attownerobj.get(),
                				statusowner,
                				datefrom.isEmpty() ? null : datefrom.get(),
                		        dateto.isEmpty() ? null : dateto.get(),
                		        pageable
        		);
        	}        	
        }
        else {
        	if ( schgroup == null ) {
        		attendances = rowRepository.filter("%"+ query + "%",  pageable);
        	}
        	else {    
        		Optional<SchoolGroup> schgroupobj = groupRepository.findById( schgroup );
        		Optional<School> schownerobj = null;
        		Optional<ClassStream> classownerobj = null;
        		Optional<Student> studentownerobj = null;
        		Optional<Calendar> calendarownerobj = null;
        		Optional<Teacher> teacherownerobj = null;
        		Optional<Subject> subjectownerobj = null;
        		Optional<Attendance> attownerobj = null;
        		
        		if(schowner != null) { schownerobj = schRepository.findById( schowner );  } 
        		if(studentowner != null) { studentownerobj = pupRepository.findById( studentowner );  } 
        		if(classowner != null) { classownerobj = clsRepository.findById( classowner );  }
        		if(calendarowner != null) { calendarownerobj = calRepository.findById( calendarowner );  }
        		if(teacherowner != null) { teacherownerobj = teaRepository.findById( teacherowner );  }
        		if(subjectowner != null) { subjectownerobj = subRepository.findById( subjectowner );  }
        		if(attendanceowner != null) { attownerobj = attRepository.findById( attendanceowner );  }
        		
        		attendances = attRepository.findFilterByStudentSchoolgroupPage( 
        						"%"+ query + "%", 
        						schgroupobj == null ? null : schgroupobj.get(), 
                				schownerobj == null ? null : schownerobj.get() , 
                				classownerobj == null ? null : classownerobj.get(), 
                				studentownerobj == null ? null : studentownerobj.get(),
                				calendarownerobj == null ? null : calendarownerobj.get(),
                				teacherownerobj == null ? null : teacherownerobj.get(),
                                subjectownerobj == null ? null : subjectownerobj.get(),
                                attownerobj == null ? null : attownerobj.get(),
                                statusowner,
                				datefrom.isEmpty() ? null : datefrom.get(),
                		        dateto.isEmpty() ? null : dateto.get(),
                		        pageable
        		);
        	}
        }

        if(attendances.getNumberOfElements() == 0) {
        	Map<String, Object> responseEmpty = new HashMap<>();
        	responseEmpty.put("attendances", Collections.emptyList());
        	responseEmpty.put("currentPage", attendances.getNumber());
        	responseEmpty.put("totalItems", attendances.getTotalElements());
        	responseEmpty.put("totalPages", attendances.getTotalPages());
        	
        	return responseEmpty;
        }
        
        List<Rowcall> calarray = new ArrayList<Rowcall>();
        
        calarray = attendances.getContent();
        
        Map<String, Object> response = new HashMap<>();
        response.put("attendances", calarray);
        response.put("currentPage", attendances.getNumber());
        response.put("totalItems", attendances.getTotalElements());
        response.put("totalPages", attendances.getTotalPages());
        response.put("isLast", attendances.isLast());
        
        long doneAttendances = calarray.stream().filter(sch -> sch.getStatus() == 1).count();       
        long notdoneAttendances = calarray.stream().filter(sch -> sch.getStatus() == 0).count();
        long excusedAttendances = calarray.stream().filter(sch -> sch.getStatus() == 2).count();
        
        response.put("totalPresent", doneAttendances);
        response.put("totalAbsent", notdoneAttendances);
        response.put("totalExcused", excusedAttendances);
        return response;
    }
	
	public Map<String, Object> getOrdinaryStudentAttendances(String query, Optional<Long> schgroupId, Optional<Long> schId, Optional<Long> classId, Optional<Long> calendarId, Optional<Long> studentId, Optional<Timestamp> datefrom, Optional<Timestamp> dateto  ) {
                
        Long schgroup = schgroupId.orElse(null);
        Long schowner = schId.orElse(null);
        Long classowner = classId.orElse(null);
        Long studentowner = studentId.orElse(null);
        Long calendarowner = calendarId.orElse(null);    
        
        List<Rowcall> attendances = null;
        
        if ( query.equals("") || query == null ) {
        	if ( schgroup == null ) {
        		attendances = rowRepository.findAll();
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
        		
        		attendances = attRepository.findByStudentSchoolgroup( 
        						schgroupobj == null ? null : schgroupobj.get(), 
                				schownerobj == null ? null : schownerobj.get() , 
                				classownerobj == null ? null : classownerobj.get(), 
                				studentownerobj == null ? null : studentownerobj.get(),
                				calendarownerobj == null ? null : calendarownerobj.get(),
                				datefrom.isEmpty() ? null : datefrom.get(),
                		        dateto.isEmpty() ? null : dateto.get()
        		);
        	}        	
        }
        else {
        	if ( schgroup == null ) {
        		attendances = rowRepository.filterAll("%"+ query + "%");
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
        		
        		attendances = attRepository.findFilterByStudentSchoolgroup( 
        				"%"+ query + "%", 
        				schgroupobj == null ? null : schgroupobj.get(), 
                		schownerobj == null ? null : schownerobj.get() , 
                		classownerobj == null ? null : classownerobj.get(), 
                		studentownerobj == null ? null : studentownerobj.get(),
                		calendarownerobj == null ? null : calendarownerobj.get(),
                		datefrom.isEmpty() ? null : datefrom.get(),
                		dateto.isEmpty() ? null : dateto.get()
        		);
        	}
        }

        if(attendances.size() == 0) {
        	Map<String, Object> responseEmpty = new HashMap<>();
        	responseEmpty.put("attendances", Collections.emptyList());
        	        	
        	return responseEmpty;
        }
        
        List<Rowcall> calarray = new ArrayList<Rowcall>(attendances);
        
        Map<String, Object> response = new HashMap<>();
        response.put("attendances", calarray);
  
        
       // long active = 1; long inactive = 0;
      /*  long sriAttendances = schRepository.countBySri(active);
        long nonSriAttendances = schRepository.countBySri(inactive);
        long inactiveAttendances = schRepository.countByStatus(inactive);*/
        
        long present = calarray.stream().filter(att -> att.getStatus() == 1).count();       
        long absent = calarray.stream().filter(att -> att.getStatus() == 0).count();
        long excused = calarray.stream().filter(att -> att.getStatus() == 2).count();
        
        response.put("totalPresent", present);
        response.put("totalAbsent", absent);
        response.put("totalExcused", excused);
        return response;
    }
	
	public Attendance update(AttendanceRequest attRequest, long id) {
		Optional<Attendance> existing = attRepository.findById(id);
		if (existing.isPresent()) {			
			Attendance attval = existing.get();
			if ( attval.getDone() == 1 ) {
				return null;
			}
			attval.set_date( new Date() );
			System.out.println("Done value: " + attRequest.getDone() + " Att value: " + attval.getAttId() );
			attval.setDone(attRequest.getDone());
			attval.setPeriod(attRequest.getPeriod());
			//CommonActivity.copyNonNullProperties(attRequest, attval);
			return attRepository.save(attval);
		}  
		return null;
	}

	public Map<String, Object> getAttendancesForSchoolCreatedWithinDays(int numberOfDays, Optional<Long> schgroupId, Optional<Long> schId, Optional<Long> teacherId ) {
		Long schgroup = schgroupId.orElse(null);
        Long schowner = schId.orElse(null);
        Long teacherowner = teacherId.orElse(null);
        
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(numberOfDays);        
        
        Optional<SchoolGroup> schgroupobj = null;
		Optional<School> schownerobj = null;
		Optional<Teacher> teaownerobj = null ;
		
		if(schowner != null) { schownerobj = schRepository.findById( schowner );  } 
		if(schgroup != null) { schgroupobj = groupRepository.findById( schgroup );  } 
		if(teacherowner != null) { teaownerobj = teaRepository.findById( teacherowner );  } 
		
		Timestamp newEndDate = convertLocalDateToTimestamp(endDate);
        Timestamp newStartDate = convertLocalDateToTimestamp(startDate);

        List<Object[]> results  =  attRepository.countSchoolAttendancesDoneCreatedPerDay(
        		newStartDate, 
        		newEndDate, 
        		schgroupobj == null ? null : schgroupobj.get(), 
        		schownerobj == null ? null : schownerobj.get() , 
        		teaownerobj == null ? null : teaownerobj.get() 
        );
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Create a map with all days in the range initialized with count 0
        Map<String, Integer> attsCreatedPerDay = startDate.datesUntil(endDate.plusDays(1))
                .collect(Collectors.toMap(
                        date -> date.format(formatter),
                        date -> 0,
                        (count1, count2) -> count1,
                        LinkedHashMap::new
                ));
        
        for (Object[] result : results) {
            LocalDate createdDate = (LocalDate) result[0];
            int count = ((Number) result[1]).intValue();
            attsCreatedPerDay.put(createdDate.format(formatter), count);
        }
        
        //Calculate the total done attendances
        int sumOfDone = sumMapValues(attsCreatedPerDay);
        
        Map<String, Object> response = new HashMap<>();
        response.put("sessions", sumOfDone);
        response.put("sessionsData", convertMapValuesToList(attsCreatedPerDay) );       

        return response;
        
	}
	
	public Map<String, Object> getAttendancesCreatedWithinDays(int numberOfDays) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(numberOfDays);
        
        Timestamp newEndDate = convertLocalDateToTimestamp(endDate);
        Timestamp newStartDate = convertLocalDateToTimestamp(startDate);

        List<Object[]> results  =  attRepository.countAttendancesDoneCreatedPerDay(newStartDate, newEndDate);//sessions
        List<Object[]> results2 = attRepository.countAttendancesTotalCreatedPerDay(newStartDate, newEndDate);//goals
        List<Object[]> results3 = attRepository.countUniqueTeachersAttendancesPerDay(newStartDate, newEndDate);//teachers

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Create a map with all days in the range initialized with count 0
        Map<String, Integer> attsCreatedPerDay = startDate.datesUntil(endDate.plusDays(1))
                .collect(Collectors.toMap(
                        date -> date.format(formatter),
                        date -> 0,
                        (count1, count2) -> count1,
                        LinkedHashMap::new
                ));
        
        // Create a map with all days in the range initialized with count 0
        Map<String, Integer> attTotalPerDay = startDate.datesUntil(endDate.plusDays(1))
                .collect(Collectors.toMap(
                        date -> date.format(formatter),
                        date -> 0,
                        (count1, count2) -> count1,
                        LinkedHashMap::new
                ));
        
        // Create a map with all days in the range initialized with count 0
        Map<String, Integer> attTeacherPerDay = startDate.datesUntil(endDate.plusDays(1))
                .collect(Collectors.toMap(
                        date -> date.format(formatter),
                        date -> 0,
                        (count1, count2) -> count1,
                        LinkedHashMap::new
                ));
        
        // Update the counts for the days with actual results
        for (Object[] result : results) {
            LocalDate createdDate = (LocalDate) result[0];
            int count = ((Number) result[1]).intValue();
            attsCreatedPerDay.put(createdDate.format(formatter), count);
        }
        ///////////////////////////////////////////////////////////////
              
        // Update the counts for the days with actual results
        for (Object[] result : results2) {
            LocalDate createdDate = (LocalDate) result[0];
            int count = ((Number) result[1]).intValue();
            attTotalPerDay.put(createdDate.format(formatter), count);
        }
        
     // Update the counts for the days with actual results
        for (Object[] result : results3) {
            LocalDate createdDate = (LocalDate) result[0];
            int count = ((Number) result[1]).intValue();
            attTeacherPerDay.put(createdDate.format(formatter), count);
        }
        
        //Calculate the total done attendances
        int sumOfDone = sumMapValues(attsCreatedPerDay);
        int sumOfTotal = sumMapValues(attTotalPerDay);
        int sumOfTeacher = sumMapValues(attTeacherPerDay);
        
        Map<String, Object> response = new HashMap<>();
        response.put("sessions", sumOfDone);
        response.put("sessionsData", convertMapValuesToList(attsCreatedPerDay) );
        response.put("goals", sumOfTotal);
        response.put("teachers", sumOfTeacher);
        response.put("retention", sumOfTotal != 0 ? ((sumOfDone/sumOfTotal) * 100) : 0 );

        return response;
    }
	
	public Map<String, Object> getAttendancesManagementCreatedWithinDays(int numberOfDays) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(numberOfDays);
        
        Timestamp newEndDate = convertLocalDateToTimestamp(endDate);
        Timestamp newStartDate = convertLocalDateToTimestamp(startDate);

        List<Object[]> results  = attmanagementRepository.countAttendancesManagementLatePerDay(newStartDate, newEndDate);//sessions
        List<Object[]> results2 = attmanagementRepository.countAttendancesManagementNoAttachmentPerDay(newStartDate, newEndDate);//goals
        List<Object[]> results3 = attactivityRepository.countAttendancesActivitySlipPerDay(newStartDate, newEndDate);//teachers

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Create a map with all days in the range initialized with count 0
        Map<String, Integer> attsLatePerDay = startDate.datesUntil(endDate.plusDays(1))
                .collect(Collectors.toMap(
                        date -> date.format(formatter),
                        date -> 0,
                        (count1, count2) -> count1,
                        LinkedHashMap::new
                ));
        
        // Create a map with all days in the range initialized with count 0
        Map<String, Integer> attNoAttachmentPerDay = startDate.datesUntil(endDate.plusDays(1))
                .collect(Collectors.toMap(
                        date -> date.format(formatter),
                        date -> 0,
                        (count1, count2) -> count1,
                        LinkedHashMap::new
                ));
        
        // Create a map with all days in the range initialized with count 0
        Map<String, Integer> attSlipPerDay = startDate.datesUntil(endDate.plusDays(1))
                .collect(Collectors.toMap(
                        date -> date.format(formatter),
                        date -> 0,
                        (count1, count2) -> count1,
                        LinkedHashMap::new
                ));
        
        // Update the counts for the days with actual results
        for (Object[] result : results) {
            LocalDate createdDate = (LocalDate) result[0];
            int count = ((Number) result[1]).intValue();
            attsLatePerDay.put(createdDate.format(formatter), count);
        }
        ///////////////////////////////////////////////////////////////
              
        // Update the counts for the days with actual results
        for (Object[] result : results2) {
            LocalDate createdDate = (LocalDate) result[0];
            int count = ((Number) result[1]).intValue();
            attNoAttachmentPerDay.put(createdDate.format(formatter), count);
        }
        
     // Update the counts for the days with actual results
        for (Object[] result : results3) {
            LocalDate createdDate = (LocalDate) result[0];
            int count = ((Number) result[1]).intValue();
            attSlipPerDay.put(createdDate.format(formatter), count);
        }
        
        //Calculate the total done attendances
        int sumOfLate = sumMapValues(attsLatePerDay);
        int sumOfNoAttachment = sumMapValues(attNoAttachmentPerDay);
        int sumOfSlip = sumMapValues(attSlipPerDay);
        
        Map<String, Object> response = new HashMap<>();
        response.put("totalFlags", (sumOfLate + sumOfNoAttachment));
        response.put("late",sumOfLate );
        response.put("noAttachment", sumOfNoAttachment);
        response.put("responseTime", sumOfSlip);
        response.put("principalData", new ArrayList<Integer>().set(0, 0) );

        return response;
    }
	
	private static Timestamp convertLocalDateToTimestamp(LocalDate localDate) {
        LocalDateTime localDateTime = LocalDateTime.of(localDate, LocalTime.MIDNIGHT);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        return Timestamp.from(zonedDateTime.toInstant());
    }
	
	private int sumMapValues  ( Map<String, Integer> map ) {
	    int sum = 0;
	    for (int value : map.values()) {
	        sum += value;
	    }
	    return sum;
	}
	
	private ArrayList<Integer> convertMapValuesToList(Map<String, Integer> map) {
	    return new ArrayList<>(map.values());
	}

	
	
}
