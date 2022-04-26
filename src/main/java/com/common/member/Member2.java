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

	// ȸ������
	@PostMapping("/insert.run")
	public String inputMember(MemberDTO4 memberDto) {
		memberServiceImpl.insertUser(memberDto);
		return "/login.bare";
	}

	// ȸ����������
	@PostMapping("/emploee/update.run")
	public String updateMember(MemberDTO4 memberDto) {
		memberServiceImpl.updateUser(memberDto);
		return "/emploee/view";
	}
/*
	// �� ���� ������
	@GetMapping("/emploee/view")
	public ModelAndView viewMember(ModelAndView mv, HttpServletRequest inputData) {
		mv.addObject("list", memberDAO.viewUser(Common.paramToMap(inputData.getParameterMap())));
		mv.setViewName("/emploee/view.tiles");
		return mv;
	}
*/
	// �α��� ������
	@GetMapping("/login")
	public ModelAndView loginPage(ModelAndView mv, HttpServletRequest request) {
		String referrer = request.getHeader("Referer");

		// �α��� �� ȸ������ �������� �����̷�Ʈ url���� ����
		if (referrer != null && !referrer.contains("login")) {
			request.getSession().setAttribute("redirectTo", referrer);
		}
		mv.addObject("ip", request.getRemoteAddr());
		mv.setViewName("/login.bare");
		return mv;
	}
}
