package com.tutorials.spring.controller;

import com.tutorials.spring.Application;
import com.tutorials.spring.dao.AccountDao;
import com.tutorials.spring.dao.BookmarkDao;
import com.tutorials.spring.model.Account;
import com.tutorials.spring.model.Bookmark;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class BookmarkRestControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf-8"));

    private MockMvc mockMvc;

    private String username = "test";

    private HttpMessageConverter messageConverter;

    private Account account;

    private List<Bookmark> bookmarks = new ArrayList<>();

    @Autowired
    private BookmarkDao bookmarkDao;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    public void setConverters(HttpMessageConverter<?>[] converters) {
        messageConverter = Arrays.stream(converters)
                .filter(mc -> mc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);
        assertNotNull(messageConverter);
    }

    @Before
    public void setUp() throws Exception {
        mockMvc = webAppContextSetup(context).build();

        account = accountDao.save(new Account(username, "password"));
        bookmarks.add(bookmarkDao.save(new Bookmark(account,
                "http://bookmark.com/1/" + username, "A description")));
        bookmarks.add(bookmarkDao.save(new Bookmark(account,
                "http://bookmark.com/2/" + username, "A description")));
    }

    @After
    public void tearDown() {
        bookmarkDao.delete(bookmarks);
        accountDao.delete(account);
    }

    @Test
    public void userNotFound() throws Exception {
        mockMvc.perform(get("/dummy/bookmarks"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getBookmarks() throws Exception {
        mockMvc.perform(get(String.format("/%s/bookmarks", username)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(bookmarks.size())))
                .andExpect(jsonPath("$[0].id", is(bookmarks.get(0).getId().intValue())))
                .andExpect(jsonPath("$[0].url", is("http://bookmark.com/1/" + username)))
                .andExpect(jsonPath("$[0].description", is("A description")))
                .andExpect(jsonPath("$[1].id", is(bookmarks.get(1).getId().intValue())))
                .andExpect(jsonPath("$[1].url", is("http://bookmark.com/2/" + username)))
                .andExpect(jsonPath("$[1].description", is("A description")));
    }

}