package basepackage.stand.standbasisprojectonev1.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import basepackage.stand.standbasisprojectonev1.model.Calendar;
import basepackage.stand.standbasisprojectonev1.payload.ApiContentResponse;
import basepackage.stand.standbasisprojectonev1.payload.ApiDataResponse;
import basepackage.stand.standbasisprojectonev1.payload.ApiResponse;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.CalendarRequest;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.DashboardAcademicRequest;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.DashboardCurriculumRequest;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.DashboardSsisRequest;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.DashboardTeacherRequest;
import basepackage.stand.standbasisprojectonev1.repository.EventManagerRepository;
import basepackage.stand.standbasisprojectonev1.repository.UserRepository;
import basepackage.stand.standbasisprojectonev1.security.UserPrincipal;
import basepackage.stand.standbasisprojectonev1.service.DashboardService;
import basepackage.stand.standbasisprojectonev1.model.DashboardAcademic;
import basepackage.stand.standbasisprojectonev1.model.DashboardCurriculum;
import basepackage.stand.standbasisprojectonev1.model.DashboardSsis;
import basepackage.stand.standbasisprojectonev1.model.DashboardTeacher;
import basepackage.stand.standbasisprojectonev1.model.EventManager;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.User;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

	 @Autowired
	 DashboardService service;
	 
	 @Autowired
	 private UserRepository userRepository;
	 
	 @Autowired
	 private EventManagerRepository eventRepository;
	 
	 @GetMapping("/standards/school/{id}")
	 public ResponseEntity<?> getStandardsBySchool( @PathVariable(value = "id") Long id ) {
		 try {	 
			 DashboardSsis val = service.findBySchoolS(id);
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Dashboard for standards has been retrieved successfully.", val));
				
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	}
	 
	 @GetMapping("/teachers/school/{id}")
	 public ResponseEntity<?> getTeachersBySchool( @PathVariable(value = "id") Long id ) {
		 try {	 
			 DashboardTeacher val = service.findBySchoolT(id);
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Dashboard for teachers has been retrieved successfully.", val));
				
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	}
	 
	 @GetMapping("/curriculum/school/{id}")
	 public ResponseEntity<?> getCurriculumBySchool( @PathVariable(value = "id") Long id ) {
		 try {	 
			 DashboardCurriculum val = service.findBySchoolC(id);
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Dashboard for curriculum has been retrieved successfully.", val));
				
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	}
	 
	 @GetMapping("/academic/school/{id}")
	 public ResponseEntity<?> getAcademicBySchool( @PathVariable(value = "id") Long id ) {
		 try {	 
			 DashboardAcademic val = service.findBySchoolA(id);
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Dashboard for academic has been retrieved successfully.", val));
				
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	}
	 
	 //-----------------------------------------------------------------------------------------
	 
	 @PutMapping("/standards/{id}")
	 public ResponseEntity<?> updateDashboardSsis(@AuthenticationPrincipal UserPrincipal userDetails, @PathVariable(value = "id") Long id, @RequestBody DashboardSsisRequest dasRequest) {
		 try {
			 DashboardSsis val = service.updateS(dasRequest,id);
			 
			 Optional<User> u = userRepository.findById( userDetails.getId() );
				
			 //------------------------------------
			 saveEvent("dashboard", "edit", "The User with name: " + u.get().getName() + "has edited a Dashboard SSIS instance with ID:  " + val.getDashId(), 
					 new Date(), u.get(), u.get().getSchool() == null ? val.getSchool() : u.get().getSchool()
			 );
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Dashboard for standards has been updated successfully.", val));	
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @PutMapping("/teachers/{id}")
	 public ResponseEntity<?> updateDashboardTeacher(@AuthenticationPrincipal UserPrincipal userDetails, @PathVariable(value = "id") Long id, @RequestBody DashboardTeacherRequest dasRequest) {
		 try {
			 DashboardTeacher val = service.updateT(dasRequest,id);
			 
			 Optional<User> u = userRepository.findById( userDetails.getId() );
				
			 //------------------------------------
			 saveEvent("dashboard", "edit", "The User with name: " + u.get().getName() + "has edited a Dashboard Teacher instance with ID:  " + val.getDashId(), 
					 new Date(), u.get(), u.get().getSchool() == null ? val.getSchool() : u.get().getSchool()
			 );
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Dashboard for teachers has been updated successfully.", val));	
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @PutMapping("/curriculum/{id}")
	 public ResponseEntity<?> updateDashboardCurriculum(@AuthenticationPrincipal UserPrincipal userDetails, @PathVariable(value = "id") Long id, @RequestBody DashboardCurriculumRequest dasRequest) {
		 try {
			 DashboardCurriculum val = service.updateC(dasRequest,id);
			 
			 Optional<User> u = userRepository.findById( userDetails.getId() );
				
			 //------------------------------------
			 saveEvent("dashboard", "edit", "The User with name: " + u.get().getName() + "has edited a Dashboard Curriculum instance with ID:  " + val.getDashId(), 
					 new Date(), u.get(), u.get().getSchool() == null ? val.getSchool() : u.get().getSchool()
			 );
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Dashboard for curriculum has been updated successfully.", val));	
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @PutMapping("/academic/{id}")
	 public ResponseEntity<?> updateDashboardAcademic(@AuthenticationPrincipal UserPrincipal userDetails, @PathVariable(value = "id") Long id, @RequestBody DashboardAcademicRequest dasRequest) {
		 try {
			 DashboardAcademic val = service.updateA(dasRequest,id);
			 
			 Optional<User> u = userRepository.findById( userDetails.getId() );
				
			 //------------------------------------
			 saveEvent("dashboard", "edit", "The User with name: " + u.get().getName() + "has edited a Dashboard Academic instance with ID:  " + val.getDashId(), 
					 new Date(), u.get(), u.get().getSchool() == null ? val.getSchool() : u.get().getSchool()
			 );
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Dashboard for academic has been updated successfully.", val));	
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
