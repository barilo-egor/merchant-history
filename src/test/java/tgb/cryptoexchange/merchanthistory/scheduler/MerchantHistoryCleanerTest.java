package tgb.cryptoexchange.merchanthistory.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tgb.cryptoexchange.merchanthistory.entity.MerchantHistory;
import tgb.cryptoexchange.merchanthistory.service.MerchantHistoryService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MerchantHistoryCleanerTest {

    @Mock
    private MerchantHistoryService merchantHistoryService;

    @InjectMocks
    private MerchantHistoryCleaner merchantHistoryCleaner;

    @Test
    void cleanMerchantHistoryShouldSkipIfNoHistories() {
        when(merchantHistoryService.findByCreatedAtBefore(any())).thenReturn(new ArrayList<>());
        merchantHistoryCleaner.cleanMerchantHistory();
        verify(merchantHistoryService, times(0)).deleteAll(any());
    }

    @ValueSource(ints = {1, 5, 10})
    @ParameterizedTest
    void cleanMerchantHistoryShouldDeleteAllMerchantHistories(int size) {
        List<MerchantHistory> merchantHistoryList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            MerchantHistory merchantHistory = new MerchantHistory();
            merchantHistory.setId((long) i);
            merchantHistoryList.add(merchantHistory);
        }
        when(merchantHistoryService.findByCreatedAtBefore(any())).thenReturn(merchantHistoryList);
        merchantHistoryCleaner.cleanMerchantHistory();
        verify(merchantHistoryService).deleteAll(merchantHistoryList);
    }
}