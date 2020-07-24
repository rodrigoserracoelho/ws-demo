package io.surisoft.demo.ws.controller;

import io.surisoft.demo.ws.data.WebApplication;
import io.surisoft.demo.ws.data.Message;
import io.surisoft.demo.ws.repository.WebApplicationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@Slf4j
public class WebSocketController {

    @Autowired
    private WebApplicationRepository webApplicationRepository;

    @Value("${capi.ws.topic.messages.name}")
    private String topicMessagesName;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/${capi.ws.root.context}/{application}")
    public void send(@DestinationVariable String application, @Headers Map<String, Object> headers, Message message) {

        String messageDestination = topicMessagesName + application;
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        Map<String, Object> nativeHeaders = null;

        if(headers.containsKey("nativeHeaders")) {
            nativeHeaders = (Map<String, Object>) headers.get("nativeHeaders");
        } else {
            messagingTemplate.convertAndSend(messageDestination, new Message("BOT", "INVALID MESSAGE", time));
        }

        Optional<WebApplication> existingWebApplication = webApplicationRepository.findByName(application);
        if(existingWebApplication.isPresent()) {
            try {
                if(isAuthorized(existingWebApplication.get(), nativeHeaders)) {
                    messagingTemplate.convertAndSend(messageDestination, new Message(message.getFrom(), message.getText(), time));
                } else {
                    messagingTemplate.convertAndSend(messageDestination, new Message("BOT", "NOT AUTHORIZED", time));
                }
            } catch(Exception e) {
                log.error(e.getMessage(), e);
                messagingTemplate.convertAndSend(messageDestination, new Message("ERROR", "ERROR", time));
            }
        } else {
            messagingTemplate.convertAndSend(messageDestination, new Message("BOT", "APPLICATION: " + application + " DOES NOT EXIST", time));
        }
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Throwable exception) {
        return exception.getMessage();
    }

    public boolean isAuthorized(WebApplication webApplication, Map<String, Object> nativeHeaders) {
        if(webApplication.isSecured()) {
            LinkedList<String> authList = (LinkedList<String>) nativeHeaders.get("Authorization");
            String authorization = authList.get(0);
            if(authorization.startsWith("JJJJ")) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
}