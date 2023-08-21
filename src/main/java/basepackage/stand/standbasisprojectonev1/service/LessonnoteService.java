package basepackage.stand.standbasisprojectonev1.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import basepackage.stand.standbasisprojectonev1.model.Lessonnote;
import basepackage.stand.standbasisprojectonev1.model.Assessment;
import basepackage.stand.standbasisprojectonev1.model.Attendance;
import basepackage.stand.standbasisprojectonev1.model.Calendar;
import basepackage.stand.standbasisprojectonev1.model.ClassStream;
import basepackage.stand.standbasisprojectonev1.model.Enrollment;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;
import basepackage.stand.standbasisprojectonev1.model.Student;
import basepackage.stand.standbasisprojectonev1.model.Subject;
import basepackage.stand.standbasisprojectonev1.model.Teacher;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.LessonnoteRequest;
import basepackage.stand.standbasisprojectonev1.repository.LessonnoteRepository;
import basepackage.stand.standbasisprojectonev1.repository.AssessmentRepository;
import basepackage.stand.standbasisprojectonev1.repository.CalendarRepository;
import basepackage.stand.standbasisprojectonev1.repository.ClassStreamRepository;
import basepackage.stand.standbasisprojectonev1.repository.EnrollmentRepository;
import basepackage.stand.standbasisprojectonev1.repository.LessonnoteActivityRepository;
import basepackage.stand.standbasisprojectonev1.repository.LessonnoteManagementRepository;
import basepackage.stand.standbasisprojectonev1.repository.SchoolRepository;
import basepackage.stand.standbasisprojectonev1.repository.SchoolgroupRepository;
import basepackage.stand.standbasisprojectonev1.repository.StudentRepository;
import basepackage.stand.standbasisprojectonev1.repository.SubjectRepository;
import basepackage.stand.standbasisprojectonev1.repository.TeacherRepository;
import basepackage.stand.standbasisprojectonev1.util.CommonActivity;

@Service
public class LessonnoteService {

	@Autowired
    private SchoolgroupRepository groupRepository;
	
	@Autowired		
    private SchoolRepository schRepository;
	
	@Autowired		
    private ClassStreamRepository clsRepository;
	
	@Autowired		
    private TeacherRepository teaRepository;
	
	@Autowired		
    private SubjectRepository subRepository;
	
	@Autowired		
    private StudentRepository pupRepository;
	
	@Autowired		
    private LessonnoteRepository lsnRepository;	
	
	@Autowired		
    private AssessmentRepository assRepository;
	
	@Autowired		
    private EnrollmentRepository enrolRepository;
	
	@Autowired		
    private LessonnoteManagementRepository lsnmanageRepository;	
	
	@Autowired		
    private LessonnoteActivityRepository lsnactivityRepository;	
	
	@Autowired		
    private CalendarRepository calRepository;
	
	private final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public List<Lessonnote> findAllByCalendar(Long id) {		
		Optional<Calendar> timecal = calRepository.findById(id);
		if (timecal.isPresent()) {
			Calendar calval = timecal.get();			
			return lsnRepository.findByCalendar(calval);
		}
		return null;		
	}
	
	public List<Lessonnote> findAll() {		
		return lsnRepository.findAll();
	}
	
	public Lessonnote saveOne(LessonnoteRequest lsnRequest) {
		 ModelMapper modelMapper = new ModelMapper();   
		 Lessonnote lsn = modelMapper.map(lsnRequest, Lessonnote.class);
		 
		 Optional<Teacher> t = teaRepository.findById( lsnRequest.getTea_id() );
		 Optional<Subject> s = subRepository.findById( lsnRequest.getSub_id() );
		 Optional<Calendar> c = calRepository.findById( lsnRequest.getCalendar_id() );
		 
		 if ( t.isPresent() && s.isPresent() && c.isPresent() ) {
			 if ( lsnRepository.findUniqueLessonnote( t.get(), s.get(), c.get(), lsnRequest.getClass_index() ).isPresent() ) {
				 System.out.println("This lessonnote has been submitted already");
				 return null;
			 }
			 lsn.setTeacher(t.get());
			 lsn.setSubject(s.get());
			 lsn.setCalendar(c.get());
			 lsn.setCycle_count(1);
			 lsn.setSubmission( CommonActivity.parseTimestamp( CommonActivity.todayDate() ) );
			 return lsnRepository.save(lsn);
		 }
		 return null;
	}	
	
		//For mobile app 
		public Map<String, Object> getTeacherLessonnoteForWeek( Optional<Long> teacherId, Optional<Integer> week ){
			
				Long teacherowner = teacherId.orElse(null); 
				Optional<Teacher> teacherownerobj = null;
				List<Lessonnote> lessonnotes = null;
				 
				if( teacherowner != null ) { teacherownerobj = teaRepository.findById( teacherowner );  }
			
				lessonnotes = lsnRepository.findByTeacherWeekLessonnote( 				
					teacherownerobj == null ? null : teacherownerobj.get()			
					//week is unused
				);
				
			 	List<Lessonnote> calarray = new ArrayList<Lessonnote>(lessonnotes);
		        
		        Map<String, Object> response = new HashMap<>();
		        response.put("lessonnotes", calarray);
		        response.put("amount", calarray.size());
		        return response;
		}
	
	public Lessonnote findLessonnote(Long id) {		
		Optional<Lessonnote> lsn = lsnRepository.findById(id);
		if (lsn.isPresent()) {
			Lessonnote lsnval = lsn.get();			
			return lsnval;
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
        Page<Lessonnote> Lessonnotes = null;
        
        if ( query == null || query.equals("") ) {
        	if ( schgroup == null ) {
        		Lessonnotes = lsnRepository.findAll(pageable);
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
        		
        		Lessonnotes = lsnRepository.findByTeacherSchoolgroupPage(
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
        		Lessonnotes = lsnRepository.filter("%"+ query + "%",  pageable);
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
        		
        		Lessonnotes = lsnRepository.findFilterByTeacherSchoolgroupPage( 
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
        
        List<Lessonnote> calarray = new ArrayList<Lessonnote>();
        
        calarray = Lessonnotes.getContent();
        
        Map<String, Object> response = new HashMap<>();
        response.put("lessonnotes", calarray);
        response.put("currentPage", Lessonnotes.getNumber());
        response.put("totalItems", Lessonnotes.getTotalElements());
        response.put("totalPages", Lessonnotes.getTotalPages());
        response.put("isLast", Lessonnotes.isLast());
        
        Map<String, Object> response2 = getOrdinaryTeacherLessonnotes(query, schgroupId, schId, classId, week, calendarId, teacherId, subjectId, datefrom, dateto );
        List<Lessonnote> ordinaryArray = (List<Lessonnote>) response2.get("lessonnotes");
        
        long submitLess = ordinaryArray.stream().filter(o -> o.getSubmission() != null).count();       
        long notsubmitLess = ordinaryArray.stream().filter(o -> o.getClosure() != null).count();
        
        response.put("totalSubmitted", submitLess);
        response.put("totalClosed", notsubmitLess);
     
        return response;
    }
	
	public Map<String, Object> getOrdinaryTeacherLessonnotes(String query, Optional<Long> schgroupId, Optional<Long> schId, Optional<Integer> classId, Optional<Integer> week, Optional<Long> calendarId, Optional<Long> teacherId, Optional<Long> subjectId, Optional<Timestamp> datefrom, Optional<Timestamp> dateto  ) {
        
        Long schgroup = schgroupId.orElse(null);
        Long schowner = schId.orElse(null);
        Integer classowner = classId.orElse(null);
        Integer weeknow = week.orElse(null);
        Long subjectowner = subjectId.orElse(null);
        Long teacherowner = teacherId.orElse(null);     
        Long calendarowner = calendarId.orElse(null);
        
        List<Lessonnote> lessonnotes = null;
        
        if ( query == null || query.equals("")  ) {
        	if ( schgroup == null ) {
        		lessonnotes = lsnRepository.findAll();
        	}
        	else {
        		Optional<SchoolGroup> schgroupobj = groupRepository.findById( schgroup );
        		Optional<School> schownerobj = null;
        		//Optional<ClassStream> classownerobj = null ;
        		Optional<Teacher> teacherownerobj = null;
        		Optional<Calendar> calendarownerobj = null;
        		Optional<Subject> subjectownerobj = null;
        		
        		if(schowner != null) { schownerobj = schRepository.findById( schowner );  } 
        		if(teacherowner != null) { teacherownerobj = teaRepository.findById( teacherowner );  } 
        		if(subjectowner != null) { subjectownerobj = subRepository.findById( subjectowner );  } 
        		if(calendarowner != null) { calendarownerobj = calRepository.findById( calendarowner );  } 
        		
        		lessonnotes = lsnRepository.findByTeacherSchoolgroup(         				
        				schgroupobj == null ? null : schgroupobj.get(), 
                        schownerobj == null ? null : schownerobj.get(), 
                        classowner,
                        weeknow,
                        teacherownerobj == null ? null : teacherownerobj.get(),
                        subjectownerobj == null ? null : subjectownerobj.get(),		
                        calendarownerobj == null ? null : calendarownerobj.get(),
                        datefrom.isEmpty() ? null : datefrom.get(),
                        dateto.isEmpty() ? null : dateto.get() 
        		);
        	}        	
        }
        else {
        	if ( schgroup == null ) {
        		lessonnotes = lsnRepository.filterAll("%"+ query + "%");
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
        		
        		lessonnotes = lsnRepository.findFilterByTeacherSchoolgroup( 
        				"%"+ query + "%", 
        				schgroupobj == null ? null : schgroupobj.get(), 
                        schownerobj == null ? null : schownerobj.get(), 
                        classowner,
                        weeknow,
                        teacherownerobj == null ? null : teacherownerobj.get(),
                        subjectownerobj == null ? null : subjectownerobj.get(),	
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
        
        List<Lessonnote> calarray = new ArrayList<Lessonnote>(lessonnotes);
        
        Map<String, Object> response = new HashMap<>();
        response.put("lessonnotes", calarray);
        
        long submitLess = calarray.stream().filter(o -> o.getSubmission() != null).count();       
        long notsubmitLess = calarray.stream().filter(o -> o.getSubmission() == null).count();
        
        response.put("totalSubmitted", submitLess);
        response.put("totalNotSubmitted", notsubmitLess);
  
        return response;
    }
	
	public Map<String, Object> getPaginatedStudentLessonnotes(int page, int size, String query, Optional<Long> schgroupId, Optional<Long> schId, Optional<Long> classId, Optional<Long> calendarId, Optional<Long> teacherId, Optional<Long> studentId, Optional<Integer> score, Optional<String> type, Optional<Long> lsn, Optional<Timestamp> datefrom, Optional<Timestamp> dateto  ) {
		CommonActivity.validatePageNumberAndSize(page, size);
        
        Long schgroup = schgroupId.orElse(null);
        Long schowner = schId.orElse(null);
        Long classowner = classId.orElse(null);
        Long calendarowner = calendarId.orElse(null);     
        Long teacherowner = teacherId.orElse(null);
        Long studentowner = studentId.orElse(null);
        Integer scoreowner = score.orElse(null);
        String typeowner = type.orElse(null);
        Long lessonnoteowner = lsn.orElse(null);
        
        
        // Retrieve Lessonnotes
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Assessment> Lessonnotes = null;
        
        if ( query == null || query.equals("") ) {
        	if ( schgroup == null ) {
        		Lessonnotes = assRepository.findAll(pageable);
        	}
        	else {
        		Optional<SchoolGroup> schgroupobj = groupRepository.findById( schgroup );
        		Optional<School> schownerobj = null;
        		Optional<ClassStream> classownerobj = null ;
        		Optional<Calendar> calendarownerobj = null;
        		Optional<Teacher> teacherownerobj = null;
        		Optional<Student> studentownerobj = null;
        		Optional<Lessonnote> lessonnoteownerobj = null;
        		
        		if(schowner != null) { schownerobj = schRepository.findById( schowner );  } 
        		if(studentowner != null) { studentownerobj = pupRepository.findById( studentowner );  } 
        		if(classowner != null) { classownerobj = clsRepository.findById( classowner );  }
        		if(calendarowner != null) { calendarownerobj = calRepository.findById( calendarowner );  }
        		if(teacherowner != null) { teacherownerobj = teaRepository.findById( teacherowner );  }
        		if(lessonnoteowner != null) { lessonnoteownerobj = lsnRepository.findById( lessonnoteowner );  }
        		
        		Lessonnotes = lsnRepository.findByStudentSchoolgroupPage(         				
        				schgroupobj == null ? null : schgroupobj.get(), 
                        schownerobj == null ? null : schownerobj.get(), 
                        classownerobj == null ? null : classownerobj.get(),
                        studentownerobj == null ? null : studentownerobj.get(),
                        teacherownerobj == null ? null : teacherownerobj.get(),
                        calendarownerobj == null ? null : calendarownerobj.get(),
                        lessonnoteownerobj == null ? null : lessonnoteownerobj.get(),
                        scoreowner,
                        typeowner,
                        datefrom.isEmpty() ? null : datefrom.get(),
                        dateto.isEmpty() ? null : dateto.get(),                                		
        				pageable
        		);
        	}        	
        }
        else {
        	if ( schgroup == null ) {
        		Lessonnotes = assRepository.filter("%"+ query + "%",  pageable);
        	}
        	else {    
        		Optional<SchoolGroup> schgroupobj = groupRepository.findById( schgroup );
        		Optional<School> schownerobj = null;
        		Optional<ClassStream> classownerobj = null;
        		Optional<Calendar> calendarownerobj = null;
        		Optional<Teacher> teacherownerobj = null;
        		Optional<Student> studentownerobj = null;
        		Optional<Lessonnote> lessonnoteownerobj = null;
        		
        		if(schowner != null) { schownerobj = schRepository.findById( schowner );  } 
        		if(studentowner != null) { studentownerobj = pupRepository.findById( studentowner );  } 
        		if(classowner != null) { classownerobj = clsRepository.findById( classowner );  }
        		if(calendarowner != null) { calendarownerobj = calRepository.findById( calendarowner );  }
        		if(teacherowner != null) { teacherownerobj = teaRepository.findById( teacherowner );  }
        		if(lessonnoteowner != null) { lessonnoteownerobj = lsnRepository.findById( lessonnoteowner );  }
        		
        		
        		Lessonnotes = lsnRepository.findFilterByStudentSchoolgroupPage( 
        				"%"+ query + "%", 
        				schgroupobj == null ? null : schgroupobj.get(), 
                        schownerobj == null ? null : schownerobj.get(), 
                        classownerobj == null ? null : classownerobj.get(),
                        studentownerobj == null ? null : studentownerobj.get(),
                        teacherownerobj == null ? null : teacherownerobj.get(),
                        calendarownerobj == null ? null : calendarownerobj.get(),
                        lessonnoteownerobj == null ? null : lessonnoteownerobj.get(),
                        scoreowner,
                        typeowner,
                        datefrom.isEmpty() ? null : datefrom.get(),
                        dateto.isEmpty() ? null : dateto.get(),  
        				pageable
        		);
        	}
        }

        if(Lessonnotes.getNumberOfElements() == 0) {
        	Map<String, Object> responseEmpty = new HashMap<>();
        	responseEmpty.put("lessonnotes", Collections.emptyList());
        	responseEmpty.put("currentPage", Lessonnotes.getNumber());
        	responseEmpty.put("totalItems", Lessonnotes.getTotalElements());
        	responseEmpty.put("totalPages", Lessonnotes.getTotalPages());        	
        	return responseEmpty;
        }
        
        List<Assessment> calarray = new ArrayList<Assessment>();
        
        calarray = Lessonnotes.getContent();
        
        Map<String, Object> response = new HashMap<>();
        response.put("lessonnotes", calarray);
        response.put("currentPage", Lessonnotes.getNumber());
        response.put("totalItems", Lessonnotes.getTotalElements());
        response.put("totalPages", Lessonnotes.getTotalPages());
        response.put("isLast", Lessonnotes.isLast());        
        
        long passedAssessment = calarray.stream().filter(o -> o.getScore() > 50).count();       
        long notpassedAssessment = calarray.stream().filter(o -> o.getScore() < 50).count();
        
        response.put("passedAssessment", passedAssessment);
        response.put("notPassedAssessment", notpassedAssessment);
        
        return response;
    }
	
	public Map<String, Object> getOrdinaryStudentLessonnotes(String query, Optional<Long> schgroupId, Optional<Long> schId, Optional<Long> classId,  Optional<Integer> week, Optional<Long> calendarId, Optional<Long> studentId, Optional<Timestamp> datefrom, Optional<Timestamp> dateto  ) {
                
        Long schgroup = schgroupId.orElse(null);
        Long schowner = schId.orElse(null);
        Long classowner = classId.orElse(null);
        Integer weeknow = week.orElse(null);
        Long studentowner = studentId.orElse(null);
        Long calendarowner = calendarId.orElse(null);    
        
        List<Lessonnote> Lessonnotes = null;
        
        if ( query == null || query.equals("") ) {
        	if ( schgroup == null ) {
        		Lessonnotes = lsnRepository.findAll();
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
        		
        		Lessonnotes = lsnRepository.findByStudentSchoolgroup( 
        				schgroupobj == null ? null : schgroupobj.get(), 
                        schownerobj == null ? null : schownerobj.get(), 
                        classownerobj == null ? null : classownerobj.get(),
                        weeknow,
                        studentownerobj == null ? null : studentownerobj.get(),
                        calendarownerobj == null ? null : calendarownerobj.get(),
                        datefrom.isEmpty() ? null : datefrom.get(),
                        dateto.isEmpty() ? null : dateto.get()
        		);
        	}        	
        }
        else {
        	if ( schgroup == null ) {
        		Lessonnotes = lsnRepository.filterAll("%"+ query + "%");
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
        		
        		Lessonnotes = lsnRepository.findFilterByStudentSchoolgroup( 
        				"%"+ query + "%", 
        				schgroupobj == null ? null : schgroupobj.get(), 
                        schownerobj == null ? null : schownerobj.get(), 
                        classownerobj == null ? null : classownerobj.get(),
                        weeknow,
                        studentownerobj == null ? null : studentownerobj.get(),
                        calendarownerobj == null ? null : calendarownerobj.get(),
                        datefrom.isEmpty() ? null : datefrom.get(),
                        dateto.isEmpty() ? null : dateto.get()
        		);
        	}
        }

        if(Lessonnotes.size() == 0) {
        	Map<String, Object> responseEmpty = new HashMap<>();
        	responseEmpty.put("Lessonnotes", Collections.emptyList());        	        	
        	return responseEmpty;
        }
        
        List<Lessonnote> calarray = new ArrayList<Lessonnote>(Lessonnotes);
        
        Map<String, Object> response = new HashMap<>();
        response.put("lessonnotes", calarray);
  
        long approvedLess = calarray.stream().filter(o -> o.getApproval() != null).count();       
        long notapprovedLess = calarray.stream().filter(o -> o.getApproval() == null).count();
        
        response.put("totalApproved", approvedLess);
        response.put("totalNotApproved", notapprovedLess);
        
        return response;
    }
	
	public Lessonnote update(LessonnoteRequest lsnRequest, long id) {
		Optional<Lessonnote> existing = lsnRepository.findById(id);
		
		if (existing.isPresent()) {
			Lessonnote lsnval = existing.get();			
			if (lsnRequest.getAction() != null) {
				if (lsnRequest.getAction().equals("submit") ) {
					if( lsnval.getSubmission() == null ) {					;
						lsnval.setSubmission( CommonActivity.parseTimestamp( CommonActivity.todayDate() ) );
						lsnval.setCycle_count( lsnval.getCycle_count() + 1 );
					}				
				}
				if (lsnRequest.getAction().equals("resubmit") ) {
					if( lsnval.getResubmission() == null ) {
						lsnval.setRevert(null);
						lsnval.setResubmission( CommonActivity.parseTimestamp( CommonActivity.todayDate() ) );
						lsnval.setCycle_count( lsnval.getCycle_count() + 1 );
					}				
				}
				else if (lsnRequest.getAction().equals("revert")) {
					if( lsnval.getRevert() == null ) {
						lsnval.setResubmission(null);
						lsnval.setRevert( CommonActivity.parseTimestamp( CommonActivity.todayDate() ) );
						lsnval.setCycle_count( lsnval.getCycle_count() + 1 );
					}
				}			
				else if (lsnRequest.getAction().equals("approval")) {
					if( lsnval.getApproval() == null ) {
						lsnval.setApproval( CommonActivity.parseTimestamp( CommonActivity.todayDate() ) );
						lsnval.setCycle_count( lsnval.getCycle_count() + 1 );	
						
						List<Enrollment> enrolData = enrolRepository.findByClassIndex( lsnval.getClass_index(), lsnval.getCalendar().getSchool() );
						
						if (lsnRequest.getClasswork().equals(1)) {
							for (Enrollment enr : enrolData) {	
								Assessment assval = new Assessment();
								assval.setTitle("CLW-"+ lsnval.getTitle() );
								assval.setLsn(lsnval);
								assval.setEnroll(enr);
								assval.set_type("clw");
								assval.setMax(0);
								assval.setActual(0);
								assval.setScore(0);
								assRepository.save(assval);
							}
						}
						
						if (lsnRequest.getTest().equals(1)) {
							for (Enrollment enr : enrolData) {	
								Assessment assval2 = new Assessment();
								assval2.setTitle("TST-"+ lsnval.getTitle() );
								assval2.setLsn(lsnval);
								assval2.setEnroll(enr);
								assval2.set_type("tst");
								assval2.setMax(0);
								assval2.setActual(0);
								assval2.setScore(0);
								assRepository.save(assval2);
							}
						}
						
						if (lsnRequest.getHomework().equals(1)) {						
							for (Enrollment enr : enrolData) {	
								Assessment assval3 = new Assessment();
								assval3.setTitle("HWK-"+ lsnval.getTitle() );
								assval3.setLsn(lsnval);
								assval3.setEnroll(enr);
								assval3.set_type("hwk");
								assval3.setMax(0);
								assval3.setActual(0);
								assval3.setScore(0);
								assRepository.save(assval3);
							}
						}
						
						if (lsnRequest.getMidterm().equals(1)) {
							for (Enrollment enr : enrolData) {	
								Assessment assval4 = new Assessment();
								assval4.setTitle("MID-"+ lsnval.getTitle() );
								assval4.setLsn(lsnval);
								assval4.setEnroll(enr);
								assval4.set_type("mid");
								assval4.setMax(0);
								assval4.setActual(0);
								assval4.setScore(0);
								assRepository.save(assval4);
							}
						}
						
						if (lsnRequest.getFinalexam().equals(1)) {
							for (Enrollment enr : enrolData) {	
								Assessment assval5 = new Assessment();
								assval5.setTitle("FINAL-"+ lsnval.getTitle() );
								assval5.setLsn(lsnval);
								assval5.setEnroll(enr);
								assval5.set_type("final");
								assval5.setMax(0);
								assval5.setActual(0);
								assval5.setScore(0);
								assRepository.save(assval5);
							}
						}
						
					}
				}
				else if (lsnRequest.getAction().equals("launch")) {
					if( lsnval.getLaunch() == null ) {
						lsnval.setLaunch( CommonActivity.parseTimestamp( CommonActivity.todayDate() ) );
					}
				}
				else if (lsnRequest.getAction().equals("closure")) {
					if( lsnval.getClosure() == null ) {
						lsnval.setLaunch(null);
						lsnval.setClosure( CommonActivity.parseTimestamp( CommonActivity.todayDate() ) );
					}
				}
				else if (lsnRequest.getAction().equals("closed")) { //for only principal
					if( lsnval.getPrincipal_closure() == null ) {						
						lsnval.setPrincipal_closure( CommonActivity.parseTimestamp( CommonActivity.todayDate() ) );
					}
				}
			}
			CommonActivity.copyNonNullProperties(lsnRequest, lsnval);
			return lsnRepository.save(lsnval);
		} 
		return null;
	}

	public String updateFile(Long id, String fileName) {
		Optional<Lessonnote> existing = lsnRepository.findById(id);
		if (existing.isPresent()) {
			Lessonnote lsnval = existing.get();		
			lsnval.set_file(fileName);
			Lessonnote filledLessonnote = lsnRepository.save(lsnval);
			return filledLessonnote.get_file();
		}  
		return null;
	}
	
	public Map<String, Object> getLessonnotesForSchoolCreatedWithinDays(int numberOfDays, Optional<Long> schgroupId, Optional<Long> schId, Optional<Long> teacherId ) {
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

        List<Object[]> results  =  lsnRepository.countSchoolLessonnotesCreatedPerDay(
        		newStartDate, 
        		newEndDate, 
        		schgroupobj == null ? null : schgroupobj.get(), 
        		schownerobj == null ? null : schownerobj.get() , 
        		teaownerobj == null ? null : teaownerobj.get() 
        );
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");

        // Create a map with all days in the range initialized with count 0
        Map<String, Integer> lsnsCreatedPerDay = startDate.datesUntil(endDate.plusDays(1))
                .collect(Collectors.toMap(
                        date -> date.format(formatter),
                        date -> 0,
                        (count1, count2) -> count1,
                        LinkedHashMap::new
                ));
        
        for (Object[] result : results) {
        	Date createdDate = (Date) result[0];
            int count = ((Number) result[1]).intValue();
            lsnsCreatedPerDay.put( formatter2.format(createdDate) , count);
        }
        
        //Calculate the total done attendances
        int sumOfDone = sumMapValues(lsnsCreatedPerDay);
        
        Map<String, Object> response = new HashMap<>();
        response.put("sessions", sumOfDone);
        response.put("sessionsData", convertMapValuesToList(lsnsCreatedPerDay) );       

        return response;
        
	}
		
	public Map<String, Object> getLessonnotesCreatedWithinDays(int numberOfDays) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(numberOfDays);
        
        Timestamp newEndDate = convertLocalDateToTimestamp(endDate);
        Timestamp newStartDate = convertLocalDateToTimestamp(startDate);

        List<Object[]> results = lsnRepository.countLessonnotesCreatedPerDay(newStartDate, newEndDate);
        List<Object[]> results2 = lsnRepository.countLessonnotesTotalCreatedPerDay(newStartDate, newEndDate);
        List<Object[]> results3 = lsnRepository.countUniqueTeachersLessonnotesCreatedPerDay(newStartDate, newEndDate);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");

        // Create a map with all days in the range initialized with count 0
        Map<String, Integer> lsnsCreatedPerDay = startDate.datesUntil(endDate.plusDays(1))
                .collect(Collectors.toMap(
                        date -> date.format(formatter),
                        date -> 0,
                        (count1, count2) -> count1,
                        LinkedHashMap::new
                ));
        
        // Create a map with all days in the range initialized with count 0
        Map<String, Integer> lsnTotalCreatedPerDay = startDate.datesUntil(endDate.plusDays(1))
                .collect(Collectors.toMap(
                        date -> date.format(formatter),
                        date -> 0,
                        (count1, count2) -> count1,
                        LinkedHashMap::new
                ));
        
        Map<String, Integer> lsnTeacherPerDay = startDate.datesUntil(endDate.plusDays(1))
                .collect(Collectors.toMap(
                        date -> date.format(formatter),
                        date -> 0,
                        (count1, count2) -> count1,
                        LinkedHashMap::new
                ));
        
        // Update the counts for the days with actual results
        for (Object[] result : results) {
        	Date createdDate = (Date) result[0];
            int count = ((Number) result[1]).intValue();
            lsnsCreatedPerDay.put( formatter2.format(createdDate) , count);
        }
        
        for (Object[] result : results2) {
        	Date createdDate = (Date) result[0];
            int count = ((Number) result[1]).intValue();
            lsnTotalCreatedPerDay.put( formatter2.format(createdDate) , count);
        }
        
        for (Object[] result : results3) {
            LocalDate createdDate = (LocalDate) result[0];
            int count = ((Number) result[1]).intValue();
            lsnTeacherPerDay.put( formatter2.format(createdDate) , count);
        }

        //Calculate the total done lessonnotes
        int sumOfCreated = sumMapValues(lsnsCreatedPerDay);
        int sumOfTotal = sumMapValues(lsnTotalCreatedPerDay);
        int sumOfTeacher = sumMapValues(lsnTeacherPerDay);
     
        
        Map<String, Object> response = new HashMap<>();
        response.put("sessions", sumOfCreated);
        response.put("sessionsData", convertMapValuesToList(lsnsCreatedPerDay) );
        response.put("goals", sumOfTotal);
        response.put("teachers", sumOfTeacher);
        response.put("retention", sumOfTotal != 0 ? ((sumOfCreated/sumOfTotal) * 100) : 0 );

        return response;
    }
	
	public Map<String, Object> getLessonnotesManagementCreatedWithinDays(int numberOfDays) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(numberOfDays);
        
        Timestamp newEndDate = convertLocalDateToTimestamp(endDate);
        Timestamp newStartDate = convertLocalDateToTimestamp(startDate);

        List<Object[]> results  = lsnRepository.countLessonnotesManagementRevertedPerDay(newStartDate, newEndDate);//
        List<Object[]> results2 = lsnRepository.countLessonnotesManagementNotSubmittedPerDay(newStartDate, newEndDate);//
        List<Object[]> results3 = lsnmanageRepository.countLessonnotesManagementBasicPerDay(newStartDate, newEndDate);//
        List<Object[]> results4 = lsnactivityRepository.countLessonnotesActivitySlipPerDay(newStartDate, newEndDate);//
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");

        // Create a map with all days in the range initialized with count 0
        Map<String, Integer> lsnsRevertPerDay = startDate.datesUntil(endDate.plusDays(1))
                .collect(Collectors.toMap(
                        date -> date.format(formatter),
                        date -> 0,
                        (count1, count2) -> count1,
                        LinkedHashMap::new
                ));
        
        // Create a map with all days in the range initialized with count 0
        Map<String, Integer> lsnNotSubmittedPerDay = startDate.datesUntil(endDate.plusDays(1))
                .collect(Collectors.toMap(
                        date -> date.format(formatter),
                        date -> 0,
                        (count1, count2) -> count1,
                        LinkedHashMap::new
                ));
       
        //
        Map<String, Integer> lsnslipPerDay = startDate.datesUntil(endDate.plusDays(1))
                .collect(Collectors.toMap(
                        date -> date.format(formatter),
                        date -> 0,
                        (count1, count2) -> count1,
                        LinkedHashMap::new
                ));
        
        // Create a map with all days in the range initialized with count 0
        Map<String, Integer> lsnManagementPerDay = startDate.datesUntil(endDate.plusDays(1))
                .collect(Collectors.toMap(
                        date -> date.format(formatter),
                        date -> 0,
                        (count1, count2) -> count1,
                        LinkedHashMap::new
                ));
        
        // Update the counts for the days with actual results
        for (Object[] result : results) {
        	Date createdDate = (Date) result[0];
            int count = ((Number) result[1]).intValue();
            lsnsRevertPerDay.put( formatter2.format(createdDate) , count);
        }
        ///////////////////////////////////////////////////////////////
              
        // Update the counts for the days with actual results
        for (Object[] result : results2) {
        	Date createdDate = (Date) result[0];
            int count = ((Number) result[1]).intValue();
            lsnManagementPerDay.put( formatter2.format(createdDate) , count);
        }
        
        // Update the counts for the days with actual results
        for (Object[] result : results3) {
        	Date createdDate = (Date) result[0];
            int count = ((Number) result[1]).intValue();
            lsnNotSubmittedPerDay.put( formatter2.format(createdDate) , count);
        }
        
        //
        for (Object[] result : results4) {
        	Date createdDate = (Date) result[0];
            int count = ((Number) result[1]).intValue();
            lsnslipPerDay.put( formatter2.format(createdDate) , count);
        }
        
        //Calculate the total done lessonnotes
        int sumOfRevert = sumMapValues(lsnsRevertPerDay);
        int sumOfManagement = sumMapValues(lsnManagementPerDay);
        int sumOfNotSubmitted = sumMapValues(lsnNotSubmittedPerDay);
        int sumOfSlip = sumMapValues(lsnslipPerDay);
        
        Map<String, Object> response = new HashMap<>();
        ArrayList<Integer> myList = new ArrayList<>();
        myList.add(0, 0);
        response.put("totalFlags", (sumOfRevert + sumOfManagement + sumOfNotSubmitted ));
        response.put("reverted", sumOfRevert );
        response.put("management", sumOfManagement);
        response.put("notsubmitted", sumOfNotSubmitted);
        response.put("responseTime", sumOfSlip);
        response.put("principalData", myList ); //EDIT LATER PLEASE

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
