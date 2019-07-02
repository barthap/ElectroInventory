package com.hapex.inventory.data.repository;

import com.hapex.inventory.data.entity.Category;
import com.hapex.inventory.data.entity.QCategory;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CategoryRepository extends PagingAndSortingRepository<Category, Long>,
        QuerydslPredicateExecutor<Category>, QuerydslBinderCustomizer<QCategory> {

    @Override
    default void customize(QuerydslBindings bindings, QCategory category) {
        bindings.bind(String.class)
                .first((SingleValueBinding< StringPath, String>) StringExpression::containsIgnoreCase);
    }
}
