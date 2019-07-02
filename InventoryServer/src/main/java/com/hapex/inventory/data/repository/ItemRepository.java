package com.hapex.inventory.data.repository;

import com.hapex.inventory.data.entity.Item;
import com.hapex.inventory.data.entity.QItem;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ItemRepository extends PagingAndSortingRepository<Item, Long>,
        QuerydslPredicateExecutor<Item>, QuerydslBinderCustomizer<QItem> {

    @Override
    default void customize(QuerydslBindings bindings, QItem root) {
        bindings.bind(String.class)
                .first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
    }
}
