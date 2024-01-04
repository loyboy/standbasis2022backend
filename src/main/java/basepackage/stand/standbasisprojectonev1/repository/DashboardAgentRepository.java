package basepackage.stand.standbasisprojectonev1.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import basepackage.stand.standbasisprojectonev1.model.DashboardAgent;

public interface DashboardAgentRepository extends JpaRepository<DashboardAgent, Long>{
	 
	  @Query("select das from DashboardAgent das where das.id = :id")
	  Optional<DashboardAgent> findByAgentId ( @Param("id") Long id );
	  
	  @Query("select das from DashboardAgent das where das.code = :code")
	  Optional<DashboardAgent> findByCode ( @Param("code") String code );
}
