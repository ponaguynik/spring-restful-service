package com.tutorials.spring.controller;

import com.tutorials.spring.dao.AccountDao;
import com.tutorials.spring.dao.BookmarkDao;
import com.tutorials.spring.exception.BookmarkNotFoundException;
import com.tutorials.spring.exception.UserNotFoundException;
import com.tutorials.spring.model.Account;
import com.tutorials.spring.model.Bookmark;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;

@RequestMapping("/{username}/bookmarks")
@RestController
public class BookmarkRestController {

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private BookmarkDao bookmarkDao;

    @RequestMapping(method = RequestMethod.GET)
    public Collection<Bookmark> getBookmarks(@PathVariable String username) {
        validateUser(username);
        return bookmarkDao.findByAccountUsername(username);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public Bookmark getBookmark(@PathVariable String username, @PathVariable Long id) {
        validateUser(username);
        Collection<Bookmark> bookmarks = bookmarkDao.findByAccountUsername(username);
        Bookmark bookmark = bookmarkDao.findOne(id);
        if (bookmarks.contains(bookmark)) {
            return bookmark;
        } else {
            throw new BookmarkNotFoundException(String.valueOf(id));
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> addBookmark(@PathVariable String username, @RequestBody Bookmark bookmark) {
        validateUser(username);
        return accountDao.findByUsername(username)
                .map(account -> {
                    Bookmark result = bookmarkDao.save(new Bookmark(account, bookmark.getUrl(), bookmark.getDescription()));
                    URI location = ServletUriComponentsBuilder
                            .fromCurrentRequest().path("/{id}")
                            .buildAndExpand(result.getId()).toUri();
                    return ResponseEntity.created(location).build();
                })
                .orElse(ResponseEntity.noContent().build());
    }

    private void validateUser(String username) throws UserNotFoundException {
        accountDao.findByUsername(username)
                .orElseThrow(
                        () -> new UserNotFoundException(username)
                );
    }
}
