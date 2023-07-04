package basepackage.stand.standbasisprojectonev1.controller;

import java.time.LocalDate;
import java.util.Date;
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
import basepackage.stand.standbasisprojectonev1.model.EventManager;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.User;
import basepackage.stand.standbasisprojectonev1.payload.ApiContentResponse;
import basepackage.stand.standbasisprojectonev1.payload.ApiDataResponse;
import basepackage.stand.standbasisprojectonev1.payload.ApiResponse;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.CalendarRequest;
import basepackage.stand.standbasisprojectonev1.repository.EventManagerRepository;
import basepackage.stand.standbasisprojectonev1.repository.UserRepository;
import basepackage.stand.standbasisprojectonev1.security.UserPrincipal;
import basepackage.stand.standbasisprojectonev1.service.CalendarService;
import basepackage.stand.standbasisprojectonev1.util.AppConstants;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

	 @Autowired
	 CalendarService service;
	 
	 @Autowired
	 private EventManagerRepository eventRepository;
	 
	 @Autowired
	 private UserRepository userRepository;
	 
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
	 public ResponseEntity<?> updateCalendar(@AuthenticationPrincipal UserPrincipal userDetails, @PathVariable(value = "id") Long id, @RequestBody CalendarRequest calRequest) {
		 try {
			 Calendar val = service.update(calRequest,id);
			 
			 Optional<User> u = userRepository.findById( userDetails.getId() );
				
			 //------------------------------------
			 saveEvent("calendar", "edit", "The User with name: " + u.get().getName() + "has edited a Calendar instance with ID:  " + val.getId(), 
					 new Date(), u.get(), u.get().getSchool() == null ? val.getSchool() : u.get().getSchool()
			 );
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Calendar has been updated successfully.", val));	
		 }
		 catch (Exception ex) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You do not have access to this resource because your Bearer token is either expired or not set."));
	     }
	 }
	 
	 @DeleteMapping("/{id}")
	 public ResponseEntity<?> deleteCalendar( @AuthenticationPrincipal UserPrincipal userDetails, @PathVariable(value = "id") Long id) {
		 try {
			 Calendar val = service.delete(id);
			 Optional<User> u = userRepository.findById( userDetails.getId() );
				
			 //------------------------------------
			 saveEvent("calendar", "delete", "The User with name: " + u.get().getName() + "has deleted a Calendar instance with ID:  " + val.getId(), 
					 new Date(), u.get(), u.get().getSchool() == null ? val.getSchool() : u.get().getSchool()
			 );
			 return ResponseEntity.ok().body(new ApiDataResponse(true, "Calendar has been deleted successfully.", val));				 
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
	 
	 // List<LocalDate> weekdayRange = getWeekdayRange(weekNumber, startDate, endDate);
     
/*
	 private List<LocalDate> getWeekdayRange(int weekNumber, LocalDate startDate, LocalDate endDate) {
	        List<LocalDate> weekdayRange = new ArrayList<>();
	        
	        // Find the first Monday of the given week
	        LocalDate firstMonday = startDate.with(DayOfWeek.MONDAY);
	        
	        // Calculate the start date of the specified week
	        LocalDate weekStartDate = firstMonday.plusWeeks(weekNumber - 1);
	        
	        // Calculate the end date of the specified week (Friday)
	        LocalDate weekEndDate = weekStartDate.plusDays(4);
	        
	        // Ensure the calculated week falls within the given date range
	        if (weekStartDate.isBefore(startDate)) {
	            weekStartDate = startDate;
	        }
	        
	        if (weekEndDate.isAfter(endDate)) {
	            weekEndDate = endDate;
	        }
	        
	        // Add each weekday (Monday to Friday) to the weekdayRange list
	        LocalDate currentDay = weekStartDate;
	        while (!currentDay.isAfter(weekEndDate)) {
	            weekdayRange.add(currentDay);
	            currentDay = currentDay.plusDays(1);
	        }
	        
	        return weekdayRange;
	    }
	*/
	 
}
