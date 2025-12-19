package tgb.cryptoexchange.merchanthistory.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tgb.cryptoexchange.merchanthistory.entity.MerchantAttempt;
import tgb.cryptoexchange.merchanthistory.entity.MerchantReceiveDuration;
import tgb.cryptoexchange.merchanthistory.entity.embeddable.AmountRange;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MerchantReceiveDurationServiceTest {

    @Mock
    private StatisticCalculateService statisticCalculateService;

    @InjectMocks
    private MerchantReceiveDurationService merchantReceiveDurationService;

    @CsvSource({
            "1,1001,632064",
            "5001,6000,25460"
    })
    @ParameterizedTest
    void buildShouldBuildMerchantReceiveDuration(int minAmount, int maxAmount, long durationMillis) {
        List<MerchantAttempt> attempts = new ArrayList<>();
        AmountRange amountRange = new AmountRange(minAmount, maxAmount);
        when(statisticCalculateService.getAverageDuration(eq(attempts), any(), any())).thenReturn(Duration.ofMillis(durationMillis));
        MerchantReceiveDuration actual = merchantReceiveDurationService.build(amountRange, attempts);
        assertAll(
                () -> assertEquals(minAmount, actual.getAmountRange().getMinAmount()),
                () -> assertEquals(maxAmount, actual.getAmountRange().getMaxAmount()),
                () -> assertEquals(durationMillis, actual.getDuration().toMillis())
        );
    }
}