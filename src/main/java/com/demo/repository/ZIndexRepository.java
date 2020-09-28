package com.demo.repository;

import com.demo.domain.ZIndex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ZIndexRepository extends JpaRepository<ZIndex, UUID> {

    boolean existsByIndex(Integer index);

    @Query("select max(z.index) from ZIndex z")
    Integer findMaxZIndex();

    @Query("SELECT z FROM ZIndex z WHERE z.index >= :index")
    List<ZIndex> findAllByIndex(Integer index);
}
