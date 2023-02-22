package basepackage.stand.standbasisprojectonev1.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
	
	public Map<String, Object> getPaginatedTeachers(int page, int size, String query, Optional<Long> ownerval) {
		CommonActivity.validatePageNumberAndSize(page, size);
        Long owner = ownerval.orElse(null);        
        // Retrieve Teachers
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Teacher> schs = null;
      //  System.out.println("Long is set "+ owner);
        
        if ( query.equals("") || query == null ) {
        	if ( owner == null ) {
        		schs = teaRepository.findAll(pageable);
        	}
        	else {
        		Optional<School> schobj = schRepository.findById( owner );
        		schs = teaRepository.findBySchool( schobj.orElse(null), pageable);
        	}        	
        }
        else {
        	if ( owner == null ) {
        		schs = teaRepository.filter("%"+ query + "%",  pageable);
        	}
        	else {    
        		Optional<School> schobj = schRepository.findById( owner );
        		schs = teaRepository.findFilterBySchool( "%"+ query + "%", schobj.orElse(null), pageable);
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
        
       // long active = 1; long inactive = 0;
      /*  long sriTeachers = schRepository.countBySri(active);
        long nonSriTeachers = schRepository.countBySri(inactive);
        long inactiveTeachers = schRepository.countByStatus(inactive);*/
        
        long activeTeachers = teaarray.stream().filter(sch -> sch.getStatus() == 1).count();       
        long inactiveTeachers = teaarray.stream().filter(sch -> sch.getStatus() == 0).count();
        
        response.put("totalActive", activeTeachers);
        response.put("totalInactive", inactiveTeachers);
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
	
}
