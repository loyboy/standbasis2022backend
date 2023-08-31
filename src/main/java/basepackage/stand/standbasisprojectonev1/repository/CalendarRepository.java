package basepackage.stand.standbasisprojectonev1.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import basepackage.stand.standbasisprojectonev1.model.Calendar;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, Long>{
	 	  Optional<Calendar> findById(Long calId);
	 	  
	 	  @Query("select cal from Calendar cal where " 
	               + "cal.status = :status "
	               + "and cal.school = :owner"
	          	 )
	 	  Optional<Calendar> findByStatus(@Param("status") Integer status, @Param("owner") School ownerId);
	 	  
	 	 @Query("select cal from Calendar cal where " 
	               + "cal.status = 1 "
	          	 )
	 	  List<Calendar> findByActive();
	    
	      List<Calendar> findBySchool(School sch);
	    
	      @Query("select cal from Calendar cal where cal.holiday like :filter " 
	               + "or cal.session like :filter "            
	       	 )
	       Page<Calendar> filter(@Param("filter") String filter, Pageable pg);
	      
	      @Query("select cal from Calendar cal where cal.holiday like :filter " 
	               + "or cal.session like :filter "            
	       	 )
	       List<Calendar> filterAll(@Param("filter") String filter);
	      
	       @Query("select cal from Calendar cal where (cal.school.owner = :group OR :group is null) AND ( cal.school = :owner OR :owner is null ) " )
	       Page<Calendar> findBySchoolPage( @Param("owner") School owner, @Param("group") SchoolGroup group, Pageable pg);
	       
	       @Query("select cal from Calendar cal where ( cal.holiday like :filter " 
	               + "OR cal.session like :filter ) "
	               + "AND (cal.school.owner = :group OR :group is null) AND ( cal.school = :owner OR :owner is null ) "
	          	 )	       
	       Page<Calendar> findFilterBySchool(@Param("filter") String filter, @Param("owner") School ownerId, @Param("group") SchoolGroup group, Pageable pg);
	       
	       @Query("select cal from Calendar cal where (cal.school.owner = :group OR :group is null) AND ( cal.school = :owner OR :owner is null ) " )
	       List<Calendar> findBySchoolPage( @Param("owner") School owner, @Param("group") SchoolGroup group);
	       
	       @Query("select cal from Calendar cal where ( cal.holiday like :filter " 
	               + "OR cal.session like :filter ) "
	               + "AND (cal.school.owner = :group OR :group is null) AND ( cal.school = :owner OR :owner is null )  "
	          	 )	       
	       List<Calendar> findFilterBySchool(@Param("filter") String filter, @Param("owner") School ownerId, @Param("group") SchoolGroup group );

}
		  