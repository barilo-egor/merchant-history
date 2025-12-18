package tgb.cryptoexchange.merchanthistory.service;

import org.springframework.stereotype.Service;
import tgb.cryptoexchange.merchanthistory.entity.*;
import tgb.cryptoexchange.merchanthistory.entity.embeddable.AmountRange;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticBuildService {

    private final HourDetailsStatisticService hourDetailsStatisticService;

    private final StatisticCalculateService statisticCalculateService;

    public StatisticBuildService(HourDetailsStatisticService hourDetailsStatisticService,
                                 StatisticCalculateService statisticCalculateService) {
        this.hourDetailsStatisticService = hourDetailsStatisticService;
        this.statisticCalculateService = statisticCalculateService;
    }

    public void buildStatistic(Instant startTime, List<DetailsReceiveMonitor> monitors) {
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
