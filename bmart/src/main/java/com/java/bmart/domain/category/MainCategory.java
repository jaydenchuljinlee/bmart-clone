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
public class MainCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mainCategoryId;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "mainCategory")
    private List<SubCategory> subCategories = new ArrayList<>();

    @OneToMany(mappedBy = "mainCategory")
    private List<Item> items = new ArrayList<>();

    public MainCategory(String name) {
        this.name = name;
    }
}
