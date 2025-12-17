package tgb.cryptoexchange.merchanthistory.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "amountDurations")
@ToString(exclude = "amountDurations")
public class HourDetailsStatistic {

    @Id
    @GeneratedValue
    private Long id;

    private Instant startTime;

    private Duration avgDuration;

    private Integer count;

    private Integer successCount;

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DetailsReceiveDuration> amountDurations = new ArrayList<>();

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<HourMerchantReceiveStatistic> merchantStatistics = new ArrayList<>();

    public void addDetailsReceiveDuration(DetailsReceiveDuration detailsReceiveDuration) {
        detailsReceiveDuration.setHourDetailsStatistic(this);
        this.amountDurations.add(detailsReceiveDuration);
    }

    public void addMerchantStatistic(HourMerchantReceiveStatistic merchantStatistic) {
        merchantStatistic.setDetails(this);
        this.merchantStatistics.add(merchantStatistic);
    }
}
