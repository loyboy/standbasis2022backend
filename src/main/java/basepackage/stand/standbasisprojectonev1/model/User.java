package basepackage.stand.standbasisprojectonev1.model;

import basepackage.stand.standbasisprojectonev1.model.audit.DateAudit;
import lombok.Getter;
import lombok.Setter;

//import org.hibernate.annotations.NaturalId;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Email;
/**
 * Created by Loy from August 2022.
 */

@SuppressWarnings("serial")
@Getter 
@Setter
@Entity
@Table(name = "users", 
		indexes = { 
				@Index(name = "my_index_principal",  columnList="principal_id", unique = false),
				@Index(name = "my_index_proprietor",  columnList="proprietor_id", unique = false)
		},		
		uniqueConstraints = {
        @UniqueConstraint(columnNames = {
            "username"
        })
})
public class User extends DateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    
    @NotNull    
    private String id;

    @Size(max = 120)
    private String logo;
    
    @NotNull
    @NotBlank
    @Size(max = 40)
    private String name;

    @NotNull
    @NotBlank
    @Size(max = 30)
    private String username;

    @NotNull
    @NotBlank
    @Size(max = 100)
    private String password;
    
    private Integer status;
    
    private Integer temp_pass;
    
    @Email
    private String email;
    
    @NotNull
    private RoleName role; 
    
    private Long teacher_id;
    private Long pupil_id;
    private Long principal_id;
    private Long proprietor_id;
    private String supervisor_id;//01-SUBP-01-IKA (Group of School-Supervisor.Group-StateofOrigin-LocalGovernment)
    private String guardian_id;//02-0021-0034-1203 (Group of School-Pupil.ID-Pupil.ID-Pupil.ID)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sch_id", nullable = true)
    private School school;
    
    @Column(name="permissionsjson",columnDefinition="text")
    private String permissionsJSON;

    public User() {

    }

    public User(String id,String name, String username, String password, Integer status, RoleName role) {        
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.status = status;
        this.role = role;
    } 
    
}