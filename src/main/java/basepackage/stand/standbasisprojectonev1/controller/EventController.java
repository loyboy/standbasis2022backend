package basepackage.stand.standbasisprojectonev1.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import basepackage.stand.standbasisprojectonev1.model.EventManager;
import basepackage.stand.standbasisprojectonev1.payload.ApiContentResponse;
import basepackage.stand.standbasisprojectonev1.payload.ApiDataResponse;
import basepackage.stand.standbasisprojectonev1.payload.ApiResponse;
import basepackage.stand.standbasisprojectonev1.service.EventManagerService;
import basepackage.stand.standbasisprojectonev1.util.AppConstants;

@RestController
@RequestMapping("/api/event")
public class EventController {

	@Autowired
	 EventManagerService service;
	 
	 @GetMapping
	 public ResponseEntity<?> getEvents() {
		 List<EventManager> list = service.findAll();
		 return ResponseEntity.ok().body(new ApiContentResponse<EventManager>(true, "List of Events gotten successfully.", list));		
	 }
	 
	 @GetMapping("/school/{id}")
	 public ResponseEntity<?> getEventsBySchool( @PathVariable(value = "id") Long id ) {
		 List<EventManager> list = service.findAllBySchool(id);
		 return ResponseEntity.ok().body(new ApiContentResponse<EventManager>(true, "List of Events by school gotten successfully.", list));		
	 }
	 
	 
	 @GetMapping("/paginate")
	 public ResponseEntity<?> getPaginatedEvents(@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
			 @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
			 @RequestParam(value = "q", required=false) String query,
			 @RequestParam(value = "module", required=false) String module,
			 @RequestParam(value = "school", required=false) Optional<Long> school,
			 @RequestParam(value = "schoolgroup", required=false) Optional<Long> schoolgroup
			 ) {
		 
		 Map<String, Object> response = service.getPaginatedEvents( page, size, query, module, school, schoolgroup );
		 return new ResponseEntity<>(response, HttpStatus.OK);	        
	 }
	 
	 @GetMapping("/{id}")
	 public ResponseEntity<?> getEvent(@PathVariable(value = "id") Long id) {
		 try {
			 EventManager val = service.findEvent(id);
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Events has been retrieved successfully.", val));
			
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
}
