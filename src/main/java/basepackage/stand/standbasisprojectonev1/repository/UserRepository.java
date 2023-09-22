package basepackage.stand.standbasisprojectonev1.repository;

import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;
import basepackage.stand.standbasisprojectonev1.model.Teacher;
import basepackage.stand.standbasisprojectonev1.model.User;

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
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByIdIn(List<Long> userIds);

    Optional<User> findByUsernameAndStatus(String username, Integer status);
    
    @Query( "select u from User u "
    		+ "WHERE ( u.school.owner = :group OR :group is null ) OR ( u.school = :sch OR :sch is null ) "
    	  )
    Page<User> findBySchool( @Param("sch") School sch, @Param("group") SchoolGroup group, Pageable pg );
    
    // ( u.proprietor_id = :tea OR u.principal_id = :tea OR u.teacher_id = :tea ) 
    @Query( "select u from User u "
    		+ "WHERE "
            + "( u.school.owner = :group OR :group is null ) OR ( u.school = :sch OR :sch is null ) "
    		+ "AND ( u.name like :filter OR u.username like :filter OR u.email like :filter ) "
    	  )
    Page<User> findFilterBySchool( @Param("filter") String filter, @Param("sch") School sch, @Param("group") SchoolGroup group,  Pageable pg );
    
    User findByUserId(Long userid);

    Boolean existsByUsername(String username);
    
    @Query("select u from User u where u.name like :filter " 
            + "or u.username like :filter "
            + "or u.email like :filter "
    	  )
    Page<User> filter(@Param("filter") String filter, Pageable pg);    

}
