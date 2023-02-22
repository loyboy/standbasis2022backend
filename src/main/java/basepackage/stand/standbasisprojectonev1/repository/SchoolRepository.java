package basepackage.stand.standbasisprojectonev1.repository;

import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by Loy from August 2022.
 */

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {

    Optional<School> findById(Long subId);
    
    @Query("select s from School s where s.name like :filter " 
         + "or s.lga like :filter " 
         + "or s.town like :filter " 
         + "or s.state like :filter " 
         + "or s.faith like :filter "
         + "or s.gender like :filter "
         + "or s.operator like :filter "
    		)
    Page<School> filter(@Param("filter") String filter, Pageable pg);    
   
    Page<School> findByOwner(SchoolGroup owner, Pageable pg);
    
    List<School> findByOwner(SchoolGroup owner);
    
    @Query("select s from School s where s.name like :filter " 
            + "or s.lga like :filter " 
            + "or s.town like :filter " 
            + "or s.state like :filter " 
            + "or s.faith like :filter "
            + "or s.gender like :filter "
            + "or s.operator like :filter "
            + "and s.owner = :owner "
       		)
    
    Page<School> findFilterByOwner(@Param("filter") String filter, @Param("owner") SchoolGroup ownerId, Pageable pg);
   // List<School> findByNameOrTownOrLgaOrStateOrFaithOrGenderOrOperator( String n,  String t, String l, String s, String f, String g, String o);
    
    @Query("SELECT COUNT(s.schId) from School s where s.sri = :active ")
    long countBySri(@Param("active") Long active);
    
    @Query("SELECT COUNT(s.schId) from School s where s.owner = :owner ")
    long countBySchoolGroup(@Param("owner") SchoolGroup owner);
    
    @Query("SELECT COUNT(s.schId) from School s where s.status = :active")
    long countByStatus(@Param("active") Long active);

}
