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
import basepackage.stand.standbasisprojectonev1.model.Student;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.StudentRequest;
import basepackage.stand.standbasisprojectonev1.repository.SchoolRepository;
import basepackage.stand.standbasisprojectonev1.repository.SchoolgroupRepository;
import basepackage.stand.standbasisprojectonev1.repository.StudentRepository;
import basepackage.stand.standbasisprojectonev1.util.CommonActivity;

@Service
public class StudentService {

private static final Logger logger = LoggerFactory.getLogger(StudentService.class);
	
	@Autowired		
    private StudentRepository studentRepository;
	
	@Autowired		
    private SchoolRepository schRepository;
	
	@Autowired
    private SchoolgroupRepository schgroupRepository;

	public List<Student> findAll() {		
		return studentRepository.findAll();
	}
	
	public List<Student> findAllBySchool(Long id) {		
		Optional<School> sch = schRepository.findById(id);
		if (sch.isPresent()) {
			School schval = sch.get();			
			return studentRepository.findBySchool(schval);
		}
		return null;		
	}
	
	public Long countBySchool(long id) {		
		Optional<School> sch = schRepository.findById(id);
		if (sch.isPresent()) {
			School schval = sch.get();
			
			long countval = studentRepository.countBySchool(schval);
			
			return countval;
		}
		return null;
	}
	
	public Student findStudent(long id) {		
		Optional<Student> sch = studentRepository.findById(id);
		if (sch.isPresent()) {
			Student schval = sch.get();			
			return schval;
		}
		return null;
	}
	
	public Long countBySchoolGroup(long id) {		
		Optional<SchoolGroup> schgroup = schgroupRepository.findById(id);
		if (schgroup.isPresent()) {
			SchoolGroup schval = schgroup.get();
			
			long countval = studentRepository.countBySchoolGroup(schval);
			
			return countval;
		}
		return null;
	}
	
	public Map<String, Object> getPaginatedStudents(int page, int size, String query) {
		CommonActivity.validatePageNumberAndSize(page, size);

        // Retrieve group of Schools
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Student> students = null;
       
        if ( query.equals("") || query == null ) {
        	students = studentRepository.findAll(pageable);
        }
        else {
        	students = studentRepository.filter("%"+ query + "%",  pageable);
        }

        if(students.getNumberOfElements() == 0) {
        	Map<String, Object> responseEmpty = new HashMap<>();
        	responseEmpty.put("students", Collections.emptyList());
        	responseEmpty.put("currentPage", students.getNumber());
        	responseEmpty.put("totalItems", students.getTotalElements());
        	responseEmpty.put("totalPages", students.getTotalPages());
        	
        	return responseEmpty;
        }
        
        List<Student> scharray = new ArrayList<Student>();
        
        scharray = students.getContent();
        
        Map<String, Object> response = new HashMap<>();
        response.put("students", scharray);
        response.put("currentPage", students.getNumber());
        response.put("totalItems", students.getTotalElements());
        response.put("totalPages", students.getTotalPages());
        response.put("isLast", students.isLast());
        
        return response;
    }
	
	public Student update(StudentRequest studentRequest,long id) {
		Optional<Student> existing = studentRepository.findById(id);
		if (existing.isPresent()) {
			Student pupval1 = existing.get();			
			CommonActivity.copyNonNullProperties(studentRequest, pupval1);
			return studentRepository.save(pupval1);
		}	   
		
		return null;
	}
	
	public Student delete(Long id) {
		Optional<Student> sch = studentRepository.findById(id);
		if (sch.isPresent()) {
			Student pupval = sch.get();
			pupval.setStatus(-1);
			studentRepository.save(pupval);
			return pupval;
		}
		return null;
	}
	

}
