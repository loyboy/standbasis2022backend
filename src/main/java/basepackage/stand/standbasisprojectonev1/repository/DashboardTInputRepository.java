package basepackage.stand.standbasisprojectonev1.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import basepackage.stand.standbasisprojectonev1.model.DashboardAcademicInput;
import basepackage.stand.standbasisprojectonev1.model.DashboardTeacherInput;
import basepackage.stand.standbasisprojectonev1.model.School;

public interface DashboardTInputRepository extends JpaRepository<DashboardTeacherInput, Long>{

	 @Query("select das from DashboardTeacherInput das where das.school = :owner AND (das._year = :year OR :year is null) ")
	  Optional<DashboardTeacherInput> findBySchoolTeacher( @Param("owner") School ownerId , @Param("year") Integer y );
	  
	  ////////////////////////////////////////////////////////////////////////////////
	 
	  @Query("select das from DashboardTeacherInput das where das.dashId = :owner")
	  Optional<DashboardAcademicInput> findByTeacherId ( @Param("owner") Long owner );
}
