package tgb.cryptoexchange.merchanthistory.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tgb.cryptoexchange.merchanthistory.dto.DetailsReceiveMonitorDTO;
import tgb.cryptoexchange.merchanthistory.repository.DetailsReceiveMonitorRepository;

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
}
