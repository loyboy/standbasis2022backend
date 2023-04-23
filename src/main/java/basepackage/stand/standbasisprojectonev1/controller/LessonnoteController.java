package basepackage.stand.standbasisprojectonev1.controller;

import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import basepackage.stand.standbasisprojectonev1.model.Assessment;
import basepackage.stand.standbasisprojectonev1.model.AttendanceActivity;
import basepackage.stand.standbasisprojectonev1.model.EventManager;
import basepackage.stand.standbasisprojectonev1.model.Lessonnote;
import basepackage.stand.standbasisprojectonev1.model.LessonnoteActivity;
import basepackage.stand.standbasisprojectonev1.model.LessonnoteManagement;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.Subject;
import basepackage.stand.standbasisprojectonev1.model.User;
import basepackage.stand.standbasisprojectonev1.payload.ApiContentResponse;
import basepackage.stand.standbasisprojectonev1.payload.ApiDataResponse;
import basepackage.stand.standbasisprojectonev1.payload.ApiResponse;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.AssessmentRequest;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.LessonnoteActivityRequest;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.LessonnoteComposite;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.LessonnoteRequest;
import basepackage.stand.standbasisprojectonev1.repository.EventManagerRepository;
import basepackage.stand.standbasisprojectonev1.repository.UserRepository;
import basepackage.stand.standbasisprojectonev1.security.UserPrincipal;
import basepackage.stand.standbasisprojectonev1.service.AssessmentService;
import basepackage.stand.standbasisprojectonev1.service.LessonnoteActivityService;
import basepackage.stand.standbasisprojectonev1.service.LessonnoteManagementService;
import basepackage.stand.standbasisprojectonev1.service.LessonnoteService;
import basepackage.stand.standbasisprojectonev1.service.TimetableService;
import basepackage.stand.standbasisprojectonev1.util.AppConstants;
import basepackage.stand.standbasisprojectonev1.util.CommonActivity;
import basepackage.stand.standbasisprojectonev1.util.FileUploadUtil;

@RestController
@RequestMapping("/api/lessonnote")
public class LessonnoteController {

	 @Autowired
	 LessonnoteService service;
	 
	 @Autowired
	 TimetableService timeservice;
	 
	 @Autowired
	 LessonnoteActivityService serviceActivity;
	 
	 @Autowired
	 LessonnoteManagementService serviceManagement;	 
	 
	 @Autowired
	 AssessmentService serviceAssessment;
	 
	 @Autowired
	 private EventManagerRepository eventRepository;
	 
	 @Autowired
	 private UserRepository userRepository;
	 
	 private final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 
	 @GetMapping
	 public ResponseEntity<?> getLessonnotes() {
		 List<Lessonnote> list = service.findAll();
		 return ResponseEntity.ok().body(new ApiContentResponse<Lessonnote>(true, "List of Lessonnotes gotten successfully.", list));		
	 }	
	 
	 @GetMapping("/activity")
	 public ResponseEntity<?> getLessonnoteActivities() {
		 List<LessonnoteActivity> list = serviceActivity.findAll();
		 return ResponseEntity.ok().body(new ApiContentResponse<LessonnoteActivity>(true, "List of Lessonnotes Activity gotten successfully.", list));		
	 }	
	 
	 @GetMapping(value = {"/management", "/management/{lsn}"})
	 public ResponseEntity<?> getLessonnoteManagements(@PathVariable(value = "lsn", required = false) Long lsn) {		
		 List<LessonnoteManagement> list = null;
		 if (lsn != null) {
			 list = serviceManagement.findByLessonnote(lsn);
		 }
		 else {
			 list = serviceManagement.findAll();
		 }
		 return ResponseEntity.ok().body(new ApiContentResponse<LessonnoteManagement>(true, "List of Lessonnotes Management gotten successfully.", list));		
	 }
	 
	 @GetMapping("/notesWeek")
	 public ResponseEntity<?> getTeacherClassLessonnoteWeek(
			 @RequestParam(value = "teacher", required=false) Optional<Long> teacher,			 
			 @RequestParam(value = "week", required=false) Optional<Integer> week
	 ) {		 
		 Map<String, Object> response = service.getTeacherLessonnoteForWeek( teacher, week  );
		 return new ResponseEntity<>(response, HttpStatus.OK);	        
	 }
	 
	 @GetMapping("/paginateLessonnoteActivity")
	 public ResponseEntity<?> getPaginatedLessonnotesActivity(@RequestParam(value = "page", 
	 		 defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
			 @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
			 @RequestParam(value = "q", required=false) String query,
			 @RequestParam(value = "schoolgroup") Optional<Long> schoolgroup,
			 @RequestParam(value = "school", required=false) Optional<Long> school,
			 @RequestParam(value = "class", required=false) Optional<Integer> classid,
			 @RequestParam(value = "calendar", required=false) Optional<Long> calendar,
			 @RequestParam(value = "teacher", required=false) Optional<Long> teacher,
			 
			 @RequestParam(value = "subject", required=false) Optional<Long> subject,
			 @RequestParam(value = "status", required=false) Optional<String> status,
			 @RequestParam(value = "slip", required=false) Optional<Integer> slip,
			 @RequestParam(value = "lessonnote", required=false) Optional<Long> lessonnote,
			 
			 @RequestParam(value = "datefrom", required=false) Optional<Timestamp> datefrom,
			 @RequestParam(value = "dateto", required=false) Optional<Timestamp> dateto
			 ) {
		 
			 Map<String, Object> response = serviceActivity.getPaginatedTeacherLessonnotes ( page, size, query, schoolgroup, school, classid, calendar, teacher, subject, status, slip, lessonnote, datefrom, dateto  );
			 return new ResponseEntity<>(response, HttpStatus.OK);
	 }
	 
	 @GetMapping("/paginateTeachers")
	 public ResponseEntity<?> getPaginatedTeacherLessonnotes(@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
			 @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
			 @RequestParam(value = "q", required=false) String query,
			 @RequestParam(value = "schoolgroup") Optional<Long> schoolgroup,
			 @RequestParam(value = "school", required=false) Optional<Long> school,
			 @RequestParam(value = "class", required=false) Optional<Integer> classid,
			 @RequestParam(value = "week", required=false) Optional<Integer> week,
			 @RequestParam(value = "calendar", required=false) Optional<Long> calendar,
			 @RequestParam(value = "teacher", required=false) Optional<Long> teacher,
			 @RequestParam(value = "datefrom", required=false) Optional<Timestamp> datefrom,
			 @RequestParam(value = "dateto", required=false) Optional<Timestamp> dateto,
			 
			 @RequestParam(value = "subject", required=false) Optional<Long> subject,
			 @RequestParam(value = "status", required=false) Optional<String> status
			 ) {
		 
		 Map<String, Object> response = service.getPaginatedTeacherLessonnotes( page, size, query, schoolgroup, school, classid, week, calendar, teacher, subject, status, datefrom, dateto  );
		 return new ResponseEntity<>(response, HttpStatus.OK);	        
	 }
	 
	 @GetMapping("/teachers")
	 public ResponseEntity<?> getTeacherLessonnotes(
			 @RequestParam(value = "q", required=false) String query,
			 @RequestParam(value = "schoolgroup") Optional<Long> schoolgroup,
			 @RequestParam(value = "school", required=false) Optional<Long> school,
			 @RequestParam(value = "class", required=false) Optional<Integer> classid,
			 @RequestParam(value = "week", required=false) Optional<Integer> week,
			 @RequestParam(value = "calendar", required=false) Optional<Long> calendar,
			 @RequestParam(value = "teacher", required=false) Optional<Long> teacher,
			 @RequestParam(value = "subject", required=false) Optional<Long> subject,
			 @RequestParam(value = "datefrom", required=false) Optional<Timestamp> datefrom,
			 @RequestParam(value = "dateto", required=false) Optional<Timestamp> dateto
			 ) {
		 
		 Map<String, Object> response = service.getOrdinaryTeacherLessonnotes(query, schoolgroup, school, classid, week, calendar, teacher, subject, datefrom, dateto  );
		 return new ResponseEntity<>(response, HttpStatus.OK);	        
	 }
	 
	 @SuppressWarnings("unchecked")
	 @GetMapping("/mneTeachers")
	 public ResponseEntity<?> getMNETeacherLessonnotes(
			 @RequestParam(value = "q", required=false) String query,
			 @RequestParam(value = "schoolgroup" ) Optional<Long> schoolgroup,
			 @RequestParam(value = "school", required=false) Optional<Long> school,
			 @RequestParam(value = "class", required=false) Optional<Integer> classid,
			 @RequestParam(value = "week", required=false) Optional<Integer> week,
			 @RequestParam(value = "calendar", required=false) Optional<Long> calendar,
			 @RequestParam(value = "subject", required=false) Optional<Long> subject,
			 @RequestParam(value = "teacher", required=false) Optional<Long> teacher,
			 @RequestParam(value = "datefrom", required=false) Optional<Timestamp> datefrom,
			 @RequestParam(value = "dateto", required=false) Optional<Timestamp> dateto
			 ) {
		 
		 Map<String, Object> response = service.getOrdinaryTeacherLessonnotes(query, schoolgroup, school, classid, week, calendar, teacher, subject, datefrom, dateto  );
		 Map<String, Object> lsnManageResponse = serviceManagement.getOrdinaryTeacherLessonnotes(query, schoolgroup, school, classid, week, calendar, teacher, subject, datefrom, dateto);
			 
		 List<Lessonnote> ordinaryArray = (List<Lessonnote>) response.get("lessonnotes");
		 List<LessonnoteManagement> ordinaryArrayManagement = (List<LessonnoteManagement>) lsnManageResponse.get("lessonnotemanagement");
		 Map<String, Object> newResponse = new HashMap<>();
		 
		 Integer max = ordinaryArray.size(); 
		 Integer maxManage = ordinaryArrayManagement.size();
		// Long expectedLessonnotes = null;
		 
		/* Long teacherExpectedLessonnote = ordinaryArray.stream().filter(o -> { 
			 
			 List<TimeTable> classesTaught = timeservice.findClassTaught( o.getTeacher().getTeaId() , o.getCalendar().getCalendarId() );
			 Integer expectedclasses = classesTaught.size() * 12; // * 12 because you always have 12 weeks to teach
			 
			 
			 
			return false;
		 } ).count(); */
		 
		 Long teacherLowQuality = ordinaryArrayManagement.stream().filter(o -> o.getQuality() < 50).count(); 
		 Long teacherLateClosure = ordinaryArray.stream().filter(o -> { 			 
			 if (o.getLaunch() != null) {
				 Date thedate = new Date( o.getLaunch().getTime() );
				 return olderThanDays( thedate, 7 );
			 }
			 return false;			 
		 }).count(); 
		 Long teacherBadCycles = ordinaryArrayManagement.stream().filter(o -> o.getManagement() < 50).count(); 
		 Long teacherNoApproval = ordinaryArray.stream().filter(o -> o.getSubmission() != null && ( o.getApproval() == null && o.getRevert() == null ) ).count(); 
			
		 
		 newResponse.put("teacher_low_quality", convertPercentage(teacherLowQuality.intValue(),maxManage) );
		 newResponse.put("teacher_late_closure", convertPercentage(teacherLateClosure.intValue(),max) );
		 newResponse.put("teacher_bad_cycles", convertPercentage(teacherBadCycles.intValue(),maxManage) );
		 newResponse.put("teacher_no_approval", convertPercentage(teacherNoApproval.intValue(),max) );
			
		 return new ResponseEntity<>(newResponse, HttpStatus.OK);	        
	 }
	 
	 //
	 
	/* @GetMapping("/paginateTeachersLnm")
	 public ResponseEntity<?> getPaginatedTeacherLessonnoteManagement(@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
			 @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
			 @RequestParam(value = "q") String query,
			 @RequestParam(value = "schoolgroup", required=false) Optional<Long> schoolgroup,
			 @RequestParam(value = "school", required=false) Optional<Long> school,
			 @RequestParam(value = "class", required=false) Optional<Integer> classid,
			 @RequestParam(value = "calendar", required=false) Optional<Long> calendar,
			 @RequestParam(value = "teacher", required=false) Optional<Long> teacher,
			 @RequestParam(value = "datefrom", required=false) Optional<Timestamp> datefrom,
			 @RequestParam(value = "dateto", required=false) Optional<Timestamp> dateto
			 ) {
		 
		 Map<String, Object> response = serviceManagement.getPaginatedTeacherLessonnotes( page, size, query, schoolgroup, school, classid, calendar, teacher, datefrom, dateto  );
		 return new ResponseEntity<>(response, HttpStatus.OK);	        
	 }*/
	 
	 @GetMapping("/teachersLnm")
	 public ResponseEntity<?> getTeacherLessonnoteManagement(
			 @RequestParam(value = "q", required=false) String query,
			 @RequestParam(value = "schoolgroup") Optional<Long> schoolgroup,
			 @RequestParam(value = "school", required=false) Optional<Long> school,
			 @RequestParam(value = "class", required=false) Optional<Integer> classid,
			 @RequestParam(value = "calendar", required=false) Optional<Long> calendar,
			 @RequestParam(value = "subject", required=false) Optional<Long> subject,
			 @RequestParam(value = "week", required=false) Optional<Integer> week,
			 @RequestParam(value = "teacher", required=false) Optional<Long> teacher,
			 @RequestParam(value = "datefrom", required=false) Optional<Timestamp> datefrom,
			 @RequestParam(value = "dateto", required=false) Optional<Timestamp> dateto
			 ) {
		 
		 Map<String, Object> response = serviceManagement.getOrdinaryTeacherLessonnotes(query, schoolgroup, school, classid, week, calendar, teacher, subject, datefrom, dateto  );
		 return new ResponseEntity<>(response, HttpStatus.OK);	        
	 }
	 
	 @GetMapping("/teachersLna")
	 public ResponseEntity<?> getTeacherLessonnoteActivity(
			 @RequestParam(value = "q" , required=false) String query,
			 @RequestParam(value = "schoolgroup") Optional<Long> schoolgroup,
			 @RequestParam(value = "school", required=false) Optional<Long> school,
			 @RequestParam(value = "class", required=false) Optional<Integer> classid,
			 @RequestParam(value = "calendar", required=false) Optional<Long> calendar,
			 @RequestParam(value = "teacher", required=false) Optional<Long> teacher,
			 @RequestParam(value = "datefrom", required=false) Optional<Timestamp> datefrom,
			 @RequestParam(value = "dateto", required=false) Optional<Timestamp> dateto
			 ) {
		 
		 Map<String, Object> response = serviceActivity.getOrdinaryTeacherLessonnotes(query, schoolgroup, school, classid, calendar, teacher, datefrom, dateto  );
		 return new ResponseEntity<>(response, HttpStatus.OK);	        
	 }
	 
	 
	 //----------------------------------------------------------------------------------------------------------------
	 
	 @GetMapping("/paginateStudents")
	 public ResponseEntity<?> getPaginatedStudentLessonnotes(@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
			 @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
			 @RequestParam(value = "q", required=false) String query,
			 @RequestParam(value = "schoolgroup"  ) Optional<Long> schoolgroup,
			 @RequestParam(value = "school", required=false) Optional<Long> school,
			 @RequestParam(value = "class", required=false) Optional<Long> classid,			
			 @RequestParam(value = "calendar", required=false) Optional<Long> calendar,
			 @RequestParam(value = "teacher", required=false) Optional<Long> teacher,
			 @RequestParam(value = "student", required=false) Optional<Long> student,
			 @RequestParam(value = "score", required=false) Optional<Integer> score,
			 @RequestParam(value = "type", required=false) Optional<String> assesstype,
			 @RequestParam(value = "lessonnote", required=false) Optional<Long> lessonnote,
			 @RequestParam(value = "datefrom", required=false) Optional<Timestamp> datefrom,
			 @RequestParam(value = "dateto", required=false) Optional<Timestamp> dateto
			 ) {
		 
		 Map<String, Object> response = service.getPaginatedStudentLessonnotes( page, size, query, schoolgroup, school, classid, calendar, teacher, student, score, assesstype, lessonnote, datefrom, dateto  );
		 return new ResponseEntity<>(response, HttpStatus.OK);	        
	 }
	 
	 @GetMapping("/students")
	 public ResponseEntity<?> getStudentLessonnotes(
			 @RequestParam(value = "q", required=false) String query,
			 @RequestParam(value = "schoolgroup") Optional<Long> schoolgroup,
			 @RequestParam(value = "school", required=false) Optional<Long> school,
			 @RequestParam(value = "class", required=false) Optional<Long> classid,
			 @RequestParam(value = "week", required=false) Optional<Integer> week,
			 @RequestParam(value = "calendar", required=false) Optional<Long> calendar,
			 @RequestParam(value = "student", required=false) Optional<Long> student,
			 @RequestParam(value = "datefrom", required=false) Optional<Timestamp> datefrom,
			 @RequestParam(value = "dateto", required=false) Optional<Timestamp> dateto
			 ) 
	 {
		 
		 Map<String, Object> response = service.getOrdinaryStudentLessonnotes(query, schoolgroup, school, classid, week, calendar, student, datefrom, dateto  );
		 return new ResponseEntity<>(response, HttpStatus.OK);	        
	 }
	 
	 @GetMapping("/mneStudents")
	 public ResponseEntity<?> getMNEStudentLessonnotes(
			 @RequestParam(value = "q", required=false) String query,
			 @RequestParam(value = "schoolgroup") Optional<Long> schoolgroup,
			 @RequestParam(value = "school", required=false) Optional<Long> school,
			 @RequestParam(value = "class", required=false) Optional<Long> classid,
			 @RequestParam(value = "week", required=false) Optional<Integer> week,
			 @RequestParam(value = "calendar", required=false) Optional<Long> calendar,
			 @RequestParam(value = "teacher", required=false) Optional<Long> teacher,
			 @RequestParam(value = "datefrom", required=false) Optional<Timestamp> datefrom,
			 @RequestParam(value = "dateto", required=false) Optional<Timestamp> dateto
			 ) 
	 {
		 
		 Map<String, Object> response = serviceAssessment.getOrdinaryStudentlessonnotes(query, schoolgroup, school, classid, week, calendar, teacher, datefrom, dateto  );
		 
		 @SuppressWarnings("unchecked")
		 List<Assessment> ordinaryArray = (List<Assessment>) response.get("assessments");
		 Map<String, Object> newResponse = new HashMap<>();
		 
		 Integer max = ordinaryArray.size();
		 Long studentHomework = ordinaryArray.stream().filter( o -> o.getScore() != null && o.getScore() > 50 && o.get_type() == "hwk" ).count(); 
		 Long studentTest = ordinaryArray.stream().filter(o -> o.getScore() != null && o.getScore() > 50 && o.get_type() == "tst").count(); 
		 Long studentClasswork = ordinaryArray.stream().filter(o -> o.getScore() != null && o.getScore() > 50 && o.get_type() == "clw").count(); 
			 
		 newResponse.put("student_homework", convertPercentage(studentHomework.intValue(),max) );
		 newResponse.put("student_test", convertPercentage(studentTest.intValue(),max) );
		 newResponse.put("student_classwork", convertPercentage(studentClasswork.intValue(),max) );
		 
		 return new ResponseEntity<>(newResponse, HttpStatus.OK);	        
	 }
	 
	 @GetMapping("/{id}")
	 public ResponseEntity<?> getLessonnote(@PathVariable(value = "id") Long id) {
		 try {
			 Lessonnote val = service.findLessonnote(id);
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Lessonnote has been retrieved successfully.", val));			
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	//Teacher only
	@PutMapping("/status/{id}")
	public ResponseEntity<?> updateLessonnote( 
			 	 @AuthenticationPrincipal UserPrincipal userDetails,
				 @PathVariable(value = "id") Long id,
				 @RequestBody LessonnoteRequest lsnRequest 		 
		 ) {
		try { 
			 Lessonnote val = null;
			 if (lsnRequest.getAction().equals("closure") || lsnRequest.getAction().equals("launch") ) {
				 val = service.update(lsnRequest,id);
				 
				 Optional<User> u = userRepository.findById( userDetails.getId() );				
				 //------------------------------------
				 saveEvent("lessonnote", "edit", "The User with name: " + u.get().getName() + "has updated a lessonnote template status with ID:  " + id + " done by the Teacher ", 
						 new Date(), u.get(), u.get().getSchool()
				 );
			 }
			 
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Lessonnote has been edited successfully.", val));	
			 }
			 catch (Exception ex) {
				 //ex.printStackTrace();
		        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set. " + ex.getMessage()  ));
		
		    }
	  }
		 
	 //Teacher only
	 @PutMapping("/{id}")
	 public ResponseEntity<?> submitLessonnote( 
			 @AuthenticationPrincipal UserPrincipal userDetails,
			 @PathVariable(value = "id") Long id,
			 @RequestBody LessonnoteComposite lsnRequest 		 
	 ) {
		 try {			 
			 Lessonnote val = service.update(lsnRequest.getLessonnote(),id);
			 
			 Optional<User> u = userRepository.findById( userDetails.getId() );				
			 //------------------------------------
			 saveEvent("lessonnote", "edit", "The User with name: " + u.get().getName() + "has submitted a lessonnote template with ID:  " + id + " done by the Teacher " + val.getTeacher().getFname() + " " + val.getTeacher().getLname(), 
					 new Date(), u.get(), u.get().getSchool()
			 );
			 
			 if (val != null) {				 
				 
				 
				 if ( lsnRequest.getLessonnote().getAction().equals("resubmit") ) {				 
										 
					 LessonnoteActivityRequest lsnactivity = new LessonnoteActivityRequest();
					 lsnactivity.setOwnertype("Principal");
					 lsnactivity.setOwner(null);
					 lsnactivity.setExpected( addDays( CommonActivity.parseTimestamp( CommonActivity.todayDate()),1) );//One day expected
					 lsnactivity.setActivity("Expected to approve/revert this Lessonnote within (1) day");
					 serviceActivity.saveOne(lsnactivity, val);	
					 saveEvent("lessonnoteactivity", "create", "The User with name: " + u.get().getName() + "has created a lessonnote activity template with Lsn ID:  " + id + " done by the Teacher after submitting a Lessonnote " + val.getTeacher().getFname() + " " + val.getTeacher().getLname(), 
							 new Date(), u.get(), u.get().getSchool()
					 );
					 serviceActivity.update(lsnRequest.getActivity(),lsnRequest.getActivity().getLsnactivityId() );
					 saveEvent("lessonnoteactivity", "edit", "The User with name: " + u.get().getName() + "has updated a lessonnote activity template with Lsn ID:  " + id + " done by the Teacher after submitting a Lessonnote " + val.getTeacher().getFname() + " " + val.getTeacher().getLname(), 
							 new Date(), u.get(), u.get().getSchool()
					 );
				 }
				 else {				 
					 lsnRequest.getActivity().setOwnertype("Principal");
					 lsnRequest.getActivity().setExpected( addDays( CommonActivity.parseTimestamp( CommonActivity.todayDate()),2) );//Two days expected
					 
					 serviceActivity.saveOne(lsnRequest.getActivity(), val);
					 serviceManagement.saveOne(lsnRequest.getManagement(), val);
					 
					 saveEvent("lessonnoteactivity", "create", "The User with name: " + u.get().getName() + "has created a lessonnote activity template with Lsn ID:  " + id + " done by the Teacher after submitting a Lessonnote" + val.getTeacher().getFname() + " " + val.getTeacher().getLname(), 
							 new Date(), u.get(), u.get().getSchool()
					 );
					 
					 saveEvent("lessonnotemanagement", "create", "The User with name: " + u.get().getName() + "has created a lessonnote management template with Lsn ID:  " + id + " done by the Teacher after submitting a Lessonnote" + val.getTeacher().getFname() + " " + val.getTeacher().getLname(), 
							 new Date(), u.get(), u.get().getSchool()
					 );
				 }
			 } 
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Lessonnote has been edited successfully.", val));	
		 }
		 catch (Exception ex) {
			 //ex.printStackTrace();
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set." + ex.getMessage()  ));
	     }
	 }
	 
	 //Principal only
	 @PutMapping("/approve/{id}")
	 public ResponseEntity<?> approveLessonnote(
			 @AuthenticationPrincipal UserPrincipal userDetails,
			 @PathVariable(value = "id") Long id,			
			 @RequestBody LessonnoteComposite lsnRequest 			
	  ) {
		 // @PathVariable(value = "activity") Long lsnactivityId,
		 try {		 
			 
			 LessonnoteActivity lsnact  = serviceActivity.findLessonnoteActivityByLessonnote(id);// this gets only the latest lessonnote with this LSN ID
			 
			 Optional<User> u = userRepository.findById( userDetails.getId() );				
			 //------------------------------------
			 saveEvent("lessonnote", "edit", "The User with name: " + u.get().getName() + "has approved a lessonnote template with ID:  " + id + " approved by the Principal after approving a Lessonnote", 
					 new Date(), u.get(), u.get().getSchool()
			 );
			 
			 Lessonnote val = null;
			 if ( lsnRequest.getActivity().getAction().equals("revert") ) {				 
				 val = service.update(lsnRequest.getLessonnote(),id);
				 
				 LessonnoteActivityRequest lsnactivity = new LessonnoteActivityRequest();
				 lsnactivity.setOwnertype("Teacher");
				 lsnactivity.setOwner(val.getTeacher().getTeaId());
				 lsnactivity.setExpected( addDays( CommonActivity.parseTimestamp( CommonActivity.todayDate()),2) );
				 lsnactivity.setActivity("Expected to re-submit this Lessonnote within (2) days");
				 lsnactivity.setComment_query(lsnRequest.getActivity().getComment_query());
				 serviceActivity.saveOne(lsnactivity, val);
				 
				 saveEvent("lessonnoteactivity", "create", "The User with name: " + u.get().getName() + "has created a lessonnote activity template with Lsn ID:  " + id + " done by the Principal after approving a Lessonnote" + val.getTeacher().getFname() + " " + val.getTeacher().getLname(), 
						 new Date(), u.get(), u.get().getSchool()
				 );
				 
				 serviceActivity.update(lsnRequest.getActivity(),lsnact.getLsnactId());	
				 saveEvent("lessonnoteactivity", "edit", "The User with name: " + u.get().getName() + "has updated a lessonnote activity template with Lsn ID:  " + id + " done by the Principal after approving a Lessonnote" + val.getTeacher().getFname() + " " + val.getTeacher().getLname(), 
						 new Date(), u.get(), u.get().getSchool()
				 );
			 }
			 
			 else {				
				 val = service.update(lsnRequest.getLessonnote(),id);
				 serviceActivity.update(lsnRequest.getActivity(),lsnact.getLsnactId());
				 saveEvent("lessonnoteactivity", "edit", "The User with name: " + u.get().getName() + "has updated a lessonnote activity template with Lsn ID:  " + id + " done by the Principal after approving a Lessonnote" + val.getTeacher().getFname() + " " + val.getTeacher().getLname(), 
						 new Date(), u.get(), u.get().getSchool()
				 );
				 serviceManagement.update(lsnRequest.getManagement(),id);
			 }				 
			 
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Lessonnote has been updated successfully.", val));	
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set." + ex.getMessage() ));
	     }
	 }
	 
	 @PostMapping("/assessment")
	 public ResponseEntity<?> submitScores( 
			 @AuthenticationPrincipal UserPrincipal userDetails,
			 @RequestBody AssessmentRequest assRequest		 
	 ) {
		 try {			 
			 Assessment val = serviceAssessment.saveOne(assRequest);
			 
			 Optional<User> u = userRepository.findById( userDetails.getId() );
				
			 //------------------------------------
			 saveEvent("assessment", "create", "The User with name: " + u.get().getName() + "has created a assessment score with ID:  " + val.getAssessId(), 
					 new Date(), u.get(), u.get().getSchool()
			 );
			 
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Assessment has been created successfully.", val));	
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @PutMapping("/assessment/{id}")
	 public ResponseEntity<?> updateScores(
			 @AuthenticationPrincipal UserPrincipal userDetails,
			 @PathVariable(value = "id") Long id, 
			 @RequestBody AssessmentRequest assRequest		 
	 ) {
		 try {			 
			 Assessment val = serviceAssessment.update(assRequest, id);	
			 
			 Optional<User> u = userRepository.findById( userDetails.getId() );
				
			 //------------------------------------
			 saveEvent("assessment", "edit", "The User with name: " + u.get().getName() + "has updated a assessment score with ID:  " + val.getAssessId(), 
					 new Date(), u.get(), u.get().getSchool()
			 );
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Assessment has been updated successfully.", val));	
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @RequestMapping(path = "/file/{id}", method = PUT, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	 public ResponseEntity<?> updateLessonnoteFile(@PathVariable(value = "id") Long id, @RequestPart("lsn") MultipartFile multipartFile) {
		 try {
			 	 String fileOriginalName = StringUtils.cleanPath(multipartFile.getOriginalFilename());		         
			 	 String fileName = "0"+ id.toString() + "." + FileUploadUtil.findExtension(fileOriginalName).get();
			 	
		         String uploadDir = "teacher-lessonnote/" + id;		 
		         FileUploadUtil.saveFile(uploadDir, fileName , multipartFile);
		        
				 String val = service.updateFile(id, fileName);			 
				 return ResponseEntity.ok().body(new ApiDataResponse(true, "Teacher Lessonnote File has been added/updated successfully.", val));	
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 
	 
	 //---------------------------------------------------------------------------------------------
	 private EventManager saveEvent( String module, String action, String comment, Date d, User u, School sch ) {		 
		 	
		 	EventManager _event = new EventManager();
		 	
		 	_event.setModule(module);
	 		_event.setAction(action);
	 		_event.setComment(comment);
	 		_event.setDateofevent(d);	
	 		_event.setUser(u);
	 		_event.setSchool(sch);
	 		
	 		return eventRepository.save(_event);
	 }
	 
	 private Integer convertPercentage( Integer actual, Integer max ) {		 
		 return actual == 0 ? 0 : ( (actual/max) * 100 );
	 }
	 	 
	 private Timestamp addDays(Timestamp date, int days) {
	        Calendar cal = Calendar.getInstance();
	        cal.setTime(date);
	        cal.add(Calendar.DATE, days); //minus number would decrement the days
	        return new Timestamp(cal.getTime().getTime());
	 }
	 
	 private boolean olderThanDays(Date givenDate, int days)
	 {
	   long currentMillis = new Date().getTime();
	   long millisInDays = days * 24 * 60 * 60 * 1000;
	   boolean result = givenDate.getTime() < (currentMillis - millisInDays);
	   return result;
	 }
	 
}
