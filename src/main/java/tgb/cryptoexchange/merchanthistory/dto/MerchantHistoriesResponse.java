package tgb.cryptoexchange.merchanthistory.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import tgb.cryptoexchange.web.ApiResponse;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class MerchantHistoriesResponse extends ApiResponse<List<MerchantHistoryDTO>> {

    public MerchantHistoriesResponse(List<MerchantHistoryDTO> merchantHistories) {
        super();
        super.setSuccess(true);
        super.setData(merchantHistories);
    }
}
