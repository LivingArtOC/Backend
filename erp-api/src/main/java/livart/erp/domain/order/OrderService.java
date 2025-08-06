package livart.erp.domain.order;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import livart.common.Auth.CustomUserDetails;
import livart.common.domain.order.entity.*;
import livart.common.domain.order.repository.*;
import livart.common.domain.product.entity.QProduct;
import livart.common.domain.setting.entity.CompanyInfo;
import livart.common.domain.setting.repository.CompanyInfoRepository;
import livart.common.domain.user.entity.Business;
import livart.common.domain.user.entity.User;
import livart.common.domain.user.repository.AdminRepository;
import livart.common.domain.user.repository.BusinessRepository;
import livart.common.domain.user.repository.ConsumerRepository;
import livart.common.domain.user.repository.UserRepository;
import livart.common.dto.enums.conv.TaxStatus;
import livart.common.dto.enums.order.*;
import livart.common.dto.enums.user.Role;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.mapper.SearchResult;
import livart.common.service.GlobalService;
import livart.common.dto.enums.order.OrderItemStatus;
import livart.erp.domain.order.dto.request.*;
import livart.erp.domain.order.dto.response.*;
import livart.erp.domain.support.quotation.dto.response.QuotationProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final GlobalService globalService;
    private final OrderItemRepository orderItemRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final OrderClaimRepository orderClaimRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final AfterServiceRequestRepository afterServiceRequestRepository;
    private final CompanyInfoRepository companyInfoRepository;
    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final ConsumerRepository consumerRepository;
    private final OrderRepository orderRepository;
    private final StatementRepository statementRepository;

    public SearchResult<OrderAllResponse> getAllOrders(CustomUserDetails customUserDetails, OrderSearchRequest request, Pageable pageable){
        globalService.validateAdmin(customUserDetails);

        QOrder order = QOrder.order;
        QPayment payment = QPayment.payment;
        QOrderItem orderItem = QOrderItem.orderItem;
        QOrderItemOption orderItemOption = QOrderItemOption.orderItemOption;
        QOrderStatusHistory orderStatusHistory = QOrderStatusHistory.orderStatusHistory;
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(request.getKeyword()) && request.getKey() != null) {
            switch (request.getKey()) {
                case ORDER_NUM -> builder.and(order.orderNum.containsIgnoreCase(request.getKeyword()));
                case ORDER_NAME -> builder.and(order.orderName.containsIgnoreCase(request.getKeyword()));
                case ORDER_PHONE_NUM -> builder.and(order.orderPhoneNum.containsIgnoreCase(request.getKeyword()));
                case ALL -> {
                    BooleanBuilder keywordBuilder = new BooleanBuilder();
                    keywordBuilder.or(order.orderNum.containsIgnoreCase(request.getKeyword()));
                    keywordBuilder.or(order.orderName.containsIgnoreCase(request.getKeyword()));
                    keywordBuilder.or(order.orderPhoneNum.containsIgnoreCase(request.getKeyword()));
                    keywordBuilder.or(order.orderEmail.containsIgnoreCase(request.getKeyword()));
                    builder.and(keywordBuilder);
                }
            }
        }

        if (request.getOrderDate() != null) {
            if (request.getOrderDate().getStartDate() != null) {
                builder.and(order.orderDate.goe(request.getOrderDate().getStartDate().atStartOfDay()));
            }
            if (request.getOrderDate().getEndDate() != null) {
                builder.and(order.orderDate.loe(request.getOrderDate().getEndDate().atTime(23, 59, 59)));
            }
        }

        List<Long> pagedOrderItemIds = jpaQueryFactory
                .select(orderItem.id)
                .distinct()
                .from(orderItem)
                .join(orderItem.order, order)
                .where(builder)
                .orderBy(order.orderDate.desc(), orderItem.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (pagedOrderItemIds.isEmpty()) {
            return SearchResult.<OrderAllResponse>builder()
                    .totalCount(0)
                    .page(pageable.getPageNumber())
                    .size(pageable.getPageSize())
                    .data(List.of())
                    .build();
        }

        List<Tuple> rows = jpaQueryFactory
                .select(
                        order.id,
                        orderItem.id,
                        order.userId,
                        order.orderNum,
                        order.orderName,
                        order.orderDate,
                        orderItem.productName,
                        orderItem.itemName,
                        orderItem.finalPrice,
                        payment.paymentMethod,
                        payment.paymentStatus,
                        orderStatusHistory.status
                )
                .from(orderItem)
                .join(orderItem.order, order)
                .leftJoin(payment).on(payment.order.eq(order))
                .leftJoin(orderStatusHistory).on(orderStatusHistory.orderItem.eq(orderItem))
                .where(orderItem.id.in(pagedOrderItemIds))
                .fetch();

        List<Tuple> optionRows = jpaQueryFactory
                .select(
                        orderItemOption.orderItem.id,
                        orderItemOption.optionName,
                        orderItemOption.valueName
                )
                .from(orderItemOption)
                .where(orderItemOption.orderItem.id.in(pagedOrderItemIds))
                .fetch();

        Map<Long, Set<OrderOptionResponse>> optionMap = new HashMap<>();

        for (Tuple row : optionRows) {
            Long orderItemId = row.get(orderItemOption.orderItem.id);
            String optionName = row.get(orderItemOption.optionName);
            String optionValue = row.get(orderItemOption.valueName);

            OrderOptionResponse option = OrderOptionResponse.builder()
                    .optionName(optionName)
                    .optionValue(optionValue)
                    .build();

            optionMap.computeIfAbsent(orderItemId, id -> new HashSet<>()).add(option);
        }

        Map<Long, OrderAllResponse.OrderAllResponseBuilder> groupedMap = new LinkedHashMap<>();

        Map<Long, Boolean> returnMap = new HashMap<>();
        Map<Long, Boolean> exchangeMap = new HashMap<>();
        Map<Long, Boolean> refundMap = new HashMap<>();

        for (Tuple row : rows) {
            Long orderItemId = row.get(orderItem.id);

            // Builder 초기화
            groupedMap.computeIfAbsent(orderItemId, id ->
                    OrderAllResponse.builder()
                            .orderId(row.get(order.id))
                            .orderItemId(orderItemId)
                            .userId(row.get(order.userId))
                            .orderNum(row.get(order.orderNum))
                            .orderName(row.get(order.orderName))
                            .orderDate(row.get(order.orderDate).toLocalDate())
                            .productName(row.get(orderItem.productName) + "/" + row.get(orderItem.itemName))
                            .finalPrice(row.get(orderItem.finalPrice))
                            .paymentMethod(row.get(payment.paymentMethod))
                            .paymentStatus(row.get(payment.paymentStatus))
            );


            OrderStatus status = row.get(orderStatusHistory.status);
            if (status != null) {
                switch (status) {
                    case RETURNED -> returnMap.put(orderItemId, true);
                    case EXCHANGED -> exchangeMap.put(orderItemId, true);
                    case REFUNDED -> refundMap.put(orderItemId, true);
                }
            }
        }

        List<OrderAllResponse> responseList = groupedMap.entrySet().stream()
                .map(entry -> {
                    Long orderItemId = entry.getKey();
                    OrderAllResponse.OrderAllResponseBuilder orderAllResponseBuilder = entry.getValue();

                    Set<OrderOptionResponse> optionList = optionMap.getOrDefault(orderItemId, Set.of());
                    orderAllResponseBuilder.orderOption(optionList);

                    orderAllResponseBuilder.isReturned(returnMap.getOrDefault(orderItemId, false));
                    orderAllResponseBuilder.isExchanged(exchangeMap.getOrDefault(orderItemId, false));
                    orderAllResponseBuilder.isRefunded(refundMap.getOrDefault(orderItemId, false));

                    return orderAllResponseBuilder.build();
                })
                .sorted(Comparator.comparing(OrderAllResponse::getOrderItemId).reversed())
                .collect(Collectors.toList());

        Long totalCount = jpaQueryFactory
                .select(orderItem.count())
                .from(orderItem)
                .join(orderItem.order, order)
                .where(builder)
                .fetchOne();

        return SearchResult.<OrderAllResponse> builder()
                .totalCount(totalCount != null ? totalCount : 0)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .data(responseList)
                .build();

    }


    public SearchResult<OrderInfoResponse> getOrderInfo(CustomUserDetails customUserDetails, OrderSearchRequest request, Pageable pageable){
        globalService.validateAdmin(customUserDetails);

        QOrder order = QOrder.order;
        QOrderItem orderItem = QOrderItem.orderItem;
        QOrderItemOption orderItemOption = QOrderItemOption.orderItemOption;
        QProduct product = QProduct.product;
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(request.getKeyword()) && request.getKey() != null) {
            switch (request.getKey()) {
                case ORDER_NUM -> builder.and(order.orderNum.containsIgnoreCase(request.getKeyword()));
                case ORDER_NAME -> builder.and(order.orderName.containsIgnoreCase(request.getKeyword()));
                case ORDER_PHONE_NUM -> builder.and(order.orderPhoneNum.containsIgnoreCase(request.getKeyword()));
                case ALL -> {
                    BooleanBuilder keywordBuilder = new BooleanBuilder();
                    keywordBuilder.or(order.orderNum.containsIgnoreCase(request.getKeyword()));
                    keywordBuilder.or(order.orderName.containsIgnoreCase(request.getKeyword()));
                    keywordBuilder.or(order.orderPhoneNum.containsIgnoreCase(request.getKeyword()));
                    keywordBuilder.or(order.orderEmail.containsIgnoreCase(request.getKeyword()));
                    builder.and(keywordBuilder);
                }
            }
        }

        if (request.getOrderDate() != null) {
            if (request.getOrderDate().getStartDate() != null) {
                builder.and(order.orderDate.goe(request.getOrderDate().getStartDate().atStartOfDay()));
            }
            if (request.getOrderDate().getEndDate() != null) {
                builder.and(order.orderDate.loe(request.getOrderDate().getEndDate().atTime(23, 59, 59)));
            }
        }

        List<Long> pagedOrderItemIds = jpaQueryFactory
                .select(orderItem.id)
                .distinct()
                .from(orderItem)
                .join(orderItem.order, order)
                .where(builder)
                .orderBy(orderItem.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (pagedOrderItemIds.isEmpty()) {
            return SearchResult.<OrderInfoResponse>builder()
                    .totalCount(0L)
                    .page(pageable.getPageNumber())
                    .size(pageable.getPageSize())
                    .data(List.of())
                    .build();
        }

        List<Tuple> rows = jpaQueryFactory
                .select(
                        order.id,
                        orderItem.id,
                        order.orderNum,
                        order.orderName,
                        order.orderDate,
                        orderItem.productName,
                        orderItem.itemName,
                        orderItem.imageUrl,
                        product.brand,
                        product.id
                )
                .from(orderItem)
                .join(orderItem.order, order)
                .join(product).on(orderItem.productId.eq(product.id))
                .where(orderItem.id.in(pagedOrderItemIds))
                .fetch();

        Map<Long, OrderInfoResponse.OrderInfoResponseBuilder> groupedMap = new LinkedHashMap<>();
        Map<Long, Set<OrderOpInfoResponse>> optionMap = new HashMap<>();

        List<Tuple> optionRows = jpaQueryFactory
                .select(
                        orderItemOption.orderItem.id,
                        orderItemOption.optionName,
                        orderItemOption.valueName
                )
                .from(orderItemOption)
                .where(orderItemOption.orderItem.id.in(pagedOrderItemIds))
                .fetch();

        for (Tuple row : optionRows) {
            Long orderItemId = row.get(orderItemOption.orderItem.id);
            String optionName = row.get(orderItemOption.optionName);
            String optionValue = row.get(orderItemOption.valueName);

            OrderOpInfoResponse option = OrderOpInfoResponse.builder()
                    .optionName(optionName)
                    .optionValue(optionValue)
                    .build();

            optionMap.computeIfAbsent(orderItemId, id -> new HashSet<>()).add(option);
        }


        for (Tuple row : rows) {
            Long orderItemId = row.get(orderItem.id);

            groupedMap.computeIfAbsent(orderItemId, id ->
                    OrderInfoResponse.builder()
                            .orderId(row.get(order.id))
                            .orderItemId(orderItemId)
                            .orderNum(row.get(order.orderNum))
                            .orderName(row.get(order.orderName))
                            .orderDate(row.get(order.orderDate).toLocalDate())
                            .productName(row.get(orderItem.productName) + "/" + row.get(orderItem.itemName))
                            .thumbNailImgUrl(row.get(orderItem.imageUrl))
                            .productId(row.get(product.id))
                            .brand(row.get(product.brand))
            );
        }

        List<OrderInfoResponse> responseList = groupedMap.entrySet().stream()
                .map(entry -> {
                    Long orderItemId = entry.getKey();
                    OrderInfoResponse.OrderInfoResponseBuilder orderBuilder = entry.getValue();

                    Set<OrderOpInfoResponse> optionList = optionMap.getOrDefault(orderItemId, Set.of());
                    orderBuilder.orderOption(optionList);

                    return orderBuilder.build();
                })
                .sorted(Comparator.comparing(OrderInfoResponse::getOrderItemId).reversed())
                .collect(Collectors.toList());

        Long totalCount = jpaQueryFactory
                .select(orderItem.count())
                .from(orderItem)
                .join(orderItem.order, order)
                .where(builder)
                .fetchOne();

        return SearchResult.<OrderInfoResponse> builder()
                .totalCount(totalCount != null ? totalCount : 0)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .data(responseList)
                .build();

    }

    public SearchResult<OrderIndResponse> getIndOrderList(CustomUserDetails customUserDetails, OrderSearchRequest request, String itemStatus, Pageable pageable){
        globalService.validateAdmin(customUserDetails);

        OrderItemStatus status = parseOrderItemStatus(itemStatus);

        QOrder order = QOrder.order;
        QPayment payment = QPayment.payment;
        QOrderItem orderItem = QOrderItem.orderItem;
        QOrderItemOption orderItemOption = QOrderItemOption.orderItemOption;
        QAfterServiceRequest afterServiceRequest = QAfterServiceRequest.afterServiceRequest;
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(request.getKeyword()) && request.getKey() != null) {
            switch (request.getKey()) {
                case ORDER_NUM -> builder.and(order.orderNum.containsIgnoreCase(request.getKeyword()));
                case ORDER_NAME -> builder.and(order.orderName.containsIgnoreCase(request.getKeyword()));
                case ORDER_PHONE_NUM -> builder.and(order.orderPhoneNum.containsIgnoreCase(request.getKeyword()));
                case ALL -> {
                    BooleanBuilder keywordBuilder = new BooleanBuilder();
                    keywordBuilder.or(order.orderNum.containsIgnoreCase(request.getKeyword()));
                    keywordBuilder.or(order.orderName.containsIgnoreCase(request.getKeyword()));
                    keywordBuilder.or(order.orderPhoneNum.containsIgnoreCase(request.getKeyword()));
                    keywordBuilder.or(order.orderEmail.containsIgnoreCase(request.getKeyword()));
                    builder.and(keywordBuilder);
                }
            }
        }

        if(status != null){
            switch (status){
                case CONFIRMED -> builder.and(orderItem.orderStatus.eq(OrderStatus.CONFIRMED));
                case WAITING_SHIPMENT -> builder.and(orderItem.orderStatus.eq(OrderStatus.WAITING_SHIPMENT));
                case DELIVERED -> builder.and(orderItem.orderStatus.eq(OrderStatus.DELIVERED));
                case PENDING -> builder.and(orderItem.orderStatus.eq(OrderStatus.PENDING));
                case FAILED -> builder.and(orderItem.orderStatus.eq(OrderStatus.FAILED));
                case PAID -> builder.and(orderItem.orderStatus.eq(OrderStatus.PAID));
                default -> throw new CustomException(ErrorCode.INVALID_TYPE);
            }
        }

        if (request.getOrderDate() != null) {
            if (request.getOrderDate().getStartDate() != null) {
                builder.and(order.orderDate.goe(request.getOrderDate().getStartDate().atStartOfDay()));
            }
            if (request.getOrderDate().getEndDate() != null) {
                builder.and(order.orderDate.loe(request.getOrderDate().getEndDate().atTime(23, 59, 59)));
            }
        }

        List<Long> pagedOrderItemIds = jpaQueryFactory
                .select(orderItem.id)
                .distinct()
                .from(orderItem)
                .join(orderItem.order, order)
                .leftJoin(afterServiceRequest).on(afterServiceRequest.orderItem.eq(orderItem))
                .where(builder)
                .orderBy(orderItem.id.desc(), orderItem.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (pagedOrderItemIds.isEmpty()) {
            return SearchResult.<OrderIndResponse>builder()
                    .totalCount(0)
                    .page(pageable.getPageNumber())
                    .size(pageable.getPageSize())
                    .data(List.of())
                    .build();
        }

        List<Tuple> rows = jpaQueryFactory
                .selectDistinct(
                        order.id,
                        orderItem.id,
                        order.userId,
                        order.orderNum,
                        order.orderName,
                        order.orderDate,
                        orderItem.productName,
                        orderItem.itemName,
                        orderItem.finalPrice,
                        payment.paymentMethod,
                        payment.paymentStatus,
                        payment.account,
                        payment.depositor,
                        afterServiceRequest.requestReason
                )
                .from(orderItem)
                .join(orderItem.order, order)
                .leftJoin(payment).on(payment.order.eq(order))
                .leftJoin(afterServiceRequest).on(afterServiceRequest.orderItem.eq(orderItem))
                .where(orderItem.id.in(pagedOrderItemIds))
                .fetch();

        Map<Long, OrderIndResponse.OrderIndResponseBuilder> groupedMap = new LinkedHashMap<>();
        Map<Long, Set<OrderOptionResponse>> optionMap = new HashMap<>();

        List<Tuple> optionRows = jpaQueryFactory
                .select(
                        orderItemOption.orderItem.id,
                        orderItemOption.optionName,
                        orderItemOption.valueName
                )
                .from(orderItemOption)
                .where(orderItemOption.orderItem.id.in(pagedOrderItemIds))
                .fetch();

        for (Tuple row : optionRows) {
            Long orderItemId = row.get(orderItemOption.orderItem.id);
            String optionName = row.get(orderItemOption.optionName);
            String optionValue = row.get(orderItemOption.valueName);

            OrderOptionResponse option = OrderOptionResponse.builder()
                    .optionName(optionName)
                    .optionValue(optionValue)
                    .build();

            optionMap.computeIfAbsent(orderItemId, id -> new HashSet<>()).add(option);
        }

        for (Tuple row : rows) {
            Long orderItemId = row.get(orderItem.id);
            LocalDateTime orderDate = row.get(order.orderDate);
            LocalDateTime now = LocalDateTime.now();

            Duration duration = Duration.between(orderDate, now);

            long secondsDiff = duration.getSeconds();

            int lap = (int) (secondsDiff / 86400);

            // Builder 초기화
            groupedMap.computeIfAbsent(orderItemId, id ->
                    OrderIndResponse.builder()
                            .orderId(row.get(order.id))
                            .orderItemId(orderItemId)
                            .userId(row.get(order.userId))
                            .orderNum(row.get(order.orderNum))
                            .orderName(row.get(order.orderName))
                            .orderDate(row.get(order.orderDate).toLocalDate())
                            .productName(row.get(orderItem.productName) + "/" + row.get(orderItem.itemName))
                            .finalPrice(row.get(orderItem.finalPrice))
                            .paymentMethod(row.get(payment.paymentMethod))
                            .paymentStatus(row.get(payment.paymentStatus))
                            .account(row.get(payment.account))
                            .depositor(row.get(payment.depositor))
                            .requestReason(row.get(afterServiceRequest.requestReason))
                            .lapsedDate(lap)
            );
        }

        List<OrderIndResponse> responseList = groupedMap.entrySet().stream()
                .map(entry -> {
                    Long orderItemId = entry.getKey();
                    OrderIndResponse.OrderIndResponseBuilder orderIndResponseBuilder = entry.getValue();

                    Set<OrderOptionResponse> optionList = optionMap.getOrDefault(orderItemId, Set.of());
                    orderIndResponseBuilder.orderOption(optionList);

                    return orderIndResponseBuilder.build();
                })
                .sorted(Comparator.comparing(OrderIndResponse::getOrderItemId).reversed())
                .collect(Collectors.toList());

        Long totalCount = jpaQueryFactory
                .select(orderItem.count())
                .from(orderItem)
                .join(orderItem.order, order)
                .leftJoin(afterServiceRequest).on(afterServiceRequest.orderItem.eq(orderItem))
                .where(builder)
                .fetchOne();

        return SearchResult.<OrderIndResponse> builder()
                .totalCount(totalCount != null ? totalCount : 0)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .data(responseList)
                .build();

    }

    public SearchResult<ClaimSearchResponse> getClaimList(CustomUserDetails customUserDetails, ClaimSearchRequest request, Pageable pageable){
        globalService.validateAdmin(customUserDetails);

        QOrder order = QOrder.order;
        QOrderItem orderItem = QOrderItem.orderItem;
        QOrderClaim orderClaim = QOrderClaim.orderClaim;
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(request.getKeyword()) && request.getKey() != null) {
            switch (request.getKey()) {
                case ORDER_NUM -> builder.and(order.orderNum.containsIgnoreCase(request.getKeyword()));
                case ORDER_NAME -> builder.and(order.orderName.containsIgnoreCase(request.getKeyword()));
                case ORDER_PHONE_NUM -> builder.and(order.orderPhoneNum.containsIgnoreCase(request.getKeyword()));
                case ALL -> {
                    BooleanBuilder keywordBuilder = new BooleanBuilder();
                    keywordBuilder.or(order.orderNum.containsIgnoreCase(request.getKeyword()));
                    keywordBuilder.or(order.orderName.containsIgnoreCase(request.getKeyword()));
                    keywordBuilder.or(order.orderPhoneNum.containsIgnoreCase(request.getKeyword()));
                    builder.and(keywordBuilder);
                }
            }
        }

        if(ClaimReqStatus.contains(request.getStatus())){
            builder.and(orderClaim.claimReqStatus.eq(request.getStatus()));
        }

        if(request.getRequestType() != null && request.getRequestType() != RequestType.ALL){
            builder.and(orderClaim.requestType.eq(request.getRequestType()));
        }

        if (request.getRequestDate() != null) {
            if (request.getRequestDate().getStartDate() != null) {
                builder.and(orderClaim.createdAt.goe(request.getRequestDate().getStartDate().atStartOfDay()));
            }
            if (request.getRequestDate().getEndDate() != null) {
                builder.and(orderClaim.createdAt.loe(request.getRequestDate().getEndDate().atTime(23, 59, 59)));
            }
        }

        List<Long> pagedClaimIds = jpaQueryFactory
                .select(orderClaim.id)
                .from(orderClaim)
                .join(orderClaim.orderItem, orderItem)
                .join(orderItem.order, order)
                .where(builder)
                .orderBy(orderClaim.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (pagedClaimIds.isEmpty()) {
            return SearchResult.<ClaimSearchResponse>builder()
                    .totalCount(0)
                    .page(pageable.getPageNumber())
                    .size(pageable.getPageSize())
                    .data(List.of())
                    .build();
        }

        List<Tuple> rows = jpaQueryFactory
                .select(
                        orderClaim.id,
                        orderClaim.requestType,
                        orderClaim.createdAt,
                        orderClaim.completeDate,
                        order.orderNum,
                        order.orderDate,
                        order.orderName,
                        order.orderPhoneNum,
                        orderItem.productName,
                        orderItem.itemName,
                        orderItem.finalPrice,
                        orderClaim.reason,
                        orderClaim.claimReqStatus
                )
                .from(orderClaim)
                .join(orderClaim.orderItem, orderItem)
                .join(orderItem.order, order)
                .where(orderClaim.id.in(pagedClaimIds))
                .orderBy(orderClaim.id.desc())
                .fetch();

        List<ClaimSearchResponse> responses = rows.stream()
                .map(r -> {
                    String itemName = Objects.toString(r.get(orderItem.productName), "")
                            + Objects.toString(r.get(orderItem.itemName), "");

                    return ClaimSearchResponse.builder()
                                    .claimId(r.get(orderClaim.id))
                                    .requestType(r.get(orderClaim.requestType))
                                    .requestDate(r.get(orderClaim.createdAt).toLocalDate())
                                    .completeDate(
                                            Optional.ofNullable(r.get(orderClaim.completeDate))
                                                    .map(LocalDateTime::toLocalDate)
                                                    .orElse(null)
                                    )
                                    .orderNum(r.get(order.orderNum))
                                    .orderDate(r.get(order.orderDate).toLocalDate())
                                    .orderName(r.get(order.orderName))
                                    .orderPhoneNum(r.get(order.orderPhoneNum))
                                    .orderItemName(itemName)
                                    .itemPrice(r.get(orderItem.finalPrice))
                                    .reason(Optional.ofNullable(r.get(orderClaim.reason)).orElse(""))
                                    .status(r.get(orderClaim.claimReqStatus))
                                    .build();
                        }
                )
                .collect(Collectors.toList());

        Long totalCount = jpaQueryFactory
                .select(orderClaim.count())
                .from(orderClaim)
                .join(orderClaim.orderItem, orderItem)
                .join(orderItem.order, order)
                .where(builder)
                .fetchOne();

        return SearchResult.<ClaimSearchResponse> builder()
                .totalCount(totalCount != null ? totalCount : 0)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .data(responses)
                .build();
    }

    @Transactional
    public ClaimChangeStatusResponse changeStatus(CustomUserDetails customUserDetails, ClaimChangeStatusRequest request){
        globalService.validateAdmin(customUserDetails);

        OrderClaim claim = orderClaimRepository.findById(request.getClaimId())
                .orElseThrow(() -> new CustomException(ErrorCode.CLAIM_NOT_FOUND));

        OrderItem item = claim.getOrderItem();

        claim.changeStatus(request.getStatus(), customUserDetails.getId());
        LocalDateTime changedAt = LocalDateTime.now();

        if(request.getStatus() == ClaimReqStatus.COMPLETED){
            if (claim.getRequestType() == null) {
                throw new CustomException(ErrorCode.INVALID_REQUEST_TYPE);
            }

            OrderStatus status = switch (claim.getRequestType()){
                case EXCHANGE -> OrderStatus.EXCHANGED;
                case REFUND -> OrderStatus.REFUNDED;
                case RETURN -> OrderStatus.RETURNED;
                case CANCEL -> OrderStatus.CANCELED;
                default -> throw new CustomException(ErrorCode.INVALID_REQUEST_TYPE);
            };

            OrderStatusHistory history = OrderStatusHistory.builder()
                    .status(status)
                    .memo("취소/교환/반품/환불 페이지에서 관리자 직접 변경")
                    .changedAt(changedAt)
                    .changedBy(customUserDetails.getId())
                    .orderItem(item)
                    .build();

            item.updateOrderStatus(status, customUserDetails.getId());
            item.getOrderStatusHistories().add(history);

            orderStatusHistoryRepository.save(history);
        }

        return ClaimChangeStatusResponse.builder()
                .claimId(claim.getId())
                .status(claim.getClaimReqStatus())
                .changedAt(changedAt)
                .build();
    }


    public SearchResult<AsSearchResponse> getAsList(CustomUserDetails customUserDetails, AsSearchRequest request, Pageable pageable){
        globalService.validateAdmin(customUserDetails);

        QOrder order = QOrder.order;
        QOrderItem orderItem = QOrderItem.orderItem;
        QAfterServiceRequest afterServiceRequest = QAfterServiceRequest.afterServiceRequest;
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(request.getKeyword()) && request.getKey() != null) {
            switch (request.getKey()) {
                case ORDER_NUM -> builder.and(order.orderNum.containsIgnoreCase(request.getKeyword()));
                case ORDER_NAME -> builder.and(order.orderName.containsIgnoreCase(request.getKeyword()));
                case ORDER_PHONE_NUM -> builder.and(order.orderPhoneNum.containsIgnoreCase(request.getKeyword()));
                case ALL -> {
                    BooleanBuilder keywordBuilder = new BooleanBuilder();
                    keywordBuilder.or(order.orderNum.containsIgnoreCase(request.getKeyword()));
                    keywordBuilder.or(order.orderName.containsIgnoreCase(request.getKeyword()));
                    keywordBuilder.or(order.orderPhoneNum.containsIgnoreCase(request.getKeyword()));
                    builder.and(keywordBuilder);
                }
            }
        }

        if(request.getStatus() != null && request.getStatus() != RequestStatus.ALL){
            builder.and(afterServiceRequest.status.eq(request.getStatus()));
        }

        if (request.getRequestDate() != null) {
            if (request.getRequestDate().getStartDate() != null) {
                builder.and(afterServiceRequest.createdAt.goe(request.getRequestDate().getStartDate().atStartOfDay()));
            }
            if (request.getRequestDate().getEndDate() != null) {
                builder.and(afterServiceRequest.createdAt.loe(request.getRequestDate().getEndDate().atTime(23, 59, 59)));
            }
        }

        List<Long> pagedClaimIds = jpaQueryFactory
                .select(afterServiceRequest.id)
                .from(afterServiceRequest)
                .join(afterServiceRequest.orderItem, orderItem)
                .join(orderItem.order, order)
                .where(builder)
                .orderBy(afterServiceRequest.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (pagedClaimIds.isEmpty()) {
            return SearchResult.<AsSearchResponse>builder()
                    .totalCount(0)
                    .page(pageable.getPageNumber())
                    .size(pageable.getPageSize())
                    .data(List.of())
                    .build();
        }

        List<Tuple> rows = jpaQueryFactory
                .select(
                        afterServiceRequest.id,
                        afterServiceRequest.createdAt,
                        afterServiceRequest.completeDate,
                        order.orderNum,
                        order.orderDate,
                        order.orderName,
                        orderItem.productName,
                        orderItem.itemName,
                        afterServiceRequest.requestReason,
                        afterServiceRequest.fileName,
                        afterServiceRequest.fileUrl,
                        afterServiceRequest.status
                )
                .from(afterServiceRequest)
                .join(afterServiceRequest.orderItem, orderItem)
                .join(orderItem.order, order)
                .where(afterServiceRequest.id.in(pagedClaimIds))
                .orderBy(afterServiceRequest.id.desc())
                .fetch();

        List<AsSearchResponse> responses = rows.stream()
                .map(r -> {
                            String itemName = Objects.toString(r.get(orderItem.productName), "")
                                    + Objects.toString(r.get(orderItem.itemName), "");

                            return AsSearchResponse.builder()
                                    .AsId(r.get(afterServiceRequest.id))
                                    .requestDate(r.get(afterServiceRequest.createdAt).toLocalDate())
                                    .completeDate(
                                            Optional.ofNullable(r.get(afterServiceRequest.completeDate))
                                                    .map(LocalDateTime::toLocalDate)
                                                    .orElse(null)
                                    )
                                    .orderNum(r.get(order.orderNum))
                                    .orderDate(r.get(order.orderDate).toLocalDate())
                                    .orderName(r.get(order.orderName))
                                    .orderItemName(itemName)
                                    .reason(Optional.ofNullable(r.get(afterServiceRequest.requestReason)).orElse(""))
                                    .fileName(Optional.ofNullable(r.get(afterServiceRequest.fileName)).orElse(""))
                                    .fileUrl(Optional.ofNullable(r.get(afterServiceRequest.fileUrl)).orElse(""))
                                    .status(r.get(afterServiceRequest.status))
                                    .build();
                        }
                )
                .collect(Collectors.toList());

        Long totalCount = jpaQueryFactory
                .select(afterServiceRequest.count())
                .from(afterServiceRequest)
                .join(afterServiceRequest.orderItem, orderItem)
                .join(orderItem.order, order)
                .where(builder)
                .fetchOne();

        return SearchResult.<AsSearchResponse> builder()
                .totalCount(totalCount != null ? totalCount : 0)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .data(responses)
                .build();
    }

    @Transactional
    public AsChangeStatusResponse changeAsStatus(CustomUserDetails customUserDetails, AsChangeStatusRequest request){
        globalService.validateAdmin(customUserDetails);

        AfterServiceRequest as = afterServiceRequestRepository.findById(request.getAsId())
                .orElseThrow(() -> new CustomException(ErrorCode.AS_NOT_FOUND));

        OrderItem item = as.getOrderItem();

        as.changeStatus(request.getStatus(), customUserDetails.getId());
        LocalDateTime changedAt = LocalDateTime.now();

        if(RequestStatus.contains(request.getStatus())){
            if (as.getStatus() == null) {
                throw new CustomException(ErrorCode.INVALID_REQUEST_TYPE);
            }

            OrderStatus status = switch (request.getStatus()){
                case EXCHANGED -> OrderStatus.EXCHANGED;
                case REFUNDED -> OrderStatus.REFUNDED;
                case RETURNED -> OrderStatus.RETURNED;
                default -> throw new CustomException(ErrorCode.INVALID_REQUEST_TYPE);
            };

            OrderStatusHistory history = OrderStatusHistory.builder()
                    .status(status)
                    .memo("A/S 페이지에서 관리자 직접 변경")
                    .changedAt(changedAt)
                    .changedBy(customUserDetails.getId())
                    .orderItem(item)
                    .build();

            item.updateOrderStatus(status, customUserDetails.getId());
            item.getOrderStatusHistories().add(history);

            orderStatusHistoryRepository.save(history);
        }

        return AsChangeStatusResponse.builder()
                .asId(as.getId())
                .status(as.getStatus())
                .changedAt(changedAt)
                .build();
    }


    private OrderItemStatus parseOrderItemStatus(String status) {
        try {
            return OrderItemStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_ORDER_ITEM_STATUS);
        }
    }

    @Transactional
    public void updatePending(CustomUserDetails customUserDetails, UpdateStatusRequest request){
        globalService.validateAdmin(customUserDetails);

        OrderStatus status = OrderStatus.fromPending(request.getStatus());

        List<OrderItem> orderItems = orderItemRepository.findAllById(request.getIdList());

        if (orderItems.size() != request.getIdList().size()) {
            throw new CustomException(ErrorCode.ORDER_ITEM_NOT_FOUND);
        }

        orderItems.forEach(orderItem -> orderItem.updateOrderStatus(status, customUserDetails.getId()));

        List<OrderItem> items = orderItems.stream()
                        .map(item -> {
                            OrderStatusHistory history = OrderStatusHistory.builder()
                                            .status(request.getStatus())
                                            .orderItem(item)
                                            .memo("관리자가 직접 변경")
                                            .changedBy(customUserDetails.getId())
                                            .changedAt(LocalDateTime.now())
                                            .build();

                            item.getOrderStatusHistories().add(history);
                            return item;
                        }
                        ).collect(Collectors.toList());

        orderItemRepository.saveAll(items);

    }

    @Transactional
    public void updatePaid(CustomUserDetails customUserDetails, UpdateStatusRequest request){
        globalService.validateAdmin(customUserDetails);

        OrderStatus status = OrderStatus.fromPaid(request.getStatus());

        List<OrderItem> orderItems = orderItemRepository.findAllById(request.getIdList());
        if (orderItems.size() != request.getIdList().size()) {
            throw new CustomException(ErrorCode.ORDER_ITEM_NOT_FOUND);
        }

        for (OrderItem orderItem : orderItems) {
            if (status != null) {
                orderItem.updateOrderStatus(status, customUserDetails.getId());
            }
        }

        List<OrderItem> items = orderItems.stream()
                .map(item -> {
                            OrderStatusHistory history = OrderStatusHistory.builder()
                                    .status(request.getStatus())
                                    .orderItem(item)
                                    .memo("관리자가 직접 변경")
                                    .changedBy(customUserDetails.getId())
                                    .changedAt(LocalDateTime.now())
                                    .build();

                            item.getOrderStatusHistories().add(history);
                            return item;
                        }
                ).collect(Collectors.toList());

        orderItemRepository.saveAll(items);

    }

    @Transactional
    public void deleteItem(CustomUserDetails customUserDetails, List<Long> idList){
        globalService.validateAdmin(customUserDetails);

        List<OrderItem> orderItems = orderItemRepository.findAllById(idList);
        orderItems.forEach(item -> item.updateOrderStatus(OrderStatus.DELETED, customUserDetails.getId()));

        List<OrderItem> items = orderItems.stream()
                .map(item -> {
                            OrderStatusHistory history = OrderStatusHistory.builder()
                                    .status(OrderStatus.DELETED)
                                    .orderItem(item)
                                    .memo("관리자가 직접 변경")
                                    .changedBy(customUserDetails.getId())
                                    .changedAt(LocalDateTime.now())
                                    .build();

                            item.getOrderStatusHistories().add(history);
                            return item;
                        }
                ).collect(Collectors.toList());

        orderItemRepository.saveAll(items);
    }

    public SearchResult<TaxSearchResponse> getTaxList(CustomUserDetails customUserDetails, TaxSearchRequest request, Pageable pageable){
        globalService.validateAdmin(customUserDetails);

        QOrder order = QOrder.order;
        QTaxInvoice taxInvoice = QTaxInvoice.taxInvoice;
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(request.getKeyword()) && request.getKey() != null) {
            switch (request.getKey()) {
                case NAME -> builder.and(order.orderName.containsIgnoreCase(request.getKeyword()));
                case PHONE_NUM -> builder.and(order.orderPhoneNum.containsIgnoreCase(request.getKeyword()));
                case ALL -> {
                    BooleanBuilder keywordBuilder = new BooleanBuilder();
                    keywordBuilder.or(order.orderName.containsIgnoreCase(request.getKeyword()));
                    keywordBuilder.or(order.orderPhoneNum.containsIgnoreCase(request.getKeyword()));
                    builder.and(keywordBuilder);
                }
            }
        }

        if(request.getStatus() != null && request.getStatus() != TaxStatus.ALL){
            builder.and(taxInvoice.status.eq(request.getStatus()));
        }

        if (request.getRequestDate() != null) {
            if (request.getRequestDate().getStartDate() != null) {
                builder.and(taxInvoice.createdAt.goe(request.getRequestDate().getStartDate().atStartOfDay()));
            }
            if (request.getRequestDate().getEndDate() != null) {
                builder.and(taxInvoice.createdAt.loe(request.getRequestDate().getEndDate().atTime(23, 59, 59)));
            }
        }

        List<Long> pagedClaimIds = jpaQueryFactory
                .select(taxInvoice.id)
                .from(taxInvoice)
                .join(taxInvoice.order, order)
                .where(builder)
                .orderBy(taxInvoice.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (pagedClaimIds.isEmpty()) {
            return SearchResult.<TaxSearchResponse>builder()
                    .totalCount(0)
                    .page(pageable.getPageNumber())
                    .size(pageable.getPageSize())
                    .data(List.of())
                    .build();
        }

        List<Tuple> rows = jpaQueryFactory
                .select(
                        taxInvoice.id,
                        taxInvoice.createdAt,
                        taxInvoice.issuedAt,
                        order.orderName,
                        order.orderPhoneNum,
                        taxInvoice.status,
                        taxInvoice.errorMessage,
                        taxInvoice.pdfUrl
                )
                .from(taxInvoice)
                .join(taxInvoice.order, order)
                .where(taxInvoice.id.in(pagedClaimIds))
                .orderBy(taxInvoice.id.desc())
                .fetch();

        List<TaxSearchResponse> responses = rows.stream()
                .map(r -> {
                    Boolean isIssued = StringUtils.hasText(r.get(taxInvoice.pdfUrl));
                    String failReason = Optional.ofNullable(r.get(taxInvoice.errorMessage)).orElse("-");
                    String pdfUrl = Optional.ofNullable(r.get(taxInvoice.pdfUrl)).orElse("-");

                    return TaxSearchResponse.builder()
                                    .taxId(r.get(taxInvoice.id))
                                    .requestDate(r.get(taxInvoice.createdAt).toLocalDate())
                                    .issuedDate(
                                            Optional.ofNullable(r.get(taxInvoice.issuedAt))
                                                    .map(LocalDateTime::toLocalDate)
                                                    .orElse(null)
                                    )
                                    .name(r.get(order.orderName))
                                    .phoneNum(r.get(order.orderPhoneNum))
                                    .status(r.get(taxInvoice.status))
                                    .failReason(failReason)
                                    .isIssued(isIssued)
                                    .pdfUrl(pdfUrl)
                                    .build();
                        }
                )
                .collect(Collectors.toList());

        Long totalCount = jpaQueryFactory
                .select(taxInvoice.count())
                .from(taxInvoice)
                .join(taxInvoice.order, order)
                .where(builder)
                .fetchOne();

        return SearchResult.<TaxSearchResponse> builder()
                .totalCount(totalCount != null ? totalCount : 0)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .data(responses)
                .build();
    }

    public SearchResult<StateSearchResponse> getStateList(CustomUserDetails customUserDetails, StateSearchRequest request, Pageable pageable){
        globalService.validateAdmin(customUserDetails);

        QOrder order = QOrder.order;
        QPayment payment = QPayment.payment;
        BooleanBuilder builder = new BooleanBuilder().and(payment.paymentStatus.eq(PaymentStatus.PAID));

        if (StringUtils.hasText(request.getKeyword()) && request.getKey() != null) {
            switch (request.getKey()) {
                case ORDER_NUM -> builder.and(order.orderNum.containsIgnoreCase(request.getKeyword()));
                case ORDER_NAME -> builder.and(order.orderName.containsIgnoreCase(request.getKeyword()));
                case ORDER_PHONE_NUM -> builder.and(order.orderPhoneNum.containsIgnoreCase(request.getKeyword()));
                case ALL -> {
                    BooleanBuilder keywordBuilder = new BooleanBuilder();
                    keywordBuilder.or(order.orderNum.containsIgnoreCase(request.getKeyword()));
                    keywordBuilder.or(order.orderName.containsIgnoreCase(request.getKeyword()));
                    keywordBuilder.or(order.orderPhoneNum.containsIgnoreCase(request.getKeyword()));
                    builder.and(keywordBuilder);
                }
            }
        }

        if (request.getOrderDate() != null) {
            if (request.getOrderDate().getStartDate() != null) {
                builder.and(order.orderDate.goe(request.getOrderDate().getStartDate().atStartOfDay()));
            }
            if (request.getOrderDate().getEndDate() != null) {
                builder.and(order.orderDate.loe(request.getOrderDate().getEndDate().atTime(23, 59, 59)));
            }
        }

        List<Long> pagedClaimIds = jpaQueryFactory
                .select(order.id)
                .from(order)
                .join(order.payment, payment)
                .where(builder)
                .orderBy(order.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (pagedClaimIds.isEmpty()) {
            return SearchResult.<StateSearchResponse>builder()
                    .totalCount(0)
                    .page(pageable.getPageNumber())
                    .size(pageable.getPageSize())
                    .data(List.of())
                    .build();
        }

        List<Tuple> rows = jpaQueryFactory
                .select(
                        order.id,
                        order.orderNum,
                        order.orderName,
                        order.orderPhoneNum,
                        payment.paidDate
                )
                .from(order)
                .join(order.payment, payment)
                .where(order.id.in(pagedClaimIds))
                .orderBy(order.id.desc())
                .fetch();

        List<StateSearchResponse> responses = rows.stream()
                .map(r -> StateSearchResponse.builder()
                                    .orderId(r.get(order.id))
                                    .orderNum(r.get(order.orderName))
                                    .orderName(r.get(order.orderName))
                                    .orderPhoneNum(r.get(order.orderPhoneNum))
                                    .paidDate(Optional.ofNullable(r.get(payment.paidDate))
                                            .map(LocalDateTime::toLocalDate)
                                            .orElse(null))
                                    .build()
                )
                .collect(Collectors.toList());

        Long totalCount = jpaQueryFactory
                .select(order.count())
                .from(order)
                .join(order.payment, payment)
                .where(builder)
                .fetchOne();

        return SearchResult.<StateSearchResponse> builder()
                .totalCount(totalCount != null ? totalCount : 0)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .data(responses)
                .build();
    }

    public SupplyInfoResponse getInfo(CustomUserDetails customUserDetails, Long orderId){
        globalService.validateAdmin(customUserDetails);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        Optional<Statement> statement = statementRepository.findFirstByOrderOrderByIdDesc(order);

        String deliveryDate = statement.map(Statement::getDeliveryDate).orElse("-");
        String accountNum = statement.map(Statement::getAccountNum).orElse("-");

        CompanyInfo companyInfo = companyInfoRepository.findById(1L)
                .orElseThrow(() -> new CustomException(ErrorCode.COMPANY_INFO_NOT_FOUND));

        return SupplyInfoResponse.builder()
                .orderId(orderId)
                .bizNum(companyInfo.getBizNum())
                .corporationName(companyInfo.getCompanyName())
                .phoneNum(companyInfo.getPhoneNum())
                .faxNum(companyInfo.getFaxNum())
                .deliveryDate(deliveryDate)
                .address(companyInfo.getAddress())
                .detailAddress(companyInfo.getDetailAddress())
                .accountNum(accountNum)
                .build();
    }

    @Transactional
    public SupplyInfoResponse updateInfo(CustomUserDetails customUserDetails, SupplyInfoRequest request, Long orderId){
        globalService.validateAdmin(customUserDetails);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        Optional<Statement> statement = statementRepository.findFirstByOrderOrderByIdDesc(order);

        Statement stt;

        if (statement.isPresent()) {
            stt = statement.get();

            stt.update(request.getDeliveryDate(), request.getAccountNum(), customUserDetails.getId());
        }else {
            stt = Statement.builder()
                    .accountNum(request.getAccountNum())
                    .deliveryDate(request.getDeliveryDate())
                    .handledBy(customUserDetails.getId())
                    .order(order)
                    .build();

            statementRepository.save(stt);
        }

        CompanyInfo companyInfo = companyInfoRepository.findById(1L)
                .orElseThrow(() -> new CustomException(ErrorCode.COMPANY_INFO_NOT_FOUND));

        return SupplyInfoResponse.builder()
                .orderId(orderId)
                .bizNum(companyInfo.getBizNum())
                .corporationName(companyInfo.getCompanyName())
                .phoneNum(companyInfo.getPhoneNum())
                .faxNum(companyInfo.getFaxNum())
                .deliveryDate(stt.getDeliveryDate())
                .address(companyInfo.getAddress())
                .detailAddress(companyInfo.getDetailAddress())
                .accountNum(stt.getAccountNum())
                .build();
    }

    public StatementResponse getStatement(CustomUserDetails customUserDetails, Long orderId){
        globalService.validateAdmin(customUserDetails);

        QOrder order = QOrder.order;
        QOrderItem orderItem = QOrderItem.orderItem;

        Order ord = jpaQueryFactory
                .selectFrom(order)
                .leftJoin(order.orderItems, orderItem).fetchJoin()
                .where(order.id.eq(orderId))
                .distinct()
                .fetchOne();

        if (ord == null) throw new CustomException(ErrorCode.ORDER_NOT_FOUND);

        User user = userRepository.findById(ord.getUserId()).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String bizNum;

        if(user.getRole() == Role.BUSINESS){
            Business business = businessRepository.findById(ord.getUserId())
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

            bizNum = business.getBizRegistrationNum();
        }else {
            bizNum = "-";
        }

        BigDecimal unitSubtotalVat = BigDecimal.ZERO;

        Map<String, Integer> itemCountMap = ord.getOrderItems().stream()
                .collect(Collectors.toMap(
                        item -> item.getOptionCode() + "_" + item.getFinalPrice().stripTrailingZeros().toPlainString(),
                        item -> 1,
                        Integer::sum
                ));

        Set<String> itemMap = new HashSet<>();

        List<StatementProductResponse> productResponseList = new ArrayList<>();

        for (OrderItem item : ord.getOrderItems()) {
            String key = item.getOptionCode() + "_" + item.getFinalPrice().stripTrailingZeros().toPlainString();

            if (itemMap.contains(key)) {
                continue;
            }

            String memo = "-";

            if(item.getCouponId() != null){
                memo = "쿠폰 사용";
            }

            itemMap.add(key);
            int quantity = itemCountMap.get(key);

            BigDecimal salePriceVat = item.getFinalPrice().multiply(BigDecimal.valueOf(quantity));

            StatementProductResponse response = StatementProductResponse.builder()
                    .itemId(item.getId())
                    .productId(item.getProductId())
                    .productName(item.getProductName())
                    .productCode(item.getOptionCode())
                    .productSize(item.getItemName())
                    .quantity(quantity)
                    .unitOriginalPrice(item.getOriginalPrice())
                    .unitSalePrice(item.getFinalPrice())
                    .salePriceVat(salePriceVat)
                    .note(memo)
                    .build();

            productResponseList.add(response);
        }

        BigDecimal totalPriceInclVat = ord.getFinalPaidAmount();
        BigDecimal totalPriceExclVat = totalPriceInclVat.multiply(BigDecimal.valueOf(0.9));
        Integer quantitySubtotal = itemCountMap.values().stream().mapToInt(Integer::intValue).sum();

        return StatementResponse.builder()
                .orderId(ord.getId())
                .orderDate(ord.getOrderDate().toLocalDate())
                .bizNum(bizNum)
                .address(ord.getAddress())
                .phoneNum(ord.getOrderPhoneNum())
                .usedMileage(ord.getUsedMileage())
                .build();



    }


}
