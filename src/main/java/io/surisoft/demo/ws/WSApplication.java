package io.surisoft.demo.ws;

import com.nimbusds.jose.jwk.JWKSet;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.surisoft.demo.ws.data.WebApplication;
import io.surisoft.demo.ws.exception.WebApplicationSecurityException;
import io.surisoft.demo.ws.repository.WebApplicationRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.component.micrometer.DistributionStatisticConfigFilter;
import org.apache.camel.component.micrometer.messagehistory.MicrometerMessageHistoryFactory;
import org.apache.camel.component.micrometer.routepolicy.MicrometerRoutePolicyFactory;
import org.apache.camel.zipkin.ZipkinTracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.okhttp3.OkHttpSender;

import static org.apache.camel.component.micrometer.MicrometerConstants.DISTRIBUTION_SUMMARIES;
import static org.apache.camel.component.micrometer.messagehistory.MicrometerMessageHistoryNamingStrategy.MESSAGE_HISTORIES;
import static org.apache.camel.component.micrometer.routepolicy.MicrometerRoutePolicyNamingStrategy.ROUTE_POLICIES;

import java.text.ParseException;
import java.time.Duration;
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