package livart.erp.domain.order;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import livart.common.Auth.CustomUserDetails;
import livart.common.domain.order.entity.*;
import livart.common.domain.order.repository.OrderItemRepository;
import livart.common.domain.order.repository.OrderRepository;
import livart.common.domain.order.repository.OrderStatusHistoryRepository;
import livart.common.domain.product.entity.QProduct;
import livart.common.dto.enums.order.*;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.mapper.SearchResult;
import livart.common.service.GlobalService;
import livart.erp.domain.order.dto.request.OrderItemStatus;
import livart.erp.domain.order.dto.request.OrderSearchRequest;
import livart.erp.domain.order.dto.request.UpdateStatusRequest;
import livart.erp.domain.order.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final GlobalService globalService;
    private final OrderItemRepository orderItemRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;

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
                .from(orderItem)
                .join(orderItem.order, order)
                .where(builder)
                .orderBy(order.orderDate.desc())
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
                        orderItem.finalPrice,
                        orderItem.deliveryStatus,
                        payment.paymentMethod,
                        payment.paymentStatus,
                        orderItemOption.optionName,
                        orderItemOption.valueName,
                        orderStatusHistory.status
                )
                .from(orderItem)
                .join(orderItem.order, order)
                .leftJoin(orderItem.orderItemOptions, orderItemOption)
                .leftJoin(payment).on(payment.order.eq(order))
                .leftJoin(orderStatusHistory).on(orderStatusHistory.orderItem.eq(orderItem))
                .where(builder)
                .fetch();

        Map<Long, OrderAllResponse.OrderAllResponseBuilder> groupedMap = new LinkedHashMap<>();
        Map<Long, List<OrderOptionResponse>> optionMap = new HashMap<>();

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
                            .productName(row.get(orderItem.productName))
                            .finalPrice(row.get(orderItem.finalPrice))
                            .deliveryStatus(row.get(orderItem.deliveryStatus))
                            .paymentMethod(row.get(payment.paymentMethod))
                            .paymentStatus(row.get(payment.paymentStatus))
            );

            convertToOrderOption(row).ifPresent(opt ->
                    optionMap.computeIfAbsent(orderItemId, id -> new ArrayList<>()).add(opt)
            );

            OrderItemStatus status = row.get(orderStatusHistory.status);
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

                    List<OrderOptionResponse> optionList = optionMap.getOrDefault(orderItemId, List.of());
                    orderAllResponseBuilder.orderOption(optionList);

                    orderAllResponseBuilder.isReturned(returnMap.getOrDefault(orderItemId, false));
                    orderAllResponseBuilder.isExchanged(exchangeMap.getOrDefault(orderItemId, false));
                    orderAllResponseBuilder.isRefunded(refundMap.getOrDefault(orderItemId, false));

                    return orderAllResponseBuilder.build();
                })
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

        builder.and(orderItem.orderStatus.eq(OrderStatus.CONFIRMED));

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
                .from(orderItem)
                .join(orderItem.order, order)
                .where(builder)
                .orderBy(order.orderDate.desc()) // 정렬 필수!
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
                        orderItemOption.optionName,
                        orderItemOption.valueName,
                        product.brand,
                        product.id
                )
                .from(orderItem)
                .join(orderItem.order, order)
                .join(product).on(orderItem.productId.eq(product.id))
                .leftJoin(orderItem.orderItemOptions, orderItemOption)
                .where(builder)
                .fetch();

        Map<Long, OrderInfoResponse.OrderInfoResponseBuilder> groupedMap = new LinkedHashMap<>();
        Map<Long, List<OrderOpInfoResponse>> optionMap = new HashMap<>();

        for (Tuple row : rows) {
            Long orderItemId = row.get(orderItem.id);

            // Builder 초기화
            groupedMap.computeIfAbsent(orderItemId, id ->
                    OrderInfoResponse.builder()
                            .orderId(row.get(order.id))
                            .orderItemId(orderItemId)
                            .orderNum(row.get(order.orderNum))
                            .orderName(row.get(order.orderName))
                            .orderDate(row.get(order.orderDate).toLocalDate())
                            .productName(row.get(orderItem.productName))
                            .productId(row.get(product.id))
                            .brand(row.get(product.brand))
            );

            Long optionId = row.get(orderItemOption.id);
            String optionName = row.get(orderItemOption.optionName);
            String optionValue = row.get(orderItemOption.valueName);

            if (optionId != null && optionName != null && optionValue != null) {
                OrderOpInfoResponse orderOption = OrderOpInfoResponse.builder()
                        .optionName(optionName)
                        .optionValue(optionValue)
                        .optionId(optionId)
                        .build();

                List<OrderOpInfoResponse> optionList = optionMap.computeIfAbsent(orderItemId, id -> new ArrayList<>());
                optionList.add(orderOption);
            }
        }

        List<OrderInfoResponse> responseList = groupedMap.entrySet().stream()
                .map(entry -> {
                    Long orderItemId = entry.getKey();
                    OrderInfoResponse.OrderInfoResponseBuilder orderBuilder = entry.getValue();

                    List<OrderOpInfoResponse> optionList = optionMap.getOrDefault(orderItemId, List.of());
                    orderBuilder.orderOption(optionList);

                    return orderBuilder.build();
                })
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
                case EXCHANGED -> {
                    builder.and(orderItem.orderStatus.eq(OrderStatus.EXCHANGED));
                    builder.and(afterServiceRequest.requestType.eq(OrderStatus.EXCHANGED));
                    builder.and(afterServiceRequest.status.eq(RequestStatus.APPROVED));
                }
                case RETURNED -> {
                    builder.and(orderItem.orderStatus.eq(OrderStatus.RETURNED));
                    builder.and(afterServiceRequest.requestType.eq(OrderStatus.RETURNED));
                    builder.and(afterServiceRequest.status.eq(RequestStatus.APPROVED));
                }
                case REFUNDED -> {
                    builder.and(orderItem.orderStatus.eq(OrderStatus.REFUNDED));
                    builder.and(afterServiceRequest.requestType.eq(OrderStatus.REFUNDED));
                    builder.and(afterServiceRequest.status.eq(RequestStatus.APPROVED));
                }
                case CANCELED -> {
                    builder.and(orderItem.orderStatus.eq(OrderStatus.CANCELED));
                    builder.and(afterServiceRequest.requestType.eq(OrderStatus.CANCELED));
                    builder.and(afterServiceRequest.status.eq(RequestStatus.APPROVED));
                }
                case CONFIRMED -> builder.and(orderItem.orderStatus.eq(OrderStatus.CONFIRMED));
                case WAITING_SHIPMENT -> builder.and(orderItem.deliveryStatus.eq(DeliveryStatus.WAITING_SHIPMENT));
                case DELIVERED -> builder.and(orderItem.deliveryStatus.eq(DeliveryStatus.DELIVERED));
                case PENDING -> builder.and(orderItem.orderStatus.eq(OrderStatus.PENDING));
                case FAILED -> builder.and(orderItem.orderStatus.eq(OrderStatus.FAILED));
                case PAID -> builder.and(orderItem.orderStatus.eq(OrderStatus.PAID));
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
                .from(orderItem)
                .join(orderItem.order, order)
                .leftJoin(afterServiceRequest).on(afterServiceRequest.orderItem.eq(orderItem))
                .where(builder)
                .orderBy(order.orderDate.desc())
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
                .select(
                        order.id,
                        orderItem.id,
                        order.userId,
                        order.orderNum,
                        order.orderName,
                        order.orderDate,
                        orderItem.productName,
                        orderItem.finalPrice,
                        orderItem.deliveryStatus,
                        payment.paymentMethod,
                        payment.paymentStatus,
                        payment.account,
                        payment.depositor,
                        orderItemOption.optionName,
                        orderItemOption.valueName,
                        afterServiceRequest.requestReason
                )
                .from(orderItem)
                .join(orderItem.order, order)
                .leftJoin(orderItem.orderItemOptions, orderItemOption)
                .leftJoin(payment).on(payment.order.eq(order))
                .leftJoin(afterServiceRequest).on(afterServiceRequest.orderItem.eq(orderItem))
                .where(builder)
                .fetch();

        Map<Long, OrderIndResponse.OrderIndResponseBuilder> groupedMap = new LinkedHashMap<>();
        Map<Long, List<OrderOptionResponse>> optionMap = new HashMap<>();

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
                            .productName(row.get(orderItem.productName))
                            .finalPrice(row.get(orderItem.finalPrice))
                            .deliveryStatus(row.get(orderItem.deliveryStatus))
                            .paymentMethod(row.get(payment.paymentMethod))
                            .paymentStatus(row.get(payment.paymentStatus))
                            .account(row.get(payment.account))
                            .depositor(row.get(payment.depositor))
                            .requestReason(row.get(afterServiceRequest.requestReason))
                            .lapsedDate(lap)
            );

            convertToOrderOption(row).ifPresent(opt ->
                    optionMap.computeIfAbsent(orderItemId, id -> new ArrayList<>()).add(opt)
            );
        }

        List<OrderIndResponse> responseList = groupedMap.entrySet().stream()
                .map(entry -> {
                    Long orderItemId = entry.getKey();
                    OrderIndResponse.OrderIndResponseBuilder orderIndResponseBuilder = entry.getValue();

                    List<OrderOptionResponse> optionList = optionMap.getOrDefault(orderItemId, List.of());
                    orderIndResponseBuilder.orderOption(optionList);

                    return orderIndResponseBuilder.build();
                })
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

    private Optional<OrderOptionResponse> convertToOrderOption(Tuple row) {
        String optionName = row.get(QOrderItemOption.orderItemOption.optionName);
        String optionValue = row.get(QOrderItemOption.orderItemOption.valueName);

        if (optionName == null || optionValue == null) return Optional.empty();

        return Optional.of(OrderOptionResponse.builder()
                .optionName(optionName)
                .optionValue(optionValue)
                .build());
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
        OrderStatus status;

        switch (request.getStatus()) {
            case PENDING -> status = OrderStatus.PENDING;
            case PAID -> status = OrderStatus.PAID;
            case CANCELED -> status = OrderStatus.CANCELED;
            default -> throw new CustomException(ErrorCode.INVALID_TYPE);
        }

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

        OrderStatus status = null;
        DeliveryStatus deliveryStatus = null;

        switch (request.getStatus()) {
            case PAID -> status = OrderStatus.PAID;
            case WAITING_SHIPMENT -> deliveryStatus = DeliveryStatus.WAITING_SHIPMENT;
            case DELIVERED -> deliveryStatus = DeliveryStatus.DELIVERED;
            case CANCELED -> status = OrderStatus.CANCELED;
            default -> throw new CustomException(ErrorCode.INVALID_TYPE);
        }

        List<OrderItem> orderItems = orderItemRepository.findAllById(request.getIdList());
        if (orderItems.size() != request.getIdList().size()) {
            throw new CustomException(ErrorCode.ORDER_ITEM_NOT_FOUND);
        }

        for (OrderItem orderItem : orderItems) {
            if (status != null) {
                orderItem.updateOrderStatus(status, customUserDetails.getId());
            }
            if (deliveryStatus != null) {
                orderItem.updateDelStatus(deliveryStatus, customUserDetails.getId());
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
                                    .status(OrderItemStatus.DELETED)
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

}
