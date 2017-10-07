package com.tutorials.spring.dao;

import com.tutorials.spring.model.Bookmark;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface BookmarkDao extends CrudRepository<Bookmark, Long> {

    Collection<Bookmark> findByAccountUsername(String username);
}
