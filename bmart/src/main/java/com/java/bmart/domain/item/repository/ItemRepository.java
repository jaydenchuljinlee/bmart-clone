package com.java.bmart.domain.item.repository;

import com.java.bmart.domain.category.MainCategory;
import com.java.bmart.domain.category.SubCategory;
import com.java.bmart.domain.item.Item;
import com.java.bmart.domain.item.service.ItemSortType;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Item i where i.itemId = :itemId")
    Optional<Item> findByItemId(@Param("itemId") Long itemId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Item i set i.quantity = i.quantity + :quantity where i.itemId = :itemId")
    void increaseQuantity(@Param("itemId") Long itemId, @Param("quantity") int quantity);

    @Query("select i from Item i where i.itemId in ?1")
    List<Item> findByItemIdIn(Collection<Long> itemIds);

}
