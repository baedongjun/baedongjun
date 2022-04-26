package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/views/statistics")
public class Statistics {

	private final String DIR_ROOT = "statistics";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name= "dbConn")
	private DbConn dbConn;


	//자사몰 전용>회원통계>회원탈퇴 추이
	@RequestMapping(value = "/member_withdrawal")
	public ModelAndView member_withdrawal(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		mv.addObject("member_withdrawal", dbConn.recordSet(QUERY_ROOT + ".member_withdrawal", map));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("statistics/member_withdrawal.tiles");
		return mv;
	}
}
