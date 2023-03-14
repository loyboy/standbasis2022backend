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
import basepackage.stand.standbasisprojectonev1.model.Calendar;
import basepackage.stand.standbasisprojectonev1.model.ClassStream;
import basepackage.stand.standbasisprojectonev1.model.Enrollment;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;
import basepackage.stand.standbasisprojectonev1.model.Student;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.EnrollmentRequest;
import basepackage.stand.standbasisprojectonev1.repository.SchoolRepository;
import basepackage.stand.standbasisprojectonev1.repository.SchoolgroupRepository;
import basepackage.stand.standbasisprojectonev1.repository.StudentRepository;
import basepackage.stand.standbasisprojectonev1.repository.CalendarRepository;
import basepackage.stand.standbasisprojectonev1.repository.ClassStreamRepository;
import basepackage.stand.standbasisprojectonev1.repository.EnrollmentRepository;
import basepackage.stand.standbasisprojectonev1.util.AppConstants;
import basepackage.stand.standbasisprojectonev1.util.CommonActivity;

@Service
public class EnrollmentService {
	private static final Logger logger = LoggerFactory.getLogger(EnrollmentService.class);
	
	@Autowired		
    private EnrollmentRepository enrollRepository;
	
	@Autowired		
    private CalendarRepository attRepository;
	
	@Autowired		
    private StudentRepository stuRepository;
	
	@Autowired		
    private ClassStreamRepository clsRepository;
	
	@Autowired		
    private SchoolRepository schRepository;
	
	@Autowired
    private SchoolgroupRepository schgroupRepository;
	
	
	public List<Enrollment> findAll() {		
		return enrollRepository.findAll();
	}
	
	public Enrollment findEnrollment(long id) {		
		Optional<Enrollment> enc = enrollRepository.findById(id);
		if (enc.isPresent()) {
			Enrollment enrolval = enc.get();			
			return enrolval;
		}
		return null;
	}
	
	public List<Enrollment> findEnrollmentFromClassIndex(Integer id) {		
		List<Enrollment> enc = enrollRepository.findByClassIndex(id);
		
		return enc;
	}	
	
	public List<Enrollment> findEnrollmentFromClass(Integer id) {		
		List<Enrollment> enc = enrollRepository.findByClassId(id);
		
		return enc;
	}
	
	public Map<String, Object> getPaginatedEnrollments(int page, int size, String query, Optional<Long> ownerval, Optional<Long> groupval) {
        CommonActivity.validatePageNumberAndSize(page, size);
        
        Long owner = ownerval.orElse(null);  //School      
        Long group = groupval.orElse(null);
        // Retrieve Enrollments
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Enrollment> schs = null;
        
        if ( query.equals("") || query == null ) {
        	if ( owner == null && group == null ) {
        		schs = enrollRepository.findAll(pageable);
        	}
        	else {
        		
        		Optional<School> schownerobj = null;
        		Optional<SchoolGroup> schgroupobj = null ;
        		
        		if(owner != null) { schownerobj = schRepository.findById( owner );  }        		
        		if(group != null) { schgroupobj = schgroupRepository.findById( group );  }
        		
        		schs = enrollRepository.findBySchool( 
        				schownerobj == null ? null : schownerobj.get(), 
        				schgroupobj == null ? null : schgroupobj.get(), 		
                		pageable
        		);
        	}        	
        }
        else {
        	if ( owner == null && group == null ) {
        		schs = enrollRepository.filter("%"+ query + "%",  pageable);
        	}
        	else {    
        		
        		Optional<School> schownerobj = null;
        		Optional<SchoolGroup> schgroupobj = null ;
        		
        		if(owner != null) { schownerobj = schRepository.findById( owner );  }        		
        		if(group != null) { schgroupobj = schgroupRepository.findById( group );  }
        		
        		schs = enrollRepository.findFilterBySchool(
        				"%"+ query + "%", 
        				schownerobj == null ? null : schownerobj.get(), 
                		schgroupobj == null ? null : schgroupobj.get(),
        				pageable
        		);
        	}
        }

        if(schs.getNumberOfElements() == 0) {
        	Map<String, Object> responseEmpty = new HashMap<>();
        	responseEmpty.put("enrollments", Collections.emptyList());
        	responseEmpty.put("currentPage", schs.getNumber());
        	responseEmpty.put("totalItems", schs.getTotalElements());
        	responseEmpty.put("totalPages", schs.getTotalPages());
        	
        	return responseEmpty;
        }
        
        List<Enrollment> enrolarray = new ArrayList<Enrollment>();
        
        enrolarray = schs.getContent();
        
        Map<String, Object> response = new HashMap<>();
        response.put("enrollments", enrolarray);
        response.put("currentPage", schs.getNumber());
        response.put("totalItems", schs.getTotalElements());
        response.put("totalPages", schs.getTotalPages());
        response.put("isLast", schs.isLast());
         
        long activeEnrollments = enrolarray.stream().filter(sch -> sch.getStatus() == 1).count();       
        long inactiveEnrollments = enrolarray.stream().filter(sch -> sch.getStatus() == 0).count();
        
        response.put("totalActive", activeEnrollments);
        response.put("totalInactive", inactiveEnrollments);
        return response;
    }
	
	public Enrollment update(EnrollmentRequest enrollRequest,long id) {
		Optional<Enrollment> existing = enrollRepository.findById(id);
		if (existing.isPresent()) {
			Enrollment enrollval = existing.get();
			if (enrollRequest.getStudent() != null) {
				Optional<Student> existingStudent = stuRepository.findById(enrollRequest.getStudent());
				enrollval.setStudent(existingStudent.get());
			}
			if (enrollRequest.getClassstream() != null) {
				Optional<ClassStream> existingClass = clsRepository.findById(enrollRequest.getClassstream());
				enrollval.setClassstream(existingClass.get());
			}
			if (enrollRequest.getCalendar() != null) {
				Optional<Calendar> existingCalendar = attRepository.findById(enrollRequest.getCalendar());
				enrollval.setCalendar(existingCalendar.get());
			}
			if (enrollRequest.getStatus() != null) {
				System.out.println("Status is changed");
				Optional<Student> existingStudentTwo = stuRepository.findById(enrollRequest.getStudent());
				Student st = existingStudentTwo.get();
				st.setStatus(enrollRequest.getStatus());
				stuRepository.save(st);
				enrollval.setStatus(enrollRequest.getStatus());
			}			
			
			
			return enrollRepository.save(enrollval);
		}	   
		
		return null;
	}
	
	public Enrollment delete(Long id) {
		Optional<Enrollment> tea = enrollRepository.findById(id);
		if (tea.isPresent()) {
			Enrollment enrollval = tea.get();
			enrollval.setStatus(-1);
			enrollRepository.save(enrollval);
			return enrollval;
		}
		return null;
	}
	
	
}
