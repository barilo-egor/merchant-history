package tgb.cryptoexchange.merchanthistory.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.Instant;

/**
 * Объект представляющий попытку получения реквизитов у мерчанта по определенному методу.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = "monitor")
@EqualsAndHashCode(exclude = "monitor")
public class MerchantAttempt {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Ссылка на объект статистики получения реквизитов
     */
    @ManyToOne
    private DetailsReceiveMonitor monitor;

    /**
     * Мерчант, у которого были запрошены реквизиты.
     */
    private String merchant;

    /**
     * Метод, по которому были запрошены реквизиты.
     */
    private String method;

    /**
     * Время перед запросом реквизитов.
     */
    private Instant startTime;

    /**
     * Время после получения ответа.
     */
    private Instant endTime;

    /**
     * Индикатор получения реквизитов: true если реквизиты были получены, false если нет.
     */
    private boolean success;

    /**
     * Индикатор ошибки при выполнении запроса: true если во время получения реквизитов произошла ошибка, false если ошибки не было.
     */
    private boolean error;
}
