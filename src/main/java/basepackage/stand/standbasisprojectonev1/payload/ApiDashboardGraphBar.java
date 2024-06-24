package basepackage.stand.standbasisprojectonev1.payload;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
@Getter 
@Setter
public class ApiDashboardGraphBar {
    private String name;
    private List<Double> data;

    public ApiDashboardGraphBar( String name, List<Double> data) {
        this.name = name;
        this.data = data;
    }
}
