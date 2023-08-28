package basepackage.stand.standbasisprojectonev1.scheduler;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import basepackage.stand.standbasisprojectonev1.model.Assessment;
import basepackage.stand.standbasisprojectonev1.model.Attendance;
import basepackage.stand.standbasisprojectonev1.model.Calendar;
import basepackage.stand.standbasisprojectonev1.model.ClassStream;
import basepackage.stand.standbasisprojectonev1.model.Enrollment;
import basepackage.stand.standbasisprojectonev1.model.Lessonnote;
import basepackage.stand.standbasisprojectonev1.model.LessonnoteManagement;
import basepackage.stand.standbasisprojectonev1.model.TimeTable;
import basepackage.stand.standbasisprojectonev1.payload.ApiResponse;
import basepackage.stand.standbasisprojectonev1.repository.AssessmentRepository;
import basepackage.stand.standbasisprojectonev1.repository.AttendanceRepository;
import basepackage.stand.standbasisprojectonev1.repository.CalendarRepository;
import basepackage.stand.standbasisprojectonev1.repository.ClassStreamRepository;
import basepackage.stand.standbasisprojectonev1.repository.EnrollmentRepository;
import basepackage.stand.standbasisprojectonev1.repository.LessonnoteManagementRepository;
import basepackage.stand.standbasisprojectonev1.repository.LessonnoteRepository;
import basepackage.stand.standbasisprojectonev1.repository.TimetableRepository;
import basepackage.stand.standbasisprojectonev1.service.AttendanceService;
import basepackage.stand.standbasisprojectonev1.service.CalendarService;
import basepackage.stand.standbasisprojectonev1.service.ClassService;
import basepackage.stand.standbasisprojectonev1.service.EnrollmentService;
import basepackage.stand.standbasisprojectonev1.service.LessonnoteManagementService;
import basepackage.stand.standbasisprojectonev1.service.TimetableService;

@Component
public class MyScheduler {
	@Autowired
	AttendanceService service;
	
	@Autowired
	ClassService classservice;
	
	@Autowired
	EnrollmentService enrolservice;
	
	@Autowired
	CalendarService calservice;
	
	@Autowired
	TimetableService timeservice;
	
	@Autowired
	LessonnoteManagementService serviceManagement;	 
	
	private final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Autowired		
    private TimetableRepository timeRepository;
	
	@Autowired		
    private AttendanceRepository attRepository;	
	
	@Autowired		
    private LessonnoteRepository lsnRepository;
	
	@Autowired		
    private LessonnoteManagementRepository lsnmanageRepository;	
	
	@Autowired		
    private AssessmentRepository assRepository;
	
	@Autowired
	private CalendarRepository calRepository;
	
	@Autowired
	private ClassStreamRepository clsRepository;
	
	@Autowired
	private EnrollmentRepository enrolRepository;
	
	// every 
	@Scheduled(cron = "0 59 * * * *")
    public void switchLessonnoteToClose() {
		
		// check the current calendars from schools by active ones
		// get the lessonnotes and assessments
		// check if assessments are filled in each approved lessonnote for all 3 assessment types
		// then change the lsn note "can_close" to  if it is so
		
		List<Calendar> calendars = calservice.findByActive();
		
		for ( Calendar cal: calendars) {
						
			if ( parseTimestamp(todayDate()).compareTo(cal.getEnddate()) > 0 ) {
				
				List<Lessonnote> allLessonnote = lsnRepository.findByCalendar(cal);
			    List<Assessment> allAssessment = assRepository.findByCalendar(cal);
				
				for ( Lessonnote lsn : allLessonnote ) {
					List<LessonnoteManagement> list = serviceManagement.findByLessonnote(lsn.getLessonnoteId());
					LessonnoteManagement lsnmanage = list.get(0);
					int totalScore = ( (lsnmanage.getSubmission() != null ? lsnmanage.getSubmission() : 0) + (lsnmanage.getQuality() != null ? lsnmanage.getQuality() : 0) + (lsnmanage.getManagement() != null ? lsnmanage.getManagement() : 0))/4;
					lsnmanage.setScore(totalScore);
						
					lsnmanageRepository.save(lsnmanage);
					
					if ( (lsn.getCan_close() == null || lsn.getCan_close() == false) && ( lsn.getApproval() != null ) ) {
						
						List<Assessment> allClswork = allAssessment.stream().filter(a -> ( a.getLsn().getLessonnoteId() == lsn.getLessonnoteId() && a.get_type().equals("clw") ) ).collect(Collectors.toList() );
						List<Assessment> allHomework = allAssessment.stream().filter(a -> (a.getLsn().getLessonnoteId() == lsn.getLessonnoteId() && a.get_type().equals("tst") ) ).collect(Collectors.toList() );
						List<Assessment> allTest = allAssessment.stream().filter(a -> (a.getLsn().getLessonnoteId() == lsn.getLessonnoteId() && a.get_type().equals("hwk") ) ).collect(Collectors.toList() );
						
						int allclasworksize = allClswork.size();
						int allhomeworksize = allHomework.size();
						int alltestsize = allTest.size();
						
						List<Assessment> allClsworkCheck = allAssessment.stream().filter(a -> ( a.getLsn().getLessonnoteId() == lsn.getLessonnoteId() && a.get_type().equals("clw") && a.getScore() != null ) ).collect(Collectors.toList() );
						List<Assessment> allHomeworkCheck = allAssessment.stream().filter(a -> (a.getLsn().getLessonnoteId() == lsn.getLessonnoteId() && a.get_type().equals("tst") && a.getScore() != null ) ).collect(Collectors.toList() );
						List<Assessment> allTestCheck = allAssessment.stream().filter(a -> (a.getLsn().getLessonnoteId() == lsn.getLessonnoteId() && a.get_type().equals("hwk") && a.getScore() != null ) ).collect(Collectors.toList() );
						
						int allclasworksizeCheck = allClsworkCheck.size();
						int allhomeworksizeCheck = allHomeworkCheck.size();
						int alltestsizeCheck = allTestCheck.size();
						
						boolean classworkCheck = allclasworksizeCheck == allclasworksize;
						boolean homeworkCheck = allhomeworksizeCheck == allhomeworksize;
						boolean testCheck = alltestsizeCheck == alltestsize;
						
						if ( classworkCheck && homeworkCheck && testCheck ) {
							List<LessonnoteManagement> listdd = serviceManagement.findByLessonnote(lsn.getLessonnoteId());
							LessonnoteManagement lsnmanagedd = listdd.get(0);
							lsn.setCan_close(true);
							lsnRepository.save(lsn);
							
							int averageClasswork = (int) allClsworkCheck.stream()
						                .mapToInt(Assessment::getScore)
						                .average()
						                .orElse(0.0);
							int averageHomework = (int) allHomeworkCheck.stream()
					                .mapToInt(Assessment::getScore)
					                .average()
					                .orElse(0.0);
							int averageTest = (int) allTestCheck.stream()
					                .mapToInt(Assessment::getScore)
					                .average()
					                .orElse(0.0);//
							lsnmanagedd.setSub_perf_classwork(averageClasswork);
							lsnmanagedd.setSub_perf_homework(averageHomework);
							lsnmanagedd.setSub_perf_test(averageTest);
							int averageAssessment = (averageClasswork+averageHomework+averageTest)/3;
							int totalScoredd = (averageAssessment + (lsnmanage.getSubmission() != null ? lsnmanage.getSubmission() : 0) + (lsnmanage.getQuality() != null ? lsnmanage.getQuality() : 0) + (lsnmanage.getManagement() != null ? lsnmanage.getManagement() : 0))/4;
							lsnmanage.setScore(totalScoredd);
							
							lsnmanageRepository.save(lsnmanage);
						}
						else {
							 
							 
						}
					}
				}			
					
			}
		}
		
	}
	
	@Scheduled(cron = "0 50 12 * * *")
    public void switchToNewTerm() {
		
	try {
	    // get all the calendars that are active
		List<Calendar> calendars =	calservice.findByActive();
	
		for ( Calendar cal: calendars) {
						
			if ( parseTimestamp(todayDate()).compareTo(cal.getEnddate()) > 0 ) {
				Timestamp enddate = null; 
				Timestamp startdate = null;
				try {
					enddate = addDays(1, cal.getEnddate());
					startdate = addDays(1 , cal.getStartdate());
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}				 
				
				// the term has ended for that School
				// then, get all the enrolments in that school
				List<Enrollment> allEnrols = enrolservice.getEnrollmentsByCalendar(cal.getCalendarId());
				List<TimeTable> allTimetables = timeservice.getTimetablesByCalendar(cal.getCalendarId());
				// Create a new Calendar object
				Calendar new_cal = new Calendar();
				String myidcal = createUuid("calendar-", cal.getSchool().getSchId() );
				new_cal.setSchool(cal.getSchool());
				new_cal.setId(myidcal);
				new_cal.setTerm(-99); //Moved over from active calendar
				new_cal.setSession(null);
				new_cal.setStatus(-1);// scheduled status, to be activated manually
				Calendar savedCalendar = calRepository.save(new_cal);
				
				//old calendar, reset to inactive
				cal.setStatus(0);
				cal.setEnddate(enddate);
				cal.setStartdate(startdate);
				calRepository.save(cal);
				
				for ( Enrollment e: allEnrols) {
					ClassStream myclassroom = clsRepository.findById(e.getClassstream().getClsId()).get();
					//System.out.println(" Class stream schid is here: " + e.getClassstream().getClsId() );
					ClassStream next_classroom = null; 
										
					List<ClassStream> allclasses = clsRepository.findBySchool( myclassroom.getSchool() );
					List<ClassStream> allclasses_filtered = allclasses.stream().filter(c -> (c.getClass_index() == myclassroom.getClass_index() + 1) ).collect(Collectors.toList());
					if (allclasses_filtered.size() > 0) {
						next_classroom = allclasses_filtered.get(0);
					}
					
					if (e.getStatus() == 1) {
						Enrollment newEnrol = new Enrollment();						
						newEnrol.setCalendar(savedCalendar);
						newEnrol.setSession_count( e.getSession_count() != null ? (e.getSession_count() + 1) : 2 );
						newEnrol.setClassstream( next_classroom );
						newEnrol.setStudent(e.getStudent());
						newEnrol.setId(e.getId());
						newEnrol.setStatus(1);
						enrolRepository.save(newEnrol);
					}	
					
					//Also roll over the timetable					
					e.setStatus(-99);
					enrolRepository.save(e);
				}
				
				for ( TimeTable tt: allTimetables) {
					if (tt.getStatus() == 1) {
						TimeTable newTime = new TimeTable();
						newTime.setId(tt.getId());
						newTime.setCalendar(savedCalendar);
						newTime.setSchool(tt.getSchool());
						newTime.setTeacher(tt.getTeacher());
						newTime.setClass_stream(tt.getClass_stream());
						newTime.setSubject(tt.getSubject());
						newTime.setTime_of(tt.getTime_of());
						newTime.setDay_of(tt.getDay_of());
						newTime.setStatus(1);
						timeRepository.save(newTime);
					}
					
					tt.setStatus(-99);
					timeRepository.save(tt);
				}
				
			
				
			}
		}		
		
	 }
	 catch (Exception ex) {
		ex.printStackTrace();
     }
		
	}
	//@Autowired	
	//private CalendarRepository calRepository;
	
	// "0 0/10 * * * *" - 10 minutes interval
	// "0 0 0 * * *" - Everyday at 0:00
	@Scheduled(cron = "15 51 12 * * *")
    public void insertAttendances() {       
		
		//Check what day of the week is this
		java.util.Calendar calendar = java.util.Calendar.getInstance();
	    int day = calendar.get(java.util.Calendar.DAY_OF_WEEK);
	    int daynew = day - 1;
	    Map<Integer, String> daysOfWeek = new HashMap<>();
	    daysOfWeek.put(1, "MONDAY");
	    daysOfWeek.put(2, "TUESDAY"); 
	    daysOfWeek.put(3, "WEDNESDAY");
	    daysOfWeek.put(4, "THURSDAY");
	    daysOfWeek.put(5, "FRIDAY");
	    daysOfWeek.put(6, "SATURDAY");
	    daysOfWeek.put(0, "SUNDAY");
		
	    //Get Timetable data for this day with Current calendar
	    List<TimeTable> tt = timeRepository.findByActiveCalendar(1);
	    
	    //Filter those that the day rhymes with today
	    List<TimeTable> ttnew = tt.stream().filter(t -> t.getDay_of() == daynew ).collect(Collectors.toList());
	    
	    System.out.println("Insert Attendances with timetable " + daynew );
	    
	    for (TimeTable it : ttnew) {  	
	    	if ( it.getCalendar().getEnddate().compareTo( parseTimestamp(todayDate()) ) > 0 ) {
	    		
	    		System.out.println( " Insert Attendances with Cron job 2 " );
	    		Attendance att = new Attendance();
		    	att.set_desc( it.getSub_name() + "_" + it.getClass_name() + "_" + daysOfWeek.get(daynew) );
		    	att.set_date( parseTimestamp(todayDate()) );
		    	att.setDone(0);
		    	att.setTeacher( it.getTeacher());
		    	att.setCalendar( it.getCalendar());
		    	att.setTimetable(it);		    	
		    	attRepository.save(att); 
		    	
	    	}
	    }
	    
	    System.out.println("Insert Attendances with Cron job");
		
    }	
	
	// "0 0 0 * * 0" -- once a week
	// 0 0 0 ? * WED
	//@SuppressWarnings("deprecation")
	@Scheduled(cron = "15 52 12 * * *")
    public void insertLessonnotes() {
		
		 Map<Integer, String> classMap = new HashMap<>();
		 classMap.put(7, "JSS1");
		 classMap.put(8, "JSS2"); 
		 classMap.put(9, "JSS3");
		 classMap.put(10, "SS1");
		 classMap.put(11, "SS2");
		 classMap.put(12, "SS3");
		    
		 // Get Timetable data for this day with Current calendar
	    List<TimeTable> tt = timeRepository.findByActiveCalendar(1);
	    
	    // Get Unique timetable data based on subject and Teacher values
	    List<TimeTable> ttnew = tt.stream()
	            .collect(Collectors.toMap(
	                    obj -> Arrays.asList(obj.getSubject(), obj.getTeacher()),  // Composite key
	                    Function.identity(),  // Keep the original object
	                    (obj1, obj2) -> obj1  // Merge function (in case of duplicate keys)
	            ))
	            .values()
	            .stream()
	            .collect(Collectors.toList());
	    
        
	    for (TimeTable it : ttnew) {	    	
	    	
	    	 	//change, check if calendar is active and lsn_start is 0
		    	//System.out.println("Calendar Timetable ID >> " + it.getTimeId() );
	    		Timestamp lsn_start_date = it.getCalendar().getLsnstartdate();
	    		for ( int i = 1 ; i < 13; i ++ ) { //Number of weeks
	    			try {
	    				int subDays = 7 * i;
	    				int closDays = 21 * i;
	    				int principalDays = 14 * i;
						Timestamp lastSubmissionDate = addDays(subDays,lsn_start_date); // 48 hours after this date, it should block submission
						Timestamp lastClosureDate = addDays(closDays,lsn_start_date);//48 hours after , it should be set as unclosed
						Timestamp lastPrincipalApprovalDate = addDays(principalDays,lsn_start_date);
						
						Lessonnote lsn = new Lessonnote();
						lsn.setTitle( "WEEK-"+ i + "_" + it.getSub_name() + "_" + it.getClass_name() );
						lsn.setClass_index( it.getClass_stream().getClass_index() );
						lsn.setWeek(i);
						lsn.setCycle_count(0);
						lsn.setTeacher(it.getTeacher());
						lsn.setCalendar(it.getCalendar());
						lsn.setSubject(it.getSubject());
						lsn.set_file("");
						lsn.setExpected_submission(lastSubmissionDate);
						lsn.setExpected_closure(lastClosureDate);
						lsn.setExpected_principal_approval(lastPrincipalApprovalDate);
						
						lsnRepository.save(lsn);
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    		}
				
	    	
	    }
	    //
	    System.out.println("Insert Lessonnotes with Cron job");
    }
	
	private ClassStream findNextClass(ClassStream formerClass) {
		//get all the classes in that school
		List<ClassStream> allclasses = clsRepository.findBySchool( formerClass.getSchool() );
		List<ClassStream> allclasses_filtered = allclasses.stream().filter(c -> (c.getClass_index() == formerClass.getClass_index() + 1) ).collect(Collectors.toList());
		if (allclasses_filtered.size() > 0) {
			return allclasses_filtered.get(0);
		}
		return null;
	}	
	
	 private String todayDate() {
			Date d = new Date();
	        String date = DATE_TIME_FORMAT.format(d);
	        return date;
	  }
	 
	 private java.sql.Timestamp parseTimestamp(String timestamp) {
		    try {
		        return new Timestamp(DATE_TIME_FORMAT.parse(timestamp).getTime());
		    } catch (ParseException e) {
		        throw new IllegalArgumentException(e);
		    }
	 }
	 
		 private String createUuid( String type, Long schId ) {
		    	String uuid = UUID.randomUUID().toString();
		    	String[] uniqueCode = uuid.split("-");    	
		    	String baseId = type + schId.toString() + "-" + uniqueCode[4].substring(6);
		    	return baseId;
		 }
	 
	 	private Long dayToMiliseconds(int days){
		    Long result = Long.valueOf(days * 24 * 60 * 60 * 1000);
		    return result;
		}

		private Timestamp addDays(int days, Timestamp t1) throws Exception{
		    if(days < 0){
		        throw new Exception("Day in wrong format.");
		    }
		    java.util.Calendar cal = java.util.Calendar.getInstance();
	        cal.setTime(t1);
	        cal.add(java.util.Calendar.DATE, days); //minus number would decrement the days
	        return new Timestamp(cal.getTime().getTime());
		}
	 
	 
	 /*
	  * 
	  *  	SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
	    	Session session = sessionFactory.openSession();
	    	Criteria criteria = session.createCriteria(TimeTable.class);
	    	criteria.setFetchMode("calendar", FetchMode.JOIN);
	    	session.beginTransaction();
	    	
	    	TimeTable mytimetable = session.find(TimeTable.class,it.getTimeId());
	    	Calendar cal = calRepository.findById(it.getCalendar());
	    	
	    	if ( it.getCalendar().getEnddate().compareTo( parseTimestamp(todayDate()) ) > 0 ) { **/
	 
}
