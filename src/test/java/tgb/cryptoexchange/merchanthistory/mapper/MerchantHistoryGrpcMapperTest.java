package tgb.cryptoexchange.merchanthistory.mapper;

import com.google.protobuf.Int32Value;
import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import tgb.cryptoexchange.grpc.generated.MerchantHistoryRequestGrpc;
import tgb.cryptoexchange.grpc.generated.MerchantHistoryResponseDTO;
import tgb.cryptoexchange.grpc.generated.MerchantHistoryResponseGrpc;
import tgb.cryptoexchange.grpc.generated.PaginationGrpc;
import tgb.cryptoexchange.merchanthistory.dto.MerchantHistoryDTO;
import tgb.cryptoexchange.merchanthistory.dto.MerchantHistoryRequest;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MerchantHistoryGrpcMapperTest {

    private MerchantHistoryGrpcMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new MerchantHistoryGrpcMapper();
    }

    @Test
    void toRequestShouldMapAllFields() {
        long epochFrom = 1730419200000L;
        long epochTo = 1733011200000L;

        MerchantHistoryRequestGrpc grpcRequest = MerchantHistoryRequestGrpc.newBuilder()
                .setPagination(PaginationGrpc.newBuilder()
                        .setPage(2)
                        .setSize(50)
                        .setSort("createdAt,asc")
                        .build())
                .setOrderId(StringValue.of("order-123"))
                .setOperationId(StringValue.of("op-2001"))
                .setActorId(StringValue.of("act-3001"))
                .setCreatedAtFrom(Int64Value.of(epochFrom))
                .setCreatedAtTo(Int64Value.of(epochTo))
                .setInitiatorApp(StringValue.of("crypto-app"))
                .setDetails(StringValue.of("payment details"))
                .addMerchants(StringValue.of("MERCHANT_A"))
                .addMerchants(StringValue.of("MERCHANT_B"))
                .setMerchantAmount(Int32Value.of(5000))
                .setRequestedAmount(Int32Value.of(5500))
                .build();

        MerchantHistoryRequest actual = mapper.toRequest(grpcRequest);

        assertAll(
                () -> assertEquals(2, actual.getPageNumber()),
                () -> assertEquals(50, actual.getPageSize()),
                () -> assertEquals("createdAt,asc", actual.getSort()),
                () -> assertEquals("order-123", actual.getOrderId()),
                () -> assertEquals("op-2001", actual.getOperationId()),
                () -> assertEquals("act-3001", actual.getActorId()),
                () -> assertEquals(Instant.ofEpochMilli(epochFrom), actual.getCreatedAtFrom()),
                () -> assertEquals(Instant.ofEpochMilli(epochTo), actual.getCreatedAtTo()),
                () -> assertEquals("crypto-app", actual.getInitiatorApp()),
                () -> assertEquals("payment details", actual.getDetails()),
                () -> assertEquals(List.of("MERCHANT_A", "MERCHANT_B"), actual.getMerchants()),
                () -> assertEquals(5000, actual.getMerchantAmount()),
                () -> assertEquals(5500, actual.getRequestedAmount()),
                () -> assertNull(actual.getDealId()),
                () -> assertNull(actual.getUserId())
        );
    }

    @Test
    void toRequestShouldHandleDefaultsAndNulls() {
        MerchantHistoryRequest actual = mapper.toRequest(null);
        assertNotNull(actual);
        assertEquals(0, actual.getPageNumber());
        assertEquals(20, actual.getPageSize());
        assertEquals("createdAt,desc", actual.getSort());
        assertNull(actual.getOrderId());
        assertNull(actual.getDealId());
        assertNull(actual.getOperationId());
        assertNull(actual.getActorId());
        assertNull(actual.getUserId());
        assertNull(actual.getCreatedAtFrom());
        assertNull(actual.getCreatedAtTo());
        assertNull(actual.getInitiatorApp());
        assertNull(actual.getDetails());
        assertTrue(actual.getMerchants().isEmpty());
        assertNull(actual.getMerchantAmount());
        assertNull(actual.getRequestedAmount());
    }

    @Test
    void toRequestShouldHandleEmptyPagination() {
        MerchantHistoryRequestGrpc grpcRequest = MerchantHistoryRequestGrpc.newBuilder()
                .setPagination(PaginationGrpc.newBuilder().build())
                .build();

        MerchantHistoryRequest actual = mapper.toRequest(grpcRequest);

        assertEquals(0, actual.getPageNumber());
        assertEquals(20, actual.getPageSize());
        assertEquals("createdAt,desc", actual.getSort());
    }

    @Test
    void toResponseDtoShouldMapAllFields() {
        Instant now = Instant.ofEpochMilli(1730000000000L);
        MerchantHistoryDTO dto = new MerchantHistoryDTO();
        dto.setOperationId("45ae86af-7fae-4406-87f5-3834feccc394");
        dto.setActorId("8a4070a2-a4e7-4230-8d60-2fbe9f7455f8");
        dto.setInitiatorApp("app-test");
        dto.setCreatedAt(now);
        dto.setMerchant("MERCHANT_TEST");
        dto.setMerchantOrderId("mo-555");
        dto.setRequestedAmount(1000);
        dto.setMerchantAmount(1050);
        dto.setMethod("SBP");
        dto.setDetails("Tinkoff Bank 1234");

        MerchantHistoryResponseDTO actual = mapper.toResponseDto(dto);

        assertAll(
                () -> assertTrue(actual.hasOperationId()),
                () -> assertEquals("45ae86af-7fae-4406-87f5-3834feccc394", actual.getOperationId().getValue()),
                () -> assertTrue(actual.hasActorId()),
                () -> assertEquals("8a4070a2-a4e7-4230-8d60-2fbe9f7455f8", actual.getActorId().getValue()),
                () -> assertTrue(actual.hasInitiatorApp()),
                () -> assertEquals("app-test", actual.getInitiatorApp().getValue()),
                () -> assertTrue(actual.hasCreatedAt()),
                () -> assertEquals(now.toEpochMilli(), actual.getCreatedAt().getValue()),
                () -> assertTrue(actual.hasMerchant()),
                () -> assertEquals("MERCHANT_TEST", actual.getMerchant().getValue()),
                () -> assertTrue(actual.hasMerchantOrderId()),
                () -> assertEquals("mo-555", actual.getMerchantOrderId().getValue()),
                () -> assertTrue(actual.hasRequestedAmount()),
                () -> assertEquals(1000, actual.getRequestedAmount().getValue()),
                () -> assertTrue(actual.hasMerchantAmount()),
                () -> assertEquals(1050, actual.getMerchantAmount().getValue()),
                () -> assertTrue(actual.hasMethod()),
                () -> assertEquals("SBP", actual.getMethod().getValue()),
                () -> assertTrue(actual.hasDetails()),
                () -> assertEquals("Tinkoff Bank 1234", actual.getDetails().getValue())
        );
    }

    @Test
    void toResponseDtoShouldHandleNullDtoAndNullFields() {
        MerchantHistoryResponseDTO nullDto = mapper.toResponseDto(null);
        assertEquals(MerchantHistoryResponseDTO.getDefaultInstance(), nullDto);

        MerchantHistoryResponseDTO emptyDto = mapper.toResponseDto(new MerchantHistoryDTO());
        assertFalse(emptyDto.hasOperationId());
        assertFalse(emptyDto.hasActorId());
        assertFalse(emptyDto.hasInitiatorApp());
        assertFalse(emptyDto.hasCreatedAt());
        assertFalse(emptyDto.hasMerchant());
        assertFalse(emptyDto.hasMerchantOrderId());
        assertFalse(emptyDto.hasRequestedAmount());
        assertFalse(emptyDto.hasMerchantAmount());
        assertFalse(emptyDto.hasMethod());
        assertFalse(emptyDto.hasDetails());
    }

    @Test
    void toResponseShouldMapPageAndList() {
        MerchantHistoryDTO dto1 = new MerchantHistoryDTO();
        dto1.setOperationId("op-1");
        MerchantHistoryDTO dto2 = new MerchantHistoryDTO();
        dto2.setOperationId("op-2");

        Page<MerchantHistoryDTO> page = new PageImpl<>(List.of(dto1, dto2));
        MerchantHistoryResponseGrpc response = mapper.toResponse(page);

        assertEquals(2, response.getResponseCount());
        assertEquals("op-1", response.getResponse(0).getOperationId().getValue());
        assertEquals("op-2", response.getResponse(1).getOperationId().getValue());

        MerchantHistoryResponseGrpc emptyResponse = mapper.toResponse((Page<MerchantHistoryDTO>) null);
        assertEquals(0, emptyResponse.getResponseCount());
    }

    @Test
    void createPageableShouldHandleVariousPagination() {
        Pageable defaultPageable = mapper.createPageable(null);
        assertEquals(0, defaultPageable.getPageNumber());
        assertEquals(20, defaultPageable.getPageSize());
        assertEquals(Sort.by(Sort.Direction.DESC, "createdAt"), defaultPageable.getSort());

        PaginationGrpc paginationDesc = PaginationGrpc.newBuilder()
                .setPage(3)
                .setSize(15)
                .setSort("createdAt,desc")
                .build();
        Pageable pageableDesc = mapper.createPageable(paginationDesc);
        assertEquals(3, pageableDesc.getPageNumber());
        assertEquals(15, pageableDesc.getPageSize());
        assertEquals(Sort.by(Sort.Direction.DESC, "createdAt"), pageableDesc.getSort());

        PaginationGrpc paginationAsc = PaginationGrpc.newBuilder()
                .setPage(-1)
                .setSize(0)
                .setSort("merchant,asc")
                .build();
        Pageable pageableAsc = mapper.createPageable(paginationAsc);
        assertEquals(0, pageableAsc.getPageNumber());
        assertEquals(20, pageableAsc.getPageSize());
        assertEquals(Sort.by(Sort.Direction.ASC, "merchant"), pageableAsc.getSort());

        PaginationGrpc paginationUnsorted = PaginationGrpc.newBuilder()
                .setPage(1)
                .setSize(10)
                .setSort("")
                .build();
        Pageable pageableUnsorted = mapper.createPageable(paginationUnsorted);
        assertEquals(Sort.unsorted(), pageableUnsorted.getSort());
    }

}
