package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/views/coupon")
public class Coupon {

	private final String DIR_ROOT = "coupon";
	private final String QUERY_ROOT = DIR_ROOT + ".query";


	@Resource(name = "dbConn")
	private DbConn dbConn;


	@RequestMapping(value = "/alert_list")
	public ModelAndView alertList(ModelAndView mv, HttpServletRequest request) {
		mv.setViewName("coupon/alert_list.pq");
		return mv;
	}

	@RequestMapping(value = "/alert_list.run", produces = "application/json")
	@ResponseBody
	public Map<String, Object> alertList(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("data", dbConn.recordSet(QUERY_ROOT + ".alertList", null));

		return resultMap;
	}

	//알림설정 저장
	@RequestMapping(value = "/alert_insert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView authbrandInsert(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		dbConn.recordSet(QUERY_ROOT + ".alertInsert", map);
		mv.setViewName("coupon/alert_list.pq");
		return mv;
	}


}
