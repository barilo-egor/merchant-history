package tgb.cryptoexchange.merchanthistory.entity;

import jakarta.persistence.*;
import lombok.*;
import tgb.cryptoexchange.merchanthistory.entity.embeddable.AmountRange;

import java.time.Duration;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "merchantStatistic")
@ToString(exclude = "merchantStatistic")
public class MerchantReceiveDuration {

    @Id
    @GeneratedValue
    private Long id;

    private Duration duration;

    @ManyToOne
    private HourMerchantReceiveStatistic merchantStatistic;

    @Embedded
    private AmountRange amountRange;
}
