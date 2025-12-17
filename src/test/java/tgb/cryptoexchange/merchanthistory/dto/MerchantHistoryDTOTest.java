package tgb.cryptoexchange.merchanthistory.dto;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import tgb.cryptoexchange.merchanthistory.entity.MerchantHistory;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class MerchantHistoryDTOTest {

    @CsvSource(textBlock = """
            123532,646262,money,2023-10-15T10:30:00Z,ALFA_TEAM,b9519d18-7ecf-47fd-ae74-0eca84d8656e,5000,5001,SBP,ALFA 1234123412341234
            7654912,4256,bulba,2025-01-22T17:36:00Z,ALFA_TEAM,869b6ba4-fc34-4df5-910c-cf69a05027b9,12506,12506,CARD,Сбер 89879875723
            """)
    @ParameterizedTest
    void fromEntityShouldCopyObject(Long dealId, Long userId, String initiatorApp, Instant createdAt, String merchant, String merchantOrderId,
                                    Integer requestedAmount, Integer merchantAmount, String method, String details) {
        MerchantHistory merchantHistory = new MerchantHistory();
        merchantHistory.setDealId(dealId);
        merchantHistory.setUserId(userId);
        merchantHistory.setInitiatorApp(initiatorApp);
        merchantHistory.setCreatedAt(createdAt);
        merchantHistory.setMerchant(merchant);
        merchantHistory.setMerchantOrderId(merchantOrderId);
        merchantHistory.setRequestedAmount(requestedAmount);
        merchantHistory.setMerchantAmount(merchantAmount);
        merchantHistory.setMethod(method);
        merchantHistory.setDetails(details);
        MerchantHistoryDTO dto = MerchantHistoryDTO.fromEntity(merchantHistory);
        assertEquals(dealId, dto.getDealId());
        assertEquals(userId, dto.getUserId());
        assertEquals(initiatorApp, dto.getInitiatorApp());
        assertEquals(createdAt, dto.getCreatedAt());
        assertEquals(merchant, dto.getMerchant());
        assertEquals(merchantOrderId, dto.getMerchantOrderId());
        assertEquals(requestedAmount, dto.getRequestedAmount());
        assertEquals(merchantAmount, dto.getMerchantAmount());
        assertEquals(method, dto.getMethod());
        assertEquals(details, dto.getDetails());
    }
}