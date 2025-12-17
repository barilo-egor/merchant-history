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
@EqualsAndHashCode(exclude = "hourDetailsStatistic")
@ToString(exclude = "hourDetailsStatistic")
public class DetailsReceiveDuration {

    @Id
    @GeneratedValue
    private Long id;

    private Duration duration;

    @ManyToOne
    private HourDetailsStatistic hourDetailsStatistic;

    @Embedded
    private AmountRange amountRange;
}
