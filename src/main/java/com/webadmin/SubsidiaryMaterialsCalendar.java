package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
@RequestMapping("/views/subsidiary_materials")
public class SubsidiaryMaterialsCalendar {

	private final String DIR_ROOT = "subsidiary_materialsCalendar";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn2")
	private DbConn dbConn2;

	//스케줄 - 화면
	@RequestMapping(value = "/calendar")
	public ModelAndView calendar(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("companyList", dbConn2.recordSet(QUERY_ROOT + ".companyList", Common.paramToMap(request.getParameterMap())));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("subsidiary_materials/calendar.tiles");
		return mv;
	}

	//달력 조회
	@RequestMapping(value = "/calendar.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> getCalendar(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("data", Common.nullToEmpty(dbConn2.recordSet(QUERY_ROOT + ".viewList", Common.paramToMap(request.getParameterMap()))));

		return resultMap;
	}

	//스케줄 확인 목록 - 화면
	@RequestMapping(value = "/calendar_chk")
	public ModelAndView calendarChk(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("companyList", dbConn2.recordSet(QUERY_ROOT + ".companyList", Common.paramToMap(request.getParameterMap())));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("subsidiary_materials/calendar_chk.pq");
		return mv;
	}

	//스케줄 확인 목록 - 리스트
	@RequestMapping(value = "/calendar_chk.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> calendarChk(HttpServletRequest request, HttpServletResponse response) {
		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn2.makeSearchSql("name", request.getParameterValues("name"), "like"));
		sqlItemList.add(dbConn2.makeSearchSql("brand", request.getParameterValues("brand"), "="));
		sqlItemList.add(dbConn2.makeSearchSql("company", request.getParameterValues("company"), "="));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("orderBy", "id desc");
		Common.PQmap(map, request);

		result = dbConn2.recordSet(QUERY_ROOT + ".list", map);

		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}
}
