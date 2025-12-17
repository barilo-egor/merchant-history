package tgb.cryptoexchange.merchanthistory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tgb.cryptoexchange.merchanthistory.entity.DetailsReceiveMonitor;

public interface DetailsReceiveMonitorRepository extends JpaRepository<DetailsReceiveMonitor, Long> {
}
