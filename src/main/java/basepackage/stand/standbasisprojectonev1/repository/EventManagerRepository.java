package basepackage.stand.standbasisprojectonev1.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import basepackage.stand.standbasisprojectonev1.model.EventManager;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;

@Repository
public interface EventManagerRepository extends JpaRepository<EventManager, Long>{

	 	Optional<EventManager> findById(Long eventId);
	    
	    List<EventManager> findBySchool(School sch);
	    
	    @Query(" select evt from EventManager evt where evt.comment like :filter ")
	    Page<EventManager> filter( @Param("filter") String filter, Pageable pg);
	    
	    @Query("select evt from EventManager evt "
	    		+ "WHERE (evt.school = :sch OR :sch is null) "
	    		+ "OR (evt.school.owner = :group OR :group is null) "
	       	 )    
	    Page<EventManager> findByEventSchoolPage(
	    		@Param("sch") School sch, 
	    		@Param("group") SchoolGroup schgroup, 
	    		Pageable pg
	    );
	    
	    @Query("select evt from EventManager evt "
	    		+ "WHERE evt.school = :sch OR :sch is null "
	    		+ "OR (evt.school.owner = :group OR :group is null) "
	    		+ "AND ( (evt.comment like :filter OR :filter is null) OR (evt.module = :module OR :module is null) )"
	       	  )    
	    Page<EventManager> findFilterByEventSchoolPage(
	    		@Param("filter") String filter, 
	    		@Param("sch") School sch, 
	    		@Param("group") SchoolGroup schgroup, 
	    		@Param("module") String module, 
	    		Pageable pg 
	    );
}
