package tgb.cryptoexchange.merchanthistory.service;

import org.springframework.stereotype.Service;
import tgb.cryptoexchange.merchanthistory.entity.DetailsReceiveMonitor;
import tgb.cryptoexchange.merchanthistory.entity.MerchantAttempt;
import tgb.cryptoexchange.merchanthistory.entity.embeddable.AmountRange;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

@Service
public class StatisticCalculateService {

    public <T> Duration getAverageDuration(List<T> list, Function<T, Instant> startTimeFunction, Function<T, Instant> endTimeFunction) {
        double averageMillis = list.stream()
                .mapToLong(t -> Duration.between(startTimeFunction.apply(t), endTimeFunction.apply(t)).toMillis())
                .average()
                .orElse(0.0);
        return Duration.ofMillis((long) averageMillis);
    }

    public <T> int count(List<T> list, Predicate<T> predicate) {
        return (int) list.stream().filter(predicate).count();
    }

    public <T> Map<AmountRange, List<T>> sortByAmountRange(List<T> list, ToIntFunction<T> amountFunction) {
        Map<AmountRange, List<T>> result = new HashMap<>();
        for (T t : list) {
            int value = amountFunction.applyAsInt(t);
            int minAmount;
            if (value % 1000 == 0) {
                minAmount = ((value / 1000) - 1) * 1000 + 1;
            } else {
                minAmount = (amountFunction.applyAsInt(t) / 1000) * 1000 + 1;
            }
            int maxAmount = minAmount + 999;
            result.computeIfAbsent(
                    AmountRange.builder().minAmount(minAmount).maxAmount(maxAmount).build(),
                    k -> new ArrayList<>()
            ).add(t);
        }
        return result;
    }

    public Map<String, List<MerchantAttempt>> sortByMerchant(List<DetailsReceiveMonitor> monitors) {
        return monitors.stream()
                .flatMap(monitor -> monitor.getAttempts().stream())
                .collect(Collectors.groupingBy(MerchantAttempt::getMerchant));
    }
}
