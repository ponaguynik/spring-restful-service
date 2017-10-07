package com.tutorials.spring.dao;

import com.tutorials.spring.model.Account;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AccountDao extends CrudRepository<Account, Long> {

    Optional<Account> findByUsername(String username);
}
