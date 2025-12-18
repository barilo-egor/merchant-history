package tgb.cryptoexchange.merchanthistory.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tgb.cryptoexchange.merchanthistory.entity.*;
import tgb.cryptoexchange.merchanthistory.entity.embeddable.AmountRange;
import tgb.cryptoexchange.merchanthistory.service.DetailsReceiveMonitorService;
import tgb.cryptoexchange.merchanthistory.service.HourDetailsStatisticService;
import tgb.cryptoexchange.merchanthistory.service.StatisticCalculateService;

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

    private final HourDetailsStatisticService hourDetailsStatisticService;

    private final StatisticCalculateService statisticCalculateService;

    public DetailsStatisticAggregator(DetailsReceiveMonitorService detailsReceiveMonitorService,
                                      HourDetailsStatisticService hourDetailsStatisticService,
                                      StatisticCalculateService statisticCalculateService) {
        this.detailsReceiveMonitorService = detailsReceiveMonitorService;
        this.hourDetailsStatisticService = hourDetailsStatisticService;
        this.statisticCalculateService = statisticCalculateService;
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
            buildStatistic(entry.getKey(), entry.getValue());
            log.debug("Создание статистики для часа {} сформирована.", entry.getKey());
        }
        log.debug("Удаление агрегированных мониторов.");
        detailsReceiveMonitorService.deleteAll(monitors);
    }

    private void buildStatistic(Instant startTime, List<DetailsReceiveMonitor> monitors) {
        HourDetailsStatistic hourDetailsStatistic = new HourDetailsStatistic();
        hourDetailsStatistic.setStartTime(startTime);
        hourDetailsStatistic.setAvgDuration(statisticCalculateService.getAverageDuration(
                monitors, DetailsReceiveMonitor::getStartTime, DetailsReceiveMonitor::getEndTime
        ));
        hourDetailsStatistic.setCount(monitors.size());
        hourDetailsStatistic.setSuccessCount(statisticCalculateService.count(monitors, DetailsReceiveMonitor::isSuccess));
        buildDetailsReceiveDurations(hourDetailsStatistic, monitors);
        buildHourMerchantReceiveStatistics(hourDetailsStatistic, monitors);
        hourDetailsStatisticService.save(hourDetailsStatistic);
    }

    private void buildDetailsReceiveDurations(HourDetailsStatistic hourDetailsStatistic,
                                              List<DetailsReceiveMonitor> monitors) {
        Map<AmountRange, List<DetailsReceiveMonitor>> rangeMonitorsMap = statisticCalculateService.sortByAmountRange(
                monitors, DetailsReceiveMonitor::getAmount
        );
        for (Map.Entry<AmountRange, List<DetailsReceiveMonitor>> entry : rangeMonitorsMap.entrySet()) {
            DetailsReceiveDuration detailsReceiveDuration = new DetailsReceiveDuration();
            detailsReceiveDuration.setAmountRange(entry.getKey());
            detailsReceiveDuration.setDuration(statisticCalculateService.getAverageDuration(
                    entry.getValue(), DetailsReceiveMonitor::getStartTime, DetailsReceiveMonitor::getEndTime
            ));
            hourDetailsStatistic.addDetailsReceiveDuration(detailsReceiveDuration);
        }
    }

    private void buildHourMerchantReceiveStatistics(HourDetailsStatistic hourDetailsStatistic,
                                                    List<DetailsReceiveMonitor> monitors) {
        Map<String, List<MerchantAttempt>> merchantsAttempts = monitors.stream()
                .flatMap(monitor -> monitor.getAttempts().stream())
                .collect(Collectors.groupingBy(MerchantAttempt::getMerchant));
        for (Map.Entry<String, List<MerchantAttempt>> entry : merchantsAttempts.entrySet()) {
            HourMerchantReceiveStatistic merchantStatistic = new HourMerchantReceiveStatistic();
            merchantStatistic.setMerchant(entry.getKey());
            merchantStatistic.setAvgDuration(statisticCalculateService.getAverageDuration(
                    entry.getValue(), MerchantAttempt::getStartTime, MerchantAttempt::getEndTime
            ));
            merchantStatistic.setCount(entry.getValue().size());
            merchantStatistic.setSuccessCount(statisticCalculateService.count(entry.getValue(), MerchantAttempt::isSuccess));
            merchantStatistic.setSuccessCount(statisticCalculateService.count(entry.getValue(), MerchantAttempt::isError));
            buildMerchantReceiveDurations(merchantStatistic, entry.getValue());
            hourDetailsStatistic.addMerchantStatistic(merchantStatistic);
        }
    }

    private void buildMerchantReceiveDurations(HourMerchantReceiveStatistic merchantStatistic, List<MerchantAttempt> attempts) {
        Map<AmountRange, List<MerchantAttempt>> rangeMerchantAttemptMap = statisticCalculateService.sortByAmountRange(
                attempts, merchantAttempt -> merchantAttempt.getMonitor().getAmount()
        );
        for (Map.Entry<AmountRange, List<MerchantAttempt>> entry : rangeMerchantAttemptMap.entrySet()) {
            MerchantReceiveDuration merchantReceiveDuration = new MerchantReceiveDuration();
            merchantReceiveDuration.setAmountRange(entry.getKey());
            merchantReceiveDuration.setDuration(statisticCalculateService.getAverageDuration(
                    entry.getValue(), MerchantAttempt::getStartTime, MerchantAttempt::getEndTime)
            );
            merchantStatistic.addMerchantReceiveDuration(merchantReceiveDuration);
        }
    }


}
