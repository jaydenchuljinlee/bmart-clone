package com.java.bmart.domain.category.service;

import com.java.bmart.domain.category.MainCategory;
import com.java.bmart.domain.category.SubCategory;
import com.java.bmart.domain.category.exception.DuplicateCategoryNameException;
import com.java.bmart.domain.category.exception.NotFoundCategoryException;
import com.java.bmart.domain.category.repository.MainCategoryRepository;
import com.java.bmart.domain.category.repository.SubCategoryRepository;
import com.java.bmart.domain.category.service.request.RegisterMainCategoryCommand;
import com.java.bmart.domain.category.service.request.RegisterSubCategoryCommand;
import com.java.bmart.domain.category.service.response.FindMainCategoriesResponse;
import com.java.bmart.domain.category.service.response.FindSubCategoriesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryService {
    private final MainCategoryRepository mainCategoryRepository;
    private final SubCategoryRepository subCategoryRepository;

    @Transactional(readOnly = true)
    public FindMainCategoriesResponse findAllMainCategories() {
        List<MainCategory> mainCategories = mainCategoryRepository.findAll();
        return FindMainCategoriesResponse.from(mainCategories);
    }

    @Transactional(readOnly = true)
    public FindSubCategoriesResponse findAllSubCategories(final Long mainCategoryId) {
        MainCategory mainCategory = mainCategoryRepository.findById(mainCategoryId)
                .orElseThrow(() -> new NotFoundCategoryException("ID에 해당하는 카테고리 미존재"));

        List<SubCategory> subCategories = subCategoryRepository.findByMainCategory(mainCategory);

        return FindSubCategoriesResponse.from(subCategories);
    }

    @Transactional
    public Long saveMainCategory(RegisterMainCategoryCommand registerMainCategoryCommand) {
        String newMainCategoryName = registerMainCategoryCommand.name();

        if (mainCategoryRepository.existsByName(newMainCategoryName)) throw new DuplicateCategoryNameException("이미 존재하는 카테고리");

        MainCategory mainCategory = new MainCategory(newMainCategoryName);

        return mainCategoryRepository.save(mainCategory).getMainCategoryId();
    }

    @Transactional
    public Long saveSubCategory(RegisterSubCategoryCommand registerSubCategoryCommand) {
        String newSubCategoryName = registerSubCategoryCommand.name();
        Long parentCategoryId = registerSubCategoryCommand.mainCategoryId();
        MainCategory mainCategory = mainCategoryRepository.findById(parentCategoryId)
                .orElseThrow(() -> new NotFoundCategoryException("ID에 해당하는 카테고리 미존재"));

        if (subCategoryRepository.existsByMainCategoryAndName(mainCategory, newSubCategoryName)) throw new DuplicateCategoryNameException("이미 존재하는 카테고리");

        SubCategory subCategory = new SubCategory(mainCategory, newSubCategoryName);

        return subCategoryRepository.save(subCategory).getSubCategoryId();

    }

}
