package basepackage.stand.standbasisprojectonev1.repository;

import basepackage.stand.standbasisprojectonev1.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by Loy from August 2022.
 */

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    Optional<Subject> findById(Long subId);

}
