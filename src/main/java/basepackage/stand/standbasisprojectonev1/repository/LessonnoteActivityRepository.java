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
import basepackage.stand.standbasisprojectonev1.model.Lessonnote;
import basepackage.stand.standbasisprojectonev1.model.LessonnoteActivity;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;
import basepackage.stand.standbasisprojectonev1.model.Subject;
import basepackage.stand.standbasisprojectonev1.model.Teacher;

@Repository
public interface LessonnoteActivityRepository extends JpaRepository<LessonnoteActivity, Long>{

	Optional<LessonnoteActivity> findById(Long lsnactivityId);
	
	@Query("select lsnactivity from LessonnoteActivity lsnactivity "
			+ "JOIN Lessonnote lsn ON lsn = lsnactivity.lsn_id "
    		+ "AND lsn.title like :filter "
       	 )
	List<LessonnoteActivity> filterAll( @Param("filter") String filter );
	
	@Query("select lsnactivity from LessonnoteActivity lsnactivity "
			+ "JOIN Lessonnote lsn ON lsn = lsnactivity.lsn_id "    		
    		+ "WHERE lsnactivity.ownertype = 'Principal' "
    		+ "AND lsn.lessonnoteId = :lsnId "
			+ "ORDER BY lsnactivity.lsnactId DESC "
			
       	  )
	List<LessonnoteActivity> findByLessonnote(@Param("lsnId") Long lsnId); 
	
	@Query("select lsnactivity from LessonnoteActivity lsnactivity "
			+ "JOIN Lessonnote lsn ON lsn = lsnactivity.lsn_id "
    		+ "WHERE lsn.lessonnoteId = :lsnId "
    		+ "AND lsnactivity.ownertype = 'Teacher' "
			+ "ORDER BY lsnactivity.lsnactId DESC "
			
       	  )
	List<LessonnoteActivity> findByLessonnoteForTeacher(@Param("lsnId") Long lsnId); 
	
	@Query("select lsnactivity from LessonnoteActivity lsnactivity "
			+ "JOIN Lessonnote lsn ON lsn = lsnactivity.lsn_id "
    		+ "AND lsn.title like :filter "
       	 )
	Page<LessonnoteActivity> filter( @Param("filter") String filter, Pageable pg );
	
	@Query("select lsnactivity from LessonnoteActivity lsnactivity "
			+ "JOIN Lessonnote lsn ON lsn = lsnactivity.lsn_id "
			+ "where lsn.calendar.school.owner = :owner "
    		+ "AND (lsn.calendar.school = :sch OR :sch is null) "
    		+ "AND (lsn.class_index = :cls OR :cls is null) "
    		+ "AND (lsn.teacher = :tea OR :tea is null) "
    		+ "AND (lsn.calendar = :cal OR :cal is null) "
    		+ "AND (lsn.submission >= :datefrom OR :datefrom is null) "
    		+ "AND (lsn.submission <= :dateto OR :dateto is null) "
    	   )
    List<LessonnoteActivity> findByTeacherSchoolgroup( 
    		@Param("owner") SchoolGroup owner, 
    		@Param("sch") School sch, 
    		@Param("cls") Integer cls, 
    		@Param("tea") Teacher tea,
    		@Param("cal") Calendar cal,
    		@Param("datefrom") Timestamp datefrom,
    		@Param("dateto") Timestamp dateto
    );
	
	@Query("select lsnactivity from LessonnoteActivity lsnactivity "
			+ "JOIN Lessonnote lsn ON lsn = lsnactivity.lsn_id "
			+ "where lsn.calendar.school.owner = :owner "
    		+ "AND (lsn.calendar.school = :sch OR :sch is null) "
    		+ "AND (lsn.class_index = :cls OR :cls is null) "
    		+ "AND (lsn.teacher = :tea OR :tea is null) "
    		+ "AND (lsn.calendar = :cal OR :cal is null) "
    		
    		+ "AND (lsn.subject = :sub OR :sub is null) "
    		+ "AND (lsnactivity.lsn_id = :lsn OR :lsn is null) "
    		+ "AND (lsnactivity.slip = :slip OR :slip is null) "
    		+ "AND (lsnactivity.action = :sta OR :sta is null) "    		
    		
    		+ "AND (lsn.submission >= :datefrom OR :datefrom is null) "
    		+ "AND (lsn.submission <= :dateto OR :dateto is null) "
    	   )
    Page<LessonnoteActivity> findByTeacherSchoolgroupPage( 
    		@Param("owner") SchoolGroup owner, 
    		@Param("sch") School sch, 
    		@Param("cls") Integer cls, 
    		@Param("tea") Teacher tea,
    		@Param("cal") Calendar cal,
    		@Param("sub") Subject sub,
    		@Param("sta") String status,
    		@Param("slip") Integer slip,
    		@Param("lsn") Lessonnote lsn,
    		@Param("datefrom") Timestamp datefrom,
    		@Param("dateto") Timestamp dateto,
    		Pageable pg
    );

	@Query("select lsnactivity from LessonnoteActivity lsnactivity "
			+ "JOIN Lessonnote lsn ON lsn = lsnactivity.lsn_id "
			+ "where lsn.calendar.school.owner = :owner "
    		+ "AND (lsn.calendar.school = :sch OR :sch is null) "
    		+ "AND (lsn.class_index = :cls OR :cls is null) "
    		+ "AND (lsn.teacher = :tea OR :tea is null) "
    		+ "AND (lsn.calendar = :cal OR :cal is null) "
    		+ "AND (lsn.submission >= :datefrom OR :datefrom is null) "
    		+ "AND (lsn.submission <= :dateto OR :dateto is null) "
    		+ "OR lsn.title like :filter "
       	 )
    
    List<LessonnoteActivity> findFilterByTeacherSchoolgroup(
    		@Param("filter") String filter, 
    		@Param("owner") SchoolGroup owner, 
    		@Param("sch") School sch, 
    		@Param("cls") Integer cls, 
    		@Param("tea") Teacher tea,  
    		@Param("cal") Calendar cal,
    		@Param("datefrom") Timestamp datefrom,
    		@Param("dateto") Timestamp dateto
    );
	
	@Query("select lsnactivity from LessonnoteActivity lsnactivity "
			+ "JOIN Lessonnote lsn ON lsn = lsnactivity.lsn_id "
			+ "where lsn.calendar.school.owner = :owner "
    		+ "AND (lsn.calendar.school = :sch OR :sch is null) "
    		+ "AND (lsn.class_index = :cls OR :cls is null) "
    		+ "AND (lsn.teacher = :tea OR :tea is null) "
    		+ "AND (lsn.calendar = :cal OR :cal is null) "
    		
			+ "AND (lsn.subject = :sub OR :sub is null) "
			+ "AND (lsnactivity.lsn_id = :lsn OR :lsn is null) "
			+ "AND (lsnactivity.slip = :slip OR :slip is null) "
			+ "AND (lsnactivity.action = :sta OR :sta is null) "  
			
    		+ "AND (lsn.submission >= :datefrom OR :datefrom is null) "
    		+ "AND (lsn.submission <= :dateto OR :dateto is null) "
    		+ "OR lsn.title like :filter "
       	 )
    
    Page<LessonnoteActivity> findFilterByTeacherSchoolgroupPage(
    		@Param("filter") String filter, 
    		@Param("owner") SchoolGroup owner, 
    		@Param("sch") School sch, 
    		@Param("cls") Integer cls, 
    		@Param("tea") Teacher tea,  
    		@Param("cal") Calendar cal,
    		@Param("sub") Subject sub,
    		@Param("sta") String status,
    		@Param("slip") Integer slip,
    		@Param("lsn") Lessonnote lsn,
    		@Param("datefrom") Timestamp datefrom,
    		@Param("dateto") Timestamp dateto,
    		Pageable pg
    );
	
	@Query("SELECT DATE(lsnact.createdAt) AS createdDate, COUNT(lsnact) AS count FROM LessonnoteActivity lsnact " +
		       "JOIN Lessonnote lsn ON lsn = lsnact.lsn_id " +
		       "WHERE DATE(lsnact.createdAt) >= :startDate AND DATE(lsnact.createdAt) <= :endDate AND lsnact.lsn_id.calendar.status = 1 AND lsnact.slip = 1 " +
		       "GROUP BY DATE(lsnact.createdAt)")
	 List<Object[]> countLessonnotesActivitySlipPerDay(Timestamp startDate, Timestamp endDate);
	 
	 @Query("SELECT DATE(lsnact.createdAt) AS createdDate, COUNT(lsnact) AS count FROM LessonnoteActivity lsnact " +
		       "JOIN Lessonnote lsn ON lsn = lsnact.lsn_id " +
		       "WHERE DATE(lsnact.createdAt) >= :startDate AND DATE(lsnact.createdAt) <= :endDate AND lsnact.lsn_id.calendar.status = 1 AND lsnact.actual is NOT NULL AND lsnact.ownertype = 'Principal' " +
		       "GROUP BY DATE(lsnact.createdAt)")
	 List<Object[]> countLessonnotesActivityPrincipalAttendedPerDay(Timestamp startDate, Timestamp endDate);
}
