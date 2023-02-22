package basepackage.stand.standbasisprojectonev1.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;
import basepackage.stand.standbasisprojectonev1.model.Student;


/**
 * Created by Loy from August 2022.
 */

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findById(Long pupId);
    
    List<Student> findBySchool(School sch);
    
    @Query(" select stu from Student stu where stu.name like :filter ")
    Page<Student> filter( @Param("filter") String filter, Pageable pg);
    
    @Query("SELECT COUNT(s.id) from Student s where s.school = :sch ")
    long countBySchool(@Param("sch") School sch);
    
    @Query("SELECT COUNT(s.id) from Student s where s.school.owner = :group ")
    long countBySchoolGroup(@Param("group") SchoolGroup group);

}