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

import basepackage.stand.standbasisprojectonev1.model.Lessonnote;
import basepackage.stand.standbasisprojectonev1.model.Assessment;
import basepackage.stand.standbasisprojectonev1.model.Calendar;
import basepackage.stand.standbasisprojectonev1.model.ClassStream;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;
import basepackage.stand.standbasisprojectonev1.model.Student;
import basepackage.stand.standbasisprojectonev1.model.Subject;
import basepackage.stand.standbasisprojectonev1.model.Teacher;


@Repository
public interface LessonnoteRepository extends JpaRepository<Lessonnote, Long>{

	Optional<Lessonnote> findById(Long classId);	
	
	 @Query("select lsn from Lessonnote lsn "
	 			+ "WHERE lsn.teacher = :teaId "	    		
	    		+ "AND lsn.subject = :subId "
	    		+ "AND lsn.calendar = :calId "
	    		+ "AND lsn.class_index = :classIndex "
	    	   )
	Optional<Lessonnote> findUniqueLessonnote(
			@Param("teaId") Teacher teaId, 
			@Param("subId") Subject subId, 
			@Param("calId") Calendar calId, 
			@Param("classIndex") Integer classIndex
	);
    
    List<Lessonnote> findByCalendar(Calendar c);
    
    @Query(" select lsn from Lessonnote lsn where lsn.title like :filter ")
    Page<Lessonnote> filter( @Param("filter") String filter, Pageable pg); 
    
    @Query(" select lsn from Lessonnote lsn where lsn.title like :filter ")
    List<Lessonnote> filterAll( @Param("filter") String filter); 
    
    @Query(   "select lsn from Lessonnote lsn where lsn.teacher = :tea "
    		+ "AND ( (lsn.delaythis = 1 AND lsn.week <= :week) "
    		+ "OR (lsn.week = :week AND lsn.delaythis is null) ) "
    		+ "AND (lsn.calendar.status = 1) "
    	  )
    List<Lessonnote> findByTeacherWeekLessonnote(     		 
    		@Param("tea") Teacher tea,    		
    		@Param("week") Integer week
    );
    
    //------------------------------------------------------------------------------------------
    @Query("select lsn from Lessonnote lsn where lsn.calendar.school.owner = :owner "
    		+ "AND (lsn.calendar.school = :sch OR :sch is null) "
    		+ "AND (lsn.class_index = :cls OR :cls is null) "
    		+ "AND (lsn.week = :week OR :week is null) "
    		+ "AND (lsn.teacher = :tea OR :tea is null) "
    		+ "AND (lsn.calendar = :cal OR :cal is null) "
    		+ "AND (lsn.subject = :sub OR :sub is null) "
    		+ "AND (lsn.submission >= :datefrom OR :datefrom is null) "
    		+ "AND (lsn.submission <= :dateto OR :dateto is null) "
    	   )
    List<Lessonnote> findByTeacherSchoolgroup( 
    		@Param("owner") SchoolGroup owner, 
    		@Param("sch") School sch, 
    		@Param("cls") Integer cls, 
    		@Param("week") Integer week, 
    		@Param("tea") Teacher tea,
    		@Param("sub") Subject sub,
    		@Param("cal") Calendar cal,
    		@Param("datefrom") Timestamp datefrom,
    		@Param("dateto") Timestamp dateto
    );
   
    @Query("select lsn from Lessonnote lsn where lsn.calendar.school.owner = :owner "
    		+ "AND (lsn.calendar.school = :sch OR :sch is null) "
    		+ "AND (lsn.class_index = :cls OR :cls is null) "
    		+ "AND (lsn.subject = :sub OR :sub is null) "
    		+ "AND (lsn.week = :week OR :week is null) "
    		+ "AND (lsn.teacher = :tea OR :tea is null) "
    		+ "AND (lsn.calendar = :cal OR :cal is null) "
    		+ "AND ( (lsn.submission is null AND :status = 'queued') OR "
    		+ "(lsn.submission != null AND lsn.approval = null AND :status = 'submitted') OR "
    		+ "(lsn.resubmission != null AND :status = 're-submitted') OR "
    		+ "(lsn.revert != null AND :status = 'revert') OR "
    		+ "(lsn.approval != null AND :status = 'approved') OR "
    		+ "(:status is null) "
    		+ " ) "
    		+ "AND (lsn.submission >= :datefrom OR :datefrom is null) "
    		+ "AND (lsn.submission <= :dateto OR :dateto is null) "
    	   )
    Page<Lessonnote> findByTeacherSchoolgroupPage( 
    		@Param("owner") SchoolGroup owner, 
    		@Param("sch") School sch, 
    		@Param("cls") Integer cls, 
    		@Param("week") Integer week, 
    		@Param("tea") Teacher tea,
    		@Param("sub") Subject sub,
    		@Param("status") String status,
    		@Param("cal") Calendar cal,
    		@Param("datefrom") Timestamp datefrom,
    		@Param("dateto") Timestamp dateto,
    		Pageable pg
    );
    
    ///-----------------------------------------------------------------------
    
    @Query("select lsn from Lessonnote lsn where lsn.calendar.school.owner = :owner "
    		+ "AND (lsn.calendar.school = :sch OR :sch is null) "
    		+ "AND (lsn.class_index = :cls OR :cls is null) "
    		+ "AND (lsn.week = :week OR :week is null) "
    		+ "AND (lsn.teacher = :tea OR :tea is null) "
    		+ "AND (lsn.calendar = :cal OR :cal is null) "
    		+ "AND (lsn.subject = :sub OR :sub is null) "
    		+ "AND (lsn.submission >= :datefrom OR :datefrom is null) "
    		+ "AND (lsn.submission <= :dateto OR :dateto is null) "
    		+ "OR lsn.title like :filter "
       	 )
    
    List<Lessonnote> findFilterByTeacherSchoolgroup(
    		@Param("filter") String filter, 
    		@Param("owner") SchoolGroup owner, 
    		@Param("sch") School sch, 
    		@Param("cls") Integer cls, 
    		@Param("week") Integer week, 
    		@Param("tea") Teacher tea, 
    		@Param("sub") Subject sub,
    		@Param("cal") Calendar cal,
    		@Param("datefrom") Timestamp datefrom,
    		@Param("dateto") Timestamp dateto
    	);
    
    @Query("select lsn from Lessonnote lsn where lsn.calendar.school.owner = :owner "
    		+ "AND (lsn.calendar.school = :sch OR :sch is null) "
    		+ "AND (lsn.class_index = :cls OR :cls is null) "
    		+ "AND (lsn.week = :week OR :week is null) "
    		+ "AND (lsn.subject = :sub OR :sub is null) "
    		+ "AND (lsn.teacher = :tea OR :tea is null) "
    		+ "AND (lsn.calendar = :cal OR :cal is null) "
    		+ "AND ( (lsn.submission = null AND :status = 'queried') OR "
    		+ "(lsn.submission != null AND lsn.approval = null AND :status = 'submitted') OR "
    		+ "(lsn.resubmission != null AND :status = 're-submitted') OR "
    		+ "(lsn.revert != null AND :status = 'revert') OR "
    		+ "(lsn.approval != null AND :status = 'approved') OR "
    		+ "(:status is null) "
    		+ " ) "
    		+ "AND (lsn.submission >= :datefrom OR :datefrom is null) "
    		+ "AND (lsn.submission <= :dateto OR :dateto is null) "
    		+ "OR lsn.title like :filter "
       	 )
    
    Page<Lessonnote> findFilterByTeacherSchoolgroupPage(
    		@Param("filter") String filter, 
    		@Param("owner") SchoolGroup owner, 
    		@Param("sch") School sch, 
    		@Param("cls") Integer cls, 
    		@Param("week") Integer week, 
    		@Param("tea") Teacher tea, 
    		@Param("sub") Subject sub,
    		@Param("status") String status,
    		@Param("cal") Calendar cal,
    		@Param("datefrom") Timestamp datefrom,
    		@Param("dateto") Timestamp dateto,
    		Pageable pg
    	);
    
    //-----------------------------------------------------------------------------------
    
    @Query("select assess from Assessment assess "
    		+ "JOIN Lessonnote lsn ON lsn = assess.lsn "
    		+ "WHERE lsn.calendar.school.owner = :owner " 
    		+ "AND (lsn.calendar.school = :sch OR :sch is null) "
    		+ "AND (assess.enroll.classstream = :cls OR :cls is null) "
    		+ "AND (lsn.week = :week OR :week is null) "
    		+ "AND (lsn.calendar = :cal OR :cal is null) "
    		+ "AND (assess.enroll.student = :pup OR :pup is null) "
    		+ "AND (lsn.submission >= :datefrom OR :datefrom is null) "
    		+ "AND (lsn.submission <= :dateto OR :dateto is null) "
       	 )
    
    List<Lessonnote> findByStudentSchoolgroup(
    		@Param("owner") SchoolGroup owner, 
    		@Param("sch") School sch, 
    		@Param("cls") ClassStream cls, 
    		@Param("week") Integer week, 
    		@Param("pup") Student pup,
    		@Param("cal") Calendar cal,
    		@Param("datefrom") Timestamp datefrom,
    		@Param("dateto") Timestamp dateto
    	);
    
    @Query("select assess from Assessment assess "
    		+ "JOIN Lessonnote lsn ON lsn = assess.lsn "
    		+ "WHERE lsn.calendar.school.owner = :owner " 
    		+ "AND (lsn.calendar.school = :sch OR :sch is null) "
    		+ "AND (assess.enroll.classstream = :cls OR :cls is null) "    		
    		+ "AND (assess.enroll.student = :pup OR :pup is null) "
    		+ "AND (lsn.teacher = :tea OR :tea is null) "
    		+ "AND (lsn.calendar = :cal OR :cal is null) "
    		+ "AND (assess.lsn = :lsn OR :lsn is null) "
    		+ "AND ( (assess.score > 80 AND assess.score <= 99 AND :score = 99) OR "
    		+ "(assess.score > 60 AND assess.score <= 80 AND :score = 80) OR "
    		+ "(assess.score > 40 AND assess.score <= 60 AND :score = 60) OR "
    		+ "(assess.score < 40 AND :score = 40) OR "
    		+ "(:score is null) "
    		+ " ) "
    		+ "AND (assess._type = :type OR :type is null) "
    		+ "AND (lsn.submission >= :datefrom OR :datefrom is null) "
    		+ "AND (lsn.submission <= :dateto OR :dateto is null) "
       	 )
    
    Page<Assessment> findByStudentSchoolgroupPage(
    		@Param("owner") SchoolGroup owner, 
    		@Param("sch") School sch, 
    		@Param("cls") ClassStream cls,
    		@Param("pup") Student pup,
    		@Param("tea") Teacher tea,    		
    		@Param("cal") Calendar cal,
    		@Param("lsn") Lessonnote lsn,
    		@Param("score") Integer score,
    		@Param("type") String type,
    		@Param("datefrom") Timestamp datefrom,
    		@Param("dateto") Timestamp dateto,
    		Pageable pg
    	);
    
    //------------------------------------------------------------
    
    @Query("select assess from Assessment assess "
    		+ "JOIN Lessonnote lsn ON lsn = assess.lsn "
    		+ "WHERE lsn.calendar.school.owner = :owner " 
    		+ "AND (lsn.calendar.school = :sch OR :sch is null) "
    		+ "AND (assess.enroll.classstream = :cls OR :cls is null) "
    		+ "AND (lsn.week = :week OR :week is null) "
    		+ "AND (lsn.calendar = :cal OR :cal is null) "
    		+ "AND (assess.enroll.student = :pup OR :pup is null) "
    		+ "AND (lsn.submission >= :datefrom OR :datefrom is null) "
    		+ "AND (lsn.submission <= :dateto OR :dateto is null) "
    		+ "OR lsn.title like :filter "
       	 )    
    List<Lessonnote> findFilterByStudentSchoolgroup(
    		@Param("filter") String filter, 
    		@Param("owner") SchoolGroup owner, 
    		@Param("sch") School sch, 
    		@Param("cls") ClassStream cls, 
    		@Param("week") Integer week, 
    		@Param("pup") Student pup,
    		@Param("cal") Calendar cal,
    		@Param("datefrom") Timestamp datefrom,
    		@Param("dateto") Timestamp dateto
    	);
    
    @Query("select assess from Assessment assess "
    		+ "JOIN Lessonnote lsn ON lsn = assess.lsn "
    		+ "WHERE lsn.calendar.school.owner = :owner " 
    		+ "AND (lsn.calendar.school = :sch OR :sch is null) "
    		+ "AND (assess.enroll.classstream = :cls OR :cls is null) "    		
    		+ "AND (assess.enroll.student = :pup OR :pup is null) "
    		+ "AND (lsn.teacher = :tea OR :tea is null) "
    		+ "AND (lsn.calendar = :cal OR :cal is null) "
    		+ "AND (assess.lsn = :lsn OR :lsn is null) "
    		+ "AND ( (assess.score > 80 AND assess.score <= 99 AND :score = 99) OR "
    		+ "(assess.score > 60 AND assess.score <= 80 AND :score = 80) OR "
    		+ "(assess.score > 40 AND assess.score <= 60 AND :score = 60) OR "
    		+ "(assess.score < 40 AND :score = 40) OR "
    		+ "(:score is null) "
    		+ " ) "
    		+ "AND (assess._type = :type OR :type is null) "
    		+ "AND (lsn.submission >= :datefrom OR :datefrom is null) "
    		+ "AND (lsn.submission <= :dateto OR :dateto is null) "
    		+ "OR lsn.title like :filter "
       	 )
    
    Page<Assessment> findFilterByStudentSchoolgroupPage(
    		@Param("filter") String filter, 
    		@Param("owner") SchoolGroup owner, 
    		@Param("sch") School sch, 
    		@Param("cls") ClassStream cls,
    		@Param("pup") Student pup,
    		@Param("tea") Teacher tea,    		
    		@Param("cal") Calendar cal,
    		@Param("lsn") Lessonnote lsn,
    		@Param("score") Integer score,
    		@Param("type") String type,
    		@Param("datefrom") Timestamp datefrom,
    		@Param("dateto") Timestamp dateto,
    		Pageable pg
    	);

}
