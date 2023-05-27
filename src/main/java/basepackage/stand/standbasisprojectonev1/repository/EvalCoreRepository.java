package basepackage.stand.standbasisprojectonev1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import basepackage.stand.standbasisprojectonev1.model.EvaluationValues;
import basepackage.stand.standbasisprojectonev1.model.EvaluatorCore;

@Repository
public interface EvalCoreRepository extends JpaRepository<EvaluatorCore, Long>{


}
