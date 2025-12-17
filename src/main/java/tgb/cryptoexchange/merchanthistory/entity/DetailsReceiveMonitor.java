package tgb.cryptoexchange.merchanthistory.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Объект статистики получения реквизитов. Содержит информацию о попытке получения реквизитов для клиента.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = "attempts")
@EqualsAndHashCode(exclude = "attempts")
@Entity
public class DetailsReceiveMonitor {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Пользовательский идентификатор сделки.
     */
    private Long dealId;

    /**
     * Сумма сделки.
     */
    private Integer amount;

    /**
     * Начальное время поиска реквизитов.
     */
    private Instant startTime;

    /**
     * Конечное время поиска реквизитов.
     */
    private Instant endTime;

    /**
     * Индикатор получения реквизитов: true если реквизит был получен, false если нет.
     */
    private boolean success;

    /**
     * Попытки получения реквизитов у мерчантов
     */
    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MerchantAttempt> attempts = new ArrayList<>();

    public void addAttempt(MerchantAttempt attempt) {
        attempt.setMonitor(this);
        attempts.add(attempt);
    }
}
