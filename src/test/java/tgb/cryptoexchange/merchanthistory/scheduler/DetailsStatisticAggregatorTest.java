package tgb.cryptoexchange.merchanthistory.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tgb.cryptoexchange.merchanthistory.entity.DetailsReceiveMonitor;
import tgb.cryptoexchange.merchanthistory.service.DetailsReceiveMonitorService;
import tgb.cryptoexchange.merchanthistory.service.HourDetailsStatisticService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DetailsStatisticAggregatorTest {

    @Mock
    private DetailsReceiveMonitorService detailsReceiveMonitorService;

    @Mock
    private HourDetailsStatisticService hourDetailsStatisticService;

    @InjectMocks
    private DetailsStatisticAggregator detailsStatisticAggregator;

    @Test
    void aggregateShouldSkipIfNoMonitors() {
        when(detailsReceiveMonitorService.findAllBeforeCurrentHour()).thenReturn(new ArrayList<>());
        detailsStatisticAggregator.aggregate();
        verify(hourDetailsStatisticService, times(0)).save(any());
        verify(detailsReceiveMonitorService, times(0)).deleteAll(anyList());
    }

    @ValueSource(ints = {1, 5, 10})
    @ParameterizedTest
    void aggregateShouldSaveOneStatisticForAnySizeOfMonitors(int size) {
        List<DetailsReceiveMonitor> monitors = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            DetailsReceiveMonitor monitor = new DetailsReceiveMonitor();
            monitor.setAmount(5000);
            monitor.setStartTime(Instant.now());
            monitor.setEndTime(Instant.now().plus(1, ChronoUnit.SECONDS));
            monitors.add(monitor);
        }
        when(detailsReceiveMonitorService.findAllBeforeCurrentHour()).thenReturn(monitors);
        detailsStatisticAggregator.aggregate();
        verify(hourDetailsStatisticService).save(any());
    }

    @ValueSource(ints = {1, 5, 10})
    @ParameterizedTest
    void aggregateShouldDeleteAllForAnySizeOfMonitors(int size) {
        List<DetailsReceiveMonitor> monitors = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            DetailsReceiveMonitor monitor = new DetailsReceiveMonitor();
            monitor.setAmount(5000);
            monitor.setStartTime(Instant.now());
            monitor.setEndTime(Instant.now().plus(1, ChronoUnit.SECONDS));
            monitors.add(monitor);
        }
        when(detailsReceiveMonitorService.findAllBeforeCurrentHour()).thenReturn(monitors);
        detailsStatisticAggregator.aggregate();
        verify(detailsReceiveMonitorService).deleteAll(monitors);
    }
}