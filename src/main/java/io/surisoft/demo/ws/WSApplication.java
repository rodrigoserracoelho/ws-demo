package io.surisoft.demo.ws;

import io.surisoft.demo.ws.data.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class WSApplication {
	public static void main(String[] args) {
		SpringApplication.run(WSApplication.class, args);
	}

	@Bean
	public List<Application> dummyApps() {
		List<Application> dummyApps = new ArrayList<>();

		Application app1 = new Application();
		app1.setName("app1");
		app1.setSecured(false);

		Application app2 = new Application();
		app2.setName("app2");
		app2.setSecured(true);

		dummyApps.add(app1);
		dummyApps.add(app2);

		return dummyApps;
	}


}