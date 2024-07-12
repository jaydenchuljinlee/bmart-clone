package com.java.bmart.domain.category.repository;

import com.java.bmart.domain.category.MainCategory;
import com.java.bmart.domain.category.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {

    boolean existsByMainCategoryAndName(MainCategory mainCategory, String name);

    List<SubCategory> findByMainCategory(MainCategory mainCategory);

    Optional<SubCategory> findByName(String name);
}
