package basepackage.stand.standbasisprojectonev1.controller;

import java.util.ArrayList;
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

import basepackage.stand.standbasisprojectonev1.model.EventManager;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.Student;
import basepackage.stand.standbasisprojectonev1.model.User;
import basepackage.stand.standbasisprojectonev1.payload.ApiContentResponse;
import basepackage.stand.standbasisprojectonev1.payload.ApiDataResponse;
import basepackage.stand.standbasisprojectonev1.payload.ApiResponse;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.StudentRequest;
import basepackage.stand.standbasisprojectonev1.repository.EventManagerRepository;
import basepackage.stand.standbasisprojectonev1.repository.UserRepository;
import basepackage.stand.standbasisprojectonev1.security.UserPrincipal;
import basepackage.stand.standbasisprojectonev1.service.StudentService;
import basepackage.stand.standbasisprojectonev1.util.AppConstants;

@RestController
@RequestMapping("/api/student")
public class StudentController {

	 @Autowired
	 StudentService service;
	 
	 @Autowired
	 private EventManagerRepository eventRepository;
	 
	 @Autowired
	 private UserRepository userRepository;
	 
	 @GetMapping
	 public ResponseEntity<?> getGroupSchools() {
		 List<Student> list = service.findAll();
		 return ResponseEntity.ok().body(new ApiContentResponse<Student>(true, "List of school groups gotten successfully.", list));		
	 }
	 
	 @GetMapping("/school/{id}")
	 public ResponseEntity<?> getStudentsBySchool( @PathVariable(value = "id") Long id ) {
		 List<Student> list = service.findAllBySchool(id);
		 return ResponseEntity.ok().body(new ApiContentResponse<Student>(true, "List of students by school gotten successfully.", list));		
	 }
	 
	 @GetMapping("/school/count/{id}")
	 public ResponseEntity<?> getCountBySchool( @PathVariable(value = "id") Long id ) {
		 Long count = service.countBySchool(id);
		 List<Long> list = new ArrayList<Long>();
		 list.add(count);
		 return ResponseEntity.ok().body(new ApiContentResponse<Long>(true, "Count of students by school gotten successfully.", list));		
	 }
	 
	 @GetMapping("/group/count/{id}")
	 public ResponseEntity<?> getCountFromSchoolGroup( @PathVariable(value = "id") Long id ) {
		 Long count = service.countBySchoolGroup(id);
		 List<Long> list = new ArrayList<Long>();
		 list.add(count);
		 return ResponseEntity.ok().body(new ApiContentResponse<Long>(true, "Count of students by group gotten successfully.", list));		
	 }
	 
	 @GetMapping("/paginate")
	 public ResponseEntity<?> getPaginatedGroupSchools(@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
			 @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
			 @RequestParam(value = "q") String query
			 ) {
		
		 Map<String, Object> response = service.getPaginatedStudents( page, size, query );
		 return new ResponseEntity<>(response, HttpStatus.OK);	        
	 }
	 
	 @GetMapping("/{id}")
	 public ResponseEntity<?> getStudent(@PathVariable(value = "id") Long id) {
		 try {
			 Student val = service.findStudent(id);
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Student data has been retrieved successfully.", val));
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @PutMapping("/{id}")
	 public ResponseEntity<?> updateStudent(@AuthenticationPrincipal UserPrincipal userDetails,@PathVariable(value = "id") Long id, @RequestBody StudentRequest stuRequest) {
		 try {
			 Student val = service.update(stuRequest,id);
			 Optional<User> u = userRepository.findById( userDetails.getId() );
				
			 //------------------------------------
			 saveEvent("student", "edit", "The User with name: " + u.get().getName() + "has upDATED a pupil with ID:  " + val.getId(), 
					 new Date(), u.get(), u.get().getSchool()
			 );
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Student data has been updated successfully.", val));	
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @DeleteMapping("/{id}")
	 public ResponseEntity<?> deleteStudent(@AuthenticationPrincipal UserPrincipal userDetails,@PathVariable(value = "id") Long id) {
		 try {
			 Student val = service.delete(id);
			 
			 Optional<User> u = userRepository.findById( userDetails.getId() );
				
			 //------------------------------------
			 saveEvent("student", "delete", "The User with name: " + u.get().getName() + "has deleted a pupil with ID:  " + val.getId(), 
					 new Date(), u.get(), u.get().getSchool()
			 );
			 
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Student data has been deleted successfully.", val));				 
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
