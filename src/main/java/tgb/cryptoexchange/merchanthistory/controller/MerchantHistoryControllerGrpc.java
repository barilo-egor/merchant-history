package tgb.cryptoexchange.merchanthistory.controller;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.grpc.server.service.GrpcService;
import tgb.cryptoexchange.grpc.generated.MerchantHistoryRequestGrpc;
import tgb.cryptoexchange.grpc.generated.MerchantHistoryResponseGrpc;
import tgb.cryptoexchange.grpc.generated.MerchantHistoryServiceGrpc;
import tgb.cryptoexchange.merchanthistory.dto.MerchantHistoryDTO;
import tgb.cryptoexchange.merchanthistory.dto.MerchantHistoryRequest;
import tgb.cryptoexchange.merchanthistory.mapper.MerchantHistoryGrpcMapper;
import tgb.cryptoexchange.merchanthistory.service.MerchantHistoryService;

@Slf4j
@GrpcService
public class MerchantHistoryControllerGrpc extends MerchantHistoryServiceGrpc.MerchantHistoryServiceImplBase {

    private final MerchantHistoryService merchantHistoryService;

    private final MerchantHistoryGrpcMapper merchantHistoryGrpcMapper;

    public MerchantHistoryControllerGrpc(MerchantHistoryService merchantHistoryService,
            MerchantHistoryGrpcMapper merchantHistoryGrpcMapper) {
        this.merchantHistoryService = merchantHistoryService;
        this.merchantHistoryGrpcMapper = merchantHistoryGrpcMapper;
    }

    @Override
    public void getHistory(MerchantHistoryRequestGrpc request,
            StreamObserver<MerchantHistoryResponseGrpc> responseObserver) {
        log.debug("Received getHistory gRPC request: {}", request);
        try {
            MerchantHistoryRequest historyRequest = merchantHistoryGrpcMapper.toRequest(request);
            Page<MerchantHistoryDTO> page = merchantHistoryService.findAll(historyRequest);
            MerchantHistoryResponseGrpc response = merchantHistoryGrpcMapper.toResponse(page);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error occurred while processing getHistory request", e);
            responseObserver.onError(e);
        }
    }

}
