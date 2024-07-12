package com.java.bmart.domain.notification.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

public interface EmitterRepository {
    void save(String emitterId, SseEmitter sseEmitter);

    void deleteById(String emitterId);

    Map<String, SseEmitter> findAllByIdStartWith(Long userId);
}
