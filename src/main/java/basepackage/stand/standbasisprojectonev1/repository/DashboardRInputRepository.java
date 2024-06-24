package basepackage.stand.standbasisprojectonev1.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import basepackage.stand.standbasisprojectonev1.model.DashboardRatingInput;
import basepackage.stand.standbasisprojectonev1.model.School;

public interface DashboardRInputRepository extends JpaRepository<DashboardRatingInput, Long>{
    @Query("select das from DashboardRatingInput das where das.school = :sch AND (das._year = :year OR :year is null) ")
	  Optional<DashboardRatingInput> findBySchoolAcademic( @Param("sch") School schId , @Param("year") Integer y );
	  
	  ////////////////////////////////////////////////////////////////////////////////
	  
	  @Query("select das from DashboardRatingInput das where das.dashId = :owner")
	  Optional<DashboardRatingInput> findByAcademicId ( @Param("owner") Long owner );
	  
	  ////////////////////////////////////////////////////////////////////////////////
	  
	  @Query("select das from DashboardRatingInput das where das.school = :sch ")
	  List<DashboardRatingInput> findBySchoolAcademicOnly ( @Param("sch") School sch );

	  //////////////////////////////////////////////////////////////////////////////
	  @Query("select das from DashboardRatingInput das where das.school = :sch  AND (das._year = :year OR :year is null) ")
	  List<DashboardRatingInput> findBySchoolAndYearAcademic ( @Param("sch") School sch , @Param("year") Integer y );
}
