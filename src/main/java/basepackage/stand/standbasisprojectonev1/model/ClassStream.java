package basepackage.stand.standbasisprojectonev1.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "class_streams")
public class ClassStream extends DateAudit{

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clsId;
    
    @NotNull    
    private String id;
    
    @NotBlank
    @NotNull
    @Size(max = 60)
    private String title;
    
    @Size(max = 30)
    private String ext;
    
    private Integer status; // 1 - active, -1 - deleted, 0 - inactive 
    
    @NotNull
    private Integer class_index;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "sch_id", nullable = false)
    private School school;
    
    public ClassStream() {}
}
