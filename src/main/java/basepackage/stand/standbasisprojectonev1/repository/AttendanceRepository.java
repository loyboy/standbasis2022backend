package basepackage.stand.standbasisprojectonev1.repository;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import basepackage.stand.standbasisprojectonev1.model.Attendance;
import basepackage.stand.standbasisprojectonev1.model.Calendar;
import basepackage.stand.standbasisprojectonev1.model.ClassStream;
import basepackage.stand.standbasisprojectonev1.model.Rowcall;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;
import basepackage.stand.standbasisprojectonev1.model.Student;
import basepackage.stand.standbasisprojectonev1.model.Subject;
import basepackage.stand.standbasisprojectonev1.model.Teacher;
import basepackage.stand.standbasisprojectonev1.model.TimeTable;


@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long>{

	Optional<Attendance> findById(Long classId);
    
    List<Attendance> findByTimetable(TimeTable t);
    
    
    @Query(" select att from Attendance att where att._desc like :filter ")
    Page<Attendance> filter( @Param("filter") String filter, Pageable pg); 
    
    @Query(" select att from Attendance att where att._desc like :filter ")
    List<Attendance> filterAll( @Param("filter") String filter); 
    
    @Query(" select rc from Rowcall rc where rc.attendance = :att ")
    Rowcall findByRowcall(   @Param("att") Attendance att    );
    
    @Query( " select att from Attendance att where att.timetable.teacher = :tea "
    		+ "AND ( DATE(att._date) >= :monday AND DATE(att._date) <= :friday ) "  
    		+ "AND (att.done = 0) "
    	  )
    List<Attendance> findByTeacherTodayClass(     		 
    		@Param("tea") Teacher tea,    		
    		@Param("friday") Date friday,
    		@Param("monday") Date monday
    );
    //-------------------------------------------------------------------------------------------------
    
    @Query("select att from Attendance att where att.timetable.school.owner = :owner "
    		+ "AND ( :sch is null OR att.timetable.school = :sch ) "
    		+ "AND ( :cls is null OR att.timetable.class_stream = :cls ) "
    		+ "AND ( :tea is null OR att.timetable.teacher = :tea ) "
    		+ "AND ( :cal is null OR att.timetable.calendar = :cal ) "
    		+ "AND ( :sub is null OR att.timetable.subject = :sub ) "
    		+ "AND ( :datefrom is null OR att._date >= :datefrom ) "
    		+ "AND ( :dateto is null OR DATE(att._date) = :dateto ) "
    	   )
    List<Attendance> findByTeacherSchoolgroup( 
    		@Param("owner") SchoolGroup owner, 
    		@Param("sch") School sch, 
    		@Param("cls") ClassStream cls, 
    		@Param("tea") Teacher tea,
    		@Param("cal") Calendar cal,
    		@Param("sub") Subject sub,
    		@Param("datefrom") Date datefrom,
    		@Param("dateto") Date dateto
    );
   
    @Query("select att from Attendance att where att.timetable.school.owner = :owner "
    		+ "AND (att.timetable.school = :sch OR :sch is null) "
    		+ "AND (att.timetable.class_stream = :cls OR :cls is null) "
    		+ "AND (att.timetable.teacher = :tea OR :tea is null) "
    		+ "AND (att.timetable.calendar = :cal OR :cal is null) "
    		+ "AND (att.timetable.subject = :sub OR :sub is null) "
    		+ "AND (att.done = :status OR :status is null) "
    		+ "AND (DATE(att._date) >= :datefrom OR :datefrom is null) "
    		+ "AND (DATE(att._date) <= :dateto OR :dateto is null) "
    	   )
    Page<Attendance> findByTeacherSchoolgroupPage( 
    		@Param("owner") SchoolGroup owner,
    		@Param("sch") School sch, 
    		@Param("cls") ClassStream cls, 
    		@Param("tea") Teacher tea,
    		@Param("cal") Calendar cal,
    		@Param("sub") Subject sub,
    		@Param("status") Integer status,
    		@Param("datefrom") Date datefrom,
    		@Param("dateto") Date dateto,
    		Pageable pg
    );
    
    /**
     * 		
     * 
     * **/
    
    /**
     * 		
     * */
    
    ///-----------------------------------------------------------------------
    
    @Query("select att from Attendance att where att.timetable.school.owner = :owner "
    		+ "AND (att.timetable.school = :sch OR :sch is null) "
    		+ "AND (att.timetable.class_stream = :cls OR :cls is null) "
    		+ "AND (att.timetable.teacher = :tea OR :tea is null) "
    		+ "AND (att.timetable.subject = :sub OR :sub is null) "
    		+ "AND (att.timetable.calendar = :cal OR :cal is null) "
    		+ "AND (att._date >= :datefrom OR :datefrom is null) "
    		+ "AND (att._date <= :dateto OR :dateto is null) "
    		+ "AND (att._desc like :filter OR :filter is null) "
       	 )
    
    List<Attendance> findFilterByTeacherSchoolgroup(
    		@Param("filter") String filter, 
    		@Param("owner") SchoolGroup owner, 
    		@Param("sch") School sch, 
    		@Param("cls") ClassStream cls, 
    		@Param("tea") Teacher tea,      		
    		@Param("cal") Calendar cal,
    		@Param("sub") Subject sub,
    		@Param("datefrom") Timestamp datefrom,
    		@Param("dateto") Timestamp dateto
    	);
    
    @Query("select att from Attendance att where att.timetable.school.owner = :owner "
    		+ "AND (att.timetable.school = :sch OR :sch is null) "
    		+ "AND (att.timetable.class_stream = :cls OR :cls is null) "
    		+ "AND (att.timetable.teacher = :tea OR :tea is null) "
    		+ "AND (att.timetable.calendar = :cal OR :cal is null) "
    		+ "AND (att.timetable.subject = :sub OR :sub is null) "
    		+ "AND (att.done = :status OR :status is null) "
    		+ "AND (DATE(att._date) >= :datefrom OR :datefrom is null) "
    		+ "AND (DATE(att._date) <= :dateto OR :dateto is null) "
    		+ "AND (att._desc like :filter OR :filter is null) "
       	 )
    
    Page<Attendance> findFilterByTeacherSchoolgroupPage(
    		@Param("filter") String filter, 
    		@Param("owner") SchoolGroup owner, 
    		@Param("sch") School sch, 
    		@Param("cls") ClassStream cls, 
    		@Param("tea") Teacher tea, 
    		@Param("cal") Calendar cal,
    		@Param("sub") Subject sub,
    		@Param("status") Integer status,
    		@Param("datefrom") Date datefrom,
    		@Param("dateto") Date dateto,
    		Pageable pg
    	);
    
    //-----------------------------------------------------------------------------------
    
    @Query("select rw from Rowcall rw "
    		+ "JOIN Attendance att ON att = rw.attendance "
    		+ "WHERE att.timetable.school.owner = :owner " 
    		+ "AND (att.timetable.school = :sch OR :sch is null) "
    		+ "AND (att.timetable.class_stream = :cls OR :cls is null) "
    		+ "AND (att.timetable.calendar = :cal OR :cal is null) "
    		+ "AND (rw.student = :pup OR :pup is null) "
    		+ "AND (att._date >= :datefrom OR :datefrom is null) "
    		+ "AND ( DATE(att._date) = :dateto OR :dateto is null) "
       	 )
    
    List<Rowcall> findByStudentSchoolgroup(
    		@Param("owner") SchoolGroup owner, 
    		@Param("sch") School sch, 
    		@Param("cls") ClassStream cls, 
    		@Param("pup") Student pup,
    		@Param("cal") Calendar cal,
    		@Param("datefrom") Date datefrom,
    		@Param("dateto") Date dateto
    	);
    
    @Query("select rw from Rowcall rw "
    		+ "JOIN Attendance att ON att = rw.attendance "
    		+ "WHERE att.timetable.school.owner = :owner " 
    		+ "AND (att.timetable.school = :sch OR :sch is null) "
    		+ "AND (att.timetable.class_stream = :cls OR :cls is null) "
    		+ "AND (att.timetable.calendar = :cal OR :cal is null) "
    		+ "AND (att.timetable.subject = :sub OR :sub is null) "
    		+ "AND (att.teacher = :tea OR :tea is null) "
    		+ "AND (rw.student = :pup OR :pup is null) "
    		+ "AND (rw.attendance = :att OR :att is null) "
    		+ "AND (rw.status = :status OR :status is null) "
    		+ "AND (att._date >= :datefrom OR :datefrom is null) "
    		+ "AND (att._date <= :dateto OR :dateto is null) "
       	 )
    
    Page<Rowcall> findByStudentSchoolgroupPage(
    		@Param("owner") SchoolGroup owner, 
    		@Param("sch") School sch, 
    		@Param("cls") ClassStream cls, 
    		@Param("pup") Student pup,
    		@Param("cal") Calendar cal,
    		@Param("tea") Teacher tea,
    		@Param("sub") Subject sub,
    		@Param("att") Attendance att,
    		@Param("status") Integer status,
    		@Param("datefrom") Timestamp datefrom,
    		@Param("dateto") Timestamp dateto,
    		Pageable pg
    	);
    
    //------------------------------------------------------------
    
    @Query("select rw from Rowcall rw "
    		+ "JOIN Attendance att ON att = rw.attendance "
    		+ "WHERE att.timetable.school.owner = :owner "    		 
    		+ "AND (att.timetable.school = :sch OR :sch is null) "
    		+ "AND (att.timetable.class_stream = :cls OR :cls is null) "
    		+ "AND (att.timetable.calendar = :cal OR :cal is null) "
    		+ "AND (rw.student = :pup OR :pup is null) "
    		+ "AND (att._date >= :datefrom OR :datefrom is null) "
    		+ "AND (att._date <= :dateto OR :dateto is null) "
    		+ "OR att._desc like :filter "
       	 )    
    List<Rowcall> findFilterByStudentSchoolgroup(
    		@Param("filter") String filter, 
    		@Param("owner") SchoolGroup owner, 
    		@Param("sch") School sch, 
    		@Param("cls") ClassStream cls, 
    		@Param("pup") Student pup,
    		@Param("cal") Calendar cal,
    		@Param("datefrom") Timestamp datefrom,
    		@Param("dateto") Timestamp dateto
    	);
    
    @Query("select rw from Rowcall rw "
    		+ "JOIN Attendance att ON att = rw.attendance "
    		+ "WHERE att.timetable.school.owner = :owner "    		 
    		+ "AND (att.timetable.school = :sch OR :sch is null) "
    		+ "AND (att.timetable.class_stream = :cls OR :cls is null) "
    		+ "AND (att.timetable.calendar = :cal OR :cal is null) "
    		+ "AND (att.timetable.subject = :sub OR :sub is null) "
    		+ "AND (att.teacher = :tea OR :tea is null) "
    		+ "AND (rw.student = :pup OR :pup is null) "
    		+ "AND (rw.attendance = :att OR :att is null) "
    		+ "AND (rw.status = :status OR :status is null) "
    		+ "AND (att._date >= :datefrom OR :datefrom is null) "
    		+ "AND (att._date <= :dateto OR :dateto is null) "
    		+ "OR att._desc like :filter "
       	 )    
    Page<Rowcall> findFilterByStudentSchoolgroupPage(
    		@Param("filter") String filter, 
    		@Param("owner") SchoolGroup owner, 
    		@Param("sch") School sch, 
    		@Param("cls") ClassStream cls, 
    		@Param("pup") Student pup,
    		@Param("cal") Calendar cal,
    		@Param("tea") Teacher tea,
    		@Param("sub") Subject sub,
    		@Param("att") Attendance att,
    		@Param("status") Integer status,
    		@Param("datefrom") Timestamp datefrom,
    		@Param("dateto") Timestamp dateto,
    		Pageable pg
    	);
    
    //MNE details here
    
    @Query("select rw from Rowcall rw "
    		+ "JOIN Attendance att ON att = rw.attendance " 
    		+ "WHERE (att.timetable.calendar.CalendarId = :cal OR :cal is null) "
    		+ "AND (rw.student.pupId = :pup OR :pup is null) "
    		+ "AND ( DATE(att._date) >= :datefrom OR :datefrom is null) "
    		+ "AND ( DATE(att._date) <= :dateto OR :dateto is null) "
       	 )    
    List<Rowcall> findByStudentMne(    		
    		@Param("pup") Long pup,
    		@Param("cal") Long cal,
    		@Param("datefrom") Timestamp datefrom,
    		@Param("dateto") Timestamp dateto
    	);
    
    @Query("select att from Attendance att "
    		+ "WHERE (att.timetable.calendar.CalendarId = :cal OR :cal is null) "
    		+ "AND (att.teacher.teaId = :tea OR :tea is null) "
    		+ "AND ( DATE(att._date) >= :datefrom OR :datefrom is null) "
    		+ "AND ( DATE(att._date) <= :dateto OR :dateto is null) "
       	 )    
    List<Attendance> findByTeacherMne(    		
    		@Param("tea") Long tea,
    		@Param("cal") Long cal,
    		@Param("datefrom") Timestamp datefrom,
    		@Param("dateto") Timestamp dateto
    	);
    
    //Super admin
     @Query("SELECT DATE(s.createdAt) AS createdDate, COUNT(s) AS count FROM Attendance s " +
	           "WHERE DATE(s.createdAt) >= :startDate AND DATE(s.createdAt) <= :endDate AND s.calendar.status = 1 AND s.done = 1 " +
	           "GROUP BY DATE(s.createdAt)")
	 List<Object[]> countAttendancesDoneCreatedPerDay(Timestamp startDate, Timestamp endDate);
	 
	 @Query("SELECT DATE(s.createdAt) AS createdDate, COUNT(s) AS count FROM Attendance s " +
	           "WHERE DATE(s.createdAt) >= :startDate AND DATE(s.createdAt) <= :endDate AND s.calendar.status = 1 " +
	           "GROUP BY DATE(s.createdAt)")
	 List<Object[]> countAttendancesTotalCreatedPerDay(Timestamp startDate, Timestamp endDate);
	 
	 @Query("SELECT DATE(s.createdAt) AS createdDate, COUNT(DISTINCT t.teaId) AS count FROM Attendance s " +
		       "JOIN Teacher t ON t = s.teacher " +
		       "WHERE DATE(s.createdAt) >= :startDate AND DATE(s.createdAt) <= :endDate AND s.calendar.status = 1 AND s.done = 1 " +
		       "GROUP BY DATE(s.createdAt)")
	 List<Object[]> countUniqueTeachersAttendancesPerDay(Timestamp startDate, Timestamp endDate);
	 
	 @Query("SELECT DATE(s.createdAt) AS createdDate, COUNT(s) AS count FROM Attendance s " +
	           "WHERE DATE(s.createdAt) >= :startDate AND DATE(s.createdAt) <= :endDate AND s.calendar.status = 1 AND s.done = 1 " +
	           "AND (s.timetable.school.owner = :owner OR :owner is null) "
	         + "AND (s.timetable.school = :sch OR :sch is null) "	         
	         + "AND (s.timetable.teacher = :tea OR :tea is null) "
	         + "GROUP BY DATE(s.createdAt) ")
	 List<Object[]> countSchoolAttendancesDoneCreatedPerDay(
			 Timestamp startDate, 
			 Timestamp endDate,
			  SchoolGroup owner, 
	    	  School sch, 
	    	  Teacher tea );
}
