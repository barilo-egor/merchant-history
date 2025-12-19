package tgb.cryptoexchange.merchanthistory.service;

import org.springframework.stereotype.Service;
import tgb.cryptoexchange.merchanthistory.entity.MerchantAttempt;
import tgb.cryptoexchange.merchanthistory.entity.MerchantReceiveDuration;
import tgb.cryptoexchange.merchanthistory.entity.embeddable.AmountRange;

import java.util.List;

@Service
public class MerchantReceiveDurationService {

    private final StatisticCalculateService statisticCalculateService;

    public MerchantReceiveDurationService(StatisticCalculateService statisticCalculateService) {
        this.statisticCalculateService = statisticCalculateService;
    }

    public MerchantReceiveDuration build(AmountRange amountRange, List<MerchantAttempt> attempts) {
        MerchantReceiveDuration merchantReceiveDuration = new MerchantReceiveDuration();
        merchantReceiveDuration.setAmountRange(amountRange);
        merchantReceiveDuration.setDuration(statisticCalculateService.getAverageDuration(
                attempts, MerchantAttempt::getStartTime, MerchantAttempt::getEndTime)
        );
        return merchantReceiveDuration;
    }
}
