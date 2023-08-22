package basepackage.stand.standbasisprojectonev1.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import basepackage.stand.standbasisprojectonev1.model.Attendance;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;
import basepackage.stand.standbasisprojectonev1.model.Teacher;
import basepackage.stand.standbasisprojectonev1.model.TimeTable;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.TeacherRequest;
import basepackage.stand.standbasisprojectonev1.repository.SchoolRepository;
import basepackage.stand.standbasisprojectonev1.repository.SchoolgroupRepository;
import basepackage.stand.standbasisprojectonev1.repository.TeacherRepository;
import basepackage.stand.standbasisprojectonev1.repository.TimetableRepository;
import basepackage.stand.standbasisprojectonev1.util.CommonActivity;

@Service
public class TeacherService {

private static final Logger logger = LoggerFactory.getLogger(TeacherService.class);
	
	@Autowired		
    private TeacherRepository teaRepository;
	
	@Autowired		
    private SchoolRepository schRepository;
	
	@Autowired
    private SchoolgroupRepository schgroupRepository;
	
	@Autowired
    private TimetableRepository timeRepository;
	
	@Autowired
	TimetableService serviceTimetable;
	
	@Autowired
	AttendanceService attTimetable;
	
	public List<Teacher> findAll() {
		
		return teaRepository.findAll();
	}
	
	public List<Teacher> findAllBySchool(Long id) {		
		Optional<School> sch = schRepository.findById(id);
		if (sch.isPresent()) {
			School schval = sch.get();			
			return teaRepository.findBySchool(schval);
		}
		return null;		
	}
	
	public Teacher findTeacher(long id) {
		
		Optional<Teacher> tea = teaRepository.findById(id);
		if (tea.isPresent()) {
			Teacher teaval = tea.get();
			
			return teaval;
		}
		return null;
	}
	
	public Long countBySchool(long id) {		
		Optional<School> sch = schRepository.findById(id);
		if (sch.isPresent()) {
			School schval = sch.get();
			
			long countval = teaRepository.countBySchool(schval);
			
			return countval;
		}
		return null;
	}
	
	public Long countBySchoolGroup(long id) {		
		Optional<SchoolGroup> schgroup = schgroupRepository.findById(id);
		if (schgroup.isPresent()) {
			SchoolGroup schval = schgroup.get();
			
			long countval = teaRepository.countBySchoolGroup(schval);
			
			return countval;
		}
		return null;
	}
	
	public Long countByTimetable(long id) {		
		Optional<Teacher> timetableTeacher = teaRepository.findById(id);
		if (timetableTeacher.isPresent()) {
			System.out.println("Teacher ID: " + id);
			Teacher teaval = timetableTeacher.get();			
			Long countval = timeRepository.countByTimetable(teaval);	
			System.out.println("Count time: " + countval);
			return countval;
		}
		return null;
	}
	
	public Map<String, Object> getPaginatedTeachers(int page, int size, String query, Optional<Long> groupval, Optional<Long> ownerval) {
		CommonActivity.validatePageNumberAndSize(page, size);
        Long owner = ownerval.orElse(null);     
        Long group = groupval.orElse(null);
        int underdeployed = 0;
		int deployed = 0; 
		int overdeployed = 0;
		int inactive = 0;
		int active = 0;
		Optional<Long> optionalValue = Optional.ofNullable(null);
		Optional<Timestamp> optionalValueTimestamp = Optional.ofNullable(null);
        // Retrieve Teachers
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Teacher> schs = null;
      //  System.out.println("Long is set "+ owner);
        
        if ( query.equals("") || query == null ) {
        	if ( group == null ) {
        		schs = teaRepository.findAll(pageable);
        	}
        	else {
        		Optional<SchoolGroup> schgroupobj = null ;
        		Optional<School> schownerobj = null;
        		
        		if(owner != null) { schownerobj = schRepository.findById( owner ); }
        		if(group != null) { schgroupobj = schgroupRepository.findById( group ); }        		
        		
        			schs = teaRepository.findBySchool( 
        					schownerobj == null ? null : schownerobj.get(), 
        	        		schgroupobj == null ? null : schgroupobj.get()
        					, pageable);
        	}       	
        }
        else {
        	if ( group == null ) {
        		schs = teaRepository.filter("%"+ query + "%",  pageable);
        	}
        	else {    
        		
        		Optional<SchoolGroup> schgroupobj = null ;
        		Optional<School> schownerobj = null;
        		
        		if(owner != null) { schownerobj = schRepository.findById( owner ); }
        		if(group != null) { schgroupobj = schgroupRepository.findById( group ); }
        		
        	// Optional<School> schobj = schRepository.findById( owner );
        		schs = teaRepository.findFilterBySchool( "%"+ query + "%", 
        				schownerobj == null ? null : schownerobj.get(), 
    	        		schgroupobj == null ? null : schgroupobj.get(), 
    	        pageable);
        	}
        }

        if(schs.getNumberOfElements() == 0) {
        	Map<String, Object> responseEmpty = new HashMap<>();
        	responseEmpty.put("teachers", Collections.emptyList());
        	responseEmpty.put("currentPage", schs.getNumber());
        	responseEmpty.put("totalItems", schs.getTotalElements());
        	responseEmpty.put("totalPages", schs.getTotalPages());
        	
        	return responseEmpty;
        }
        
        List<Teacher> teaarray = new ArrayList<Teacher>();
        
        teaarray = schs.getContent();
        
        Map<String, Object> response = new HashMap<>();
        response.put("teachers", teaarray);
        response.put("currentPage", schs.getNumber());
        response.put("totalItems", schs.getTotalElements());
        response.put("totalPages", schs.getTotalPages());
        response.put("isLast", schs.isLast());
        
        Map<String, Object> response2 = serviceTimetable.getOrdinaryTimeTables(query, ownerval, groupval);
        
        Map<String, Object> response3 = attTimetable.getOrdinaryTeacherAttendances(query, groupval, ownerval, optionalValue, optionalValue, optionalValue, optionalValue, optionalValueTimestamp, optionalValueTimestamp);
		
        @SuppressWarnings("unchecked")
		List<TimeTable> listTimetable = (List<TimeTable>) response2.get("timetables");	
        
        @SuppressWarnings("unchecked")
		List<Attendance> listAttendance = (List<Attendance>) response3.get("attendances");
						  
		for (Teacher teaT : teaarray) {
			
			 /*List<TimeTable> countTimetableTeacherHas = listTimetable.stream()
					 	.filter(tt -> tt.getTeacher().getTeaId() == teaT.getTeaId() )
			            .collect(Collectors.toMap(
			                    obj -> Arrays.asList( obj.getSubject(), obj.getTeacher(), obj.getClass_stream() ),  // Composite key
			                    Function.identity(),  // Keep the original object
			                    (obj1, obj2) -> obj1  // Merge function (in case of duplicate keys)
			            ))
			            .values()
			            .stream()
			            .collect(Collectors.toList());*/
			 List<TimeTable> countTimetableTeacherHas = listTimetable.stream()
					.filter(tt -> tt.getTeacher().getTeaId() == teaT.getTeaId() )
			        .filter(distinctByKey(pr -> Arrays.asList(pr.getSubject(), pr.getTeacher(), pr.getClass_stream() )))
			        .collect(Collectors.toList());
			 
			 System.out.println("countTimetableTeacherHas >> " + teaT.getTeaId() + " : " + countTimetableTeacherHas.size() );
			
			/* List<Attendance> countAttendanceTeacherHas = listAttendance.stream()
					 	.filter(tt -> tt.getTeacher().getTeaId() == teaT.getTeaId() )
			            .collect(Collectors.toMap(
			                    obj -> Arrays.asList( obj.getTeacher(), obj.getTimetable().getClass_stream(), obj.getTimetable().getSubject()  ),  // Composite key
			                    Function.identity(),  // Keep the original object
			                    (obj1, obj2) -> obj1  // Merge function (in case of duplicate keys)
			            ))
			            .values()
			            .stream()
			            .collect(Collectors.toList());*/
			 
			 List<Attendance> countAttendanceTeacherHas = listAttendance.stream()
					 	.filter(tt -> tt.getTeacher().getTeaId() == teaT.getTeaId() )
				        .filter(distinctByKey(pr -> Arrays.asList(pr.getTimetable().getSubject(), pr.getTeacher(), pr.getTimetable().getClass_stream() )))
				        .collect(Collectors.toList());
			 
			 System.out.println("countAttendanceTeacherHas >> " + teaT.getTeaId() + " : " + countAttendanceTeacherHas.size() );	
			 
			if (countTimetableTeacherHas.size() > 0) {
				deployed++;
			}
			
			else if (countTimetableTeacherHas.size() > 0 && countTimetableTeacherHas.size() > 3) {
				overdeployed++;
			}
			
			else if (countTimetableTeacherHas.size() > 0 && countTimetableTeacherHas.size() <= 2) {
				underdeployed++;
			}
			
			else if ( countAttendanceTeacherHas != null && countAttendanceTeacherHas.size() == 0) {
				inactive++;
			}
			
			else if ( countAttendanceTeacherHas != null && countAttendanceTeacherHas.size() > 0) {
				active++;
			}
		}
		
		  response.put("totalActive", active);
		  response.put("totalInactive", inactive);
		  response.put("totalUnder", underdeployed);
		  response.put("totalDeploy", deployed);
		  response.put("totalOver", overdeployed);
      
        return response;
    }
	
	public Teacher update(TeacherRequest teaRequest,long id) {
		Optional<Teacher> existing = teaRepository.findById(id);
		if (existing.isPresent()) {
			Teacher teaval = existing.get();
			CommonActivity.copyNonNullProperties(teaRequest, teaval);
			return teaRepository.save(teaval);
		} 
		return null;
	}
	
	public String updatePhoto(long id, String fileName) {
		Optional<Teacher> existing = teaRepository.findById(id);
		if (existing.isPresent()) {
			Teacher teaval = existing.get();		
			teaval.setPhoto(fileName);
			Teacher filledSchool = teaRepository.save(teaval);
			return filledSchool.getPhoto();
		}	   
		
		return null;
	}
	
	public Teacher delete(Long id) {
		Optional<Teacher> tea = teaRepository.findById(id);
		if (tea.isPresent()) {
			Teacher teaval = tea.get();
			teaval.setStatus(-1);
			teaRepository.save(teaval);
			return teaval;
		}
		return null;
	}
	
	public Map<String, Object> getTeachersCreatedWithinDays(int numberOfDays) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(numberOfDays);
        
        Timestamp newEndDate = convertLocalDateToTimestamp(endDate);
        Timestamp newStartDate = convertLocalDateToTimestamp(startDate);

        List<Object[]> results = teaRepository.countTeachersCreatedPerDay(newStartDate, newEndDate);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");

        // Create a map with all days in the range initialized with count 0
        Map<String, Integer> teachersCreatedPerDay = startDate.datesUntil(endDate.plusDays(1))
                .collect(Collectors.toMap(
                        date -> date.format(formatter),
                        date -> 0,
                        (count1, count2) -> count1,
                        LinkedHashMap::new
                ));
        
        // Update the counts for the days with actual results
        for (Object[] result : results) {
        	Date createdDate = (Date) result[0];
            int count = ((Number) result[1]).intValue();
            teachersCreatedPerDay.put( formatter2.format(createdDate), count);
        }
        
        int sumOfDone = sumMapValues(teachersCreatedPerDay);
        Map<String, Object> response = new HashMap<>();
        response.put("teachers", sumOfDone);
        response.put("teachersData", convertMapValuesToList(teachersCreatedPerDay) );

        return response;
    }
	
	private int sumMapValues  ( Map<String, Integer> map ) {
	    int sum = 0;
	    for (int value : map.values()) {
	        sum += value;
	    }
	    return sum;
	}
	
	private ArrayList<Integer> convertMapValuesToList(Map<String, Integer> map) {
	    return new ArrayList<>(map.values());
	}
	
	private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	
	private static Timestamp convertLocalDateToTimestamp(LocalDate localDate) {
        LocalDateTime localDateTime = LocalDateTime.of(localDate, LocalTime.MIDNIGHT);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        return Timestamp.from(zonedDateTime.toInstant());
    }
	
}
