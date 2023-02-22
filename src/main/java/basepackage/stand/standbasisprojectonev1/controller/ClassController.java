package basepackage.stand.standbasisprojectonev1.controller;

import java.util.ArrayList;
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

import basepackage.stand.standbasisprojectonev1.model.ClassStream;
import basepackage.stand.standbasisprojectonev1.payload.ApiContentResponse;
import basepackage.stand.standbasisprojectonev1.payload.ApiDataResponse;
import basepackage.stand.standbasisprojectonev1.payload.ApiResponse;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.ClassRequest;
import basepackage.stand.standbasisprojectonev1.service.ClassService;
import basepackage.stand.standbasisprojectonev1.util.AppConstants;

@RestController
@RequestMapping("/api/classstream")
public class ClassController {

	@Autowired
	 ClassService service;
	 
	 @GetMapping
	 public ResponseEntity<?> getClasses() {
		 List<ClassStream> list = service.findAll();
		 return ResponseEntity.ok().body(new ApiContentResponse<ClassStream>(true, "List of Classes gotten successfully.", list));		
	 }
	 
	 @GetMapping("/school/{id}")
	 public ResponseEntity<?> getClassesBySchool( @PathVariable(value = "id") Long id ) {
		 List<ClassStream> list = service.findAllBySchool(id);
		 return ResponseEntity.ok().body(new ApiContentResponse<ClassStream>(true, "List of Classes by school gotten successfully.", list));		
	 }
	 
	 @GetMapping("/school/count/{id}")
	 public ResponseEntity<?> getCountBySchool( @PathVariable(value = "id") Long id ) {
		 Long count = service.countBySchool(id);
		 List<Long> list = new ArrayList<Long>();
		 list.add(count);
		 return ResponseEntity.ok().body(new ApiContentResponse<Long>(true, "Count of Classes by school gotten successfully.", list));		
	 }
	 
	 @GetMapping("/paginate")
	 public ResponseEntity<?> getPaginatedClasss(@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
			 @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
			 @RequestParam(value = "q") String query,
			 @RequestParam(value = "school", required=false) Optional<Long> school
			 ) {
		 
		 Map<String, Object> response = service.getPaginatedClassStreams( page, size, query, school );
		 return new ResponseEntity<>(response, HttpStatus.OK);	        
	 }
	 
	 @GetMapping("/{id}")
	 public ResponseEntity<?> getClass(@PathVariable(value = "id") Long id) {
		 try {
			 ClassStream val = service.findClassStream(id);
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "ClassStream has been retrieved successfully.", val));
			
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @PutMapping("/{id}")
	 public ResponseEntity<?> updateClass(@PathVariable(value = "id") Long id, @RequestBody ClassRequest clsRequest) {
		 try {
			 ClassStream val = service.update(clsRequest,id);			 
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "ClassStream has been updated successfully.", val));	
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @DeleteMapping("/{id}")
	 public ResponseEntity<?> deleteClass(@PathVariable(value = "id") Long id) {
		 try {
			 ClassStream val = service.delete(id);
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "ClassStream has been deleted successfully.", val));				 
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 
	 }
}
