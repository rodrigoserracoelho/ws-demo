package io.surisoft.demo.ws.config;

import io.surisoft.demo.ws.data.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Slf4j
public class ScheduledMessage {

    @Autowired
    private SimpMessagingTemplate template;

    @Scheduled(fixedRate = 10000)
    public void sendMessage() {
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        Message message =  new Message("Robot", "this is me", time);
        template.convertAndSend("/topic/messages", message);
    }
}