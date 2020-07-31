package io.surisoft.demo.ws.config;

import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.search.RequiredSearch;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MetricsProcessor implements Processor {

    @Autowired
    private CompositeMeterRegistry meterRegistry;

    @Override
    public void process(Exchange exchange) {
        if(exchange.getIn().getHeader("CamelServletContextPath") != null && exchange.getIn().getHeader(Exchange.HTTP_METHOD) != null) {
            String routeId = exchange.getFromRouteId();
            RequiredSearch s = meterRegistry.get(routeId);
            s.counter().increment();
        }
    }
}