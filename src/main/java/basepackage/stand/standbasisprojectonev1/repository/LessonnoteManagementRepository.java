package basepackage.stand.standbasisprojectonev1.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
				+ "where lsn.calendar.school.owner = :owner "
	    		+ "AND (lsn.calendar.school = :sch OR :sch is null) "
	    		+ "AND (lsn.class_index = :cls OR :cls is null) "
	    		+ "AND (lsn.teacher = :tea OR :tea is null) "
	    		+ "AND (lsn.calendar = :cal OR :cal is null) "
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
	    		@Param("cal") Calendar cal,
	    		@Param("datefrom") Timestamp datefrom,
	    		@Param("dateto") Timestamp dateto
	    );
	
		@Query("select lsnmanage from LessonnoteManagement lsnmanage "
				+ "JOIN Lessonnote lsn ON lsn = lsnmanage.lsn_id "
				+ "where lsn.calendar.school.owner = :owner "
	    		+ "AND (lsn.calendar.school = :sch OR :sch is null) "
	    		+ "AND (lsn.class_index = :cls OR :cls is null) "
	    		+ "AND (lsn.teacher = :tea OR :tea is null) "
	    		+ "AND (lsn.calendar = :cal OR :cal is null) "
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
	    		@Param("cal") Calendar cal,
	    		@Param("datefrom") Timestamp datefrom,
	    		@Param("dateto") Timestamp dateto
	    );
	 
	 	
	    
}
