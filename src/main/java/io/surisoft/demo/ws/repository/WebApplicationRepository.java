package io.surisoft.demo.ws.repository;

import io.surisoft.demo.ws.data.WebApplication;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface WebApplicationRepository extends MongoRepository<WebApplication, String> {
    public Optional<WebApplication> findByName(String applicationName);
}
