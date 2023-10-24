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

//import basepackage.stand.standbasisprojectonev1.model.Calendar;

public interface DashboardRepository extends JpaRepository<DashboardSsis, Long> {

	  @Query("select das from DashboardSsis das where "
            + "das.school = :owner"
       	 )
	  Optional<DashboardSsis> findBySchoolSSIS( @Param("owner") School ownerId );
	  
	  @Query("select das from DashboardTeacher das where das.school = :owner")
	  Optional<DashboardTeacher> findBySchoolTeacher( @Param("owner") School ownerId );
	  
	  @Query("select das from DashboardCurriculum das where das.school = :owner")
	  Optional<DashboardCurriculum> findBySchoolCurriculum( @Param("owner") School ownerId );
	  
	  @Query("select das from DashboardAcademic das where das.school = :owner")
	  Optional<DashboardAcademic> findBySchoolAcademic( @Param("owner") School ownerId );
	  
	  ////////////////////////////////////////////////////////////////////////////////
	  @Query("select das from DashboardSsis das where das.dashId = :owner")
	  Optional<DashboardSsis> findBySsisId ( @Param("owner") Long owner );
	  
	  @Query("select das from DashboardTeacher das where das.dashId = :owner")
	  Optional<DashboardTeacher> findByTeacherId ( @Param("owner") Long owner );
	  
	  @Query("select das from DashboardCurriculum das where das.dashId = :owner")
	  Optional<DashboardCurriculum> findByCurriculumId ( @Param("owner") Long owner );
	  
	  @Query("select das from DashboardAcademic das where das.dashId = :owner")
	  Optional<DashboardAcademic> findByAcademicId ( @Param("owner") Long owner );
}
