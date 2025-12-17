package tgb.cryptoexchange.merchanthistory.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tgb.cryptoexchange.merchanthistory.dto.DetailsReceiveMonitorDTO;
import tgb.cryptoexchange.merchanthistory.entity.DetailsReceiveMonitor;
import tgb.cryptoexchange.merchanthistory.repository.DetailsReceiveMonitorRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DetailsReceiveMonitorServiceTest {

    @Mock
    private DetailsReceiveMonitorRepository detailsReceiveMonitorRepository;

    @InjectMocks
    private DetailsReceiveMonitorService detailsReceiveMonitorService;

    @Test
    void saveShouldCallRepositorySave() {
        DetailsReceiveMonitorDTO dto = new DetailsReceiveMonitorDTO();
        detailsReceiveMonitorService.save(dto);
        verify(detailsReceiveMonitorRepository).save(any(DetailsReceiveMonitor.class));
    }
}