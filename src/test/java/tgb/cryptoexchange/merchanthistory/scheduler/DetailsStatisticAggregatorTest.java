package tgb.cryptoexchange.merchanthistory.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tgb.cryptoexchange.merchanthistory.service.StatisticBuildService;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DetailsStatisticAggregatorTest {

    @Mock
    private StatisticBuildService statisticBuildService;

    @InjectMocks
    private DetailsStatisticAggregator detailsStatisticAggregator;

    @Test
    void aggregateShouldCallServiceMethod() {
        detailsStatisticAggregator.aggregate();
        verify(statisticBuildService).buildStatistic();
    }
}