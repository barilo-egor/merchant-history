package tgb.cryptoexchange.merchanthistory.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tgb.cryptoexchange.merchanthistory.entity.DetailsReceiveDuration;
import tgb.cryptoexchange.merchanthistory.entity.HourDetailsStatistic;
import tgb.cryptoexchange.merchanthistory.entity.HourMerchantReceiveStatistic;
import tgb.cryptoexchange.merchanthistory.entity.MerchantReceiveDuration;
import tgb.cryptoexchange.merchanthistory.entity.embeddable.AmountRange;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class HourDetailsStatisticMapperTest {

    @Autowired
    private HourDetailsStatisticMapper hourDetailsStatisticMapper;

    @Test
    void mapShouldMapHourDetailsStatisticMapper() {
        HourDetailsStatistic hourDetailsStatistic = new HourDetailsStatistic();
        Long id = 1L;
        hourDetailsStatistic.setId(id);
        Instant startTime = Instant.now();
        hourDetailsStatistic.setStartTime(startTime);
        Duration duration = Duration.ZERO;
        hourDetailsStatistic.setAvgDuration(duration);
        Integer count = 5;
        hourDetailsStatistic.setCount(count);
        int successCount = 2;
        hourDetailsStatistic.setSuccessCount(successCount);
        DetailsReceiveDuration detailsReceiveDuration = new DetailsReceiveDuration();
        detailsReceiveDuration.setId(id);
        detailsReceiveDuration.setDuration(duration);
        AmountRange amountRange = new AmountRange();
        int minAmount = 100;
        int maxAmount = 200;
        amountRange.setMinAmount(minAmount);
        amountRange.setMaxAmount(maxAmount);
        detailsReceiveDuration.setAmountRange(amountRange);
        List<DetailsReceiveDuration> amountDurations = List.of(detailsReceiveDuration);
        hourDetailsStatistic.setAmountDurations(amountDurations);
        HourMerchantReceiveStatistic hourMerchantReceiveStatistic = new HourMerchantReceiveStatistic();
        hourMerchantReceiveStatistic.setId(id);
        String merchant = "ALFA_TEAM";
        hourMerchantReceiveStatistic.setMerchant(merchant);
        hourMerchantReceiveStatistic.setAvgDuration(duration);
        hourMerchantReceiveStatistic.setCount(count);
        hourMerchantReceiveStatistic.setSuccessCount(successCount);
        int errorCount = 1;
        hourMerchantReceiveStatistic.setErrorCount(errorCount);
        MerchantReceiveDuration merchantReceiveDuration = new MerchantReceiveDuration();
        merchantReceiveDuration.setId(id);
        merchantReceiveDuration.setDuration(duration);
        merchantReceiveDuration.setAmountRange(amountRange);
        List<MerchantReceiveDuration> receiveDurations = List.of(merchantReceiveDuration);
        hourMerchantReceiveStatistic.setReceiveDurations(receiveDurations);
        List<HourMerchantReceiveStatistic> merchantStatistics = List.of(hourMerchantReceiveStatistic);
        hourDetailsStatistic.setMerchantStatistics(merchantStatistics);
        HourDetailsStatisticDTO actual = hourDetailsStatisticMapper.map(hourDetailsStatistic);
        assertAll(
                () -> assertEquals(id, actual.getId()),
                () -> assertEquals(startTime, actual.getStartTime()),
                () -> assertEquals(duration, actual.getAvgDuration()),
                () -> assertEquals(count, actual.getCount()),
                () -> assertEquals(successCount, actual.getSuccessCount()),
                () -> assertEquals(id, actual.getAmountDurations().getFirst().getId()),
                () -> assertEquals(duration, actual.getAmountDurations().getFirst().getDuration()),
                () -> assertEquals(minAmount, actual.getAmountDurations().getFirst().getAmountRange().getMinAmount()),
                () -> assertEquals(maxAmount, actual.getAmountDurations().getFirst().getAmountRange().getMaxAmount()),
                () -> assertEquals(id, actual.getMerchantStatistics().getFirst().getId()),
                () -> assertEquals(merchant, actual.getMerchantStatistics().getFirst().getMerchant()),
                () -> assertEquals(duration, actual.getMerchantStatistics().getFirst().getAvgDuration()),
                () -> assertEquals(count, actual.getMerchantStatistics().getFirst().getCount()),
                () -> assertEquals(successCount, actual.getMerchantStatistics().getFirst().getSuccessCount()),
                () -> assertEquals(errorCount, actual.getMerchantStatistics().getFirst().getErrorCount()),
                () -> assertEquals(id, actual.getMerchantStatistics().getFirst().getReceiveDurations().getFirst().getId()),
                () -> assertEquals(duration, actual.getMerchantStatistics().getFirst().getReceiveDurations().getFirst().getDuration()),
                () -> assertEquals(amountRange.getMinAmount(), actual.getMerchantStatistics().getFirst().getReceiveDurations().getFirst().getAmountRange().getMinAmount()),
                () -> assertEquals(amountRange.getMaxAmount(), actual.getMerchantStatistics().getFirst().getReceiveDurations().getFirst().getAmountRange().getMaxAmount())
        );
    }
}