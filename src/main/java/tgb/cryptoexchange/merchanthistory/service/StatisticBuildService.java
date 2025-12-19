package tgb.cryptoexchange.merchanthistory.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tgb.cryptoexchange.merchanthistory.entity.DetailsReceiveMonitor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StatisticBuildService {

    private final HourDetailsStatisticService hourDetailsStatisticService;

    private final DetailsReceiveMonitorService detailsReceiveMonitorService;

    public StatisticBuildService(HourDetailsStatisticService hourDetailsStatisticService, DetailsReceiveMonitorService detailsReceiveMonitorService) {
        this.hourDetailsStatisticService = hourDetailsStatisticService;
        this.detailsReceiveMonitorService = detailsReceiveMonitorService;
    }

    @Transactional
    public void buildStatistic() {
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
            hourDetailsStatisticService.create(entry.getKey(), monitors);
            log.debug("Создание статистики для часа {} сформирована.", entry.getKey());
        }
        log.debug("Удаление агрегированных мониторов.");
        detailsReceiveMonitorService.deleteAll(monitors);

    }
}
