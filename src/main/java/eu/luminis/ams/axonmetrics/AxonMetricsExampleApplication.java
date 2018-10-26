package eu.luminis.ams.axonmetrics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AxonMetricsExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(AxonMetricsExampleApplication.class, args);
	}
}
