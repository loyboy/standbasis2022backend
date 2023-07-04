package basepackage.stand.standbasisprojectonev1.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import basepackage.stand.standbasisprojectonev1.model.TimeTable;
import basepackage.stand.standbasisprojectonev1.model.Calendar;
import basepackage.stand.standbasisprojectonev1.model.ClassStream;
import basepackage.stand.standbasisprojectonev1.model.Enrollment;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;
import basepackage.stand.standbasisprojectonev1.model.Teacher;


/**
 * Created by Loy from August 2022.
 */

@Repository
public interface TimetableRepository extends JpaRepository<TimeTable, Long> {

    Optional<TimeTable> findById(Long timetableId);
    
    List<TimeTable> findBySchool(School sch);
    
    @Query("select tt from TimeTable tt "
    		+ "where tt.teacher = :tea " 
            + "and tt.calendar = :cal ")
    List<TimeTable> findByClassTaught(@Param("tea") Teacher tea, @Param("cal") Calendar cal);
    
    @Query("select tt from TimeTable tt "
    		+ "where tt.class_stream = :cls " 
            + "and tt.calendar = :cal ")
    List<TimeTable> findClassOffered(@Param("cls") ClassStream cls, @Param("cal") Calendar cal);
    
    @Query("select tt from TimeTable tt WHERE tt.calendar.status = :status ")
    List<TimeTable> findByActiveCalendar(@Param("status") Integer status );
    
    @Query("select tt from TimeTable tt where tt.tea_name like :filter " 
             + "or tt.sub_name like :filter "
             + "or tt.class_name like :filter "
     	 )
     Page<TimeTable> filter(@Param("filter") String filter, Pageable pg);    
    
     @Query(" SELECT tt from TimeTable tt where (tt.teacher = :tea OR :tea is null) " 
    		 + "AND (tt.school = :owner OR :owner is null) "
    		 + "AND (tt.school.owner = :group OR :group is null) "
    		)
     Page<TimeTable> findBySchoolAndTeacherPage( @Param("owner") School owner, @Param("group") SchoolGroup group, @Param("tea") Teacher tea, Pageable pg );
     
     @Query("select tt from TimeTable tt " 
             + "JOIN Calendar cs ON cs = tt.calendar " 
             + "WHERE cs = :cal AND cs.status = 1 AND tt.status = 1 "            
    	   )
     List<TimeTable> findByCalendar( @Param("cal") Calendar cal );
     
     //@Param("owner") School owner, @Param("group") SchoolGroup group,
     // + "OR (tt.school = :owner OR :owner is null) "
	 // + "OR (tt.school.owner = :group OR :group is null) "  
     
     @Query("select tt from TimeTable tt where tt.tea_name like :filter " 
             + "or tt.sub_name like :filter "
             + "or tt.class_name like :filter "
             + "or tt.school.name like :filter "
             + "AND ( (tt.school.owner = :group OR :group is null) AND (tt.school = :owner OR :owner is null) AND (tt.teacher = :tea OR :tea is null) ) "
           )     
     Page<TimeTable> findFilterBySchool(@Param("filter") String filter, @Param("owner") School ownerId, @Param("group") SchoolGroup group, @Param("tea") Teacher tea, Pageable pg);

     @Query( " SELECT COUNT(t.id) from TimeTable t "    		
    			+ "WHERE t.teacher = :tea "    			
 		 )
    long countByTimetable(@Param("tea") Teacher tea );
}