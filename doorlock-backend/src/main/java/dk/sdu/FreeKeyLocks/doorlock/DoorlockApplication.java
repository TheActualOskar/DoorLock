package dk.sdu.FreeKeyLocks.doorlock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DoorlockApplication {

	public static void main(String[] args) {
		SpringApplication.run(DoorlockApplication.class, args);
	}

}
