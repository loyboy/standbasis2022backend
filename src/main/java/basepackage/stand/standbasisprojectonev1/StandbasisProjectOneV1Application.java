package basepackage.stand.standbasisprojectonev1;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StandbasisProjectOneV1Application {

	
	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Africa/Lagos"));
		SpringApplication.run(StandbasisProjectOneV1Application.class, args);
	}

}
