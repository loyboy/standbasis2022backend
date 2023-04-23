package basepackage.stand.standbasisprojectonev1.controller;

import basepackage.stand.standbasisprojectonev1.model.EventManager;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.User;
import basepackage.stand.standbasisprojectonev1.payload.*;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.UserAccountRequest;
import basepackage.stand.standbasisprojectonev1.repository.EventManagerRepository;
import basepackage.stand.standbasisprojectonev1.repository.UserRepository;
import basepackage.stand.standbasisprojectonev1.security.UserPrincipal;
import basepackage.stand.standbasisprojectonev1.service.UserService;
import basepackage.stand.standbasisprojectonev1.util.AppConstants;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

	@Autowired
	 UserService service;
	
	@Autowired
	 private EventManagerRepository eventRepository;
	 
	 @Autowired
	 private UserRepository userRepository;
	 
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
			 @RequestParam(value = "schoolgroup", required=false) Optional<Long> schoolgroup
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
	 public ResponseEntity<?> updateUser(@AuthenticationPrincipal UserPrincipal userDetails, @PathVariable(value = "id") Long id, @RequestBody UserAccountRequest userRequest) {
		 try {
			 User val = service.update(userRequest,id);	
			 Optional<User> u = userRepository.findById( userDetails.getId() );				
			 //------------------------------------
			 saveEvent("user", "edit", "The User with name: " + u.get().getName() + "has deleted a user with ID:  " + val.getUserId(), 
					 new Date(), u.get(), u.get().getSchool()
			 );
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "User has been updated successfully.", val));	
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @DeleteMapping("/{id}")
	 public ResponseEntity<?> deleteUser(@AuthenticationPrincipal UserPrincipal userDetails, @PathVariable(value = "id") Long id) {
		 try {
			 User val = service.delete(id);
			 Optional<User> u = userRepository.findById( userDetails.getId() );
				
			 //------------------------------------
			 saveEvent("user", "delete", "The User with name: " + u.get().getName() + "has deleted a user with ID:  " + val.getUserId(), 
					 new Date(), u.get(), u.get().getSchool()
			 );
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "User has been deleted successfully.", val));				 
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
