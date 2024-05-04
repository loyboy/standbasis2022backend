package basepackage.stand.standbasisprojectonev1.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

//import basepackage.stand.standbasisprojectonev1.model.Calendar;
import basepackage.stand.standbasisprojectonev1.model.DashboardSsis;
import basepackage.stand.standbasisprojectonev1.model.School;

//import basepackage.stand.standbasisprojectonev1.model.Calendar;

public interface DashboardSRepository extends JpaRepository<DashboardSsis, Long> {

	  @Query("select das from DashboardSsis das where das.school = :owner AND (das._year = :year OR :year is null) ")
	  Optional<DashboardSsis> findBySchoolSSIS( @Param("owner") School ownerId, @Param("year") Integer y  );
	  
	  ////////////////////////////////////////////////////////////////////////////////
	  @Query("select das from DashboardSsis das where das.dashId = :owner")
	  Optional<DashboardSsis> findBySsisId ( @Param("owner") Long owner );
	  
}
