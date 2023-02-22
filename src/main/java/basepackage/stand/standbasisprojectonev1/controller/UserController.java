package basepackage.stand.standbasisprojectonev1.controller;

import basepackage.stand.standbasisprojectonev1.model.User;
import basepackage.stand.standbasisprojectonev1.payload.*;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.UserAccountRequest;
import basepackage.stand.standbasisprojectonev1.service.UserService;
import basepackage.stand.standbasisprojectonev1.util.AppConstants;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

	@Autowired
	 UserService service;
	 
	 @GetMapping
	 public ResponseEntity<?> getUsers() {
		 List<User> list = service.findAll();
		 return ResponseEntity.ok().body(new ApiContentResponse<User>(true, "List of Users gotten successfully.", list));		
	 }
	 
	 @GetMapping("/paginate")
	 public ResponseEntity<?> getPaginatedUsers(@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
			 @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
			 @RequestParam(value = "q",required=false) String query,
			 @RequestParam(value = "school", required=false) Optional<Long> school,
			 @RequestParam(value = "schoolgroup", required=false) Optional<Long> schoolgroup,
			 @RequestParam(value = "teacher", required=false) Optional<Long> teacher
			 ) {
		 
		 Map<String, Object> response = service.getPaginatedUsers( page, size, query, school, schoolgroup );
		 return new ResponseEntity<>(response, HttpStatus.OK);	        
	 }
	 
	 @GetMapping("/{id}")
	 public ResponseEntity<?> getUser(@PathVariable(value = "id") Long id) {
		 try {
			 User val = service.findUser(id);
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "User has been retrieved successfully.", val));
			
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @PutMapping("/{id}")
	 public ResponseEntity<?> updateUser(@PathVariable(value = "id") Long id, @RequestBody UserAccountRequest userRequest) {
		 try {
			 User val = service.update(userRequest,id);			 
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "User has been updated successfully.", val));	
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @DeleteMapping("/{id}")
	 public ResponseEntity<?> deleteUser(@PathVariable(value = "id") Long id) {
		 try {
			 User val = service.delete(id);
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "User has been deleted successfully.", val));				 
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 
	 }

}
