package basepackage.stand.standbasisprojectonev1.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import basepackage.stand.standbasisprojectonev1.model.DashboardAcademic;
import basepackage.stand.standbasisprojectonev1.model.DashboardAcademicInput;
import basepackage.stand.standbasisprojectonev1.model.DashboardCurriculum;
import basepackage.stand.standbasisprojectonev1.model.DashboardSsis;
import basepackage.stand.standbasisprojectonev1.model.DashboardTeacher;
import basepackage.stand.standbasisprojectonev1.model.DashboardTeacherInput;
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
	 
	@GetMapping( value = {"/displaytaqcolumn/{id}"})
	public ResponseEntity<?> getDisplayTaqColumn( @PathVariable(value = "id") Long id ) {
		 try {	 
				Map<String, Object> newResponse = service.calculateITeacherBar(id);
				return new ResponseEntity<>(newResponse, HttpStatus.OK);
		 }
		 catch (Exception ex) {
			 System.out.println("Error in displaytaqcolumn " + ex.getMessage() );
	         return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ApiResponse(false, ex.getMessage()));
	     }
	}

	@GetMapping( value = {"/displaytaqline/{id}"})
	public ResponseEntity<?> getDisplayTaqLine( @PathVariable(value = "id") Long id ) {
		try {	 
			   Map<String, Object> newResponse = service.calculateITeacherLine(id);
			   return new ResponseEntity<>(newResponse, HttpStatus.OK);
		}
		catch (Exception ex) {
			System.out.println("Error in displaytaqline " + ex.getMessage() );
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ApiResponse(false, ex.getMessage()));
		}
    }

	@GetMapping( value = {"/displaytshscolumn/{id}"})
	public ResponseEntity<?> getDisplayTshsColumn( @PathVariable(value = "id") Long id ) {
		try {	 
			   Map<String, Object> newResponse = service.calculateIRatingBar(id);
			   return new ResponseEntity<>(newResponse, HttpStatus.OK);
		}
		catch (Exception ex) {
			System.out.println("Error in displaytshscolumn " + ex.getMessage() );
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ApiResponse(false, ex.getMessage()));
		}
    }

	@GetMapping( value = {"/displaytshsline/{id}"})
	public ResponseEntity<?> getDisplayTshsLine( @PathVariable(value = "id") Long id ) {
		try {	 
			   Map<String, Object> newResponse = service.calculateIRatingLine(id);
			   return new ResponseEntity<>(newResponse, HttpStatus.OK);
		}
		catch (Exception ex) {
			System.out.println("Error in displaytshsline " + ex.getMessage() );
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ApiResponse(false, ex.getMessage()));
		}
    }

	@GetMapping( value = {"/displayacademiccolumn/{id}/year/{year}"})
	public ResponseEntity<?> getDisplayAcademicColumn( @PathVariable(value = "id") Long id, @PathVariable(value = "year") String _year ) {
		try {	 
			   Map<String, Object> newResponse = service.calculateAIAcademicBar( id , Integer.valueOf(_year) );
			   return new ResponseEntity<>(newResponse, HttpStatus.OK);
		}
		catch (Exception ex) {
			System.out.println("Error in displayacademiccolumn " + ex.getMessage() );
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ApiResponse(false, ex.getMessage()));
		}
    }

	@GetMapping( value = {"/displayacademicline/{id}/year/{year}"})
	public ResponseEntity<?> getDisplayAcademicLine( @PathVariable(value = "id") Long id, @PathVariable(value = "year") String _year ) {
		try {	 
			   Map<String, Object> newResponse = service.calculateAIAcademicLine( id , Integer.valueOf(_year) );
			   return new ResponseEntity<>(newResponse, HttpStatus.OK);
		}
		catch (Exception ex) {
			System.out.println("Error in displayacademicline " + ex.getMessage() );
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ApiResponse(false, ex.getMessage()));
		}
    }

	//////////////////////////////////////////////////////////////////////////////
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
	         return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ApiResponse(false, ex.getMessage()));
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
	         return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ApiResponse(false, ex.getMessage()));
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
	         return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ApiResponse(false, ex.getMessage()));
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
	         return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ApiResponse(false, ex.getMessage()));
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
	         return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ApiResponse(false, ex.getMessage()));
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
	         return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ApiResponse(false, ex.getMessage()));
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
	         return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ApiResponse(false, ex.getMessage()));
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
	         return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ApiResponse(false, ex.getMessage()));
	     }
	 }
	 
	 @PostMapping("/academic")
	 public ResponseEntity<?> createDashboardAcademic(@AuthenticationPrincipal UserPrincipal userDetails, @RequestBody DashboardAcademicInputRequest dasRequest) {
		 try {
			 Integer overall_total = dasRequest.getA1_grade_count() + dasRequest.getB2_grade_count() + 
			 dasRequest.getB3_grade_count() +  dasRequest.getC4_grade_count() + dasRequest.getC5_grade_count()
			 + dasRequest.getC6_grade_count() + dasRequest.getD7_grade_count() + dasRequest.getE8_grade_count()
			 + dasRequest.getF9_grade_count() + dasRequest.getAbsent_count();

			if ( overall_total > dasRequest.getEnrollment_count() ){
				return ResponseEntity.ok().body(new ApiDataResponse(false, "Dashboard Total Enrollment Total is less than the inputted value.", null));
			}

			 Double grade_a = Double.valueOf( dasRequest.getA1_grade_count() / dasRequest.getEnrollment_count() );
			 Double grade_b = Double.valueOf( ( dasRequest.getB2_grade_count() + dasRequest.getB3_grade_count() ) / dasRequest.getEnrollment_count() );	
			 Double grade_c = Double.valueOf( ( dasRequest.getC4_grade_count() + dasRequest.getC5_grade_count() + dasRequest.getC6_grade_count() ) / dasRequest.getEnrollment_count() );	
			 Double grade_d = Double.valueOf( ( dasRequest.getD7_grade_count() + dasRequest.getE8_grade_count() + dasRequest.getF9_grade_count() ) / dasRequest.getEnrollment_count() );	
			 
			 if (1.00 > (grade_a + grade_b + grade_c + grade_d) ){
				return ResponseEntity.ok().body(new ApiDataResponse(false, "The addition of the grades are greather than 1.", null));
		
			 }
			 DashboardAcademicInput val = service.saveOne(dasRequest);
			 
			 Optional<User> u = userRepository.findById( userDetails.getId() );
				
			 //------------------------------------
			 saveEvent("dashboard", "create", "The User with name: " + u.get().getName() + "has created a Dashboard Academic instance with ID:" + val.getDashId(), 
					 new Date(), u.get(), u.get().getSchool() == null ? val.getSchool() : u.get().getSchool()
			 );
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Dashboard for academic has been created successfully.", val));	
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ApiResponse(false, ex.getMessage()));
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

			return ResponseEntity.ok().body(new ApiDataResponse(true, "Dashboard input data has been updated", "Dashboard input data updated is " + val.size() ));
		}
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ApiResponse(false, ex.getMessage()));
	     }
	 }

	 @GetMapping( value = {"/academicinput/{id}" } )
	 public ResponseEntity<?> getAcademicInputBySchool( @PathVariable(value = "id") Long id ) {
		 try 
		 {	 
			List<DashboardAcademicInput> list = service.findAcademicExists(id);
			Map<String, Object> response = new HashMap<>();
			
			response.put("data", list);			
			
			return new ResponseEntity<>(response, HttpStatus.OK);	
			 
		 }
		 catch (Exception ex) {
	        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ApiResponse(false, ex.getMessage()));
	     }
	}

	@GetMapping( value = {"/teacherinput/{id}" } )
	 public ResponseEntity<?> getTeacherInputBySchool( @PathVariable(value = "id") Long id ) {
		 try 
		 {	 
			List<DashboardTeacherInput> list = service.findTeacherExists(id);
			Map<String, Object> response = new HashMap<>();
			
			response.put("data", list); 
			return new ResponseEntity<>(response, HttpStatus.OK);	
		 }
		 catch (Exception ex) {
	        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ApiResponse(false, ex.getMessage()));
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
