package basepackage.stand.standbasisprojectonev1.service;

import java.util.ArrayList;
import java.util.Collections;
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
import basepackage.stand.standbasisprojectonev1.model.ClassStream;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.ClassRequest;
import basepackage.stand.standbasisprojectonev1.repository.ClassStreamRepository;
import basepackage.stand.standbasisprojectonev1.repository.SchoolRepository;
import basepackage.stand.standbasisprojectonev1.util.AppConstants;

@Service
public class ClassService {
	
	@Autowired		
    private ClassStreamRepository classRepository;
	
	@Autowired		
    private SchoolRepository schRepository;
	
	public List<ClassStream> findAllBySchool(Long id) {		
		Optional<School> sch = schRepository.findById(id);
		if (sch.isPresent()) {
			School schval = sch.get();			
			return classRepository.findBySchool(schval);
		}
		return null;		
	}
	
	public List<ClassStream> findAll() {		
		return classRepository.findAll();
	}
	
	public ClassStream findClassStream(long id) {		
		Optional<ClassStream> enc = classRepository.findById(id);
		if (enc.isPresent()) {
			ClassStream classval = enc.get();			
			return classval;
		}
		return null;
	}
	
	public Long countBySchool(long id) {		
		Optional<School> sch = schRepository.findById(id);
		if (sch.isPresent()) {
			School schval = sch.get();
			
			long countval = classRepository.countBySchool(schval);
			
			return countval;
		}
		return null;
	}
	
	public Map<String, Object> getPaginatedClassStreams(int page, int size, String query, Optional<Long> ownerval) {
        validatePageNumberAndSize(page, size);
        Long owner = ownerval.orElse(null);        
        // Retrieve ClassStreams
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<ClassStream> schs = null;
        
        if ( query.equals("") || query == null ) {
        	if ( owner == null ) {
        		schs = classRepository.findAll(pageable);
        	}
        	else {
        		Optional<School> schobj = schRepository.findById( owner );
        		schs = classRepository.findBySchoolPage( schobj.orElse(null), pageable);
        	}        	
        }
        else {
        	if ( owner == null ) {
        		schs = classRepository.filter("%"+ query + "%",  pageable);
        	}
        	else {    
        		Optional<School> schobj = schRepository.findById( owner );
        		schs = classRepository.findFilterBySchoolPage( "%"+ query + "%", schobj.orElse(null), pageable);
        	}
        }

        if(schs.getNumberOfElements() == 0) {
        	Map<String, Object> responseEmpty = new HashMap<>();
        	responseEmpty.put("classrooms", Collections.emptyList());
        	responseEmpty.put("currentPage", schs.getNumber());
        	responseEmpty.put("totalItems", schs.getTotalElements());
        	responseEmpty.put("totalPages", schs.getTotalPages());
        	
        	return responseEmpty;
        }
        
        List<ClassStream> teaarray = new ArrayList<ClassStream>();
        
        teaarray = schs.getContent();
        
        Map<String, Object> response = new HashMap<>();
        response.put("classrooms", teaarray);
        response.put("currentPage", schs.getNumber());
        response.put("totalItems", schs.getTotalElements());
        response.put("totalPages", schs.getTotalPages());
        response.put("isLast", schs.isLast());
        
        long activeClassStreams = teaarray.stream().filter(sch -> sch.getStatus() == 1).count();       
        long inactiveClassStreams = teaarray.stream().filter(sch -> sch.getStatus() == 0).count();
        
        response.put("totalActive", activeClassStreams);
        response.put("totalInactive", inactiveClassStreams);
        return response;
    }
	
	public ClassStream update(ClassRequest classRequest,long id) {
		Optional<ClassStream> existing = classRepository.findById(id);
		if (existing.isPresent()) {
			ClassStream classval = existing.get();
			copyNonNullProperties(classRequest, classval);
			return classRepository.save(classval);
		} 
		return null;
	}
	
	public ClassStream delete(Long id) {
		Optional<ClassStream> tea = classRepository.findById(id);
		if (tea.isPresent()) {
			ClassStream classval = tea.get();
			classval.setStatus(-1);
			classRepository.save(classval);
			return classval;
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
