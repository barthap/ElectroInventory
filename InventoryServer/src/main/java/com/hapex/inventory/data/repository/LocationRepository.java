package com.hapex.inventory.data.repository;

import com.hapex.inventory.data.entity.Location;
import com.hapex.inventory.data.entity.QCategory;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface LocationRepository extends PagingAndSortingRepository<Location, Long>,
        QuerydslPredicateExecutor<Location> {//, QuerydslBinderCustomizer<QLocation> {

    /*@Override
    default void customize(QuerydslBindings bindings, QLocation location) {
        bindings.bind(String.class)
                .first((SingleValueBinding< StringPath, String>) StringExpression::containsIgnoreCase);
    }*/
}
