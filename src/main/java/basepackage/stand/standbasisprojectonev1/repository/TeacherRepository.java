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

import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;
import basepackage.stand.standbasisprojectonev1.model.Teacher;
import basepackage.stand.standbasisprojectonev1.model.TimeTable;


/**
 * Created by Loy from August 2022.
 */

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

      Optional<Teacher> findById(Long teaId);
      
      List<Teacher> findBySchool(School sch);
      
      @Query("SELECT t from Teacher t where t.school.owner = :group AND t.office = 'Teacher' AND ( t.school = :sch OR :sch is null )" )
      List<Teacher> findBySchoolAndGroup(@Param("sch") School owner, @Param("group") SchoolGroup group);
    
      @Query("select t from Teacher t where t.fname like :filter " 
              + "or t.lname like :filter " 
              + "or t.bias like :filter " 
              + "or t.coursetype like :filter " 
              + "or t.email like :filter "
              + "or t.office like :filter "
              + "or t.qualification like :filter "              
         	)
      List<Teacher> filterAll(@Param("filter") String filter);
      
      @Query("select t from Teacher t where t.office = 'Teacher' AND ( t.fname like :filter " 
            + "or t.lname like :filter " 
            + "or t.bias like :filter " 
            + "or t.coursetype like :filter " 
            + "or t.email like :filter "
            + "or t.office like :filter "
            + "or t.qualification like :filter ) "
            
       		)
       Page<Teacher> filter(@Param("filter") String filter, Pageable pg);    
      
       @Query("SELECT t from Teacher t where t.school.owner = :group AND t.office = 'Teacher' AND ( t.school = :sch OR :sch is null )" )
       Page<Teacher> findBySchool( @Param("sch") School owner, @Param("group") SchoolGroup group, Pageable pg);
       
       @Query("select t from Teacher t where t.office = 'Teacher' AND ( t.fname like :filter " 
               + "or t.lname like :filter " 
               + "or t.bias like :filter " 
               + "or t.coursetype like :filter " 
               + "or t.email like :filter "
               + "or t.office like :filter "
               + "or t.qualification like :filter "
               + "and t.school.owner = :group AND ( t.school = :owner OR :owner is null ) )"
          	)       
       List<Teacher> findFilterBySchool(@Param("filter") String filter, @Param("owner") School ownerId, @Param("group") SchoolGroup group);
       
       
       @Query("select t from Teacher t where t.office = 'Teacher' AND ( t.fname like :filter " 
               + "or t.lname like :filter " 
               + "or t.bias like :filter " 
               + "or t.coursetype like :filter " 
               + "or t.email like :filter "
               + "or t.office like :filter "
               + "or t.qualification like :filter "
               + "and t.school.owner = :group AND ( t.school = :owner OR :owner is null ) ) "
          	)       
       Page<Teacher> findFilterBySchool(@Param("filter") String filter, @Param("owner") School ownerId, @Param("group") SchoolGroup group, Pageable pg);
       
       @Query("SELECT COUNT(t.teaId) from Teacher t where t.office = 'Teacher' AND t.school = :sch ")
       long countBySchool(@Param("sch") School sch);
       
       @Query("SELECT COUNT(t.teaId) from Teacher t where t.office = 'Teacher' AND t.school.owner = :group ")
       long countBySchoolGroup(@Param("group") SchoolGroup group);
       
       @Query("SELECT DATE(s.createdAt) AS createdDate, COUNT(s) AS count FROM Teacher s " +
	           "WHERE DATE(s.createdAt) >= :startDate AND DATE(s.createdAt) <= :endDate " +
	           "GROUP BY DATE(s.createdAt)")
	 List<Object[]> countTeachersCreatedPerDay(Timestamp startDate, Timestamp endDate);
        

}