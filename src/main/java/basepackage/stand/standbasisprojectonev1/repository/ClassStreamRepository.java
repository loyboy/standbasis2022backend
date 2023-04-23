package basepackage.stand.standbasisprojectonev1.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import basepackage.stand.standbasisprojectonev1.model.ClassStream;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.Teacher;

/**
 * Created by Loy from August 2022.
 */

@Repository
public interface ClassStreamRepository extends JpaRepository<ClassStream, Long> {

	   Optional<ClassStream> findById(Long classId);
	    
	   List<ClassStream> findBySchool(School sch);
	   
	   @Query("select DISTINCT(cs) from ClassStream cs INNER JOIN TimeTable tt ON cs.clsId = tt.class_stream.clsId "
	   		+ "where tt.teacher = :tea "          
       )
       List<ClassStream> findByTeacher(@Param("tea") Teacher tea); 
    
       @Query("select cs from ClassStream cs where cs.title like :filter " 
               + "or cs.ext like :filter "            
       	 )
       Page<ClassStream> filter(@Param("filter") String filter, Pageable pg);    
      
       @Query("select cs from ClassStream cs where cs.school = :owner " )
       Page<ClassStream> findBySchoolPage( @Param("owner") School owner, Pageable pg);
       
       @Query("select cs from ClassStream cs where cs.title like :filter " 
               + "or cs.ext like :filter " 
               + "and cs.school = :owner "
          	 )
       
       Page<ClassStream> findFilterBySchoolPage(@Param("filter") String filter, @Param("owner") School ownerId, Pageable pg);
       
       @Query("SELECT COUNT(cs.id) from ClassStream cs where cs.school = :sch ")
       long countBySchool(@Param("sch") School sch);

}
