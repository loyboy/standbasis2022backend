package basepackage.stand.standbasisprojectonev1.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import basepackage.stand.standbasisprojectonev1.model.DashboardAcademic;
import basepackage.stand.standbasisprojectonev1.model.School;


public interface DashboardARepository extends JpaRepository<DashboardAcademic, Long> {
	  
	  @Query("select das from DashboardAcademic das where das.school = :owner")
	  Optional<DashboardAcademic> findBySchoolAcademic( @Param("owner") School ownerId );
	  
	  ////////////////////////////////////////////////////////////////////////////////
	 
	  @Query("select das from DashboardAcademic das where das.dashId = :owner")
	  Optional<DashboardAcademic> findByAcademicId ( @Param("owner") Long owner );
}
