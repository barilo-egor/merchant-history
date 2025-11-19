package tgb.cryptoexchange.merchanthistory.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tgb.cryptoexchange.merchanthistory.dto.MerchantHistoryDTO;
import tgb.cryptoexchange.merchanthistory.dto.MerchantHistoryRequest;
import tgb.cryptoexchange.merchanthistory.service.MerchantHistoryService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(MerchantHistoryController.class)
class MerchantHistoryControllerTest {

    @MockitoBean
    private MerchantHistoryService merchantHistoryService;

    @Mock
    private Page<MerchantHistoryDTO> page;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getShouldReturnEmptyArrayIfHistoriesNotFound() throws Exception {
        when(page.getContent()).thenReturn(new ArrayList<>());
        when(page.getTotalElements()).thenReturn(0L);
        when(merchantHistoryService.findAll(any(), any())).thenReturn(page);
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @CsvSource("""
            5,10,e9c12d92-15a6-4a94-80e4-d5d4bf22baa7,17463634776,2025-11-10T06:55:23.671975234Z,398673285
            1,25,a901e865-a408-4269-95d8-7c935fce0d40,17324623466,2025-11-01T00:00:00.000000000Z,9695285239
            """)
    @ParameterizedTest
    void getShouldPassRequestObject(Integer pageNumber, Integer pageSize, String orderId, Long dealId, Instant createdAt,
                                    Long userId) throws Exception {
        when(page.getContent()).thenReturn(new ArrayList<>());
        when(page.getTotalElements()).thenReturn(0L);
        when(merchantHistoryService.findAll(any(), any())).thenReturn(page);
        ArgumentCaptor<MerchantHistoryRequest> requestCaptor = ArgumentCaptor.forClass(MerchantHistoryRequest.class);
        ArgumentCaptor<PageRequest> pageRequestCaptor = ArgumentCaptor.forClass(PageRequest.class);
        mockMvc.perform(get("/")
                .queryParam("pageNumber", String.valueOf(pageNumber))
                .queryParam("pageSize", String.valueOf(pageSize))
                .queryParam("orderId", String.valueOf(orderId))
                .queryParam("dealId", String.valueOf(dealId))
                .queryParam("createdAtFrom", createdAt.toString())
                .queryParam("userId", String.valueOf(userId))
        );
        verify(merchantHistoryService).findAll(pageRequestCaptor.capture(), requestCaptor.capture());
        MerchantHistoryRequest actualRequest = requestCaptor.getValue();
        PageRequest actualPageRequest = pageRequestCaptor.getValue();
        assertAll(
                () -> assertEquals(orderId, actualRequest.getOrderId()),
                () -> assertEquals(dealId, actualRequest.getDealId()),
                () -> assertEquals(createdAt, actualRequest.getCreatedAtFrom()),
                () -> assertEquals(userId, actualRequest.getUserId()),
                () -> assertEquals(pageNumber, actualPageRequest.getPageNumber()),
                () -> assertEquals(pageSize, actualPageRequest.getPageSize())
        );
    }

    @ValueSource(ints = {5, 10})
    @ParameterizedTest
    void getShouldReturnHistories(int size) throws Exception {
        List<MerchantHistoryDTO> histories = new ArrayList<>();
        Instant now = Instant.now();
        for (long i = 0; i < size; i++) {
            MerchantHistoryDTO merchantHistoryDTO = new MerchantHistoryDTO();
            merchantHistoryDTO.setDealId(i * 10 + i);
            merchantHistoryDTO.setUserId(i);
            merchantHistoryDTO.setInitiatorApp("app" + i);
            merchantHistoryDTO.setCreatedAt(now.minusSeconds(i));
            merchantHistoryDTO.setMerchant("MERCHANT_" + i);
            merchantHistoryDTO.setMerchantOrderId("a901e865-a408-4269-95d8-7c935fce0d40" + i);
            merchantHistoryDTO.setRequestedAmount(5000 + (int) i);
            merchantHistoryDTO.setMerchantAmount(5001 + (int) i);
            merchantHistoryDTO.setMethod("CARD_" + i);
            merchantHistoryDTO.setDetails(i + " Bank 1234 1234 1234 1234");
            histories.add(merchantHistoryDTO);
        }
        when(merchantHistoryService.findAll(any(), any())).thenReturn(page);
        when(page.getContent()).thenReturn(histories);
        when(page.getTotalElements()).thenReturn((long) size);
        ResultActions perform = mockMvc.perform(get("/"));
        for (int i = 0; i < size; i++) {
            perform.andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data").isNotEmpty())
                    .andExpect(jsonPath("$.data[" + i + "].dealId").value(10 * i + i))
                    .andExpect(jsonPath("$.data[" + i + "].userId").value(i))
                    .andExpect(jsonPath("$.data[" + i + "].initiatorApp").value("app" + i))
                    .andExpect(jsonPath("$.data[" + i + "].createdAt").value(now.minusSeconds(i).toString()))
                    .andExpect(jsonPath("$.data[" + i + "].merchant").value("MERCHANT_" + i))
                    .andExpect(jsonPath("$.data[" + i + "].merchantOrderId").value("a901e865-a408-4269-95d8-7c935fce0d40" + i))
                    .andExpect(jsonPath("$.data[" + i + "].requestedAmount").value(5000 + i))
                    .andExpect(jsonPath("$.data[" + i + "].merchantAmount").value(5001 + i))
                    .andExpect(jsonPath("$.data[" + i + "].method").value("CARD_" + i))
                    .andExpect(jsonPath("$.data[" + i + "].details").value(i + " Bank 1234 1234 1234 1234"));
        }
        perform.andExpect(header().longValue("X-Total-Count", size));
    }
}