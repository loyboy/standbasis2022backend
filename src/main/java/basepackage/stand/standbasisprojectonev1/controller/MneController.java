package basepackage.stand.standbasisprojectonev1.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import basepackage.stand.standbasisprojectonev1.model.Assessment;
import basepackage.stand.standbasisprojectonev1.model.Attendance;
import basepackage.stand.standbasisprojectonev1.model.AttendanceActivity;
import basepackage.stand.standbasisprojectonev1.model.AttendanceManagement;
import basepackage.stand.standbasisprojectonev1.model.Calendar;
import basepackage.stand.standbasisprojectonev1.model.ClassStream;
import basepackage.stand.standbasisprojectonev1.model.Enrollment;
import basepackage.stand.standbasisprojectonev1.model.EvaluationValues;
import basepackage.stand.standbasisprojectonev1.model.Lessonnote;
import basepackage.stand.standbasisprojectonev1.model.LessonnoteActivity;
import basepackage.stand.standbasisprojectonev1.model.LessonnoteManagement;
import basepackage.stand.standbasisprojectonev1.model.Rowcall;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;
import basepackage.stand.standbasisprojectonev1.model.Student;
import basepackage.stand.standbasisprojectonev1.model.Subject;
import basepackage.stand.standbasisprojectonev1.model.Teacher;
import basepackage.stand.standbasisprojectonev1.model.TimeTable;
import basepackage.stand.standbasisprojectonev1.payload.ApiResponse;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.TimetableRequest;
import basepackage.stand.standbasisprojectonev1.repository.AssessmentRepository;
import basepackage.stand.standbasisprojectonev1.repository.AttendanceManagementRepository;
import basepackage.stand.standbasisprojectonev1.repository.AttendanceRepository;
import basepackage.stand.standbasisprojectonev1.repository.CalendarRepository;
import basepackage.stand.standbasisprojectonev1.repository.LessonnoteActivityRepository;
import basepackage.stand.standbasisprojectonev1.repository.LessonnoteManagementRepository;
import basepackage.stand.standbasisprojectonev1.repository.LessonnoteRepository;
import basepackage.stand.standbasisprojectonev1.repository.UserRepository;
import basepackage.stand.standbasisprojectonev1.service.AttendanceActivityService;
import basepackage.stand.standbasisprojectonev1.service.AttendanceManagementService;
import basepackage.stand.standbasisprojectonev1.service.AttendanceService;
import basepackage.stand.standbasisprojectonev1.service.CalendarService;
import basepackage.stand.standbasisprojectonev1.service.ClassService;
import basepackage.stand.standbasisprojectonev1.service.EnrollmentService;
import basepackage.stand.standbasisprojectonev1.service.LessonnoteActivityService;
import basepackage.stand.standbasisprojectonev1.service.LessonnoteManagementService;
import basepackage.stand.standbasisprojectonev1.service.SchoolService;
import basepackage.stand.standbasisprojectonev1.service.StudentService;
import basepackage.stand.standbasisprojectonev1.service.SubjectService;
import basepackage.stand.standbasisprojectonev1.service.TeacherService;
import basepackage.stand.standbasisprojectonev1.service.TimetableService;

@RestController
@RequestMapping("/api/mne")
public class MneController {
	 
	 @Autowired
	 SchoolService schoolservice;
	
	 @Autowired
	 StudentService studentservice;
	 
	 @Autowired
	 TimetableService timetableservice;
	 
	 @Autowired
	 EnrollmentService enrolservice;
	 
	 @Autowired
	 TeacherService teacherservice;
	 
	 @Autowired
	 ClassService classservice;
	 
	 @Autowired
	 SubjectService subjectservice;
	 
	 @Autowired
	 CalendarService calendarservice;
	 
	 @Autowired
	 LessonnoteManagementService serviceManagement;
	 
	 @Autowired
	 LessonnoteActivityService serviceActivity;
	 
	 @Autowired
	 AttendanceManagementService serviceAttManagement;
	 
	 @Autowired
	 AttendanceService serviceAtt;
	 
	 @Autowired
	 AttendanceActivityService serviceAttActivity;
	 
	 @Autowired
	 private AttendanceRepository attRepository;
	 
	 @Autowired
	 private AttendanceManagementRepository attManageRepository;
	 
	 @Autowired
	 private AssessmentRepository assRepository;
	 
	 @Autowired
	 private LessonnoteRepository lsnRepository;
	 
	 @Autowired
	 private LessonnoteManagementRepository lsnmanageRepository;
	 
	 @Autowired
	 private LessonnoteActivityRepository lsnactivityRepository;
	 
	 @Autowired		
	 private CalendarRepository calRepository;
	 
	 @GetMapping("/attendance/students")
	 public ResponseEntity<?> getStudentAttendance(
			 @RequestParam(value = "enrol") Long enrolId,
			 @RequestParam(value = "calendar") Long calendar,
			 @RequestParam(value = "week",required=false) Integer week
			 ) {		
		 
		// Map<String, Object> response = service.getPaginatedCalendars( page, size, query, school );
		// return new ResponseEntity<>(response, HttpStatus.OK);
		 
		 Calendar calobj = calendarservice.findCalendar(calendar);
		 Enrollment enrolobj = enrolservice.findEnrollment(enrolId);
		 LocalDate stDate = calobj.getStartdate().toInstant().atZone(ZoneId.of("UTC")).toLocalDate();
		 LocalDate endDate = calobj.getEnddate().toInstant().atZone(ZoneId.of("UTC")).toLocalDate();
		 Timestamp timestampWeekStart = null;
		 Timestamp timestampWeekEnd = null;
		 
		 if (week != null) {
			 List<LocalDate> weekrange = getWeekdayRange(week, stDate, endDate);
			 LocalDateTime localDateTime1 = weekrange.get(0).atStartOfDay();
			 LocalDateTime localDateTime2 = weekrange.get(4).atStartOfDay();

		     timestampWeekStart = Timestamp.valueOf(localDateTime1);
		     timestampWeekEnd = Timestamp.valueOf(localDateTime2);
		 }
	     
	     //System.out.println("myrowcall precall: " + enrolobj.getStudent().getPupId() + " --- " + calendar );
	     
		 //get all rowcalls irrepective of the subjects
	     List<Rowcall> myrowcall = attRepository.findByStudentMne(enrolobj.getStudent().getPupId(), calendar, timestampWeekStart, timestampWeekEnd);
	     
	     System.out.println("myrowcall: " + myrowcall.size() );
	     
	     List<TimeTable> pupilclasses = timetableservice.findClassOffered(enrolobj.getClassstream().getClsId(), calendar);
	     
	     System.out.println("pupilclasses: " + pupilclasses.size() );
	     
	     List< Map<String, Object> > mnecolumndata = new ArrayList<>();
	     List< Map<String, Object> > mnecolumn = new ArrayList<>();
		 
	     Map<String, Object> objectmnecolumn = new HashMap<>();
	     objectmnecolumn.put("key", "student_name");
	     objectmnecolumn.put("label", "Student Name");
	     objectmnecolumn.put("sortable", true);
	     
	     mnecolumn.add( objectmnecolumn );
	     
	     Map<String, Object> objectmnecolumn2 = new HashMap<>();
	     objectmnecolumn2.put("key", "performance");
	     objectmnecolumn2.put("label", "Performance");
	     objectmnecolumn2.put("sortable", true);
	     
	     mnecolumn.add( objectmnecolumn2 );
	     
	     Map<String, Object> objectmnecolumndata = new HashMap<>();
	     objectmnecolumndata.put("student_name", enrolobj.getStudent().getName() );
	     
	     int j = 1;
	     
	     Set<String> subjectNamesSet = new HashSet<>();

	     for (TimeTable timetable : pupilclasses) {
	         Subject subject = timetable.getSubject();
	         if (subject != null) {
	             String subjectName = subject.getName();
	             subjectNamesSet.add(subjectName);
	         }
	     }
	     
	     List<Integer> allAverage = new ArrayList<>();
	     
	     String[] subjectNamesArray = subjectNamesSet.toArray(new String[0]);
	     
	     System.out.println("subjectNamesArray: " + subjectNamesArray.length );
	     
	     for (String sub : subjectNamesArray) {	 
	    	  
		     List<Rowcall> rwcallOne = myrowcall.stream().filter(rw -> rw.getAttendance().getTimetable().getSubject().getName().equals(sub) ).collect(Collectors.toList());     
		     
		     if (rwcallOne.size() > 0) {
		    	 
		    	 System.out.println("Current Index: " + j + " >> " + sub);
		    	 System.out.println("rwcallOne: " + rwcallOne.size() );
		    	 int perf = 0;
		    	 
		    	 List<Rowcall> rwcallPresent = rwcallOne.stream().filter(rw -> rw.getStatus().equals(1) ).collect(Collectors.toList());
		    	 
		    	 System.out.println("rwcallPresent: " + rwcallPresent.size() );
		    	 
		    	 if (rwcallPresent.size() > 0) {
		    		 perf = (rwcallPresent.size() * 100) / rwcallOne.size();
		    	 }   	
		    	 
			     Map<String, Object> objectmnecolumntemp = new HashMap<>();
		    	 objectmnecolumntemp.put("key", "d"+j);
		    	 objectmnecolumntemp.put("label", sub );
		    	 objectmnecolumntemp.put("sortable", true);
		    	 
			     mnecolumn.add( objectmnecolumntemp );
			     
			     System.out.println("pupilclass chose: " + sub );
			     
			     allAverage.add(perf);
			     
			     objectmnecolumndata.put("d"+j, perf  );		     
			     
			     j++;
		     }
		     
		 }
	     
	     double averagePerf = allAverage.stream()
	                .mapToInt(Integer::intValue)
	                .average()
	                .orElse(0.0);
	     
	     objectmnecolumndata.put("performance", averagePerf);
	     mnecolumndata.add( objectmnecolumndata ); 
	     
	     Map<String, Object> response = new HashMap<>();
			
		 response.put("mnecolumn", mnecolumn);
		 response.put("mnecolumndata", mnecolumndata);
	     
		 return new ResponseEntity<>(response, HttpStatus.OK);		
	 }
	 
	 @GetMapping("/attendance/teachers")
	 public ResponseEntity<?> getTeacherAttendance(
			 @RequestParam(value = "teacher") Long teacher,
			 @RequestParam(value = "calendar") Long calendar,
			 @RequestParam(value = "week",required=false) Integer week
			 ) {		
		 
		// Map<String, Object> response = service.getPaginatedCalendars( page, size, query, school );
		// return new ResponseEntity<>(response, HttpStatus.OK);
		try { 
		 Calendar calobj = calendarservice.findCalendar(calendar);
		 Teacher teaobj = teacherservice.findTeacher(teacher);
		 LocalDate stDate = calobj.getStartdate().toInstant().atZone(ZoneId.of("UTC")).toLocalDate();
		 LocalDate endDate = calobj.getEnddate().toInstant().atZone(ZoneId.of("UTC")).toLocalDate();
		 Timestamp timestampWeekStart = null;
		 Timestamp timestampWeekEnd = null;
		 
		 if (week != null) {
			 List<LocalDate> weekrange = getWeekdayRange(week, stDate, endDate);
			 LocalDateTime localDateTime1 = weekrange.get(0).atStartOfDay();
			 LocalDateTime localDateTime2 = weekrange.get(4).atStartOfDay();

		     timestampWeekStart = Timestamp.valueOf(localDateTime1);
		     timestampWeekEnd = Timestamp.valueOf(localDateTime2);
		 }
		      
	     List<Attendance> my_attendance = attRepository.findByTeacherMne( teaobj.getTeaId(), calendar, timestampWeekStart, timestampWeekEnd);    
	     List<AttendanceManagement> my_attendancemanagement = attManageRepository.findByTeacherMne( teaobj.getTeaId(), calendar, timestampWeekStart, timestampWeekEnd);    
	     
	   //  System.out.println("Inside the start :: " + my_attendance.size() + " >> " + my_attendancemanagement.size() + " << " );
	     
	     List<TimeTable> teacherclasses = timetableservice.findClassTaught( teaobj.getTeaId(), calendar);
	     
	     List< Map<String, Object> > mnecolumndata = new ArrayList<>();
	     List< Map<String, Object> > mnecolumn = new ArrayList<>();
		 
	     Map<String, Object> objectmnecolumn = new HashMap<>();
	     objectmnecolumn.put("key", "teacher_name");
	     objectmnecolumn.put("label", "Teacher Name");
	     objectmnecolumn.put("sortable", true);
	     
	     mnecolumn.add( objectmnecolumn );
	     
	     Map<String, Object> objectmnecolumn2 = new HashMap<>();
	     objectmnecolumn2.put("key", "performance");
	     objectmnecolumn2.put("label", "Attendance");
	     objectmnecolumn2.put("sortable", true);
	     
	     mnecolumn.add( objectmnecolumn2 );
	     
	     Map<String, Object> objectmnecolumn3 = new HashMap<>();
	     objectmnecolumn3.put("key", "management");
	     objectmnecolumn3.put("label", "Management");
	     objectmnecolumn3.put("sortable", true);
	     
	     mnecolumn.add( objectmnecolumn3 );
	     
	     Map<String, Object> objectmnecolumndata = new HashMap<>();
	     objectmnecolumndata.put("teacher_name", teaobj.getFname() + " " + teaobj.getLname() );
	     
	     int j = 1;
	     
	     List<TimeTable> uniqueTimetables = findUniqueTimetables(teacherclasses);
	     
	     List<Double> allAverage = new ArrayList<>();
	     List<Double> allAverageManagement = new ArrayList<>();
	     //String[] subjectNamesArray = subjectNamesSet.toArray(new String[0]);
	     
	     for (TimeTable subclass : uniqueTimetables) {	    	
		     
	    	// System.out.println("Unique timetable seen: " + subclass.getTimeId() );
	    	 
		     List<Attendance> attcall = my_attendance.stream().filter(att -> att.getTimetable().getSubject().equals(subclass.getSubject()) && att.getTimetable().getClass_stream().equals( subclass.getClass_stream() ) ).collect(Collectors.toList());
		     List<AttendanceManagement> attcall_manage = my_attendancemanagement.stream().filter(att -> att.getAtt_id().getTimetable().getSubject().equals(subclass.getSubject()) && att.getAtt_id().getTimetable().getClass_stream().equals( subclass.getClass_stream() ) ).collect(Collectors.toList());
		     
		    // System.out.println("Inside the for loop : " + attcall.size() + " >> " + attcall_manage.size() + ">>" + j );
		     
		     if (attcall.size() > 0) {
		    	 
		    	 double perf = 0.0;
		    	 List<Attendance> attcallPresent = attcall.stream().filter(att -> ( att.getDone() == 1 || att.getDone() == 2 ) ).collect(Collectors.toList());
		    	 //same with upper
		    	 List<Attendance> new_my_attendance = my_attendance.stream().filter(att -> att.getTimetable().getSubject().equals(subclass.getSubject()) && att.getTimetable().getClass_stream().equals( subclass.getClass_stream() ) ).collect(Collectors.toList());
		    	 	    
		    	 if (attcallPresent.size() > 0) {		    		
		    		 perf = (double) ( attcallPresent.size() * 100 ) / new_my_attendance.size();
		    	 }    	
		    	 
			     Map<String, Object> objectmnecolumntemp = new HashMap<>();
		    	 objectmnecolumntemp.put("key", "d"+j);
		    	 objectmnecolumntemp.put("label", subclass.getClass_stream().getTitle() + subclass.getClass_stream().getExt() + " " + subclass.getSubject().getName() + " " + "Performance" );
		    	 objectmnecolumntemp.put("sortable", true);
		    	 
			     mnecolumn.add( objectmnecolumntemp );
			     
			     allAverage.add(perf);
			     
			     objectmnecolumndata.put("d"+j, (int) perf  );		     
			     
		     }
		     
		     if (attcall_manage.size() > 0) {
		    	 
		    	 double perf = 0.0;
		    	 List<AttendanceManagement> attcallPresent1 = attcall_manage.stream().filter(att -> att.getScore() >= 25 ).collect(Collectors.toList());
		    	 //same with upper
		    	 List<AttendanceManagement> new_my_attendance1 = my_attendancemanagement.stream().filter(att -> att.getAtt_id().getTimetable().getSubject().equals(subclass.getSubject()) && att.getAtt_id().getTimetable().getClass_stream().equals( subclass.getClass_stream() )).collect(Collectors.toList());
		    	 	    
		    	 if (attcallPresent1.size() > 0) {
		    		 int sum = attcallPresent1.stream()
	                            .mapToInt(att -> att.getScore())
	                            .sum();
		    		 perf = (double)(sum * 100)/(new_my_attendance1.size() * 100);
		    	 }    	
		    	 
		    	 j++;
		    	 
			     Map<String, Object> objectmnecolumntemp = new HashMap<>();
		    	 objectmnecolumntemp.put("key", "d"+j);
		    	 objectmnecolumntemp.put("label", subclass.getClass_stream().getTitle() + subclass.getClass_stream().getExt() + " " + subclass.getSubject().getName() + " " + "Management" );
		    	 objectmnecolumntemp.put("sortable", true);
		    	 
			     mnecolumn.add( objectmnecolumntemp );
			     
			     allAverageManagement.add(perf);	     
			     
			     objectmnecolumndata.put("d"+j, (int) perf  );	     	     
			     
		     }
		     
		     j++;
		     
		 }
	     
	     double averagePerf = allAverage.stream()
	                .mapToInt(Double::intValue)
	                .average()
	                .orElse(0.0);
	     
	     objectmnecolumndata.put("performance", averagePerf);
	  //   mnecolumndata.add( objectmnecolumndata );
	     
	     double averagePerfManagement = allAverageManagement.stream()
	                .mapToInt(Double::intValue)
	                .average()
	                .orElse(0.0);
	     
	     objectmnecolumndata.put("management", averagePerfManagement);
	     
	     mnecolumndata.add( objectmnecolumndata );
	     
	     Map<String, Object> response = new HashMap<>();
			
		 response.put("mnecolumn", mnecolumn);
		 response.put("mnecolumndata", mnecolumndata);
	     
		 return new ResponseEntity<>(response, HttpStatus.OK);
		 
	 }
	 catch (Exception ex) {
		 ex.printStackTrace();
         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "Error encountered."));
     }
	 
	 }
	
	 @GetMapping("/lessonnote/students")
	 public ResponseEntity<?> getStudentLessonnote( 
			 @RequestParam(value = "enrol") Long enrolId,
			 @RequestParam(value = "calendar") Long calendar,
			 @RequestParam(value = "week",required=false) Integer week,
			 @RequestParam(value = "type",required=false) String typeof
	  ){
		 
		 Calendar calobj = calendarservice.findCalendar(calendar);
		 Enrollment enrolobj = enrolservice.findEnrollment(enrolId);
		 
		 List<Assessment> myassesssments = assRepository.findStudentMne( week, enrolobj.getStudent(), calobj, typeof );
	     
		 List<TimeTable> pupilclasses = timetableservice.findClassOffered(enrolobj.getClassstream().getClsId(), calendar);
		    
		 List< Map<String, Object> > mnecolumndata = new ArrayList<>();
	     List< Map<String, Object> > mnecolumn = new ArrayList<>();
		 
	     Map<String, Object> objectmnecolumn = new HashMap<>();
	     objectmnecolumn.put("key", "student_name");
	     objectmnecolumn.put("label", "Student Name");
	     objectmnecolumn.put("sortable", true);
	     
	     mnecolumn.add( objectmnecolumn );
	     
	     Map<String, Object> objectmnecolumn2 = new HashMap<>();
	     objectmnecolumn2.put("key", "performance");
	     objectmnecolumn2.put("label", "Performance");
	     objectmnecolumn2.put("sortable", true);
	     
	     mnecolumn.add( objectmnecolumn2 );
	     
	     Map<String, Object> objectmnecolumndata = new HashMap<>();
	     objectmnecolumndata.put("student_name", enrolobj.getStudent().getName() );
	     
	     int j = 1;
	     
	     Set<String> subjectNamesSet = new HashSet<>();
	     
	     for (TimeTable timetable : pupilclasses) {
	         Subject subject = timetable.getSubject();
	         if (subject != null) {
	             String subjectName = subject.getName();
	             subjectNamesSet.add(subjectName);
	         }
	     }
	     
	     List<Integer> allAverage = new ArrayList<>();
	     if ( myassesssments.size() > 0 ) {
	    	 String[] subjectNamesArray = subjectNamesSet.toArray(new String[0]);
		     for (String sub : subjectNamesArray) {	    	
			     
			     List<Assessment> ascallOne = myassesssments.stream().filter(as -> as.getLsn().getSubject().getName().equals(sub) ).collect(Collectors.toList());
			     int perf = 0;
			     perf = ascallOne.get(0).getScore();        		    	 
				     
				 Map<String, Object> objectmnecolumntemp = new HashMap<>();
			     objectmnecolumntemp.put("key", "d"+j);
			     objectmnecolumntemp.put("label", sub );
			     objectmnecolumntemp.put("sortable", true);
			    	 
				 mnecolumn.add( objectmnecolumntemp );
				     
				 System.out.println("pupilclass chose: " + sub );
				     
				 allAverage.add(perf);
				     
				 objectmnecolumndata.put("d"+j, perf  );		     
				     
				 j++;		     
			     
			 }
	     }
	     else{
	    	 String[] subjectNamesArray = subjectNamesSet.toArray(new String[0]);
		     for (String sub : subjectNamesArray) {	    	
			     
			     int perf = 0;    		    	 
				     
				 Map<String, Object> objectmnecolumntemp = new HashMap<>();
			     objectmnecolumntemp.put("key", "d"+j);
			     objectmnecolumntemp.put("label", sub );
			     objectmnecolumntemp.put("sortable", true);
			    	 
				 mnecolumn.add( objectmnecolumntemp );
				     
				 System.out.println("pupilclass chose: " + sub );
				     
				 allAverage.add(perf);
				     
				 objectmnecolumndata.put("d"+j, perf  );		     
				     
				 j++;		     
			     
			 }
	     }
	     
	     
	     double averagePerf = allAverage.stream()
	                .mapToInt(Integer::intValue)
	                .average()
	                .orElse(0.0);
	     
	     objectmnecolumndata.put("performance", averagePerf);
	     mnecolumndata.add( objectmnecolumndata ); 
	     
	     Map<String, Object> response = new HashMap<>();
			
		 response.put("mnecolumn", mnecolumn);
		 response.put("mnecolumndata", mnecolumndata);
	     
		 return new ResponseEntity<>(response, HttpStatus.OK);
	 }
	 
	 @GetMapping("/lessonnote/teachers")
	 public ResponseEntity<?> getTeacherLessonnote(
			 @RequestParam(value = "teacher",required=false) Long teacher,
			 @RequestParam(value = "calendar",required=false) Long calendar,
			 @RequestParam(value = "week",required=false) Integer week
	 ){
		 
		 try { 
			 Calendar calobj = calendarservice.findCalendar(calendar);
			 Teacher teaobj = teacher != null ? teacherservice.findTeacher(teacher) : null;			 
			      
		     List<Lessonnote> my_lessonnote = lsnRepository.findTeacherMne(  week , teaobj , calobj );    
		     List<LessonnoteManagement> my_lessonnotemanagement = lsnmanageRepository.findTeacherMne( week , teaobj , calobj);    
		     List<LessonnoteActivity> my_lessonnoteactivity = lsnactivityRepository.findPrincipalMne( week , teaobj , calobj);    
		     List<TimeTable> teacherclasses = null;
		     if (teacher != null) {
				  teacherclasses = timetableservice.findClassTaught( teaobj.getTeaId(), calendar);
			 }
			 else {
				  teacherclasses = timetableservice.getTimetablesByCalendar(calendar);
			 }
		     
		     List< Map<String, Object> > mnecolumndata = new ArrayList<>();
		     List< Map<String, Object> > mnecolumn = new ArrayList<>();
			 
		     Map<String, Object> objectmnecolumn = new HashMap<>();
		     objectmnecolumn.put("key", "teacher_name");
		     objectmnecolumn.put("label", "User Name");
		     objectmnecolumn.put("sortable", true);
		     
		     mnecolumn.add( objectmnecolumn );
		     
		     if( teacher != null ) {
			     Map<String, Object> objectmnecolumn2 = new HashMap<>();
			     objectmnecolumn2.put("key", "classwork_performance");
			     objectmnecolumn2.put("label", "Classwork");
			     objectmnecolumn2.put("sortable", true);
			     
			     mnecolumn.add( objectmnecolumn2 );
			     
			     Map<String, Object> objectmnecolumn21 = new HashMap<>();
			     objectmnecolumn21.put("key", "homework_performance");
			     objectmnecolumn21.put("label", "Homework");
			     objectmnecolumn21.put("sortable", true);
			     
			     mnecolumn.add( objectmnecolumn21 );
			     
			     Map<String, Object> objectmnecolumn22 = new HashMap<>();
			     objectmnecolumn22.put("key", "test_performance");
			     objectmnecolumn22.put("label", "Test");
			     objectmnecolumn22.put("sortable", true);
			     
			     mnecolumn.add( objectmnecolumn22 );
		     
		     }
		     
		     Map<String, Object> objectmnecolumn3 = new HashMap<>();
		     objectmnecolumn3.put("key", "management");
		     objectmnecolumn3.put("label", "Management");
		     objectmnecolumn3.put("sortable", true);
		     
		     mnecolumn.add( objectmnecolumn3 );
		     
		     Map<String, Object> objectmnecolumndata = new HashMap<>();
		     if (teacher != null) {			     
			     objectmnecolumndata.put("teacher_name", teaobj.getFname() + " " + teaobj.getLname() );
		     }
		     else {
			     objectmnecolumndata.put("teacher_name", "Principal" );
		     }
		     
		     int j = 1;
		     
		     List<TimeTable> uniqueTimetables = findUniqueTimetablesByClassIndex(teacherclasses);
		     
		     List<Double> allAveragePrincipal = new ArrayList<>();
		     List<Double> allAverageClaswork = new ArrayList<>();
		     List<Double> allAverageHomework = new ArrayList<>();
		     List<Double> allAverageTest = new ArrayList<>();
		     List<Double> allAverageManagement = new ArrayList<>();
		     
		     for (TimeTable subclass : uniqueTimetables) {	    	
			     		    	 
			     List<Lessonnote> lsncall = my_lessonnote.stream().filter(lsn -> lsn.getSubject().equals(subclass.getSubject()) && lsn.getClass_index().equals( subclass.getClass_stream().getClass_index() ) ).collect(Collectors.toList());
			     List<LessonnoteManagement> lsncall_manage = my_lessonnotemanagement.stream().filter(lsn -> lsn.getLsn_id().getSubject().equals(subclass.getSubject()) && lsn.getLsn_id().getClass_index().equals( subclass.getClass_stream().getClass_index() ) ).collect(Collectors.toList());
			     List<LessonnoteActivity> lsncall_activity = my_lessonnoteactivity.stream().filter(lsn -> lsn.getLsn_id().getSubject().equals(subclass.getSubject()) && lsn.getLsn_id().getClass_index().equals( subclass.getClass_stream().getClass_index() ) ).collect(Collectors.toList());
			     
			    // System.out.println("Inside the for loop : " + attcall.size() + " >> " + attcall_manage.size() + ">>" + j );  
			     if (teacher == null && lsncall_activity.size() > 0) {
			    	 
			    	 double perf = 0.0;
			    	 List<LessonnoteActivity> lsncallActivity_principal = lsncall_activity.stream().filter(lsn -> lsn.getSlip() != null && lsn.getSlip().equals(0) ).collect(Collectors.toList());
			    	 //same with upper
			    	 List<LessonnoteActivity> new_my_activity = lsncall_activity;
			    	 	    
			    	 System.out.println("filtered List: " + lsncallActivity_principal.get(0).getLsnactId() );
			    	 System.out.println("Total List: " 	  + new_my_activity.get(0).getLsnactId() );
			    	 
			    	 if (lsncallActivity_principal.size() > 0) {
			    		 
			    		 perf = (double) (lsncallActivity_principal.size() * 100)/(new_my_activity.size() );
			    	 }    	
			    	 
			    	 j++;
			    	 
				     Map<String, Object> objectmnecolumntemp = new HashMap<>();
			    	 objectmnecolumntemp.put("key", "d"+j);
			    	 objectmnecolumntemp.put("label", subclass.getClass_stream().getTitle() + " " + subclass.getSubject().getName() + " " + "Principal Management" );
			    	 objectmnecolumntemp.put("sortable", true);
			    	 
				     mnecolumn.add( objectmnecolumntemp );
				     
				     allAveragePrincipal.add(perf);	     
				     
				     objectmnecolumndata.put("d"+j, (int) perf  );	     	     
				     
			     }
			     
			     if (teacher != null && lsncall_manage.size() > 0) {
			    	 
			    	 double classwork_perf = 0.0;
			    	 double homework_perf = 0.0;
			    	 double test_perf = 0.0;
			    	 double management_perf = 0.0;
			    	 List<LessonnoteManagement> lsncallManagement_classwork = lsncall_manage.stream().filter(lsn -> lsn.getSub_perf_classwork() != null ).collect(Collectors.toList());
			    	 List<LessonnoteManagement> lsncallManagement_homework = lsncall_manage.stream().filter(lsn -> lsn.getSub_perf_homework() != null ).collect(Collectors.toList());
			    	 List<LessonnoteManagement> lsncallManagement_test = lsncall_manage.stream().filter(lsn -> lsn.getSub_perf_test() != null ).collect(Collectors.toList());
			    	 List<LessonnoteManagement> lsncallManagement = lsncall_manage.stream().filter(lsn -> lsn.getManagement() != null ).collect(Collectors.toList());
			    	 
			    	 //same with upper
			    	 List<LessonnoteManagement> new_my_lsn_manage = lsncall_manage;
			    	 	    
			    	 if (lsncallManagement_classwork.size() > 0) {
			    		 int sum = lsncallManagement_classwork.stream()
		                            .mapToInt(lsn -> lsn.getSub_perf_classwork())
		                            .sum();
			    		 classwork_perf = (double)(sum * 100)/(new_my_lsn_manage.size() * 100);
			    	 }
			    	 
			    	 if (lsncallManagement_homework.size() > 0) {
			    		 int sum = lsncallManagement_homework.stream()
		                            .mapToInt(lsn -> lsn.getSub_perf_homework())
		                            .sum();
			    		 homework_perf = (double)(sum * 100)/(new_my_lsn_manage.size() * 100);
			    	 }
			    	 
			    	 if (lsncallManagement_test.size() > 0) {
			    		 int sum = lsncallManagement_test.stream()
		                            .mapToInt(lsn -> lsn.getSub_perf_test())
		                            .sum();
			    		 test_perf = (double)(sum * 100)/(new_my_lsn_manage.size() * 100);
			    	 }
			    	 
			    	 if (lsncallManagement.size() > 0) {
			    		 int sum = lsncallManagement.stream()
		                            .mapToInt(lsn -> lsn.getManagement() )
		                            .sum();
			    		 management_perf = (double)(sum * 100)/(new_my_lsn_manage.size() * 100);
			    	 }
			    	 
			    	 j++;
			    	 
				     Map<String, Object> objectmnecolumntemp = new HashMap<>();
			    	 objectmnecolumntemp.put("key", "d"+j);
			    	 objectmnecolumntemp.put("label", subclass.getClass_stream().getTitle() + " " + subclass.getSubject().getName() + " " + "Classwork %" );
			    	 objectmnecolumntemp.put("sortable", true);
			    	 
				     mnecolumn.add( objectmnecolumntemp );
				     
				     allAverageClaswork.add(classwork_perf);	     
				     
				     objectmnecolumndata.put("d"+j , (int) classwork_perf  );
				     
				     ////////////////////////////
				     j++;
				     
				     Map<String, Object> objectmnecolumntemp2 = new HashMap<>();
			    	 objectmnecolumntemp2.put("key", "d"+j);
			    	 objectmnecolumntemp2.put("label", subclass.getClass_stream().getTitle() + " " + subclass.getSubject().getName() + " " + "Homework %" );
			    	 objectmnecolumntemp2.put("sortable", true);
			    	 
				     mnecolumn.add( objectmnecolumntemp2 );
				     
				     allAverageHomework.add(homework_perf);	     
				     
				     objectmnecolumndata.put("d"+j , (int) homework_perf  );
				     
				     /////////////////////////////
				     j++;
				     
				     Map<String, Object> objectmnecolumntemp3 = new HashMap<>();
			    	 objectmnecolumntemp3.put("key", "d"+j);
			    	 objectmnecolumntemp3.put("label", subclass.getClass_stream().getTitle() + " " + subclass.getSubject().getName() + " " + "Test %" );
			    	 objectmnecolumntemp3.put("sortable", true);
			    	 
				     mnecolumn.add( objectmnecolumntemp3 );
				     
				     allAverageTest.add(test_perf);	     
				     
				     objectmnecolumndata.put("d"+j , (int) test_perf  );
				     
				     ///////////////////////////////
				     j++;
				     
				     Map<String, Object> objectmnecolumntemp4 = new HashMap<>();
			    	 objectmnecolumntemp4.put("key", "d"+j);
			    	 objectmnecolumntemp4.put("label", subclass.getClass_stream().getTitle() + " " + subclass.getSubject().getName() + " " + "Management %" );
			    	 objectmnecolumntemp4.put("sortable", true);
			    	 
				     mnecolumn.add( objectmnecolumntemp4 );
				     
				     allAverageManagement.add(management_perf);	     
				     
				     objectmnecolumndata.put("d"+j , (int) management_perf  );
				     
			     }
			     
			     
			     
			     j++;
			     
			 }
		     
		     if (teacher != null) {
		    	 double averagePerfClasswork = allAverageClaswork.stream()
			                .mapToInt(Double::intValue)
			                .average()
			                .orElse(0.0);
			     double averagePerfHomework = allAverageHomework.stream()
			                .mapToInt(Double::intValue)
			                .average()
			                .orElse(0.0);
			     double averagePerfTest = allAverageTest.stream()
			                .mapToInt(Double::intValue)
			                .average()
			                .orElse(0.0);
			     
			     objectmnecolumndata.put("classwork_performance", averagePerfClasswork);
			     objectmnecolumndata.put("homework_performance", averagePerfHomework);
			     objectmnecolumndata.put("test_performance", averagePerfTest);
			     
			     double averagePerfManagement = allAverageManagement.stream()
			                .mapToInt(Double::intValue)
			                .average()
			                .orElse(0.0);
			     
			     objectmnecolumndata.put("management", averagePerfManagement);
			     
			     mnecolumndata.add( objectmnecolumndata );
		     }
		     else {
		    	 
		    	 double averagePerfManagement = allAveragePrincipal.stream()
			                .mapToInt(Double::intValue)
			                .average()
			                .orElse(0.0);
			     
			     objectmnecolumndata.put("management", averagePerfManagement);
			     
			     mnecolumndata.add( objectmnecolumndata );
		    	 
		     }    
		     
		     Map<String, Object> response = new HashMap<>();
				
			 response.put("mnecolumn", mnecolumn);
			 response.put("mnecolumndata", mnecolumndata);
		     
			 return new ResponseEntity<>(response, HttpStatus.OK);
			 
		 }
		 catch (Exception ex) {
			 ex.printStackTrace();
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "Error encountered."));
	     }
	 }
	 
	 @SuppressWarnings("unchecked")
	@GetMapping("/lessonnote/proprietors")
	 public ResponseEntity<?> getProprietorLessonnote(
			 @RequestParam(value = "schoolgroup") Optional<Long> schoolgroup,
			 @RequestParam(value = "school", required=false) Optional<Long> school,
			 @RequestParam(value = "calendar", required=false) Optional<Long> calendar,
			 @RequestParam(value = "week", required=false) Optional<Integer>  week
	 ){
		 
			 Optional<Integer> termVal = Optional.ofNullable(null);
		     Optional<String> yearVal = Optional.ofNullable(null);
		     Optional<basepackage.stand.standbasisprojectonev1.model.Calendar> calendarownerobj = null;
		     if(calendar.isPresent()) { calendarownerobj = calRepository.findById( calendar.get() );  } 
		     if (calendarownerobj.isPresent()) { 
		    	 termVal = Optional.ofNullable(calendarownerobj.get().getTerm());  
		    	 yearVal = Optional.ofNullable(calendarownerobj.get().getSession());  
		     }
			 Optional<Long> WithNullableValue = Optional.ofNullable(null);
			 Optional<Integer> WithNullableValueInt = Optional.ofNullable(null);
			 Optional<Timestamp> WithNullableValueTime = Optional.ofNullable(null);
			 Map<String, Object> lsnManageResponse = serviceManagement.getOrdinaryTeacherLessonnotes("", schoolgroup, school, WithNullableValueInt, week, yearVal, termVal, WithNullableValue, WithNullableValue, WithNullableValueTime, WithNullableValueTime );
			 Map<String, Object> lsnActivityResponse = serviceActivity.getOrdinaryTeacherLessonnotes("", schoolgroup, school, WithNullableValueInt, week,  calendar, WithNullableValue, WithNullableValueTime, WithNullableValueTime );
			 
			 List<LessonnoteManagement> ordinaryArrayManagement = (List<LessonnoteManagement>) lsnManageResponse.get("lessonnotemanagement");
			 List<LessonnoteActivity> ordinaryArrayActivity = (List<LessonnoteActivity>) lsnActivityResponse.get("Lessonnoteactivity");
			 
			 Map<String, Object> newResponse = new HashMap<>();
			 
			 Integer maxManagement = ordinaryArrayManagement.size(); 
			 Integer maxActivity = ordinaryArrayActivity.size(); 
			 
			 Long teacherManagement = ordinaryArrayManagement.stream().filter(o -> o.getManagement() >= 50).count(); 
			 Long headAdministration = ordinaryArrayActivity.stream().filter(o -> o.getActual() != null && o.getOwnertype().equals("Principal") ).count(); 
			 
			 newResponse.put("teacher_management", (teacherManagement.intValue()/maxManagement) * 100 );
			 newResponse.put("head_admin", (headAdministration.intValue()/maxActivity) * 100 );
			 
			 return new ResponseEntity<>(newResponse, HttpStatus.OK);
				
	 }
	 
	 @SuppressWarnings("unchecked")
	 @GetMapping("/attendance/proprietors")
	 public ResponseEntity<?> getProprietorAttendance(
			 @RequestParam(value = "schoolgroup") Optional<Long> schoolgroup,
			 @RequestParam(value = "school", required=false) Optional<Long> school,
			 @RequestParam(value = "calendar", required=false) Optional<Long> calendar,
			 @RequestParam(value = "week", required=false) Optional<Integer>  week
	 ){
		 try { 
		 	 
			
		 	 Optional<Timestamp> WithStartValueTime = Optional.ofNullable(null);
		 	 Optional<Timestamp> WithEndValueTime = Optional.ofNullable(null);
		 	 
		 	 if (calendar.isPresent() && week.isPresent()) {
		 		Calendar calobj = calendarservice.findCalendar(calendar.get());
		 		LocalDate stDate = calobj.getStartdate().toInstant().atZone(ZoneId.of("UTC")).toLocalDate();
				LocalDate endDate = calobj.getEnddate().toInstant().atZone(ZoneId.of("UTC")).toLocalDate();
				 
				 List<LocalDate> weekrange = getWeekdayRange(week.get(), stDate, endDate);				 
				 
				 LocalDateTime localDateTime1 = weekrange.get(0).atStartOfDay();
				 LocalDateTime localDateTime2 = weekrange.get(4).atStartOfDay();

			     Timestamp timestampWeekStart = Timestamp.valueOf(localDateTime1);
			     Timestamp timestampWeekEnd = Timestamp.valueOf(localDateTime2);
			     
			     WithStartValueTime = Optional.ofNullable(timestampWeekStart);
			     WithEndValueTime = Optional.ofNullable(timestampWeekEnd);
		 	 }
		 
			 Optional<Long> WithNullableValue = Optional.ofNullable(null);
			 Optional<Integer> WithNullableValueInt = Optional.ofNullable(null);
			 
			 Map<String, Object> attResponse = serviceAtt.getOrdinaryTeacherAttendances("", schoolgroup, school, WithNullableValue, calendar, WithNullableValue, WithNullableValue, WithStartValueTime, WithEndValueTime );
			 Map<String, Object> attManangeResponse = serviceAttManagement.getOrdinaryTeacherAttendances("", schoolgroup, school, WithNullableValue, calendar, WithNullableValue, WithNullableValue, WithStartValueTime, WithEndValueTime );
			 Map<String, Object> attActivityResponse = serviceAttActivity.getOrdinaryTeacherAttendances( "", schoolgroup, school, WithNullableValue, calendar, WithNullableValue, WithStartValueTime, WithEndValueTime );
			 Map<String, Object> attStudent = serviceAtt.getOrdinaryStudentAttendances("", schoolgroup, school, WithNullableValue,  calendar, WithNullableValue, WithStartValueTime, WithEndValueTime);
			 
			 List<Attendance> ordinaryArray = (List<Attendance>) attResponse.get("attendances");
			 List<Rowcall> ordinaryStudentArray = (List<Rowcall>) attStudent.get("attendances");
			 List<AttendanceManagement> ordinaryArrayManagement = (List<AttendanceManagement>) attManangeResponse.get("attendancemanagement");
			 List<AttendanceActivity> ordinaryArrayActivity = (List<AttendanceActivity>) attActivityResponse.get("attendanceactivity");
			 
			 Map<String, Object> newResponse = new HashMap<>();
			 
			 Integer maxManagement = ordinaryArrayManagement.size(); 
			 Integer max = ordinaryArray.size();
			 Integer maxActivity = ordinaryArrayActivity.size();
			 Integer maxStudent = ordinaryStudentArray.size();
			 
			 Long teacherAttendance = ordinaryArray.stream().filter(o -> ( o.getDone() == 1 || o.getDone() == 2 ) ).count(); 
			 Long teacherAttendanceManagement = ordinaryArrayManagement.stream().filter(o -> o.getScore() >= 50).count();
			 Long studentAttendance = ordinaryStudentArray.stream().filter(o -> o.getStatus() == 1).count(); 
			 Long studentExcusedAttendance = ordinaryStudentArray.stream().filter(o -> o.getStatus() == 0 && o.getRemark() != null).count(); 
			 Long headAdministration = ordinaryArrayActivity.stream().filter(o -> o.getActual() != null && o.getOwnertype().equals("Principal") ).count(); 
			 
			 newResponse.put("teacher_attendance", (teacherAttendance.intValue()/max) * 100 );
			 newResponse.put("teacher_management", (teacherAttendanceManagement.intValue()/maxManagement) * 100 );
			 newResponse.put("student_att", (studentAttendance.intValue()/maxStudent) * 100 );
			 newResponse.put("student_att_excused", (studentExcusedAttendance.intValue()/maxStudent) * 100 );
			 newResponse.put("head_admin", (headAdministration.intValue()/maxActivity) * 100 );
			 
			 return new ResponseEntity<>(newResponse, HttpStatus.OK);
		 }
		 catch (Exception ex) {
			 ex.printStackTrace();
		     return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "Error encountered."));
		 }
				
	 }
	 
	 private List<LocalDate> getWeekdayRange(int weekNumber, LocalDate startDate, LocalDate endDate) {
	        
		 	List<LocalDate> weekdayRange = new ArrayList<>();
	        
	        // Find the first Monday of the given week
	        LocalDate firstMonday = startDate.with(DayOfWeek.MONDAY);
	        
	        // Calculate the start date of the specified week
	        LocalDate weekStartDate = firstMonday.plusWeeks(weekNumber);
	        
	        // Calculate the end date of the specified week (Friday)
	        LocalDate weekEndDate = weekStartDate.plusDays(4);
	        
	        // Ensure the calculated week falls within the given date range
	        if (weekStartDate.isBefore(startDate)) {
	            weekStartDate = startDate;
	        }
	        
	        if (weekEndDate.isAfter(endDate)) {
	            weekEndDate = endDate;
	        }
	        
	        // Add each weekday (Monday to Friday) to the weekdayRange list
	        LocalDate currentDay = weekStartDate;
	        while (!currentDay.isAfter(weekEndDate)) {
	            weekdayRange.add(currentDay);
	            currentDay = currentDay.plusDays(1);
	        }
	        
	        System.out.println("weekrange--- : " + weekdayRange );
	        
	        return weekdayRange;
	    }
	 
	 private List<TimeTable> findUniqueTimetables(List<TimeTable> timetables) {
	        List<TimeTable> uniqueTimetables = new ArrayList<>();

	        for (TimeTable timetable : timetables) {
	            // Check if the timetable's subject and class stream are unique
	            boolean isUnique = true;

	            for (TimeTable uniqueTimetable : uniqueTimetables) {
	                if ( ( timetable.getSubject().getSubId() == uniqueTimetable.getSubject().getSubId() ) &&
	                     ( timetable.getClass_stream().getClsId() == uniqueTimetable.getClass_stream().getClsId() )  
	                   ) {
	                    isUnique = false;
	                    break;
	                }
	            }

	            if (isUnique) {
	                uniqueTimetables.add(timetable);
	            }
	        }

	        return uniqueTimetables;
	    }
	 
	 private List<TimeTable> findUniqueTimetablesByClassIndex(List<TimeTable> timetables) {
	        List<TimeTable> uniqueTimetables = new ArrayList<>();

	        for (TimeTable timetable : timetables) {
	            // Check if the timetable's subject and class stream are unique
	            boolean isUnique = true;

	            for (TimeTable uniqueTimetable : uniqueTimetables) {
	                if ( ( timetable.getSubject().getSubId() == uniqueTimetable.getSubject().getSubId() ) &&
	                     ( timetable.getClass_stream().getClass_index() == uniqueTimetable.getClass_stream().getClass_index() )  
	                   ) {
	                    isUnique = false;
	                    break;
	                }
	            }

	            if (isUnique) {
	                uniqueTimetables.add(timetable);
	            }
	        }

	        return uniqueTimetables;
	    }
}
