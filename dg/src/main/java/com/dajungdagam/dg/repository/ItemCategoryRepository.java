package com.dajungdagam.dg.repository;

import com.dajungdagam.dg.domain.entity.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemCategoryRepository extends JpaRepository<ItemCategory, Long> {

    ItemCategory findByCategoryName(String categoryName);
}
