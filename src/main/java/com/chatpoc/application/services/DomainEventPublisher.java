package com.chatpoc.application.services;

public interface DomainEventPublisher {
    
    void publish(Object event);
}