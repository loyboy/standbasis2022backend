package basepackage.stand.standbasisprojectonev1.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import basepackage.stand.standbasisprojectonev1.model.EvaluationValues;
import basepackage.stand.standbasisprojectonev1.model.EvaluatorCore;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.User;
import basepackage.stand.standbasisprojectonev1.payload.ApiContentResponse;
import basepackage.stand.standbasisprojectonev1.payload.ApiDataResponse;
import basepackage.stand.standbasisprojectonev1.payload.ApiResponse;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.EvaluationRequest;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.EvaluationValuesRequest;
import basepackage.stand.standbasisprojectonev1.repository.EvaluationRepository;
import basepackage.stand.standbasisprojectonev1.service.EvaluationService;

@RestController
@RequestMapping("/api/evaluation")
public class EvaluationController {

	@Autowired
	 EvaluationService<?> service;
	
	@Autowired
	private EvaluationRepository evaRepository;
	 
	 @GetMapping("/rounds")
	 public ResponseEntity<?> getEvaluations( 
			 @RequestParam(value = "school") Optional<Long> school,
			 @RequestParam(value = "user") Optional<Long> user,
			 @RequestParam(value = "complete" , required=false) Optional<Boolean> complete
			 ) {
		
		 List<EvaluationValues> list = service.findRounds( school.get(), user.get(), complete.isPresent() ? complete.get() : false );
		 
		 return ResponseEntity.ok().body(new ApiContentResponse<EvaluationValues>(true, "List of Evaluations gotten successfully.", list));		
	 }
	 
	 @GetMapping("/schools")
	 public ResponseEntity<?> getEvaluationsForSchools( @RequestParam(value = "user") Optional<Long> user ) {
		 
		 List<EvaluationValues> list = service.findSchools( user.get() );
		 
		 return ResponseEntity.ok().body(new ApiContentResponse<EvaluationValues>(true, "List of Unique Schools gotten successfully.", list));		
	 }
	 
	 @GetMapping("/schoolgroups")
	 public ResponseEntity<?> getEvaluationsForSchoolGroups( @RequestParam(value = "user") Optional<Long> user ) {
		 
		 List<EvaluationValues> list = service.findSchoolGroups( user.get() );
		 
		 return ResponseEntity.ok().body(new ApiContentResponse<EvaluationValues>(true, "List of Unique School Groups gotten successfully.", list));		
	 }
	 
	 @GetMapping("/schoolzones")
	 public ResponseEntity<?> getEvaluationsForSchoolZones( @RequestParam(value = "user") Optional<Long> user ) {
		 
		 List<EvaluationValues> list = service.findSchoolZones( user.get() );
		 
		 return ResponseEntity.ok().body(new ApiContentResponse<EvaluationValues>(true, "List of Unique School Zones gotten successfully.", list));		
	 }
	 
	 //
	 @PostMapping("/round")
	 public ResponseEntity<?> addRound( 
			 @RequestBody EvaluationRequest evaRequest
	 ) {
		 try {
			 String val = service.createRound(evaRequest.getSchool(), evaRequest.getUserid());
			 
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Created a new round successfully.", val));
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @GetMapping("/round/{id}")
	 public ResponseEntity<?> getRound(@PathVariable(value = "id") String id) {
		 try {
			 Map<String, Object> response =  service.fetchRound(id);
			 return new ResponseEntity<>(response, HttpStatus.OK);			
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @GetMapping("/round/group/{name}")
	 public ResponseEntity<?> getRoundByGroup(@PathVariable(value = "name") String name) {
		 try {
			 Map<String, Object> response =  service.fetchRoundByGroup(name);
			 return new ResponseEntity<>(response, HttpStatus.OK);			
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @GetMapping("/round/zone/{name}")
	 public ResponseEntity<?> getRoundByZone(@PathVariable(value = "name") String name) {
		 try {
			 Map<String, Object> response =  service.fetchRoundByZone(name);
			 return new ResponseEntity<>(response, HttpStatus.OK);			
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @PutMapping("/round")
	 public ResponseEntity<?> updateRound(  @RequestBody List<EvaluationValuesRequest> evalRequest ) {
		 try {
			 	
			 	ModelMapper modelMapper = new ModelMapper();
			 	
			 	List<EvaluationValues> rclist = evalRequest.stream().map(t -> 
			 	{
			 		Optional<EvaluationValues> rc = evaRepository.findById(t.getJobvalueId());
			 		EvaluationValues myval = rc.get();
			 		if (t.getComplete() != null) {
			 			myval.setComplete(t.getComplete()); 
			 		}
			 		myval.setValue( t.getValue() );	 
			 		myval.setPerformance( t.getPerformance() );
			 		return myval;			 	
			 	}).collect(Collectors.toList());
		    	
				 List<EvaluationValues> evlist = service.updateRound(rclist);
				 
				 return ResponseEntity.ok().body(new ApiDataResponse(true, "Evaluation round has been updated", evlist ));
		 }
		 catch (Exception ex) {
			 ex.printStackTrace();
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
		
	 }
	 
	//EvaluatorCore core = new EvaluatorCore();
		//School sch = new School();
		//User user = new User();
		
		/*core.setJobId(t.getEvaluation_id());
		sch.setSchId(t.getSchool_id());
		user.setUserId(t.getUser_id());
		
		rc.setJobvalueId(t.getJobvalueId());
		rc.setValue(t.getValue());
		rc.setRoundId(t.getRoundId());
		rc.setRound(t.getRound());
		rc.setPerformance(t.getPerformance());
		
		rc.setEvaluation(core);
		rc.setSchool(sch);
		rc.setUser(user);
		rc.setCreatedAt(null);*/
	
}
