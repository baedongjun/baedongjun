package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.common.ExcelCon;
import com.common.member.MemberDTO4;
import java.io.IOException;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/views/crm")
public class Crm {

	private final String DIR_ROOT = "crm";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;
	@Resource(name = "dbConn2")
	private DbConn dbConn2;
	@Resource(name = "excelCon")
	private ExcelCon excelCon;

	//기타참고 - 베스트 100(상품별)
	@RequestMapping(value = "/product_best_100")
	public ModelAndView product_best_100(ModelAndView mv, HttpServletRequest request, Authentication authentication) throws Exception {
		LocalDate now = LocalDate.now();

		String brand = request.getParameter("brand");
		String yearV = request.getParameter("yearV");

		if (Common.isNullOrEmpty(yearV) || Common.isNullOrEmpty(brand)) {
			mv.setViewName("redirect:/views/crm/product_best_100?yearV=" + now.format(DateTimeFormatter.ofPattern("yyyy")) + "&brand=55&divide=order&marketMinus2=16&marketMinus2=31&marketMinus2=25&marketMinus2=20&marketMinus2=24&marketMinus2=32&marketMinus2=34&marketMinus2=33&marketMinus2=21&marketMinus2=42&marketMinus2=40&marketMinus2=47&marketMinus2=49");
			return mv;
		}

		List<String> marketMinusList = new ArrayList<>();
		marketMinusList.add(!Common.isNullOrEmpty(request.getParameter(Common.paramIsArray("marketMinus", request))) ? String.join(",", request.getParameterValues(Common.paramIsArray("marketMinus", request))) : null);
		marketMinusList.add(!Common.isNullOrEmpty(request.getParameter(Common.paramIsArray("marketMinus2", request))) ? String.join(",", request.getParameterValues(Common.paramIsArray("marketMinus2", request))) : null);
		marketMinusList.removeAll(Collections.singleton(null));
		String marketMinus = !Common.isNullOrEmpty(marketMinusList) ? String.join(",", marketMinusList) : null;

		Map<String, String> map = new HashMap<>();
		map.put("brand", brand);
		map.put("marketMinus", marketMinus);
		map.put("divide", request.getParameter("divide"));
		map.put("yearV", request.getParameter("yearV"));
		map.put("userBrand", ((MemberDTO4) authentication.getPrincipal()).getUser_brand());

		mv.addObject("data", dbConn2.recordSet(QUERY_ROOT + ".product_best_100", map));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("yearRange", dbConn2.recordSet(QUERY_ROOT + ".yearRange"));
		mv.addObject("mdBrandStock", dbConn.recordSet(QUERY_ROOT + ".mdBrandStock"));
		mv.addObject("yearV", yearV);
		mv.addObject("nowYear", now.format(DateTimeFormatter.ofPattern("yyyy")));
		mv.addObject("nowMonth", now.format(DateTimeFormatter.ofPattern("M")));
		mv.addObject("nextMonth", now.plusMonths(1).format(DateTimeFormatter.ofPattern("M")));

		mv.setViewName("crm/product_best_100.pq");
		return mv;
	}

	//기타참고 - 베스트 100(그룹별)
	@RequestMapping(value = "/group_best_100")
	public ModelAndView group_best_100(ModelAndView mv, HttpServletRequest request, Authentication authentication) throws Exception {
		LocalDate now = LocalDate.now();
		String brand = request.getParameter("brand");
		String yearV = request.getParameter("yearV");

		if (Common.isNullOrEmpty(yearV) || Common.isNullOrEmpty(brand)) {
			mv.setViewName("redirect:/views/crm/group_best_100?yearV=" + now.format(DateTimeFormatter.ofPattern("yyyy")) + "&brand=55&divide=order&marketMinus2=16&marketMinus2=31&marketMinus2=25&marketMinus2=20&marketMinus2=24&marketMinus2=32&marketMinus2=34&marketMinus2=33&marketMinus2=21&marketMinus2=42&marketMinus2=40&marketMinus2=47&marketMinus2=49");
			return mv;
		}

		Map<String, String> map = new HashMap<>();

		List<String> marketMinusList = new ArrayList<>();
		marketMinusList.add(!Common.isNullOrEmpty(request.getParameter(Common.paramIsArray("marketMinus", request))) ? String.join(",", request.getParameterValues(Common.paramIsArray("marketMinus", request))) : null);
		marketMinusList.add(!Common.isNullOrEmpty(request.getParameter(Common.paramIsArray("marketMinus2", request))) ? String.join(",", request.getParameterValues(Common.paramIsArray("marketMinus2", request))) : null);
		marketMinusList.removeAll(Collections.singleton(null));
		String marketMinus = !Common.isNullOrEmpty(marketMinusList) ? String.join(",", marketMinusList) : null;

		map.put("brand", brand);
		map.put("marketMinus", marketMinus);
		map.put("divide", request.getParameter("divide"));
		map.put("yearV", request.getParameter("yearV"));
		map.put("userBrand", ((MemberDTO4) authentication.getPrincipal()).getUser_brand());

		mv.addObject("data", dbConn2.recordSet(QUERY_ROOT + ".group_best_100", map));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("yearRange", dbConn2.recordSet(QUERY_ROOT + ".yearRange"));
		mv.addObject("mdBrandStock", dbConn.recordSet(QUERY_ROOT + ".mdBrandStock"));
		mv.addObject("yearV", yearV);
		mv.addObject("nowYear", now.format(DateTimeFormatter.ofPattern("yyyy")));
		mv.addObject("nowMonth", now.format(DateTimeFormatter.ofPattern("M")));
		mv.addObject("nextMonth", now.plusMonths(1).format(DateTimeFormatter.ofPattern("M")));

		mv.setViewName("crm/group_best_100.pq");
		return mv;
	}

	//기타참고 - 베스트 100(그룹별) 그래프
	@RequestMapping(value = "/group_best_100_graph")
	public ModelAndView group_best_100_detail(ModelAndView mv, HttpServletRequest request, Authentication authentication) throws Exception {
		LocalDate now = LocalDate.now();

		List<String> marketMinusList = new ArrayList<>();
		marketMinusList.add(!Common.isNullOrEmpty(request.getParameter(Common.paramIsArray("marketMinus", request))) ? String.join(",", request.getParameterValues(Common.paramIsArray("marketMinus", request))) : null);
		marketMinusList.add(!Common.isNullOrEmpty(request.getParameter(Common.paramIsArray("marketMinus2", request))) ? String.join(",", request.getParameterValues(Common.paramIsArray("marketMinus2", request))) : null);
		marketMinusList.removeAll(Collections.singleton(null));
		String marketMinus = !Common.isNullOrEmpty(marketMinusList) ? String.join(",", marketMinusList) : null;

		Map<String, String> map = new HashMap<>();
		map.put("brand", request.getParameter("brand"));
		map.put("groupname", request.getParameter("groupname"));
		map.put("marketMinus", marketMinus);
		map.put("divide", request.getParameter("divide"));
		map.put("yearV", request.getParameter("yearV"));

		mv.addObject("data", dbConn2.recordSet(QUERY_ROOT + ".group_best_100_detail", map));
		mv.addObject("yearV", request.getParameter("yearV"));
		mv.addObject("nowYear", now.format(DateTimeFormatter.ofPattern("yyyy")));
		mv.addObject("nowMonth", now.format(DateTimeFormatter.ofPattern("M")));
		mv.addObject("nextMonth", now.plusMonths(1).format(DateTimeFormatter.ofPattern("M")));
		mv.addObject("mdBrandStock", dbConn.recordSet(QUERY_ROOT + ".mdBrandStock"));
		mv.addObject("brand", request.getParameter("brand"));
		mv.addObject("groupname", request.getParameter("groupname"));
		mv.addObject("marketMinus", marketMinus);
		mv.addObject("divide", request.getParameter("divide"));

		mv.setViewName("crm/group_best_100_graph.pq");
		return mv;
	}

	// 방문자 통계 - 구매율
	@RequestMapping(value = "/month_visited_new")
	public ModelAndView month_visited_new(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));

		Map<String, String> map = new HashMap<>();
		map.put("yearV", Optional.ofNullable(request.getParameter("yearV")).orElse(Common.nowDate().substring(0, 4)));
		map.put("monthV", Optional.ofNullable(request.getParameter("monthV")).orElse(Common.nowDate().substring(5, 7)));
		map.put("category_site_id", !Common.isNullOrEmpty(request.getParameter(Common.paramIsArray("category_site_id", request))) ? String.join(",", request.getParameterValues(Common.paramIsArray("category_site_id", request))) : "55");
		map.put("userBrand", ((MemberDTO4) authentication.getPrincipal()).getUser_brand().replaceAll("\\]\\[", ",").replaceAll("\\[", "").replaceAll("\\]", ""));

		mv.addObject("result", dbConn.recordSet(QUERY_ROOT + ".month_visited_new", map));

		mv.setViewName("crm/month_visited_new.tiles");
		return mv;
	}

	// 방문자 통계 - 장바구니 수량(일)
	@RequestMapping(value = "/cart_sales")
	public ModelAndView cart_sales(ModelAndView mv,HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		map.put("yearV", Common.defaultValue(map.get("yearV"), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))));
		map.put("monthV", Common.defaultValue(map.get("monthV"), LocalDate.now().format(DateTimeFormatter.ofPattern("MM"))));
		LocalDate localDate = LocalDate.parse(map.get("yearV")+"-"+ Common.selectZero(Integer.parseInt(map.get("monthV")), "00")+"-01").with(TemporalAdjusters.lastDayOfMonth());

		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("lastday", localDate.getDayOfMonth());
		mv.setViewName("crm/cart_sales.pq");
		return mv;
	}

	// 방문자 통계 - 장바구니 수량(일)
	@RequestMapping(value = "/cart_sales.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> cart_sales(HttpServletRequest request, Authentication authentication){
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("category_site_id", !Common.isNullOrEmpty(request.getParameter(Common.paramIsArray("category_site_id", request))) ? String.join(",", request.getParameterValues(Common.paramIsArray("category_site_id", request))) : "55");
		map.put("userBrand", ((MemberDTO4) authentication.getPrincipal()).getUser_brand().replaceAll("\\]\\[", ",").replaceAll("\\[", "").replaceAll("\\]", ""));
		map.put("goods_name", request.getParameter("search_goods_name"));
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

		resultMap.put("data", Common.nullToEmpty(dbConn.recordSet(QUERY_ROOT + ".cart_sales", map)));
		return resultMap;
	}

	// 방문자 통계 - 장바구니 수량(일) > 그래프
	@RequestMapping(value = "/cart_sales_graph")
	public ModelAndView cart_sales_graph(ModelAndView mv, HttpServletRequest request, Authentication authentication) throws Exception {
		String yearV = request.getParameter("yearV");
		String monthV = request.getParameter("monthV");

		Map<String, String> map = new HashMap<>();
		map.put("yearV", yearV);
		map.put("monthV", monthV);
		map.put("category_site_id", !Common.isNullOrEmpty(request.getParameter(Common.paramIsArray("category_site_id", request))) ? String.join(",", request.getParameterValues(Common.paramIsArray("category_site_id", request))) : "55");
		map.put("wdate", yearV + "-" + monthV + "-1");
		map.put("wdateMonth", yearV + monthV);
		map.put("nowMonth", Common.nowDate().substring(0, 4) + Common.nowDate().substring(5, 7));
		map.put("nowDate", Common.nowDate().substring(8, 10));
		map.put("groupname", request.getParameter("groupname"));
		map.put("userBrand", ((MemberDTO4) authentication.getPrincipal()).getUser_brand().replaceAll("\\]\\[", ",").replaceAll("\\[", "").replaceAll("\\]", ""));
		map.put("goods_name", request.getParameter("search_goods_name"));
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

		mv.addObject("dayValue", request.getParameter("query_day"));
		mv.addObject("lastday",  localDate.getDayOfMonth());
		mv.addObject("resultCart", dbConn.recordSet(QUERY_ROOT + ".cart_sales_graph_cart", map));
		mv.addObject("resultBuy", dbConn.recordSet(QUERY_ROOT + ".cart_sales_graph_buy", map));

		mv.setViewName("crm/cart_sales_graph.pq");
		return mv;
	}



	// 자사몰 - 기타통계 - 구매통계(최근1년) - 구매전환/재구매
	@RequestMapping(value = "/join_buy_count")
	public ModelAndView join_buy_count(ModelAndView mv, HttpServletRequest request) throws Exception {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("yearV", Common.defaultValue(map.get("yearV"), Common.nowDate().substring(0, 4)));
		List<List<Map>> result = dbConn.recordSet(QUERY_ROOT + ".joinCount", map);

		mv.addObject("joinCount", result.get(0));
		mv.addObject("buyCount", result.get(1));
		mv.addObject("joinBuyCount", result.get(2));
		mv.addObject("reBuyCount", result.get(3));
		mv.addObject("reBuyingCount", result.get(4));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("crm/join_buy_count.tiles");
		return mv;
	}

	// 자사몰 - 기타통계 - 구매통계(최근1년) - 구매횟수별
	@RequestMapping(value = "/buy_count_chart")
	public ModelAndView buy_count_chart(ModelAndView mv, HttpServletRequest request) throws Exception {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("yearV", Common.defaultValue(map.get("yearV"), Common.nowDate().substring(0, 4)));
		mv.addObject("buy_count_chart", dbConn.recordSet(QUERY_ROOT + ".buy_count_chart", map));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("crm/buy_count_chart.tiles");
		return mv;
	}

	@RequestMapping(value = "/month_visited_new_ExcelDown.run")
	public void monthVisitedDownExcel(HttpServletResponse response, HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = new HashMap<>();

		map.put("yearV", request.getParameter("yearV"));
		map.put("monthV", request.getParameter("monthV"));
		map.put("category_site_id", !Common.isNullOrEmpty(request.getParameter(Common.paramIsArray("category_site_id", request))) ? String.join(",", request.getParameterValues(Common.paramIsArray("category_site_id", request))) : null);
		map.put("userBrand", ((MemberDTO4) authentication.getPrincipal()).getUser_brand().replaceAll("\\]\\[", ",").replaceAll("\\[", "").replaceAll("\\]", ""));

		List<String> dbField = Arrays.asList("goods_name", "siteCnt", "siteReadNum", "mobileCnt", "mobileReadNum", "appCnt", "appReadNum", "buyCnt", "perSite", "perView", "totalCnt");
		List<String> cellName = Arrays.asList("상품", "방문자수(사이트)", "페이지뷰(사이트)", "방문자수(모바일)", "페이지뷰(모바일)", "방문자수(APP)", "페이지뷰(APP)", "구매수", "방문자수(구매율)", "페이지뷰(구매율)", "방문자수(사이트:모바일:APP)");

		try {
			excelCon.downExcelFile(response, dbConn, QUERY_ROOT + ".month_visited_new_excel", map, dbField, cellName);
		} catch (IOException e) {
		}
	}

	@RequestMapping(value = "/productExcelDown.run", produces = "application/json")
	public void prdocutDownExcel(HttpServletResponse response, HttpServletRequest request, Authentication authentication) throws IOException {
		Map<String, String> map = new HashMap<>();

		List<String> marketMinusList = new ArrayList<>();
		marketMinusList.add(!Common.isNullOrEmpty(request.getParameter(Common.paramIsArray("marketMinus", request))) ? String.join(",", request.getParameterValues(Common.paramIsArray("marketMinus", request))) : null);
		marketMinusList.add(!Common.isNullOrEmpty(request.getParameter(Common.paramIsArray("marketMinus2", request))) ? String.join(",", request.getParameterValues(Common.paramIsArray("marketMinus2", request))) : null);
		marketMinusList.removeAll(Collections.singleton(null));
		String marketMinus = !Common.isNullOrEmpty(marketMinusList) ? String.join(",", marketMinusList) : null;

		String nowYear = Common.nowDate().substring(0, 4);

		int nowMonth = 0;
		if (request.getParameter("yearV").equals(nowYear)) {
			nowMonth = Integer.parseInt(Common.nowDate().substring(5, 7));
		} else {
			nowMonth = 13;
		}

		map.put("brand", request.getParameter("brand"));
		map.put("marketMinus", marketMinus);
		map.put("divide", request.getParameter("divide"));
		map.put("yearV", request.getParameter("yearV"));
		map.put("nowYear", nowYear);
		map.put("userBrand", ((MemberDTO4) authentication.getPrincipal()).getUser_brand());

		List<String> dbField = new ArrayList<>();
		List<String> cellName = new ArrayList<>();

		String mColumn = "";
		String sColumn = "";
		dbField.addAll(Arrays.asList("brand", "groupname", "codename2", "name", "rrp"));
		for (int i = 1; i <= nowMonth - 1; i++) {
			mColumn = "s" + i;

			dbField.add(mColumn);
		}
		if (request.getParameter("yearV").equals(nowYear)) {
			dbField.add("s13");
			dbField.add("s14");
		}
		dbField.add("sTotal");
		for (int i = 1; i <= nowMonth - 1; i++) {
			sColumn = "m" + i;
			dbField.add(sColumn);
		}

		if (request.getParameter("yearV").equals(nowYear)) {
			dbField.add("m13");
			dbField.add("m14");
		}
		dbField.add("mTotal");

		String mCell = "";
		String sCell = "";
		cellName.addAll(Arrays.asList("브랜드", "그룹", "코드", "상품명", "RRP"));
		for (int i = 1; i <= nowMonth - 1; i++) {
			mCell = i + "월(금액)";
			cellName.add(mCell);
		}

		if (request.getParameter("yearV").equals(nowYear)) {
			cellName.add(nowMonth + "월(금액)");
			cellName.add((nowMonth + 1 == 13 ? "내년1" : String.valueOf(nowMonth + 1)) + "월(금액)");
		}

		cellName.add("합계(금액");
		for (int i = 1; i <= nowMonth - 1; i++) {
			sCell = i + "월(수량)";
			cellName.add(sCell);
		}

		if (request.getParameter("yearV").equals(nowYear)) {
			cellName.add(nowMonth + "월(수량)");
			cellName.add((nowMonth + 1 == 13 ? "내년1" : String.valueOf(nowMonth + 1)) + "월(수량)");
		}

		cellName.add("합계(수량)");

		try {
			excelCon.downExcelFile(response, dbConn2, QUERY_ROOT + ".product_best_100_excel", map, dbField, cellName);
		} catch (Exception e) {
		}
	}

	@RequestMapping(value = "/groupExcelDown.run", produces = "application/json")
	public void groupDownExcel(HttpServletResponse response, HttpServletRequest request, Authentication authentication) throws IOException {
		Map<String, String> map = new HashMap<>();

		List<String> marketMinusList = new ArrayList<>();
		marketMinusList.add(!Common.isNullOrEmpty(request.getParameter(Common.paramIsArray("marketMinus", request))) ? String.join(",", request.getParameterValues(Common.paramIsArray("marketMinus", request))) : null);
		marketMinusList.add(!Common.isNullOrEmpty(request.getParameter(Common.paramIsArray("marketMinus2", request))) ? String.join(",", request.getParameterValues(Common.paramIsArray("marketMinus2", request))) : null);
		marketMinusList.removeAll(Collections.singleton(null));
		String marketMinus = !Common.isNullOrEmpty(marketMinusList) ? String.join(",", marketMinusList) : null;

		String nowYear = Common.nowDate().substring(0, 4);

		int nowMonth = 0;
		if (request.getParameter("yearV").equals(nowYear)) {
			nowMonth = Integer.parseInt(Common.nowDate().substring(5, 7));
		} else {
			nowMonth = 13;
		}

		map.put("brand", request.getParameter("brand"));
		map.put("marketMinus", marketMinus);
		map.put("divide", request.getParameter("divide"));
		map.put("yearV", request.getParameter("yearV"));
		map.put("nowYear", nowYear);
		map.put("userBrand", ((MemberDTO4) authentication.getPrincipal()).getUser_brand());

		List<String> dbField = new ArrayList<>();
		List<String> cellName = new ArrayList<>();

		String mColumn = "";
		String sColumn = "";
		dbField.addAll(Arrays.asList("brand", "groupname"));
		for (int i = 1; i <= nowMonth - 1; i++) {
			sColumn = "s" + i;
			dbField.add(sColumn);
		}
		if (request.getParameter("yearV").equals(nowYear)) {
			dbField.add("s13");
			dbField.add("s14");
		}
		dbField.add("sTotal");
		for (int i = 1; i <= nowMonth - 1; i++) {
			mColumn = "m" + i;
			dbField.add(mColumn);
		}

		if (request.getParameter("yearV").equals(nowYear)) {
			dbField.add("m13");
			dbField.add("m14");
		}
		dbField.add("mTotal");

		String mCell = "";
		String sCell = "";
		cellName.addAll(Arrays.asList("브랜드", "그룹"));
		for (int i = 1; i <= nowMonth - 1; i++) {
			mCell = i + "월(금액)";
			cellName.add(mCell);
		}

		if (request.getParameter("yearV").equals(nowYear)) {
			cellName.add(nowMonth + "월(금액)");
			cellName.add((nowMonth + 1 == 13 ? "내년1" : String.valueOf(nowMonth + 1)) + "월(금액)");
		}
		cellName.add("합계(금액");

		for (int i = 1; i <= nowMonth - 1; i++) {
			sCell = i + "월(수량)";
			cellName.add(sCell);
		}

		if (request.getParameter("yearV").equals(nowYear)) {
			cellName.add(nowMonth + "월(수량)");
			cellName.add((nowMonth + 1 == 13 ? "내년1" : String.valueOf(nowMonth + 1)) + "월(수량)");
		}

		cellName.add("합계(수량)");

		try {
			excelCon.downExcelFile(response, dbConn2, QUERY_ROOT + ".group_best_100_excel", map, dbField, cellName);
		} catch (Exception e) {
		}
	}

	@RequestMapping(value = "/groupDetailExcelDown.run", produces = "application/json")
	public void groupDetailDownExcel(HttpServletResponse response, HttpServletRequest request, Authentication authentication) throws IOException {
		Map<String, String> map = new HashMap<>();

		List<String> marketMinusList = new ArrayList<>();
		marketMinusList.add(!Common.isNullOrEmpty(request.getParameter(Common.paramIsArray("marketMinus", request))) ? String.join(",", request.getParameterValues(Common.paramIsArray("marketMinus", request))) : null);
		marketMinusList.add(!Common.isNullOrEmpty(request.getParameter(Common.paramIsArray("marketMinus2", request))) ? String.join(",", request.getParameterValues(Common.paramIsArray("marketMinus2", request))) : null);
		marketMinusList.removeAll(Collections.singleton(null));
		String marketMinus = !Common.isNullOrEmpty(marketMinusList) ? String.join(",", marketMinusList) : null;

		String nowYear = Common.nowDate().substring(0, 4);

		int nowMonth = 0;
		if (request.getParameter("yearV").equals(nowYear)) {
			nowMonth = Integer.parseInt(Common.nowDate().substring(5, 7));
		} else {
			nowMonth = 13;
		}

		map.put("brand", request.getParameter("brand"));
		map.put("marketMinus", marketMinus);
		map.put("divide", request.getParameter("divide"));
		map.put("yearV", request.getParameter("yearV"));
		map.put("nowYear", nowYear);
		map.put("groupname", URLDecoder.decode(request.getParameter("groupnameParam"), "UTF-8"));

		List<String> dbField = new ArrayList<>();
		List<String> cellName = new ArrayList<>();

		String mColumn = "";
		String sColumn = "";
		dbField.addAll(Arrays.asList("codename2", "name", "rrp"));
		for (int i = 1; i <= nowMonth - 1; i++) {
			sColumn = "s" + i;
			dbField.add(sColumn);
		}
		if (request.getParameter("yearV").equals(nowYear)) {
			dbField.add("s13");
			dbField.add("s14");
		}
		dbField.add("sTotal");
		for (int i = 1; i <= nowMonth - 1; i++) {

			mColumn = "m" + i;
			dbField.add(mColumn);
		}

		if (request.getParameter("yearV").equals(nowYear)) {
			dbField.add("m13");
			dbField.add("m14");
		}
		dbField.add("mTotal");

		//셀
		String mCell = "";
		String sCell = "";
		cellName.addAll(Arrays.asList("코드", "상품명", "rrp"));
		for (int i = 1; i <= nowMonth - 1; i++) {
			mCell = i + "월(금액)";
			cellName.add(mCell);
		}

		if (request.getParameter("yearV").equals(nowYear)) {
			cellName.add(nowMonth + "월(금액)");
			cellName.add((nowMonth + 1 == 13 ? "내년1" : String.valueOf(nowMonth + 1)) + "월(금액)");
		}

		cellName.add("합계(금액");
		for (int i = 1; i <= nowMonth - 1; i++) {
			sCell = i + "월(수량)";
			cellName.add(sCell);
		}

		if (request.getParameter("yearV").equals(nowYear)) {
			cellName.add(nowMonth + "월(수량)");
			cellName.add((nowMonth + 1 == 13 ? "내년1" : String.valueOf(nowMonth + 1)) + "월(수량)");
		}

		cellName.add("합계(수량)");

		try {
			excelCon.downExcelFile(response, dbConn2, QUERY_ROOT + ".group_best_100_detail_excel", map, dbField, cellName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	//기타참고>브랜드별 통계>한계이익(월별) - 화면
	@RequestMapping(value = "/brand_limit_money")
	public ModelAndView brand_limit_money(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("yearV", Common.defaultValue(map.get("yearV"), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))));
		map.put("market", Common.defaultValue(map.get("market"), "1"));
		mv.addObject("codeGroupList", dbConn.recordSet(QUERY_ROOT + ".codeGroupList", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("crm/brand_limit_money.pq");
		return mv;
	}

	//기타참고>브랜드별 통계>한계이익(월별) - 리스트
	@RequestMapping(value = "/brand_limit_money.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> brand_limit_money(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		String tbGubun = "_"+request.getParameter("yearV");
		String procNm = "";
		if(request.getParameter("gubun").equals("1")){
			procNm = "proc_brand_limit_money_category"+tbGubun;
		}else{
			procNm = "proc_brand_limit_money_product"+tbGubun;
		}
		map.put("procNm", procNm);
		map.put("codeGroup", map.get(Common.paramIsArray("codeGroup", request)));
		map.put("market", map.get(Common.paramIsArray("market", request)));

		List<Map> result = dbConn2.recordSet(QUERY_ROOT + ".brand_limit_money", map);
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}


	//기타참고>브랜드별 통계>실판매가(월별) - 화면
	@RequestMapping(value = "/brand_real_money")
	public ModelAndView brand_real_money(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("yearV", Common.defaultValue(map.get("yearV"), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))));
		map.put("market", Common.defaultValue(map.get("market"), "1"));
		mv.addObject("codeGroupList", dbConn.recordSet(QUERY_ROOT + ".codeGroupList", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("crm/brand_real_money.pq");
		return mv;
	}

	//기타참고>브랜드별 통계>실판매가(월별) - 리스트
	@RequestMapping(value = "/brand_real_money.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> brand_real_money(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		String tbGubun = "_"+request.getParameter("yearV");
		String procNm = "";
		if(request.getParameter("gubun").equals("1")){
			procNm = "proc_brand_real_money_category"+tbGubun;
		}else{
			procNm = "proc_brand_real_money_product"+tbGubun;
		}
		map.put("procNm", procNm);
		map.put("codeGroup", map.get(Common.paramIsArray("codeGroup", request)));
		map.put("market", map.get(Common.paramIsArray("market", request)));

		List<Map> result = dbConn2.recordSet(QUERY_ROOT + ".brand_real_money", map);
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}
}
