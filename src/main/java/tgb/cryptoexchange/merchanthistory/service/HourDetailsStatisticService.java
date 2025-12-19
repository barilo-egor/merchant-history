package tgb.cryptoexchange.merchanthistory.service;

import org.springframework.stereotype.Service;
import tgb.cryptoexchange.merchanthistory.entity.DetailsReceiveMonitor;
import tgb.cryptoexchange.merchanthistory.entity.HourDetailsStatistic;
import tgb.cryptoexchange.merchanthistory.entity.HourMerchantReceiveStatistic;
import tgb.cryptoexchange.merchanthistory.entity.MerchantAttempt;
import tgb.cryptoexchange.merchanthistory.entity.embeddable.AmountRange;
import tgb.cryptoexchange.merchanthistory.repository.HourDetailsStatisticRepository;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class HourDetailsStatisticService {

    private final HourDetailsStatisticRepository repository;

    private final StatisticCalculateService statisticCalculateService;

    private final DetailsReceiveDurationService detailsReceiveDurationService;

    private final HourMerchantReceiveStatisticService hourMerchantReceiveStatisticService;

    public HourDetailsStatisticService(HourDetailsStatisticRepository repository,
                                       StatisticCalculateService statisticCalculateService,
                                       DetailsReceiveDurationService detailsReceiveDurationService,
                                       HourMerchantReceiveStatisticService hourMerchantReceiveStatisticService) {
        this.repository = repository;
        this.statisticCalculateService = statisticCalculateService;
        this.detailsReceiveDurationService = detailsReceiveDurationService;
        this.hourMerchantReceiveStatisticService = hourMerchantReceiveStatisticService;
    }

    public void save(HourDetailsStatistic statistic) {
        repository.save(statistic);
    }

    public void create(Instant startTime, List<DetailsReceiveMonitor> monitors) {
        HourDetailsStatistic hourDetailsStatistic = new HourDetailsStatistic();
        hourDetailsStatistic.setStartTime(startTime);
        hourDetailsStatistic.setAvgDuration(statisticCalculateService.getAverageDuration(
                monitors, DetailsReceiveMonitor::getStartTime, DetailsReceiveMonitor::getEndTime
        ));
        hourDetailsStatistic.setCount(monitors.size());
        hourDetailsStatistic.setSuccessCount(statisticCalculateService.count(monitors, DetailsReceiveMonitor::isSuccess));
        buildDetailsReceiveDurations(hourDetailsStatistic, monitors);
        buildHourMerchantReceiveStatistics(hourDetailsStatistic, monitors);
        save(hourDetailsStatistic);
    }

    private void buildDetailsReceiveDurations(HourDetailsStatistic hourDetailsStatistic,
                                              List<DetailsReceiveMonitor> monitors) {
        Map<AmountRange, List<DetailsReceiveMonitor>> rangeMonitorsMap = statisticCalculateService.sortByAmountRange(
                monitors, DetailsReceiveMonitor::getAmount
        );
        for (Map.Entry<AmountRange, List<DetailsReceiveMonitor>> entry : rangeMonitorsMap.entrySet()) {
            hourDetailsStatistic.addDetailsReceiveDuration(detailsReceiveDurationService.build(entry.getKey(), entry.getValue()));
        }
    }

    private void buildHourMerchantReceiveStatistics(HourDetailsStatistic hourDetailsStatistic,
                                                    List<DetailsReceiveMonitor> monitors) {
        Map<String, List<MerchantAttempt>> merchantsAttempts = statisticCalculateService.sortByMerchant(monitors);
        for (Map.Entry<String, List<MerchantAttempt>> entry : merchantsAttempts.entrySet()) {
            HourMerchantReceiveStatistic merchantStatistic = hourMerchantReceiveStatisticService.build(entry.getKey(), entry.getValue());
            hourDetailsStatistic.addMerchantStatistic(merchantStatistic);
        }
    }
}
