package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.member.MemberDTO4;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/views/emploee")
public class EmploeeInnovateHQ {

	private final String DIR_ROOT = "emploeeInnovateHQ";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;


	//각 점심목록
	@RequestMapping(value = "/innovate_HQ")
	public ModelAndView list(ModelAndView mv, HttpServletRequest request, HttpSession session) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		session.setAttribute("emploee_gubun", map.get("gubun"));
		map.put("gubun", session.getAttribute("emploee_gubun").toString());

		mv.addObject("listMap", dbConn.recordSet(QUERY_ROOT + ".list", map));
		mv.addObject("exceptMap", dbConn.recordSet(QUERY_ROOT + ".exceptList", map));
		mv.setViewName("emploee/innovate_HQ.tiles");
		return mv;
	}


	//못먹어요 처리
	@RequestMapping(value = "/innovate_except.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView exceptRun(ModelAndView mv, HttpServletRequest request, Authentication authentication, HttpSession session) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("user_id", ((MemberDTO4) authentication.getPrincipal()).get_Id());
		dbConn.recordSet(QUERY_ROOT + ".except", map);

		mv.setViewName("redirect:/views/emploee/innovate_HQ?gubun=" + session.getAttribute("emploee_gubun").toString());
		return mv;
	}
}
