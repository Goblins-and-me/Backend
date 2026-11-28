package com.shop.SweetStory.catalog.filling.repository;

import com.shop.SweetStory.catalog.filling.entity.Filling;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FillingRepository extends JpaRepository<Filling, Long> {

    Optional<Filling> findByName(String name);

}




