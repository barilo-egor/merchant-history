package tgb.cryptoexchange.merchanthistory.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tgb.cryptoexchange.merchanthistory.dto.DetailsReceiveMonitorDTO;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DetailsReceiveMonitorTopicListenerTest {

    @Mock
    private DetailsReceiveMonitorService detailsReceiveMonitorService;

    @InjectMocks
    private DetailsReceiveMonitorTopicListener detailsReceiveMonitorTopicListener;

    @Test
    void receiveShouldCallSaveServiceMethod() {
        DetailsReceiveMonitorDTO dto = new DetailsReceiveMonitorDTO();
        detailsReceiveMonitorTopicListener.receive(dto);
        verify(detailsReceiveMonitorService).save(dto);
    }
}