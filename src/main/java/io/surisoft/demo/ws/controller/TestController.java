package io.surisoft.demo.ws.controller;

import io.surisoft.demo.ws.data.Message;
import io.surisoft.demo.ws.data.WebApplication;
import io.surisoft.demo.ws.repository.WebApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@RestController
public class TestController {

    @Autowired
    private SimpMessagingTemplate template;

    @Value("${capi.ws.topic.messages.name}")
    private String topicMessagesName;

    @Autowired
    private WebApplicationRepository webApplicationRepository;

    @GetMapping(path = "/${capi.ws.rest.endpoint}/{application}/{message}")
    public ResponseEntity<String> sendMessage(@PathVariable String application, @PathVariable String message) {
        Optional<WebApplication> webApplication = webApplicationRepository.findByName(application);
        if(webApplication.isPresent()) {
            String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
            Message templateMessage =  new Message("Test Controller", message, time);
            template.convertAndSend(topicMessagesName + application, templateMessage);
            return new ResponseEntity<>("OK", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
