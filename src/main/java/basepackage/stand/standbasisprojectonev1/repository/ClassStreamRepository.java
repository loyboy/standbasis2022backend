package basepackage.stand.standbasisprojectonev1.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import basepackage.stand.standbasisprojectonev1.model.ClassStream;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;
import basepackage.stand.standbasisprojectonev1.model.Teacher;

/**
 * Created by Loy from August 2022.
 */

@Repository
public interface ClassStreamRepository extends JpaRepository<ClassStream, Long> {

	   Optional<ClassStream> findById(Long classId);
	    
	   List<ClassStream> findBySchool(School sch);
	   
	   @Query("select DISTINCT(cs) from ClassStream cs INNER JOIN TimeTable tt ON cs.clsId = tt.class_stream.clsId "
	   		+ "where tt.teacher = :tea "          
       )
       List<ClassStream> findByTeacher(@Param("tea") Teacher tea); 
    
       @Query("select cs from ClassStream cs where cs.title like :filter " 
               + "or cs.ext like :filter "            
       	 )
       Page<ClassStream> filter(@Param("filter") String filter, Pageable pg);  
       
       @Query("select cs from ClassStream cs where cs.title like :filter " 
               + "or cs.ext like :filter "            
       	 )
       List<ClassStream> filterAll(@Param("filter") String filter);
      
       @Query("select cs from ClassStream cs where ( cs.school.owner = :group OR :group is null ) AND ( cs.school = :sch OR :sch is null ) " )
       Page<ClassStream> findBySchoolPage( @Param("sch") School owner, @Param("group") SchoolGroup group, Pageable pg);
       
       @Query("select cs from ClassStream cs "
          + "JOIN School s ON s = cs.school "
          + "WHERE (s.lga_code = :lga OR :lga is null) " 
          + "and (s.zone = :zone OR :zone is null) " 
          + "and (s.state = :state OR :state is null) " 
          + "and (s.type_of = :agency OR :agency is null) "
       )
       Page<ClassStream> findBySupervisor( 
                                        @Param("state") String state,
                                        @Param("agency") String agency,
                                        @Param("zone") String zone,
                                        @Param("lga") String lga, 
                                        Pageable pg
                                );

                        @Query("select cs from ClassStream cs "
                                + "JOIN School s ON s = cs.school "
                                + "WHERE (s.lga_code = :lga OR :lga is null) " 
                                + "and (s.zone = :zone OR :zone is null) " 
                                + "and (s.state = :state OR :state is null) " 
                                + "and (s.type_of = :agency OR :agency is null) "
                             )
                             List<ClassStream> findBySupervisor( 
                                                              @Param("state") String state,
                                                              @Param("agency") String agency,
                                                              @Param("zone") String zone,
                                                              @Param("lga") String lga                                                              
                                                      );      

       @Query("select cs from ClassStream cs where ( cs.school.owner = :group OR :group is null ) AND ( cs.school = :sch OR :sch is null ) " )
       List<ClassStream> findBySchoolPage( @Param("sch") School owner, @Param("group") SchoolGroup group);
       
       @Query("select cs from ClassStream cs where ( cs.school.owner = :group OR :group is null ) AND ( cs.title like :filter " 
               + "or cs.ext like :filter ) " 
               + "AND ( cs.school = :sch OR :sch is null ) "
             )       
       Page<ClassStream> findFilterBySchoolPage(@Param("filter") String filter, @Param("sch") School ownerId, @Param("group") SchoolGroup group, Pageable pg);
       
       @Query("select cs from ClassStream cs "
                + "JOIN School s ON s = cs.school "
                + "WHERE (s.lga_code = :lga OR :lga is null) " 
                + "and (s.zone = :zone OR :zone is null) " 
                + "and (s.state = :state OR :state is null) " 
                + "and (s.type_of = :agency OR :agency is null) "
                + "AND ( cs.title like :filter or cs.ext like :filter ) "
        )
        Page<ClassStream> findFilterBySupervisor( 
                                     @Param("filter") String filter,
                                     @Param("state") String state,
                                     @Param("agency") String agency,
                                     @Param("zone") String zone,
                                     @Param("lga") String lga, 
                                     Pageable pg
                             );

                    @Query("select cs from ClassStream cs "
                             + "JOIN School s ON s = cs.school "
                             + "WHERE (s.lga_code = :lga OR :lga is null) " 
                             + "and (s.zone = :zone OR :zone is null) " 
                             + "and (s.state = :state OR :state is null) " 
                             + "and (s.type_of = :agency OR :agency is null) "
                             + "AND ( cs.title like :filter or cs.ext like :filter ) "
                     )
                     List<ClassStream> findFilterBySupervisor( 
                                                  @Param("filter") String filter,
                                                  @Param("state") String state,
                                                  @Param("agency") String agency,
                                                  @Param("zone") String zone,
                                                  @Param("lga") String lga                                                  
                                          );

       @Query("select cs from ClassStream cs where ( cs.school.owner = :group OR :group is null ) AND ( cs.title like :filter " 
               + "or cs.ext like :filter ) " 
               + "AND ( cs.school = :sch OR :sch is null ) "
             )       
       List<ClassStream> findFilterBySchoolPage(@Param("filter") String filter, @Param("sch") School ownerId, @Param("group") SchoolGroup group);
       
       @Query("SELECT COUNT(cs.id) from ClassStream cs where cs.school = :sch ")
       long countBySchool(@Param("sch") School sch);

}
