package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import java.util.ArrayList;
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
public class StockDay {

	private final String DIR_ROOT = "stock";
	private final String QUERY_ROOT = "stockDay.query";

	@Resource(name = "dbConn")
	private DbConn dbConn;
	@Resource(name = "dbConn2")
	private DbConn dbConn2;

	// 일출고량
	@RequestMapping(value = "/day")
	public ModelAndView dayList(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("brand", Common.defaultValue(map.get(Common.paramIsArray("brand", request)), "55"));
		map.put("display", Common.defaultValue(map.get(Common.paramIsArray("display", request)), "1"));
		map.put("marketMinus", "");
		List<Map<String, String>> checkList = dbConn2.recordSet(QUERY_ROOT + ".checkDayCount", map);
		if (!Common.isNullOrEmpty(checkList.get(0).get("marketMinus")) && !checkList.get(0).get("marketMinus").equals("-")) {
			map.put("marketMinus", checkList.get(0).get("marketMinus"));
		}
		map.put("divide", checkList.get(0).get("divide"));

		List<Map<String, String>> list = dbConn2.recordSet(QUERY_ROOT + ".stockDay", map);
		String productIdArray = "0";
		for (int i = 0; i < list.size(); i++) {
			String productId = Common.defaultValue(String.valueOf(list.get(i).get("product_id")), "0");
			productIdArray += "," + productId;
		}
		map.put("productIdArray", productIdArray);

		mv.addObject("confirmDay", dbConn2.recordSet(QUERY_ROOT + ".confirmDay", map));
		mv.addObject("checkDayCount", checkList);
		mv.addObject("stockDayExcept", dbConn2.recordSet(QUERY_ROOT + ".stockDayExcept", map));
		mv.addObject("productGubun", dbConn2.recordSet(QUERY_ROOT + ".productGubun", map));
		mv.addObject("codeList", dbConn.recordSet("stock.query.codeList", map));
		mv.addObject("list", list);
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("stock/day.pq");
		return mv;
	}

	// 기본계산일 및 계산제외 등록
	@RequestMapping(value = "/day_insertDB.run", method = {RequestMethod.POST})
	@Transactional
	public ModelAndView dayInsertDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		String[] selectField = new String[]{"productId", "thisDay"};
		List<Map<String, String>> productIdList = Common.paramToList(selectField, request.getParameterMap());

		for (int i = 0; i < productIdList.size(); i++) {
			productIdList.get(i).put("thisDay", String.valueOf(Math.round(Double.parseDouble(Common.defaultValue(productIdList.get(i).get("thisDay"), "0")) * 10) / 10.0));
		}

		Map<String, Object> totalMap = new HashMap<>();
		totalMap.put("productIdList", productIdList);
		totalMap.put("param", map);
		dbConn2.recordSet(QUERY_ROOT + ".dayInsert", totalMap);

		mv.setViewName("redirect:/views/stock/day?brand=" + map.get("brand"));
		return mv;
	}

	// 일출고량
	@RequestMapping(value = "/day_graph")
	public ModelAndView dayGraph(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("display", "1, 0");

		mv.addObject("checkDayCount", dbConn2.recordSet(QUERY_ROOT + ".checkDayCount", map));
		mv.addObject("stockDayExcept", dbConn2.recordSet(QUERY_ROOT + ".stockDayExcept", map));
		mv.addObject("list", dbConn2.recordSet(QUERY_ROOT + ".stockDay", map));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("stock/day_graph.pq");
		return mv;
	}

	// 일 출고량 계산일을 변경
	@RequestMapping(value = "/day_graph_insertDB.run")
	@Transactional
	public ModelAndView dayGraphInsertDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		List chkValue = new ArrayList<>();

		for (int i = 0; i < map.get("chkValue_product_id").split(",").length; i++) {
			Map<String, String> thisMap = new HashMap<>();
			thisMap.put("chkValue_product_id", map.get("chkValue_product_id").split(",")[i]);
			thisMap.put("chkValue_day", map.get("chkValue_day").split(",")[i]);
			thisMap.put("chkValue_power", map.get("chkValue_power").split(",")[i]);
			chkValue.add(thisMap);
		}

		Map<String, Object> totalMap = new HashMap<>();
		totalMap.put("chkValue", chkValue);
		totalMap.put("param", map);
		dbConn2.recordSet(QUERY_ROOT + ".dayGraphInsert", totalMap);

		mv.setViewName("redirect:/views/stock/day?brand=" + map.get("brand"));
		return mv;
	}

	// 기본계산일 및 계산제외 리스트
	@RequestMapping(value = "/day_count")
	public ModelAndView dayCountList(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		mv.addObject("codeList", dbConn.recordSet("stock.query.codeList", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("stock/day_count.pq");
		return mv;
	}

	//월별 재고금액 - 리스트
	@RequestMapping(value = "/day_count.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> dayCount(HttpServletRequest request) {
		List<Map> result = dbConn2.recordSet(QUERY_ROOT + ".dayCount", null);
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}


	// 기본계산일 및 계산제외 등록
	@RequestMapping(value = "/day_count_insertDB.run")
	@Transactional
	public ModelAndView dayCountListInsertDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("marketMinus", Common.defaultValue(map.get("marketMinus"), ""));
		String[] selectField = new String[]{"brand"};
		List<Map<String, String>> brand = Common.paramToList(selectField, request.getParameterMap());

		Map<String, Object> totalMap = new HashMap<>();
		totalMap.put("brandList", brand);
		totalMap.put("param", map);
		dbConn2.recordSet(QUERY_ROOT + ".dayCountInsert", totalMap);

		mv.setViewName("redirect:/views/stock/day_count");
		return mv;
	}


}