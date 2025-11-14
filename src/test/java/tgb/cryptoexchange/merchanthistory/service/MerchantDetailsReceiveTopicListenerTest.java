package tgb.cryptoexchange.merchanthistory.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tgb.cryptoexchange.merchanthistory.dto.MerchantDetailsReceiveEvent;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MerchantDetailsReceiveTopicListenerTest {

    @Mock
    private MerchantHistoryService merchantHistoryService;

    @InjectMocks
    private MerchantDetailsReceiveTopicListener merchantDetailsReceiveTopicListener;

    @Test
    void receiveShouldSaveEvent() {
        MerchantDetailsReceiveEvent event = new MerchantDetailsReceiveEvent();
        event.setMerchant("ALFA_TEAM");
        event.setDealId(1755365755L);
        event.setRequestedAmount(5000);
        event.setMerchantAmount(5000);
        merchantDetailsReceiveTopicListener.receive(event);
        verify(merchantHistoryService).save(event);
    }
}