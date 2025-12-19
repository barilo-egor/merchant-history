package tgb.cryptoexchange.merchanthistory.service;

import org.springframework.stereotype.Service;
import tgb.cryptoexchange.merchanthistory.entity.DetailsReceiveDuration;
import tgb.cryptoexchange.merchanthistory.entity.DetailsReceiveMonitor;
import tgb.cryptoexchange.merchanthistory.entity.embeddable.AmountRange;

import java.util.List;

@Service
public class DetailsReceiveDurationService {

    private final StatisticCalculateService statisticCalculateService;

    public DetailsReceiveDurationService(StatisticCalculateService statisticCalculateService) {
        this.statisticCalculateService = statisticCalculateService;
    }

    public DetailsReceiveDuration build(AmountRange amountRange, List<DetailsReceiveMonitor> monitors) {
        DetailsReceiveDuration detailsReceiveDuration = new DetailsReceiveDuration();
        detailsReceiveDuration.setAmountRange(amountRange);
        detailsReceiveDuration.setDuration(statisticCalculateService.getAverageDuration(
                monitors, DetailsReceiveMonitor::getStartTime, DetailsReceiveMonitor::getEndTime
        ));
        return detailsReceiveDuration;
    }
}
