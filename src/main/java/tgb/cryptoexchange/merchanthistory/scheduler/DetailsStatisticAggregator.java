package tgb.cryptoexchange.merchanthistory.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tgb.cryptoexchange.merchanthistory.service.StatisticBuildService;

@Component
@Slf4j
public class DetailsStatisticAggregator {
    private final StatisticBuildService statisticBuildService;

    public DetailsStatisticAggregator(StatisticBuildService statisticBuildService) {
        this.statisticBuildService = statisticBuildService;
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void aggregate() {
        statisticBuildService.buildStatistic();
    }
}
