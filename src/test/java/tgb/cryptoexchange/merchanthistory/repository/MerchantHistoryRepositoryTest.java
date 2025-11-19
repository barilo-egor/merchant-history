package tgb.cryptoexchange.merchanthistory.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import tgb.cryptoexchange.merchanthistory.bean.MerchantHistory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MerchantHistoryRepositoryTest {

    @Autowired
    private MerchantHistoryRepository merchantHistoryRepository;

    @BeforeEach
    void setUp() {
        merchantHistoryRepository.deleteAll();
    }

    @Test
    void findByCreatedAtBeforeShouldReturnEmptyListIfNoRecords() {
        assertTrue(merchantHistoryRepository.findByCreatedAtBefore(Instant.MIN).isEmpty());
    }

    @Test
    void findByCreatedAtBeforeShouldReturnEmptyListIfMerchantHistoryAfterDate() {
        MerchantHistory merchantHistory = new MerchantHistory();
        Instant createdAt = Instant.now().minus(1, ChronoUnit.DAYS);
        merchantHistory.setCreatedAt(createdAt);
        merchantHistory.setDealId(543L);
        merchantHistoryRepository.save(merchantHistory);
        assertTrue(merchantHistoryRepository.findByCreatedAtBefore(createdAt.minusSeconds(1)).isEmpty());
    }

    @Test
    void findByCreatedAtBeforeShouldReturnEmptyListIfMerchantHistoryEqualDate() {
        MerchantHistory merchantHistory = new MerchantHistory();
        Instant createdAt = Instant.now().minus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS);
        merchantHistory.setCreatedAt(createdAt);
        merchantHistory.setDealId(543L);
        merchantHistoryRepository.save(merchantHistory);
        assertTrue(merchantHistoryRepository.findByCreatedAtBefore(createdAt).isEmpty());
    }

    @Test
    void findByCreatedAtBeforeShouldReturnHistoryIfMerchantHistoryBeforeDate() {
        MerchantHistory merchantHistory = new MerchantHistory();
        Instant createdAt = Instant.now().minus(1, ChronoUnit.DAYS);
        merchantHistory.setCreatedAt(createdAt);
        merchantHistory.setDealId(543L);
        merchantHistoryRepository.save(merchantHistory);
        List<MerchantHistory> actual = merchantHistoryRepository.findByCreatedAtBefore(createdAt.plusSeconds(1));
        assertFalse(actual.isEmpty());
        assertEquals(actual.getFirst(), merchantHistory);
    }
}