package basepackage.stand.standbasisprojectonev1.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import basepackage.stand.standbasisprojectonev1.model.DashboardAcademic;
import basepackage.stand.standbasisprojectonev1.model.DashboardCurriculum;
//import basepackage.stand.standbasisprojectonev1.model.Calendar;
import basepackage.stand.standbasisprojectonev1.model.DashboardSsis;
import basepackage.stand.standbasisprojectonev1.model.DashboardTeacher;
import basepackage.stand.standbasisprojectonev1.model.School;

public interface DashboardCRepository extends JpaRepository<DashboardCurriculum, Long> {
	  
	  @Query("select das from DashboardCurriculum das where das.school = :owner")
	  Optional<DashboardCurriculum> findBySchoolCurriculum( @Param("owner") School ownerId );
	  
	  ////////////////////////////////////////////////////////////////////////////////
	 
	  @Query("select das from DashboardCurriculum das where das.dashId = :owner")
	  Optional<DashboardCurriculum> findByCurriculumId ( @Param("owner") Long owner );
	 
}
