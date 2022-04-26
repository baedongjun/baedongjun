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
@RequestMapping("/views/event")
public class EventJoriwon {

	private final String DIR_ROOT = "eventJoriwon";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;
	@Resource(name = "excelCon")
	private ExcelCon excelCon;

	//산후조리원제휴_2016
	@RequestMapping(value = "/joriwon_2016")
	public ModelAndView list(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("event/joriwon_2016.pq");
		return mv;
	}

	//산후조리원제휴_2016 검색 및 페이징
	@RequestMapping(value = "/joriwon_2016.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> list(HttpServletResponse response, HttpServletRequest request) {
		Map<String, String> param = new HashMap<>();
		List<String> searchList = new ArrayList<>();
		List<String> searchList2 = new ArrayList<>();

		searchList2.add(dbConn.makeSearchSql("A.userid",request.getParameterValues("search_word"),"like"));
		searchList2.add(dbConn.makeSearchSql("A.username",request.getParameterValues("search_word"),"like"));
		searchList2.removeAll(Collections.singleton(null));

		String sqlItem2 = String.join(" or ",searchList2);
		sqlItem2 = (!Common.isNullOrEmpty(sqlItem2)) ? " and (" + sqlItem2 + ")" : "";

		searchList.add(dbConn.makeSearchSql("year(wdate)",request.getParameterValues("yearV"),"="));
		searchList.add(dbConn.makeSearchSqlRange("A.cdate", request.getParameter("wdate1"), request.getParameter("wdate2")));
		searchList.add(dbConn.makeSearchSqlRange("A.wdate", request.getParameter("rdate1"), request.getParameter("rdate2")));
		searchList.add(dbConn.makeSearchSqlRange("A.udate", request.getParameter("udate1"), request.getParameter("udate2")));
		searchList.add(dbConn.makeSearchSql("A.joriwon", request.getParameterValues("joriwon"), "="));
		searchList.removeAll(Collections.singleton(null));

		param.put("sqlItem", String.join(" and ", searchList) + sqlItem2);
		param.put("orderBy", "A.wdate desc");
		Common.PQmap(param,request);

		List<Map> list = new ArrayList<>();
		list = dbConn.recordSet(QUERY_ROOT + ".list", param);

		Map<String, Object> returnParam = new HashMap<>();
		Common.PQresultMap(returnParam,param.get("curPage"),list);
		returnParam.put("data", list);

		return returnParam;
	}

	//엑셀다운로드
	@RequestMapping(value = "/excelDown.run")
	public void downExcel(HttpServletResponse response, HttpServletRequest request) {
		Map<String,String> param = new HashMap<>();
		param.put("yearV",request.getParameter("yearV"));

		List<String> dbField = Arrays.asList("userid", "username", "coupon_register", "joriwon", "couponCnt", "wdate", "this_use", "udate", "jumuncode", "sales_money", "email", "mobile", "cdate");
		List<String> cellName = Arrays.asList("아이디", "이름", "쿠폰번호", "조리원", "총등록수량", "등록일", "사용여부", "사용일", "주문번호", "총주문금액(기본할인)", "이메일", "연락처", "가입일자");

		try {
			excelCon.downExcelFile(response,dbConn, QUERY_ROOT + ".excelDown", param, dbField, cellName);
		} catch (IOException e) {
		}
	}
}

