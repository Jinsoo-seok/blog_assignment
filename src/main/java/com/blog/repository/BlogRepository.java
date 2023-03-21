package com.blog.repository;

import com.blog.vo.BlogVo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<BlogVo, Long> {

    @Query("SELECT B FROM BlogVo B WHERE B.blogName = :name")
    BlogVo findByName(String name);

    @Query("SELECT B FROM BlogVo B")
    List<BlogVo> findTop10By(Pageable pageable);
}
