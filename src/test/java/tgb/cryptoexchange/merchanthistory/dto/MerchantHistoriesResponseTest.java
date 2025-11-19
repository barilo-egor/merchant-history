package tgb.cryptoexchange.merchanthistory.dto;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class MerchantHistoriesResponseTest {

    @CsvSource(textBlock = """
            15155
            2626637108
            """)
    @ParameterizedTest
    void merchantHistoryShouldConstructWithMerchantHistoryData(Long dealId) {
        MerchantHistoryDTO dto = new MerchantHistoryDTO();
        dto.setDealId(dealId);
        List<MerchantHistoryDTO> list = new ArrayList<>();
        list.add(dto);
        assertEquals(dto, new MerchantHistoriesResponse(list).getData().getFirst());
    }
}