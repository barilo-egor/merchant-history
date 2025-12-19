package tgb.cryptoexchange.merchanthistory.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tgb.cryptoexchange.merchanthistory.entity.DetailsReceiveDuration;
import tgb.cryptoexchange.merchanthistory.entity.DetailsReceiveMonitor;
import tgb.cryptoexchange.merchanthistory.entity.embeddable.AmountRange;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DetailsReceiveDurationServiceTest {

    @Mock
    private StatisticCalculateService statisticCalculateService;

    @InjectMocks
    private DetailsReceiveDurationService detailsReceiveDurationService;

    @CsvSource({"""
            68350,1000,2000
            235643,1,16000
            24675,1,2
            """
    })
    @ParameterizedTest
    void buildShouldBuildDetailsReceiveDuration(long durationMillis, int minAmount, int maxAmount) {
        when(statisticCalculateService.getAverageDuration(anyList(), any(), any())).thenReturn(Duration.ofMillis(durationMillis));
        AmountRange amountRange = AmountRange.builder().minAmount(minAmount).maxAmount(maxAmount).build();
        List<DetailsReceiveMonitor> monitors = new ArrayList<>();
        monitors.add(mock(DetailsReceiveMonitor.class));
        monitors.add(mock(DetailsReceiveMonitor.class));
        monitors.add(mock(DetailsReceiveMonitor.class));
        DetailsReceiveDuration actual = detailsReceiveDurationService.build(amountRange, monitors);
        verify(statisticCalculateService).getAverageDuration(eq(monitors), any(), any());
        assertAll(
                () -> assertNull(actual.getId()),
                () -> assertEquals(amountRange, actual.getAmountRange()),
                () -> assertEquals(durationMillis, actual.getDuration().toMillis())
        );
    }
}