package com.common.member;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/views")
public class Member2 {
	@Resource
	private MemberServiceImpl3 memberServiceImpl;
	@Resource
	private MemberDAO5 memberDAO;

	// 회원가입
	@PostMapping("/insert.run")
	public String inputMember(MemberDTO4 memberDto) {
		memberServiceImpl.insertUser(memberDto);
		return "/login.bare";
	}

	// 회원정보수정
	@PostMapping("/emploee/update.run")
	public String updateMember(MemberDTO4 memberDto) {
		memberServiceImpl.updateUser(memberDto);
		return "/emploee/view";
	}
/*
	// 내 정보 페이지
	@GetMapping("/emploee/view")
	public ModelAndView viewMember(ModelAndView mv, HttpServletRequest inputData) {
		mv.addObject("list", memberDAO.viewUser(Common.paramToMap(inputData.getParameterMap())));
		mv.setViewName("/emploee/view.tiles");
		return mv;
	}
*/
	// 로그인 페이지
	@GetMapping("/login")
	public ModelAndView loginPage(ModelAndView mv, HttpServletRequest request) {
		String referrer = request.getHeader("Referer");

		// 로그인 및 회원가입 페이지는 리다이렉트 url에서 제외
		if (referrer != null && !referrer.contains("login")) {
			request.getSession().setAttribute("redirectTo", referrer);
		}
		mv.addObject("ip", request.getRemoteAddr());
		mv.setViewName("/login.bare");
		return mv;
	}
}
