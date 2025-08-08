package com.chatpoc.infrastructure.messaging;

import com.chatpoc.application.services.DomainEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SpringDomainEventPublisher implements DomainEventPublisher {
    
    private final ApplicationEventPublisher applicationEventPublisher;
    
    public SpringDomainEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = Objects.requireNonNull(applicationEventPublisher);
    }
    
    @Override
    public void publish(Object event) {
        Objects.requireNonNull(event, "Event cannot be null");
        applicationEventPublisher.publishEvent(event);
    }
}