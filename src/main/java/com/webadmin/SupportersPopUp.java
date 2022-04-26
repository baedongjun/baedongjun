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
@RequestMapping("/views/supporters/popup")
public class SupportersPopUp {
	private final String DIR_ROOT = "supportersPopUp";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;

	@RequestMapping(value = "/popup_list")
	public ModelAndView list(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("supporters/popup/popup_list.pq");
		return mv;
	}

	@RequestMapping(value = "popup_list.run")
	@ResponseBody
	public Map<String, Object> list(HttpServletRequest request, HttpServletResponse response) {
		Map<String, String> map = new HashMap<>();
		List<String> searchList = new ArrayList<>();

		searchList.add(dbConn.makeSearchSql("title", request.getParameterValues("title"), "like"));
		searchList.add(dbConn.makeSearchSqlRange("convert(varchar(10),wdate,126)", request.getParameter("sdate"), request.getParameter("edate")));
		searchList.removeAll(Collections.singleton(null));

		map.put("sqlItem", String.join(" and ", searchList));
		map.put("orderBy", "id desc");
		Common.PQmap(map, request);

		List<Map> list = new ArrayList<>();
		list = dbConn.recordSet(QUERY_ROOT + ".list", map);

		String iniId = "id";
		Map resultSub = Common.subQuery(list, iniId);

		List<Map> listSub = new ArrayList<>();
		listSub = dbConn.recordSet(QUERY_ROOT + ".listSub", resultSub);

		Map<String, Object> returnParam = new HashMap<>();
		Common.PQresultMap(returnParam, map.get("curPage"), list);
		returnParam.put("data", Common.nullToEmpty(Common.combineRecordSet(iniId, list, listSub)));

		return returnParam;
	}


	@RequestMapping(value = "/popup_input")
	public ModelAndView input(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		List<List<Map>> view = dbConn.recordSet(QUERY_ROOT + ".view", map);

		mv.addObject("popupView0", view.get(0));
		mv.addObject("popupView1", view.get(1));
		mv.setViewName("supporters/popup/popup_input.tiles");
		return mv;
	}

	@RequestMapping(value = "/popup_input.run", method = {RequestMethod.POST}, produces = "application/json")
	public String input(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		if (map.get("choice").equals("insert")) {
			List<Map<String, String>> id = dbConn.recordSet(QUERY_ROOT + ".insert", map);
			map.put("popupId", id.get(0).get("popupId"));
		} else {
			map.put("popupId", map.get("id"));
			dbConn.recordSet(QUERY_ROOT + ".update", map);
			dbConn.recordSet(QUERY_ROOT + ".delete", map);
		}

		String[] divide_supporters = map.get("divide_supporters").split(",");
		String[] target1 = map.get("target1").replace(" ", "").split(",");
		String[] target2 = map.get("target2").replace(" ", "").split(",");

		for (int i = 0; i < divide_supporters.length; i++) {
			if (target1[i].equals("y")) {
				map.put("divide_supporters", divide_supporters[i]);
				dbConn.recordSet(QUERY_ROOT + ".divide1", map);
			}

			if (target2[i].equals("y")) {
				map.put("divide_supporters", divide_supporters[i]);
				dbConn.recordSet(QUERY_ROOT + ".divide2", map);
			}
		}
		return "redirect:/views/supporters/popup/popup_list";
	}
}
