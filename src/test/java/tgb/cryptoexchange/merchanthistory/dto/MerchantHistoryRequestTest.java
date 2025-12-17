package tgb.cryptoexchange.merchanthistory.dto;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tgb.cryptoexchange.merchanthistory.entity.MerchantHistory;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MerchantHistoryRequestTest {

    @Mock
    private Root<MerchantHistory> root;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Predicate like;

    @Mock
    private Predicate equal;

    @Mock
    private Predicate greaterThanOrEqualTo;

    @Test
    void toPredicatesShouldAddOrderIdPredicate() {
        MerchantHistoryRequest request = new MerchantHistoryRequest();
        request.setOrderId("some-order-id");
        when(criteriaBuilder.like(any(), anyString())).thenReturn(like);
        List<Predicate> actual = request.toPredicates(root, criteriaBuilder);

        verify(root).get("merchantOrderId");
        verify(criteriaBuilder).like(any(), eq("some-order-id"));
        assertEquals(1, actual.size());
        assertEquals(like, actual.getFirst());
    }

    @Test
    void toPredicatesShouldAddDealIdPredicate() {
        MerchantHistoryRequest request = new MerchantHistoryRequest();
        request.setDealId(528615L);
        when(criteriaBuilder.equal(any(), anyLong())).thenReturn(equal);
        List<Predicate> actual = request.toPredicates(root, criteriaBuilder);

        verify(root).get("dealId");
        verify(criteriaBuilder).equal(any(), eq(528615L));
        assertEquals(1, actual.size());
        assertEquals(equal, actual.getFirst());
    }

    @Test
    void toPredicatesShouldAddCreatedAtFromPredicate() {
        MerchantHistoryRequest request = new MerchantHistoryRequest();
        Instant now = Instant.now();
        request.setCreatedAtFrom(now);
        when(criteriaBuilder.greaterThanOrEqualTo(any(), any(Instant.class))).thenReturn(greaterThanOrEqualTo);
        List<Predicate> actual = request.toPredicates(root, criteriaBuilder);

        verify(root).get("createdAt");
        verify(criteriaBuilder).greaterThanOrEqualTo(any(), eq(now));
        assertEquals(1, actual.size());
        assertEquals(greaterThanOrEqualTo, actual.getFirst());
    }

    @Test
    void toPredicatesShouldAddUserIdPredicate() {
        MerchantHistoryRequest request = new MerchantHistoryRequest();
        request.setUserId(396846723L);
        when(criteriaBuilder.equal(any(), anyLong())).thenReturn(equal);
        List<Predicate> actual = request.toPredicates(root, criteriaBuilder);

        verify(root).get("userId");
        verify(criteriaBuilder).equal(any(), eq(396846723L));
        assertEquals(1, actual.size());
        assertEquals(equal, actual.getFirst());
    }
}