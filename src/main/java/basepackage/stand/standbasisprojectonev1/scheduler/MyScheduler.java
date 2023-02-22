package basepackage.stand.standbasisprojectonev1.scheduler;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import basepackage.stand.standbasisprojectonev1.model.Attendance;
import basepackage.stand.standbasisprojectonev1.model.Lessonnote;
import basepackage.stand.standbasisprojectonev1.model.TimeTable;
import basepackage.stand.standbasisprojectonev1.repository.AttendanceRepository;
import basepackage.stand.standbasisprojectonev1.repository.LessonnoteRepository;
import basepackage.stand.standbasisprojectonev1.repository.TimetableRepository;
import basepackage.stand.standbasisprojectonev1.service.AttendanceService;

@Component
public class MyScheduler {
	@Autowired
	AttendanceService service;
	
	private final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Autowired		
    private TimetableRepository timeRepository;
	
	@Autowired		
    private AttendanceRepository attRepository;	
	
	@Autowired		
    private LessonnoteRepository lsnRepository;
	
	//@Autowired	
	//private CalendarRepository calRepository;
	
	// "0 0/10 * * * *" - 10 minutes interval
	// "0 0 0 * * *" - Everyday at 0:00
	@Scheduled(cron = "0 0 0 * * *")
    public void insertAttendances() {
        // this code will be executed every 10 minutes
		
		//Check what day of the week is this
		Calendar calendar = Calendar.getInstance();
	    int day = calendar.get(Calendar.DAY_OF_WEEK);
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
	//@SuppressWarnings("deprecation")
	@Scheduled(cron = "0 0 0 * * *")
    public void insertLessonnotes() {
		
		 Map<Integer, String> classMap = new HashMap<>();
		 classMap.put(7, "JSS1");
		 classMap.put(8, "JSS2"); 
		 classMap.put(9, "JSS3");
		 classMap.put(10, "SS1");
		 classMap.put(11, "SS2");
		 classMap.put(12, "SS3");
		    
		 //Get Timetable data for this day with Current calendar
	    List<TimeTable> tt = timeRepository.findByActiveCalendar(1);
        
	    for (TimeTable it : tt) {	    	
	    	
	    	if ( it.getCalendar().getEnddate().compareTo( parseTimestamp(todayDate()) ) > 0 ) {
		    	
	    		// Today' date - Start date
	    		long diff = parseTimestamp(todayDate()).getTime() - it.getCalendar().getStartdate().getTime();
	    		int weeks = (int) diff / (7 * 24 * 60 * 60 * 1000 );
				
				Lessonnote lsn = new Lessonnote();
				lsn.setTitle( "WEEK-"+ weeks + "_" + it.getSub_name() + "_" + it.getClass_name() );
				lsn.setClass_index( it.getClass_stream().getClass_index() );
				lsn.setWeek(weeks);
				lsn.setCycle_count(0);
				lsn.setTeacher(it.getTeacher());
				lsn.setCalendar(it.getCalendar());
				lsn.setSubject(it.getSubject());
				lsn.set_file("");
				
				lsnRepository.save(lsn);
				
	    	}
	    }
	    //
	    System.out.println("Insert Lessonnotes with Cron job");
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
