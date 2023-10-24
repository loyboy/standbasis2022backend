package basepackage.stand.standbasisprojectonev1.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import basepackage.stand.standbasisprojectonev1.model.Calendar;
import basepackage.stand.standbasisprojectonev1.model.DashboardAcademic;
import basepackage.stand.standbasisprojectonev1.model.DashboardCurriculum;
import basepackage.stand.standbasisprojectonev1.model.DashboardSsis;
import basepackage.stand.standbasisprojectonev1.model.DashboardTeacher;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.CalendarRequest;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.DashboardAcademicRequest;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.DashboardCurriculumRequest;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.DashboardSsisRequest;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.DashboardTeacherRequest;
import basepackage.stand.standbasisprojectonev1.repository.DashboardRepository;
import basepackage.stand.standbasisprojectonev1.repository.SchoolRepository;
import basepackage.stand.standbasisprojectonev1.util.CommonActivity;

@Service
public class DashboardService {

	@Autowired		
    private DashboardRepository dashRepository;
	
	@Autowired		
    private SchoolRepository schRepository;
	
	public DashboardSsis findBySchoolS(long id) {
		Optional<School> sch = schRepository.findById(id);
		Optional<DashboardSsis> das = dashRepository.findBySchoolSSIS(sch.get());
		if (das.isPresent()) {
			DashboardSsis dasval = das.get();			
			return dasval;
		}
		return null;
	}
	public DashboardTeacher findBySchoolT(long id) {
		Optional<School> sch = schRepository.findById(id);
		Optional<DashboardTeacher> das = dashRepository.findBySchoolTeacher(sch.get());
		if (das.isPresent()) {
			DashboardTeacher dasval = das.get();			
			return dasval;
		}
		return null;
	}
	
	public DashboardCurriculum findBySchoolC(long id) {
		Optional<School> sch = schRepository.findById(id);
		Optional<DashboardCurriculum> das = dashRepository.findBySchoolCurriculum(sch.get());
		if (das.isPresent()) {
			DashboardCurriculum dasval = das.get();			
			return dasval;
		}
		return null;
	}
	
	public DashboardAcademic findBySchoolA(long id) {
		Optional<School> sch = schRepository.findById(id);
		Optional<DashboardAcademic> das = dashRepository.findBySchoolAcademic(sch.get());
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
		Optional<DashboardTeacher> existing = dashRepository.findByTeacherId(id);
		if (existing.isPresent()) {
			DashboardTeacher dashval = existing.get();
			
			copyNonNullProperties(dashRequest, dashval);			
			
			return dashRepository.save(dashval);
		}   
		
		return null;
	}
	
	public DashboardCurriculum updateC(DashboardCurriculumRequest dashRequest,long id) {
		Optional<DashboardCurriculum> existing = dashRepository.findByCurriculumId(id);
		if (existing.isPresent()) {
			DashboardCurriculum dashval = existing.get();
			
			copyNonNullProperties(dashRequest, dashval);			
			
			return dashRepository.save(dashval);
		}   
		
		return null;
	}
	
	public DashboardAcademic updateA(DashboardAcademicRequest dashRequest,long id) {
		Optional<DashboardAcademic> existing = dashRepository.findByAcademicId(id);
		if (existing.isPresent()) {
			DashboardAcademic dashval = existing.get();
			
			copyNonNullProperties(dashRequest, dashval);			
			
			return dashRepository.save(dashval);
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
