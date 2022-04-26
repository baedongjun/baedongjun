package com.common.member;

import javax.annotation.Resource;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl3 implements UserDetailsService {

	@Resource
	private MemberDAO5 memberDAO;
	@Resource
	PasswordEncoder passwordEncoder;

	public void insertUser(MemberDTO4 memberDto) {
		memberDto.setUser_pass(passwordEncoder.encode(memberDto.getPassword()));
		memberDAO.insertUser(memberDto);
	}

	public void updateUser(MemberDTO4 memberDto) {
		memberDto.setUser_pass(passwordEncoder.encode(memberDto.getPassword()));
		memberDAO.updateUser(memberDto);
	}


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		MemberDTO4 memberDTO = memberDAO.readUser(username);

		if (memberDTO==null) throw new InternalAuthenticationServiceException(username);

		return memberDTO;
	}
}
