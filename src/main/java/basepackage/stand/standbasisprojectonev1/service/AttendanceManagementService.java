package basepackage.stand.standbasisprojectonev1.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.modelmapper.ModelMapper;
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
import basepackage.stand.standbasisprojectonev1.model.AttendanceManagement;
import basepackage.stand.standbasisprojectonev1.model.Calendar;
import basepackage.stand.standbasisprojectonev1.model.ClassStream;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;
import basepackage.stand.standbasisprojectonev1.model.Subject;
import basepackage.stand.standbasisprojectonev1.model.Teacher;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.AttendanceManagementRequest;
import basepackage.stand.standbasisprojectonev1.repository.AttendanceManagementRepository;
import basepackage.stand.standbasisprojectonev1.repository.AttendanceRepository;
import basepackage.stand.standbasisprojectonev1.repository.CalendarRepository;
import basepackage.stand.standbasisprojectonev1.repository.ClassStreamRepository;
import basepackage.stand.standbasisprojectonev1.repository.SchoolRepository;
import basepackage.stand.standbasisprojectonev1.repository.SchoolgroupRepository;
import basepackage.stand.standbasisprojectonev1.repository.SubjectRepository;
import basepackage.stand.standbasisprojectonev1.repository.TeacherRepository;
import basepackage.stand.standbasisprojectonev1.util.AppConstants;

@Service
public class AttendanceManagementService {

	@Autowired		
    private AttendanceManagementRepository attmanageRepository;	
	
	@Autowired
    private SchoolgroupRepository groupRepository;
	
	@Autowired		
    private SchoolRepository schRepository;
	
	@Autowired		
    private ClassStreamRepository clsRepository;
	
	@Autowired		
    private TeacherRepository teaRepository;
	
	@Autowired		
    private CalendarRepository calRepository;
	
	@Autowired		
    private AttendanceRepository attRepository;
	
	@Autowired		
    private SubjectRepository subRepository;

	public List<AttendanceManagement> findAll() {		
		return attmanageRepository.findAll();
	}
	
	public AttendanceManagement saveOne(AttendanceManagementRequest attactRequest, Attendance att) {
		 ModelMapper modelMapper = new ModelMapper();   
		 AttendanceManagement val = modelMapper.map(attactRequest, AttendanceManagement.class);
		 val.setAtt_id(att);			
		 return attmanageRepository.save(val); 
	}
	
	public AttendanceManagement findAttendanceManagement(Long id) {		
		Optional<AttendanceManagement> att = attmanageRepository.findById(id);
		if (att.isPresent()) {
			AttendanceManagement attval = att.get();			
			return attval;
		}
		return null;
	}

	public AttendanceManagement findAttendanceManagementByAttendance(Long id) {		
		Optional<AttendanceManagement> att = attmanageRepository.findByAttendance(id);
		if (att.isPresent()) {
			AttendanceManagement attval = att.get();			
			return attval;
		}
		return null;
	}
	
	public AttendanceManagement update(AttendanceManagementRequest attRequest, long id) {
		Optional<AttendanceManagement> existing = attmanageRepository.findById(id);
		if (existing.isPresent()) {
			AttendanceManagement attval = existing.get();
			copyNonNullProperties(attRequest, attval);
			return attmanageRepository.save(attval);
		} 
		return null;
	}
	
	public AttendanceManagement updateByAttendance( AttendanceManagementRequest attRequest, long id) {
		//Optional<Attendance> existingAtt = attRepository.findById(id);
			Optional<AttendanceManagement> existing = attmanageRepository.findByAttendance( id );
			if (existing.isPresent()) {
				AttendanceManagement attval = existing.get();
				copyNonNullProperties(attRequest, attval);
				return attmanageRepository.save(attval);
			} 
			return null;
	}
	
	public Map<String, Object> getPaginatedTeacherAttendances(int page, int size, String query, Optional<Long> schgroupId, Optional<Long> schId, Optional<Long> classId, Optional<Long> calendarId, Optional<Long> teacherId, Optional<Timestamp> datefrom, Optional<Timestamp> dateto ) {
        validatePageNumberAndSize(page, size);
        
        Long schgroup = schgroupId.orElse(null);
        Long schowner = schId.orElse(null);
        Long classowner = classId.orElse(null);
        Long teacherowner = teacherId.orElse(null);
        Long calendarowner = calendarId.orElse(null);
        
        // Retrieve Attendances
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<AttendanceManagement> attendances = null;
        
        if ( query == null || query.equals("") ) {
        	if ( schgroup == null ) {
        		attendances = attmanageRepository.findAll(pageable);
        	}
        	else {
        		Optional<SchoolGroup> schgroupobj = groupRepository.findById( schgroup );
        		Optional<School> schownerobj = null;
        		Optional<ClassStream> classownerobj = null ;
        		Optional<Teacher> teacherownerobj = null;
        		Optional<Calendar> calendarownerobj = null;
        		
        		if(schowner != null) { schownerobj = schRepository.findById( schowner );  } 
        		if(teacherowner != null) { teacherownerobj = teaRepository.findById( teacherowner );  } 
        		if(classowner != null) { classownerobj = clsRepository.findById( classowner );  } 
        		if(calendarowner != null) { calendarownerobj = calRepository.findById( calendarowner );  } 
        		
        		attendances = attmanageRepository.findByTeacherSchoolgroupPage( 
        						schgroupobj == null ? null : schgroupobj.get(), 
                				schownerobj == null ? null : schownerobj.get() , 
                				classownerobj == null ? null : classownerobj.get(), 
                				teacherownerobj == null ? null : teacherownerobj.get(),
                				calendarownerobj == null ? null : calendarownerobj.get(),
                				datefrom.isEmpty() ? null : datefrom.get(),
                				dateto.isEmpty() ? null : dateto.get(),       				
                				pageable
        		);
        	}        	
        }
        else {
        	if ( schgroup == null ) {
        		//attendances = attmanageRepository.filter("%"+ query + "%",  pageable);
        		attendances = attmanageRepository.findAll(pageable);
        	}
        	else {    
        		Optional<SchoolGroup> schgroupobj = groupRepository.findById( schgroup );
        		Optional<School> schownerobj = null;
        		Optional<ClassStream> classownerobj = null;
        		Optional<Teacher> teacherownerobj = null;
        		Optional<Calendar> calendarownerobj = null;
        		
        		if(schowner != null) { schownerobj = schRepository.findById( schowner );  } 
        		if(teacherowner != null) { teacherownerobj = teaRepository.findById( teacherowner );  } 
        		if(classowner != null) { classownerobj = clsRepository.findById( classowner );  }
        		if(calendarowner != null) { calendarownerobj = calRepository.findById( calendarowner );  } 
        		
        		attendances = attmanageRepository.findFilterByTeacherSchoolgroupPage( 
        				"%"+ query + "%", 
        				schgroupobj == null ? null : schgroupobj.get(), 
                		schownerobj == null ? null : schownerobj.get() , 
                		classownerobj == null ? null : classownerobj.get(), 
                		teacherownerobj == null ? null : teacherownerobj.get(),
                		calendarownerobj == null ? null : calendarownerobj.get(),
                		datefrom.isEmpty() ? null : datefrom.get(),
                		dateto.isEmpty() ? null : dateto.get(), 
        				pageable
        		);
        	}
        }

        if(attendances.getNumberOfElements() == 0) {
        	Map<String, Object> responseEmpty = new HashMap<>();
        	responseEmpty.put("attendances", Collections.emptyList());
        	responseEmpty.put("currentPage", attendances.getNumber());
        	responseEmpty.put("totalItems", attendances.getTotalElements());
        	responseEmpty.put("totalPages", attendances.getTotalPages());        	
        	return responseEmpty;
        }
        
        List<AttendanceManagement> calarray = new ArrayList<AttendanceManagement>();
        
        calarray = attendances.getContent();
        
        Map<String, Object> response = new HashMap<>();
        response.put("attendancemanagement", calarray);
       
        return response;
    }
	
	public Map<String, Object> getOrdinaryTeacherAttendances(String query, Optional<Long> schgroupId, Optional<Long> schId, Optional<Long> classId, Optional<Long> calendarId, Optional<Long> teacherId, Optional<Long> subject, Optional<Timestamp> datefrom, Optional<Timestamp> dateto  ) {
        
        Long schgroup = schgroupId.orElse(null);
        Long schowner = schId.orElse(null);
        Long classowner = classId.orElse(null);
        Long teacherowner = teacherId.orElse(null);     
        Long calendarowner = calendarId.orElse(null);
        Long subjectowner = subject.orElse(null);
        
        List<AttendanceManagement> attendances = null;
        
        if ( query == null || query.equals("") ) {
        	if ( schgroup == null ) {
        		attendances = attmanageRepository.findAll();
        	}
        	else {
        		Optional<SchoolGroup> schgroupobj = groupRepository.findById( schgroup );
        		Optional<School> schownerobj = null;
        		Optional<ClassStream> classownerobj = null ;
        		Optional<Teacher> teacherownerobj = null;
        		Optional<Calendar> calendarownerobj = null;
        		Optional<Subject> subjectownerobj = null;
        		
        		if(schowner != null) { schownerobj = schRepository.findById( schowner );  } 
        		if(teacherowner != null) { teacherownerobj = teaRepository.findById( teacherowner );  } 
        		if(classowner != null) { classownerobj = clsRepository.findById( classowner );  } 
        		if(calendarowner != null) { calendarownerobj = calRepository.findById( calendarowner );  } 
        		if(subjectowner != null) { subjectownerobj = subRepository.findById( subjectowner );  }
        		
        		attendances = attmanageRepository.findByTeacherSchoolgroup( 
        				schgroupobj == null ? null : schgroupobj.get(), 
                        schownerobj == null ? null : schownerobj.get() , 
                        classownerobj == null ? null : classownerobj.get(), 
                        teacherownerobj == null ? null : teacherownerobj.get(),
                        calendarownerobj == null ? null : calendarownerobj.get(),
                        subjectownerobj == null ? null : subjectownerobj.get(),
                        datefrom.isEmpty() ? null : datefrom.get(),
                        dateto.isEmpty() ? null : dateto.get()
        		);
        	}        	
        }
        else {
        	if ( schgroup == null ) {
        		attendances = attmanageRepository.findAll();
        	}
        	else {    
        		Optional<SchoolGroup> schgroupobj = groupRepository.findById( schgroup );
        		Optional<School> schownerobj = null;
        		Optional<ClassStream> classownerobj = null;
        		Optional<Teacher> teacherownerobj = null;
        		Optional<Calendar> calendarownerobj = null;
        		Optional<Subject> subjectownerobj = null;
        		
        		if(schowner != null) { schownerobj = schRepository.findById( schowner );  } 
        		if(teacherowner != null) { teacherownerobj = teaRepository.findById( teacherowner );  } 
        		if(classowner != null) { classownerobj = clsRepository.findById( classowner );  }
        		if(calendarowner != null) { calendarownerobj = calRepository.findById( calendarowner );  } 
        		if(subjectowner != null) { subjectownerobj = subRepository.findById( subjectowner );  }
        		
        		
        		attendances = attmanageRepository.findFilterByTeacherSchoolgroup( 
        				"%"+ query + "%", 
        				schgroupobj == null ? null : schgroupobj.get(), 
                        schownerobj == null ? null : schownerobj.get() , 
                        classownerobj == null ? null : classownerobj.get(), 
                        teacherownerobj == null ? null : teacherownerobj.get(),
                        calendarownerobj == null ? null : calendarownerobj.get(),
                        subjectownerobj == null ? null : subjectownerobj.get(),		
                        datefrom.isEmpty() ? null : datefrom.get(),
                        dateto.isEmpty() ? null : dateto.get()
        		);
        	}
        }

        if(attendances.size() == 0) {
        	Map<String, Object> responseEmpty = new HashMap<>();
        	responseEmpty.put("attendancemanagement", Collections.emptyList());
        	        	
        	return responseEmpty;
        }
        
        List<AttendanceManagement> calarray = new ArrayList<AttendanceManagement>(attendances);
        
        Map<String, Object> response = new HashMap<>();
        response.put("attendancemanagement", calarray);
        
        
        return response;
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
