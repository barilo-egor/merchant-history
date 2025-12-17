package tgb.cryptoexchange.merchanthistory.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tgb.cryptoexchange.merchanthistory.entity.*;
import tgb.cryptoexchange.merchanthistory.entity.embeddable.AmountRange;
import tgb.cryptoexchange.merchanthistory.service.DetailsReceiveMonitorService;
import tgb.cryptoexchange.merchanthistory.service.HourDetailsStatisticService;

import java.time.Duration;
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

    public DetailsStatisticAggregator(DetailsReceiveMonitorService detailsReceiveMonitorService,
                                      HourDetailsStatisticService hourDetailsStatisticService) {
        this.detailsReceiveMonitorService = detailsReceiveMonitorService;
        this.hourDetailsStatisticService = hourDetailsStatisticService;
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
        hourDetailsStatistic.setAvgDuration(getAverageDuration(monitors));
        hourDetailsStatistic.setCount(monitors.size());
        hourDetailsStatistic.setSuccessCount((int) monitors.stream().filter(DetailsReceiveMonitor::isSuccess).count());
        buildDetailsReceiveDurations(hourDetailsStatistic, monitors);
        buildHourMerchantReceiveStatistics(hourDetailsStatistic, monitors);
        hourDetailsStatisticService.save(hourDetailsStatistic);
    }

    private void buildDetailsReceiveDurations(HourDetailsStatistic hourDetailsStatistic,
                                              List<DetailsReceiveMonitor> monitors) {
        Map<Integer, List<DetailsReceiveMonitor>> rangeMonitorsMap = monitors.stream()
                .collect(Collectors.groupingBy(monitor -> monitor.getAmount() / 1000));
        for (Map.Entry<Integer, List<DetailsReceiveMonitor>> entry : rangeMonitorsMap.entrySet()) {
            int minAmount = entry.getKey() * 1000 + 1;
            int maxAmount = minAmount + 1000;
            DetailsReceiveDuration detailsReceiveDuration = new DetailsReceiveDuration();
            detailsReceiveDuration.setAmountRange(AmountRange.builder().minAmount(minAmount).maxAmount(maxAmount).build());
            detailsReceiveDuration.setDuration(getAverageDuration(entry.getValue()));
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
            merchantStatistic.setAvgDuration(getMerchantAttemptAverageDuration(entry.getValue()));
            merchantStatistic.setCount(entry.getValue().size());
            merchantStatistic.setSuccessCount((int) entry.getValue().stream()
                    .filter(MerchantAttempt::isSuccess)
                    .count());
            merchantStatistic.setErrorCount((int) entry.getValue().stream()
                    .filter(MerchantAttempt::isError)
                    .count());
            buildMerchantReceiveDurations(merchantStatistic, entry.getValue());
            hourDetailsStatistic.addMerchantStatistic(merchantStatistic);
        }
    }

    private void buildMerchantReceiveDurations(HourMerchantReceiveStatistic merchantStatistic, List<MerchantAttempt> attempts) {
        Map<Integer, List<MerchantAttempt>> rangeMerchantAttemptMap = attempts.stream()
                .collect(Collectors.groupingBy(attempt -> attempt.getMonitor().getAmount() / 1000));
        for (Map.Entry<Integer, List<MerchantAttempt>> mapEntry : rangeMerchantAttemptMap.entrySet()) {
            int minAmount = mapEntry.getKey() * 1000 + 1;
            int maxAmount = minAmount + 1000;
            MerchantReceiveDuration merchantReceiveDuration = new MerchantReceiveDuration();
            merchantReceiveDuration.setAmountRange(AmountRange.builder().minAmount(minAmount).maxAmount(maxAmount).build());
            merchantReceiveDuration.setDuration(getMerchantAttemptAverageDuration(mapEntry.getValue()));
            merchantStatistic.addMerchantReceiveDuration(merchantReceiveDuration);
        }
    }

    private Duration getMerchantAttemptAverageDuration(List<MerchantAttempt> merchantAttempts) {
        double averageMillis = merchantAttempts.stream()
                .mapToLong(t -> Duration.between(t.getStartTime(), t.getEndTime()).toMillis())
                .average()
                .orElse(0.0);
        return Duration.ofMillis((long) averageMillis);
    }

    private Duration getAverageDuration(List<DetailsReceiveMonitor> monitors) {
        double averageMillis = monitors.stream()
                .mapToLong(t -> Duration.between(t.getStartTime(), t.getEndTime()).toMillis())
                .average()
                .orElse(0.0);
        return Duration.ofMillis((long) averageMillis);
    }
}
