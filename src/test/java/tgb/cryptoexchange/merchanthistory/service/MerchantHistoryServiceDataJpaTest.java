package tgb.cryptoexchange.merchanthistory.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import tgb.cryptoexchange.merchanthistory.dto.MerchantHistoryDTO;
import tgb.cryptoexchange.merchanthistory.dto.MerchantHistoryRequest;
import tgb.cryptoexchange.merchanthistory.entity.MerchantHistory;
import tgb.cryptoexchange.merchanthistory.repository.MerchantHistoryRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import(MerchantHistoryService.class)
class MerchantHistoryServiceDataJpaTest {

    @Autowired
    private MerchantHistoryService merchantHistoryService;

    @Autowired
    private MerchantHistoryRepository merchantHistoryRepository;

    @BeforeEach
    void setUp() {
        merchantHistoryRepository.deleteAll();
    }

    @Test
    void findAllShouldReturnEmptyListIfNoRecords() {
        MerchantHistoryRequest request = new MerchantHistoryRequest();
        assertTrue(merchantHistoryService.findAll(PageRequest.of(0, 10), request).isEmpty());
    }

    @Test
    void findAllShouldReturnEmptyListIfRequestParamDoesNotMatch() {
        MerchantHistoryRequest request = new MerchantHistoryRequest();
        request.setOrderId("first-order-id");
        MerchantHistory merchantHistory = new MerchantHistory();
        merchantHistory.setMerchantOrderId("second-order-id");
        merchantHistoryRepository.save(merchantHistory);
        assertTrue(merchantHistoryService.findAll(PageRequest.of(0, 10), request).isEmpty());
    }

    @Test
    void findAllShouldReturnRecordIfOrderIdMatch() {
        MerchantHistoryRequest request = new MerchantHistoryRequest();
        request.setOrderId("first-order-id");
        MerchantHistory merchantHistory = new MerchantHistory();
        merchantHistory.setMerchantOrderId("first-order-id");
        merchantHistoryRepository.save(merchantHistory);
        Page<MerchantHistoryDTO> actual = merchantHistoryService.findAll(PageRequest.of(0, 10), request);
        assertEquals(1, actual.getTotalElements());
        assertEquals("first-order-id", actual.getContent().getFirst().getMerchantOrderId());
    }

    @Test
    void findAllShouldReturnRecordIfDealIdMatch() {
        MerchantHistoryRequest request = new MerchantHistoryRequest();
        request.setDealId(500414L);
        MerchantHistory merchantHistory = new MerchantHistory();
        merchantHistory.setDealId(500414L);
        merchantHistoryRepository.save(merchantHistory);
        Page<MerchantHistoryDTO> actual = merchantHistoryService.findAll(PageRequest.of(0, 10), request);
        assertEquals(1, actual.getTotalElements());
        assertEquals(500414L, actual.getContent().getFirst().getDealId());
    }

    @Test
    void findAllShouldReturnRecordIfCreatedAtMatch() {
        MerchantHistoryRequest request = new MerchantHistoryRequest();
        Instant now = Instant.now();
        request.setCreatedAtFrom(now.minus(1, ChronoUnit.DAYS));
        MerchantHistory merchantHistory = new MerchantHistory();
        merchantHistory.setCreatedAt(now);
        merchantHistoryRepository.save(merchantHistory);
        Page<MerchantHistoryDTO> actual = merchantHistoryService.findAll(PageRequest.of(0, 10), request);
        assertEquals(1, actual.getTotalElements());
        assertEquals(now, actual.getContent().getFirst().getCreatedAt());
    }

    @Test
    void findAllShouldReturnRecordIfUserIdMatch() {
        MerchantHistoryRequest request = new MerchantHistoryRequest();
        request.setUserId(398543096L);
        MerchantHistory merchantHistory = new MerchantHistory();
        merchantHistory.setUserId(398543096L);
        merchantHistoryRepository.save(merchantHistory);
        Page<MerchantHistoryDTO> actual = merchantHistoryService.findAll(PageRequest.of(0, 10), request);
        assertEquals(1, actual.getTotalElements());
        assertEquals(398543096L, actual.getContent().getFirst().getUserId());
    }
}
