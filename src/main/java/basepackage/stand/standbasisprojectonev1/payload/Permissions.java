package basepackage.stand.standbasisprojectonev1.payload;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class Permissions {
    
	private String module;
	
    @NotNull
    private Boolean read = false;
    
    @NotNull
    private Boolean create = false;
    
    @NotNull
    private Boolean edit = false;
    
    @NotNull
    private Boolean delete = false;
    
    public Permissions(Boolean r, Boolean e, Boolean d, Boolean c ) {
    	  this.read = r;
          this.edit = e;
          this.delete = d;
          this.create = c;
    }
    
    public Permissions(String m, Boolean r, Boolean e, Boolean d, Boolean c ) {
  	  	this.module = m;
    	this.read = r;
        this.edit = e;
        this.delete = d;
        this.create = c;
  }
}
