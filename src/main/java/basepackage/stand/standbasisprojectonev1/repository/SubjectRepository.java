package basepackage.stand.standbasisprojectonev1.repository;

import basepackage.stand.standbasisprojectonev1.model.ClassStream;
import basepackage.stand.standbasisprojectonev1.model.Subject;
import basepackage.stand.standbasisprojectonev1.model.Teacher;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by Loy from August 2022.
 */

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    Optional<Subject> findById(Long subId);
    
    @Query("select DISTINCT(su) from Subject su JOIN TimeTable tt ON su.subId = tt.subject.subId "
	   		+ "where tt.teacher = :tea "          
       )
    List<Subject> findByTeacher(@Param("tea") Teacher tea); 

}
