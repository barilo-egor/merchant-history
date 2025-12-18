package tgb.cryptoexchange.merchanthistory.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tgb.cryptoexchange.merchanthistory.entity.DetailsReceiveMonitor;
import tgb.cryptoexchange.merchanthistory.service.DetailsReceiveMonitorService;
import tgb.cryptoexchange.merchanthistory.service.StatisticBuildService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DetailsStatisticAggregator {

    private final DetailsReceiveMonitorService detailsReceiveMonitorService;

    private final StatisticBuildService statisticBuildService;

    public DetailsStatisticAggregator(DetailsReceiveMonitorService detailsReceiveMonitorService,
                                      StatisticBuildService statisticBuildService) {
        this.detailsReceiveMonitorService = detailsReceiveMonitorService;
        this.statisticBuildService = statisticBuildService;
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void aggregate() {
        List<DetailsReceiveMonitor> monitors = detailsReceiveMonitorService.findAllBeforeCurrentHour();
        if (monitors.isEmpty()) {
            log.debug("Не найден ни один монитор для агрегации.");
            return;
        }
        log.debug("Найдено {} мониторов для агрегации.", monitors.size());
        monitors.sort(Comparator.comparing(DetailsReceiveMonitor::getStartTime));
        Map<Instant, List<DetailsReceiveMonitor>> groupedByHour = monitors.stream()
                .collect(Collectors.groupingBy(
                        obj -> obj.getStartTime().truncatedTo(ChronoUnit.HOURS)
                ));
        for (Map.Entry<Instant, List<DetailsReceiveMonitor>> entry : groupedByHour.entrySet()) {
            log.debug("Создание статистики для часа {}. Количество мониторов {}.", entry.getKey(), entry.getValue().size());
            statisticBuildService.buildStatistic(entry.getKey(), entry.getValue());
            log.debug("Создание статистики для часа {} сформирована.", entry.getKey());
        }
        log.debug("Удаление агрегированных мониторов.");
        detailsReceiveMonitorService.deleteAll(monitors);
    }










}
