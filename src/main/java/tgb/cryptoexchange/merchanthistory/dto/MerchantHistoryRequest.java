package tgb.cryptoexchange.merchanthistory.dto;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.util.CollectionUtils;
import tgb.cryptoexchange.merchanthistory.entity.MerchantHistory;

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

    private String sort = "createdAt,desc";

    private String orderId;

    private String operationId;

    private String actorId;

    private Instant createdAtFrom;

    private Instant createdAtTo;

    private String initiatorApp;

    private String details;

    private List<String> merchants = new ArrayList<>();

    private Integer merchantAmount;

    private Integer requestedAmount;

    public List<Predicate> toPredicates(Root<MerchantHistory> root, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        if (Objects.nonNull(orderId)) {
            predicates.add(cb.like(root.get("merchantOrderId"), orderId));
        }
        if (Objects.nonNull(operationId)) {
            predicates.add(cb.equal(root.get("operationId"), operationId));
        }
        if (Objects.nonNull(actorId)) {
            predicates.add(cb.equal(root.get("actorId"), actorId));
        }
        if (Objects.nonNull(createdAtFrom)) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), createdAtFrom));
        }
        if (Objects.nonNull(createdAtTo)) {
            predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), createdAtTo));
        }
        if (Objects.nonNull(initiatorApp)) {
            predicates.add(cb.equal(root.get("initiatorApp"), initiatorApp));
        }
        if (Objects.nonNull(details)) {
            predicates.add(cb.like(root.get("details"), details));
        }
        List<Predicate> amountPredicates = new ArrayList<>();
        if (Objects.nonNull(merchantAmount)) {
            amountPredicates.add(cb.equal(root.get("merchantAmount"), merchantAmount));
        }
        if (Objects.nonNull(requestedAmount)) {
            amountPredicates.add(cb.equal(root.get("requestedAmount"), requestedAmount));
        }
        if (!amountPredicates.isEmpty()) {
            predicates.add(cb.or(amountPredicates.toArray(new Predicate[0])));
        }
        if (!CollectionUtils.isEmpty(merchants)) {
            predicates.add(root.get("merchant").in(merchants));
        }
        return predicates;
    }

}
