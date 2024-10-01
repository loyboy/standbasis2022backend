package basepackage.stand.standbasisprojectonev1.service;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
import basepackage.stand.standbasisprojectonev1.model.Calendar;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;
import basepackage.stand.standbasisprojectonev1.model.Teacher;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.CalendarRequest;
import basepackage.stand.standbasisprojectonev1.repository.CalendarRepository;
import basepackage.stand.standbasisprojectonev1.repository.SchoolRepository;
import basepackage.stand.standbasisprojectonev1.repository.SchoolgroupRepository;
import basepackage.stand.standbasisprojectonev1.util.AppConstants;
import basepackage.stand.standbasisprojectonev1.util.CommonActivity;

@Service
public class CalendarService {

	@Autowired		
    private CalendarRepository calRepository;
	
	@Autowired
    private SchoolgroupRepository schgroupRepository;
	
	@Autowired		
    private SchoolRepository schRepository;
	
	public List<Calendar> findAllBySchool(Long id) {
		
		Optional<School> sch = schRepository.findById(id);
		if (sch.isPresent()) {
			School schval = sch.get();
			
			return calRepository.findBySchool(schval);
		}
		return null;
		
	}

	public List<Calendar> findAllBySupervisor(String id) {
		List <Calendar> calList = new ArrayList<Calendar>();
		String[] codes = CommonActivity.parseStringForSupervisor(id);
		List<School> schs = schRepository.findBySupervisor( 
			codes.length > 0 ? codes[0] : null,
			codes.length > 1 ? codes[1] : null,
			codes.length > 2 ? codes[2] : null,
			codes.length > 3 ? codes[3] : null
		);
		
		if (schs.size() > 0) {
			for( School sch : schs ) {		
				calList.addAll(calRepository.findBySchool(sch));
			}
			return calList;
		}
		return null;
		
	}
	
	public Optional<Calendar> findAllByStatus(Long schid, Integer id) {
		
	Optional<School> sch = schRepository.findById(schid);
	if (sch.isPresent()) {
		School schval = sch.get();
		
		return calRepository.findByStatus(id, schval);
	}
		return null;
		
	}
	
	public List<Calendar> findAll() {
		
		return calRepository.findAll();
	}
	
public List<Calendar> findByActive() {
		
		return calRepository.findByActive();
	}
	
	public Calendar findCalendar(long id) {
		
		Optional<Calendar> enc = calRepository.findById(id);
		if (enc.isPresent()) {
			Calendar calval = enc.get();
			
			return calval;
		}
		return null;
	}
	
public Map<String, Object> getOrdinaryCalendars( String query, Optional<Long> ownerval, Optional<Long> groupval,  Optional<String> supervisorval) {
		
        Long owner = ownerval.orElse(null); 
        Long group = groupval.orElse(null);
		// Retrieve Teachers
        
        List<Calendar> teas = null;
		String supervisor = supervisorval.orElse(null);      
		String[] codes = CommonActivity.parseStringForSupervisor(supervisor);
             
        if ( query.equals("") || query == null ) {
			if (!supervisor.isEmpty() && !supervisor.equals("")){
				teas = calRepository.findBySupervisor( 
					codes.length > 0 ? codes[0].equalsIgnoreCase("Null") ? null : codes[0] : null,
					codes.length > 1 ? codes[1].equalsIgnoreCase("Null") ? null : codes[1] : null,
					codes.length > 2 ? codes[2].equalsIgnoreCase("Null") ? null : codes[2] : null,
					codes.length > 3 ? codes[3].equalsIgnoreCase("Null") ? null : codes[3] : null
				);
			}
        	else if ( group == null && owner == null ) {
        		teas = calRepository.findAll();
        	}
        	else {
        		Optional<SchoolGroup> schgroupobj = null ;
        		Optional<School> schownerobj = null;
        		
        		if(owner != null) { schownerobj = schRepository.findById( owner ); }
        		if(group != null) { schgroupobj = schgroupRepository.findById( group ); }        		
        		
        			teas = calRepository.findBySchoolPage( 
        					schownerobj == null ? null : schownerobj.get(), 
        	        		schgroupobj == null ? null : schgroupobj.get()
        			);
        	}       	
        }
        else {
			if (!supervisor.isEmpty() && !supervisor.equals("")){
				teas = calRepository.findBySupervisor( 
					codes.length > 0 ? codes[0].equalsIgnoreCase("Null") ? null : codes[0] : null,
					codes.length > 1 ? codes[1].equalsIgnoreCase("Null") ? null : codes[1] : null,
					codes.length > 2 ? codes[2].equalsIgnoreCase("Null") ? null : codes[2] : null,
					codes.length > 3 ? codes[3].equalsIgnoreCase("Null") ? null : codes[3] : null
				);
			}
        	else if ( group == null && owner == null ) {
        		teas = calRepository.filterAll("%"+ query + "%");
        	}
        	else {    
        		
        		Optional<SchoolGroup> schgroupobj = null ;
        		Optional<School> schownerobj = null;
        		
        		if(owner != null) { schownerobj = schRepository.findById( owner ); }
        		if(group != null) { schgroupobj = schgroupRepository.findById( group ); }
        		
        		teas = calRepository.findFilterBySchool( "%"+ query + "%", 
        				schownerobj == null ? null : schownerobj.get(), 
    	        		schgroupobj == null ? null : schgroupobj.get() 
    	         );
        	}
        }

        if(teas.size() == 0) {
        	Map<String, Object> responseEmpty = new HashMap<>();
        	responseEmpty.put("calendars", Collections.emptyList());        	
        	return responseEmpty;
        }
        
        List<Calendar> calarray = new ArrayList<Calendar>(teas);
        
        Map<String, Object> response = new HashMap<>();
        response.put("calendars", calarray);      
        
        return response;
    }
	
	
	public Map<String, Object> getPaginatedCalendars(int page, int size, String query, Optional<Long> ownerval, Optional<Long> groupval, Optional<String> supervisorval) {
        validatePageNumberAndSize(page, size);
        Long owner = ownerval.orElse(null); 
        Long group = groupval.orElse(null);
        // Retrieve Calendars
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Calendar> schs = null;
		String supervisor = supervisorval.orElse(null);      
		String[] codes = CommonActivity.parseStringForSupervisor(supervisor);
        
        if ( query.equals("") || query == null ) {
        	
			if (!supervisor.isEmpty() && !supervisor.equals("")){
				schs = calRepository.findBySupervisor( 
					codes.length > 0 ? codes[0].equalsIgnoreCase("Null") ? null : codes[0] : null,
					codes.length > 1 ? codes[1].equalsIgnoreCase("Null") ? null : codes[1] : null,
					codes.length > 2 ? codes[2].equalsIgnoreCase("Null") ? null : codes[2] : null,
					codes.length > 3 ? codes[3].equalsIgnoreCase("Null") ? null : codes[3] : null,
					pageable
				);
			}

			else if ( group == null && owner == null ) {
        		schs = calRepository.findAll(pageable);
        	}

        	else {
        		//Optional<School> schobj = schRepository.findById( owner );
        		//schs = calRepository.findBySchoolPage( schobj.orElse(null), pageable);
        		
        		Optional<SchoolGroup> schgroupobj = null ;
        		Optional<School> schownerobj = null;
        		
        		if(owner != null) { schownerobj = schRepository.findById( owner ); }
        		if(group != null) { schgroupobj = schgroupRepository.findById( group ); }        		
        		
        			schs = calRepository.findBySchoolPage( 
        					schownerobj == null ? null : schownerobj.get(), 
        	        		schgroupobj == null ? null : schgroupobj.get()
        					, pageable);
        	}        	
        }
        else {
			if (!supervisor.isEmpty() && !supervisor.equals("")){
				schs = calRepository.findFilterBySupervisor("%"+ query + "%",  
					codes.length > 0 ? codes[0].equalsIgnoreCase("Null") ? null : codes[0] : null,
					codes.length > 1 ? codes[1].equalsIgnoreCase("Null") ? null : codes[1] : null,
					codes.length > 2 ? codes[2].equalsIgnoreCase("Null") ? null : codes[2] : null,
					codes.length > 3 ? codes[3].equalsIgnoreCase("Null") ? null : codes[3] : null,
					pageable
				);
			}

        	else if ( group == null && owner == null ) {
        		schs = calRepository.filter("%"+ query + "%",  pageable);
        	}
			
        	else {    
        		//Optional<School> schobj = schRepository.findById( owner );
        		//schs = calRepository.findFilterBySchool( "%"+ query + "%", schobj.orElse(null), pageable);
        		Optional<SchoolGroup> schgroupobj = null ;
        		Optional<School> schownerobj = null;
        		
        		if(owner != null) { schownerobj = schRepository.findById( owner ); }
        		if(group != null) { schgroupobj = schgroupRepository.findById( group ); }
        		
        	// Optional<School> schobj = schRepository.findById( owner );
        		schs = calRepository.findFilterBySchool( "%"+ query + "%", 
        				schownerobj == null ? null : schownerobj.get(), 
    	        		schgroupobj == null ? null : schgroupobj.get(), 
    	        pageable);
        	}
        }

        if(schs.getNumberOfElements() == 0) {
        	Map<String, Object> responseEmpty = new HashMap<>();
        	responseEmpty.put("calendars", Collections.emptyList());
        	responseEmpty.put("currentPage", schs.getNumber());
        	responseEmpty.put("totalItems", schs.getTotalElements());
        	responseEmpty.put("totalPages", schs.getTotalPages());
        	
        	return responseEmpty;
        }
        
        List<Calendar> calarray = new ArrayList<Calendar>();
        
        calarray = schs.getContent();
        
        Map<String, Object> response = new HashMap<>();
        response.put("calendars", calarray);
        response.put("currentPage", schs.getNumber());
        response.put("totalItems", schs.getTotalElements());
        response.put("totalPages", schs.getTotalPages());
        response.put("isLast", schs.isLast());
        
        Map<String, Object> response2 = getOrdinaryCalendars(query, ownerval, groupval, supervisorval);
        
        @SuppressWarnings("unchecked")
		List<Calendar> listCalendar = (List<Calendar>) response2.get("calendars");
        
        long activeCalendars = listCalendar.stream().filter(sch -> sch.getStatus() == 1).count();       
        long inactiveCalendars = listCalendar.stream().filter(sch -> sch.getStatus() == 0).count();
        
        response.put("totalActive", activeCalendars);
        response.put("totalInactive", inactiveCalendars);
        return response;
    }
	
	public Calendar update(CalendarRequest calRequest,long id) {
		Optional<Calendar> existing = calRepository.findById(id);
		if (existing.isPresent()) {
			Calendar calval = existing.get();
			
			copyNonNullProperties(calRequest, calval);
			
			if (calRequest.getStartdate() != null) {
				calval.setStartdate( CommonActivity.parseTimestamp(calRequest.getStartdate() ) );
			}
			if (calRequest.getEnddate() != null) {
				calval.setEnddate( CommonActivity.parseTimestamp(calRequest.getEnddate() ) );
			}
			if (calRequest.getLsnstartdate() != null) {
				calval.setLsnstartdate( CommonActivity.parseTimestamp(calRequest.getLsnstartdate()) );
			}
			return calRepository.save(calval);
		}   
		
		return null;
	}
	
	public Calendar delete(Long id) {
		Optional<Calendar> tea = calRepository.findById(id);
		if (tea.isPresent()) {
			Calendar calval = tea.get();
			calval.setStatus(-1);
			calRepository.save(calval);
			return calval;
		}
		return null;
	}
	
	public Integer calculateWeeks(String s, String e) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date start = null;
        Date end = null;
		try {
			start = dateFormat.parse(s);
			end = dateFormat.parse(e);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return findWeeks(start, end); 
	}
	
	private static int findWeeks(Date start, Date end) {
        java.util.Calendar cal = new GregorianCalendar();
        cal.setTime(start);

        int weeks = 0;
        while (cal.getTime().before(end)) {
            cal.add(java.util.Calendar.WEEK_OF_YEAR, 1);
            weeks++;
        }
        return weeks;
    }
	
	private static void copyNonNullProperties(Object src, Object target) {
	    BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
	}

	private static String[] getNullPropertyNames (Object source) {
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
