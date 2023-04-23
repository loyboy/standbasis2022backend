package basepackage.stand.standbasisprojectonev1.controller;

import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

import basepackage.stand.standbasisprojectonev1.model.EventManager;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.Teacher;
import basepackage.stand.standbasisprojectonev1.model.User;
import basepackage.stand.standbasisprojectonev1.payload.ApiContentResponse;
import basepackage.stand.standbasisprojectonev1.payload.ApiDataResponse;
import basepackage.stand.standbasisprojectonev1.payload.ApiResponse;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.TeacherRequest;
import basepackage.stand.standbasisprojectonev1.repository.EventManagerRepository;
import basepackage.stand.standbasisprojectonev1.repository.UserRepository;
import basepackage.stand.standbasisprojectonev1.security.UserPrincipal;
import basepackage.stand.standbasisprojectonev1.service.TeacherService;
import basepackage.stand.standbasisprojectonev1.util.AppConstants;
import basepackage.stand.standbasisprojectonev1.util.FileUploadUtil;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

	 @Autowired
	 TeacherService service;
	 
	 @Autowired
	 private EventManagerRepository eventRepository;
	 
	 @Autowired
	 private UserRepository userRepository;
	 
	 @GetMapping
	 public ResponseEntity<?> getTeachers() {
		 List<Teacher> list = service.findAll();
		 return ResponseEntity.ok().body(new ApiContentResponse<Teacher>(true, "List of teachers gotten successfully.", list));		
	 }
	 
	 @GetMapping("/school/{id}")
	 public ResponseEntity<?> getTeachersBySchool( @PathVariable(value = "id") Long id ) {
		 List<Teacher> list = service.findAllBySchool(id);
		 return ResponseEntity.ok().body(new ApiContentResponse<Teacher>(true, "List of teachers by school gotten successfully.", list));		
	 }
	 
	 @GetMapping("/school/count/{id}")
	 public ResponseEntity<?> getCountBySchool( @PathVariable(value = "id") Long id ) {
		 Long count = service.countBySchool(id);
		 List<Long> list = new ArrayList<Long>();
		 list.add(count);
		 return ResponseEntity.ok().body(new ApiContentResponse<Long>(true, "Count of teachers by school gotten successfully.", list));		
	 }
	 
	 @GetMapping("/group/count/{id}")
	 public ResponseEntity<?> getCountFromSchoolGroup( @PathVariable(value = "id") Long id ) {
		 Long count = service.countBySchoolGroup(id);
		 List<Long> list = new ArrayList<Long>();
		 list.add(count);
		 return ResponseEntity.ok().body(new ApiContentResponse<Long>(true, "Count of teachers by group gotten successfully.", list));		
	 }
	 
	 @GetMapping("/timetable/count/{id}")
	 public ResponseEntity<?> getCountFromTimetable( @PathVariable(value = "id") Long id ) {
		 Long count = service.countByTimetable(id);
		 List<Long> list = new ArrayList<Long>();
		 list.add(count);
		 return ResponseEntity.ok().body(new ApiContentResponse<Long>(true, "Count of teachers by timetable gotten successfully.", list));		
	 }
	 
	 @GetMapping("/paginate")
	 public ResponseEntity<?> getPaginatedTeachers(@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
			 @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
			 @RequestParam(value = "q") String query,
			 @RequestParam(value = "school", required=false) Optional<Long> school
			 ) {
		// System.out.println("Long is set here "+ owner);
		 
		 Map<String, Object> response = service.getPaginatedTeachers( page, size, query, school );
		 return new ResponseEntity<>(response, HttpStatus.OK);	        
	 }
	 
	 @GetMapping("/{id}")
	 public ResponseEntity<?> getTeacher(@PathVariable(value = "id") Long id) {
		 try {
			 Teacher val = service.findTeacher(id);
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Teacher has been retrieved successfully.", val));
			
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @PutMapping("/{id}")
	 public ResponseEntity<?> updateTeacher(@AuthenticationPrincipal UserPrincipal userDetails, @PathVariable(value = "id") Long id, @RequestBody TeacherRequest teaRequest) {
		 try {
			 Teacher val = service.update(teaRequest,id);	
			 
			 Optional<User> u = userRepository.findById( userDetails.getId() );
				
			 //------------------------------------
			 saveEvent("teacher", "edit", "The User with name: " + u.get().getName() + "has deleted a teacher with ID:  " + val.getTeaId(), 
					 new Date(), u.get(), u.get().getSchool()
			 );
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Teacher has been updated successfully.", val));	
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @RequestMapping(path = "/photo/{id}", method = PUT, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	 public ResponseEntity<?> updateTeacherLogo(@PathVariable(value = "id") Long id, @RequestPart("logo") MultipartFile multipartFile) {
		 try {
			 	 String fileOriginalName = StringUtils.cleanPath(multipartFile.getOriginalFilename());		         
			 	 String fileName = "0"+ id.toString() + "." + FileUploadUtil.findExtension(fileOriginalName).get();
			 	
		         String uploadDir = "teacher-photo/" + id;		 
		         FileUploadUtil.saveFile(uploadDir, fileName , multipartFile);
		        
				 String val = service.updatePhoto(id, fileName);			 
				 return ResponseEntity.ok().body(new ApiDataResponse(true, "Teacher Photo has been updated successfully.", val));	
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @DeleteMapping("/{id}")
	 public ResponseEntity<?> deleteTeacher(@AuthenticationPrincipal UserPrincipal userDetails, @PathVariable(value = "id") Long id) {
		 try {
			 Teacher val = service.delete(id);
			 
			 Optional<User> u = userRepository.findById( userDetails.getId() );
				
			 //------------------------------------
			 saveEvent("teacher", "delete", "The User with name: " + u.get().getName() + "has deleted a teacher with ID:  " + val.getTeaId(), 
					 new Date(), u.get(), u.get().getSchool()
			 );
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Teacher has been deleted successfully.", val));				 
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
