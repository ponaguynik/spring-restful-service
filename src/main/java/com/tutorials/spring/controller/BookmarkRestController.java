package com.tutorials.spring.controller;

import com.tutorials.spring.dao.AccountDao;
import com.tutorials.spring.dao.BookmarkDao;
import com.tutorials.spring.exception.BookmarkNotFoundException;
import com.tutorials.spring.exception.UserNotFoundException;
import com.tutorials.spring.model.Bookmark;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    private void validateUser(String username) throws UserNotFoundException {
        accountDao.findByUsername(username)
                .orElseThrow(
                        () -> new UserNotFoundException(username)
                );
    }
}
