package io.surisoft.demo.ws.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@Slf4j
public class DemoListener implements ApplicationListener {
    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {

        if(applicationEvent instanceof SessionConnectedEvent) {
            SessionConnectedEvent sessionConnectedEvent = (SessionConnectedEvent) applicationEvent;
            log.info("CONNECTED");
            log.info(sessionConnectedEvent.getMessage().toString());
            log.info(sessionConnectedEvent.getTimestamp()+"");
        } else if(applicationEvent instanceof SessionDisconnectEvent) {
            log.info("DISCONNECTED");
        }
    }
}
