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
import basepackage.stand.standbasisprojectonev1.payload.onboarding.CalendarRequest;
import basepackage.stand.standbasisprojectonev1.repository.CalendarRepository;
import basepackage.stand.standbasisprojectonev1.repository.SchoolRepository;
import basepackage.stand.standbasisprojectonev1.util.AppConstants;
import basepackage.stand.standbasisprojectonev1.util.CommonActivity;

@Service
public class CalendarService {

	@Autowired		
    private CalendarRepository calRepository;
	
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
	
	public Map<String, Object> getPaginatedCalendars(int page, int size, String query, Optional<Long> ownerval) {
        validatePageNumberAndSize(page, size);
        Long owner = ownerval.orElse(null);        
        // Retrieve Calendars
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Calendar> schs = null;
        
        if ( query.equals("") || query == null ) {
        	if ( owner == null ) {
        		schs = calRepository.findAll(pageable);
        	}
        	else {
        		Optional<School> schobj = schRepository.findById( owner );
        		schs = calRepository.findBySchoolPage( schobj.orElse(null), pageable);
        	}        	
        }
        else {
        	if ( owner == null ) {
        		schs = calRepository.filter("%"+ query + "%",  pageable);
        	}
        	else {    
        		Optional<School> schobj = schRepository.findById( owner );
        		schs = calRepository.findFilterBySchool( "%"+ query + "%", schobj.orElse(null), pageable);
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
        
       // long active = 1; long inactive = 0;
      /*  long sriCalendars = schRepository.countBySri(active);
        long nonSriCalendars = schRepository.countBySri(inactive);
        long inactiveCalendars = schRepository.countByStatus(inactive);*/
        
        long activeCalendars = calarray.stream().filter(sch -> sch.getStatus() == 1).count();       
        long inactiveCalendars = calarray.stream().filter(sch -> sch.getStatus() == 0).count();
        
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
