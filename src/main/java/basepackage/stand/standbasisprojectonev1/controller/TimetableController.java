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

import basepackage.stand.standbasisprojectonev1.model.TimeTable;
import basepackage.stand.standbasisprojectonev1.payload.ApiContentResponse;
import basepackage.stand.standbasisprojectonev1.payload.ApiDataResponse;
import basepackage.stand.standbasisprojectonev1.payload.ApiResponse;
import basepackage.stand.standbasisprojectonev1.payload.TimetableRequestTwo;
import basepackage.stand.standbasisprojectonev1.service.TimetableService;
import basepackage.stand.standbasisprojectonev1.util.AppConstants;

@RestController
@RequestMapping("/api/timetable")
public class TimetableController {

	@Autowired
	 TimetableService service;
	 
	 @GetMapping
	 public ResponseEntity<?> getTimetables() {
		 List<TimeTable> list = service.findAll();
		 return ResponseEntity.ok().body(new ApiContentResponse<TimeTable>(true, "List of Timetables gotten successfully.", list));		
	 }
	 
	 @GetMapping("/paginate")
	 public ResponseEntity<?> getPaginatedTimetables(@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
			 @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
			 @RequestParam(value = "q",required=false) String query,
			 @RequestParam(value = "school", required=false) Optional<Long> school,
			 @RequestParam(value = "schoolgroup", required=false) Optional<Long> schoolgroup,
			 @RequestParam(value = "teacher", required=false) Optional<Long> teacher
			 ) {
		 
		 Map<String, Object> response = service.getPaginatedTimeTables( page, size, query, school, schoolgroup, teacher );
		 return new ResponseEntity<>(response, HttpStatus.OK);	        
	 }
	 
	 @GetMapping("/{id}")
	 public ResponseEntity<?> getTimetable(@PathVariable(value = "id") Long id) {
		 try {
			 TimeTable val = service.findTimeTable(id);
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "TimeTable has been retrieved successfully.", val));
			
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @PutMapping("/{id}")
	 public ResponseEntity<?> updateTimetable(@PathVariable(value = "id") Long id, @RequestBody TimetableRequestTwo timeRequest) {
		 try {
			 TimeTable val = service.update(timeRequest,id);			 
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "TimeTable has been updated successfully.", val));	
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @DeleteMapping("/{id}")
	 public ResponseEntity<?> deleteTimetable(@PathVariable(value = "id") Long id) {
		 try {
			 TimeTable val = service.delete(id);
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "TimeTable has been deleted successfully.", val));				 
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 
	 }
}
