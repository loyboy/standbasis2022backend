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
import basepackage.stand.standbasisprojectonev1.model.Lessonnote;
import basepackage.stand.standbasisprojectonev1.model.LessonnoteManagement;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;
import basepackage.stand.standbasisprojectonev1.model.Subject;
import basepackage.stand.standbasisprojectonev1.model.Teacher;

@Repository
public interface LessonnoteManagementRepository extends JpaRepository<LessonnoteManagement, Long>{

		Optional<LessonnoteManagement> findById(Long lsnmanageId);
		
		@Query("select lsnmanage from LessonnoteManagement lsnmanage "
				+ "WHERE lsnmanage.lsn_id = :lsn "
	       	 )
		Optional<LessonnoteManagement> findByLessonnote( @Param("lsn") Lessonnote lsn);
		
		@Query("select lsnmanage from LessonnoteManagement lsnmanage "
				+ "JOIN Lessonnote lsn ON lsn = lsnmanage.lsn_id "
	    		+ "AND lsn.title like :filter "
	       	 )
		List<LessonnoteManagement> filterAll( @Param("filter") String filter );
		
		@Query("select lsnmanage from LessonnoteManagement lsnmanage "
				+ "JOIN Lessonnote lsn ON lsn = lsnmanage.lsn_id "
	    		+ "AND lsn.title like :filter "
	       	 )
		Page<LessonnoteManagement> filter( @Param("filter") String filter, Pageable pg); 
		
		@Query(
				 "select lsnmanage from LessonnoteManagement lsnmanage "
					+ "JOIN Lessonnote lsn ON lsn = lsnmanage.lsn_id "
					+ "where lsn.calendar.school.owner = :owner "
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
		    		+ "AND ( DATE(lsn.submission) >= :datefrom OR :datefrom is null) "
		    		+ "AND ( DATE(lsn.submission) <= :dateto OR :dateto is null) "
		    	   )
		    Page<LessonnoteManagement> findByTeacherSchoolgroupPage( 
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
		 
		 @Query(
				 "select lsnmanage from LessonnoteManagement lsnmanage "
					+ "JOIN Lessonnote lsn ON lsn = lsnmanage.lsn_id "
					+ "where lsn.calendar.school.owner = :owner "
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
		    		+ "AND ( DATE(lsn.submission) >= :datefrom OR :datefrom is null) "
		    		+ "AND ( DATE(lsn.submission) <= :dateto OR :dateto is null) "
		    		+ "OR lsn.title like :filter "
		    	   )
		    Page<LessonnoteManagement> findFilterByTeacherSchoolgroupPage( 
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
	
		@Query("select lsnmanage from LessonnoteManagement lsnmanage "
				+ "JOIN Lessonnote lsn ON lsn = lsnmanage.lsn_id "
				+ "where lsn.calendar.school.owner = :owner "
	    		+ "AND (lsn.calendar.school = :sch OR :sch is null) "
	    		+ "AND (lsn.class_index = :cls OR :cls is null) "
	    		+ "AND (lsn.teacher = :tea OR :tea is null) "
	    		+ "AND (lsn.calendar.term = :term OR :term is null) "
	    		+ "AND (lsn.calendar.session = :year OR :year is null) "
	    		+ "AND (lsn.subject = :sub OR :sub is null) "
	    		+ "AND (lsn.week = :week OR :week is null) "
	    		+ "AND (lsn.submission >= :datefrom OR :datefrom is null) "
	    		+ "AND (lsn.submission <= :dateto OR :dateto is null) "
	    	   )
	    List<LessonnoteManagement> findByTeacherSchoolgroup( 
	    		@Param("owner") SchoolGroup owner, 
	    		@Param("sch") School sch, 
	    		@Param("cls") Integer cls, 
	    		@Param("week") Integer week, 
	    		@Param("tea") Teacher tea,
	    		@Param("sub") Subject sub,
	    		@Param("term") Integer term,
	    		@Param("year") String year,
	    		@Param("datefrom") Timestamp datefrom,
	    		@Param("dateto") Timestamp dateto
	    );
	
		@Query("select lsnmanage from LessonnoteManagement lsnmanage "
				+ "JOIN Lessonnote lsn ON lsn = lsnmanage.lsn_id "
				+ "where lsn.calendar.school.owner = :owner "
	    		+ "AND (lsn.calendar.school = :sch OR :sch is null) "
	    		+ "AND (lsn.class_index = :cls OR :cls is null) "
	    		+ "AND (lsn.teacher = :tea OR :tea is null) "
	    		+ "AND (lsn.calendar.term = :term OR :term is null) "
	    		+ "AND (lsn.calendar.session = :year OR :year is null) "
	    		+ "AND (lsn.subject = :sub OR :sub is null) "
	    		+ "AND (lsn.week = :week OR :week is null) "
	    		+ "AND (lsn.submission >= :datefrom OR :datefrom is null) "
	    		+ "AND (lsn.submission <= :dateto OR :dateto is null) "
	    		+ "OR lsn.title like :filter "
	       	 )
	    
	    List<LessonnoteManagement> findFilterByTeacherSchoolgroup(
	    		@Param("filter") String filter, 
	    		@Param("owner") SchoolGroup owner, 
	    		@Param("sch") School sch, 
	    		@Param("cls") Integer cls, 
	    		@Param("week") Integer week, 
	    		@Param("tea") Teacher tea,  
	    		@Param("sub") Subject sub,
	    		@Param("term") Integer term,
	    		@Param("year") String year,
	    		@Param("datefrom") Timestamp datefrom,
	    		@Param("dateto") Timestamp dateto
	    );
		
		@Query("SELECT DATE(lsnmanage.updatedAt) AS createdDate, COUNT(lsnmanage) AS count FROM LessonnoteManagement lsnmanage " +
			       "JOIN Lessonnote lsn ON lsn = lsnmanage.lsn_id " +
			       "WHERE DATE(lsnmanage.updatedAt) >= :startDate AND DATE(lsnmanage.updatedAt) <= :endDate AND lsnmanage.lsn_id.calendar.status = 1 "
			     + "AND lsnmanage.management < 50 " +
			       "GROUP BY DATE(lsnmanage.updatedAt)")
		List<Object[]> countLessonnotesManagementBasicPerDay(Timestamp startDate, Timestamp endDate);
	 
	 	
	    
}
