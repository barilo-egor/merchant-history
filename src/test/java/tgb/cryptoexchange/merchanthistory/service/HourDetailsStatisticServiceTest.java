package tgb.cryptoexchange.merchanthistory.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tgb.cryptoexchange.merchanthistory.entity.*;
import tgb.cryptoexchange.merchanthistory.entity.embeddable.AmountRange;
import tgb.cryptoexchange.merchanthistory.repository.HourDetailsStatisticRepository;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HourDetailsStatisticServiceTest {

    @Mock
    private HourDetailsStatisticRepository repository;

    @Mock
    private StatisticCalculateService statisticCalculateService;

    @Mock
    private DetailsReceiveDurationService detailsReceiveDurationService;

    @Mock
    private HourMerchantReceiveStatisticService hourMerchantReceiveStatisticService;

    @InjectMocks
    private HourDetailsStatisticService hourDetailsStatisticService;

    @CsvSource("""
            19.12.2025 15:00:15,654127
            17.11.2025 01:00:15,256264
            """)
    @ParameterizedTest
    void createShouldBuildAndSaveStatistic(String dateTimeString, Long durationMillis) {
        List<DetailsReceiveMonitor> monitors = new ArrayList<>();
        Instant expectedInstant = LocalDateTime.parse(
                dateTimeString, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
        ).atZone(ZoneId.systemDefault()).toInstant();
        when(statisticCalculateService.getAverageDuration(eq(monitors), any(), any())).thenReturn(Duration.ofMillis(durationMillis));
        int expectedSuccessCount = 3;
        when(statisticCalculateService.count(eq(monitors), any())).thenReturn(expectedSuccessCount);

        Map<AmountRange, List<DetailsReceiveMonitor>> sortedMonitors = new HashMap<>();
        List<DetailsReceiveMonitor> monitorsFrom1To1000 = new ArrayList<>();
        monitorsFrom1To1000.add(DetailsReceiveMonitor.builder().build());
        monitorsFrom1To1000.add(DetailsReceiveMonitor.builder().build());
        monitorsFrom1To1000.add(DetailsReceiveMonitor.builder().build());
        monitorsFrom1To1000.add(DetailsReceiveMonitor.builder().build());
        AmountRange amountRangeFrom1To1000 = AmountRange.builder().minAmount(1).maxAmount(1000).build();
        sortedMonitors.put(amountRangeFrom1To1000, monitorsFrom1To1000);
        List<DetailsReceiveMonitor> monitorsFrom1001To2000 = new ArrayList<>();
        monitorsFrom1To1000.add(DetailsReceiveMonitor.builder().build());
        monitorsFrom1To1000.add(DetailsReceiveMonitor.builder().build());
        AmountRange amountRangeFrom1001To2000 = AmountRange.builder().minAmount(1001).maxAmount(2000).build();
        sortedMonitors.put(amountRangeFrom1001To2000, monitorsFrom1001To2000);
        List<DetailsReceiveMonitor> monitorsFrom3001To4000 = new ArrayList<>();
        monitorsFrom1To1000.add(DetailsReceiveMonitor.builder().build());
        monitorsFrom1To1000.add(DetailsReceiveMonitor.builder().build());
        monitorsFrom1To1000.add(DetailsReceiveMonitor.builder().build());
        monitorsFrom1To1000.add(DetailsReceiveMonitor.builder().build());
        monitorsFrom1To1000.add(DetailsReceiveMonitor.builder().build());
        monitorsFrom1To1000.add(DetailsReceiveMonitor.builder().build());
        AmountRange amountRangeFrom3001To4000 = AmountRange.builder().minAmount(3001).maxAmount(4000).build();
        sortedMonitors.put(amountRangeFrom3001To4000, monitorsFrom3001To4000);
        when(statisticCalculateService.sortByAmountRange(eq(monitors), any())).thenReturn(sortedMonitors);
        when(detailsReceiveDurationService.build(any(), any())).thenReturn(DetailsReceiveDuration.builder().build());

        Map<String, List<MerchantAttempt>> merchantAttempts = new HashMap<>();
        merchantAttempts.put("ALFA_TEAM", new ArrayList<>());
        merchantAttempts.put("ONLY_PAYS", new ArrayList<>());
        merchantAttempts.put("BIT_ZONE", new ArrayList<>());
        merchantAttempts.put("EVO_PAY", new ArrayList<>());
        merchantAttempts.put("PAYSCROW", new ArrayList<>());
        when(statisticCalculateService.sortByMerchant(monitors)).thenReturn(merchantAttempts);
        when(hourMerchantReceiveStatisticService.build(anyString(), anyList())).thenReturn(HourMerchantReceiveStatistic.builder().build());

        ArgumentCaptor<HourDetailsStatistic> statisticCaptor = ArgumentCaptor.forClass(HourDetailsStatistic.class);
        hourDetailsStatisticService.create(expectedInstant, monitors);
        verify(repository).save(statisticCaptor.capture());
        HourDetailsStatistic actual = statisticCaptor.getValue();

        assertAll(
                () -> assertEquals(expectedInstant, actual.getStartTime()),
                () -> assertEquals(durationMillis, actual.getAvgDuration().toMillis()),
                () -> assertEquals(monitors.size(), actual.getCount()),
                () -> assertEquals(expectedSuccessCount, actual.getSuccessCount()),
                () -> assertEquals(3, actual.getAmountDurations().size()),
                () -> assertEquals(merchantAttempts.size(), actual.getMerchantStatistics().size())
        );
    }
}