package io.surisoft.demo.ws;

import com.nimbusds.jose.jwk.JWKSet;
import io.surisoft.demo.ws.data.WebApplication;
import io.surisoft.demo.ws.exception.WebApplicationSecurityException;
import io.surisoft.demo.ws.repository.WebApplicationRepository;
import io.surisoft.demo.ws.security.Authorization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class WSApplication {
	public static void main(String[] args) {
		SpringApplication.run(WSApplication.class, args);
	}

	@Value("${capi.ws.jwk.endpoint}")
	private String capiJwkEndpoint;

	@Autowired
	private WebApplicationRepository webApplicationRepository;

	@Bean
	public JWKSet jwkSet() throws ParseException, WebApplicationSecurityException {
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> publicKeyEndpoint = restTemplate.getForEntity(capiJwkEndpoint, String.class);
		if(!publicKeyEndpoint.getStatusCode().is2xxSuccessful()) {
			throw new WebApplicationSecurityException("Invalid JWK endpoint");
		}
		return JWKSet.parse(publicKeyEndpoint.getBody());
	}

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