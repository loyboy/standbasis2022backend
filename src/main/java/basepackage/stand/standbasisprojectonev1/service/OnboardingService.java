package basepackage.stand.standbasisprojectonev1.service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;

import basepackage.stand.standbasisprojectonev1.model.Calendar;
import basepackage.stand.standbasisprojectonev1.model.ClassStream;
import basepackage.stand.standbasisprojectonev1.model.Enrollment;
import basepackage.stand.standbasisprojectonev1.model.RoleName;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;
import basepackage.stand.standbasisprojectonev1.model.Student;
import basepackage.stand.standbasisprojectonev1.model.Subject;
import basepackage.stand.standbasisprojectonev1.model.Teacher;
import basepackage.stand.standbasisprojectonev1.model.TimeTable;
import basepackage.stand.standbasisprojectonev1.model.User;
import basepackage.stand.standbasisprojectonev1.payload.Permissions;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.*;
import basepackage.stand.standbasisprojectonev1.repository.*;


@Service
public class OnboardingService {

//private static final Logger logger = LoggerFactory.getLogger(OnboardingService.class);
	
	Gson gsonObj = new Gson();
	
	private final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Autowired
    private JavaMailSender mailSender;
	
	@Autowired
	private PasswordEncoder passwordencoder;
	
	@Autowired
    private SchoolRepository schRepository;
	
	@Autowired
    private SchoolgroupRepository schgroupRepository;
	
	@Autowired
    private TeacherRepository teaRepository;
	
	@Autowired
    private StudentRepository pupilRepository;
	
	@Autowired
    private ClassStreamRepository classRepository;
	
	@Autowired
    private EnrollmentRepository enrollRepository;
	
	@Autowired
    private TimetableRepository timeRepository;
	
	@Autowired
    private SubjectRepository subRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CalendarRepository calRepository;

	@Autowired
	private CalendarService calService;

	@Autowired
	private ClassService clsService;

	ModelMapper modelMapper = new ModelMapper();   

	

	public Boolean onboardNewStudent( List<StudentRequest> pupRequest, Long calendarId ) {	
		try{
				Calendar mycal = calService.findCalendar(calendarId);

				List<ClassStream> clsstream = clsService.findAllBySchool(mycal.getSchool().getSchId());

				Map< String, List<ClassStream> > foundUniqueClass = new HashMap< String, List<ClassStream> >();

			    for (StudentRequest c : pupRequest) {
			    	String clsname = c.getClass_name(); 
					String arm = c.getArm(); 
					List<ClassStream> foundMatch = findWithTitleAndArm( clsname, arm, clsstream );	
					 	 
					if(foundMatch.size() > 0) {
						List<ClassStream> noduplicateclass = foundMatch.stream().distinct().collect(Collectors.toList());
						String mykey = c.getName().toString() + "_" + c.getGender().toString() + "_" + c.getRegno().toString();					 		
						foundUniqueClass.put( mykey , noduplicateclass );
					}
			    }

				if(foundUniqueClass.size() > 0) {
			    		 
			    		 for (Map.Entry<String, List<ClassStream>> entry : foundUniqueClass.entrySet()) {
			    		        Student s = new Student();
			    		        
			    		        //save student first
			    		        String[] separateValues = entry.getKey().split("_");
			    		        String specialIdStudent = createUuid("student-", mycal.getSchool().getSchId() );
			    		        
			    		        s.setId(specialIdStudent);
			    		        s.setName(separateValues[0]);
			    		        s.setGender(separateValues[1]);
			    		        s.setReg_no(separateValues[2]);
			    		        s.setSchool(mycal.getSchool());
			    		        
			    		        Student savedValue = pupilRepository.save(s);
			    		        List<ClassStream> classFound = entry.getValue();
			    		        
			    		        String specialId = createUuid("enrollment-", mycal.getSchool().getSchId());
			    		        Enrollment e = new Enrollment(specialId, savedValue, classFound.get(0), mycal, 1 );
			    		        enrollRepository.save(e);
			    		 }

						return true; 
			    }

			return false;
		}
    	catch (Exception e) {
    		e.printStackTrace();
    		System.out.println( "Student Onboarding Error: " + e.getLocalizedMessage() );
    		return false;
    	}
	}
	
    public Boolean onboardNewSchool( SchoolRequest schRequest, List<TeacherRequest> teaRequest, List<StudentRequest> pupRequest, List<ClassRequest> classRequest, List<TimetableRequest> timeRequest, UserAccountRequest userRequest ) {
    	 	
    	HashMap<String, Integer> dayToInteger = new HashMap<String, Integer>();
    	dayToInteger.put("Monday",1);
    	dayToInteger.put("Tuesday",2);
    	dayToInteger.put("Wednesday",3);
    	dayToInteger.put("Thursday",4);
    	dayToInteger.put("Friday",5);
    	dayToInteger.put("Saturday",6);
    	
    	try {
    		// First save School, Teacher and Class
    		School sch = modelMapper.map(schRequest, School.class);
    		Optional<SchoolGroup> sg = schgroupRepository.findById( schRequest.getOwner() );
    		if(sg.isPresent()) {
    				String uuid = UUID.randomUUID().toString();
		    		School prePersistSchool = prePersistFunction(sch);
					String schStringId = prePersistSchool.getId();
		    		prePersistSchool.setOwner(sg.get());
		    		prePersistSchool.setStatus(1);
		    		prePersistSchool.setSri(0);
		    		prePersistSchool.setId( schStringId + "-" + uuid.split("-")[4].substring(4) );
		    		School savedSchool = schRepository.save(prePersistSchool);
		    		TimeUnit.SECONDS.sleep(1);    		
		    		
		    		//Create a calendar
		    		Calendar _cal = new Calendar();
		    		String myidcal = createUuid("calendar-", savedSchool.getSchId() );
		    		_cal.setId(myidcal);
		    		_cal.setTerm(-99);
		    		_cal.setStartdate( parseTimestamp( "1999-01-03 00:00:00" ) );
		    		_cal.setEnddate( parseTimestamp( "1999-04-21 00:00:00" ) );
		    		_cal.setStatus(0);
		    		_cal.setSession("1999/2000");
		    		_cal.setSchool(savedSchool);		    		
		    		Calendar savedCalendar = calRepository.save(_cal);		    		
			    	
		    		// Save all the teachers at once
			    	List<Teacher> tea = teaRequest.stream().map(t -> modelMapper.map(t, Teacher.class)).collect(Collectors.toList());
			    	List<Teacher> newTeaObject = tea.stream().map(t -> {
			    		String myid = createUuid("teacher-", savedSchool.getSchId() );
			    		t.setId(myid);
			    		t.setStatus(1);
			    		t.setSchool(savedSchool);	           
		 	            return t;
			    	}).collect(Collectors.toList());
			    	List<Teacher> savedTeachers = teaRepository.saveAll(newTeaObject);
			    	TimeUnit.SECONDS.sleep(1);	    	
			    	
			    	// Save all the class rooms at once
			    	List<ClassStream> cls = classRequest.stream().map(c -> modelMapper.map(c, ClassStream.class)).collect(Collectors.toList());
			    	List<ClassStream> newClassObject = cls.stream().map(c -> {   
			    		String myid = createUuid("classroom-", savedSchool.getSchId() );
			    		c.setId(myid);
			    		c.setStatus(1);
					 	c.setSchool( savedSchool);	           
			            return c;
			    	}).collect(Collectors.toList());
			    	List<ClassStream> savedClasses = classRepository.saveAll(newClassObject);
			    	TimeUnit.SECONDS.sleep(1);	

			    	List<Subject> savedSubjects = subRepository.findAll();			    	
			    	// Then search in student list for each data, search for both class_name & class_arm , 
		    		// use those 2 attributes to search in ClassStream for corresponding IDs and save the IDs of ClassStream
			    	Map< String, List<ClassStream> > foundUniqueClass = new HashMap< String, List<ClassStream> >();
			    	for (StudentRequest c : pupRequest) {
			    		 String clsname = c.getClass_name(); 
					 	 String arm = c.getArm(); 
					 	 List<ClassStream> foundMatch = findWithTitleAndArm( clsname, arm, savedClasses );	
					 	 
					 	 if(foundMatch.size() > 0) {
					 		List<ClassStream> noduplicateclass = foundMatch.stream().distinct().collect(Collectors.toList());
					 		String mykey = c.getName().toString() + "_" + c.getGender().toString() + "_" + c.getRegno().toString();					 		
					 		foundUniqueClass.put( mykey , noduplicateclass );
					 	 }
			    	}
				 	 
			    	if(foundUniqueClass.size() > 0) {
			    		 
			    		 for (Map.Entry<String, List<ClassStream>> entry : foundUniqueClass.entrySet()) {
			    		        Student s = new Student();
			    		        
			    		        //save student first
			    		        String[] separateValues = entry.getKey().split("_");
			    		        String specialIdStudent = createUuid("student-", savedSchool.getSchId() );
			    		        
			    		        s.setId(specialIdStudent);
			    		        s.setName(separateValues[0]);
			    		        s.setGender(separateValues[1]);
			    		        s.setReg_no(separateValues[2]);
			    		        s.setSchool(savedSchool);
			    		        
			    		        Student savedValue = pupilRepository.save(s);
			    		        List<ClassStream> classFound = entry.getValue();
			    		        
			    		        String specialId = createUuid("enrollment-", savedSchool.getSchId() );
			    		        Enrollment e = new Enrollment(specialId, savedValue, classFound.get(0), savedCalendar, 1 );
			    		        enrollRepository.save(e);
			    		 }
			    	}
			    	
			    	
			    	for (TimetableRequest t : timeRequest) {	    		 
			    		 String clsname = t.getClass_name(); 
					 	 String arm = t.getArm(); 
					 	 String teac = t.getTea_name(); 
					 	 String sub = t.getSubject(); 
					 	 
					 	 List<ClassStream> foundMatchC = findWithTitleAndArm( clsname, arm, savedClasses );
					 	 List<Teacher> foundMatchT = findWithName( teac, savedTeachers );
					 	 List<Subject> foundMatchS = findWithSubName( sub, savedSubjects ); 
					 	 
					 	 if(foundMatchC.size() > 0 && foundMatchT.size() > 0 && foundMatchS.size() > 0 ) {
					 		List<ClassStream> noduplicateclass = foundMatchC.stream().distinct().collect(Collectors.toList());
					 		List<Teacher> noduplicateteacher = foundMatchT.stream().distinct().collect(Collectors.toList());
					 		List<Subject> noduplicatesubject = foundMatchS.stream().distinct().collect(Collectors.toList());
					 		
					 		String mytime = t.getTime().split(":").length == 2 ? (t.getTime() + ":00") : t.getTime();
					 		
					 		TimeTable time = new TimeTable();
					 		time.setClass_stream( noduplicateclass.get(0) );
					 		time.setTeacher( noduplicateteacher.get(0) );
					 		time.setSubject( noduplicatesubject.get(0) );
					 		time.setSchool( noduplicateteacher.get(0).getSchool() );
					 		time.setClass_name( noduplicateclass.get(0).getTitle() );
					 		time.setTea_name( noduplicateteacher.get(0).getFname() + " " + noduplicateteacher.get(0).getLname() );
					 		time.setSub_name( noduplicatesubject.get(0).getName() );
					 		time.setTime_of( mytime );
					 		time.setStatus(1);
					 		time.setCalendar(savedCalendar);
					 		time.setDay_of( dayToInteger.get( t.getDay() )  );
					 		String specialId = createUuid("timetable-", savedSchool.getSchId() );
					 		time.setId(specialId);
					 		timeRepository.save(time);
					 		//foundUniqueClassTwo. ( "classId" , noduplicateclass.get(0).getClsId() );/
					 	 }
					 	
			    	}	
			    	
			    	  	
			    	User u = modelMapper.map(userRequest, User.class);
			    	String specialIdUser = createUuid("user-", savedSchool.getSchId() );
			    	u.setPassword( passwordencoder.encode( userRequest.getPassword() ) );
			    	u.setRole(RoleName.PRINCIPAL);
			    	u.setId(specialIdUser);
			    	u.setStatus(1);
					u.setSchool(savedSchool);
			    
			    	Map<String, Object> attributes = new HashMap<>();
			    	Permissions perm = new Permissions( true, true, true, false);
			    	attributes.put("school", perm);
			    	attributes.put("teacher", perm);
			    	attributes.put("enrollment", perm);
			    	attributes.put("classroom", perm);
			    	attributes.put("calendar", perm);
			    	attributes.put("timetable", perm);
			    	attributes.put("attendance", perm);
			    	attributes.put("lessonnote", perm);
			    	attributes.put("user", perm);
		
			    	String jsonStr = gsonObj.toJson(attributes);		
			    	
			    	u.setPermissionsJSON(jsonStr);
			    	u.setEmail( userRequest.getEmail() );   	
			    
		    		userRepository.save(u); 
		    			    		
			    	TimeUnit.SECONDS.sleep(1);   
			    	
			    	// Create users for teachers and send them all a mail 
			    	for (Teacher t : savedTeachers) {	
			    		String from = "loyboy606@gmail.com";
			    		String to = t.getEmail();
			    		 
			    		//SimpleMailMessage message = new SimpleMailMessage();
			    		String specialIdUser2 = createUuid("user-", savedSchool.getSchId() );
			    		
			    		//String specialPassword = createUuidPassword();
						String specialPassword = "mypassword";
			    		
			    	//	System.out.println( " User details >>>  " + specialIdUsername + " >>> " + specialPassword);
			    		User _u = new User();
			    		if (t.getOffice().equalsIgnoreCase("Teacher")) {
							String specialIdUsername = createUuidUsername("teacher");
			    			_u.setId(specialIdUser2);
				    		_u.setUsername(specialIdUsername);
				    		_u.setStatus(1);
				    		_u.setEmail( t.getEmail() );//change
				    		_u.setName( t.getFname() + " " + t.getLname() );
				    		_u.setRole(RoleName.TEACHER);
				    		_u.setPassword( passwordencoder.encode( specialPassword ) );
				    		_u.setTeacher_id(t.getTeaId());
				    		_u.setSchool(t.getSchool());
				    		
				    		Map<String, Object> _attributes = new HashMap<>();
					    	
					    	_attributes.put("school",  new Permissions( true, false, false, false));
					    	_attributes.put("teacher", new Permissions( true, true, false, false));
					    	_attributes.put("enrollment", new Permissions( true, false, false, false));
					    	_attributes.put("classroom", new Permissions( true, false, false, false));
					    	_attributes.put("calendar", new Permissions( true, false, false, false));
					    	_attributes.put("timetable", new Permissions( true, false, false, false));
					    	_attributes.put("user", new Permissions( true, false, false, false));
					    	_attributes.put("attendance", new Permissions( true, true, false, true));
					    	_attributes.put("lessonnote", new Permissions( true, true, false, true));
					    	
					    	String jsonStr2 = gsonObj.toJson(_attributes);		
					    	
					    	_u.setPermissionsJSON(jsonStr2);
							userRepository.save(_u);
			    		}
			    		 
			    		
			    	}
					return true;
    			}
    		
    		return false;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		System.out.println( "Onboarding Error: " + e.getLocalizedMessage() );
    		return false;
    	}
    }

	/**
     * STEP 1: Initialize School, Calendar, and Admin User
     */
    @Transactional
    public Boolean registerSchoolAndAdmin(OnboardStepOneRequest.SchoolRequest schRequest, OnboardStepOneRequest.UserAccountRequest userRequest) {
        try {
            // 1. Save School
            School sch = modelMapper.map(schRequest, School.class);
            Optional<SchoolGroup> sg = schgroupRepository.findById(schRequest.getOwner());
            
            if (sg.isPresent()) {
                String uuid = UUID.randomUUID().toString();
                School prePersistSchool = prePersistFunction(sch); // Your existing helper
                String schStringId = prePersistSchool.getId();
                
                prePersistSchool.setOwner(sg.get());
                prePersistSchool.setStatus(1);
                // Custom ID logic from your code
                prePersistSchool.setId(schStringId + "-" + uuid.split("-")[4].substring(4));
                
                School savedSchool = schRepository.save(prePersistSchool);
                
                // 2. Create Default Calendar
                Calendar _cal = new Calendar();
                String myidcal = createUuid("calendar-", savedSchool.getSchId());
                _cal.setId(myidcal);
                _cal.setTerm(-99);
                _cal.setStartdate(parseTimestamp("1999-01-03 00:00:00"));
                _cal.setEnddate(parseTimestamp("1999-04-21 00:00:00"));
                _cal.setStatus(0); // 0 = Inactive/Setup mode
                _cal.setSession("1999/2000");
                _cal.setSchool(savedSchool);
                calRepository.save(_cal);

                // 3. Create Admin User (Principal)
                User u = new User();
                String specialIdUser = createUuid("user-", savedSchool.getSchId());
                
                // Map fields from accountRequest
                u.setName(userRequest.getName());
                u.setUsername(userRequest.getUsername());
                u.setEmail(userRequest.getEmail());
                u.setPassword(passwordencoder.encode(userRequest.getPassword()));
                
                u.setRole(RoleName.PRINCIPAL);
                u.setId(specialIdUser);
                u.setStatus(1);
                u.setSchool(savedSchool);

                // Set Permissions (Your existing logic)
                Map<String, Object> attributes = new HashMap<>();
                Permissions perm = new Permissions(true, true, true, false);
                attributes.put("school", perm);
                attributes.put("teacher", perm);
                attributes.put("enrollment", perm);
                attributes.put("classroom", perm);
                attributes.put("calendar", perm);
                attributes.put("timetable", perm);
                attributes.put("attendance", perm);
                attributes.put("lessonnote", perm);
                attributes.put("user", perm);
                
                u.setPermissionsJSON(gsonObj.toJson(attributes));
                userRepository.save(u);

                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }

    /**
     * STEP 2-5: Process Optional Lists (Classes, Teachers, Students, Timetable)
     */
    @Transactional
    public void processOnboardingData(School school, OnboardingStepTwoRequest request) {
        
        // 1. Process Classes (If present)
        if (request.getClassRequest() != null && !request.getClassRequest().isEmpty()) {
            saveClassrooms(school, request.getClassRequest());
        }

        // 2. Process Teachers (If present)
        if (request.getTeaRequest() != null && !request.getTeaRequest().isEmpty()) {
            saveTeachers(school, request.getTeaRequest());
        }

        // 3. Process Students (If present)
        // Note: Students require Classes to exist.
        if (request.getStudentRequest() != null && !request.getStudentRequest().isEmpty()) {
            saveStudents(school, request.getStudentRequest());
        }

        // 4. Process Timetable (If present)
        // Note: Requires Classes, Teachers, and Subjects.
        if (request.getTimetableRequest() != null && !request.getTimetableRequest().isEmpty()) {
            saveTimetable(school, request.getTimetableRequest());
        }
    }

	public OnboardingStatusResponse getOnboardingStatus(School school) {
				
		if (school == null) {
			return new OnboardingStatusResponse(0, 0, 0, 0, false, false, false, false, false);
		}

		// 2. Check counts
		// School Details is implicitly true if we have a School object
		boolean isSchoolFilled = true; 
		
		boolean isClassFilled = classRepository.countBySchool(school) > 0;
		boolean isTeacherFilled = teaRepository.countBySchool(school) > 0;
		boolean isStudentFilled = pupilRepository.countBySchool(school) > 0;
		boolean isTimetableFilled = timeRepository.countBySchool(school) > 0;

		long classCount = classRepository.countBySchool(school);
    	long teaCount = teaRepository.countBySchool(school);
    	long stuCount = pupilRepository.countBySchool(school);
    	long timeCount = timeRepository.countBySchool(school);

		return new OnboardingStatusResponse(
			classCount,
			teaCount,
			stuCount,
			timeCount,
			isSchoolFilled,
			isClassFilled,
			isTeacherFilled,
			isStudentFilled,
			isTimetableFilled
		);
	}
    // --- HELPER METHODS FOR MODULARITY ---

    private void saveClassrooms(School school, List<ClassRequest> classRequests) {
        List<ClassStream> newClassObject = classRequests.stream().map(c -> {
            ClassStream cs = modelMapper.map(c, ClassStream.class);
            String myid = createUuid("classroom-", school.getSchId());
            cs.setId(myid);
            cs.setStatus(1);
            cs.setSchool(school);
            return cs;
        }).collect(Collectors.toList());
        classRepository.saveAll(newClassObject);
    }

    private void saveTeachers(School school, List<TeacherRequest> teaRequests) {
        List<Teacher> newTeaObject = teaRequests.stream().map(t -> {
            Teacher tea = modelMapper.map(t, Teacher.class);
            String myid = createUuid("teacher-", school.getSchId());
            tea.setId(myid);
            tea.setStatus(1);
            tea.setSchool(school);
            return tea;
        }).collect(Collectors.toList());
        
        List<Teacher> savedTeachers = teaRepository.saveAll(newTeaObject);
        
        // Create User Accounts for Teachers immediately
        createTeacherUsers(savedTeachers);
    }

    private void createTeacherUsers(List<Teacher> teachers) {
        for (Teacher t : teachers) {
            // Basic check if user already exists could be added here
            String specialIdUser = createUuid("user-", t.getSchool().getSchId());
            String specialPassword = "mypassword"; // Or generate random

            if (t.getOffice().equalsIgnoreCase("Teacher")) {
                User _u = new User();
                String specialIdUsername = createUuidUsername("teacher"); // Your helper
                _u.setId(specialIdUser);
                _u.setUsername(specialIdUsername);
                _u.setStatus(1);
                _u.setEmail(t.getEmail());
                _u.setName(t.getFname() + " " + t.getLname());
                _u.setRole(RoleName.TEACHER);
                _u.setPassword(passwordencoder.encode(specialPassword));
                _u.setTeacher_id(t.getTeaId());
                _u.setSchool(t.getSchool());

                // Set Teacher Permissions
                Map<String, Object> _attributes = new HashMap<>();
                _attributes.put("school", new Permissions(true, false, false, false));
                _attributes.put("teacher", new Permissions(true, true, false, false));
                _attributes.put("enrollment", new Permissions(true, false, false, false));
                _attributes.put("classroom", new Permissions(true, false, false, false));
                _attributes.put("calendar", new Permissions(true, false, false, false));
                _attributes.put("timetable", new Permissions(true, false, false, false));
                _attributes.put("user", new Permissions(true, false, false, false));
                _attributes.put("attendance", new Permissions(true, true, false, true));
                _attributes.put("lessonnote", new Permissions(true, true, false, true));

                _u.setPermissionsJSON(gsonObj.toJson(_attributes));
                userRepository.save(_u);
            }
        }
    }

    private void saveStudents(School school, List<StudentRequest> pupRequests) {
        // We need to fetch existing classes to link students
        // Note: Use school.getId() to ensure we only get classes for this school
        List<ClassStream> existingClasses = classRepository.findBySchool(school);
        Calendar currentCalendar = calRepository.findBySchool(school).get(0); // Assuming you have this query

        for (StudentRequest c : pupRequests) {
            String clsname = c.getClass_name(); 
            String arm = c.getArm();         
            
            // Your logic: findWithTitleAndArm
            List<ClassStream> foundMatch = findWithTitleAndArm(clsname, arm, existingClasses);

            if (!foundMatch.isEmpty()) {
                Student s = new Student();
                String specialIdStudent = createUuid("student-", school.getSchId());
                
                s.setId(specialIdStudent);
                s.setName(c.getName());
                s.setGender(c.getGender());
                s.setReg_no(c.getRegno());
                s.setSchool(school);
                
                Student savedValue = pupilRepository.save(s);
                
                // Enroll student
                String specialId = createUuid("enrollment-", school.getSchId());
                Enrollment e = new Enrollment(specialId, savedValue, foundMatch.get(0), currentCalendar, 1);
                enrollRepository.save(e);
            }
        }
    }

    private void saveTimetable(School school, List<TimetableRequest> timeRequests) {
        List<ClassStream> savedClasses = classRepository.findBySchool(school);
        List<Teacher> savedTeachers = teaRepository.findBySchool(school);
        List<Subject> savedSubjects = subRepository.findAll(); // Assuming subjects are global?
        Calendar currentCalendar = calRepository.findBySchool(school).get(0); 
        
        // Day mapping
        HashMap<String, Integer> dayToInteger = new HashMap<>();
        dayToInteger.put("Monday", 1);
        dayToInteger.put("Tuesday", 2);
        dayToInteger.put("Wednesday", 3);
        dayToInteger.put("Thursday", 4);
        dayToInteger.put("Friday", 5);
        dayToInteger.put("Saturday", 6);
        dayToInteger.put("Sunday", 7); // Added Sunday just in case

        for (TimetableRequest t : timeRequests) {
            String clsname = t.getClass_name();
            String arm = t.getArm();
            String teac = t.getTea_name();
            String sub = t.getSubject();
            String day = t.getDay(); // Ensure casing matches map keys (e.g. "Monday" vs "monday")

            // Case-insensitive day lookup
            Integer dayInt = dayToInteger.get(day.substring(0, 1).toUpperCase() + day.substring(1).toLowerCase());
            if(dayInt == null) dayInt = 1;

            List<ClassStream> foundMatchC = findWithTitleAndArm(clsname, arm, savedClasses);
            List<Teacher> foundMatchT = findWithName(teac, savedTeachers);
            List<Subject> foundMatchS = findWithSubName(sub, savedSubjects);

            if (!foundMatchC.isEmpty() && !foundMatchT.isEmpty() && !foundMatchS.isEmpty()) {
                ClassStream selectedClass = foundMatchC.get(0);
                Teacher selectedTeacher = foundMatchT.get(0);
                Subject selectedSubject = foundMatchS.get(0);

                TimeTable time = new TimeTable();
                String specialId = createUuid("timetable-", school.getSchId());
                
                time.setId(specialId);
                time.setClass_stream(selectedClass);
                time.setTeacher(selectedTeacher);
                time.setSubject(selectedSubject);
                time.setSchool(school);
                
                // Store Denormalized names (as per your legacy code)
                time.setClass_name(selectedClass.getTitle());
                time.setTea_name(selectedTeacher.getFname() + " " + selectedTeacher.getLname());
                time.setSub_name(selectedSubject.getName());
                
                time.setTime_of(t.getTime());
                time.setDay_of(dayInt);
                time.setStatus(1);
                time.setCalendar(currentCalendar);

                timeRepository.save(time);
            }
        }
    }

	/*public Boolean onboardNewSchoolTwo( SchoolRequest schRequest, List<TeacherRequest> teaRequest, List<StudentRequest> pupRequest, List<ClassRequest> classRequest, List<TimetableRequest> timeRequest, UserAccountRequest userRequest ) {
	}*/

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
    
    private List<ClassStream> findWithTitleAndArm(String cname, String arm, List<ClassStream> classstream) {
    	List<ClassStream> newList = new ArrayList<ClassStream>();
    	for (ClassStream c : classstream) {
    		
    		if ( c.getTitle().toLowerCase().contains( cname.toLowerCase() ) && c.getExt().toLowerCase().contains( arm.toLowerCase() )  ) {
    				newList.add(c);
    		    }
    	}
        return newList;
    }
    
    private List<Teacher> findWithName(String tname, List<Teacher> tea) {
    	List<Teacher> newList = new ArrayList<Teacher>();  	
    	for (Teacher t : tea) {
    		String fullname = t.getFname() + " " + t.getLname();
    		
    		if ( fullname.toLowerCase().contains( tname.toLowerCase() ) ) {
    		        newList.add(t);
    		    }
    	}
        return newList;
    }
    
    private List<Subject> findWithSubName(String subname, List<Subject> sub) {
    	List<Subject> newList = new ArrayList<Subject>();
    	for (Subject s : sub) {
    		
    		if ( s.getName().toLowerCase().contains( subname.toLowerCase() ) ) {
    		        newList.add(s);
    		    }
    	}
        return newList;
    }
    
    private java.sql.Timestamp parseTimestamp(String timestamp) {
	    try {
	        return new Timestamp(DATE_TIME_FORMAT.parse(timestamp).getTime());
	    } catch (ParseException e) {
	        throw new IllegalArgumentException(e);
	    }
 }
    
    private School prePersistFunction(School sch){
    	School schoolDefault = new School();
    	schoolDefault = sch;
    	HashMap<String, String> schids = new HashMap<String, String>();
    	schids.put("abak", "96201");
    	schids.put("eastern obolo", "96202");
    	schids.put("eket", "96203");
    	schids.put("esit eket", "96204");
    	schids.put("essien udim", "96205");
    	schids.put("etim ekpo", "96206");
    	schids.put("etinan", "96207");
    	schids.put("ibeno", "96208");
    	schids.put("ibesikpo asutan", "96209");
    	schids.put("ibiono ibom", "96210");
    	schids.put("ika", "96211");
    	schids.put("ikono", "96212");
    	schids.put("ikot abasi", "96213");
    	schids.put("ikot ekpene", "96214");
    	schids.put("ini", "96215");
    	schids.put("itu", "96216");
    	schids.put("mbo", "96217");
    	schids.put("mkpat enin", "96218");
    	schids.put("nsit atai", "96219");
    	schids.put("nsit ibom", "96220");
    	schids.put("nsit ubium", "96221");
    	schids.put("obot akara", "96222");
    	schids.put("okobo", "96223");
    	schids.put("onna", "96224");
    	schids.put("oron", "96225");
    	schids.put("oruk anam", "96226");
    	schids.put("udung uko", "96227");
    	schids.put("ukanafun", "96228");
    	schids.put("uruan", "96229");
    	schids.put("urue offong oruko", "96230"); 
    	schids.put("uyo", "96231");//end akwa bom
		schids.put("agege", "96001");
		schids.put("alimosho", "96002");
		schids.put("apapa", "96003");
		schids.put("ifako-ijaye", "96004");
		schids.put("ikeja", "96005");
		schids.put("kosofe", "96006");
		schids.put("mushin", "96007");
		schids.put("oshodi-isolo", "96008");
		schids.put("shomolu", "96009");
		schids.put("eti-osa", "96010");
		schids.put("lagos-island", "96011");
		schids.put("lagos-mainland", "96012");
		schids.put("surulere", "96013");
		schids.put("ojo", "96014");
		schids.put("ajeromi-ifelodun", "96015");
		schids.put("amuwo-odofin", "96016");
		schids.put("badagry", "96017");
		schids.put("ikorodu", "96018");
		schids.put("ibeju-lekki", "96019");
		schids.put("epe", "96020");//end lagos
		schids.put("aba-north", "96301");
		schids.put("aba-south", "96302");
		schids.put("arochukwu", "96303");
		schids.put("bende", "96304");
		schids.put("ikwuano", "96305");
		schids.put("isiala-ngwa-north", "96306");
		schids.put("isiala-ngwa-south", "96307");
		schids.put("isiukwuato", "96308");
		schids.put("obingwa", "96309");
		schids.put("ohafia", "96310");
		schids.put("osisioma-ngwa", "96311");
		schids.put("ugwunagbo", "96312");
		schids.put("ukwa-east", "96313");
		schids.put("ukwa-west", "96314");
		schids.put("umuahia-north", "96315");
		schids.put("umuahia-south", "96316");
		schids.put("umu-neochi", "96317");	
		schids.put("bwari", "96401");	
		schids.put("muncipal", "96402");	
		schids.put("amac", "96403");
		schids.put("abaji", "96404");	
		schids.put("kuje", "96405");
		schids.put("kwali", "96406");		
		schids.put("gwagwalada", "96407");	
		
    	String code = schids.get( schoolDefault.getLga().toLowerCase() );
		schoolDefault.setLga_code(code);
		
        if( schoolDefault.getOperator().toLowerCase().equals("government") ){
            String newId = "100000";
            String schId = code + newId;            
            schoolDefault.setId(schId);           
        }
        else if( schoolDefault.getOperator().toLowerCase().equals("private single") ){
            String newId = "200000";
            String schId = code + newId;
            schoolDefault.setId(schId);            
        }	        
        else if( schoolDefault.getOperator().toLowerCase().equals("private group") ){
            String newId = "300000";
            String schId = code + newId;
            schoolDefault.setId(schId);         
        }
        
        return schoolDefault;
    }
    
   /* ExecutorService executor = Executors.newSingleThreadExecutor();

    executor.submit(new Runnable() {jnjkkjkjkjk
        public void run() {
            // Perform the JPA save operation here
            entityManager.persist(yourModel); // Replace entityManager with your JPA entity manager
        }
    });

    executor.shutdown(); // Shutdown the executor service after task completion*/
}

//username-b3522ba6 >>> 89226cc
//username-9e015f43 >>> 1f8b8e5
//username-9bacd8fb >>> cddb102
//evaluator1 >>> standevaluator
