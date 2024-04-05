package basepackage.stand.standbasisprojectonev1.service;

import java.util.HashSet;
import java.util.List;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import basepackage.stand.standbasisprojectonev1.model.DashboardAcademic;
import basepackage.stand.standbasisprojectonev1.model.DashboardCurriculum;
import basepackage.stand.standbasisprojectonev1.model.DashboardSsis;
import basepackage.stand.standbasisprojectonev1.model.DashboardAcademicInput;
import basepackage.stand.standbasisprojectonev1.model.DashboardTeacher;
import basepackage.stand.standbasisprojectonev1.model.DashboardTeacherInput;
import basepackage.stand.standbasisprojectonev1.model.Enrollment;
import basepackage.stand.standbasisprojectonev1.model.Lessonnote;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.DashboardAcademicRequest;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.DashboardAcademicInputRequest;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.DashboardCurriculumRequest;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.DashboardSsisRequest;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.DashboardTeacherInputRequest;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.DashboardTeacherRequest;
import basepackage.stand.standbasisprojectonev1.repository.DashboardARepository;
import basepackage.stand.standbasisprojectonev1.repository.DashboardCRepository;
import basepackage.stand.standbasisprojectonev1.repository.DashboardSRepository;
import basepackage.stand.standbasisprojectonev1.repository.DashboardTInputRepository;
import basepackage.stand.standbasisprojectonev1.repository.DashboardTRepository;
import basepackage.stand.standbasisprojectonev1.repository.SchoolRepository;
import basepackage.stand.standbasisprojectonev1.repository.DashboardAInputRepository;

@Service
public class DashboardService {

	@Autowired		
    private DashboardSRepository dashRepository;
	
	@Autowired		
    private DashboardTRepository dashTRepository;
	
	@Autowired		
    private DashboardCRepository dashCRepository;
	
	@Autowired		
    private DashboardARepository dashARepository;
	
	@Autowired		
    private DashboardAInputRepository dashAIRepository;
	
	@Autowired		
    private DashboardTInputRepository dashTIRepository;
	
	@Autowired		
    private SchoolRepository schRepository;
	
	public DashboardAcademicInput saveOne(DashboardAcademicInputRequest dashRequest) {
		 ModelMapper modelMapper    = new ModelMapper();   
		 DashboardAcademicInput val = modelMapper.map(dashRequest, DashboardAcademicInput.class);
		 School newschool = new School();
		 newschool.setSchId(dashRequest.getSch_id());
		 val.setSchool(newschool);
		 return dashAIRepository.save(val);		 
	}
	
	public DashboardTeacherInput saveOne(DashboardTeacherInputRequest dashRequest) {
		 ModelMapper modelMapper   = new ModelMapper();   
		 DashboardTeacherInput val = modelMapper.map(dashRequest, DashboardTeacherInput.class);
		 School newschool = new School();
		 newschool.setSchId(dashRequest.getSch_id());
		 val.setSchool(newschool);
		 return dashTIRepository.save(val);		 
	}

	public List<DashboardTeacherInput> saveAll( List<DashboardTeacherInput> dti  ) {		
		return dashTIRepository.saveAll(dti);
	}
	
	public List<DashboardAcademicInput> findAcademicExists(long id) {
		Optional<School> sch = schRepository.findById(id);
		if ( sch.isPresent() ) {
			School val = sch.get();
			List<DashboardAcademicInput> dalist = dashAIRepository.findBySchoolAcademicOnly(val); 
			return dalist;
		}
		return null;	
	}
	
	public List<DashboardTeacherInput> findTeacherExists(long id) {
		Optional<School> sch = schRepository.findById(id);
		if ( sch.isPresent() ) {
			School val = sch.get();
			List<DashboardTeacherInput> dalist = dashTIRepository.findBySchoolTeacherOnly(val); 
			return dalist;
		}
		return null;	
	}
	
	///////////////////////////////////////////////////////////
	
	public DashboardSsis findBySchoolS(long id, Integer year) {
		Optional<School> sch = schRepository.findById(id);
		Optional<DashboardSsis> das = dashRepository.findBySchoolSSIS(sch.get(), year);
		if (das.isPresent()) {
			DashboardSsis dasval = das.get();			
			return dasval;
		}
		return null;
	}
	public DashboardTeacher findBySchoolT(long id, Integer year) {
		Optional<School> sch = schRepository.findById(id);
		Optional<DashboardTeacher> das = dashTRepository.findBySchoolTeacher(sch.get(), year);
		if (das.isPresent()) {
			DashboardTeacher dasval = das.get();			
			return dasval;
		}
		return null;
	}
	
	public DashboardCurriculum findBySchoolC(long id, Integer year) {
		Optional<School> sch = schRepository.findById(id);
		Optional<DashboardCurriculum> das = dashCRepository.findBySchoolCurriculum(sch.get(), year);
		if (das.isPresent()) {
			DashboardCurriculum dasval = das.get();			
			return dasval;
		}
		return null;
	}
	
	public DashboardAcademic findBySchoolA(long id, Integer year) {
		Optional<School> sch = schRepository.findById(id);
		Optional<DashboardAcademic> das = dashARepository.findBySchoolAcademic(sch.get(), year);
		if (das.isPresent()) {
			DashboardAcademic dasval = das.get();			
			return dasval;
		}
		return null;
	}
	
	////////////////////////////////////////////////////////////////////////
	
	public DashboardSsis updateS(DashboardSsisRequest dashRequest,long id) {
		Optional<DashboardSsis> existing = dashRepository.findBySsisId(id);
		if (existing.isPresent()) {
			DashboardSsis dashval = existing.get();
			
			copyNonNullProperties(dashRequest, dashval);			
			
			return dashRepository.save(dashval);
		}   
		
		return null;
	}
	
	public DashboardTeacher updateT(DashboardTeacherRequest dashRequest,long id) {
		Optional<DashboardTeacher> existing = dashTRepository.findByTeacherId(id);
		if (existing.isPresent()) {
			DashboardTeacher dashval = existing.get();
			
			copyNonNullProperties(dashRequest, dashval);			
			
			return dashTRepository.save(dashval);
		}   
		
		return null;
	}
	
	public DashboardCurriculum updateC(DashboardCurriculumRequest dashRequest,long id) {
		Optional<DashboardCurriculum> existing = dashCRepository.findByCurriculumId(id);
		if (existing.isPresent()) {
			DashboardCurriculum dashval = existing.get();
			
			copyNonNullProperties(dashRequest, dashval);			
			
			return dashCRepository.save(dashval);
		}   
		
		return null;
	}
	
	public DashboardAcademic updateA(DashboardAcademicRequest dashRequest,long id) {
		Optional<DashboardAcademic> existing = dashARepository.findByAcademicId(id);
		if (existing.isPresent()) {
			DashboardAcademic dashval = existing.get();
			
			copyNonNullProperties(dashRequest, dashval);			
			
			return dashARepository.save(dashval);
		}   
		
		return null;
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
}
