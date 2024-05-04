package basepackage.stand.standbasisprojectonev1.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import basepackage.stand.standbasisprojectonev1.payload.ApiContentResponse;
import basepackage.stand.standbasisprojectonev1.payload.ApiDataResponse;
import basepackage.stand.standbasisprojectonev1.payload.ApiResponse;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.DashboardAcademicInputRequest;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.DashboardTeacherInputRequest;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.DashboardAcademicRequest;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.DashboardCurriculumRequest;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.DashboardSsisRequest;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.DashboardTeacherRequest;
import basepackage.stand.standbasisprojectonev1.repository.EventManagerRepository;
import basepackage.stand.standbasisprojectonev1.repository.UserRepository;
import basepackage.stand.standbasisprojectonev1.security.UserPrincipal;
import basepackage.stand.standbasisprojectonev1.service.DashboardService;
import basepackage.stand.standbasisprojectonev1.model.Attendance;
import basepackage.stand.standbasisprojectonev1.model.DashboardAcademic;
import basepackage.stand.standbasisprojectonev1.model.DashboardAcademicInput;
import basepackage.stand.standbasisprojectonev1.model.DashboardCurriculum;
import basepackage.stand.standbasisprojectonev1.model.DashboardSsis;
import basepackage.stand.standbasisprojectonev1.model.DashboardTeacher;
import basepackage.stand.standbasisprojectonev1.model.DashboardTeacherInput;
import basepackage.stand.standbasisprojectonev1.model.Enrollment;
import basepackage.stand.standbasisprojectonev1.model.EventManager;
import basepackage.stand.standbasisprojectonev1.model.Rowcall;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.Student;
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
	 
	 @GetMapping("/academicinput")
	 public ResponseEntity<?> getAcademicInput( @RequestParam(value = "sch") Long sch ) {
		 List<DashboardAcademicInput> list = service.findAcademicExists(sch);
		 return ResponseEntity.ok().body(new ApiContentResponse<DashboardAcademicInput>(true, "List of Dashboard Academic Input gotten successfully.", list));		
	 }
	 
	 @GetMapping("/teacherinput")
	 public ResponseEntity<?> getTeacherInput( @RequestParam(value = "sch") Long sch ) {
		 List<DashboardTeacherInput> list = service.findTeacherExists(sch);
		 return ResponseEntity.ok().body(new ApiContentResponse<DashboardTeacherInput>(true, "List of Dashboard Teacher Input gotten successfully.", list));		
	 }
	 
	 @GetMapping( value = {"/standards/school/{id}/year/{year}", "/standards/school/{id}" })
	 public ResponseEntity<?> getStandardsBySchool( @PathVariable(value = "id") Long id, @PathVariable(value = "year", required = false) String _year  ) {
		 try {	 
			 if ( _year != null && _year != "null") {
				 DashboardSsis val = service.findBySchoolS(id, Integer.parseInt(_year) );
				 return ResponseEntity.ok().body(new ApiDataResponse(true, "Dashboard for standards has been retrieved successfully.", val));
			 }	
			 else {
				 DashboardSsis val = service.findBySchoolS(id, null);
				 return ResponseEntity.ok().body(new ApiDataResponse(true, "Dashboard for standards has been retrieved successfully.", val));
		
			 }
		 }
		 catch (Exception ex) {
			 System.out.println("Error in Standards " + ex.getMessage() );
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	}
	 
	 @GetMapping( value = {"/teachers/school/{id}/year/{year}", "/teachers/school/{id}" } )
	 public ResponseEntity<?> getTeachersBySchool( @PathVariable(value = "id") Long id, @PathVariable(value = "year", required = false) String _year ) {
		 try {	
			 if ( _year != null && _year != "null" ) {
				 DashboardTeacher val = service.findBySchoolT(id, Integer.parseInt(_year));
				 return ResponseEntity.ok().body(new ApiDataResponse(true, "Dashboard for teachers has been retrieved successfully.", val));
			 }
			 else {
				 DashboardTeacher val = service.findBySchoolT(id, null);
				 return ResponseEntity.ok().body(new ApiDataResponse(true, "Dashboard for teachers has been retrieved successfully.", val));
			 }
		 }
		 catch (Exception ex) {
			 System.out.println("Error in Teacher " + ex.getMessage() );
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	}
	 
	 @GetMapping( value = {"/curriculum/school/{id}/year/{year}", "/curriculum/school/{id}" })
	 public ResponseEntity<?> getCurriculumBySchool( @PathVariable(value = "id") Long id, @PathVariable(value = "year", required = false) String _year ) {
		 try {	 
			 if ( _year != null && _year != "null") {
				 DashboardCurriculum val = service.findBySchoolC(id, Integer.parseInt(_year));
				 return ResponseEntity.ok().body(new ApiDataResponse(true, "Dashboard for curriculum has been retrieved successfully.", val));
			 }
			 else {
				 DashboardCurriculum val = service.findBySchoolC(id, null);
				 return ResponseEntity.ok().body(new ApiDataResponse(true, "Dashboard for curriculum has been retrieved successfully.", val));
			
			 }
		 }
		 catch (Exception ex) {
			 System.out.println("Error in Curriculum " + ex.getMessage() );
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	}
	 
	 @GetMapping( value = {"/academic/school/{id}/year/{year}", "/academic/school/{id}" } )
	 public ResponseEntity<?> getAcademicBySchool( @PathVariable(value = "id") Long id, @PathVariable(value = "year", required = false) String _year ) {
		 try {	 
			 if ( _year != null && _year != "null" ) {
				DashboardAcademic val = service.findBySchoolA(id, Integer.parseInt(_year));
				return ResponseEntity.ok().body(new ApiDataResponse(true, "Dashboard for academic has been retrieved successfully.", val));
			 }
			 else {
				DashboardAcademic val = service.findBySchoolA(id, null);
				return ResponseEntity.ok().body(new ApiDataResponse(true, "Dashboard for academic has been retrieved successfully.", val));
			 }
		 }
		 catch (Exception ex) {
			 System.out.println("Error in Academic " + ex.getMessage() );
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
	 
	 @PostMapping("/academic")
	 public ResponseEntity<?> createDashboardAcademic(@AuthenticationPrincipal UserPrincipal userDetails, @RequestBody DashboardAcademicInputRequest dasRequest) {
		 try {
			 DashboardAcademicInput val = service.saveOne(dasRequest);
			 
			 Optional<User> u = userRepository.findById( userDetails.getId() );
				
			 //------------------------------------
			 saveEvent("dashboard", "create", "The User with name: " + u.get().getName() + "has created a Dashboard Academic instance with ID:" + val.getDashId(), 
					 new Date(), u.get(), u.get().getSchool() == null ? val.getSchool() : u.get().getSchool()
			 );
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Dashboard for academic has been created successfully.", val));	
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @PostMapping("/teacher")
	 public ResponseEntity<?> createDashboardTeacher(@AuthenticationPrincipal UserPrincipal userDetails, @RequestBody List<DashboardTeacherInputRequest> dasRequest ) {
		 try {
			ModelMapper modelMapper = new ModelMapper(); 
			List<DashboardTeacherInput> dtlist = dasRequest.stream().map(t -> 
			 	{
					DashboardTeacherInput dti = modelMapper.map(t, DashboardTeacherInput.class);
			 		School sch = new School();			 		
			 		sch.setSchId(t.getSch_id());
			 		dti.setSchool(sch);
			 		return dti;
			 	
			 	}).collect(Collectors.toList());
		    	
			List<DashboardTeacherInput> val = service.saveAll(dtlist);

			// DashboardTeacherInput val = service.saveOne(dasRequest);
			 
			// Optional<User> u = userRepository.findById( userDetails.getId() );
				
			 //------------------------------------
			/* saveEvent("dashboard", "create", "The User with name: " + u.get().getName() + "has created a Dashboard Teacher instance with ID:" + val.getDashId(), 
					 new Date(), u.get(), u.get().getSchool() == null ? val.getSchool() : u.get().getSchool()
			 );*/
			// return ResponseEntity.ok().body(new ApiDataResponse(true, "Dashboard for teacher has been created successfully.", val));	
			return ResponseEntity.ok().body(new ApiDataResponse(true, "Dashboard input data has been updated", "Dashboard input data updated is " + val.size() ));
		}
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }

	 @GetMapping( value = {"/academicinput/{id}" } )
	 public ResponseEntity<?> getAcademicInputBySchool( @PathVariable(value = "id") Long id ) {
		 try 
		 {	 
			DashboardAcademicInput val = service.findBySchoolAI(id, null);
			return ResponseEntity.ok().body(new ApiDataResponse(true, "Dashboard Input for academic has been retrieved successfully.", val));
			 
		 }
		 catch (Exception ex) {
			System.out.println("Error in Academic Input " + ex.getMessage() );
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	}

	@GetMapping( value = {"/teacherinput/{id}" } )
	 public ResponseEntity<?> getTeacherInputBySchool( @PathVariable(value = "id") Long id ) {
		 try 
		 {	 
			DashboardTeacherInput val = service.findBySchoolTI(id, null);
			return ResponseEntity.ok().body(new ApiDataResponse(true, "Dashboard Input for teacher has been retrieved successfully.", val));
			 
		 }
		 catch (Exception ex) {
			System.out.println("Error in Teacher Input " + ex.getMessage() );
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
