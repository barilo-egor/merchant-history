package tgb.cryptoexchange.merchanthistory.dto;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class MerchantHistoryResponseTest {

    @CsvSource(textBlock = """
            15155
            2626637108
            """)
    @ParameterizedTest
    void merchantHistoryShouldConstructWithMerchantHistoryData(Long dealId) {
        MerchantHistoryDTO dto = new MerchantHistoryDTO();
        dto.setDealId(dealId);
        assertEquals(dto, new MerchantHistoryResponse(dto).getData());
    }
}