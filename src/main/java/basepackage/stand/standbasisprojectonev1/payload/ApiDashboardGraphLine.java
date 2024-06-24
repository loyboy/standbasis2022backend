package basepackage.stand.standbasisprojectonev1.payload;
import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
@Getter 
@Setter
public class ApiDashboardGraphLine {
    private String name;
    private List<BigDecimal> data;

    public ApiDashboardGraphLine( String name, List<BigDecimal> data) {
        this.name = name;
        this.data = data;
    }
}

