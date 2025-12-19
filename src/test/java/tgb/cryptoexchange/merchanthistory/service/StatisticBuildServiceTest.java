package tgb.cryptoexchange.merchanthistory.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import tgb.cryptoexchange.merchanthistory.dto.HourDetailsStatisticDTO;
import tgb.cryptoexchange.merchanthistory.dto.HourDetailsStatisticMapper;
import tgb.cryptoexchange.merchanthistory.entity.DetailsReceiveMonitor;
import tgb.cryptoexchange.merchanthistory.entity.HourDetailsStatistic;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatisticBuildServiceTest {

    @Mock
    private HourDetailsStatisticMapper mapper;

    @Mock
    private HourDetailsStatisticService hourDetailsStatisticService;

    @Mock
    private DetailsReceiveMonitorService detailsReceiveMonitorService;

    @Mock
    private KafkaTemplate<String, HourDetailsStatisticDTO> statisticKafkaTemplate;

    private StatisticBuildService statisticBuildService;

    @BeforeEach
    void setUp() {
        statisticBuildService = new StatisticBuildService(
                "topicName", mapper, hourDetailsStatisticService, detailsReceiveMonitorService, statisticKafkaTemplate
        );
    }

    @Test
    void buildStatisticShouldSkipIfNoMonitors() {
        when(detailsReceiveMonitorService.findAllBeforeCurrentHour()).thenReturn(new ArrayList<>());
        statisticBuildService.buildStatistic();
        verify(hourDetailsStatisticService, times(0)).create(any(), any());
        verify(detailsReceiveMonitorService, times(0)).deleteAll(anyList());
    }

    @Test
    void buildStatisticShouldCreateStatisticAndDeleteMonitorsWithOneHour() {
        List<LocalDateTime> dates = new ArrayList<>();
        dates.add(LocalDateTime.of(2025, 1, 1, 15, 0, 0));
        dates.add(LocalDateTime.of(2025, 1, 1, 15, 15, 0));
        dates.add(LocalDateTime.of(2025, 1, 1, 15, 0, 1));
        dates.add(LocalDateTime.of(2025, 1, 1, 15, 59, 59));
        List<DetailsReceiveMonitor> monitors = new ArrayList<>();
        for (LocalDateTime date : dates) {
            monitors.add(DetailsReceiveMonitor.builder()
                    .startTime(date
                            .atZone(ZoneId.systemDefault())
                            .toInstant())
                    .build());
        }
        when(detailsReceiveMonitorService.findAllBeforeCurrentHour()).thenReturn(monitors);
        when(hourDetailsStatisticService.create(any(), any())).thenReturn(new HourDetailsStatistic());
        when(mapper.map(any(HourDetailsStatistic.class))).thenReturn(new HourDetailsStatisticDTO());

        statisticBuildService.buildStatistic();

        verify(hourDetailsStatisticService)
                .create(eq(LocalDateTime.of(2025, 1, 1, 15, 0)
                                .atZone(ZoneId.systemDefault()).toInstant()),
                        argThat(monitorsArg -> Objects.nonNull(monitorsArg) && monitorsArg.size() == 4)
                );
        verify(statisticKafkaTemplate).send(eq("topicName"), anyString(), any(HourDetailsStatisticDTO.class));
    }

    @Test
    void buildStatisticShouldCreateStatisticAndDeleteMonitors() {
        List<LocalDateTime> dates = new ArrayList<>();
        dates.add(LocalDateTime.of(2025, 1, 1, 15, 0, 0));
        dates.add(LocalDateTime.of(2025, 1, 1, 15, 15, 0));
        dates.add(LocalDateTime.of(2025, 1, 1, 15, 0, 1));
        dates.add(LocalDateTime.of(2025, 1, 1, 15, 59, 59));
        dates.add(LocalDateTime.of(2025, 1, 1, 18, 0, 0));
        dates.add(LocalDateTime.of(2025, 1, 1, 18, 0, 0));
        dates.add(LocalDateTime.of(2025, 1, 1, 18, 59, 59));
        dates.add(LocalDateTime.of(2025, 1, 1, 18, 59, 59));
        dates.add(LocalDateTime.of(2025, 1, 1, 20, 0, 0));
        dates.add(LocalDateTime.of(2025, 1, 1, 21, 0, 0));
        List<DetailsReceiveMonitor> monitors = new ArrayList<>();
        for (LocalDateTime date : dates) {
            monitors.add(DetailsReceiveMonitor.builder()
                    .startTime(date
                            .atZone(ZoneId.systemDefault())
                            .toInstant())
                    .build());
        }
        when(detailsReceiveMonitorService.findAllBeforeCurrentHour()).thenReturn(monitors);
        when(hourDetailsStatisticService.create(any(), any())).thenReturn(new HourDetailsStatistic());
        when(mapper.map(any(HourDetailsStatistic.class))).thenReturn(new HourDetailsStatisticDTO());

        statisticBuildService.buildStatistic();

        verify(hourDetailsStatisticService)
                .create(eq(LocalDateTime.of(2025, 1, 1, 15, 0)
                                .atZone(ZoneId.systemDefault()).toInstant()),
                        argThat(monitorsArg -> Objects.nonNull(monitorsArg) && monitorsArg.size() == 4)
                );
        verify(hourDetailsStatisticService)
                .create(eq(LocalDateTime.of(2025, 1, 1, 18, 0)
                                .atZone(ZoneId.systemDefault()).toInstant()),
                        argThat(monitorsArg -> Objects.nonNull(monitorsArg) && monitorsArg.size() == 4)
                );
        verify(hourDetailsStatisticService)
                .create(eq(LocalDateTime.of(2025, 1, 1, 20, 0)
                                .atZone(ZoneId.systemDefault()).toInstant()),
                        argThat(monitorsArg -> Objects.nonNull(monitorsArg) && monitorsArg.size() == 1)
                );
        verify(hourDetailsStatisticService)
                .create(eq(LocalDateTime.of(2025, 1, 1, 21, 0)
                                .atZone(ZoneId.systemDefault()).toInstant()),
                        argThat(monitorsArg -> Objects.nonNull(monitorsArg) && monitorsArg.size() == 1)
                );
        verify(statisticKafkaTemplate, times(4)).send(eq("topicName"), anyString(), any(HourDetailsStatisticDTO.class));
        verify(detailsReceiveMonitorService).deleteAll(monitors);
    }
}