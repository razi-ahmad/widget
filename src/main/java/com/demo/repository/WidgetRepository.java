package com.demo.repository;

import com.demo.domain.Widget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WidgetRepository extends JpaRepository<Widget, UUID> {

    @Query("SELECT w from Widget w order by w.zIndex.index asc")
    List<Widget> getAllOrderByZIndex();

    @Query("SELECT w from Widget w order by w.zIndex.index asc")
    Page<Widget> getAllOrderByZIndex(Pageable pageable);
}
