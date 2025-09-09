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

import basepackage.stand.standbasisprojectonev1.model.Assessment;
import basepackage.stand.standbasisprojectonev1.model.Calendar;
import basepackage.stand.standbasisprojectonev1.model.ClassStream;
import basepackage.stand.standbasisprojectonev1.model.Enrollment;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;
import basepackage.stand.standbasisprojectonev1.model.Student;


@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long>{

	Optional<Assessment> findById(Long assessId);

	@Query("select assess from Assessment assess WHERE assess.lsn = :lsn ")
	List<Assessment> findByLessonnote( @Param("lsn") Lessonnote lsn);
	
	@Query( "select assess from Assessment assess "
			+ "JOIN Lessonnote lsn ON lsn = assess.lsn "
			+ "WHERE (assess.enroll = :pup) "
			+ "AND (lsn.week = :week) "
       	 )
	List<Assessment> findAssessmentByEnrolment( @Param("pup") Enrollment enrol, @Param("week") Integer week );
	
	@Query("select assess from Assessment assess "
			+ "JOIN Lessonnote lsn ON lsn = assess.lsn "
    		+ "WHERE lsn.title like :filter "
       	 )
	List<Assessment> filterAll( @Param("filter") String filter );
	
	@Query("select assess from Assessment assess "
			+ "JOIN Lessonnote lsn ON lsn = assess.lsn "
    		+ "WHERE lsn.calendar = :cal "
       	  )
	List<Assessment> findByCalendar( @Param("cal") Calendar c);
	
	@Query("select assess from Assessment assess "
			+ "JOIN Lessonnote lsn ON lsn = assess.lsn "
    		+ "WHERE (assess.enroll.student.name like :filter OR lsn.title like :filter OR lsn.subject.name like :filter ) "
       	 )
	Page<Assessment> filter( @Param("filter") String filter, Pageable pg); 
	
	@Query("select assess from Assessment assess "
			+ "JOIN Lessonnote lsn ON lsn = assess.lsn "
			+ "WHERE assess._type = :typeof " 
			+ "AND (assess.enroll.student = :pup OR :pup is null) "
			+ "AND (lsn.calendar = :cal OR :cal is null) "
			+ "AND (lsn.week = :week OR :week is null) "
       	  )
	List<Assessment> findStudentMne( @Param("week") Integer week, @Param("pup") Student pup, @Param("cal") Calendar cal, @Param("typeof")  String typeof );
	
	@Query("select assess from Assessment assess "
			+ "JOIN Lessonnote lsn ON lsn = assess.lsn "
			+ "WHERE assess._type = :typeof " 
			+ "AND (lsn.teacher = :tea OR :tea is null) "
			+ "AND (lsn.calendar = :cal OR :cal is null) "
			+ "AND (lsn.week = :week OR :week is null) "
       	  )
	List<Assessment> findTeacherMne( @Param("week") Integer week, @Param("tea") Teacher tea, @Param("cal") Calendar cal, @Param("typeof")  String typeof );
	
	@Query("select assess from Assessment assess "
    		+ "JOIN Lessonnote lsn ON lsn = assess.lsn "
    		+ "WHERE lsn.calendar.school.owner = :owner " 
    		+ "AND (lsn.calendar.school = :sch OR :sch is null) "
    		+ "AND (assess.enroll.classstream = :cls OR :cls is null) "
    		+ "AND (lsn.week = :week OR :week is null) "
    		+ "AND (lsn.calendar = :cal OR :cal is null) "
    		+ "AND (lsn.calendar.term = :term OR :term is null) "
    		+ "AND (lsn.calendar.session = :year OR :year is null) "
    		+ "AND (assess.enroll.student = :pup OR :pup is null) "
    		+ "AND (lsn.submission >= :datefrom OR :datefrom is null) "
    		+ "AND (lsn.submission <= :dateto OR :dateto is null) "
       	 )    
    List<Assessment> findByStudentSchoolgroup(
    		@Param("owner") SchoolGroup owner, 
    		@Param("sch") School sch, 
    		@Param("cls") ClassStream cls,
    		@Param("week") Integer week,
    		@Param("pup") Student pup,
    		@Param("cal") Calendar cal,
    		@Param("term") Integer term,
    		@Param("year") String year,
    		@Param("datefrom") Timestamp datefrom,
    		@Param("dateto") Timestamp dateto
    	);
	
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
    Page<Assessment> findByStudentSchoolgroupPage(
    		@Param("owner") SchoolGroup owner, 
    		@Param("sch") School sch, 
    		@Param("cls") ClassStream cls, 
    		@Param("week") Integer week,
    		@Param("pup") Student pup,
    		@Param("cal") Calendar cal,
    		@Param("datefrom") Timestamp datefrom,
    		@Param("dateto") Timestamp dateto,
    		Pageable pg
    	);
	
	@Query("select assess from Assessment assess "
    		+ "JOIN Lessonnote lsn ON lsn = assess.lsn "
    		+ "WHERE lsn.calendar.school.owner = :owner " 
    		+ "AND (lsn.calendar.school = :sch OR :sch is null) "
    		+ "AND (assess.enroll.classstream = :cls OR :cls is null) "
    		+ "AND (lsn.week = :week OR :week is null) "
    		+ "AND (lsn.calendar = :cal OR :cal is null) "
    		+ "AND (lsn.calendar.term = :term OR :term is null) "
    		+ "AND (lsn.calendar.session = :year OR :year is null) "
    		+ "AND (assess.enroll.student = :pup OR :pup is null) "
    		+ "AND (lsn.submission >= :datefrom OR :datefrom is null) "
    		+ "AND (lsn.submission <= :dateto OR :dateto is null) "
    		+ "OR lsn.title like :filter "
       	 )    
    	
		List<Assessment> findFilterByStudentSchoolgroup(
    		@Param("filter") String filter, 
    		@Param("owner") SchoolGroup owner, 
    		@Param("sch") School sch, 
    		@Param("cls") ClassStream cls, 
    		@Param("week") Integer week,
    		@Param("pup") Student pup,
    		@Param("cal") Calendar cal,
    		@Param("term") Integer term,
    		@Param("year") String year,
    		@Param("datefrom") Timestamp datefrom,
    		@Param("dateto") Timestamp dateto
    	);
	
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
    	
		Page<Assessment> findFilterByStudentSchoolgroupPage(
    		@Param("filter") String filter, 
    		@Param("owner") SchoolGroup owner, 
    		@Param("sch") School sch, 
    		@Param("cls") ClassStream cls, 
    		@Param("week") Integer week, 
    		@Param("pup") Student pup,
    		@Param("cal") Calendar cal,
    		@Param("datefrom") Timestamp datefrom,
    		@Param("dateto") Timestamp dateto,
    		Pageable pg
    	);
}
