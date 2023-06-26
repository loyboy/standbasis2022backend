package basepackage.stand.standbasisprojectonev1.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;

@Repository
public interface SchoolgroupRepository extends JpaRepository <SchoolGroup, Long> {

	 Optional<SchoolGroup> findById(Long groupId);
	 
	 @Query( "select s from SchoolGroup s where s.name like :filter " )
	 Page<SchoolGroup> filter(@Param("filter") String filter, Pageable pg);
	 
	 @Query("SELECT DATE(s.createdAt) AS createdDate, COUNT(s) AS count FROM SchoolGroup s " +
	           "WHERE DATE(s.createdAt) >= :startDate AND DATE(s.createdAt) <= :endDate " +
	           "GROUP BY DATE(s.createdAt)")
	 List<Object[]> countSchoolsCreatedPerDay(Timestamp startDate, Timestamp endDate);
}
