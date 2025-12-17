package tgb.cryptoexchange.merchanthistory.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tgb.cryptoexchange.merchanthistory.dto.MerchantDetailsReceiveEvent;
import tgb.cryptoexchange.merchanthistory.entity.MerchantHistory;
import tgb.cryptoexchange.merchanthistory.repository.MerchantHistoryRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MerchantHistoryServiceTest {

    @Mock
    private MerchantHistoryRepository merchantHistoryRepository;

    @InjectMocks
    private MerchantHistoryService merchantHistoryService;

    @CsvSource("""
            17551592595,398395786,banan,ALFA_TEAM,b9519d18-7ecf-47fd-ae74-0eca84d8656e,2500,2502,SBP,ALFA 79879878787
            17551564636,8050468384,money,WELL_BIT,869b6ba4-fc34-4df5-910c-cf69a05027b9,25300,25301,CARD,Банк развития 1234123412341234
            """)
    @ParameterizedTest
    void saveShouldSaveMerchantHistoryFromEvent(Long dealId, Long userId, String appId, String merchant, String orderId,
                                                Integer requestedAmount, Integer merchantAmount, String method, String details) {
        MerchantDetailsReceiveEvent event = new MerchantDetailsReceiveEvent();
        event.setDealId(dealId);
        event.setUserId(userId);
        event.setInitiatorApp(appId);
        event.setMerchant(merchant);
        event.setMerchantOrderId(orderId);
        event.setRequestedAmount(requestedAmount);
        event.setMerchantAmount(merchantAmount);
        event.setMethod(method);
        event.setDetails(details);
        ArgumentCaptor<MerchantHistory> historyCaptor = ArgumentCaptor.forClass(MerchantHistory.class);
        merchantHistoryService.save(event);
        verify(merchantHistoryRepository).save(historyCaptor.capture());
        MerchantHistory actual = historyCaptor.getValue();
        assertAll(
                () -> assertEquals(dealId, actual.getDealId()),
                () -> assertEquals(userId, actual.getUserId()),
                () -> assertEquals(appId, actual.getInitiatorApp()),
                () -> assertEquals(merchant, actual.getMerchant()),
                () -> assertEquals(orderId, actual.getMerchantOrderId()),
                () -> assertEquals(requestedAmount, actual.getRequestedAmount()),
                () -> assertEquals(merchantAmount, actual.getMerchantAmount()),
                () -> assertEquals(method, actual.getMethod()),
                () -> assertEquals(details, actual.getDetails())
        );
    }

    @Test
    void findByCreatedAtBeforeShouldCallRepositoryMethod() {
        Instant now = Instant.now();
        merchantHistoryService.findByCreatedAtBefore(now);
        verify(merchantHistoryRepository).findByCreatedAtBefore(now);
    }

    @Test
    void deleteAllShouldCallRepositoryMethod() {
        List<MerchantHistory> merchantHistoryList = new ArrayList<>();
        merchantHistoryList.add(new MerchantHistory());
        merchantHistoryService.deleteAll(merchantHistoryList);
        verify(merchantHistoryRepository).deleteAll(merchantHistoryList);
    }


}