package basepackage.stand.standbasisprojectonev1.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import basepackage.stand.standbasisprojectonev1.model.Rowcall;

@Repository
public interface RowcallRepository extends JpaRepository <Rowcall, Long> {

	Optional<Rowcall> findById(Long rowId);
	
	@Query(" select rw from Rowcall rw where rw.remark like :filter ")
    Page<Rowcall> filter( @Param("filter") String filter, Pageable pg);
	
	@Query(" select rw from Rowcall rw where rw.remark like :filter ")
    List<Rowcall> filterAll( @Param("filter") String filter); 
}
