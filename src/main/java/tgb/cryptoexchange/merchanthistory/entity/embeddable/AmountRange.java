package tgb.cryptoexchange.merchanthistory.entity.embeddable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AmountRange {

    private Integer minAmount;

    private Integer maxAmount;
}
