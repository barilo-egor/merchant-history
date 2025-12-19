package tgb.cryptoexchange.merchanthistory.service;

import org.springframework.stereotype.Service;
import tgb.cryptoexchange.merchanthistory.entity.HourMerchantReceiveStatistic;
import tgb.cryptoexchange.merchanthistory.entity.MerchantAttempt;
import tgb.cryptoexchange.merchanthistory.entity.embeddable.AmountRange;

import java.util.List;
import java.util.Map;

@Service
public class HourMerchantReceiveStatisticService {

    private final StatisticCalculateService statisticCalculateService;

    private final MerchantReceiveDurationService merchantReceiveDurationService;

    public HourMerchantReceiveStatisticService(StatisticCalculateService statisticCalculateService,
                                               MerchantReceiveDurationService merchantReceiveDurationService) {
        this.statisticCalculateService = statisticCalculateService;
        this.merchantReceiveDurationService = merchantReceiveDurationService;
    }

    public HourMerchantReceiveStatistic build(String merchant, List<MerchantAttempt> merchantAttempts) {
        HourMerchantReceiveStatistic merchantStatistic = new HourMerchantReceiveStatistic();
        merchantStatistic.setMerchant(merchant);
        merchantStatistic.setAvgDuration(statisticCalculateService.getAverageDuration(
                merchantAttempts, MerchantAttempt::getStartTime, MerchantAttempt::getEndTime
        ));
        merchantStatistic.setCount(merchantAttempts.size());
        merchantStatistic.setSuccessCount(statisticCalculateService.count(merchantAttempts, MerchantAttempt::isSuccess));
        merchantStatistic.setErrorCount(statisticCalculateService.count(merchantAttempts, MerchantAttempt::isError));
        buildMerchantReceiveDurations(merchantStatistic, merchantAttempts);
        return merchantStatistic;
    }

    private void buildMerchantReceiveDurations(HourMerchantReceiveStatistic merchantStatistic, List<MerchantAttempt> attempts) {
        Map<AmountRange, List<MerchantAttempt>> rangeMerchantAttemptMap = statisticCalculateService.sortByAmountRange(
                attempts, merchantAttempt -> merchantAttempt.getMonitor().getAmount()
        );
        for (Map.Entry<AmountRange, List<MerchantAttempt>> entry : rangeMerchantAttemptMap.entrySet()) {
            merchantStatistic.addMerchantReceiveDuration(merchantReceiveDurationService.build(entry.getKey(), entry.getValue()));
        }
    }
}
