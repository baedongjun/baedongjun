package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/views/stock")
public class StockAddUser {
	private final String DIR_ROOT = "stockAddUser";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn2")
	private DbConn dbConn2;
	@Resource(name = "dbConn")
	private DbConn dbConn;

	//대량출고 - 요청목록- 추가인원요청서 화면(views/distribution/bulk)
	@RequestMapping(value = "/add_user_input")
	public ModelAndView add_user_input(ModelAndView mv,HttpServletRequest request) {

		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		List<List<Map<String, Object>>> list = dbConn2.recordSet(QUERY_ROOT + ".add_user_input", map);

		mv.addObject("reqDept", list.get(0));
		mv.addObject("writer", list.get(1));
		mv.addObject("idInfo", list.get(2));
		mv.addObject("workInfo", list.get(3));

		mv.setViewName("stock/add_user_input.tiles");
		return mv;
	}

	//대량출고 - 요청목록 - 추가인원요청서 등록,수정,취소
	@RequestMapping(value = "/insert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView insert(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		String choice = map.get("choice");

		String[] selectField = new String[]{"sdate", "n_man_days", "man_days", "man_pay"};
		List<Map<String, String>> list = Common.paramToList(selectField, request.getParameterMap());
		Map<String, Object> totalMap = new HashMap<>();
		totalMap.put("list", list);
		totalMap.put("ini", map);

		if ("insert".equals(choice)) {
			dbConn2.recordSet(QUERY_ROOT + ".insert", totalMap);
		} else if ("modify".equals(choice)) {
			dbConn2.recordSet(QUERY_ROOT + ".modify", totalMap);
		} else if ("cancel".equals(choice)) {
			dbConn2.recordSet(QUERY_ROOT + ".cancel", map.get("aid"));
		} else if ("complete".equals(choice)) {
			dbConn2.recordSet(QUERY_ROOT + ".complete", totalMap);
		}

		mv.setViewName("redirect:/views/distribution/bulk/input?id=" + map.get("bulk_id"));
		return mv;
	}

	//대량출고 - 추가인원 비용 통계 화면
	@RequestMapping(value = "/add_user_graph")
	public ModelAndView add_user_graph(ModelAndView mv,HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		map.put("com_root", Common.defaultValue(Common.paramIsArray("com_root", request), ""));
		map.put("yearV", Common.defaultValue(map.get("yearV"), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))));
		map.put("monthV", Common.defaultValue(map.get("monthV"), LocalDate.now().format(DateTimeFormatter.ofPattern("MM"))));
		LocalDate localDate = LocalDate.parse(map.get("yearV")+"-"+ Common.selectZero(Integer.parseInt(map.get("monthV")), "00")+"-01").with(TemporalAdjusters.lastDayOfMonth());

		mv.addObject("comRootMap", dbConn.recordSet(QUERY_ROOT + ".comRootMap", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("lastday", localDate.getDayOfMonth());
		mv.setViewName("stock/add_user_graph.pq");
		return mv;
	}

	// 대량출고 - 추가인원 비용 통계 - 리스트 json
	@RequestMapping(value = "/add_user_graph.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> add_user_graph(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		LocalDate localDate = LocalDate.parse(map.get("yearV")+"-"+ Common.selectZero(Integer.parseInt(map.get("monthV")), "00")+"-01").with(TemporalAdjusters.lastDayOfMonth());

		String query_day = "";
		for(int i = 0; i <= localDate.getDayOfMonth(); i++) {
			if(i == localDate.getDayOfMonth()){
				query_day = query_day + "["+i+"]";
			}else{
				query_day = query_day + "["+i+"],";
			}
		}
		map.put("query_day", query_day);
		Map resultMap = new HashMap();

		resultMap.put("data", Common.nullToEmpty(dbConn2.recordSet(QUERY_ROOT + ".add_user_graph", map)));

		return resultMap;
	}
}
