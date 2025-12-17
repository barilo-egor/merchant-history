package tgb.cryptoexchange.merchanthistory.service;

import org.springframework.stereotype.Service;
import tgb.cryptoexchange.merchanthistory.entity.HourDetailsStatistic;
import tgb.cryptoexchange.merchanthistory.repository.HourDetailsStatisticRepository;

@Service
public class HourDetailsStatisticService {

    private final HourDetailsStatisticRepository repository;

    public HourDetailsStatisticService(HourDetailsStatisticRepository repository) {
        this.repository = repository;
    }

    public void save(HourDetailsStatistic statistic) {
        repository.save(statistic);
    }
}
