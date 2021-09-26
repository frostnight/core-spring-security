package io.security.corespringsecurity.security.service;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import io.security.corespringsecurity.domain.entity.Account;
import lombok.Data;

@Data
public class AccountContext extends User {

	private final Account account;

	public AccountContext(Account account, List<GrantedAuthority> roles) {
		super(account.getUsername(), account.getPassword(), roles);
		this.account = account;
	}

}
