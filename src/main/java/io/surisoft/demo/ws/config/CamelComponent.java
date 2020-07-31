package io.surisoft.demo.ws.config;

import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.surisoft.demo.ws.data.Message;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.spi.RestConfiguration;
import org.apache.camel.zipkin.ZipkinTracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CamelComponent extends RouteBuilder {

    private static final int DEFAULT_STREAM_BUFFER_SIZE = 127;

    private int streamBufferSize = DEFAULT_STREAM_BUFFER_SIZE;

    @Value("${capi.ws.topic.messages.name}")
    private String topicMessagesName;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private CompositeMeterRegistry meterRegistry;

    @Autowired
    private ZipkinTracer zipkinTracer;

    @Autowired
    private MetricsProcessor metricsProcessor;

    @Override
    public void configure() throws Exception {

        String webSocketName = "/chat";
        String authorizedHost = "http://localhost";
        String endpointProtocol = "http://";
        String endpointHost = "localhost:9010";
        String endpointContext = "/chat";

        Map<String, String> corsHeaders = new HashMap<>();
        corsHeaders.put("Access-Control-Allow-Credentials", "true");
        corsHeaders.put("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE");
        corsHeaders.put("Access-Control-Allow-Headers", "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, Authorization");
        corsHeaders.put("Access-Control-Allow-Origin", authorizedHost);

        RestConfiguration restConfiguration = new RestConfiguration();
        restConfiguration.setEnableCORS(true);
        restConfiguration.setCorsHeaders(corsHeaders);

        String app2RouteId = "app2-rest";
        String app1RouteId = "app1-rest";

        rest( "/some-context/app1")
                .post()
                .route()
                .unmarshal() //.pr
                .json(JsonLibrary.Jackson, Message.class)
                .process(metricsProcessor)
                .process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                log.info(exchange.getIn().getBody().getClass().getCanonicalName());
                String messageDestination = topicMessagesName + "app1";
                messagingTemplate.convertAndSend(messageDestination, exchange.getIn().getBody());

            }
        }).to("log:loggerName").routeId(app1RouteId);

        rest( "/some-context/app2")
                .post()
                .route()
                .unmarshal() //.pr
                .json(JsonLibrary.Jackson, Message.class)
                .process(metricsProcessor)
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        log.info(exchange.getIn().getBody().getClass().getCanonicalName());
                        String messageDestination = topicMessagesName + "app2";
                        messagingTemplate.convertAndSend(messageDestination, exchange.getIn().getBody());

                    }
                }).to("log:loggerName").routeId(app2RouteId);

        meterRegistry.counter(app1RouteId);
        meterRegistry.counter(app2RouteId);

        zipkinTracer.addServerServiceMapping("/some-context/app1", app1RouteId);
        zipkinTracer.addServerServiceMapping("/some-context/app2", app2RouteId);
    }


}
