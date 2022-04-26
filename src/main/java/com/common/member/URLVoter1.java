package com.common.member;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;

@Component
public class URLVoter1 implements AccessDecisionVoter<Object> {
 	MemberDAO5 memberDAO;

	private AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();

	public URLVoter1 (MemberDAO5 memberDAO) {
		this.memberDAO = memberDAO;
	}

	@Override
	public boolean supports(ConfigAttribute attribute) {
		return true;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return true;
	}

	@Override
	public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
		if (!authenticationTrustResolver.isAnonymous(authentication)) {
			FilterInvocation fi = (FilterInvocation) object;
			String url = fi.getRequestUrl();

			UserDetails user = (UserDetails) authentication.getPrincipal();
			String username = user.getUsername();

			Map<String, String> map = new HashMap<>();
			map.put("url", url);
			map.put("username", username);

			String isAuth;
			isAuth = (username.equals("master")) ? "O" : memberDAO.viewUserAuthority(map);

			if (isAuth.equals("")) {
				return ACCESS_ABSTAIN;
			} else if (isAuth.equals("O")){
				return ACCESS_GRANTED;
			} else {
				return ACCESS_DENIED;
			}
		} else {
			return ACCESS_ABSTAIN;
		}
	}
}