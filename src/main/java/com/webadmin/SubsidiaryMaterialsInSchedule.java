package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.member.MemberDTO4;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/views/subsidiary_materials")
public class SubsidiaryMaterialsInSchedule {

	private final String DIR_ROOT = "subsidiary_materialsInSchedule";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn2")
	private DbConn dbConn2;

	//입고예정 목록 - 화면
	@RequestMapping(value = "/in_schedule_list")
	public ModelAndView list(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("companyList", dbConn2.recordSet(QUERY_ROOT + ".companyList", Common.paramToMap(request.getParameterMap())));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));

		mv.setViewName("subsidiary_materials/in_schedule_list.pq");
		return mv;
	}

	//입고예정 목록 - 리스트
	@RequestMapping(value = "/in_schedule_list.run", method = {RequestMethod.POST}, produces = "application/json;")
	@ResponseBody
	public Map<String, Object> list(HttpServletRequest request, HttpServletResponse response) {
		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn2.makeSearchSql("brand", request.getParameterValues("brand"), "="));
		sqlItemList.add(dbConn2.makeSearchSql("company", request.getParameterValues("company"), "="));
		sqlItemList.add(dbConn2.makeSearchSql("work", request.getParameterValues("work"), "="));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("orderBy", "order_id desc");

		resultMap.put("data", Common.nullToEmpty(dbConn2.recordSet(QUERY_ROOT + ".list1", map)));

		return resultMap;
	}

	//입고예정 목록 - 등록
	@RequestMapping(value = "/in_schedule_list_insert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView insertDB(ModelAndView mv, HttpServletRequest request) {
		String[] selectField = new String[]{"delId"};
		List<Map<String, String>> listMap = Common.paramToList(selectField, request.getParameterMap());

		dbConn2.recordSet(QUERY_ROOT + ".insert1", listMap);
		mv.setViewName("redirect:/views/subsidiary_materials/in_schedule_list");
		return mv;
	}

	//입고예정 등록 - 화면
	@RequestMapping(value = "/in_schedule")
	public ModelAndView inSchedule(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn2.makeSearchSql("brand", request.getParameterValues("brand"), "="));
		sqlItemList.add(dbConn2.makeSearchSql("company", request.getParameterValues("company"), "="));
		sqlItemList.add(dbConn2.makeSearchSql("work", request.getParameterValues("work"), "="));
		sqlItemList.removeAll(Collections.singleton(null));

		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("orderBy", "id desc");

		List<Map> result = dbConn2.recordSet(QUERY_ROOT + ".list2", map);

		mv.addObject("companyList", dbConn2.recordSet(QUERY_ROOT + ".companyList", map));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("list", Common.nullToEmpty(result));
		mv.setViewName("subsidiary_materials/in_schedule.tiles");
		return mv;
	}

	//입고예정 등록 - 등록
	@RequestMapping(value = "/in_schedule_insert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView inScheduleInsertDB(ModelAndView mv, HttpServletRequest request,Authentication authentication) {
		List<Map> result = dbConn2.recordSet(QUERY_ROOT + ".maxOrderId", null);
		String[] selectField = new String[]{"brand", "name", "size","company_name","gu","pri","in_qty","in_date","work"};

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("list", Common.paramToList(selectField, request.getParameterMap()));
		paramMap.put("order_id", result.get(0).get("order_id").toString());
		paramMap.put("user_id", ((MemberDTO4) authentication.getPrincipal()).getUser_id());

		dbConn2.recordSet(QUERY_ROOT + ".insert2", paramMap);
		mv.setViewName("redirect:/views/subsidiary_materials/in_schedule_print?order_id=" + result.get(0).get("order_id").toString() + "&gu=1");
		return mv;
	}

	//입고예정 등록 - 화면
	@RequestMapping(value = "/in_schedule_print")
	public ModelAndView inSchedulePrint(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		mv.addObject("list", dbConn2.recordSet(QUERY_ROOT + ".printList1", map));
		mv.addObject("list2", Common.nullToEmpty(dbConn2.recordSet(QUERY_ROOT + ".printList2", map)));
		mv.addObject("returnParam", map);
		mv.setViewName("subsidiary_materials/in_schedule_print.tiles");
		return mv;
	}

}
