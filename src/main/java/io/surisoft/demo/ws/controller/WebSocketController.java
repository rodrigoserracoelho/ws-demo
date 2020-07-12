package io.surisoft.demo.ws.controller;

import com.google.gson.Gson;
import io.surisoft.demo.ws.data.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@Slf4j
public class WebSocketController {

    private Gson gson = new Gson();

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Message send(Message message) throws Exception {
        Thread.sleep(3000);
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        return new Message(message.getFrom(), message.getText(), time);
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Throwable exception) {
        return exception.getMessage();
    }
}
