package in.mshitlearner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringbootSqsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootSqsApplication.class, args);
	}

}
