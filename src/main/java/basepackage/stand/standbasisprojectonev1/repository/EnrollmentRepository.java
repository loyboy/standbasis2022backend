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
import basepackage.stand.standbasisprojectonev1.model.Enrollment;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;


@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    	Optional<Enrollment> findById(Long enrolId);
    
    	@Query("select e from Enrollment e " 
                + "JOIN ClassStream cs ON cs = e.classstream " 
                + "WHERE cs.clsId = :classId AND cs.status = 1 AND e.status = 1 "            
           	  )
        List<Enrollment> findByClassId( @Param("classId") Long classId );
    	
    	@Query("select e from Enrollment e " 
            + "JOIN ClassStream cs ON cs = e.classstream " 
            + "WHERE cs.class_index = :index AND cs.status = 1 AND e.status = 1 AND e.calendar.school = :sch "            
       	  )
    	List<Enrollment> findByClassIndex( @Param("index") Integer classIndex, @Param("sch") School sch );
    	
    	@Query( " select e from Enrollment e "                
                + "WHERE e.calendar.school = :sch AND "
                + "e.calendar = :cal"
           	  )
        List<Enrollment> findBySchool( @Param("sch") School sch, @Param("cal") Calendar cal );
    	
    	@Query("select e from Enrollment e " 
                + "JOIN Calendar cs ON cs = e.calendar " 
                + "WHERE cs = :cal AND cs.status = 1 AND e.status = 1 "            
           	  )
        List<Enrollment> findByCalendar( @Param("cal") Calendar cal );
    
    	@Query("select e from Enrollment e " 
            + "JOIN ClassStream cs ON cs = e.classstream " 
            + "JOIN Student st ON st = e.student " 
            + "WHERE cs.title like :filter or st.name like :filter "            
       	  )
       Page<Enrollment> filter(@Param("filter") String filter, Pageable pg);    
      
    	@Query("select e from Enrollment e "
          + "WHERE (e.classstream.school = :owner OR :owner = null) " 
    	  + "OR (e.classstream.school.owner = :group OR :group = null) "
       	   )
       Page<Enrollment> findBySchool( @Param("owner") School owner,@Param("group") SchoolGroup group, Pageable pg);
       
       @Query("select e from Enrollment e " 
               + "JOIN ClassStream cs ON cs = e.classstream " 
               + "JOIN Student st ON st = e.student " 
               + "WHERE cs.title like :filter or st.name like :filter "
               + "and ( e.classstream.school = :owner OR :owner = null) OR ( e.classstream.school.owner = :group OR :group = null ) "
          	 )
       
       Page<Enrollment> findFilterBySchool(@Param("filter") String filter, @Param("owner") School ownerId, @Param("group") SchoolGroup group, Pageable pg);

}
