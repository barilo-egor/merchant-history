package tgb.cryptoexchange.merchanthistory.service;

import org.junit.jupiter.api.Test;
import tgb.cryptoexchange.merchanthistory.entity.DetailsReceiveMonitor;
import tgb.cryptoexchange.merchanthistory.entity.MerchantAttempt;
import tgb.cryptoexchange.merchanthistory.entity.embeddable.AmountRange;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StatisticCalculateServiceTest {

    private final StatisticCalculateService statisticCalculateService = new StatisticCalculateService();

    @Test
    void getAverageDurationShouldCalculateAverageDurationOfFewObjects() {
        List<DetailsReceiveMonitor> monitors = new ArrayList<>();
        monitors.add(DetailsReceiveMonitor.builder()
                .startTime(LocalDateTime.of(2000, 1, 1, 0, 59, 0)
                        .atZone(ZoneId.systemDefault()).toInstant())
                .endTime(LocalDateTime.of(2000, 1, 1, 0, 59, 15)
                        .atZone(ZoneId.systemDefault()).toInstant())
                .build());
        monitors.add(DetailsReceiveMonitor.builder()
                .startTime(LocalDateTime.of(2022, 6, 5, 0, 1, 13)
                        .atZone(ZoneId.systemDefault()).toInstant())
                .endTime(LocalDateTime.of(2022, 6, 5, 0, 1, 43)
                        .atZone(ZoneId.systemDefault()).toInstant())
                .build());
        monitors.add(DetailsReceiveMonitor.builder()
                .startTime(LocalDateTime.of(1955, 12, 1, 23, 12, 4)
                        .atZone(ZoneId.systemDefault()).toInstant())
                .endTime(LocalDateTime.of(1955, 12, 1, 23, 12, 49)
                        .atZone(ZoneId.systemDefault()).toInstant())
                .build());
        Duration actual = statisticCalculateService.getAverageDuration(
                monitors, DetailsReceiveMonitor::getStartTime, DetailsReceiveMonitor::getEndTime
        );
        assertEquals(30, actual.getSeconds());
    }

    @Test
    void getAverageDurationShouldCalculateAverageDurationOfOneObjects() {
        List<DetailsReceiveMonitor> monitors = new ArrayList<>();
        monitors.add(DetailsReceiveMonitor.builder()
                .startTime(LocalDateTime.of(2022, 6, 5, 0, 1, 13)
                        .atZone(ZoneId.systemDefault()).toInstant())
                .endTime(LocalDateTime.of(2022, 6, 5, 0, 1, 45)
                        .atZone(ZoneId.systemDefault()).toInstant())
                .build());
        Duration actual = statisticCalculateService.getAverageDuration(
                monitors, DetailsReceiveMonitor::getStartTime, DetailsReceiveMonitor::getEndTime
        );
        assertEquals(32, actual.getSeconds());
    }

    @Test
    void getAverageDurationShouldReturn0IfNoObjects() {
        List<DetailsReceiveMonitor> monitors = new ArrayList<>();
        Duration actual = statisticCalculateService.getAverageDuration(
                monitors, DetailsReceiveMonitor::getStartTime, DetailsReceiveMonitor::getEndTime
        );
        assertEquals(0, actual.getSeconds());
    }

    @Test
    void countShouldCountIfDifferentPredicateResult() {
        List<DetailsReceiveMonitor> monitors = new ArrayList<>();
        monitors.add(DetailsReceiveMonitor.builder().success(false).build());
        monitors.add(DetailsReceiveMonitor.builder().success(true).build());
        monitors.add(DetailsReceiveMonitor.builder().success(true).build());
        monitors.add(DetailsReceiveMonitor.builder().success(false).build());
        monitors.add(DetailsReceiveMonitor.builder().success(true).build());
        monitors.add(DetailsReceiveMonitor.builder().success(true).build());
        assertEquals(4, statisticCalculateService.count(monitors, DetailsReceiveMonitor::isSuccess));
    }

    @Test
    void countShouldCountAllIfAllTrue() {
        List<DetailsReceiveMonitor> monitors = new ArrayList<>();
        monitors.add(DetailsReceiveMonitor.builder().success(true).build());
        monitors.add(DetailsReceiveMonitor.builder().success(true).build());
        monitors.add(DetailsReceiveMonitor.builder().success(true).build());
        monitors.add(DetailsReceiveMonitor.builder().success(true).build());
        monitors.add(DetailsReceiveMonitor.builder().success(true).build());
        assertEquals(5, statisticCalculateService.count(monitors, DetailsReceiveMonitor::isSuccess));
    }

    @Test
    void countShouldCountNothingIfAllFalse() {
        List<DetailsReceiveMonitor> monitors = new ArrayList<>();
        monitors.add(DetailsReceiveMonitor.builder().success(false).build());
        monitors.add(DetailsReceiveMonitor.builder().success(false).build());
        assertEquals(0, statisticCalculateService.count(monitors, DetailsReceiveMonitor::isSuccess));
    }

    @Test
    void countShouldReturn0IfNoObjects() {
        List<DetailsReceiveMonitor> monitors = new ArrayList<>();
        assertEquals(0, statisticCalculateService.count(monitors, DetailsReceiveMonitor::isSuccess));
    }

    @Test
    void sortByAmountRangeShouldSortObjects() {
        List<DetailsReceiveMonitor> monitors = new ArrayList<>();
        monitors.add(DetailsReceiveMonitor.builder().amount(1).build());
        monitors.add(DetailsReceiveMonitor.builder().amount(1).build());
        monitors.add(DetailsReceiveMonitor.builder().amount(525).build());
        monitors.add(DetailsReceiveMonitor.builder().amount(999).build());
        monitors.add(DetailsReceiveMonitor.builder().amount(1000).build());
        monitors.add(DetailsReceiveMonitor.builder().amount(1001).build());
        monitors.add(DetailsReceiveMonitor.builder().amount(1535).build());
        monitors.add(DetailsReceiveMonitor.builder().amount(3001).build());
        monitors.add(DetailsReceiveMonitor.builder().amount(3555).build());
        monitors.add(DetailsReceiveMonitor.builder().amount(4000).build());
        monitors.add(DetailsReceiveMonitor.builder().amount(4001).build());
        monitors.add(DetailsReceiveMonitor.builder().amount(5000).build());
        monitors.add(DetailsReceiveMonitor.builder().amount(5000).build());
        monitors.add(DetailsReceiveMonitor.builder().amount(5000).build());
        Map<AmountRange, List<DetailsReceiveMonitor>> actual = statisticCalculateService.sortByAmountRange(monitors, DetailsReceiveMonitor::getAmount);
        assertEquals(4, actual.size());
        assertEquals(5, actual.get(AmountRange.builder().minAmount(1).maxAmount(1000).build()).size());
        assertEquals(2, actual.get(AmountRange.builder().minAmount(1001).maxAmount(2000).build()).size());
        assertFalse(actual.containsKey(AmountRange.builder().minAmount(2001).maxAmount(3000).build()));
        assertEquals(3, actual.get(AmountRange.builder().minAmount(3001).maxAmount(4000).build()).size());
        assertEquals(4, actual.get(AmountRange.builder().minAmount(4001).maxAmount(5000).build()).size());
    }

    @Test
    void sortByAmountRangeShouldReturnEmptyMapIfNoObjects() {
        List<DetailsReceiveMonitor> monitors = new ArrayList<>();
        assertEquals(0, statisticCalculateService.sortByAmountRange(monitors, DetailsReceiveMonitor::getAmount).size());
    }

    @Test
    void sortByMerchantShouldReturnEmptyMapIfNoObjects() {
        List<DetailsReceiveMonitor> monitors = new ArrayList<>();
        assertEquals(0, statisticCalculateService.sortByMerchant(monitors).size());
    }

    @Test
    void sortByMerchantShouldSortAttempts() {
        List<DetailsReceiveMonitor> monitors = new ArrayList<>();
        monitors.add(DetailsReceiveMonitor.builder().attempts(List.of(
                MerchantAttempt.builder().merchant("ALFA_TEAM").build(),
                MerchantAttempt.builder().merchant("BIT_ZONE").build(),
                MerchantAttempt.builder().merchant("ALFA_TEAM").build(),
                MerchantAttempt.builder().merchant("BIT_ZONE").build()
        )).build());
        monitors.add(DetailsReceiveMonitor.builder().attempts(List.of(
                MerchantAttempt.builder().merchant("EVO_PAY").build()
        )).build());
        monitors.add(DetailsReceiveMonitor.builder().attempts(List.of(
                MerchantAttempt.builder().merchant("EVO_PAY").build(),
                MerchantAttempt.builder().merchant("ALFA_TEAM").build(),
                MerchantAttempt.builder().merchant("EVO_PAY").build(),
                MerchantAttempt.builder().merchant("ALFA_TEAM").build()
        )).build());
        monitors.add(DetailsReceiveMonitor.builder().attempts(List.of(
                MerchantAttempt.builder().merchant("BIT_ZONE").build(),
                MerchantAttempt.builder().merchant("BIT_ZONE").build(),
                MerchantAttempt.builder().merchant("BIT_ZONE").build(),
                MerchantAttempt.builder().merchant("BIT_ZONE").build()
        )).build());
        Map<String, List<MerchantAttempt>> actual = statisticCalculateService.sortByMerchant(monitors);
        assertEquals(3, actual.size());

        assertTrue(actual.containsKey("ALFA_TEAM"));
        assertEquals(4, actual.get("ALFA_TEAM").size());
        assertTrue(actual.containsKey("EVO_PAY"));
        assertEquals(3, actual.get("EVO_PAY").size());
        assertTrue(actual.containsKey("BIT_ZONE"));
        assertEquals(6, actual.get("BIT_ZONE").size());
    }
}