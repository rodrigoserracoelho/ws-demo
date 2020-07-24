package io.surisoft.demo.ws;

import io.surisoft.demo.ws.data.WebApplication;
import io.surisoft.demo.ws.repository.WebApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private WebApplicationRepository webApplicationRepository;

	@Bean
	public void dummyApps() {

		List<WebApplication> webApplications = webApplicationRepository.findAll();

		if(webApplications.isEmpty()) {
			WebApplication app1 = new WebApplication();
			app1.setName("app1");
			app1.setSecured(false);

			WebApplication app2 = new WebApplication();
			app2.setName("app2");
			app2.setSecured(true);

			webApplicationRepository.save(app1);
			webApplicationRepository.save(app2);

		}
	}
}