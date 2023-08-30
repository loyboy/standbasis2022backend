package basepackage.stand.standbasisprojectonev1.service;

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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import basepackage.stand.standbasisprojectonev1.exception.BadRequestException;
import basepackage.stand.standbasisprojectonev1.model.Attendance;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;
import basepackage.stand.standbasisprojectonev1.model.Teacher;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.SchoolRequest;
import basepackage.stand.standbasisprojectonev1.repository.SchoolRepository;
import basepackage.stand.standbasisprojectonev1.repository.SchoolgroupRepository;
import basepackage.stand.standbasisprojectonev1.util.AppConstants;

@Service
public class SchoolService {

	private static final Logger logger = LoggerFactory.getLogger(SchoolService.class);
	
	@Autowired		
    private SchoolRepository schRepository;
	
	@Autowired
    private SchoolgroupRepository schgroupRepository;
	
	public List<School> findAll() {
		
		return schRepository.findAll();
	}
	
	public List<School> findAllByGroup(Long groupId) {
		
		Optional<SchoolGroup> schgroupval = schgroupRepository.findById(groupId);
		if (schgroupval.isPresent()) {
			SchoolGroup schgroup = schgroupval.get();
			List<School> schval = schRepository.findByOwner(schgroup);
			return schval;
		}
		return null;
	}
	
	public List<School> findAllByLga(String lga) {
		List<School> schval = schRepository.findByLga(lga);
		return schval;
	}
	
	public List<School> findAllByState(String state) {
		List<School> schval = schRepository.findByState(state);
		return schval;
	}
	
	public School findSchool(long id) {
		
		Optional<School> sch = schRepository.findById(id);
		if (sch.isPresent()) {
			School schval = sch.get();
			
			return schval;
		}
		return null;
	}
	
	public Long countBySchoolGroup(Long id) {		
		Optional<SchoolGroup> schgroup = schgroupRepository.findById(id);
		if (schgroup.isPresent()) {
			SchoolGroup schval = schgroup.get();
			
			Long countval = schRepository.countBySchoolGroup(schval);
			
			return countval;
		}
		return null;
	}
	
	public Map<String, Object> getOrdinarySchools( String query, Optional<Long> groupval) {
		    
        Long group = groupval.orElse(null);
        
		// Retrieve Teachers
        
        List<School> schs = null;
        
        if ( query.equals("") || query == null ) {
        	if ( group == null ) {
        		schs = schRepository.findAll();
        	}
        	else {
        		Optional<SchoolGroup> schgroupobj = null ;
        		 
        		if(group != null) { schgroupobj = schgroupRepository.findById( group ); }        		
        		
        			schs = schRepository.findByOwner(
        	        	schgroupobj == null ? null : schgroupobj.get()
        			);
        	}       	
        }
        else {
        	if ( group == null ) {
        		schs = schRepository.filterAll("%"+ query + "%");
        	}
        	else {    
        		
        		Optional<SchoolGroup> schgroupobj = null ;
        		
        		if(group != null) { schgroupobj = schgroupRepository.findById( group ); }
        		
        		schs = schRepository.findFilterByOwner( "%"+ query + "%", 
    	        		schgroupobj == null ? null : schgroupobj.get() 
    	         );
        	}
        }

        if(schs.size() == 0) {
        	Map<String, Object> responseEmpty = new HashMap<>();
        	responseEmpty.put("schools", Collections.emptyList());
        	
        	return responseEmpty;
        }
        
        List<School> calarray = new ArrayList<School>(schs);
        
        Map<String, Object> response = new HashMap<>();
        response.put("schools", calarray);      
        
        return response;
    }
	
	public Map<String, Object> getPaginatedSchools(int page, int size, String query, Optional<Long> ownerval) {
        validatePageNumberAndSize(page, size);
        Long owner = ownerval.orElse(null);        
        // Retrieve Schools
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<School> schs = null;
      //  System.out.println("Long is set "+ owner);
        
        if ( query.equals("") || query == null ) {
        	if ( owner == null ) {
        		schs = schRepository.findAll(pageable);
        	}
        	else {
        		Optional<SchoolGroup> sg = schgroupRepository.findById( owner );
        		schs = schRepository.findByOwner( sg.orElse(null), pageable);
        	}        	
        }
        else {
        	if ( owner == null ) {
        		schs = schRepository.filter("%"+ query + "%",  pageable);
        	}
        	else {    
        		Optional<SchoolGroup> sg = schgroupRepository.findById( owner );
        		schs = schRepository.findFilterByOwner( "%"+ query + "%", sg.orElse(null), pageable);
        	}
        }

        if(schs.getNumberOfElements() == 0) {
        	Map<String, Object> responseEmpty = new HashMap<>();
        	responseEmpty.put("schools", Collections.emptyList());
        	responseEmpty.put("currentPage", schs.getNumber());
        	responseEmpty.put("totalItems", schs.getTotalElements());
        	responseEmpty.put("totalPages", schs.getTotalPages());
        	
        	return responseEmpty;
        }
        
        List<School> scharray = new ArrayList<School>();
        
        scharray = schs.getContent();
        
        Map<String, Object> response = new HashMap<>();
        response.put("schools", scharray);
        response.put("currentPage", schs.getNumber());
        response.put("totalItems", schs.getTotalElements());
        response.put("totalPages", schs.getTotalPages());
        response.put("isLast", schs.isLast());
        
        Map<String, Object> response2 = getOrdinarySchools(query, ownerval);
        
        @SuppressWarnings("unchecked")
		List<School> listSchool = (List<School>) response2.get("schools");
        
        List<School> countSecondary = listSchool.stream()
			 	.filter(ss -> ss.getType_of().equals("secondary") )
		        .collect(Collectors.toList());
        List<School> countPrimary = listSchool.stream()
			 	.filter(ss -> ss.getType_of().equals("primary") )
		        .collect(Collectors.toList());
        
        response.put("totalSecondary", countSecondary.size());
		response.put("totalPrimary", countPrimary.size());
        
        return response;
    }
	
	public School update(SchoolRequest schRequest,long id) {
		Optional<School> existing = schRepository.findById(id);
		if (existing.isPresent()) {
			School schval = existing.get();
			copyNonNullProperties(schRequest, schval);			
			return schRepository.save(schval);
		}	   
		
		return null;
	}
	
	public String updateLogo(long id, String fileName) {
		Optional<School> existing = schRepository.findById(id);
		if (existing.isPresent()) {
			School schval = existing.get();		
			schval.setLogo(fileName);
			School filledSchool = schRepository.save(schval);
			return filledSchool.getLogo();
		}	   
		
		return null;
	}
	
	public School delete(Long id) {
		Optional<School> sch = schRepository.findById(id);
		if (sch.isPresent()) {
			School schval = sch.get();
			schval.setStatus(-1);
			schRepository.save(schval);
			return schval;
		}
		return null;
	}
	
	public Map<String, Object> getSchoolsCreatedWithinDays(int numberOfDays) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(numberOfDays);
        
        Timestamp newEndDate = convertLocalDateToTimestamp(endDate);
        Timestamp newStartDate = convertLocalDateToTimestamp(startDate);

        List<Object[]> results = schRepository.countSchoolsCreatedPerDay(newStartDate, newEndDate);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");

        // Create a map with all days in the range initialized with count 0
        Map<String, Integer> schoolsCreatedPerDay = startDate.datesUntil(endDate.plusDays(1))
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
            schoolsCreatedPerDay.put( formatter2.format(createdDate) , count);
        }
        
        int sumOfDone = sumMapValues(schoolsCreatedPerDay);
        Map<String, Object> response = new HashMap<>();
        response.put("schools", sumOfDone);
        response.put("schoolsData", convertMapValuesToList(schoolsCreatedPerDay) );

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
	
	private static Timestamp convertLocalDateToTimestamp(LocalDate localDate) {
        LocalDateTime localDateTime = LocalDateTime.of(localDate, LocalTime.MIDNIGHT);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        return Timestamp.from(zonedDateTime.toInstant());
    }
	
	public static void copyNonNullProperties(Object src, Object target) {
	    BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
	}

	public static String[] getNullPropertyNames (Object source) {
	    final BeanWrapper src = new BeanWrapperImpl(source);
	    java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

	    Set<String> emptyNames = new HashSet<String>();
	    for(java.beans.PropertyDescriptor pd : pds) {
	        Object srcValue = src.getPropertyValue(pd.getName());
	        if (srcValue == null) emptyNames.add(pd.getName());
	    }
	    String[] result = new String[emptyNames.size()];
	    return emptyNames.toArray(result);
	}
	
	private void validatePageNumberAndSize(int page, int size) {
	        if(page < 0) {
	            throw new BadRequestException("Page number cannot be less than zero.");
	        }

	        if(size > AppConstants.MAX_PAGE_SIZE) {
	            throw new BadRequestException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
	        }
	    }
}
