package basepackage.stand.standbasisprojectonev1.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import basepackage.stand.standbasisprojectonev1.model.DashboardAcademicInput;
import basepackage.stand.standbasisprojectonev1.model.School;

public interface DashboardAInputRepository extends JpaRepository<DashboardAcademicInput, Long>{

	  @Query("select das from DashboardAcademicInput das where das.school = :owner AND (das._year = :year OR :year is null) ")
	  Optional<DashboardAcademicInput> findBySchoolAcademic( @Param("owner") School ownerId , @Param("year") Integer y );
	  
	  ////////////////////////////////////////////////////////////////////////////////
	  
	  @Query("select das from DashboardAcademicInput das where das.dashId = :owner")
	  Optional<DashboardAcademicInput> findByAcademicId ( @Param("owner") Long owner );
	  
	  ////////////////////////////////////////////////////////////////////////////////
	  
	  @Query("select das from DashboardAcademicInput das where das.school = :sch ")
	  List<DashboardAcademicInput> findBySchoolAcademicOnly ( @Param("sch") School sch );
	  
}
