package basepackage.stand.standbasisprojectonev1.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
import basepackage.stand.standbasisprojectonev1.model.Enrollment;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;
import basepackage.stand.standbasisprojectonev1.model.Teacher;
import basepackage.stand.standbasisprojectonev1.model.TimeTable;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.ClassRequest;
import basepackage.stand.standbasisprojectonev1.repository.ClassStreamRepository;
import basepackage.stand.standbasisprojectonev1.repository.SchoolRepository;
import basepackage.stand.standbasisprojectonev1.repository.SchoolgroupRepository;
import basepackage.stand.standbasisprojectonev1.repository.TeacherRepository;
import basepackage.stand.standbasisprojectonev1.util.AppConstants;
import basepackage.stand.standbasisprojectonev1.util.CommonActivity;

@Service
public class ClassService {

	private final Long FCTA_ID = 4L;
	
	@Autowired		
    private ClassStreamRepository classRepository;
	
	@Autowired		
    private SchoolRepository schRepository;
	
	@Autowired		
    private TeacherRepository teaRepository;
	
	@Autowired
    private SchoolgroupRepository schgroupRepository;
	
	@Autowired
	TimetableService serviceTimetable;
	
	public List<ClassStream> findAllBySchool(Long id) {		
		Optional<School> sch = schRepository.findById(id);
		if (sch.isPresent()) {
			School schval = sch.get();			
			return classRepository.findBySchool(schval);
		}
		return null;		
	}
	
	public List<ClassStream> findAllByTeacher(Long id) {		
		Optional<Teacher> tea = teaRepository.findById(id);
		if (tea.isPresent()) {
			Teacher teaval = tea.get();			
			return classRepository.findByTeacher(teaval);
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
	
public Map<String, Object> getOrdinaryClassStreams( String query, Optional<Long> groupval, Optional<Long> ownerval, Optional<String> supervisorval) {
		
        Long owner = ownerval.orElse(null);     
        Long group = groupval.orElse(null);
        
		// Retrieve Teachers        
        List<ClassStream> cls = null;
		String supervisor = supervisorval.orElse(null);      
		String[] codes = CommonActivity.parseStringForSupervisor(supervisor);
        
        if ( query.equals("") || query == null ) {
			if (!supervisor.isEmpty() && !supervisor.equals("")){
				cls = classRepository.findBySupervisor( 
					codes.length > 0 ? codes[0].equalsIgnoreCase("Null") ? null : codes[0] : null,
					codes.length > 1 ? codes[1].equalsIgnoreCase("Null") ? null : codes[1] : null,
					codes.length > 2 ? codes[2].equalsIgnoreCase("Null") ? null : codes[2] : null,
					codes.length > 3 ? codes[3].equalsIgnoreCase("Null") ? null : codes[3] : null
				);
			}
        	else if ( group == null && owner == null ) {
        		cls = classRepository.findAll();
        	}
        	else {
        		Optional<SchoolGroup> schgroupobj = null ;
        		Optional<School> schownerobj = null;
        		
        		if(owner != null) { schownerobj = schRepository.findById( owner ); }
        		if(group != null) { schgroupobj = schgroupRepository.findById( group ); }        		
        		
        			cls = classRepository.findBySchoolPage( 
        					schownerobj == null ? null : schownerobj.get(), 
        	        		schgroupobj == null ? null : schgroupobj.get()
        			);
        	}       	
        }
        else {
			if (!supervisor.isEmpty() && !supervisor.equals("")){
				cls = classRepository.findBySupervisor( 
					codes.length > 0 ? codes[0].equalsIgnoreCase("Null") ? null : codes[0] : null,
					codes.length > 1 ? codes[1].equalsIgnoreCase("Null") ? null : codes[1] : null,
					codes.length > 2 ? codes[2].equalsIgnoreCase("Null") ? null : codes[2] : null,
					codes.length > 3 ? codes[3].equalsIgnoreCase("Null") ? null : codes[3] : null
				);
			}
        	else if ( group == null && owner == null ) {
        		cls = classRepository.filterAll("%"+ query + "%");
        	}
        	else {    
        		
        		Optional<SchoolGroup> schgroupobj = null ;
        		Optional<School> schownerobj = null;
        		
        		if(owner != null) { schownerobj = schRepository.findById( owner ); }
        		if(group != null) { schgroupobj = schgroupRepository.findById( group ); }
        		
        		cls = classRepository.findFilterBySchoolPage( "%"+ query + "%", 
        				schownerobj == null ? null : schownerobj.get(), 
    	        		schgroupobj == null ? null : schgroupobj.get() 
    	         );
        	}
        }

        if(cls.size() == 0) {
        	Map<String, Object> responseEmpty = new HashMap<>();
        	responseEmpty.put("classrooms", Collections.emptyList());
        	
        	return responseEmpty;
        }
        
        List<ClassStream> calarray = new ArrayList<ClassStream>(cls);
        
        Map<String, Object> response = new HashMap<>();
        response.put("classrooms", calarray);      
        
        return response;
    }
	
	
	public Map<String, Object> getPaginatedClassStreams(int page, int size, String query,  Optional<Long> groupval, Optional<Long> ownerval, Optional<String> supervisorval) {
        validatePageNumberAndSize(page, size);
        Long owner = ownerval.orElse(null);
        Long group = groupval.orElse(null);
        int undeployedSeconday = 0;
		int undeployedPrimary = 0; 
		String supervisor = supervisorval.orElse(null);      
		String[] codes = CommonActivity.parseStringForSupervisor(supervisor);
        // Retrieve ClassStreams
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<ClassStream> schs = null;
        
        if ( query.equals("") || query == null ) {
			if (!supervisor.isEmpty() && !supervisor.equals("")){
				schs = classRepository.findBySupervisor( 
					codes.length > 0 ? codes[0].equalsIgnoreCase("Null") ? null : codes[0] : null,
					codes.length > 1 ? codes[1].equalsIgnoreCase("Null") ? null : codes[1] : null,
					codes.length > 2 ? codes[2].equalsIgnoreCase("Null") ? null : codes[2] : null,
					codes.length > 3 ? codes[3].equalsIgnoreCase("Null") ? null : codes[3] : null,
					pageable
				);
			}
        	else if ( owner == null ) {
        		schs = classRepository.findAll(pageable);
        	}
			
        	else {
        		Optional<SchoolGroup> schgroupobj = null ;
        		Optional<School> schownerobj = null;
        		
        		if(owner != null) { schownerobj = schRepository.findById( owner ); }
        		if(group != null) { schgroupobj = schgroupRepository.findById( group ); }  
        		
        		//Optional<School> schobj = schRepository.findById( owner );
        		schs = classRepository.findBySchoolPage( 
        				schownerobj == null ? null : schownerobj.get(), 
    	        		schgroupobj == null ? null : schgroupobj.get(), pageable);
        	}        	
        }
        else {
        	if (!supervisor.isEmpty() && !supervisor.equals("")){
				schs = classRepository.findFilterBySupervisor("%"+ query + "%", 
					codes.length > 0 ? codes[0].equalsIgnoreCase("Null") ? null : codes[0] : null,
					codes.length > 1 ? codes[1].equalsIgnoreCase("Null") ? null : codes[1] : null,
					codes.length > 2 ? codes[2].equalsIgnoreCase("Null") ? null : codes[2] : null,
					codes.length > 3 ? codes[3].equalsIgnoreCase("Null") ? null : codes[3] : null, 
				pageable);
			}

			else if ( owner == null ) {
        		schs = classRepository.filter("%"+ query + "%",  pageable);
        	}
			
        	else {    
        		Optional<SchoolGroup> schgroupobj = null ;
        		Optional<School> schownerobj = null;
        		
        		if(owner != null) { schownerobj = schRepository.findById( owner ); }
        		if(group != null) { schgroupobj = schgroupRepository.findById( group ); } 
        		
        		schs = classRepository.findFilterBySchoolPage( "%"+ query + "%", 
        				schownerobj == null ? null : schownerobj.get(), 
    	        		schgroupobj == null ? null : schgroupobj.get(), pageable);
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
        response.put("totalPages", schs.getTotalPages());
        response.put("isLast", schs.isLast());
        
        Map<String, Object> response2 = getOrdinaryClassStreams(query, groupval, ownerval, supervisorval);
        
        Map<String, Object> response3 = serviceTimetable.getOrdinaryTimeTables(query, ownerval, groupval, supervisorval);        
         
        @SuppressWarnings("unchecked")
		List<ClassStream> listClassrooms = (List<ClassStream>) response2.get("classrooms");
        
        @SuppressWarnings("unchecked")
		List<TimeTable> listTimetable = (List<TimeTable>) response3.get("timetables");        
        
        List<ClassStream> countPrimary = listClassrooms.stream().anyMatch(classrm -> classrm.getSchool().getOwner().getId() == FCTA_ID) ? listClassrooms.stream()
        		.filter(en -> en.getStatus() == 1 && en.getSchool().getType_of().equals("fctubeb") && en.getClass_index() <= 6 && en.getClass_index() >= 1 )
        		.collect(Collectors.toList()) : listClassrooms.stream()
        		.filter(en -> en.getStatus() == 1 && en.getSchool().getType_of().equals("subeb") && en.getClass_index() <= 6 && en.getClass_index() >= 1 )
        		.collect(Collectors.toList());
        
        List<ClassStream> countSecondaryJunior = listClassrooms.stream().anyMatch(classrm -> classrm.getSchool().getOwner().getId() == FCTA_ID) ? listClassrooms.stream()
        		.filter(en -> en.getStatus() == 1 && en.getSchool().getType_of().equals("fctubeb") && en.getClass_index() <= 9 && en.getClass_index() >= 7 )
        		.collect(Collectors.toList()) : listClassrooms.stream()
        		.filter(en -> en.getStatus() == 1 && en.getSchool().getType_of().equals("subeb") && en.getClass_index() <= 9 && en.getClass_index() >= 7 )
        		.collect(Collectors.toList());
        
        List<ClassStream> countSecondarySenior = listClassrooms.stream().anyMatch(classrm -> classrm.getSchool().getOwner().getId() == FCTA_ID) ? listClassrooms.stream()
        		.filter(en -> en.getStatus() == 1 && en.getSchool().getType_of().equals("fctseb") && en.getClass_index() <= 12 && en.getClass_index() >= 10 )
        		.collect(Collectors.toList()) : listClassrooms.stream()
        		.filter(en -> en.getStatus() == 1 && en.getSchool().getType_of().equals("semb") && en.getClass_index() <= 12 && en.getClass_index() >= 10 )
        		.collect(Collectors.toList());
        
        for (ClassStream clsT : listClassrooms) {
        	
        	List<TimeTable> countTimetableTeacherSecondaryHas = listTimetable.stream()
					.filter(tt -> tt.getClass_stream().getClsId() == clsT.getClsId() && tt.getClass_stream().getSchool().getType_of().equals("subeb") && clsT.getClass_index() <= 12 && clsT.getClass_index() >= 7 && tt.getStatus() == 1 && tt.getCalendar().getStatus() == 1 )
			        .filter(distinctByKey(pr -> Arrays.asList( pr.getSubject(),  pr.getClass_stream() )))
			        .collect(Collectors.toList());
        	
        	List<TimeTable> countTimetableTeacherPrimaryHas = listTimetable.stream()
					.filter(tt -> tt.getClass_stream().getClsId() == clsT.getClsId() && tt.getClass_stream().getSchool().getType_of().equals("subeb") && clsT.getClass_index() <= 6 && clsT.getClass_index() >= 1 && tt.getStatus() == 1 && tt.getCalendar().getStatus() == 1 )
			        .filter(distinctByKey(pr -> Arrays.asList( pr.getSubject(),  pr.getClass_stream() )))
			        .collect(Collectors.toList());
        	
        	if (countTimetableTeacherSecondaryHas.size() < 4) {
				undeployedSeconday++;
			}
			
			if (countTimetableTeacherPrimaryHas.size() < 4 ) {
				undeployedPrimary++;
			}
        }
        	response.put("totalItems", listClassrooms.size());
        	response.put("totalPrimary", countPrimary.size() );
        	response.put("totalSJunior", countSecondaryJunior.size() );
        	response.put("totalSSenior", countSecondarySenior.size());
        	response.put("totalSUndeployed", undeployedSeconday);
        	response.put("totalPUndeployed", undeployedPrimary);
        
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
	
	private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
}
