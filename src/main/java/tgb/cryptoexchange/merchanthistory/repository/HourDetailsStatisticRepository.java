package tgb.cryptoexchange.merchanthistory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tgb.cryptoexchange.merchanthistory.entity.HourDetailsStatistic;

public interface HourDetailsStatisticRepository extends JpaRepository<HourDetailsStatistic, Integer> {
}
