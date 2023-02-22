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
import basepackage.stand.standbasisprojectonev1.model.Calendar;
import basepackage.stand.standbasisprojectonev1.payload.ApiContentResponse;
import basepackage.stand.standbasisprojectonev1.payload.ApiDataResponse;
import basepackage.stand.standbasisprojectonev1.payload.ApiResponse;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.CalendarRequest;
import basepackage.stand.standbasisprojectonev1.service.CalendarService;
import basepackage.stand.standbasisprojectonev1.util.AppConstants;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

	 @Autowired
	 CalendarService service;
	 
	 @GetMapping
	 public ResponseEntity<?> getCalendars() {
		 List<Calendar> list = service.findAll();
		 return ResponseEntity.ok().body(new ApiContentResponse<Calendar>(true, "List of Calendars gotten successfully.", list));		
	 }
	 
	 @GetMapping("/school/{id}")
	 public ResponseEntity<?> getCalendarsBySchool( @PathVariable(value = "id") Long id ) {
		 List<Calendar> list = service.findAllBySchool(id);
		 return ResponseEntity.ok().body(new ApiContentResponse<Calendar>(true, "List of Calendars by school gotten successfully.", list));		
	 }
	 
	 @GetMapping("/paginate")
	 public ResponseEntity<?> getPaginatedCalendars(@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
			 @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
			 @RequestParam(value = "q") String query,
			 @RequestParam(value = "school", required=false) Optional<Long> school
			 ) {
		// System.out.println("Long is set here "+ owner);
		 
		 Map<String, Object> response = service.getPaginatedCalendars( page, size, query, school );
		 return new ResponseEntity<>(response, HttpStatus.OK);	        
	 }
	 
	 @GetMapping("/{id}")
	 public ResponseEntity<?> getCalendar(@PathVariable(value = "id") Long id) {
		 try {
			 Calendar val = service.findCalendar(id);
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Calendar has been retrieved successfully.", val));
			
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @PutMapping("/{id}")
	 public ResponseEntity<?> updateCalendar(@PathVariable(value = "id") Long id, @RequestBody CalendarRequest teaRequest) {
		 try {
			 Calendar val = service.update(teaRequest,id);			 
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Calendar has been updated successfully.", val));	
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @DeleteMapping("/{id}")
	 public ResponseEntity<?> deleteCalendar(@PathVariable(value = "id") Long id) {
		 try {
			 Calendar val = service.delete(id);
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Calendar has been deleted successfully.", val));				 
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 
	 }
	 
}
