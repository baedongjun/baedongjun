package com.common.member;

import java.io.IOException;
import java.util.Collection;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class MemberSuccessHandler1 extends SimpleUrlAuthenticationSuccessHandler {
	private String defaultUrl = "/";

	public MemberSuccessHandler1 (String defaultUrl) {
		this.defaultUrl = defaultUrl;
	}

	public MemberSuccessHandler1 () {}

	@Resource
	MemberDAO5 memberDAO;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

		setDefaultTargetUrl(defaultUrl);

		// 실패회수 초기화
		memberDAO.resetFailure(authentication.getName());

		// 에러세션 지우기. 로그인 실패에도 세션이 생성.
		clearAuthenticationAttributes(request);

		// 이전 페이지 이동
		HttpSession savedRequest = request.getSession();
		if (savedRequest == null) {
			super.onAuthenticationSuccess(request, response, authentication);
		} else {
			savedRequest.setMaxInactiveInterval(60*60*24*30);
			Collection<String> headers = response.getHeaders(HttpHeaders.SET_COOKIE);
			boolean firstHeader = true;

			for(String header: headers){
				if(firstHeader){
					response.setHeader(HttpHeaders.SET_COOKIE, String.format("%s; Secure; %s", header, "SameSite=None"));
					firstHeader = false;
					continue;
				}
				response.addHeader(HttpHeaders.SET_COOKIE, String.format("%s; Secure; %s", header, "SameSite=None"));
			}

			String redirectTo = (String) savedRequest.getAttribute("redirectTo");
			if (redirectTo != null) {
				savedRequest.removeAttribute("redirectTo");
				getRedirectStrategy().sendRedirect(request, response, redirectTo);
			} else {
				getRedirectStrategy().sendRedirect(request, response, defaultUrl);
			}
		}
	}
}

