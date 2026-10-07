package tgb.cryptoexchange.merchanthistory.mapper;

import com.google.protobuf.Int32Value;
import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import tgb.cryptoexchange.grpc.generated.MerchantHistoryRequestGrpc;
import tgb.cryptoexchange.grpc.generated.MerchantHistoryResponseDTO;
import tgb.cryptoexchange.grpc.generated.MerchantHistoryResponseGrpc;
import tgb.cryptoexchange.grpc.generated.PaginationGrpc;
import tgb.cryptoexchange.merchanthistory.dto.MerchantHistoryDTO;
import tgb.cryptoexchange.merchanthistory.dto.MerchantHistoryRequest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class MerchantHistoryGrpcMapper {

    public Pageable createPageable(PaginationGrpc pagination) {
        if (Objects.isNull(pagination)) {
            return PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt"));
        }
        int page = Math.max(pagination.getPage(), 0);
        int size = pagination.getSize() > 0 ? pagination.getSize() : 20;
        return PageRequest.of(page, size, parseSort(pagination.getSort()));
    }

    private Sort parseSort(String sortString) {
        if (StringUtils.isBlank(sortString)) {
            return Sort.unsorted();
        }
        String[] parts = sortString.split(",");
        String property = parts[0].trim();
        Sort.Direction direction = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim()))
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        return Sort.by(direction, property);
    }

    public MerchantHistoryRequest toRequest(MerchantHistoryRequestGrpc requestGrpc) {
        MerchantHistoryRequest request = new MerchantHistoryRequest();
        if (Objects.isNull(requestGrpc)) {
            return request;
        }

        if (requestGrpc.hasPagination()) {
            mapPagination(request, requestGrpc.getPagination());
        }
        mapRequestIdentifiers(request, requestGrpc);
        mapRequestDateFilters(request, requestGrpc);
        mapRequestDetailsAndAmounts(request, requestGrpc);

        return request;
    }

    private void mapPagination(MerchantHistoryRequest request, PaginationGrpc pagination) {
        if (pagination.getPage() >= 0) {
            request.setPageNumber(pagination.getPage());
        }
        if (pagination.getSize() > 0) {
            request.setPageSize(pagination.getSize());
        }
        if (StringUtils.isNotBlank(pagination.getSort())) {
            request.setSort(pagination.getSort());
        }
    }

    private void mapRequestIdentifiers(MerchantHistoryRequest request, MerchantHistoryRequestGrpc requestGrpc) {
        if (requestGrpc.hasOrderId()) {
            request.setOrderId(requestGrpc.getOrderId().getValue());
        }
        if (requestGrpc.hasOperationId()) {
            request.setOperationId(requestGrpc.getOperationId().getValue());
        }
        if (requestGrpc.hasActorId()) {
            request.setActorId(requestGrpc.getActorId().getValue());
        }
    }

    private void mapRequestDateFilters(MerchantHistoryRequest request, MerchantHistoryRequestGrpc requestGrpc) {
        if (requestGrpc.hasCreatedAtFrom()) {
            request.setCreatedAtFrom(Instant.ofEpochMilli(requestGrpc.getCreatedAtFrom().getValue()));
        }
        if (requestGrpc.hasCreatedAtTo()) {
            request.setCreatedAtTo(Instant.ofEpochMilli(requestGrpc.getCreatedAtTo().getValue()));
        }
    }

    private void mapRequestDetailsAndAmounts(MerchantHistoryRequest request, MerchantHistoryRequestGrpc requestGrpc) {
        if (requestGrpc.hasInitiatorApp()) {
            request.setInitiatorApp(requestGrpc.getInitiatorApp().getValue());
        }
        if (requestGrpc.hasDetails()) {
            request.setDetails(requestGrpc.getDetails().getValue());
        }
        if (requestGrpc.getMerchantsCount() > 0) {
            request.setMerchants(new ArrayList<>(requestGrpc.getMerchantsList().stream()
                    .map(StringValue::getValue)
                    .toList()));
        }
        if (requestGrpc.hasMerchantAmount()) {
            request.setMerchantAmount(requestGrpc.getMerchantAmount().getValue());
        }
        if (requestGrpc.hasRequestedAmount()) {
            request.setRequestedAmount(requestGrpc.getRequestedAmount().getValue());
        }
    }

    public MerchantHistoryResponseGrpc toResponse(Page<MerchantHistoryDTO> page) {
        return toResponse(Objects.nonNull(page) ? page.getContent() : Collections.emptyList());
    }

    public MerchantHistoryResponseGrpc toResponse(List<MerchantHistoryDTO> dtoList) {
        MerchantHistoryResponseGrpc.Builder builder = MerchantHistoryResponseGrpc.newBuilder();
        if (Objects.nonNull(dtoList)) {
            for (MerchantHistoryDTO dto : dtoList) {
                builder.addResponse(toResponseDto(dto));
            }
        }
        return builder.build();
    }

    public MerchantHistoryResponseDTO toResponseDto(MerchantHistoryDTO dto) {
        if (Objects.isNull(dto)) {
            return MerchantHistoryResponseDTO.getDefaultInstance();
        }

        MerchantHistoryResponseDTO.Builder builder = MerchantHistoryResponseDTO.newBuilder();
        mapResponseIdentifiers(builder, dto);
        mapResponseDetails(builder, dto);
        mapResponseAmounts(builder, dto);

        return builder.build();
    }

    private void mapResponseIdentifiers(MerchantHistoryResponseDTO.Builder builder, MerchantHistoryDTO dto) {
        if (Objects.nonNull(dto.getOperationId())) {
            builder.setOperationId(StringValue.of(dto.getOperationId()));
        }
        if (Objects.nonNull(dto.getActorId())) {
            builder.setActorId(StringValue.of(dto.getActorId()));
        }
    }

    private void mapResponseDetails(MerchantHistoryResponseDTO.Builder builder, MerchantHistoryDTO dto) {
        if (Objects.nonNull(dto.getInitiatorApp())) {
            builder.setInitiatorApp(StringValue.of(dto.getInitiatorApp()));
        }
        if (Objects.nonNull(dto.getCreatedAt())) {
            builder.setCreatedAt(Int64Value.of(dto.getCreatedAt().toEpochMilli()));
        }
        if (Objects.nonNull(dto.getMerchant())) {
            builder.setMerchant(StringValue.of(dto.getMerchant()));
        }
        if (Objects.nonNull(dto.getMerchantOrderId())) {
            builder.setMerchantOrderId(StringValue.of(dto.getMerchantOrderId()));
        }
        if (Objects.nonNull(dto.getMethod())) {
            builder.setMethod(StringValue.of(dto.getMethod()));
        }
        if (Objects.nonNull(dto.getDetails())) {
            builder.setDetails(StringValue.of(dto.getDetails()));
        }
    }

    private void mapResponseAmounts(MerchantHistoryResponseDTO.Builder builder, MerchantHistoryDTO dto) {
        if (Objects.nonNull(dto.getRequestedAmount())) {
            builder.setRequestedAmount(Int32Value.of(dto.getRequestedAmount()));
        }
        if (Objects.nonNull(dto.getMerchantAmount())) {
            builder.setMerchantAmount(Int32Value.of(dto.getMerchantAmount()));
        }
    }

}
