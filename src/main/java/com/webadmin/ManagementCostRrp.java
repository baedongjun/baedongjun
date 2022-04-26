package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/views/management")
public class ManagementCostRrp {

	private final String DIR_ROOT = "managementCostRrp";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;

	//원가, RRP 관리 - 화면
	@RequestMapping(value = "/costRrp_list")
	public ModelAndView list(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("gubun", Common.defaultValue(map.get("gubun"), "cost"));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("list", Common.nullToEmpty(dbConn.recordSet(QUERY_ROOT + ".list", map)));
		mv.setViewName("management/costRrp_list.tiles");
		return mv;
	}

	//원가, RRP 관리 - 등록 화면
	@RequestMapping(value = "/costRrp_input")
	public ModelAndView input(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("list", dbConn.recordSet(QUERY_ROOT + ".view", Common.paramToMap(request.getParameterMap())));
		mv.setViewName("management/costRrp_input.tiles");
		return mv;
	}

	//원가, RRP 관리 - 화면
	@RequestMapping(value = "/history.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> history(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("data", Common.nullToEmpty(dbConn.recordSet(QUERY_ROOT + ".history", Common.paramToMap(request.getParameterMap()))));

		return resultMap;
	}

}
