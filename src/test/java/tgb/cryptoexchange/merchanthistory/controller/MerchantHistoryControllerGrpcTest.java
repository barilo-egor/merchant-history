package tgb.cryptoexchange.merchanthistory.controller;

import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import tgb.cryptoexchange.grpc.generated.MerchantHistoryRequestGrpc;
import tgb.cryptoexchange.grpc.generated.MerchantHistoryResponseDTO;
import tgb.cryptoexchange.grpc.generated.MerchantHistoryResponseGrpc;
import tgb.cryptoexchange.grpc.generated.PaginationGrpc;
import tgb.cryptoexchange.merchanthistory.dto.MerchantHistoryDTO;
import tgb.cryptoexchange.merchanthistory.dto.MerchantHistoryRequest;
import tgb.cryptoexchange.merchanthistory.mapper.MerchantHistoryGrpcMapper;
import tgb.cryptoexchange.merchanthistory.service.MerchantHistoryService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MerchantHistoryControllerGrpcTest {

    @Mock
    private MerchantHistoryService merchantHistoryService;

    @Mock
    private StreamObserver<MerchantHistoryResponseGrpc> responseObserver;

    @Mock
    private Page<MerchantHistoryDTO> page;

    private MerchantHistoryControllerGrpc controller;

    @BeforeEach
    void setUp() {
        MerchantHistoryGrpcMapper mapper = new MerchantHistoryGrpcMapper();
        controller = new MerchantHistoryControllerGrpc(merchantHistoryService, mapper);
    }

    @Test
    void getHistoryShouldReturnEmptyArrayIfHistoriesNotFound() {
        when(page.getContent()).thenReturn(Collections.emptyList());
        when(merchantHistoryService.findAll(any())).thenReturn(page);

        controller.getHistory(MerchantHistoryRequestGrpc.getDefaultInstance(), responseObserver);

        ArgumentCaptor<MerchantHistoryResponseGrpc> responseCaptor =
                ArgumentCaptor.forClass(MerchantHistoryResponseGrpc.class);
        verify(responseObserver).onNext(responseCaptor.capture());
        verify(responseObserver).onCompleted();
        verify(responseObserver, never()).onError(any());

        MerchantHistoryResponseGrpc actualResponse = responseCaptor.getValue();
        assertNotNull(actualResponse);
        assertEquals(0, actualResponse.getResponseCount());
    }

    @CsvSource("""
            5,10,order-abc,op-111,act-222,1762757723671
            1,25,order-xyz,op-333,act-444,1761955200000
            """)
    @ParameterizedTest
    void getHistoryShouldPassRequestParameters(Integer pageNumber, Integer pageSize, String orderId,
            String operationId, String actorId, Long createdAtEpoch) {
        when(page.getContent()).thenReturn(Collections.emptyList());
        when(merchantHistoryService.findAll(any(MerchantHistoryRequest.class))).thenReturn(page);

        MerchantHistoryRequestGrpc grpcRequest = MerchantHistoryRequestGrpc.newBuilder()
                .setPagination(PaginationGrpc.newBuilder()
                        .setPage(pageNumber)
                        .setSize(pageSize)
                        .build())
                .setOrderId(StringValue.of(orderId))
                .setOperationId(StringValue.of(operationId))
                .setActorId(StringValue.of(actorId))
                .setCreatedAtFrom(Int64Value.of(createdAtEpoch))
                .build();

        controller.getHistory(grpcRequest, responseObserver);

        ArgumentCaptor<MerchantHistoryRequest> requestCaptor =
                ArgumentCaptor.forClass(MerchantHistoryRequest.class);
        verify(merchantHistoryService).findAll(requestCaptor.capture());
        MerchantHistoryRequest actual = requestCaptor.getValue();

        assertAll(
                () -> assertEquals(orderId, actual.getOrderId()),
                () -> assertEquals(operationId, actual.getOperationId()),
                () -> assertEquals(actorId, actual.getActorId()),
                () -> assertEquals(Instant.ofEpochMilli(createdAtEpoch), actual.getCreatedAtFrom()),
                () -> assertEquals(pageNumber, actual.getPageNumber()),
                () -> assertEquals(pageSize, actual.getPageSize())
        );

        verify(responseObserver).onNext(any());
        verify(responseObserver).onCompleted();
    }

    @ValueSource(ints = { 2, 5 })
    @ParameterizedTest
    void getHistoryShouldReturnHistories(int size) {
        List<MerchantHistoryDTO> histories = new ArrayList<>();
        Instant now = Instant.ofEpochMilli(1730000000000L);
        for (long i = 0; i < size; i++) {
            MerchantHistoryDTO dto = new MerchantHistoryDTO();
            dto.setOperationId("op-" + (100L + i));
            dto.setActorId("act-" + (200L + i));
            dto.setInitiatorApp("app" + i);
            dto.setCreatedAt(now.minusSeconds(i));
            dto.setMerchant("MERCHANT_" + i);
            dto.setMerchantOrderId("mo-" + i);
            dto.setRequestedAmount(5000 + (int) i);
            dto.setMerchantAmount(5001 + (int) i);
            dto.setMethod("CARD_" + i);
            dto.setDetails(i + " Bank 1234 1234");
            histories.add(dto);
        }

        when(page.getContent()).thenReturn(histories);
        when(merchantHistoryService.findAll(any())).thenReturn(page);

        controller.getHistory(MerchantHistoryRequestGrpc.getDefaultInstance(), responseObserver);

        ArgumentCaptor<MerchantHistoryResponseGrpc> responseCaptor =
                ArgumentCaptor.forClass(MerchantHistoryResponseGrpc.class);
        verify(responseObserver).onNext(responseCaptor.capture());
        verify(responseObserver).onCompleted();

        MerchantHistoryResponseGrpc response = responseCaptor.getValue();
        assertEquals(size, response.getResponseCount());

        for (int i = 0; i < size; i++) {
            MerchantHistoryResponseDTO item = response.getResponse(i);
            String expectedOperationId = "op-" + (100L + i);
            String expectedActorId = "act-" + (200L + i);
            Instant expectedCreatedAt = now.minusSeconds(i);

            assertEquals(expectedOperationId, item.getOperationId().getValue());
            assertEquals(expectedActorId, item.getActorId().getValue());
            assertEquals("app" + i, item.getInitiatorApp().getValue());
            assertEquals(expectedCreatedAt.toEpochMilli(), item.getCreatedAt().getValue());
            assertEquals("MERCHANT_" + i, item.getMerchant().getValue());
            assertEquals("mo-" + i, item.getMerchantOrderId().getValue());
            assertEquals(5000 + i, item.getRequestedAmount().getValue());
            assertEquals(5001 + i, item.getMerchantAmount().getValue());
            assertEquals("CARD_" + i, item.getMethod().getValue());
            assertEquals(i + " Bank 1234 1234", item.getDetails().getValue());
        }
    }

    @Test
    void getHistoryShouldCallOnErrorWhenServiceThrows() {
        RuntimeException exception = new RuntimeException("DB connection error");
        when(merchantHistoryService.findAll(any())).thenThrow(exception);

        controller.getHistory(MerchantHistoryRequestGrpc.getDefaultInstance(), responseObserver);

        verify(responseObserver).onError(exception);
        verify(responseObserver, never()).onNext(any());
        verify(responseObserver, never()).onCompleted();
    }

}
