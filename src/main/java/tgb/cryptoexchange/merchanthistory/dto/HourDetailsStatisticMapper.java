package tgb.cryptoexchange.merchanthistory.dto;

import org.mapstruct.Mapper;
import tgb.cryptoexchange.merchanthistory.entity.DetailsReceiveDuration;
import tgb.cryptoexchange.merchanthistory.entity.HourDetailsStatistic;
import tgb.cryptoexchange.merchanthistory.entity.HourMerchantReceiveStatistic;
import tgb.cryptoexchange.merchanthistory.entity.MerchantReceiveDuration;
import tgb.cryptoexchange.merchanthistory.entity.embeddable.AmountRange;

@Mapper(componentModel = "spring")
public interface HourDetailsStatisticMapper {

    HourDetailsStatisticDTO map(HourDetailsStatistic statistic);

    HourDetailsStatisticDTO.DetailsReceiveDuration map(DetailsReceiveDuration detailsReceiveDuration);

    HourDetailsStatisticDTO.HourMerchantReceiveStatistic map(HourMerchantReceiveStatistic hourMerchantReceiveStatistic);

    HourDetailsStatisticDTO.HourMerchantReceiveStatistic.MerchantReceiveDuration map(MerchantReceiveDuration merchantReceiveDuration);

    HourDetailsStatisticDTO.AmountRange map(AmountRange amountRange);
}
