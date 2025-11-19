package tgb.cryptoexchange.merchanthistory.dto;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import tgb.cryptoexchange.merchanthistory.bean.MerchantHistory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class MerchantHistoryRequest {

    @Min(0)
    private Integer pageNumber = 0;

    @Max(100)
    @Min(1)
    private Integer pageSize = 20;

    private String orderId;

    private Long dealId;

    private Instant createdAtFrom;

    private Long userId;

    public List<Predicate> toPredicates(Root<MerchantHistory> root, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        if (Objects.nonNull(orderId)) {
            predicates.add(cb.like(root.get("merchantOrderId"), orderId));
        }
        if (Objects.nonNull(dealId)) {
            predicates.add(cb.equal(root.get("dealId"), dealId));
        }
        if (Objects.nonNull(createdAtFrom)) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), createdAtFrom));
        }
        if (Objects.nonNull(userId)) {
            predicates.add(cb.equal(root.get("userId"), userId));
        }
        return predicates;
    }
}
