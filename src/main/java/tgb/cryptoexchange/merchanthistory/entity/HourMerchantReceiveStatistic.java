package tgb.cryptoexchange.merchanthistory.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "details")
@ToString(exclude = "details")
public class HourMerchantReceiveStatistic {

    @Id
    @GeneratedValue
    private Long id;

    private String merchant;

    @ManyToOne
    private HourDetailsStatistic details;

    private Duration avgDuration;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<MerchantReceiveDuration> receiveDurations;

    private Integer count;

    private Integer successCount;

    private Integer errorCount;
}
