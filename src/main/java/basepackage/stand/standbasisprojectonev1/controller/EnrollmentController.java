package basepackage.stand.standbasisprojectonev1.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import basepackage.stand.standbasisprojectonev1.model.Enrollment;
import basepackage.stand.standbasisprojectonev1.model.EventManager;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.User;
import basepackage.stand.standbasisprojectonev1.payload.ApiContentResponse;
import basepackage.stand.standbasisprojectonev1.payload.ApiDataResponse;
import basepackage.stand.standbasisprojectonev1.payload.ApiResponse;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.EnrollmentRequest;
import basepackage.stand.standbasisprojectonev1.repository.EventManagerRepository;
import basepackage.stand.standbasisprojectonev1.repository.UserRepository;
import basepackage.stand.standbasisprojectonev1.security.UserPrincipal;
import basepackage.stand.standbasisprojectonev1.service.EnrollmentService;
import basepackage.stand.standbasisprojectonev1.util.AppConstants;

@RestController
@RequestMapping("/api/enrollment")
public class EnrollmentController {

	@Autowired
	 EnrollmentService service;
	 
	 @Autowired
	 private EventManagerRepository eventRepository;
	 
	 @Autowired
	 private UserRepository userRepository;
	 
	 @GetMapping
	 public ResponseEntity<?> getEnrollments() {
		 List<Enrollment> list = service.findAll();
		 return ResponseEntity.ok().body(new ApiContentResponse<Enrollment>(true, "List of Enrollments gotten successfully.", list));		
	 }
	 
	 @GetMapping("/class")
	 public ResponseEntity<?> getEnrollmentsFromClassId(  @RequestParam(value = "id") Long id ) {
		 List<Enrollment> list = service.findEnrollmentFromClass(id);
		 return ResponseEntity.ok().body(new ApiContentResponse<Enrollment>(true, "List of Enrollments by Class ID gotten successfully.", list));		
	 }
	 
	 @GetMapping("/classindex")
	 public ResponseEntity<?> getEnrollmentsFromClassIndex(  @RequestParam(value = "id") Integer id , @RequestParam(value = "sch") Long sch ) {
		 List<Enrollment> list = service.findEnrollmentFromClassIndex(id,sch);
		 return ResponseEntity.ok().body(new ApiContentResponse<Enrollment>(true, "List of Enrollments by Class Index gotten successfully.", list));		
	 }
	 
	 @GetMapping("/paginate")
	 public ResponseEntity<?> getPaginatedEnrollments(@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
			 @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
			 @RequestParam(value = "q") String query,
			 @RequestParam(value = "school", required=false) Optional<Long> school,
			 @RequestParam(value = "schoolgroup", required=false) Optional<Long> schoolgroup
			 ) {
		 
		 Map<String, Object> response = service.getPaginatedEnrollments( page, size, query, school, schoolgroup );
		 return new ResponseEntity<>(response, HttpStatus.OK);	        
	 }
	 
	 @GetMapping("/calendar")
	 public ResponseEntity<?> getEnrollmentsByCalendar(
			 @RequestParam(value = "calendar") Long calendar			 
			 ) {
		 try { 
		 List<Enrollment> list = service.getEnrollmentsByCalendar( calendar );
		 return ResponseEntity.ok().body(new ApiContentResponse<Enrollment>(true, "List of Enrollments by Calendar gotten successfully.", list));	        
		 }
		 catch (Exception ex) {
			 ex.printStackTrace();
			 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	}
	 
	 @GetMapping("/student/{id}")
	 public ResponseEntity<?> getEnrollmentsByPupilId( @PathVariable(value = "id") Long pupId ) {
		 try { 
			 List<Enrollment> list = service.findEnrollmentByPupil( pupId );
			 return ResponseEntity.ok().body(new ApiContentResponse<Enrollment>(true, "List of Enrollments by Pupil ID gotten successfully.", list));	        
		 }
		 catch (Exception ex) {
			 ex.printStackTrace();
			 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @GetMapping("/{id}")
	 public ResponseEntity<?> getEnrollment(@PathVariable(value = "id") Long id) {
		 try {
			 Enrollment val = service.findEnrollment(id);
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Enrollment has been retrieved successfully.", val));
			
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @PutMapping("/{id}")
	 public ResponseEntity<?> updateEnrollment( @AuthenticationPrincipal UserPrincipal userDetails, @PathVariable(value = "id") Long id, @RequestBody EnrollmentRequest teaRequest) {
		 try {
			 Enrollment val = service.update(teaRequest,id);
			 
			 Optional<User> u = userRepository.findById( userDetails.getId() );
				
			 //------------------------------------
			 saveEvent("enrolment", "edit", "The User with name: " + u.get().getName() + "has edited a Enrolment instance with ID:  " + val.getId(), 
					 new Date(), u.get(), u.get().getSchool() == null ? val.getStudent().getSchool() : u.get().getSchool()
			 );
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Enrollment has been updated successfully.", val));	
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @DeleteMapping("/{id}")
	 public ResponseEntity<?> deleteEnrollment( @AuthenticationPrincipal UserPrincipal userDetails, @PathVariable(value = "id") Long id) {
		 try {
			 Enrollment val = service.delete(id);
			 
			 Optional<User> u = userRepository.findById( userDetails.getId() );
				
			 //------------------------------------
			 saveEvent("enrolment", "delete", "The User with name: " + u.get().getName() + "has deleted a Enrolment instance with ID:  " + val.getId(), 
					 new Date(), u.get(), u.get().getSchool() == null ? val.getStudent().getSchool() : u.get().getSchool()
			 );
			 
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Enrollment has been deleted successfully.", val));				 
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 
	 }
	 
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
}
