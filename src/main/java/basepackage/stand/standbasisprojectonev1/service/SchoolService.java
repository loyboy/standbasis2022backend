package basepackage.stand.standbasisprojectonev1.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;
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
        
       // long active = 1; long inactive = 0;
      /*  long sriSchools = schRepository.countBySri(active);
        long nonSriSchools = schRepository.countBySri(inactive);
        long inactiveSchools = schRepository.countByStatus(inactive);*/
        
        long sriSchools = scharray.stream().filter(sch -> sch.getSri() == 1).count();
        long nonSriSchools = scharray.stream().filter(sch -> sch.getSri() == 0).count();
        long inactiveSchools = scharray.stream().filter(sch -> sch.getStatus() == 0).count();
        
        response.put("totalSri", sriSchools);
        response.put("totalNonSri", nonSriSchools);
        response.put("totalInactive", inactiveSchools);
        return response;
    }
	
	public School update(SchoolRequest schRequest,long id) {
		Optional<School> existing = schRepository.findById(id);
		if (existing.isPresent()) {
			School schval = existing.get();
			copyNonNullProperties(schRequest, schval);
			//schval.setLogo(fn);
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
