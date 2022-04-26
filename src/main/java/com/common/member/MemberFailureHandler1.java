package com.common.member;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class MemberFailureHandler1 extends SimpleUrlAuthenticationFailureHandler {

	private String username;
	private String ingCheck;
	private String defaultUrl;

	@Resource
	private MemberDAO5 memberDAO;
	@Resource
	private MessageSource messageSource;

	public MemberFailureHandler1 (String defaultUrl) {
		this.defaultUrl = defaultUrl;
	}

	public MemberFailureHandler1 () {}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

		this.username = request.getParameter("user_id");
		this.ingCheck = Optional.ofNullable(request.getParameter("ingCheck")).orElse("");

		String errormsg = exception.getMessage();
		if (exception instanceof BadCredentialsException) {
			//로그인 실패횟수 추가
			memberDAO.addFailure(username);
			//계정잠금 이전
			if (memberDAO.readFailure(username) <= 5)
			errormsg = messageSource.getMessage("error.BadCredentials", null, Locale.getDefault());
		} else if (exception instanceof InternalAuthenticationServiceException) {
			errormsg = messageSource.getMessage("error.BadCredentials", null, Locale.getDefault());
		} else if (exception instanceof DisabledException) {
			errormsg = messageSource.getMessage("error.Disaled", null, Locale.getDefault());
		} else if (exception instanceof CredentialsExpiredException) {
			errormsg = messageSource.getMessage("error.CredentialsExpired", null, Locale.getDefault());
		} else if (exception instanceof LockedException) {
			errormsg = messageSource.getMessage("error.Locked", null, Locale.getDefault());
		}

		request.setAttribute("username", username);
		request.setAttribute("errormsg", errormsg);
		request.setAttribute("ingCheck", ingCheck);
		request.setAttribute("ip", request.getRemoteAddr());

		request.getRequestDispatcher(defaultUrl).forward(request, response);
	}
}