package basepackage.stand.standbasisprojectonev1.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import basepackage.stand.standbasisprojectonev1.model.audit.DateAudit;
import lombok.Getter;
import lombok.Setter;


@SuppressWarnings("serial")
@Getter 
@Setter
@Entity
@Table(name = "subjects")
public class Subject extends DateAudit {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subId;
    
    @NotNull    
    private String id;
    
    @NotBlank
    @NotNull
    @Size(max = 100)
    private String name, category;
    
    @Size(max = 100)
    private String sch_id;
    
    public Subject(){}
    
    public Subject(String id,String name, String category) {
    	this.id = id;
        this.name = name;
        this.category = category;
    }
}
