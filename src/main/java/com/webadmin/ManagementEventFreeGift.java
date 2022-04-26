package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.common.ExcelCon;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
@RequestMapping("/views/management")
public class ManagementEventFreeGift {

	private final String DIR_ROOT = "managementEventFreeGift";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;
	@Resource(name = "excelCon")
	private ExcelCon excelCon;

	//요청 진행 관리 - 화면
	@RequestMapping(value = "/event_free_gift_list")
	public ModelAndView list(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("management/event_free_gift_list.pq");
		return mv;
	}

	//무료체험 통계 - 화면
	@RequestMapping(value = "/event_free_gift_list.run", method = {RequestMethod.POST}, produces = "application/json;")
	@ResponseBody
	public Map<String, Object> list(HttpServletRequest request, HttpServletResponse response) {
		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn.makeSearchSql("A.market", new String[]{"20","44","42","51"}, "in"));
		sqlItemList.add(dbConn.makeSearchSql(" A.delivery", new String[]{"99"}, "not in"));
		sqlItemList.add(dbConn.makeSearchSql("B.cancel", new String[]{"n"}, "in"));
		sqlItemList.add(dbConn.makeSearchSqlRange("ready_wdate", Common.defaultValue(request.getParameter("wdate1"), Common.nowDate().substring(0, 8) + "01"), Common.defaultValue(request.getParameter("wdate2"), Common.nowDate())));
		sqlItemList.add(dbConn.makeSearchSql("A.market", request.getParameterValues(Common.paramIsArray("market", request)), "in"));
		sqlItemList.add(dbConn.makeSearchSql("C.category_site_id", request.getParameterValues(Common.paramIsArray("category_site_id", request)), "in"));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		Map resultMap = new HashMap();

		map.put("sqlItem2", String.join(" and ", sqlItemList));
		map.put("orderBy", "A.order_buy_ini_id desc, A.id asc");
		Common.PQmap(map, request);

		result = dbConn.recordSet(QUERY_ROOT + ".list", map);

		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}

	//무료체험 통계 - 엑셀다운로드
	@RequestMapping(value = "/excelDown.run")
	public void downExcel(HttpServletResponse response, HttpServletRequest request) {
		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn.makeSearchSql("A.market", new String[]{"20","44","42","51"},"in"));
		sqlItemList.add(dbConn.makeSearchSql("A.delivery",new String[]{"99"} ,"not in"));
		sqlItemList.add(dbConn.makeSearchSql("B.cancel", new String[]{"n"},"="));
		sqlItemList.add(dbConn.makeSearchSqlRange("ready_wdate", Common.defaultValue(request.getParameter("wdate1"), Common.nowDate().substring(0, 8) + "01"), Common.defaultValue(request.getParameter("wdate2"), Common.nowDate())));
		sqlItemList.add(dbConn.makeSearchSql("A.market", request.getParameterValues(Common.paramIsArray("market", request)), "in"));
		sqlItemList.add(dbConn.makeSearchSql("D.category_site_id", request.getParameterValues(Common.paramIsArray("category_site_id", request)), "in"));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("orderBy", "A.id desc, B.id asc");

		List<String> dbField = Arrays.asList("id", "userid", "username", "market", "category_name_id", "content", "codename2", "name", "rrp", "cost", "cnt", "wdate", "last_money", "sales_money", "baesong_money", "ready_wdate", "paymode");
		List<String> cellName = Arrays.asList("Serial No.", "아이디", "성명", "판매구분", "브랜드", "구매목록", "품목코드", "품목명", "RRP", "원가", "수량", "주문일", "총결제액", "상품결제액", "배송료", "발주일", "결제방법");
		try {
			excelCon.downExcelFile(response, dbConn,QUERY_ROOT + ".listExcelDown", map, dbField, cellName);
		} catch (IOException e) {
		}
	}
}
