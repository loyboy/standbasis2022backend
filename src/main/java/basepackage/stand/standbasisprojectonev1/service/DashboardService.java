package basepackage.stand.standbasisprojectonev1.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.stereotype.Service;

import basepackage.stand.standbasisprojectonev1.model.DashboardAcademic;
import basepackage.stand.standbasisprojectonev1.model.DashboardCurriculum;
import basepackage.stand.standbasisprojectonev1.model.DashboardRatingInput;
import basepackage.stand.standbasisprojectonev1.model.DashboardSsis;
import basepackage.stand.standbasisprojectonev1.model.DashboardAcademicInput;
import basepackage.stand.standbasisprojectonev1.model.DashboardTeacher;
import basepackage.stand.standbasisprojectonev1.model.DashboardTeacherInput;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.DashboardAcademicRequest;
import basepackage.stand.standbasisprojectonev1.payload.ApiDashboardGraphBar;
import basepackage.stand.standbasisprojectonev1.payload.ApiDashboardGraphLine;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.DashboardAcademicInputRequest;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.DashboardCurriculumRequest;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.DashboardSsisRequest;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.DashboardTeacherInputRequest;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.DashboardTeacherRequest;
import basepackage.stand.standbasisprojectonev1.repository.DashboardARepository;
import basepackage.stand.standbasisprojectonev1.repository.DashboardCRepository;
import basepackage.stand.standbasisprojectonev1.repository.DashboardRInputRepository;
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
    private DashboardRInputRepository dashRIRepository;
	
	@Autowired		
    private SchoolRepository schRepository;

	private Map<String, Integer> mapTeacherRatings = new HashMap<>();
	
	
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
	
	//////////////////////////////////////////////////////////////////

	/*private Map<String, Double> findBySchoolAIGrade(long id, Integer year) {
		Optional<School> sch = schRepository.findById(id);
		Optional<DashboardAcademicInput> das = dashAIRepository.findBySchoolAcademic(sch.get(), year);
		if (das.isPresent()) {
			DashboardAcademicInput dasval = das.get();
			Double grade_a = Double.valueOf( dasval.getA1_grade_count()	/ dasval.getEnrollment_count() );
			Double grade_b = Double.valueOf( ( dasval.getB2_grade_count() + dasval.getB3_grade_count() ) / dasval.getEnrollment_count() );	
			Double grade_c = Double.valueOf( ( dasval.getC4_grade_count() + dasval.getC5_grade_count() + dasval.getC6_grade_count() ) / dasval.getEnrollment_count() );	
			Double grade_d = Double.valueOf( ( dasval.getD7_grade_count() + dasval.getE8_grade_count() + dasval.getF9_grade_count() ) / dasval.getEnrollment_count() );	
			Double transition_index = Double.valueOf( dasval.getHit_pass_count() / dasval.getEnrollee_count() );
			Double drag_index = Double.valueOf( ( dasval.getD7_grade_count() + dasval.getE8_grade_count() + dasval.getF9_grade_count() ) / dasval.getEnrollment_count() );

			Map<String, Double> response = new HashMap<>();
        	response.put("grade_a", Double.parseDouble(String.format("%.2f", grade_a)));	
			response.put("grade_b", Double.parseDouble(String.format("%.2f", grade_b)));	
			response.put("grade_c", Double.parseDouble(String.format("%.2f", grade_c)));	
			response.put("grade_d", Double.parseDouble(String.format("%.2f", grade_d)));
			response.put("transition_index", Double.parseDouble(String.format("%.2f", transition_index)));
			response.put("drag_index", Double.parseDouble(String.format("%.2f", drag_index)));
			
			return response;
		}
		return null;
	}*/

	public Map<String, Object> calculateAIAcademicBar(long sch_id, Integer year) {
		
			List<ApiDashboardGraphBar> dataSets;
			dataSets = new ArrayList<>();
			List<String> categoryList = new ArrayList<>();
			List<Double> gradeValueList = new ArrayList<>();
			List<Double> transitionIndexValueList = new ArrayList<>();
			List<Double> dragIndexValueList = new ArrayList<>();

			Optional<School> sch = schRepository.findById(sch_id);
			if ( sch.isPresent() ) {
				School val_sch = sch.get();				
				List<DashboardAcademicInput> dalist = dashAIRepository.findBySchoolAndYearAcademic(val_sch, year);
					if (dalist.size() > 0) {
						for (DashboardAcademicInput dai: dalist){
							categoryList.add(dai.get_type().replaceAll("_(.)", " $1"));
							Double grade_a = Double.valueOf( dai.getA1_grade_count()	/ dai.getEnrollment_count() );
							Double grade_b = Double.valueOf( ( dai.getB2_grade_count() + dai.getB3_grade_count() ) / dai.getEnrollment_count() );	
							Double grade_c = Double.valueOf( ( dai.getC4_grade_count() + dai.getC5_grade_count() + dai.getC6_grade_count() ) / dai.getEnrollment_count() );	
							Double grade_d = Double.valueOf( ( dai.getD7_grade_count() + dai.getE8_grade_count() + dai.getF9_grade_count() ) / dai.getEnrollment_count() );	
							Double transition_index = Double.valueOf( dai.getHit_pass_count() / dai.getEnrollee_count() );
							Double drag_index = Double.valueOf( ( dai.getD7_grade_count() + dai.getE8_grade_count() + dai.getF9_grade_count() ) / dai.getEnrollment_count() );

							if ( (grade_a > grade_b) && (grade_a > grade_c) && (grade_a > grade_d) ){
								gradeValueList.add(grade_a * 10);
							}
							else if ( (grade_b > grade_c) && (grade_b > grade_d) ){
								gradeValueList.add(grade_b * 10);
							}
							else if ( (grade_c > grade_d) ){
								gradeValueList.add(grade_c * 10);
							}
							else{
								gradeValueList.add(grade_d * 10);
							}	
							
							transitionIndexValueList.add(transition_index);
							dragIndexValueList.add(drag_index);
						}
						ApiDashboardGraphBar appbar  = new ApiDashboardGraphBar( "School Grade", gradeValueList );
						ApiDashboardGraphBar appbar2 = new ApiDashboardGraphBar( "Transition Index", transitionIndexValueList );
						ApiDashboardGraphBar appbar3 = new ApiDashboardGraphBar( "Drag Index", dragIndexValueList );
						dataSets.add(appbar);
						dataSets.add(appbar2);
						dataSets.add(appbar3);						
					}				
				
					Map<String, Object> response = new HashMap<>();
				
					response.put("categories", categoryList);
					response.put("series", dataSets);
					
					return response;			
			}
			return null;
	}

	public Map<String, Object> calculateAIAcademicLine(long sch_id, Integer year) {
		List<List<Object>> schoolGradeData = new ArrayList<>();
		List<List<Object>> transitionIndexData = new ArrayList<>();
		List<List<Object>> dragIndexData = new ArrayList<>();
		List<String> categoryList = new ArrayList<>();
	
		Optional<School> sch = schRepository.findById(sch_id);
		if (sch.isPresent()) {
			School val_sch = sch.get();
			List<DashboardAcademicInput> dalist = dashAIRepository.findBySchoolAndYearAcademic(val_sch, year);
			if (dalist.size() > 0) {
				for (DashboardAcademicInput dai : dalist) {
					String category = dai.get_type().replaceAll("_(.)", " $1");
					categoryList.add(category);
	
					Double grade_a = Double.valueOf(dai.getA1_grade_count() / dai.getEnrollment_count());
					Double grade_b = Double.valueOf((dai.getB2_grade_count() + dai.getB3_grade_count()) / dai.getEnrollment_count());
					Double grade_c = Double.valueOf((dai.getC4_grade_count() + dai.getC5_grade_count() + dai.getC6_grade_count()) / dai.getEnrollment_count());
					Double grade_d = Double.valueOf((dai.getD7_grade_count() + dai.getE8_grade_count() + dai.getF9_grade_count()) / dai.getEnrollment_count());
					Double schoolGrade;
					if ((grade_a > grade_b) && (grade_a > grade_c) && (grade_a > grade_d)) {
						schoolGrade = grade_a * 10;
					} else if ((grade_b > grade_c) && (grade_b > grade_d)) {
						schoolGrade = grade_b * 10;
					} else if (grade_c > grade_d) {
						schoolGrade = grade_c * 10;
					} else {
						schoolGrade = grade_d * 10;
					}
					schoolGradeData.add(Arrays.asList(category, schoolGrade));
	
					Double transitionIndex = Double.valueOf(dai.getHit_pass_count() / dai.getEnrollee_count());
					transitionIndexData.add(Arrays.asList(category, transitionIndex));
	
					Double dragIndex = Double.valueOf((dai.getD7_grade_count() + dai.getE8_grade_count() + dai.getF9_grade_count()) / dai.getEnrollment_count());
					dragIndexData.add(Arrays.asList(category, dragIndex));
				}
			}
		}
	
		Map<String, Object> response = new HashMap<>();
		response.put("categories", categoryList);
		response.put("series", Arrays.asList(
				new HashMap<String, Object>() {{
					put("name", "School Grade");
					put("data", schoolGradeData);
				}},
				new HashMap<String, Object>() {{
					put("name", "Transition Index");
					put("data", transitionIndexData);
				}},
				new HashMap<String, Object>() {{
					put("name", "Drag Index");
					put("data", dragIndexData);
				}}
		));
	
		return response;
	}

	public Map<String, Object> calculateIRatingBar(long sch_id) {
		
		List<ApiDashboardGraphLine> dataSets;
		dataSets = new ArrayList<>();
		List<String> categoryList = new ArrayList<>();
		List<BigDecimal> teachingProcessesValueList = new ArrayList<>();
		List<BigDecimal> teachingResourcesValueList = new ArrayList<>();
		List<BigDecimal> learningEnvValueList = new ArrayList<>();
		List<BigDecimal> sustainValueList = new ArrayList<>();
		List<BigDecimal> studentDevValueList = new ArrayList<>();
		List<BigDecimal> academicPerfValueList = new ArrayList<>();
		List<BigDecimal> ssheValueList = new ArrayList<>();

		Optional<School> sch = schRepository.findById(sch_id);
		if ( sch.isPresent() ) {
			School val_sch = sch.get();				
			List<DashboardRatingInput> dalist = dashRIRepository.findBySchoolAndYearAcademic(val_sch, null);
				if (dalist.size() > 0) {
					for (DashboardRatingInput dai: dalist){
						categoryList.add(String.valueOf(dai.get_year()));							
						teachingProcessesValueList.add(dai.getTeaching_processes());
						teachingResourcesValueList.add(dai.getTeacher_resources());
						learningEnvValueList.add(dai.getLearning_environment());
						sustainValueList.add(dai.getSustainability());
						studentDevValueList.add(dai.getStudent_development());
						academicPerfValueList.add(dai.getAcademic_performance());
						ssheValueList.add(dai.getSshe());
					}
					ApiDashboardGraphLine appbar  = new ApiDashboardGraphLine( "Teaching Processes", teachingProcessesValueList );
					ApiDashboardGraphLine appbar2 = new ApiDashboardGraphLine( "Teacher Resources", teachingResourcesValueList );
					ApiDashboardGraphLine appbar3 = new ApiDashboardGraphLine( "Learning Environment", learningEnvValueList );
					ApiDashboardGraphLine appbar4 = new ApiDashboardGraphLine( "Sustainability", sustainValueList );
					ApiDashboardGraphLine appbar5 = new ApiDashboardGraphLine( "Student Development", studentDevValueList );
					ApiDashboardGraphLine appbar6 = new ApiDashboardGraphLine( "Academic Performance", academicPerfValueList );
					ApiDashboardGraphLine appbar7 = new ApiDashboardGraphLine( "SSHE", ssheValueList );
					dataSets.add(appbar);
					dataSets.add(appbar2);
					dataSets.add(appbar3);
					dataSets.add(appbar4);	
					dataSets.add(appbar5);
					dataSets.add(appbar6);
					dataSets.add(appbar7);					
				}				
			
				Map<String, Object> response = new HashMap<>();
			
				response.put("categories", categoryList);
				response.put("series", dataSets);
				
				return response;			
		}
		return null;
	}

	public Map<String, Object> calculateIRatingLine(long sch_id) {
		List<List<Object>> tprocessData = new ArrayList<>();
		List<List<Object>> tresourcesData = new ArrayList<>();
		List<List<Object>> learningData = new ArrayList<>();
		List<List<Object>> sustainData = new ArrayList<>();
		List<List<Object>> studentData = new ArrayList<>();
		List<List<Object>> academicData = new ArrayList<>();
		List<List<Object>> ssheData = new ArrayList<>();
		List<String> categoryList = new ArrayList<>();
	
		Optional<School> sch = schRepository.findById(sch_id);
		if (sch.isPresent()) {
			School val_sch = sch.get();
			List<DashboardRatingInput> dalist = dashRIRepository.findBySchoolAndYearAcademic(val_sch, null);
			if (dalist.size() > 0) {
				for (DashboardRatingInput dai : dalist) {
					String category = String.valueOf(dai.get_year());
					categoryList.add(category);
					tprocessData.add(Arrays.asList(category,   dai.getTeaching_processes()));
					tresourcesData.add(Arrays.asList(category, dai.getTeacher_resources() ));
					learningData.add(Arrays.asList(category,   dai.getLearning_environment() ));
					sustainData.add(Arrays.asList(category,    dai.getSustainability() ));
					studentData.add(Arrays.asList(category,    dai.getStudent_development() ));
					academicData.add(Arrays.asList(category,   dai.getAcademic_performance() ));
					ssheData.add(Arrays.asList(category,       dai.getSshe() ));
				}
			}
		}
	
		Map<String, Object> response = new HashMap<>();
		response.put("categories", categoryList);
		response.put("series", Arrays.asList(
				new HashMap<String, Object>() {{
					put("name", "Teaching Processes");
					put("data", tprocessData);
				}},
				new HashMap<String, Object>() {{
					put("name", "Teaching Resources");
					put("data", tresourcesData);
				}},
				new HashMap<String, Object>() {{
					put("name", "Learning Environment");
					put("data", learningData);
				}},
				new HashMap<String, Object>() {{
					put("name", "Sustainability");
					put("data", sustainData);
				}},
				new HashMap<String, Object>() {{
					put("name", "Student Development");
					put("data", studentData);
				}},
				new HashMap<String, Object>() {{
					put("name", "Academic Development");
					put("data", academicData);
				}},
				new HashMap<String, Object>() {{
					put("name", "SSHE");
					put("data", ssheData);
				}}
		));
	
		return response;
	}

	public Map<String, Object> calculateITeacherBar(long sch_id) {
		
		List<ApiDashboardGraphBar> dataSets;
		dataSets = new ArrayList<>();
		List<String> categoryList     = new ArrayList<>();
		List<Double> tqgenValueList   = new ArrayList<>();
		List<Double> tqgenpValueList  = new ArrayList<>();
		List<Double> tqscValueList    = new ArrayList<>();
		List<Double> tqscpValueList   = new ArrayList<>();
		List<Double> tqgenaoValueList = new ArrayList<>();
		List<Double> tqpaoValueList   = new ArrayList<>();
		Map<String, Integer> tmapings = copyTeacherMappings();

		Optional<School> sch = schRepository.findById(sch_id);
		if ( sch.isPresent() ) {
			School val_sch = sch.get();				
			List<DashboardTeacherInput> dalist = dashTIRepository.findBySchoolTeacherOnly(val_sch);
				if (dalist.size() > 0) {
					for (DashboardTeacherInput dai: dalist){
						categoryList.add(String.valueOf(dai.get_year()));
						Double trcc            = Double.valueOf( tmapings.get(dai.getTrcc_option()) );		
						Double academic        = Double.valueOf( tmapings.get(dai.getAcademic_option()) );
						Double qualification   = Double.valueOf( tmapings.get(dai.getQualification_in_education_option()));
						Double engagement      = Double.valueOf( tmapings.get(dai.getType_of_engagement_option()));	
						Double experience      = Double.valueOf( tmapings.get(dai.getHighest_experience_option()));
						Double total1          = trcc + academic + engagement + experience;					
						tqgenValueList.add( ( total1/50 ) * 10 );
						Double total2 = trcc + qualification + engagement + experience;
						tqgenpValueList.add( ( total2/65 ) * 10);

						if (dai.getDiscipline_option().equals("stem") ){
							Double trcc2            = Double.valueOf( tmapings.get(dai.getTrcc_option()) );		
							Double academic2        = Double.valueOf( tmapings.get(dai.getAcademic_option()) );
							Double qualification2   = Double.valueOf( tmapings.get(dai.getQualification_in_education_option()));
							Double engagement2      = Double.valueOf( tmapings.get(dai.getType_of_engagement_option()));	
							Double experience2      = Double.valueOf( tmapings.get(dai.getHighest_experience_option()));
							Double total3          = trcc2 + academic2 + engagement2 + experience2;					
							tqscValueList.add( ( total3/50 ) * 10 );
							Double total4 = trcc2 + qualification2 + engagement2 + experience2;
							tqscpValueList.add( ( total4/65 ) * 10);
						}

						else if (dai.getDiscipline_option().equals("arts") || dai.getDiscipline_option().equals("social_science")){
							Double trcc2            = Double.valueOf( tmapings.get(dai.getTrcc_option()) );		
							Double academic2        = Double.valueOf( tmapings.get(dai.getAcademic_option()) );
							Double qualification2   = Double.valueOf( tmapings.get(dai.getQualification_in_education_option()));
							Double engagement2      = Double.valueOf( tmapings.get(dai.getType_of_engagement_option()));	
							Double experience2      = Double.valueOf( tmapings.get(dai.getHighest_experience_option()));
							Double total3          = trcc2 + academic2 + engagement2 + experience2;					
							tqgenaoValueList.add( ( total3/50 ) * 10 );
							Double total4 = trcc2 + qualification2 + engagement2 + experience2;
							tqpaoValueList.add( ( total4/65 ) * 10);
						}
						
					}
					ApiDashboardGraphBar appbar  = new ApiDashboardGraphBar( "TQ GEN",     tqgenValueList );
					ApiDashboardGraphBar appbar2 = new ApiDashboardGraphBar( "TQ GEN P",   tqgenpValueList );
					ApiDashboardGraphBar appbar3 = new ApiDashboardGraphBar( "TQ SC GEN",  tqscValueList );
					ApiDashboardGraphBar appbar4 = new ApiDashboardGraphBar( "TQ SC P",    tqscpValueList );
					ApiDashboardGraphBar appbar5 = new ApiDashboardGraphBar( "TQ A&O GEN", tqgenaoValueList );
					ApiDashboardGraphBar appbar6 = new ApiDashboardGraphBar( "TQ A&O P",   tqpaoValueList );
					
					dataSets.add(appbar);
					dataSets.add(appbar2);
					dataSets.add(appbar3);
					dataSets.add(appbar4);	
					dataSets.add(appbar5);
					dataSets.add(appbar6);									
				}				
			
				Map<String, Object> response = new HashMap<>();
			
				response.put("categories", categoryList);
				response.put("series", dataSets);
				
				return response;			
		}
		return null;
	}

	public Map<String, Object> calculateITeacherLine(long sch_id) {		
		Map<String, Integer> tmapings     = copyTeacherMappings();
		Map<String, Object> series        = new HashMap<>();
		List<String> categoryList         = new ArrayList<>();
		
	
		Optional<School> sch = schRepository.findById(sch_id);
		if (sch.isPresent()) {
			School val_sch = sch.get();
			List<DashboardTeacherInput> dalist = dashTIRepository.findBySchoolTeacherOnly(val_sch);
			if (dalist.size() > 0) {
				for (DashboardTeacherInput dai : dalist) {
					String category = String.valueOf(dai.get_year());
					ArrayList<ArrayList<Object>> dataArray = new ArrayList<>();
					categoryList.add(category);
					Double trcc            = Double.valueOf( tmapings.get(dai.getTrcc_option()) );		
					Double academic        = Double.valueOf( tmapings.get(dai.getAcademic_option()) );
					Double qualification   = Double.valueOf( tmapings.get(dai.getQualification_in_education_option()) );
					Double engagement      = Double.valueOf( tmapings.get(dai.getType_of_engagement_option()) );	
					Double experience      = Double.valueOf( tmapings.get(dai.getHighest_experience_option()) );
					Double total1          = trcc + academic + engagement + experience;	
					Double total2          = trcc + qualification + engagement + experience;

					Double total3          = dai.getDiscipline_option().equals("stem") ? (trcc + academic + engagement + experience) : 1;	
					Double total4          = dai.getDiscipline_option().equals("stem") ? (trcc + qualification + engagement + experience) : 1;	
					
					Double total5          = dai.getDiscipline_option().equals("social_science") || dai.getDiscipline_option().equals("arts") ? (trcc + academic + engagement + experience) : 1;
					Double total6          = dai.getDiscipline_option().equals("social_science") || dai.getDiscipline_option().equals("arts") ? (trcc + qualification + engagement + experience) : 1;

					dataArray.add(new ArrayList<>(List.of("TQ GEN", ( total1/50 ) * 10 )));
					dataArray.add(new ArrayList<>(List.of("TQ GEN P", ( total2/65 ) * 10 )));
					dataArray.add(new ArrayList<>(List.of("TQ SC GEN", ( total3/50 ) * 10 )));
					dataArray.add(new ArrayList<>(List.of("TQ SC P", ( total4/65 ) * 10 )));
					dataArray.add(new ArrayList<>(List.of("TQ A&O GEN", ( total5/50 ) * 10 )));
					dataArray.add(new ArrayList<>(List.of("TQ A&O P", ( total6/65 ) * 10 )));

					series.put("name", category );
					series.put("data", dataArray );
								
				}
				
			}
		}
	
		Map<String, Object> response = new HashMap<>();
		response.put("categories", categoryList);
		response.put("series", series);
	
		return response;
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

	public DashboardAcademicInput findBySchoolAI(long id, Integer year) {
		Optional<School> sch = schRepository.findById(id);
		Optional<DashboardAcademicInput> das = dashAIRepository.findBySchoolAcademic(sch.get(), year);
		if (das.isPresent()) {
			DashboardAcademicInput dasval = das.get();			
			return dasval;
		}
		return null;
	}

	public DashboardTeacherInput findBySchoolTI(long id, Integer year) {
		Optional<School> sch = schRepository.findById(id);
		Optional<DashboardTeacherInput> das = dashTIRepository.findBySchoolTeacher(sch.get(), year);
		if (das.isPresent()) {
			DashboardTeacherInput dasval = das.get();			
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

	private Map<String, Integer> copyTeacherMappings() {
		mapTeacherRatings.put("yes", 5);
		mapTeacherRatings.put("no", 0);

		mapTeacherRatings.put("waec", 5);
		mapTeacherRatings.put("ttc", 10);
		mapTeacherRatings.put("ond", 15);
		mapTeacherRatings.put("hnd", 20);
		mapTeacherRatings.put("bsc", 25);
		mapTeacherRatings.put("pgd", 30);
		mapTeacherRatings.put("masters", 35);
		mapTeacherRatings.put("doctorate", 40);

		mapTeacherRatings.put("edu", 5);
		mapTeacherRatings.put("nce", 20);
		mapTeacherRatings.put("bed", 25);
		mapTeacherRatings.put("pgde", 30);
		mapTeacherRatings.put("med", 35);
		mapTeacherRatings.put("phded", 40);

		mapTeacherRatings.put("intern", 5);
		mapTeacherRatings.put("parttime", 5);
		mapTeacherRatings.put("permanent", 10);

		mapTeacherRatings.put("stem", 5);
		mapTeacherRatings.put("arts", 5);
		mapTeacherRatings.put("social_science", 10);

		mapTeacherRatings.put("not_available", 0);
		mapTeacherRatings.put("less", 0);
		mapTeacherRatings.put("more", 10);
		
	    return mapTeacherRatings;
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
