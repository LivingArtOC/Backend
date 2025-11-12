package livart.common.util;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
public class QuerydslSortUtil {

    public static <T> OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable, Class<T> entityClass, String alias) {
        return getOrderSpecifiers(pageable, entityClass, alias, null);
    }

    public static <T> OrderSpecifier<?>[] getOrderSpecifiers(
            Pageable pageable,
            Class<T> entityClass,
            String alias,
            Set<String> allowedFields
    ) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        PathBuilder<T> entityPath = new PathBuilder<>(entityClass, alias);

        for (Sort.Order order : pageable.getSort()) {
            String property = order.getProperty();

            if (allowedFields != null && !allowedFields.contains(property)) {
                log.warn("Disallowed sort property attempted: {}", property);
                continue;
            }

            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            try {
                orderSpecifiers.add(new OrderSpecifier<>(direction, entityPath.getComparable(property, Comparable.class)).nullsLast());
                log.debug("Sort applied: {} {}", property, direction);
            } catch (Exception e) {
                log.error("Invalid sort property: {}", property, e);
                throw new CustomException(ErrorCode.INVALID_SORT_PARAM_VARIABLES);
            }
        }

        if (orderSpecifiers.isEmpty()) {
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, entityPath.getComparable("createdAt", Comparable.class)));
        }

        return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }
}

