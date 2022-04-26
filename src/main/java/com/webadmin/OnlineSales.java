package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.common.ExcelCon;
import com.common.member.MemberDTO4;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/views/online_sales")
public class OnlineSales {

	private final String DIR_ROOT = "online_sales";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;
	@Resource(name = "dbConn2")
	private DbConn dbConn2;
	@Resource(name = "excelCon")
	private ExcelCon excelCon;

	//자사몰 전용 > 업무 전용 > 업무 구분 관리
	@RequestMapping(value = "/work_auth_info")
	public ModelAndView workAuthInfo(ModelAndView mv) {
		mv.addObject("list", dbConn2.recordSet(QUERY_ROOT + ".authInfo", null));
		mv.addObject("adminList", dbConn.recordSet(QUERY_ROOT + ".adminList", null));
		mv.setViewName("online_sales/work_auth_info.tiles");
		return mv;
	}

	//자사몰 전용 > 업무 전용 > 업무 구분 관리 > 수정하기
	@RequestMapping(value = "/work_auth")
	public ModelAndView workAuth(ModelAndView mv) {
		mv.addObject("list", dbConn2.recordSet(QUERY_ROOT + ".authInfo", null));
		mv.addObject("adminList", dbConn.recordSet(QUERY_ROOT + ".adminList", null));
		mv.setViewName("online_sales/work_auth.tiles");
		return mv;
	}

	//자사몰 전용 > 업무 전용 > 업무 구분 관리 > 수정하기 insert
	@RequestMapping(value = "/work_auth_insert_DB.run", method = RequestMethod.POST)
	@Transactional
	@ResponseBody
	public ModelAndView workAuthInsertDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> param = Common.paramToMap(request.getParameterMap());
		Map<String, String[]> map = request.getParameterMap();
		List resultList = new ArrayList<>();
		for (int i = 1; i <= map.get("jobId").length; i++) {
			Map<String, String> thisMap = new HashMap<>();
			thisMap.put("jobId", map.get("jobId")[i - 1]);
			thisMap.put("mainAdmin", param.containsKey("mainAdmin" + i) ? param.get("mainAdmin" + i) : "");
			thisMap.put("subAdmin", param.containsKey("subAdmin" + i) ? param.get("subAdmin" + i) : "");
			resultList.add(thisMap);
		}

		Map<String, Object> totalMap = new HashMap<>();
		totalMap.put("param", param);
		totalMap.put("list", resultList);

		dbConn2.recordSet(QUERY_ROOT + ".workAuthInsert", totalMap);
		mv.setViewName("redirect:/views/online_sales/work_auth");
		return mv;
	}

	//자사몰 전용 > 업무 전용 > 자사몰 일간 업무
	@RequestMapping(value = "/work_schedule_period")
	public ModelAndView workSchedulePeriod(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		LocalDate now = LocalDate.now();
		map.put("sdate", Common.defaultValue(map.get("sdate"), now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
		map.put("edate", Common.defaultValue(map.get("edate"), now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));

		mv.addObject("sdate", map.get("sdate"));
		mv.addObject("edate", map.get("edate"));
		mv.addObject("day_len", ChronoUnit.DAYS.between(LocalDate.parse(map.get("sdate")), LocalDate.parse(map.get("edate"))));
		mv.addObject("adminList", dbConn.recordSet(QUERY_ROOT + ".adminList", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("online_sales/work_schedule_period.tiles");
		return mv;
	}

	//자사몰 전용 > 업무 전용 > 자사몰 일간 업무 > 수정
	@RequestMapping(value = "/work_view_period.run", method = RequestMethod.POST)
	@Transactional
	@ResponseBody
	public Map<String, Object> workViewPeriod(ModelAndView mv, HttpServletRequest request) {
		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn2.makeSearchSqlRange("sdate", request.getParameter("sdate"), request.getParameter("edate")));
		sqlItemList.add(dbConn2.makeSearchSql("admin_member_id", request.getParameterValues(Common.paramIsArray("admin_member_id", request)), "in"));
		sqlItemList.add(dbConn2.makeSearchSql("process", request.getParameterValues("process"), "="));
		sqlItemList.add(dbConn2.makeSearchSql("divide", new String[]{"daily"}, "="));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("orderBy", "sdate asc,process asc");

		result = dbConn2.recordSet(QUERY_ROOT + ".workViewPeriod", map);

		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}

	//자사몰 전용 > 업무 전용 > 자사몰 업무 등록
	@RequestMapping(value = "/work")
	public ModelAndView work(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("divide", Common.defaultValue(map.get("divide"), "daily"));
		map.put("id", Common.defaultValue(map.get("id"), ""));
		map.put("getId", ((MemberDTO4) authentication.getPrincipal()).get_Id());

		List<List<Map>> result = dbConn2.recordSet(QUERY_ROOT + ".workInput", map);
		for (int i = 0; i < result.get(0).size(); i++) {
			result.get(0).get(i).put("result_content", (result.get(0).get(i).get("result_content")).toString().replaceAll("(\r\n|\r|\n|\n\r)", "<br>"));
		}
		mv.addObject("pageParam", map);
		mv.addObject("adminList", dbConn.recordSet(QUERY_ROOT + ".adminList", null));
		mv.addObject("view", result.get(0));
		mv.addObject("salesProduct", result.get(1));
		mv.setViewName("online_sales/work.bare");
		return mv;
	}

	//자사몰 전용 > 업무 전용 > 자사몰 일간 업무 등록
	@RequestMapping(value = "/work_insert_DB.run", produces = "application/json")
	@Transactional
	@ResponseBody
	public Map<String, Object> workInsertDB(HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("getId", ((MemberDTO4) authentication.getPrincipal()).get_Id());
		List<Map<String, String>> list = new ArrayList<>();
		if (map.containsKey("pack_content_id") && map.containsKey("special_dis_nom") && map.containsKey("special_dis_exc") && map.containsKey("reducing_dis_nom") && map.containsKey("reducing_dis_exc") && map.containsKey("etc")) {
			String[] selectField = new String[]{"pack_content_id", "special_dis_nom", "special_dis_exc", "reducing_dis_nom", "reducing_dis_exc", "etc"};
			list = Common.paramToList(selectField, request.getParameterMap());
		}

		Map<String, Object> totalMap = new HashMap<>();
		totalMap.put("param", map);
		totalMap.put("itemIn", list);

		List<Map> result = new ArrayList<>();

		if (map.get("choice").equals("insert")) {
			result = dbConn2.recordSet(QUERY_ROOT + ".workInsert", totalMap);
		} else {
			dbConn2.recordSet(QUERY_ROOT + ".workUpdate", totalMap);
		}
		Map resultMap = new HashMap();

		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//자사몰 전용 > 업무 전용 > 자사몰 일간 업무 삭제
	@RequestMapping(value = "/work_delete_DB.run", produces = "application/json")
	@ResponseBody
	public void workDeleteDB(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		dbConn2.recordSet(QUERY_ROOT + ".workDelete", map);
	}

	//자사몰 전용 > 업무 전용 > 자사몰 일간 업무 등록
	@RequestMapping(value = "/work_input")
	public ModelAndView workInput(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("divide", Common.defaultValue(map.get("divide"), "daily"));
		map.put("id", Common.defaultValue(map.get("id"), ""));
		map.put("getId", ((MemberDTO4) authentication.getPrincipal()).get_Id());

		List<List<Map>> result = dbConn2.recordSet(QUERY_ROOT + ".workInput", map);
		mv.addObject("pageParam", map);
		mv.addObject("adminList", dbConn.recordSet(QUERY_ROOT + ".adminList", null));
		mv.addObject("view", result.get(0));
		mv.addObject("salesProduct", result.get(1));
		mv.setViewName("online_sales/work_input.bare");
		return mv;
	}

	//자사몰 전용 > 업무 전용 > 자사몰 일간 업무 등록 입력 > 업무구분
	@RequestMapping(value = "/category")
	public ModelAndView category(ModelAndView mv) {
		mv.setViewName("online_sales/category.bare");
		return mv;
	}


	//자사몰 전용 > 업무 전용 > 자사몰 월간 업무
	@RequestMapping(value = "/work_schedule")
	public ModelAndView workSchedule(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		LocalDate now = LocalDate.now();
		map.put("divide", Common.defaultValue(map.get("divide"), "monthly"));
		if (map.get("divide").equals("monthly")) {
			map.put("gubun", "2");
		} else {
			map.put("gubun", "1");
		}
		map.put("year_check", Common.defaultValue(map.get("year_check"), now.format(DateTimeFormatter.ofPattern("yyyy"))));
		map.put("month_check", Common.defaultValue(map.get("month_check"), now.format(DateTimeFormatter.ofPattern("M"))));

		List<Map<String, String>> view = dbConn2.recordSet(QUERY_ROOT + ".workSchedule", map);
		if (!Common.isNullOrEmpty(view)) {
			mv.addObject("content", view.get(0).get("content").replaceAll("(\r\n|\r|\n|\n\r)", "<br>"));
		}

		mv.addObject("pageParam", map);
		mv.addObject("adminList", dbConn.recordSet(QUERY_ROOT + ".adminList", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("online_sales/work_schedule.tiles");
		return mv;
	}

	//자사몰 전용 > 업무 전용 > 자사몰 월간 업무 달력
	@RequestMapping(value = "/calendar.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> getCalendar(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		List<String> sqlItemList = new ArrayList<>();
		List<String> sqlItemList2 = new ArrayList<>();
		List<String> sqlItemList3 = new ArrayList<>();
		List<String> sqlItemList4 = new ArrayList<>();
		sqlItemList.add(dbConn2.makeSearchSqlRange("sdate", request.getParameter("sdate"), request.getParameter("edate")));
		sqlItemList.add(dbConn2.makeSearchSql("process", request.getParameterValues("process"), "="));
		sqlItemList.add(dbConn2.makeSearchSql("divide", new String[]{"monthly"}, "="));
		sqlItemList.removeAll(Collections.singleton(null));

		sqlItemList2.add(dbConn2.makeSearchSql("year(sdate)", request.getParameterValues("year_check"), "="));
		sqlItemList2.add(dbConn2.makeSearchSql("month(sdate)", request.getParameterValues("month_check"), "="));
		sqlItemList2.removeAll(Collections.singleton(null));

		sqlItemList3.add(dbConn2.makeSearchSql("year(edate)", request.getParameterValues("year_check"), "="));
		sqlItemList3.add(dbConn2.makeSearchSql("month(edate)", request.getParameterValues("month_check"), "="));
		sqlItemList3.removeAll(Collections.singleton(null));

		List<Map> result = new ArrayList<>();
		Map resultMap = new HashMap();

		String sqlItem2 = String.join(" and ", sqlItemList2);
		String sqlItem3 = String.join(" and ", sqlItemList3);

		sqlItemList4.add((!Common.isNullOrEmpty(sqlItem2)) ? sqlItem2 : "");
		sqlItemList4.add((!Common.isNullOrEmpty(sqlItem3)) ? sqlItem3 : "");
		sqlItemList4.removeAll(Collections.singleton(null));

		String sqlItem4 = String.join(" or ", sqlItemList4);
		sqlItem4 = (!Common.isNullOrEmpty(sqlItem4)) ? " and (" + sqlItem4 + ")" : "";
		map.put("admin_member_id", map.get(Common.paramIsArray("admin_member_id", request)));
		map.put("sqlItem", String.join(" and ", sqlItemList) + sqlItem4);
		map.put("orderBy", "sdate asc");

		result = dbConn2.recordSet(QUERY_ROOT + ".workViewMonthly", map);

		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}


	//상품 리스트 일괄 등록
	@RequestMapping(value = "/product_excel_insert_DB.run", method = {RequestMethod.POST})
	@Transactional
	@ResponseBody
	public Map<String, Object> productExcelInsertDB(ModelAndView mv, HttpServletRequest request) {
		MultipartHttpServletRequest mr = (MultipartHttpServletRequest) request;
		Map resultMap = new HashMap();
		List<Map> result = new ArrayList<>();
		try {
			List<String> dbField = Arrays.asList("pack_content_id", "goods_name", "special_dis_nom", "special_dis_exc", "reducing_dis_nom", "reducing_dis_exc", "etc");
			List<Map<String, String>> list = excelCon.uploadExcel(mr.getFiles("product_file"), dbField, "상품코드");
			Map<String, Object> totalMap = new HashMap<>();
			totalMap.put("item", list);
			result = dbConn.recordSet(QUERY_ROOT + ".itemSelect", totalMap);
		} catch (IOException e) {
		}

		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}


	//자사몰 전용 > 업무 전용 > 자사몰 매출 분석
	@RequestMapping(value = "/statistics")
	public ModelAndView list(ModelAndView mv, HttpServletRequest request) {
		LocalDate now = LocalDate.now();
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		if (Common.isNullOrEmpty(map.get("yearV")) || Common.isNullOrEmpty(map.get("monthV"))) {
			map.put("wdate", now.format(DateTimeFormatter.ofPattern("yyyy-MM-01")));
		} else {
			map.put("wdate", Common.addString(map.get("yearV"), "-", Common.selectZero(Integer.parseInt(map.get("monthV")), "00"), "-01"));
		}
		map.put("not_partner", Common.defaultValue(map.get("not_partner"), ""));
		map.put("yesterday", Common.defaultValue(map.get("yesterday"), "0"));

		int dayV = LocalDate.parse(now.plusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-01"))).minusDays(1).getDayOfMonth();

		boolean nowDate = false;
		if (ChronoUnit.MONTHS.between(now, LocalDate.parse(map.get("wdate"))) == 0) {
			nowDate = true;
		}
		map.put("cyear", LocalDate.parse(map.get("wdate")).format(DateTimeFormatter.ofPattern("yyyy")));
		map.put("cmonth", LocalDate.parse(map.get("wdate")).format(DateTimeFormatter.ofPattern("MM")));
		List<List<Map>> list = dbConn.recordSet(QUERY_ROOT + ".statistics", map);
		List<Map> commentList = dbConn2.recordSet(QUERY_ROOT + ".commentList", map);

		mv.addObject("list", list.get(0));
		mv.addObject("siteList", list.get(1));
		mv.addObject("etcList", list.get(2));
		mv.addObject("weekList", list.get(3));
		mv.addObject("comment", commentList);
		mv.addObject("nowDate", nowDate);
		mv.addObject("day", (now.getDayOfMonth() + Integer.parseInt(map.get("yesterday"))));
		mv.addObject("dayV", dayV);
		mv.addObject("cyear", map.get("cyear"));
		mv.addObject("cmonth", map.get("cmonth"));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("online_sales/statistics.tiles");
		return mv;
	}

	//자사몰 전용 > 업무 전용 > 자사몰 매출 분석 > 월간 이슈 등록
	@RequestMapping(value = "/comment_input")
	public ModelAndView commentInput(ModelAndView mv, HttpServletRequest request) {
		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn2.makeSearchSql("gubun", request.getParameterValues("gubun"), "="));
		sqlItemList.add(dbConn2.makeSearchSql("ydate", request.getParameterValues("cyear"), "="));
		sqlItemList.add(dbConn2.makeSearchSql("mdate", request.getParameterValues("cmonth"), "="));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("orderBy", "id desc");
		map.put("curPage", Common.defaultValue(request.getParameter("curPage"), "1"));
		map.put("pageSize", Common.defaultValue(request.getParameter("pageSize"), "20"));

		result = dbConn2.recordSet(QUERY_ROOT + ".commentView", map);
		for (int i = 0; i < result.size(); i++) {
			result.get(i).put("content", (result.get(i).get("content")).toString().replaceAll("(\r\n|\r|\n|\n\r)", "<br>"));
		}

		mv.addObject("returnParam", map);
		mv.addObject("list", Common.nullToEmpty(result));
		mv.setViewName("online_sales/comment_input.bare");
		return mv;
	}

	//자사몰 전용 > 업무 전용 > 자사몰 매출 분석 > 목표등록
	@RequestMapping(value = "/statistics_comment_input")
	public ModelAndView statisticsCommentInput(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		LocalDate wdate = LocalDate.parse(map.get("cyear") + "-" + map.get("cmonth") + "-01");
		LocalDate wsdate = wdate.with(TemporalAdjusters.firstDayOfMonth()).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		LocalDate wedate = wdate.with(TemporalAdjusters.lastDayOfMonth()).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
		LocalDate ysdate = wdate.with(TemporalAdjusters.firstDayOfYear()).with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

		map.put("qsdate", String.valueOf(wsdate.get(WeekFields.ISO.weekOfWeekBasedYear()) + 1));
		map.put("qedate", String.valueOf(wedate.get(WeekFields.ISO.weekOfWeekBasedYear()) + 1));

		List<Map<String, String>> comment = new ArrayList<>();
		if (map.get("gubun").equals("5")) {
			comment = dbConn2.recordSet(QUERY_ROOT + ".comment5", map);
		} else {
			comment = dbConn2.recordSet(QUERY_ROOT + ".comment", map);
		}

		mv.addObject("wsdate", wsdate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		mv.addObject("wedate", wedate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		mv.addObject("ysdate", ysdate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		mv.addObject("qsdate", map.get("qsdate"));
		mv.addObject("qedate", map.get("qedate"));
		mv.addObject("comment", comment);
		mv.addObject("gubun", map.get("gubun"));
		mv.addObject("cyear", map.get("cyear"));
		mv.addObject("cmonth", map.get("cmonth"));

		mv.setViewName("online_sales/statistics_comment_input.bare");
		return mv;
	}

	//자사몰 전용 > 업무 전용 > 자사몰 매출 분석 > 주간목표등록 Insert
	@RequestMapping(value = "/comment_insert_DB.run", method = RequestMethod.POST)
	@Transactional
	@ResponseBody
	public void commentInsertDB(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("user_name", ((MemberDTO4) authentication.getPrincipal()).getUser_name());

		List<Map<String, String>> list = new ArrayList<>();

		if (map.containsKey("weekValue") && map.containsKey("weekId")) {
			String[] selectField = new String[]{"weekValue", "weekId"};
			list = Common.paramToList(selectField, request.getParameterMap());
		}

		Map<String, Object> totalMap = new HashMap<>();
		totalMap.put("param", map);
		totalMap.put("itemIn", list);

		if (map.get("gubun").equals("5")) {
			dbConn2.recordSet(QUERY_ROOT + ".commentInsertDB5", totalMap);
		} else if (map.get("gubun").equals("3") || map.get("gubun").equals("4")) {
			dbConn2.recordSet(QUERY_ROOT + ".commentInsertDB34", totalMap);
		} else {
			dbConn2.recordSet(QUERY_ROOT + ".commentInsertDB126", map);
		}
	}

	//자사몰 매출 비중
	@RequestMapping(value = "/statistics_weight")
	public ModelAndView statisticsWeight(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("online_sales/statistics_weight.pq");
		return mv;
	}

	//자사몰 매출 비중
	@RequestMapping(value = "/statistics_weight.run")
	@ResponseBody
	public Map<String, Object> statisticsWeightList(HttpServletRequest request, Authentication authentication) {
		LocalDate now = LocalDate.now();
		Map<String, Object> returnParam = new HashMap<>();
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("user_brand", ((MemberDTO4) authentication.getPrincipal()).getUser_brand().replaceAll("\\]\\[", ",").replaceAll("\\[", "").replaceAll("\\]", ""));
		map.put("yesterday", Common.defaultValue(map.get("yesterday"), "0"));
		if (Common.isNullOrEmpty(map.get("yearV"))) {
			map.put("wdate", now.format(DateTimeFormatter.ofPattern("yyyy-MM-01")));
		} else {
			map.put("wdate", Common.addString(map.get("yearV"), "-", now.format(DateTimeFormatter.ofPattern("MM-01"))));
		}

		List<String> brandList = new ArrayList<>();
		brandList.add(Common.defaultValue(map.get(Common.paramIsArray("category_site_id", request)), "28"));
		map.put("brandList", String.join(", ", brandList));

		List<Map> list = dbConn.recordSet(QUERY_ROOT + ".weightList", map);
		returnParam.put("data", Common.nullToEmpty(list));
		return returnParam;
	}

	//자사몰 매출 비중
	@RequestMapping(value = "/statistics_brand_weekly")
	public ModelAndView statisticsBrandWeekly(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("online_sales/statistics_brand_weekly.pq");
		return mv;
	}

	//자사몰 매출 비중
	@RequestMapping(value = "/statistics_brand_weekly.run")
	@ResponseBody
	public Map<String, Object> statisticsBrandWeekly(HttpServletRequest request, Authentication authentication) {
		LocalDate now = LocalDate.now();
		Map<String, Object> returnParam = new HashMap<>();
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("user_brand", ((MemberDTO4) authentication.getPrincipal()).getUser_brand().replaceAll("\\]\\[", ",").replaceAll("\\[", "").replaceAll("\\]", ""));
		map.put("yesterday", Common.defaultValue(map.get("yesterday"), "0"));
		if (Common.isNullOrEmpty(map.get("yearV")) || Common.isNullOrEmpty(map.get("monthV"))) {
			map.put("wdate", now.format(DateTimeFormatter.ofPattern("yyyy-MM-01")));
		} else {
			map.put("wdate", Common.addString(map.get("yearV"), "-", Common.selectZero(Integer.parseInt(map.get("monthV")), "00"), "-01"));
		}

		List<String> brandList = new ArrayList<>();
		brandList.add(Common.defaultValue(map.get(Common.paramIsArray("MD_brand", request)), "28"));
		map.put("brandList", String.join(", ", brandList));

		List<List<Map>> list = dbConn.recordSet(QUERY_ROOT + ".brandWeekly", map);
		returnParam.put("data", Common.nullToEmpty(list.get(1)));
		return returnParam;
	}
}
