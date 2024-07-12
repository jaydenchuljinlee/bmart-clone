package com.java.bmart.domain.event.service;

import com.java.bmart.domain.event.Event;
import com.java.bmart.domain.event.exception.NotFoundEventException;
import com.java.bmart.domain.event.repository.EventRepository;
import com.java.bmart.domain.event.service.request.FindEventDetailCommand;
import com.java.bmart.domain.event.service.request.RegisterEventCommand;
import com.java.bmart.domain.event.service.response.FindEventDetailResponse;
import com.java.bmart.domain.event.service.response.FindEventDetailResponse.EventDetailResponse;
import com.java.bmart.domain.event.service.response.FindEventsResponse;
import com.java.bmart.domain.event.service.response.FindEventsResponse.FindEventResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EventService {
    private final EventRepository eventRepository;

    @Transactional(readOnly = true)
    public FindEventsResponse findEvents() {
        List<Event> events = eventRepository.findAllByOrderByCreatedAtDesc();

        return FindEventsResponse.of(events.stream()
                .map(event -> new FindEventResponse(
                        event.getEventId(),
                        event.getTitle(),
                        event.getDescription()
                )).collect(Collectors.toList()));
    }

    @Transactional(readOnly = true)
    public FindEventDetailResponse findEventDetail(FindEventDetailCommand findEventDetailCommand) {
        Event event = eventRepository.findByIdWithEventItems(findEventDetailCommand.eventId())
                .orElseThrow(() -> new NotFoundEventException("존재하지 않는 이벤트"));

        EventDetailResponse eventDetailResponse = new EventDetailResponse(event.getEventId(), event.getTitle(), event.getDescription());
        List<FindEventDetailResponse.EventItemResponse> eventItemResponses = event.getEventItemList().stream()
                .map(eventItem -> new FindEventDetailResponse.EventItemResponse(
                                eventItem.getItem().getItemId(),
                                eventItem.getItem().getName(),
                                eventItem.getItem().getPrice(),
                                eventItem.getItem().getDiscount(),
                                eventItem.getItem().getReviews().size(),
                                eventItem.getItem().getLikeItems().size(),
                                eventItem.getItem().getRate()
                        )
                ).collect(Collectors.toList());

        return FindEventDetailResponse.of(eventDetailResponse, eventItemResponses);

    }

    @Transactional
    public Long registerEvent(RegisterEventCommand registerEventCommand) {
        Event event = new Event(registerEventCommand.title(), registerEventCommand.description());
        Event registered = eventRepository.save(event);

        return registered.getEventId();
    }
}
