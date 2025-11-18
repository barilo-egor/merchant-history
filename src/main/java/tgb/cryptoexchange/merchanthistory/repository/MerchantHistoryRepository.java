package tgb.cryptoexchange.merchanthistory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tgb.cryptoexchange.merchanthistory.bean.MerchantHistory;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface MerchantHistoryRepository extends JpaRepository<MerchantHistory, Long> {

    Optional<MerchantHistory> findByMerchantOrderId(String merchantOrderId);

    List<MerchantHistory> findByCreatedAtBefore(Instant createdAt);
}
