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
	
    public Boolean onboardNewSchool( SchoolRequest schRequest, List<TeacherRequest> teaRequest, List<StudentRequest> pupRequest, List<ClassRequest> classRequest, List<TimetableRequest> timeRequest, UserAccountRequest userRequest ) {
    	ModelMapper modelMapper = new ModelMapper();    	
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
		    		prePersistSchool.setOwner(sg.get());
		    		prePersistSchool.setStatus(1);
		    		prePersistSchool.setSri(0);
		    		prePersistSchool.setId( "school" + "-" + uuid.split("-")[4].substring(4) );
		    		School savedSchool = schRepository.save(prePersistSchool);
		    		TimeUnit.SECONDS.sleep(1);    		
		    		
		    		//Create a calendar
		    		Calendar _cal = new Calendar();
		    		String myidcal = createUuid("calendar-", savedSchool.getSchId() );
		    		_cal.setId(myidcal);
		    		_cal.setTerm(1);
		    		_cal.setStartdate( parseTimestamp( "2023-01-03 00:00:00" ) );
		    		_cal.setEnddate( parseTimestamp( "2023-04-21 00:00:00" ) );
		    		_cal.setStatus(1);
		    		_cal.setSession("2022/2023");
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
			    	
			    	//add to Calendar
			    	  	
			    	User u = modelMapper.map(userRequest, User.class);
			    	String specialIdUser = createUuid("user-", savedSchool.getSchId() );
			    	u.setPassword( passwordencoder.encode( userRequest.getPassword() ) );
			    	u.setRole(RoleName.PROPRIETOR);
			    	u.setId(specialIdUser);
			    	u.setStatus(1);
			    
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
			    	
			    	Teacher _tea = new Teacher();
		    		String myidcal2 = createUuid("teacher-", savedSchool.getSchId() );
		    		_tea.setId(myidcal2);
		    		_tea.setStatus(1);
		    		_tea.setSchool(savedSchool);
		    	
		    		_tea.setFname(userRequest.getName().split(" ")[0]);
		    		_tea.setLname(userRequest.getName().split(" ")[1]);
		    		_tea.setGender( "M/F" );
		    		_tea.setOffice("Proprietor");
		    		_tea.setEmail( userRequest.getEmail() );
		    		
		    		Teacher savedProprietor = teaRepository.save(_tea);
		    		
		    		//Save the proprietor as a Teacher oBject		    		
		    		savedTeachers.add(savedProprietor);
			    	TimeUnit.SECONDS.sleep(1);   
			    	
			    	// Create users for teachers and send them all a mail 
			    	for (Teacher t : savedTeachers) {	
			    		String from = "loyboy606@gmail.com";
			    		String to = t.getEmail();
			    		 
			    		SimpleMailMessage message = new SimpleMailMessage();
			    		String specialIdUser2 = createUuid("user-", savedSchool.getSchId() );
			    		String specialIdUsername = createUuidUsername("username");
			    		String specialPassword = createUuidPassword();
			    		
			    		System.out.println( " User details >>>  " + specialIdUsername + " >>> " + specialPassword);
			    		User _u = new User();
			    		if (t.getOffice().equalsIgnoreCase("Teacher")) {
			    			_u.setId(specialIdUser2);
				    		_u.setUsername(specialIdUsername);
				    		_u.setStatus(1);
				    		_u.setEmail( t.getEmail() );
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
			    		}
			    		else if (t.getOffice().equalsIgnoreCase("Principal")) {
			    			_u.setId(specialIdUser2);
				    		_u.setUsername(specialIdUsername);
				    		_u.setStatus(1);
				    		_u.setEmail( t.getEmail() );
				    		_u.setName( t.getFname() + " " + t.getLname() );
				    		_u.setRole(RoleName.PRINCIPAL);
				    		_u.setPassword( passwordencoder.encode( specialPassword ) );
				    		_u.setPrincipal_id(t.getTeaId());
				    		_u.setSchool(t.getSchool());
				    		
				    		Map<String, Object> _attributes = new HashMap<>();
					    	
					    	_attributes.put("school",  new Permissions( true, true, false, false));
					    	_attributes.put("teacher", new Permissions( true, true, false, true));
					    	_attributes.put("enrollment", new Permissions( true, true, false, true));
					    	_attributes.put("classroom", new Permissions( true, true, false, true));
					    	_attributes.put("calendar", new Permissions( true, true, false, true));
					    	_attributes.put("timetable", new Permissions( true, true, false, true));
					    	_attributes.put("user", new Permissions( true, true, false, true));
					    	_attributes.put("attendance", new Permissions( true, true, false, false));
					    	_attributes.put("lessonnote", new Permissions( true, true, false, false));
					    	
					    	String jsonStr2 = gsonObj.toJson(_attributes);		
					    	
					    	_u.setPermissionsJSON(jsonStr2);
			    		}
			    		
			    		else if (t.getOffice().equalsIgnoreCase("Proprietor")) {
			    			_u.setId(specialIdUser2);
				    		_u.setUsername(specialIdUsername);
				    		_u.setStatus(1);
				    		_u.setEmail( t.getEmail() );
				    		_u.setName( t.getFname() + " " + t.getLname() );
				    		_u.setRole(RoleName.PROPRIETOR);
				    		_u.setPassword( passwordencoder.encode( specialPassword ) );
				    		_u.setProprietor_id(t.getTeaId());
				    		_u.setSchool(t.getSchool());
				    		
				    		Map<String, Object> _attributes = new HashMap<>();
					    	
					    	_attributes.put("school",  new Permissions( true, true, false, false));
					    	_attributes.put("teacher", new Permissions( true, true, false, true));
					    	_attributes.put("enrollment", new Permissions( true, true, false, false));
					    	_attributes.put("classroom", new Permissions( true, true, false, false));
					    	_attributes.put("calendar", new Permissions( true, false, false, false));
					    	_attributes.put("timetable", new Permissions( true, false, false, false));
					    	_attributes.put("user", new Permissions( true, true, false, true));
					    	_attributes.put("attendance", new Permissions( true, true, false, false));
					    	_attributes.put("lessonnote", new Permissions( true, true, false, false));
					    	
					    	String jsonStr2 = gsonObj.toJson(_attributes);		
					    	
					    	_u.setPermissionsJSON(jsonStr2);
			    		}   		
			    						    	
			    		/*message.setFrom(from);
			    		message.setTo(to);
			    		message.setSubject("Welcome to Standbasis :: You are a Teacher from " + savedSchool.getName() + " school" );
			    		message.setText("Hello sir/mrs! This is to congratulate you on your successful onboarding process into the Standbasis school standards management system. Your login details are: " + System.lineSeparator() + "User: " + specialIdUsername +  System.lineSeparator() + "Password: " + specialPassword  );
			    		mailSender.send(message);*/
			    		
			    		userRepository.save(_u);
			    		TimeUnit.SECONDS.sleep(1);   
			    		
			    	}
					return true;
    			}
    		
    		return false;
    	}
    	catch (Exception e) {
    		System.out.println( "Onboarding Error: " + e.getMessage());
    		return false;
    	}
    }

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
    	schids.put("urue offong oruko", "96230"); //
    	schids.put("uyo", "96231");
    	
    	String code = schids.get( schoolDefault.getLga().toLowerCase() );
    	System.out.println( "Code ::" + schoolDefault.getOperator().toLowerCase() );
    	
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
}

//username-b3522ba6 >>> 89226cc
//username-9e015f43 >>> 1f8b8e5
//username-9bacd8fb >>> cddb102
