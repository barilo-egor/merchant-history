package tgb.cryptoexchange.merchanthistory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tgb.cryptoexchange.merchanthistory.bean.MerchantHistory;

import java.time.Instant;
import java.util.List;

public interface MerchantHistoryRepository extends JpaRepository<MerchantHistory, Long>, JpaSpecificationExecutor<MerchantHistory> {

    List<MerchantHistory> findByCreatedAtBefore(Instant createdAt);
}
