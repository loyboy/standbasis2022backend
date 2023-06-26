package basepackage.stand.standbasisprojectonev1.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import basepackage.stand.standbasisprojectonev1.model.Attendance;
import basepackage.stand.standbasisprojectonev1.model.AttendanceActivity;
import basepackage.stand.standbasisprojectonev1.model.Calendar;
import basepackage.stand.standbasisprojectonev1.model.ClassStream;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;
import basepackage.stand.standbasisprojectonev1.model.Subject;
import basepackage.stand.standbasisprojectonev1.model.Teacher;


@Repository
public interface AttendanceActivityRepository extends JpaRepository<AttendanceActivity, Long>{
	
	Optional<AttendanceActivity> findById(Long attactivityId);
	
	@Query("select attact from AttendanceActivity attact "
			+ "JOIN Attendance att ON att = attact.att_id "
    		+ "AND att._desc like :filter "
       	 )
	List<AttendanceActivity> filterAll( @Param("filter") String filter );
	
	@Query("select attact from AttendanceActivity attact "
			+ "JOIN Attendance att ON att = attact.att_id "
    		+ "AND att._desc like :filter "
       	 )
	Page<AttendanceActivity> filter( @Param("filter") String filter, Pageable pg); 
	
	@Query("select attact from AttendanceActivity attact "
			+ "JOIN Attendance att ON att = attact.att_id "
    		+ "AND att.attId = :attId "
       	 )
	Optional<AttendanceActivity> findByAttendance(@Param("attId") Long attId); 
	
	@Query("select attact from AttendanceActivity attact "
    		+ "JOIN Attendance att ON att = attact.att_id "
 			+ "WHERE att.timetable.school.owner = :owner "
    		+ "AND (att.timetable.school = :sch OR :sch is null) "
    		+ "AND (att.timetable.class_stream = :cls OR :cls is null) "
    		+ "AND (att.timetable.teacher = :tea OR :tea is null) "
    		+ "AND (att.timetable.calendar = :cal OR :cal is null) "
    		+ "AND (att._date >= :datefrom OR :datefrom is null) "
    		+ "AND (att._date <= :dateto OR :dateto is null) "
    		+ "OR att._desc like :filter "
      )	    
    List<AttendanceActivity> findFilterByTeacherSchoolgroup(
    		@Param("filter") String filter, 
    		@Param("owner") SchoolGroup owner, 
    		@Param("sch") School sch, 
    		@Param("cls") ClassStream cls, 
    		@Param("tea") Teacher tea,  
    		@Param("cal") Calendar cal,
    		@Param("datefrom") Timestamp datefrom,
    		@Param("dateto") Timestamp dateto
    	);
 	
 	@Query("select attact from AttendanceActivity attact "
    		+ "JOIN Attendance att ON att = attact.att_id "
 			+ "WHERE att.timetable.school.owner = :owner "
    		+ "AND (att.timetable.school = :sch OR :sch is null) "
    		+ "AND (att.timetable.class_stream = :cls OR :cls is null) "
    		+ "AND (att.timetable.teacher = :tea OR :tea is null) "
    		+ "AND (att.timetable.calendar = :cal OR :cal is null) "
    		+ "AND (att._date >= :datefrom OR :datefrom is null) "
    		+ "AND (att._date <= :dateto OR :dateto is null) "
      )	    
    List<AttendanceActivity> findByTeacherSchoolgroup(
    		@Param("owner") SchoolGroup owner, 
    		@Param("sch") School sch, 
    		@Param("cls") ClassStream cls, 
    		@Param("tea") Teacher tea,  
    		@Param("cal") Calendar cal,
    		@Param("datefrom") Timestamp datefrom,
    		@Param("dateto") Timestamp dateto
    	);
 	
 	
 	
 	@Query("select attact from AttendanceActivity attact "
    		+ "JOIN Attendance att ON att = attact.att_id "
 			+ "WHERE att.timetable.school.owner = :owner "
    		+ "AND (att.timetable.school = :sch OR :sch is null) "
    		+ "AND (att.timetable.class_stream = :cls OR :cls is null) "
    		+ "AND (att.timetable.teacher = :tea OR :tea is null) "
    		+ "AND (att.timetable.calendar = :cal OR :cal is null) "
    		+ "AND (att.timetable.subject = :sub OR :sub is null) "
    		+ "AND (attact.slip = :slip OR :slip is null) "
    		+ "AND (attact.action = :sta OR :sta is null) "
    		+ "AND (attact.att_id = :att OR :att is null) "
    		+ "AND (att._date >= :datefrom OR :datefrom is null) "
    		+ "AND (att._date <= :dateto OR :dateto is null) "
    	   )
    Page<AttendanceActivity> findByTeacherSchoolgroupPage( 
    		@Param("owner") SchoolGroup owner, 
    		@Param("sch") School sch, 
    		@Param("cls") ClassStream cls, 
    		@Param("tea") Teacher tea,
    		@Param("cal") Calendar cal,
    		@Param("sub") Subject sub,
    		@Param("sta") String status,
    		@Param("slip") Integer slip,
    		@Param("att") Attendance att,
    		@Param("datefrom") Timestamp datefrom,
    		@Param("dateto") Timestamp dateto,
    		Pageable pg
    );
 	
 	
 	 @Query("select attact from AttendanceActivity attact "
	    	+ "JOIN Attendance att ON att = attact.att_id "
 	 		+ "WHERE att.timetable.school.owner = :owner "
     		+ "AND (att.timetable.school = :sch OR :sch is null) "
     		+ "AND (att.timetable.class_stream = :cls OR :cls is null) "
     		+ "AND (att.timetable.teacher = :tea OR :tea is null) "
     		+ "AND (att.timetable.calendar = :cal OR :cal is null) "
     		+ "AND (att.timetable.subject = :sub OR :sub is null) "
    		+ "AND (attact.slip = :slip OR :slip is null) "
    		+ "AND (attact.action = :sta OR :sta is null) "
    		+ "AND (attact.att_id = :att OR :att is null) "
     		+ "AND (att._date >= :datefrom OR :datefrom is null) "
     		+ "AND (att._date <= :dateto OR :dateto is null) "
     		+ "OR att._desc like :filter "
        	 )	     
     Page<AttendanceActivity> findFilterByTeacherSchoolgroupPage(
     		@Param("filter") String filter, 
     		@Param("owner") SchoolGroup owner, 
     		@Param("sch") School sch, 
     		@Param("cls") ClassStream cls, 
     		@Param("tea") Teacher tea, 
     		@Param("cal") Calendar cal,
     		@Param("sub") Subject sub,
    		@Param("sta") String status,
    		@Param("slip") Integer slip,
    		@Param("att") Attendance att,
     		@Param("datefrom") Timestamp datefrom,
     		@Param("dateto") Timestamp dateto,
     		Pageable pg
     	);
 	 
 	@Query("SELECT DATE(attact.createdAt) AS createdDate, COUNT(attact) AS count FROM AttendanceActivity attact " +
		       "JOIN Attendance att ON att = attact.att_id " +
		       "WHERE DATE(attact.createdAt) >= :startDate AND DATE(attact.createdAt) <= :endDate AND attact.att_id.calendar.status = 1 AND attact.slip = 1 " +
		       "GROUP BY DATE(attact.createdAt)")
	 List<Object[]> countAttendancesActivitySlipPerDay(Timestamp startDate, Timestamp endDate);
	 
	 @Query("SELECT DATE(attact.createdAt) AS createdDate, COUNT(attact) AS count FROM AttendanceActivity attact " +
		       "JOIN Attendance att ON att = attact.att_id " +
		       "WHERE DATE(attact.createdAt) >= :startDate AND DATE(attact.createdAt) <= :endDate AND attact.att_id.calendar.status = 1 AND attact.actual is NOT NULL AND attact.ownertype = 'Principal' " +
		       "GROUP BY DATE(attact.createdAt)")
	 List<Object[]> countAttendancesActivityPrincipalAttendedPerDay(Timestamp startDate, Timestamp endDate);
    
}
