package tgb.cryptoexchange.merchanthistory.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tgb.cryptoexchange.merchanthistory.dto.DetailsReceiveMonitorDTO;
import tgb.cryptoexchange.merchanthistory.entity.DetailsReceiveMonitor;
import tgb.cryptoexchange.merchanthistory.repository.DetailsReceiveMonitorRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class DetailsReceiveMonitorService {

    private final DetailsReceiveMonitorRepository detailsReceiveMonitorRepository;

    public DetailsReceiveMonitorService(DetailsReceiveMonitorRepository detailsReceiveMonitorRepository) {
        this.detailsReceiveMonitorRepository = detailsReceiveMonitorRepository;
    }

    @Transactional
    public void save(DetailsReceiveMonitorDTO detailsReceiveMonitorDTO) {
        detailsReceiveMonitorRepository.save(detailsReceiveMonitorDTO.toEntity());
    }

    public List<DetailsReceiveMonitor> findAllBeforeCurrentHour() {
        return detailsReceiveMonitorRepository.findAllByStartTimeBefore(Instant.now().truncatedTo(ChronoUnit.HOURS));
    }

    public void deleteAll(List<DetailsReceiveMonitor> monitors) {
        detailsReceiveMonitorRepository.deleteAll(monitors);
    }
}
