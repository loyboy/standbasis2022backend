package basepackage.stand.standbasisprojectonev1.controller;

import org.springframework.http.MediaType;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.payload.ApiContentResponse;
import basepackage.stand.standbasisprojectonev1.payload.ApiDataResponse;
import basepackage.stand.standbasisprojectonev1.payload.ApiResponse;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.SchoolRequest;
import basepackage.stand.standbasisprojectonev1.service.SchoolService;
import basepackage.stand.standbasisprojectonev1.util.AppConstants;
import basepackage.stand.standbasisprojectonev1.util.FileUploadUtil;
//
@RestController
@RequestMapping("/api/school")
public class SchoolController {

	 @Autowired
	 SchoolService service;
	 
	 @GetMapping
	 public ResponseEntity<?> getSchools() {
		 List<School> list = service.findAll();
		 return ResponseEntity.ok().body(new ApiContentResponse<School>(true, "List of schools gotten successfully.", list));		
	 }
	 
	 @GetMapping("/group/{id}")
	 public ResponseEntity<?> getSchoolsFromSchoolGroup( @PathVariable(value = "id") Long id ) {
		 List<School> list = service.findAllByGroup(id);
		 return ResponseEntity.ok().body(new ApiContentResponse<School>(true, "List of schools by group gotten successfully.", list));		
	 }
	 
	 @GetMapping("/group/count/{id}")
	 public ResponseEntity<?> getCountFromSchoolGroup( @PathVariable(value = "id") Long id ) {
		 Long count = service.countBySchoolGroup(id);
		 List<Long> list = new ArrayList<Long>();
		 list.add(count);
		 return ResponseEntity.ok().body(new ApiContentResponse<Long>(true, "Count of schools by group gotten successfully.", list));		
	 }
	 
	 @GetMapping("/paginate")
	 public ResponseEntity<?> getPaginatedSchools(@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
			 @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
			 @RequestParam(value = "q", required=false) String query,
			 @RequestParam(value = "owner", required=false) Optional<Long> owner
			 ) {
		// System.out.println("Long is set here "+ owner);
		 
		 Map<String, Object> response = service.getPaginatedSchools( page, size, query, owner );
		 return new ResponseEntity<>(response, HttpStatus.OK);	        
	 }
	 
	 @GetMapping("/{id}")
	 public ResponseEntity<?> getSchool(@PathVariable(value = "id") Long id) {
		 try {
			 School val = service.findSchool(id);
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "School data has been retrieved successfully.", val));
			
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @PutMapping("/{id}")
	 public ResponseEntity<?> updateSchool(@PathVariable(value = "id") Long id, @RequestBody SchoolRequest schRequest) {
		 try {		        
				 School val = service.update(schRequest,id);			 
				 return ResponseEntity.ok().body(new ApiDataResponse(true, "School data has been updated successfully.", val));	
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @RequestMapping(path = "/logo/{id}", method = PUT, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	 public ResponseEntity<?> updateSchoolLogo(@PathVariable(value = "id") Long id, @RequestPart("logo") MultipartFile multipartFile) {
		 try {
			 	 String fileOriginalName = StringUtils.cleanPath(multipartFile.getOriginalFilename());		         
			 	 String fileName = "0"+ id.toString() + "." + FileUploadUtil.findExtension(fileOriginalName).get();
			 	
		         String uploadDir = "school-logo/" + id;		 
		         FileUploadUtil.saveFile(uploadDir, fileName , multipartFile);
		        
				 String val = service.updateLogo(id, fileName);			 
				 return ResponseEntity.ok().body(new ApiDataResponse(true, "School Logo has been updated successfully.", val));	
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 
	 @DeleteMapping("/{id}")
	 public ResponseEntity<?> deleteSchool(@PathVariable(value = "id") Long id) {
		 try {
			 School val = service.delete(id);
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "School has been deleted successfully.", val));				 
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
}
