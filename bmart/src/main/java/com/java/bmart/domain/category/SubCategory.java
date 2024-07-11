package com.java.bmart.domain.category;

import com.java.bmart.domain.item.Item;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subCategoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_category_id")
    private MainCategory mainCategory;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "subCategory")
    private List<Item> items = new ArrayList<>();

    public SubCategory(MainCategory mainCategory, String name) {
        this.mainCategory = mainCategory;
        this.name = name;
    }
}

