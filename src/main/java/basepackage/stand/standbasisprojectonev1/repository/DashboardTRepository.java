package basepackage.stand.standbasisprojectonev1.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import basepackage.stand.standbasisprojectonev1.model.DashboardTeacher;
import basepackage.stand.standbasisprojectonev1.model.School;

public interface DashboardTRepository extends JpaRepository<DashboardTeacher, Long> {
	  
	  @Query("select das from DashboardTeacher das where das.school = :owner")
	  Optional<DashboardTeacher> findBySchoolTeacher( @Param("owner") School ownerId );
	  
	  ////////////////////////////////////////////////////////////////////////////////
	  
	  @Query("select das from DashboardTeacher das where das.dashId = :owner")
	  Optional<DashboardTeacher> findByTeacherId ( @Param("owner") Long owner );
	  
}
