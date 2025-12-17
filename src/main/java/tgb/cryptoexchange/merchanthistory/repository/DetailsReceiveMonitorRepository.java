package tgb.cryptoexchange.merchanthistory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tgb.cryptoexchange.merchanthistory.entity.DetailsReceiveMonitor;

import java.time.Instant;
import java.util.List;

public interface DetailsReceiveMonitorRepository extends JpaRepository<DetailsReceiveMonitor, Long> {

    List<DetailsReceiveMonitor> findAllByStartTimeBefore(Instant startTime);
}
