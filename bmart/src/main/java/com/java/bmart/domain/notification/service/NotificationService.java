package com.java.bmart.domain.notification.service;

import static java.text.MessageFormat.format;

import com.java.bmart.domain.notification.Notification;
import com.java.bmart.domain.notification.NotificationType;
import com.java.bmart.domain.notification.controller.request.ConnectNotificationCommand;
import com.java.bmart.domain.notification.repository.EmitterRepository;
import com.java.bmart.domain.notification.service.request.SendNotificationCommand;
import com.java.bmart.domain.notification.service.response.NotificationResponse;
import com.java.bmart.domain.user.exception.NotFoundUserException;
import com.java.bmart.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 120;

    private final EmitterRepository  emitterRepository;
    private final UserRepository userRepository;

    public SseEmitter connectNotification(ConnectNotificationCommand connectNotificationCommand) {
        Long userId = connectNotificationCommand.userId();
        String lastEventId = connectNotificationCommand.lastEventId();

        String emitterId = format("{0}_{1}",userId, System.currentTimeMillis());
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(emitterId, emitter);

        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));
        emitter.onError(e -> emitterRepository.deleteById(emitterId));

        send(emitter, emitterId, format("[Connected] UserId={0}", userId));

        if (!connectNotificationCommand.lastEventId().isEmpty()) {
            Map<String, SseEmitter> events = emitterRepository.findAllByIdStartWith(userId);
            events.entrySet().stream().filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> send(emitter, entry.getKey(), entry.getValue()));
        }

        return emitter;
    }

    public void sendNotification(SendNotificationCommand sendNotificationCommand) {
        Long userId = sendNotificationCommand.userId();
        String title = sendNotificationCommand.title();
        String content = sendNotificationCommand.content();
        NotificationType notificationType = sendNotificationCommand.notificationType();

        verifyExistsUser(userId);
        Notification notification = Notification.builder()
                .title(title)
                .content(content)
                .userId(userId)
                .notificationType(notificationType)
                .build();

        Map<String, SseEmitter> emitters = emitterRepository.findAllByIdStartWith(userId);
        emitters.forEach((key, emitter) -> send(emitter, key, NotificationResponse.from(notification)));
    }

    private void send(SseEmitter sseEmitter, String emitterId, Object data) {
        try {
            sseEmitter.send(SseEmitter.event()
                    .id(emitterId)
                    .data(data));
        } catch (IOException ex) {
            emitterRepository.deleteById(emitterId);
            log.error("알림 전송에 실패했습니다.", ex);
        }
    }

    private void verifyExistsUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException("존재하지 않는 유저입니다."));
    }
}
