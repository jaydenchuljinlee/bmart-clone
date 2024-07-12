package com.java.bmart.domain.notification.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class NotificationRepository implements EmitterRepository{
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public void save(String emitterId, SseEmitter sseEmitter) {
        emitters.put(emitterId, sseEmitter);
    }

    @Override
    public void deleteById(String emitterId) {
        emitters.remove(emitterId);
    }

    @Override
    public Map<String, SseEmitter> findAllByIdStartWith(Long userId) {
        String emitterIdPrefix = userId + "_";
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(emitterIdPrefix))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
