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

import basepackage.stand.standbasisprojectonev1.model.AttendanceManagement;
import basepackage.stand.standbasisprojectonev1.model.Calendar;
import basepackage.stand.standbasisprojectonev1.model.ClassStream;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;
import basepackage.stand.standbasisprojectonev1.model.Subject;
import basepackage.stand.standbasisprojectonev1.model.Teacher;

@Repository
public interface AttendanceManagementRepository extends JpaRepository<AttendanceManagement, Long>{

		Optional<AttendanceManagement> findById(Long attmanageId); 
		
		@Query("select attmanage from AttendanceManagement attmanage "
				+ "JOIN Attendance att ON att = attmanage.att_id "
	    		+ "AND att.attId = :attId "
	       	 )
		Optional<AttendanceManagement> findByAttendance(@Param("attId") Long attId); 
		
		@Query("select attmanage from AttendanceManagement attmanage "
				+ "JOIN Attendance att ON att = attmanage.att_id "
	    		+ "AND att._desc like :filter "
	       	 )
		List<AttendanceManagement> filterAll( @Param("filter") String filter );
		
	 	@Query("select attmanage from AttendanceManagement attmanage "
	    		+ "JOIN Attendance att ON att = attmanage.att_id "
	 			+ "WHERE att.timetable.school.owner = :owner "
	    		+ "AND (att.timetable.school = :sch OR :sch is null) "
	    		+ "AND (att.timetable.class_stream = :cls OR :cls is null) "
	    		+ "AND (att.timetable.teacher = :tea OR :tea is null) "
	    		+ "AND (att.timetable.calendar = :cal OR :cal is null) "
	    		+ "AND (att.timetable.subject = :sub OR :sub is null) "
	    		+ "AND (att._date >= :datefrom OR :datefrom is null) "
	    		+ "AND (att._date <= :dateto OR :dateto is null) "
	    		+ "OR att._desc like :filter "
	      )	    
	    List<AttendanceManagement> findFilterByTeacherSchoolgroup(
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
	 	
	 	@Query("select attmanage from AttendanceManagement attmanage "
	    		+ "JOIN Attendance att ON att = attmanage.att_id "
	 			+ "WHERE att.timetable.school.owner = :owner "
	    		+ "AND (att.timetable.school = :sch OR :sch is null) "
	    		+ "AND (att.timetable.class_stream = :cls OR :cls is null) "
	    		+ "AND (att.timetable.teacher = :tea OR :tea is null) "
	    		+ "AND (att.timetable.calendar = :cal OR :cal is null) "
	    		+ "AND (att.timetable.subject = :sub OR :sub is null) "
	    		+ "AND (att._date >= :datefrom OR :datefrom is null) "
	    		+ "AND (att._date <= :dateto OR :dateto is null) "
	      )	    
	    List<AttendanceManagement> findByTeacherSchoolgroup(
	    		@Param("owner") SchoolGroup owner, 
	    		@Param("sch") School sch, 
	    		@Param("cls") ClassStream cls, 
	    		@Param("tea") Teacher tea,  
	    		@Param("cal") Calendar cal,
	    		@Param("sub") Subject sub,
	    		@Param("datefrom") Timestamp datefrom,
	    		@Param("dateto") Timestamp dateto
	    	);
	 	
	 	
	 	@Query("select attmanage from AttendanceManagement attmanage "
	    		+ "JOIN Attendance att ON att = attmanage.att_id "
	 			+ "WHERE att.timetable.school.owner = :owner "
	    		+ "AND (att.timetable.school = :sch OR :sch is null) "
	    		+ "AND (att.timetable.class_stream = :cls OR :cls is null) "
	    		+ "AND (att.timetable.teacher = :tea OR :tea is null) "
	    		+ "AND (att.timetable.calendar = :cal OR :cal is null) "
	    		+ "AND (att._date >= :datefrom OR :datefrom is null) "
	    		+ "AND (att._date <= :dateto OR :dateto is null) "
	    	   )
	    Page<AttendanceManagement> findByTeacherSchoolgroupPage( 
	    		@Param("owner") SchoolGroup owner, 
	    		@Param("sch") School sch, 
	    		@Param("cls") ClassStream cls, 
	    		@Param("tea") Teacher tea,
	    		@Param("cal") Calendar cal,
	    		@Param("datefrom") Timestamp datefrom,
	    		@Param("dateto") Timestamp dateto,
	    		Pageable pg
	    );
	 	
	 	
	 	 @Query("select attmanage from AttendanceManagement attmanage "
		    	+ "JOIN Attendance att ON att = attmanage.att_id "
	 	 		+ "WHERE att.timetable.school.owner = :owner "
	     		+ "AND (att.timetable.school = :sch OR :sch is null) "
	     		+ "AND (att.timetable.class_stream = :cls OR :cls is null) "
	     		+ "AND (att.timetable.teacher = :tea OR :tea is null) "
	     		+ "AND (att.timetable.calendar = :cal OR :cal is null) "
	     		+ "AND (att._date >= :datefrom OR :datefrom is null) "
	     		+ "AND (att._date <= :dateto OR :dateto is null) "
	     		+ "OR att._desc like :filter "
	        	 )	     
	     Page<AttendanceManagement> findFilterByTeacherSchoolgroupPage(
	     		@Param("filter") String filter, 
	     		@Param("owner") SchoolGroup owner, 
	     		@Param("sch") School sch, 
	     		@Param("cls") ClassStream cls, 
	     		@Param("tea") Teacher tea, 
	     		@Param("cal") Calendar cal,
	     		@Param("datefrom") Timestamp datefrom,
	     		@Param("dateto") Timestamp dateto,
	     		Pageable pg
	     	);
}
