package com.github.acnaweb.study_apir.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.acnaweb.study_apir.model.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>{


}