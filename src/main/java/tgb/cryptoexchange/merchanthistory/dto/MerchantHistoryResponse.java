package tgb.cryptoexchange.merchanthistory.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import tgb.cryptoexchange.web.ApiResponse;

@EqualsAndHashCode(callSuper = true)
@Data
public class MerchantHistoryResponse extends ApiResponse<MerchantHistoryDTO> {

    public MerchantHistoryResponse(MerchantHistoryDTO merchantHistory) {
        super();
        super.setSuccess(true);
        super.setData(merchantHistory);
    }
}
