package basepackage.stand.standbasisprojectonev1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import basepackage.stand.standbasisprojectonev1.model.EvaluationValues;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.User;

@Repository
public interface EvaluationRepository extends JpaRepository<EvaluationValues, Long>{

	 @Query( " select eva from EvaluationValues eva WHERE eva.school = :sch AND eva.user = :u ")
     List<EvaluationValues> findRoundsByUser( @Param("sch") School sch,  @Param("u") User u );
	 
	 @Query( " select eva from EvaluationValues eva WHERE eva.school = :sch ")
     List<EvaluationValues> findRounds( @Param("sch") School sch );
	 
	 @Query( " select eva from EvaluationValues eva WHERE eva.user = :u ")
     List<EvaluationValues> findRoundsByUser2( @Param("u") User u );
	 
	 @Query( " select eva from EvaluationValues eva WHERE eva.roundId = :id ")
     List<EvaluationValues> findByRoundId(@Param("id") String roundId);
	 
	 @Query( " select eva from EvaluationValues eva WHERE LOWER(eva.school.owner.name) = LOWER(:name) ")
     List<EvaluationValues> findByRoundByGroup(@Param("name") String name);
	 
	 @Query( " select eva from EvaluationValues eva WHERE LOWER(eva.school.jurisdiction) = LOWER(:zone) ")
     List<EvaluationValues> findByRoundByZone(@Param("zone") String zone);
	 
	 
}
