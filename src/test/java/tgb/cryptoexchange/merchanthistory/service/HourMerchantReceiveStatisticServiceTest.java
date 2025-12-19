package tgb.cryptoexchange.merchanthistory.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tgb.cryptoexchange.merchanthistory.entity.HourMerchantReceiveStatistic;
import tgb.cryptoexchange.merchanthistory.entity.MerchantAttempt;
import tgb.cryptoexchange.merchanthistory.entity.MerchantReceiveDuration;
import tgb.cryptoexchange.merchanthistory.entity.embeddable.AmountRange;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HourMerchantReceiveStatisticServiceTest {

    @Mock
    private StatisticCalculateService statisticCalculateService;

    @Mock
    private MerchantReceiveDurationService merchantReceiveDurationService;

    @InjectMocks
    private HourMerchantReceiveStatisticService hourMerchantReceiveStatisticService;

    @CsvSource("""
            EVO_PAY,346046,15,3,34
            ALFA_TEAM,634641,2,3,13
            """)
    @ParameterizedTest
    void buildShouldBuildHourMerchantReceiveStatistic(String merchant, Long durationMillis, int successCount, int errorCount,
                                                      int count) {
        List<MerchantAttempt> attempts = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            attempts.add(new MerchantAttempt());
        }

        when(statisticCalculateService.getAverageDuration(eq(attempts), any(), any())).thenReturn(Duration.ofMillis(durationMillis));
        when(statisticCalculateService.count(eq(attempts), any()))
                .thenReturn(successCount)
                .thenReturn(errorCount);
        when(merchantReceiveDurationService.build(any(), any())).thenReturn(MerchantReceiveDuration.builder().build());
        Map<AmountRange, List<MerchantAttempt>> attemptsMap = new HashMap<>();
        attemptsMap.put(AmountRange.builder().minAmount(1).maxAmount(1000).build(), new ArrayList<>());
        attemptsMap.put(AmountRange.builder().minAmount(1001).maxAmount(2000).build(), new ArrayList<>());
        attemptsMap.put(AmountRange.builder().minAmount(2001).maxAmount(3000).build(), new ArrayList<>());
        attemptsMap.put(AmountRange.builder().minAmount(3001).maxAmount(4000).build(), new ArrayList<>());
        when(statisticCalculateService.sortByAmountRange(eq(attempts), any())).thenReturn(attemptsMap);

        HourMerchantReceiveStatistic actual = hourMerchantReceiveStatisticService.build(merchant, attempts);

        assertAll(
                () -> assertEquals(merchant, actual.getMerchant()),
                () -> assertEquals(durationMillis, actual.getAvgDuration().toMillis()),
                () -> assertEquals(count, actual.getCount()),
                () -> assertEquals(successCount, actual.getSuccessCount()),
                () -> assertEquals(errorCount, actual.getErrorCount()),
                () -> assertEquals(4, actual.getReceiveDurations().size())
        );
    }
}