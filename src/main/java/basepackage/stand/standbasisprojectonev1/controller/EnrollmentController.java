package basepackage.stand.standbasisprojectonev1.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import basepackage.stand.standbasisprojectonev1.model.Enrollment;
import basepackage.stand.standbasisprojectonev1.payload.ApiContentResponse;
import basepackage.stand.standbasisprojectonev1.payload.ApiDataResponse;
import basepackage.stand.standbasisprojectonev1.payload.ApiResponse;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.EnrollmentRequest;
import basepackage.stand.standbasisprojectonev1.service.EnrollmentService;
import basepackage.stand.standbasisprojectonev1.util.AppConstants;

@RestController
@RequestMapping("/api/enrollment")
public class EnrollmentController {

	@Autowired
	 EnrollmentService service;
	 
	 @GetMapping
	 public ResponseEntity<?> getEnrollments() {
		 List<Enrollment> list = service.findAll();
		 return ResponseEntity.ok().body(new ApiContentResponse<Enrollment>(true, "List of Enrollments gotten successfully.", list));		
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
	 public ResponseEntity<?> updateEnrollment(@PathVariable(value = "id") Long id, @RequestBody EnrollmentRequest teaRequest) {
		 try {
			 Enrollment val = service.update(teaRequest,id);			 
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Enrollment has been updated successfully.", val));	
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @DeleteMapping("/{id}")
	 public ResponseEntity<?> deleteEnrollment(@PathVariable(value = "id") Long id) {
		 try {
			 Enrollment val = service.delete(id);
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Enrollment has been deleted successfully.", val));				 
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 
	 }
}
