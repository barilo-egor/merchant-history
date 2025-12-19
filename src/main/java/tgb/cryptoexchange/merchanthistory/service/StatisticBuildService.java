package tgb.cryptoexchange.merchanthistory.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tgb.cryptoexchange.merchanthistory.dto.HourDetailsStatisticDTO;
import tgb.cryptoexchange.merchanthistory.dto.HourDetailsStatisticMapper;
import tgb.cryptoexchange.merchanthistory.entity.DetailsReceiveMonitor;
import tgb.cryptoexchange.merchanthistory.entity.HourDetailsStatistic;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StatisticBuildService {

    private final String topicName;

    private final HourDetailsStatisticMapper mapper;

    private final HourDetailsStatisticService hourDetailsStatisticService;

    private final DetailsReceiveMonitorService detailsReceiveMonitorService;

    private final KafkaTemplate<String, HourDetailsStatisticDTO> statisticKafkaTemplate;

    public StatisticBuildService(@Value("${kafka.topic.merchant-history.statistic}") String topicName,
                                 HourDetailsStatisticMapper mapper,
                                 HourDetailsStatisticService hourDetailsStatisticService,
                                 DetailsReceiveMonitorService detailsReceiveMonitorService,
                                 KafkaTemplate<String, HourDetailsStatisticDTO> statisticKafkaTemplate) {
        this.topicName = topicName;
        this.mapper = mapper;
        this.hourDetailsStatisticService = hourDetailsStatisticService;
        this.detailsReceiveMonitorService = detailsReceiveMonitorService;
        this.statisticKafkaTemplate = statisticKafkaTemplate;
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
            HourDetailsStatistic hourDetailsStatistic;
            try {
                hourDetailsStatistic = hourDetailsStatisticService.create(entry.getKey(), entry.getValue());
            } catch (Exception e) {
                log.error("Ошибка при попытке сформировать статистику для часа {}: {}", entry.getKey(), e.getMessage(), e);
                continue;
            }
            statisticKafkaTemplate.send(topicName, UUID.randomUUID().toString(), mapper.map(hourDetailsStatistic));
            log.debug("Создание статистики для часа {} сформирована.", entry.getKey());
        }
        log.debug("Удаление агрегированных мониторов.");
        detailsReceiveMonitorService.deleteAll(monitors);

    }
}
