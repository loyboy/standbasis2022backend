package basepackage.stand.standbasisprojectonev1.controller;

import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.sql.Timestamp;
import java.text.ParseException;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import basepackage.stand.standbasisprojectonev1.model.Attendance;
import basepackage.stand.standbasisprojectonev1.model.AttendanceActivity;
import basepackage.stand.standbasisprojectonev1.model.AttendanceManagement;
import basepackage.stand.standbasisprojectonev1.model.Rowcall;
import basepackage.stand.standbasisprojectonev1.payload.ApiContentResponse;
import basepackage.stand.standbasisprojectonev1.payload.ApiDataResponse;
import basepackage.stand.standbasisprojectonev1.payload.ApiResponse;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.AttendanceComposite;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.AttendanceManagementRequest;
import basepackage.stand.standbasisprojectonev1.service.AttendanceActivityService;
import basepackage.stand.standbasisprojectonev1.service.AttendanceManagementService;
import basepackage.stand.standbasisprojectonev1.service.AttendanceService;
import basepackage.stand.standbasisprojectonev1.service.UserService;
import basepackage.stand.standbasisprojectonev1.util.AppConstants;
import basepackage.stand.standbasisprojectonev1.util.FileUploadUtil;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

	 @Autowired
	 AttendanceService service;
	 
	 @Autowired
	 AttendanceActivityService serviceActivity;
	 
	 @Autowired
	 AttendanceManagementService serviceManagement;
	 
	 @Autowired
	 UserService serviceUser;
	 
	 private final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 
	 @GetMapping
	 public ResponseEntity<?> getAttendances() {
		 List<Attendance> list = service.findAll();
		 return ResponseEntity.ok().body(new ApiContentResponse<Attendance>(true, "List of Attendances gotten successfully.", list));		
	 }	
	 
	 @GetMapping("/paginateAttendanceActivity")
	 public ResponseEntity<?> getPaginatedAttendancesActivity(@RequestParam(value = "page", 
	 		 defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
			 @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
			 @RequestParam(value = "q", required=false) String query,
			 @RequestParam(value = "schoolgroup") Optional<Long> schoolgroup,
			 @RequestParam(value = "school", required=false) Optional<Long> school,
			 @RequestParam(value = "class", required=false) Optional<Long> classid,
			 @RequestParam(value = "calendar", required=false) Optional<Long> calendar,
			 @RequestParam(value = "teacher", required=false) Optional<Long> teacher,
			 @RequestParam(value = "subject", required=false) Optional<Long> subject,
			 @RequestParam(value = "status", required=false) Optional<String> status,
			 @RequestParam(value = "slip", required=false) Optional<Integer> slip,
			 @RequestParam(value = "attendance", required=false) Optional<Long> attendance,
			 @RequestParam(value = "datefrom", required=false) Optional<Timestamp> datefrom,
			 @RequestParam(value = "dateto", required=false) Optional<Timestamp> dateto
			 ) {
		 
			 Map<String, Object> response = serviceActivity.getPaginatedTeacherAttendances ( page, size, query, schoolgroup, school, classid, calendar, teacher, subject, status, slip, attendance, datefrom, dateto  );
			 return new ResponseEntity<>(response, HttpStatus.OK);
	 }
	 
	 @GetMapping("/paginateTeachers")
	 public ResponseEntity<?> getPaginatedTeacherAttendances(@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
			 @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
			 @RequestParam(value = "q", required=false) String query,
			 @RequestParam(value = "schoolgroup") Optional<Long> schoolgroup,
			 @RequestParam(value = "school", required=false) Optional<Long> school,
			 @RequestParam(value = "class", required=false) Optional<Long> classid,
			 @RequestParam(value = "calendar", required=false) Optional<Long> calendar,
			 @RequestParam(value = "teacher", required=false) Optional<Long> teacher,
			 @RequestParam(value = "subject", required=false) Optional<Long> subject,
			 @RequestParam(value = "status", required=false) Optional<Integer> status,
			 @RequestParam(value = "datefrom", required=false) Optional<String> datefrom,
			 @RequestParam(value = "dateto", required=false) Optional<String> dateto
			 ) {
		 
		 	 Date datefrom1 = null;  
		 	 Date dateto1 = null;
		 				 
		 	 if(datefrom.isPresent()) {
				  try {
					datefrom1 = DATE_TIME_FORMAT.parse(datefrom.get());
				} catch (ParseException e) {					
					e.printStackTrace();
				}
			 }
			 if(dateto.isPresent()) {
				  try {
					dateto1 = DATE_TIME_FORMAT.parse(dateto.get());
				} catch (ParseException e) {					
					e.printStackTrace();
				}
			 }
			 Map<String, Object> response = service.getPaginatedTeacherAttendances( page, size, query, schoolgroup, school, classid, calendar, teacher, subject, status, datefrom1, dateto1  );
			 return new ResponseEntity<>(response, HttpStatus.OK);	        
	 }
	 
	 @GetMapping("/teachers")
	 public ResponseEntity<?> getTeacherAttendances(
			 @RequestParam(value = "q", required=false ) String query,
			 @RequestParam(value = "schoolgroup") Optional<Long> schoolgroup,
			 @RequestParam(value = "school", required=false) Optional<Long> school,
			 @RequestParam(value = "class", required=false) Optional<Long> classid,
			 @RequestParam(value = "calendar", required=false) Optional<Long> calendar,
			 @RequestParam(value = "teacher", required=false) Optional<Long> teacher,
			 @RequestParam(value = "subject", required=false) Optional<Long> subject,
			 @RequestParam(value = "datefrom", required=false) Optional<Timestamp> datefrom,
			 @RequestParam(value = "dateto", required=false) Optional<Timestamp> dateto
			 ) {
		 
		 Map<String, Object> response = service.getOrdinaryTeacherAttendances(query, schoolgroup, school, classid, calendar, teacher, subject, datefrom, dateto  );
		 return new ResponseEntity<>(response, HttpStatus.OK);	        
	 }
	 
	 @SuppressWarnings("unchecked")
	@GetMapping("/mneTeachers")
	 public ResponseEntity<?> getMNETeacherAttendances(
			 @RequestParam(value = "q", required=false) String query,
			 @RequestParam(value = "schoolgroup") Optional<Long> schoolgroup,
			 @RequestParam(value = "school", required=false) Optional<Long> school,
			 @RequestParam(value = "class", required=false) Optional<Long> classid,
			 @RequestParam(value = "calendar", required=false) Optional<Long> calendar,
			 @RequestParam(value = "teacher", required=false) Optional<Long> teacher,
			 @RequestParam(value = "subject", required=false) Optional<Long> subject,
			 
			 @RequestParam(value = "datefrom", required=false) Optional<Timestamp> datefrom,
			 @RequestParam(value = "dateto", required=false) Optional<Timestamp> dateto
			 ) {
		 
		 Map<String, Object> response = service.getOrdinaryTeacherAttendances(query, schoolgroup, school, classid, calendar, teacher, subject, datefrom, dateto  );
		 Map<String, Object> attManageResponse = serviceManagement.getOrdinaryTeacherAttendances(query, schoolgroup, school, classid, calendar, teacher, subject, datefrom, dateto);
		 
		 List<Attendance> ordinaryArray = (List<Attendance>) response.get("attendances");
		 List<AttendanceManagement> ordinaryArrayManagement = (List<AttendanceManagement>) attManageResponse.get("attendancemanagement");
		 Map<String, Object> newResponse = new HashMap<>();
		 
		 Integer max = ordinaryArray.size(); 
		 Integer maxManage = ordinaryArrayManagement.size();
		 
		 Long teacherAttendance = ordinaryArray.stream().filter(o -> o.getDone() == 1).count(); 
		 Long classAttendance = ordinaryArrayManagement.stream().filter(o -> o.getClass_perf() > 0).count(); 
		 Long lateAttendance = ordinaryArrayManagement.stream().filter(o -> o.getTiming() == 50).count(); 
		 Long completenessAttendance = ordinaryArrayManagement.stream().filter(o -> o.getCompleteness() == 100).count(); 
			 
		 newResponse.put("teacher_attendance", convertPercentage(teacherAttendance.intValue(),max) );
		 newResponse.put("class_attendance", convertPercentage(classAttendance.intValue(),maxManage) );
		 newResponse.put("late_attendance", convertPercentage(lateAttendance.intValue(),maxManage) );
		 newResponse.put("completeness_attendance", convertPercentage(completenessAttendance.intValue(),maxManage) );
			
		 return new ResponseEntity<>(newResponse, HttpStatus.OK);	        
	 }
	 
	 
	 @GetMapping("/paginateStudents")
	 public ResponseEntity<?> getPaginatedStudentAttendances(@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
			 @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
			 @RequestParam(value = "q", required=false) String query,
			 @RequestParam(value = "schoolgroup" ) Optional<Long> schoolgroup,
			 @RequestParam(value = "school", required=false) Optional<Long> school,
			 @RequestParam(value = "class", required=false) Optional<Long> classid,
			 @RequestParam(value = "calendar", required=false) Optional<Long> calendar,
			 @RequestParam(value = "teacher", required=false) Optional<Long> teacher,
			 @RequestParam(value = "subject", required=false) Optional<Long> subject,
			 @RequestParam(value = "status", required=false) Optional<Integer> status,
			 @RequestParam(value = "student", required=false) Optional<Long> student,
			 @RequestParam(value = "attendance", required=false) Optional<Long> attendance,
			 @RequestParam(value = "datefrom", required=false) Optional<Timestamp> datefrom,
			 @RequestParam(value = "dateto", required=false) Optional<Timestamp> dateto
			 ) {
		 
		 Map<String, Object> response = service.getPaginatedStudentAttendances( page, size, query, schoolgroup, school, classid, calendar, teacher, subject, status, student, attendance, datefrom, dateto  );
		 return new ResponseEntity<>(response, HttpStatus.OK);	        
	 }
	 
	 @GetMapping("/students")
	 public ResponseEntity<?> getStudentAttendances(
			 @RequestParam(value = "q", required=false) String query,
			 @RequestParam(value = "schoolgroup") Optional<Long> schoolgroup,
			 @RequestParam(value = "school", required=false) Optional<Long> school,
			 @RequestParam(value = "class", required=false) Optional<Long> classid,
			 @RequestParam(value = "calendar", required=false) Optional<Long> calendar,
			 @RequestParam(value = "teacher", required=false) Optional<Long> teacher,
			 @RequestParam(value = "datefrom", required=false) Optional<Timestamp> datefrom,
			 @RequestParam(value = "dateto", required=false) Optional<Timestamp> dateto
			 ) 
	 {
		 
		 Map<String, Object> response = service.getOrdinaryStudentAttendances(query, schoolgroup, school, classid, calendar, teacher, datefrom, dateto  );
		 return new ResponseEntity<>(response, HttpStatus.OK);	        
	 }
	 
	 @GetMapping("/mneStudents")
	 public ResponseEntity<?> getMNEStudentAttendances(
			 @RequestParam(value = "q", required=false) String query,
			 @RequestParam(value = "schoolgroup") Optional<Long> schoolgroup,
			 @RequestParam(value = "school", required=false) Optional<Long> school,
			 @RequestParam(value = "class", required=false) Optional<Long> classid,
			 @RequestParam(value = "calendar", required=false) Optional<Long> calendar,
			 @RequestParam(value = "teacher", required=false) Optional<Long> teacher,
			 @RequestParam(value = "datefrom", required=false) Optional<Timestamp> datefrom,
			 @RequestParam(value = "dateto", required=false) Optional<Timestamp> dateto
			 ) 
	 {
		 
		 Map<String, Object> response = service.getOrdinaryStudentAttendances(query, schoolgroup, school, classid, calendar, teacher, datefrom, dateto  );
		 
		 @SuppressWarnings("unchecked")
		 List<Rowcall> ordinaryArray = (List<Rowcall>) response.get("attendances");
		 Map<String, Object> newResponse = new HashMap<>();
		 
		 Integer max = ordinaryArray.size();
		 Long studentAttendance = ordinaryArray.stream().filter(o -> o.getStatus() == 1).count(); 
		 Long studentAbsent = ordinaryArray.stream().filter(o -> o.getStatus() == 0).count(); 
		 Long studentExcused = ordinaryArray.stream().filter(o -> o.getStatus() == 2).count(); 
			 
		 newResponse.put("student_attendance", convertPercentage(studentAttendance.intValue(),max) );
		 newResponse.put("student_absent", convertPercentage(studentAbsent.intValue(),max) );
		 newResponse.put("student_excused", convertPercentage(studentExcused.intValue(),max) );
		 
		 return new ResponseEntity<>(newResponse, HttpStatus.OK);	        
	 }
	 
	 @GetMapping("/{id}")
	 public ResponseEntity<?> getAttendance(@PathVariable(value = "id") Long id) {
		 try {
			 Attendance val = service.findAttendance(id);
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Attendance has been retrieved successfully.", val));
			
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @GetMapping("/management/{id}")
	 public ResponseEntity<?> getAttendanceManagement(@PathVariable(value = "id") Long id) {
		 try {
			 AttendanceManagement val = serviceManagement.findAttendanceManagementByAttendance(id);
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Attendance Management has been retrieved successfully.", val));
			
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @PutMapping("/management/attendance/{id}")
	 public ResponseEntity<?> updateAttendanceManagement(@PathVariable(value = "id") Long id, 
			 @RequestBody AttendanceComposite attRequest
			 ) {
		 try {
			 AttendanceManagement val = serviceManagement.updateByAttendance(attRequest.getManagement(), id);
			 //get Activity ID
			 AttendanceActivity val2  = serviceActivity.findAttendanceActivityByAttendance(id);
			 //then Update the activity
			 AttendanceActivity attval = serviceActivity.update(attRequest.getActivity(), val2.getAttactId());
			 
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Attendance Management and Activity has been updated successfully.", val));
			
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @GetMapping("/activity/{id}")
	 public ResponseEntity<?> getAttendanceActivity(@PathVariable(value = "id") Long id) {
		 try {
			 AttendanceActivity val = serviceActivity.findAttendanceActivityByAttendance(id);
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Attendance Activity has been retrieved successfully.", val));
			
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 //findAttendanceActivity
	 
	 //Teacher only
	 @PutMapping("/{id}")
	 public ResponseEntity<?> updateAttendance( @PathVariable(value = "id") Long id, 
			 @RequestBody AttendanceComposite attRequest
	 ) {
		 try {
			 Attendance val = service.update(attRequest.getAttendance(),id);
			 if (val != null) {
				
				 attRequest.getActivity().setOwnertype("Principal");
				 attRequest.getActivity().setExpected( addDays( parseTimestamp(todayDate()),2) );
				 
				 serviceActivity.saveOne(attRequest.getActivity(), val);
				 
				 serviceManagement.saveOne(attRequest.getManagement(), val);
			 }	
			 if (val != null) {
				 return ResponseEntity.ok().body(new ApiDataResponse(true, "Attendance has been updated successfully.", val));	
			 }
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Attendance update was halted.", "Attendance has been taken already."));
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 //Done by Principal
	 @PutMapping("/approve/{activity}")
	 public ResponseEntity<?> approveAttendance(
			 @PathVariable(value = "activity") Long attactivityId , 
			 @RequestBody AttendanceComposite attRequest
	 ) {
		 try {
						 
			 AttendanceActivity attval = serviceActivity.update(attRequest.getActivity(), attactivityId);	
				//update the attendance management
			 
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Attendance activity has been approved/denied successfully.", attval));	
		 }
		 catch (Exception ex) {
			 ex.printStackTrace();
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."  ));
	     }
	 }
	 
	 @RequestMapping(path = "/file/{id}", method = PUT, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	 public ResponseEntity<?> updateAttendancePhoto(@PathVariable(value = "id") Long id, @RequestPart("lsn") MultipartFile multipartFile) {
		 try {
			 	 String fileOriginalName = StringUtils.cleanPath(multipartFile.getOriginalFilename());		         
			 	 String fileName = "0"+ id.toString() + "." + FileUploadUtil.findExtension(fileOriginalName).get();
			 	
		         String uploadDir = "teacher-attendance/" + id;		 
		         FileUploadUtil.saveFile(uploadDir, fileName , multipartFile);
		        
				 String val = service.updatePhoto(id, fileName);			 
				 return ResponseEntity.ok().body(new ApiDataResponse(true, "Teacher Attendance Photo has been added/updated successfully.", val));	
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 
	 
	 
	 
	 
	 
	 //------------------------------------------------------------------------------------------------------------------
	 private Integer convertPercentage( Integer actual, Integer max ) {		 
		 return actual == 0 ? 0 : ( (actual/max) * 100 );
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
	 
	 private Timestamp addDays(Timestamp date, int days) {
	        Calendar cal = Calendar.getInstance();
	        cal.setTime(date);
	        cal.add(Calendar.DATE, days); //minus number would decrement the days
	        return new Timestamp(cal.getTime().getTime());
	 }
	 
	
}
