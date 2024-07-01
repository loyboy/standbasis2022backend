package basepackage.stand.standbasisprojectonev1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import basepackage.stand.standbasisprojectonev1.model.Attendance;
import basepackage.stand.standbasisprojectonev1.model.AttendanceActivity;
import basepackage.stand.standbasisprojectonev1.model.AttendanceManagement;
import basepackage.stand.standbasisprojectonev1.model.Calendar;
import basepackage.stand.standbasisprojectonev1.model.Enrollment;
import basepackage.stand.standbasisprojectonev1.model.Lessonnote;
import basepackage.stand.standbasisprojectonev1.model.LessonnoteActivity;
import basepackage.stand.standbasisprojectonev1.model.LessonnoteManagement;
import basepackage.stand.standbasisprojectonev1.model.Rowcall;
import basepackage.stand.standbasisprojectonev1.repository.CalendarRepository;

@Service
public class MneService {
	
	private static final Logger logger = LoggerFactory.getLogger(SubjectService.class);
	
    @Autowired
	AttendanceService service;

    @Autowired
	LessonnoteService lsnService;

    @Autowired
    AssessmentService asservice;
   
    @Autowired
    SchoolService schoolservice;
   
    @Autowired
    StudentService studentservice;
    
    @Autowired
    TimetableService timetableservice;
    
    @Autowired
    EnrollmentService enrolservice;
    
    @Autowired
    TeacherService teacherservice;
    
    @Autowired
    ClassService classservice;
    
    @Autowired
    SubjectService subjectservice;
    
    @Autowired
    CalendarService calendarservice;
    
    @Autowired
    LessonnoteManagementService serviceManagement;
    
    @Autowired
    LessonnoteActivityService serviceActivity;
    
    @Autowired
    AttendanceManagementService serviceAttManagement;

    @Autowired
    AttendanceService serviceAtt;
    
    @Autowired
    AttendanceActivityService serviceAttActivity;

    @Autowired
	EnrollmentService serviceEnrollment;
    
    @Autowired		
    private CalendarRepository calRepository;

    @SuppressWarnings("unchecked")
    public Map<String, Object> getOrdinaryLessonnoteMneProprietor( Optional<Long> schoolgroup, Optional<Long> school, Optional<Long> calendar, Optional<Integer> week ) {

             Optional<Integer> termVal = Optional.ofNullable(null);
		     Optional<String> yearVal = Optional.ofNullable(null);
		     
		     Optional<basepackage.stand.standbasisprojectonev1.model.Calendar> calendarownerobj = null;
		     if(calendar.isPresent()) { calendarownerobj = calRepository.findById( calendar.get() );  } 
		     if (calendar.isPresent()) { 
		    	 termVal = Optional.ofNullable(calendarownerobj.get().getTerm());  
		    	 yearVal = Optional.ofNullable(calendarownerobj.get().getSession());  
		     }
		     
			 Optional<Long> WithNullableValue = Optional.ofNullable(null);
			 Optional<Integer> WithNullableValueInt = Optional.ofNullable(null);
			 Optional<Timestamp> WithNullableValueTime = Optional.ofNullable(null);
			 
			 Map<String, Object> lsnManageResponse = serviceManagement.getOrdinaryTeacherLessonnotes("", schoolgroup, school, WithNullableValueInt, week, yearVal, termVal, WithNullableValue, WithNullableValue, WithNullableValueTime, WithNullableValueTime );
			 Map<String, Object> lsnActivityResponse = serviceActivity.getOrdinaryTeacherLessonnotes("", schoolgroup, school, WithNullableValueInt, week,  calendar, WithNullableValue, WithNullableValueTime, WithNullableValueTime );
			 
			 
             List<LessonnoteManagement> ordinaryArrayManagement = (List<LessonnoteManagement>) lsnManageResponse.get("lessonnotemanagement");
			 List<LessonnoteActivity> ordinaryArrayActivity = (List<LessonnoteActivity>) lsnActivityResponse.get("Lessonnoteactivity");
			 
			 Map<String, Object> newResponse = new HashMap<>();
			 
			 Integer maxManagement = ordinaryArrayManagement != null ? ordinaryArrayManagement.size() : 10; 
			 Integer maxActivity = ordinaryArrayActivity != null ? ordinaryArrayActivity.size() : 10; 
			 
			 Long teacherManagement = ordinaryArrayManagement != null ? ordinaryArrayManagement.stream().filter(o -> o.getManagement() >= 50).count() : 0; 
			 Long headAdministration = ordinaryArrayActivity != null ? ordinaryArrayActivity.stream().filter(o -> o.getActual() != null && o.getSlip().equals(0) && o.getOwnertype().equals("Principal") ).count() : 0; 
			 
			 newResponse.put("teacher_management", maxManagement > 0 ? (teacherManagement.intValue() * 100)/maxManagement : 0 );
			 newResponse.put("head_admin", maxActivity > 0 ? (headAdministration.intValue() * 100)/maxActivity : 0 );

             return newResponse;
    }
	
    @SuppressWarnings("unchecked")
    public Map<String, Object> getOrdinaryAttendanceMneProprietor( Optional<Long> schoolgroup, Optional<Long> school, Optional<Long> calendar, Optional<Integer> week  ){

        Optional<Timestamp> WithStartValueTime = Optional.ofNullable(null);
        Optional<Timestamp> WithEndValueTime = Optional.ofNullable(null);
        
        if (calendar.isPresent() && week.isPresent()) {
           Calendar calobj = calendarservice.findCalendar(calendar.get());
           LocalDate stDate = calobj.getStartdate().toInstant().atZone(ZoneId.of("UTC")).toLocalDate();
           LocalDate endDate = calobj.getEnddate().toInstant().atZone(ZoneId.of("UTC")).toLocalDate();
           
           List<LocalDate> weekrange = getWeekdayRange(week.get(), stDate, endDate);				 
           
           LocalDateTime localDateTime1 = weekrange.get(0).atStartOfDay();
           LocalDateTime localDateTime2 = weekrange.get(4).atStartOfDay();

           Timestamp timestampWeekStart = Timestamp.valueOf(localDateTime1);
           Timestamp timestampWeekEnd = Timestamp.valueOf(localDateTime2);
           
           WithStartValueTime = Optional.ofNullable(timestampWeekStart);
           WithEndValueTime = Optional.ofNullable(timestampWeekEnd);
        }
   
       Optional<Long> WithNullableValue = Optional.ofNullable(null);
       
       Map<String, Object> attResponse = serviceAtt.getOrdinaryTeacherAttendances("", schoolgroup, school, WithNullableValue, calendar, WithNullableValue, WithNullableValue, WithStartValueTime, WithEndValueTime );
       Map<String, Object> attManangeResponse = serviceAttManagement.getOrdinaryTeacherAttendances("", schoolgroup, school, WithNullableValue, calendar, WithNullableValue, WithNullableValue, WithStartValueTime, WithEndValueTime );
       Map<String, Object> attActivityResponse = serviceAttActivity.getOrdinaryTeacherAttendances( "", schoolgroup, school, WithNullableValue, calendar, WithNullableValue, WithStartValueTime, WithEndValueTime );
       Map<String, Object> attStudent = serviceAtt.getOrdinaryStudentAttendances("", schoolgroup, school, WithNullableValue,  calendar, WithNullableValue, WithStartValueTime, WithEndValueTime);
       
       List<Attendance> ordinaryArray = (List<Attendance>) attResponse.get("attendances");
       List<Rowcall> ordinaryStudentArray = (List<Rowcall>) attStudent.get("attendances");
       List<AttendanceManagement> ordinaryArrayManagement = (List<AttendanceManagement>) attManangeResponse.get("attendancemanagement");
       List<AttendanceActivity> ordinaryArrayActivity = (List<AttendanceActivity>) attActivityResponse.get("attendanceactivity");
       
       Map<String, Object> newResponse = new HashMap<>();
       
       Integer maxManagement = ordinaryArrayManagement != null ? ordinaryArrayManagement.size() : 10; 
       Integer max = ordinaryArray != null ? ordinaryArray.size() : 10;
       Integer maxActivity = ordinaryArrayActivity != null ? ordinaryArrayActivity.size() : 10;
       Integer maxStudent = ordinaryStudentArray != null ? ordinaryStudentArray.size() : 10;
       
       Long teacherAttendance = ordinaryArray != null ? ordinaryArray.stream().filter(o -> ( o.getDone() == 1 || o.getDone() == 2 ) ).count() : 0; 
       Long teacherAttendanceManagement = ordinaryArrayManagement != null ? ordinaryArrayManagement.stream().filter(o -> o.getScore() >= 50).count() : 0;
       Long studentAttendance = ordinaryStudentArray != null ? ordinaryStudentArray.stream().filter(o -> o.getStatus() == 1).count() : 0; 
       Long studentExcusedAttendance = ordinaryStudentArray != null ?  ordinaryStudentArray.stream().filter(o -> o.getStatus() == 0 && o.getRemark() != null).count() : 0; 
       Long headAdministration = ordinaryArrayActivity != null ? ordinaryArrayActivity.stream().filter(o -> o.getActual() != null && o.getSlip().equals(0) && o.getOwnertype().equals("Principal") ).count() : 0; 
       
       newResponse.put("teacher_attendance", max > 0 ? (teacherAttendance.intValue() * 100)/max : 0  );
       newResponse.put("teacher_management", maxManagement > 0 ? (teacherAttendanceManagement.intValue() * 100)/maxManagement : 0 );
       newResponse.put("student_att", maxStudent > 0 ? (studentAttendance.intValue() * 100)/maxStudent : 0 ) ;
       newResponse.put("student_att_excused", maxStudent > 0 ? (studentExcusedAttendance.intValue()  * 100)/maxStudent : 0 );
       newResponse.put("head_admin", maxActivity > 0 ? (headAdministration.intValue() * 100)/maxActivity : 0);

       return newResponse;
    }
	
    @SuppressWarnings("unchecked")
    public Map<String, Object> getOrdinaryAttendanceFlags( String query, Optional<Long> schoolgroup, Optional<Long> school, Optional<Long> classid, Optional<Long> calendar, Optional<Long> teacher, Optional<Long> subject,  Optional<Long> student, Optional<Timestamp> datefrom, Optional<Timestamp> dateto ) {
        
        Map<String, Object> response = service.getOrdinaryTeacherAttendances(query, schoolgroup, school, classid, calendar, teacher, subject, datefrom, dateto  );
        Map<String, Object> attManageResponse = serviceAttManagement.getOrdinaryTeacherAttendances(query, schoolgroup, school, classid, calendar, teacher, subject, datefrom, dateto);
        Map<String, Object> attStudent = service.getOrdinaryStudentAttendances(query, schoolgroup, school, classid,  calendar, student, datefrom, dateto);
        Map<String, Object> attActivityResponse = serviceAttActivity.getOrdinaryTeacherAttendances(query, schoolgroup, school, classid, calendar, teacher, datefrom, dateto);
            
        List<Attendance> ordinaryArray = (List<Attendance>) response.get("attendances");
        List<Rowcall> ordinaryStudentArray = (List<Rowcall>) attStudent.get("attendances");
        List<AttendanceManagement> ordinaryArrayManagement = (List<AttendanceManagement>) attManageResponse.get("attendancemanagement");
        List<AttendanceActivity> ordinaryArrayActivity = (List<AttendanceActivity>) attActivityResponse.get("attendanceactivity");
           
        Map<String, Object> newResponse = new HashMap<>();
        int student_population = 0;
        for (Attendance att : ordinaryArray) {	
            List<Enrollment> ordinaryEnroll = serviceEnrollment.findEnrollmentFromClass(att.getTimetable().getClass_stream().getClsId());
            student_population += ordinaryEnroll.size();
        }
        
        Integer max = ordinaryArray.size(); 
        Integer maxManage = ordinaryArrayManagement.size();
        Integer maxStudent = ordinaryStudentArray.size();
        Integer maxActivity = ordinaryArrayActivity.size();
        
        Long studentAbsence = ordinaryStudentArray.stream().filter(o -> o.getStatus() == 1).count(); 
        Long studentExcusedAbsence = ordinaryStudentArray.stream().filter(o -> o.getStatus() == 0 && o.getRemark() != null ).count(); 
      //  Long incompleteAttendance = ordinaryArrayManagement.stream().filter(o -> o.getCompleteness() == 50 ).count(); 
        Long lateAttendance = ordinaryArrayManagement.stream().filter(o -> o.getTiming() == 50).count(); 
        Long voidAttendance = ordinaryArrayManagement.stream().filter(o -> o.getTiming() == 0).count();
        Long Approvalslip  = ordinaryArrayActivity.stream().filter(o -> o.getSlip() == 1 && o.getOwnertype().equals("Principal")).count();
        Long Approvaldone  = ordinaryArrayActivity.stream().filter(o -> o.getOwnertype().equals("Principal") && o.getActual() != null ).count();
        Long ApprovalStatus  = ordinaryArrayActivity.stream().filter(o -> o.getOwnertype().equals("Principal") && o.getActual() != null && o.getAction().equals("queried") ).count();
        Long teacherAbsent = ordinaryArray.stream().filter(o -> o.getDone() == 0 ).count(); 
                   
        newResponse.put("student_absence", studentAbsence.intValue() );
        newResponse.put("student_excused_absence", studentExcusedAbsence.intValue() );
        newResponse.put("queried_attendance", ApprovalStatus.intValue() );
        newResponse.put("late_attendance", lateAttendance.intValue() );
        newResponse.put("void_attendance", voidAttendance.intValue() );
        newResponse.put("approval_delays", Approvalslip.intValue() );
        newResponse.put("approval_done", Approvaldone.intValue() );
        newResponse.put("teacher_absent", teacherAbsent.intValue() );
        newResponse.put("teacher_expected", max );
        newResponse.put("student_expected", student_population );
        newResponse.put("endorsement_expected", ordinaryArrayActivity.stream().filter(o -> o.getOwnertype().equals("Principal") ).count() );
           
        return newResponse;
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, Object> getOrdinaryLessonnoteFlags( String query, Optional<Long> schoolgroup, Optional<Long> school, Optional<String> schoolyear, Optional<Integer> schoolterm, Optional<Integer> week, Optional<Integer> classid,  Optional<Long> teacher, Optional<Long> subject, Optional<Timestamp> datefrom, Optional<Timestamp> dateto ) {
    
        Map<String, Object> response = lsnService.getOrdinaryTeacherLessonnotes(query, schoolgroup, school, classid, week, schoolyear, schoolterm, teacher, subject, datefrom, dateto );
        Map<String, Object> lsnManageResponse = serviceManagement.getOrdinaryTeacherLessonnotes(query, schoolgroup, school, classid, week, schoolyear, schoolterm, teacher, subject, datefrom, dateto );
               
        List<Lessonnote> ordinaryArray = (List<Lessonnote>) response.get("lessonnotes");
        List<LessonnoteManagement> ordinaryArrayManagement = (List<LessonnoteManagement>) lsnManageResponse.get("lessonnotemanagement");
        //List<LessonnoteActivity> ordinaryArrayLessonnote = (List<LessonnoteActivity>) lsnActivityResponse.get("Lessonnoteactivity");
           
        Map<String, Object> newResponse = new HashMap<>();
      
        Integer max = ordinaryArray.size(); 
        //Integer maxManage = ordinaryArrayManagement.size();
        //Integer maxActivity = ordinaryArrayLessonnote.size();
                
        Long teacherTotalLessonnotes = ordinaryArray.stream().filter(o -> o.getSubmission() != null ).count();
        Long teacherLateLessonnotes = ordinaryArray.stream().filter(o -> (o.getResubmission() != null && o.getResubmission().compareTo( o.getExpected_submission() ) > 0) || (o.getSubmission() != null && o.getSubmission().compareTo( o.getExpected_submission() ) > 0) ).count();
        
        Long teacherLateApprovalLessonnotes = ordinaryArray.stream().filter(o -> ( o.getResubmission() != null && ( o.getApproval() != null && o.getRevert() == null ) && o.getResubmission().compareTo( o.getExpected_submission() ) > 0 && o.getResubmission().compareTo( addDays(2,o.getExpected_submission()) ) < 0 ) 
                || ( o.getSubmission() != null && ( o.getApproval() != null && o.getRevert() == null ) && o.getSubmission().compareTo( o.getExpected_submission() ) > 0 && o.getSubmission().compareTo( addDays(2,o.getExpected_submission()) ) < 0 ) ).count();
        
        Long teacherNoApprovalLessonnotes = ordinaryArray.stream().filter(o -> o.getSubmission() != null && ( o.getApproval() != null && o.getRevert() == null ) && o.getApproval().compareTo( addDays(2,o.getExpected_submission()) ) > 0 ).count();
        
        Long teacherRevertedLessonnotes = ordinaryArray.stream().filter(o -> o.getSubmission() != null && o.getRevert() != null ).count();
        Long teacherBadCycles = ordinaryArrayManagement.stream().filter(o -> o.getLsn_id().getSubmission() != null && o.getQuality() < 50 && o.getQuality() != 0).count(); 
        
       /* Long teacherLateClosure = ordinaryArray.stream().filter(o -> { 			 
            if (o.getLaunch() != null) {
                Date thedate = new Date( o.getLaunch().getTime() );
                return olderThanDays( thedate, 7 );
            }
            return false;			 
        }).count();*/
        Long teacherLateClosure = ordinaryArray.stream().filter(o -> o.getSubmission() != null && o.getClosure() != null && ( o.getApproval() != null ) && o.getPrincipal_closure().compareTo( o.getExpected_closure() ) > 0 ).count(); 
        Long teacherNoClosure   = ordinaryArray.stream().filter(o -> o.getSubmission() != null && o.getClosure() != null && ( o.getApproval() != null ) && o.getPrincipal_closure().compareTo( addDays(2,o.getExpected_closure())  ) > 0 ).count();			
        
        newResponse.put("total_lessonnotes", max );
        newResponse.put("teacher_submitted", teacherTotalLessonnotes.intValue() );
        newResponse.put("teacher_late_submitted", teacherLateLessonnotes.intValue() );
        newResponse.put("teacher_late_approval", teacherLateApprovalLessonnotes.intValue() );
        newResponse.put("teacher_no_approval", teacherNoApprovalLessonnotes.intValue() );
        newResponse.put("teacher_queried", teacherRevertedLessonnotes.intValue() );
        newResponse.put("teacher_late_closure", teacherLateClosure.intValue() );
        newResponse.put("teacher_bad_cycles", teacherBadCycles.intValue() );
        newResponse.put("teacher_no_closure", teacherNoClosure.intValue() );
                
        return newResponse;
    }

    private List<LocalDate> getWeekdayRange(int weekNumber, LocalDate startDate, LocalDate endDate) {
	        
        List<LocalDate> weekdayRange = new ArrayList<>();
       
       // Find the first Monday of the given week
       LocalDate firstMonday = startDate.with(DayOfWeek.MONDAY);
       
       // Calculate the start date of the specified week
       LocalDate weekStartDate = firstMonday.plusWeeks(weekNumber);
       
       // Calculate the end date of the specified week (Friday)
       LocalDate weekEndDate = weekStartDate.plusDays(4);
       
       // Ensure the calculated week falls within the given date range
       if (weekStartDate.isBefore(startDate)) {
           weekStartDate = startDate;
       }
       
       if (weekEndDate.isAfter(endDate)) {
           weekEndDate = endDate;
       }
       
       // Add each weekday (Monday to Friday) to the weekdayRange list
       LocalDate currentDay = weekStartDate;
       while (!currentDay.isAfter(weekEndDate)) {
           weekdayRange.add(currentDay);
           currentDay = currentDay.plusDays(1);
       }
       
       System.out.println("weekrange--- : " + weekdayRange );
       
       return weekdayRange;
    }

    private Timestamp addDays(int days, Timestamp t1) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(t1);
        cal.add(java.util.Calendar.DATE, days); //minus number would decrement the days
        return new Timestamp(cal.getTime().getTime());
    }

}

