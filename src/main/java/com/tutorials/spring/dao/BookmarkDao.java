package com.tutorials.spring.dao;

import com.tutorials.spring.model.Bookmark;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

public interface BookmarkDao extends CrudRepository<Bookmark, Long> {

    Collection<Bookmark> findByAccountUsername(String username);

    @Transactional
    List<Bookmark> removeByAccountId(Long id);
}
