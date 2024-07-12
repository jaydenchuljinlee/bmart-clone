package com.java.bmart.domain.user.service;

import com.java.bmart.domain.user.UserGrade;
import com.java.bmart.domain.user.repository.UserRepository;
import com.java.bmart.domain.user.repository.response.UserOrderCount;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@RequiredArgsConstructor
@Service
public class GradeService {
    private static final int ONE = 1;

    private final UserRepository userRepository;

    @Transactional
    public void updateUserGrade() {
        LocalDateTime startTimeOfPreviousMonth = getStartTimeOfPreviousMonth();
        LocalDateTime endTimeOfPreviousMonth = getEndTimeOfPreviousMonth();

        List<UserOrderCount> userOrderCounts = userRepository.getUserOrderCount(startTimeOfPreviousMonth, endTimeOfPreviousMonth);
        Map<UserGrade, List<UserOrderCount>> userGradeGroup = groupByUserGrade(userOrderCounts);

        userGradeGroup.forEach((userGrade, userOrderCountGroup) ->
                userRepository.updateUserGrade(userGrade, extractUserIds(userOrderCountGroup)));

    }

    private LocalDateTime getStartTimeOfPreviousMonth() {
        return YearMonth.now()
                .minusMonths(ONE)
                .atDay(ONE)
                .atStartOfDay();
    }

    private LocalDateTime getEndTimeOfPreviousMonth() {
        return YearMonth.now()
                .atDay(ONE)
                .atStartOfDay()
                .minusNanos(ONE);
    }

    private Map<UserGrade, List<UserOrderCount>> groupByUserGrade(
            List<UserOrderCount> userOrderCounts) {
        return userOrderCounts.stream()
                .collect(groupingBy(userOrderCount ->
                        UserGrade.calculateUserGrade(userOrderCount.getOrderCount())));
    }

    private List<Long> extractUserIds(List<UserOrderCount> userOrderCountGroup) {
        return userOrderCountGroup.stream()
                .map(UserOrderCount::getUserId)
                .toList();
    }
}
