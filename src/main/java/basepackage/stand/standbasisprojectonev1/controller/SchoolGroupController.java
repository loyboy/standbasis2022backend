package basepackage.stand.standbasisprojectonev1.controller;

import java.util.List;
import java.util.Map;

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

import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;
import basepackage.stand.standbasisprojectonev1.payload.ApiContentResponse;
import basepackage.stand.standbasisprojectonev1.payload.ApiDataResponse;
import basepackage.stand.standbasisprojectonev1.payload.ApiResponse;
import basepackage.stand.standbasisprojectonev1.payload.SchoolGroupRequest;
import basepackage.stand.standbasisprojectonev1.service.SchoolGroupService;
import basepackage.stand.standbasisprojectonev1.util.AppConstants;

@RestController
@RequestMapping("/api/schoolgroup")
public class SchoolGroupController {
	 @Autowired
	 SchoolGroupService service;
	 
	 @GetMapping
	 public ResponseEntity<?> getGroupSchools() {
		 List<SchoolGroup> list = service.findAll();
		 return ResponseEntity.ok().body(new ApiContentResponse<SchoolGroup>(true, "List of school groups gotten successfully.", list));		
	 }
	 
	 @GetMapping("/paginate")
	 public ResponseEntity<?> getPaginatedGroupSchools(@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
			 @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
			 @RequestParam(value = "q") String query
			 ) {
		
		 Map<String, Object> response = service.getPaginatedGroupSchools( page, size, query );
		 return new ResponseEntity<>(response, HttpStatus.OK);	        
	 }
	 
	 @GetMapping("/{id}")
	 public ResponseEntity<?> getSchoolGroup(@PathVariable(value = "id") Long id) {
		 try {
			 SchoolGroup val = service.findSchoolGroup(id);
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "School group has been retrieved successfully.", val));
			
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @PutMapping("/{id}")
	 public ResponseEntity<?> updateSchoolGroup(@PathVariable(value = "id") Long id, @RequestBody SchoolGroupRequest schRequest) {
		 try {
			 SchoolGroup val = service.update(schRequest,id);			 
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "School group has been updated successfully.", val));	
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @DeleteMapping("/{id}")
	 public ResponseEntity<?> deleteSchoolGroup(@PathVariable(value = "id") Long id) {
		 try {
			 SchoolGroup val = service.delete(id);
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "School group has been deleted successfully.", val));				 
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
}
