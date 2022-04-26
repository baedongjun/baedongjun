package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.common.ExcelCon;
import com.common.member.MemberDTO4;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/views/stock")
public class Stock {
	private final String DIR_ROOT = "stock";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;
	@Resource(name = "dbConn2")
	private DbConn dbConn2;
	@Resource(name = "excelCon")
	private ExcelCon excelCon;
	@Resource
	private MemberDTO4 memberDTO4;

	//재고 조정이력 list
	@RequestMapping(value = "/adjust_stock")
	public ModelAndView adjust_stock(ModelAndView mv, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		List<String> searchList = new ArrayList<>();

		searchList.add(dbConn2.makeSearchSql("code1", request.getParameterValues("code"), "=")); //브랜드
		searchList.add(dbConn2.makeSearchSql("code2", request.getParameterValues("code2"), "=")); //품목
		searchList.add(dbConn2.makeSearchSql("codename2", request.getParameterValues("codename"), "like")); //기초코드
		searchList.removeAll(Collections.singleton(null));

		String searchListJoin = new String();
		searchListJoin = String.join(" and ", searchList);

		StringBuilder arrQuery = new StringBuilder();
		arrQuery.append("[petitelin_CRM2]..[STOCK_adjust_stock] A");

		if (searchList.size() != 0) {
			arrQuery.append(" inner join (");
			arrQuery.append(" select id from openquery (server7, 'select id from [petitelin_CRM]..[view_product] where 1=1 and " + searchListJoin.replace(", null", "").replace("'", "''").replace("[", "").replace("]", "") + "')");
			arrQuery.append(" ) B ON A.product_id=B.id");
		}

		List<String> searchList2 = new ArrayList<>();
		searchList2.add(dbConn2.makeSearchSqlRange("adate", request.getParameter("adate1"), request.getParameter("adate2")));
		if (request.getParameter("cnt") != null && request.getParameter("cnt") != "") {
			searchList2.add("abs(cnt) >=" + request.getParameter("cnt"));
		}
		searchList2.add(dbConn2.makeSearchSql("zone", request.getParameterValues("zone"), "=")); //창고
		searchList2.add(dbConn2.makeSearchSql("detail", request.getParameterValues("detail"), "like")); //메모
		searchList2.removeAll(Collections.singleton(null));

		map.put("sqlItem", String.join(" and ", searchList2));
		map.put("orderBy", "A.id desc");
		map.put("totalRecords", Common.defaultValue(request.getParameter("totalRecords"),"0"));
		map.put("curPage", Common.defaultValue(request.getParameter("curPage"),"1"));
		map.put("pageSize", Common.defaultValue(request.getParameter("pageSize"),"50"));

		map.put("tbname", arrQuery.toString());

		List<Map> list = new ArrayList<>();
		list = dbConn2.recordSet(QUERY_ROOT + ".adjust", map);


		//하단 list
		String adjust_id_array = "0";
		String product_id_array = "0";
		for (int i = 0; i < list.size(); i++) {
			adjust_id_array = adjust_id_array + ", " + list.get(i).get("id");
			product_id_array = product_id_array + ", " + list.get(i).get("product_id");
		}

		Map<String, String> param = new HashMap<>();

		param.put("adjust_id_array", adjust_id_array);
		param.put("product_id_array", product_id_array);

		List<Map> list2 = new ArrayList<>();
		list2 = dbConn2.recordSet(QUERY_ROOT + ".adjust_id_array", param);
		List<Map> list3 = new ArrayList<>();
		list3 = dbConn.recordSet(QUERY_ROOT + ".product_id_array", param);

		map.put("totalRecords", list.size() > 0 ? list.get(0).get("totalRecords") : 0);

		mv.addObject("returnParam", map);
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("list", Common.nullToEmpty(list));

		mv.addObject("list2", Common.nullToEmpty(list2));
		mv.addObject("list3", Common.nullToEmpty(list3));

		mv.addObject("code1",dbConn.recordSet("common.query.code1",null));

		mv.setViewName("stock/adjust_stock.tiles");
		return mv;
	}


	//조정재고 이력 일괄 등록 엑셀
	@RequestMapping(value = "/insertExcel.run", method = {RequestMethod.POST})
	public ModelAndView insertExcel(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		MultipartHttpServletRequest mr = (MultipartHttpServletRequest) request;
		memberDTO4 = (MemberDTO4) authentication.getPrincipal();
		String userId = memberDTO4.getUser_id();
		Map<String, String> map = new HashMap<>();
		try {
			List<String> dbField = Arrays.asList("codename2", "location", "zone", "gubun", "adate", "gubun2", "cnt", "admindate", "detail", "comment", "admin");
			List<Map<String, String>> list = excelCon.uploadExcel(mr.getFiles("addFile"), dbField, "상품코드");
			for (int i = 0; i < list.size(); i++) {
				list.get(i).put("admin", userId);
			}


			dbConn2.recordSet(QUERY_ROOT + ".excelInsert", list);
		} catch (Exception e) {
		}

		mv.setViewName("redirect:/views/stock/adjust_stock");
		return mv;
	}


	//추가사유 (조정재고 이력 코멘트 등록 상단 view
	@RequestMapping(value = "/adjust_comment_input")
	public ModelAndView commentInput(ModelAndView mv, HttpServletRequest request) {
		List<Map> list = new ArrayList<>();
		list = dbConn.recordSet(QUERY_ROOT + ".commentInput", request.getParameter("product_id"));

		mv.addObject("list", list);
		mv.addObject("product_id", request.getParameterValues("product_id"));
		mv.addObject("adjust_id", request.getParameterValues("adjust_id"));
		mv.setViewName("stock/adjust_comment_input.bare");
		return mv;
	}


	//조정재고 이력 코멘트 등록 하단 view
	@RequestMapping(value = "/adjust_comment_view")
	public ModelAndView commentView(ModelAndView mv, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		List<String> searchList = new ArrayList<>();

		searchList.add(dbConn.makeSearchSql("adjust_id", request.getParameterValues("adjust_id"), "="));
		searchList.removeAll(Collections.singleton(null));

		map.put("sqlItem", String.join(" and ", searchList));
		map.put("orderBy", "id desc");
		map.put("totalRecords", Common.defaultValue(request.getParameter("totalRecords"),"0"));
		map.put("curPage", Common.defaultValue(request.getParameter("curPage"),"1"));
		map.put("pageSize", Common.defaultValue(request.getParameter("pageSize"),"10"));

		List<String> id = dbConn.recordSet(QUERY_ROOT + ".commentViewId", request.getParameter("product_id"));
		map.put("name", id.toString());

		List<Map> list = dbConn2.recordSet(QUERY_ROOT + ".commentView", map);

		map.put("totalRecords", list.size() > 0 ? list.get(0).get("totalRecords") : 0);

		mv.addObject("returnParam", map);
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("list", Common.nullToEmpty(list));
		mv.addObject("product_id", request.getParameter("product_id"));
		mv.addObject("id", id);

		mv.setViewName("stock/adjust_comment_view");
		return mv;
	}

	//조정재고 이력 코멘트 등록 하단 run (adjust_comment_insert_DB.asp
	@RequestMapping(value = "adjust_comment_insert_DB.run")
	public String commentInsert(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		if (request.getParameter("choice").equals("insert")) {
			dbConn2.recordSet(QUERY_ROOT + ".commentInsert", map);  //등록.  adjust_comment_input.jsp
		}

		if (request.getParameter("choice").equals("del")) { //삭제 adjust_comment_view.jsp
			dbConn2.recordSet(QUERY_ROOT + ".commentDelete", map.get("id"));
		}
		return "redirect:/views/stock/adjust_comment_input?adjust_id=" + request.getParameter("adjust_id") + "&product_id=" + request.getParameter("product_id");
	}

	// 재고 관리 전용 - 특이사항 - 리스트
	@RequestMapping(value = "/important")
	public ModelAndView important(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("marketName", dbConn.recordSet(QUERY_ROOT + ".marketName", null));
		mv.addObject("brand", request.getParameter("brand"));
		mv.addObject("searchName", request.getParameter("searchName"));
		mv.setViewName("stock/important.pq");
		return mv;
	}

	// 재고 관리 전용 - 특이사항 - 리스트
	@RequestMapping(value = "/important.run", method = {RequestMethod.POST}, produces = "application/json;")
	@ResponseBody
	public Map<String, Object> important(HttpServletRequest request, HttpServletResponse response) {
		List<String> sqlItemList = new ArrayList<>();
		List<String> sqlItemList2 = new ArrayList<>();

		String[] searchName = request.getParameterValues("searchName");

		if (!Common.isNullOrEmpty(request.getParameterValues("searchName"))) {
			searchName = new String[]{searchName[0].replace("-","")};
		}

		sqlItemList2.add(dbConn2.makeSearchSql("replace(codename2,'-','')", searchName, "like"));
		sqlItemList2.add(dbConn2.makeSearchSql("name", request.getParameterValues("searchName"), "like"));
		sqlItemList2.removeAll(Collections.singleton(null));

		String sqlItem2 = String.join(" or ", sqlItemList2);
		sqlItem2 = (!Common.isNullOrEmpty(sqlItem2)) ? " and (" + sqlItem2 + " )" : "";

		sqlItemList.add(dbConn2.makeSearchSql("brand", request.getParameterValues("brand"), "="));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", String.join(" and ", sqlItemList) + sqlItem2);
		map.put("orderBy", "A.id desc");
		Common.PQmap(map, request);

		result = dbConn2.recordSet(QUERY_ROOT + ".important", map);

		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}

	// 재고 관리 전용 - 특이사항 - 특이사항에서 제외
	@RequestMapping(value = "important_insert_solve.run")
	public ModelAndView importantInsertSolve(HttpServletRequest request, ModelAndView mv) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		String ids = !Common.isNullOrEmpty(request.getParameter(Common.paramIsArray("id", request))) ? String.join(",", request.getParameterValues(Common.paramIsArray("id", request))) : null;
		String checkIdArray = !Common.isNullOrEmpty(request.getParameter(Common.paramIsArray("checkIdArray", request))) ? String.join(",", request.getParameterValues(Common.paramIsArray("checkIdArray", request))) : null;

		map.put("ids",ids);
		map.put("checkIdArray",checkIdArray);

		dbConn2.recordSet(QUERY_ROOT + ".solveInsert", map);

		if (request.getParameter("choice").equals("solve")) {
			mv.addObject("brand",request.getParameter("brand"));
			mv.addObject("searchName",request.getParameter("searchName"));
			mv.addObject("resultCode","0");
			mv.setViewName("redirect:/views/stock/important");
		} else {
			mv.addObject("product_id",request.getParameter("product_id"));
			mv.addObject("resultCode","0");
			mv.setViewName("redirect:/views/stock/important_view");
		}
		return mv;
	}

	// 재고 관리 전용 - 특이사항 - 기초상품 특이사항 등록
	@RequestMapping(value = "important_insert.run")
	@ResponseBody
	public String importantInsert(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		map.put("brand",request.getParameter("market"));
		map.put("product_id",request.getParameter("view_name"));

		dbConn2.recordSet(QUERY_ROOT + ".importantInsert", map);

		return request.getParameter("view_name");
	}

	// 재고 관리 전용 - 특이사항 - 등록 화면
	@RequestMapping(value = "/important_input")
	public ModelAndView important_input(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("marketName", dbConn.recordSet(QUERY_ROOT + ".marketName", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("product_id", request.getParameter("product_id"));
		mv.setViewName("stock/important_input.tiles");
		return mv;
	}

	// 재고 관리 전용 - 특이사항 - 기초상품 특이사항 등록 화면 - 기초상품 카테고리
	@RequestMapping(value = "/category_view.run", produces = "Application/json")
	@ResponseBody
	public Map<String, Object> categoryView(HttpServletResponse response, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		Map<String, Object> returnParam = new HashMap<>();

		List<Map> list = null;

		if (request.getParameter("group").equals("1")) {
			list = dbConn.recordSet(QUERY_ROOT + ".category1", map);
		} else if (request.getParameter("group").equals("2")) {
			list = dbConn.recordSet(QUERY_ROOT + ".category2", map);
		} else {
			list = dbConn.recordSet(QUERY_ROOT + ".category3", map);
		}

		returnParam.put("data", Common.nullToEmpty(list));
		return returnParam;
	}

	// 재고 관리 전용 - 특이사항 - 리스트
	@RequestMapping(value = "/important_view")
	public ModelAndView important_view(ModelAndView mv, HttpServletRequest request) {
		List<String> sqlItemList = new ArrayList<>();

		sqlItemList.add(dbConn2.makeSearchSql("A.product_id", request.getParameterValues("product_id"), "="));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, Object> map = new HashMap<>();
		String curPage = Optional.ofNullable(request.getParameter("curPage")).orElse("1");

		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("orderBy", "A.id desc");
		map.put("totalRecords", Optional.ofNullable(request.getParameter("totalRecords")).orElse("0"));
		map.put("curPage", curPage);
		map.put("product_id", request.getParameter("product_id"));
		map.put("pageSize", Optional.ofNullable(request.getParameter("pageSize")).orElse("18"));

		List<Map> list = dbConn2.recordSet(QUERY_ROOT + ".important", map);

		map.put("totalRecords", list.size() > 0 ? list.get(0).get("totalRecords") : 0);

		mv.addObject("list", list);
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("stock/important_view.bare");
		return mv;
	}

	// 재고 관리 전용 - 공휴일 등록 화면
	@RequestMapping(value = "/holiday")
	public ModelAndView holiday(ModelAndView mv, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();


		List<Map> list = dbConn2.recordSet(QUERY_ROOT + ".holiday", map);

		mv.addObject("list", list);
		mv.setViewName("stock/holiday.tiles");
		return mv;
	}

	// 재고 관리 전용 - 공휴일 등록하기
	@RequestMapping(value = "holidayInsert.run")
	public ModelAndView holidayInsert(HttpServletRequest request, ModelAndView mv) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		String sdate = "";
		String edate = "";

		dbConn2.recordSet(QUERY_ROOT + ".holidayTruncate", map);

		for (int i=0; i<request.getParameterValues("name").length; i++) {
			if (!Common.defaultValue(request.getParameterValues("name")[i],"").equals("")) {
				if (Common.defaultValue(request.getParameterValues("yearV1")[i],"").equals("")) {
					sdate = request.getParameterValues("monthV1")[i] + "-" + request.getParameterValues("dayV1")[i];
				} else {
					sdate = request.getParameterValues("yearV1")[i] + "-" + request.getParameterValues("monthV1")[i] + "-" + request.getParameterValues("dayV1")[i];
				}

				if (Common.defaultValue(request.getParameterValues("yearV2")[i],"").equals("")) {
					edate = request.getParameterValues("monthV2")[i] + "-" + request.getParameterValues("dayV2")[i];
				} else {
					edate = request.getParameterValues("yearV2")[i] + "-" + request.getParameterValues("monthV2")[i] + "-" + request.getParameterValues("dayV2")[i];
				}

				map.put("name",request.getParameterValues("name")[i]);
				map.put("gubun",request.getParameterValues("gubun")[i]);
				map.put("sdate",sdate);
				map.put("edate",edate);

				if ((i+1) == request.getParameterValues("name").length) {
					dbConn2.recordSet(QUERY_ROOT + ".holidayInsert", map);
				} else {
					if (request.getParameterValues("thisDel")[i].equals("n")) {
						dbConn2.recordSet(QUERY_ROOT + ".holidayInsert", map);
					}
				}
			}
		}

		mv.setViewName("redirect:/views/stock/holiday");
		return mv;
	}

	// 재고 관리 전용 - 그룹별 기초상품
	@RequestMapping(value = "/groupPer")
	public ModelAndView groupPer(ModelAndView mv) {
		mv.setViewName("stock/groupPer.pq");
		return mv;
	}

	// 재고 관리 전용 - 그룹별 기초상품
	@RequestMapping(value = "/groupPer.run", method = {RequestMethod.POST}, produces = "application/json;")
	@ResponseBody
	public Map<String, Object> groupPer(HttpServletRequest request) {
		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		Map resultMap = new HashMap();

		map.put("orderBy", "groupname, code1Name +' > '+ code2Name +' > '+ name +' '+ code4code collate Korean_Wansung_CS_AS");
		Common.PQmap(map, request);

		result = dbConn.recordSet(QUERY_ROOT + ".groupPer", map);

		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}

	// 재고 관리 전용 - 일별 평균 재고 (물류)
	@RequestMapping(value = "/stockDayMoney")
	public ModelAndView stockDayMoney(ModelAndView mv,HttpServletRequest request) throws Exception {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		List<Map> list = dbConn.recordSet(QUERY_ROOT + ".codeGroupList", map);

		if (Common.isNullOrEmpty(request.getParameter("yearValue")) || Common.isNullOrEmpty(request.getParameter("codeGroup"))) {
			mv.setViewName("redirect:/views/stock/stockDayMoney?yearValue=" + LocalDate.now().getYear() + "&monthValue=" + LocalDate.now().getMonthValue() + "&codeGroup=242");
			return mv;
		}

		Calendar calendar = Calendar.getInstance();
		calendar.set(Integer.parseInt(request.getParameter("yearValue")), Integer.parseInt(request.getParameter("monthValue"))-1,1);

		mv.addObject("lastDay", String.valueOf(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("list", list);
		mv.addObject("data", Common.mapToJson(dbConn2.recordSet(QUERY_ROOT + ".stockDayMoney", map)));
		mv.setViewName("stock/stock_day_money.pq");
		return mv;
	}

	// 재고 관리 전용 - 기초/기말 (물류)
	@RequestMapping(value = "/logistics")
	public ModelAndView logistics(ModelAndView mv,HttpServletRequest request) throws Exception {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		if (Common.isNullOrEmpty(request.getParameter("yearValue")) || Common.isNullOrEmpty(request.getParameter("brand"))) {
			mv.setViewName("redirect:/views/stock/logistics?yearValue=" + LocalDate.now().getYear() + "&monthValue=" + (LocalDate.now().getMonthValue()-1) + "&brand=55");
			return mv;
		}

		List<Map> list = dbConn2.recordSet(QUERY_ROOT + ".logistics", map);

		String thisGroupName = "";
		for (int i=0; i<list.size(); i++) {
			if (!list.get(i).get("groupname").equals(thisGroupName)) {
				map = new HashMap<>();
				thisGroupName = list.get(i).get("groupname").toString();
				map.put("thisGroupName",thisGroupName);
				list.add(i,map);
			}
		}

		mv.addObject("marketName", dbConn.recordSet(QUERY_ROOT + ".marketName", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("data", Common.mapToJson(list));
		mv.setViewName("stock/logistics.pq");
		return mv;
	}

	// 재고 관리 전용 - 단종예정 상품
	@RequestMapping(value = "/productDiscontinued")
	public ModelAndView productDiscontinued(ModelAndView mv, HttpServletRequest request) {
		if (Common.isNullOrEmpty(request.getParameter("bprand_status")) || Common.isNullOrEmpty(request.getParameter("brand"))) {
			mv.setViewName("redirect:/views/stock/productDiscontinued?bprand_status=" + Common.defaultValue(request.getParameter("bprand_status"),"0") + "&brand=" + Common.defaultValue(request.getParameter("brand"),"55"));
			return mv;
		}

		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("marketName", dbConn.recordSet(QUERY_ROOT + ".marketName", null));
		mv.addObject("brand", request.getParameter("brand"));
		mv.setViewName("stock/product_discontinued.pq");
		return mv;
	}

	// 재고 관리 전용 - 단종예정 상품
	@RequestMapping(value = "/productDiscontinued.run", method = {RequestMethod.POST}, produces = "application/json;")
	@ResponseBody
	public Map<String, Object> productDiscontinued(HttpServletRequest request, HttpServletResponse response) {
		List<String> sqlItemList = new ArrayList<>();

		if (!Common.isNullOrEmpty(request.getParameterValues("codename2"))) {
			sqlItemList.add(dbConn2.makeSearchSql("replace(codename2,'-','')", new String[]{request.getParameterValues("codename2")[0].replace("-","")}, "like"));
		}
		sqlItemList.add(dbConn2.makeSearchSql("D.brand", request.getParameterValues("brand"), "="));
		sqlItemList.add(dbConn2.makeSearchSql("case when isNull(C.stock,0) <=0 then 1 else 0 end", request.getParameterValues("bprand_status"), "="));
		sqlItemList.add(dbConn2.makeSearchSql("name", request.getParameterValues("name"), "like"));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("orderBy", "A.wdate desc, isNull(C.stock,0) desc");
		Common.PQmap(map, request);

		result = dbConn2.recordSet(QUERY_ROOT + ".productDiscontinued", map);

		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}

	// 재고 관리 전용 - 단종예정 상품 - 물류확인
	@RequestMapping(value = "product_discontinued_insert.run")
	@ResponseBody
	public ModelAndView product_discontinued_insert(HttpServletRequest request, Authentication authentication,ModelAndView mv) {
		MemberDTO4 member = (MemberDTO4) authentication.getPrincipal();
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		String chkid = !Common.isNullOrEmpty(request.getParameter(Common.paramIsArray("chkid", request))) ? String.join(",", request.getParameterValues(Common.paramIsArray("chkid", request))) : null;
		map.put("userName",member.getUser_name());
		map.put("chkid",chkid);

		dbConn2.recordSet(QUERY_ROOT + ".productDiscontinuedConfirm", map);

		mv.addObject("resultCode","0");
		mv.setViewName("redirect:/views/stock/productDiscontinued");

		return mv;
	}

	// 재고 관리 전용 - 상품 그룹 관리
	@RequestMapping(value = "/group")
	public ModelAndView group(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("marketName", dbConn.recordSet(QUERY_ROOT + ".marketName", null));

		mv.setViewName("stock/group.tiles");
		return mv;
	}

	// 재고 관리 전용 - 상품 그룹 관리 - 카테고리 등록,수정,삭제
	@RequestMapping(value = "/stock_category_insert.run")
	@ResponseBody
	public ModelAndView stock_category_insert(HttpServletRequest request, ModelAndView mv) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		if (request.getParameter("choice").equals("insert")) {
			dbConn.recordSet(QUERY_ROOT + ".stockCategoryInsert", map);
		} else if (request.getParameter("choice").equals("correct")) {
			dbConn.recordSet(QUERY_ROOT + ".stockCategoryUpdate", map);
		} else {
			dbConn.recordSet(QUERY_ROOT + ".stockCategoryDelete", map);
		}

		mv.setViewName("redirect:/views/stock/group");
		return mv;
	}

	// 재고 관리 전용 - 상품 그룹 관리 - 그룹 등록,수정,삭제
	@RequestMapping(value = "/category_insert.run")
	@ResponseBody
	public ModelAndView category_insert(HttpServletRequest request, ModelAndView mv) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		if (request.getParameter("choice").equals("insert")) {
			dbConn.recordSet(QUERY_ROOT + ".categoryInsert", map);
		} else if (request.getParameter("choice").equals("correct")) {
			dbConn.recordSet(QUERY_ROOT + ".categoryUpdate", map);
		} else if (request.getParameter("choice").equals("packInsert")) {
			dbConn.recordSet(QUERY_ROOT + ".categoryDelete", map);

			for (int i=0; i<request.getParameterValues("view_name").length; i++) {
				if (request.getParameter("view_name").split("^").length > 1) {
					map.put("viewName0",request.getParameter("view_name").split("^")[0]);
					map.put("viewName1",request.getParameter("view_name").split("^")[1]);
					dbConn.recordSet(QUERY_ROOT + ".categoryPackInsert1", map);
				} else {
					map.put("view_name",request.getParameterValues("view_name")[i]);
					dbConn.recordSet(QUERY_ROOT + ".categoryPackInsert2", map);
				}
			}
		} else {
			dbConn.recordSet(QUERY_ROOT + ".categoryDelete", map);
		}

		mv.addObject("resultCode","0");
		mv.setViewName("redirect:/views/stock/group");

		return mv;
	}

	// 재고 관리 전용 - 일별재고 (물류)
	@RequestMapping(value = "/stockDayZone")
	public ModelAndView stockDayZone2(ModelAndView mv,HttpServletRequest request) throws Exception {
		Map<String, String[]> paramMap = new HashMap();
		paramMap.put("codeGroup", new String[]{"503"});
		paramMap.put("display", new String[]{"1"});
		paramMap.put("yearValue", new String[]{String.valueOf(LocalDate.now().getYear())});
		paramMap.put("monthValue", new String[]{String.valueOf(LocalDate.now().getMonthValue())});
		paramMap.putAll(request.getParameterMap());

		Map<String, String> map = Common.paramToMap(paramMap);
		List<Map> list = dbConn.recordSet(QUERY_ROOT + ".codeGroupList2", map);
		map.put("zone", request.getParameter("zone"));

		Calendar calendar = Calendar.getInstance();
		calendar.set(Integer.parseInt(map.get("yearValue")), Integer.parseInt(map.get("monthValue"))-1,1);

		mv.addObject("lastDay", calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		mv.addObject("searchParam", Common.paramToSearch(paramMap));
		mv.addObject("list", list);
		mv.addObject("zone", request.getParameter("zone"));
		mv.addObject("yearValue", calendar.get(Calendar.YEAR));
		mv.addObject("monthValue", Common.selectZero(Integer.parseInt(String.valueOf(calendar.get(Calendar.MONTH)+1)),"00"));
		mv.addObject("data", dbConn2.recordSet(QUERY_ROOT + ".stockDayZone", map));
		mv.setViewName("stock/stock_day_zone.tiles");
		return mv;
	}

	@RequestMapping(value = "/stockDayZoneExcelDown.run")
	public void stockDayZoneExcelDown(HttpServletResponse response, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("codeGroup", Common.defaultValue(map.get(Common.paramIsArray("codeGroup", request)), "503"));
		map.put("display", Common.defaultValue(map.get(Common.paramIsArray("display", request)), "503"));
		List<String> dbField = new ArrayList<>();
		List<String> cellName = new ArrayList<>();

		String column = "";
		dbField.addAll(Arrays.asList("code2name", "codename2","name", "grade"));
		for (int i = 1; i <= 31; i++) {
			column = "s" + i;
			dbField.add(column);
		}

		String cell = "";
		cellName.addAll(Arrays.asList("카테고리", "기초코드","상품", "등급"));
		for (int i = 1; i <= 31; i++) {
			cell = String.valueOf(i);
			cellName.add(cell);
		}

		try {
			excelCon.downExcelFile(response, dbConn2, QUERY_ROOT + ".stockDayZoneExcel", map, dbField, cellName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 재고 관리 전용 - 본부별 재고관리 - 본부별 재고
	@RequestMapping(value = "/headOffice")
	public ModelAndView headOffice(ModelAndView mv,HttpServletRequest request, Authentication authentication) throws Exception {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		if (((MemberDTO4) authentication.getPrincipal()).getUser_duty().equals("1") || ((MemberDTO4) authentication.getPrincipal()).getUser_duty().equals("2") || ((MemberDTO4) authentication.getPrincipal()).get_Id().equals("1")) {
			map.put("admin_id",((MemberDTO4) authentication.getPrincipal()).get_Id());
			mv.addObject("admin_id", ((MemberDTO4) authentication.getPrincipal()).get_Id());
		} else {
			map.put("userId",((MemberDTO4) authentication.getPrincipal()).get_Id());
			map.put("admin_id",dbConn.recordSet(QUERY_ROOT + ".getDeptId", map).get(0).toString());
			mv.addObject("admin_id", map.get("admin_id"));
		}

		map.put("yearValue", Common.defaultValue(request.getParameter("yearValue"),LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))));
		map.put("flag", Common.defaultValue(request.getParameter("flag"),"0"));

		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));

		if (Common.defaultValue(request.getParameter("flag"),"0").equals("1")) {
			mv.addObject("list", dbConn2.recordSet(QUERY_ROOT + ".headOfficeWeek", map));
		} else {
			mv.addObject("list", dbConn2.recordSet(QUERY_ROOT + ".headOffice", map).get(0));
			mv.addObject("list2", dbConn2.recordSet(QUERY_ROOT + ".headOffice", map).get(1));
		}

		mv.addObject("flag", Common.defaultValue(request.getParameter("flag"),"0"));
		mv.addObject("yearValue", Common.defaultValue(request.getParameter("yearValue"),LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))));
		mv.setViewName("stock/headOffice.tiles");

		return mv;
	}

	// 재고 관리 전용 - 본부별 재고관리 - 본부별 재고 상세화면
	@RequestMapping(value = "/headOfficeBrand")
	public ModelAndView headOfficeBrand(ModelAndView mv,HttpServletRequest request, Authentication authentication) throws Exception {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		if (((MemberDTO4) authentication.getPrincipal()).getUser_duty().equals("1") || ((MemberDTO4) authentication.getPrincipal()).getUser_duty().equals("2") || ((MemberDTO4) authentication.getPrincipal()).get_Id().equals("1")) {
			map.put("admin_id",((MemberDTO4) authentication.getPrincipal()).get_Id());
		} else {
			map.put("userId",((MemberDTO4) authentication.getPrincipal()).get_Id());
			map.put("admin_id",dbConn.recordSet(QUERY_ROOT + ".getDeptId", map).get(0).toString());
		}

		map.put("yearValue", Common.defaultValue(request.getParameter("yearValue"),LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))));
		mv.addObject("list", dbConn2.recordSet(QUERY_ROOT + ".headOfficeBrand", map));

		mv.addObject("yearValue", Common.defaultValue(request.getParameter("yearValue"),LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))));
		mv.addObject("monthValue", Common.defaultValue(request.getParameter("monthValue"),LocalDate.now().format(DateTimeFormatter.ofPattern("MM"))));
		mv.setViewName("stock/headOfficeBrand.tiles");
		return mv;
	}

	// 재고 관리 전용 - 본부별 재고관리 - 브랜드 분류
	@RequestMapping(value = "/brand")
	public ModelAndView brand(ModelAndView mv,HttpServletRequest request, Authentication authentication) throws Exception {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		map.put("admin_id",((MemberDTO4) authentication.getPrincipal()).get_Id());
		mv.addObject("list", dbConn.recordSet(QUERY_ROOT + ".brand", map));
		mv.setViewName("stock/brand.tiles");
		return mv;
	}

	// 재고 관리 전용 - 본부별 재고관리 - 브랜드 분류 - 등록
	@RequestMapping(value = "/brand_insert_DB.run")
	@ResponseBody
	public ModelAndView brand_insert(HttpServletRequest request, ModelAndView mv, Authentication authentication) {
		List<Map<String, String>> brandList = new ArrayList<>();

		for (int i=1; i<=3; i++) {
			String[] selectField = new String[]{"brand"+i};
			List<Map<String, String>> list = Common.paramToList(selectField, request.getParameterMap());
			brandList.addAll(list);
		}

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("brandList", brandList);
		paramMap.put("admin_id", ((MemberDTO4) authentication.getPrincipal()).get_Id());

		dbConn.recordSet(QUERY_ROOT + ".brandInsert", paramMap);
		mv.setViewName("redirect:/views/stock/brand");

		return mv;
	}

	// 재고 관리 전용 - 특이사항 - 기초상품 특이사항 등록 화면 - 기초상품 카테고리
	@RequestMapping(value = "/getImportantCategory.run", produces = "Application/json")
	@ResponseBody
	public Map<String, Object> getImportantCategory(HttpServletResponse response, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		Map<String, Object> returnParam = new HashMap<>();

		List<Map> list = dbConn.recordSet(QUERY_ROOT + ".importantCategory", map);

		returnParam.put("data", Common.nullToEmpty(list));
		return returnParam;
	}

	//출고량 > 그룹별 - 화면
	@RequestMapping(value = "/sales")
	public ModelAndView stockSales(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("display", Common.defaultValue(map.get(Common.paramIsArray("display", request)), "1"));
		map.put("marketMinus", Common.defaultValue(map.get(Common.paramIsArray("marketMinus", request)), ""));

		LocalDate now = LocalDate.now();
		LocalDate perWeek = now.minusWeeks(4).minusDays(LocalDate.now().get(ChronoField.DAY_OF_WEEK)+1);
		map.put("perWeeks", perWeek.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		map.put("perWeeke", perWeek.plusDays(6).format(DateTimeFormatter.ofPattern("MM-dd")));
		map.put("perWeek1s", perWeek.plusWeeks(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		map.put("perWeek1e", perWeek.plusWeeks(1).plusDays(6).format(DateTimeFormatter.ofPattern("MM-dd")));
		map.put("perWeek2s", perWeek.plusWeeks(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		map.put("perWeek2e", perWeek.plusWeeks(2).plusDays(6).format(DateTimeFormatter.ofPattern("MM-dd")));
		map.put("perWeek3s", perWeek.plusWeeks(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		map.put("perWeek3e", perWeek.plusWeeks(3).plusDays(6).format(DateTimeFormatter.ofPattern("MM-dd")));
		map.put("perWeek4s", perWeek.plusWeeks(4).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

		map.put("perMonth1", now.minusMonths(2).format(DateTimeFormatter.ofPattern("M")));
		map.put("perMonth2", now.minusMonths(1).format(DateTimeFormatter.ofPattern("M")));
		map.put("perMonth3", now.minusMonths(0).format(DateTimeFormatter.ofPattern("M")));
		map.put("perMonth4", now.minusMonths(-1).format(DateTimeFormatter.ofPattern("M")));

		List<Map<String, String>> list = dbConn2.recordSet(QUERY_ROOT + ".stockSales", map);
		String brandArray = "0";
		String checkBrand = "";
		for (int i = 0; i < list.size(); i++) {
			String brandCode = Common.defaultValue(String.valueOf(list.get(i).get("brand")), "0");
			if(!checkBrand.equals(brandCode)){
				brandArray += "," + brandCode;
				checkBrand = brandCode;
			}
		}
		map.put("brandArray", brandArray);

		map.put("monthCount", String.valueOf(now.plusMonths(1).lengthOfMonth()));
		map.put("addPlus", String.valueOf(Period.between(now, now.withDayOfMonth(now.lengthOfMonth())).getDays()));

		mv.addObject("list", list);
		mv.addObject("nowDate", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		mv.addObject("stockSalesDate", dbConn2.recordSet(QUERY_ROOT + ".stockSalesDate", map));
		mv.addObject("codeList", dbConn.recordSet(QUERY_ROOT + ".codeList", map));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("stock/sales.tiles");
		return mv;
	}

	//출고량 > 그룹별 - 화면
	@RequestMapping(value = "/sales_graph")
	public ModelAndView salesGraph(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		LocalDate now = LocalDate.now();
		LocalDate perWeek = now.minusWeeks(4).minusDays(LocalDate.now().get(ChronoField.DAY_OF_WEEK)+1);
		map.put("perWeeks", perWeek.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		map.put("perWeeke", perWeek.plusDays(6).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		map.put("perWeek1s", perWeek.plusWeeks(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		map.put("perWeek1e", perWeek.plusWeeks(1).plusDays(6).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		map.put("perWeek2s", perWeek.plusWeeks(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		map.put("perWeek2e", perWeek.plusWeeks(2).plusDays(6).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		map.put("perWeek3s", perWeek.plusWeeks(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		map.put("perWeek3e", perWeek.plusWeeks(3).plusDays(6).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		map.put("perWeek4s", perWeek.plusWeeks(4).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

		map.put("perMonth1", now.minusMonths(2).format(DateTimeFormatter.ofPattern("M")));
		map.put("perMonth2", now.minusMonths(1).format(DateTimeFormatter.ofPattern("M")));
		map.put("perMonth3", now.minusMonths(0).format(DateTimeFormatter.ofPattern("M")));
		map.put("perMonth4", now.minusMonths(-1).format(DateTimeFormatter.ofPattern("M")));

		map.put("m1", now.minusMonths(2).format(DateTimeFormatter.ofPattern("M")));
		map.put("m2", now.minusMonths(1).format(DateTimeFormatter.ofPattern("M")));
		map.put("m3", now.format(DateTimeFormatter.ofPattern("M")));

		map.put("monthCount", String.valueOf(now.plusMonths(1).lengthOfMonth()));
		map.put("addPlus", String.valueOf(Period.between(now, now.withDayOfMonth(now.lengthOfMonth())).getDays()));

		mv.addObject("list", dbConn2.recordSet(QUERY_ROOT + ".stockSalesGraph", map));
		mv.addObject("historyDate", dbConn2.recordSet(QUERY_ROOT + ".stockSalesHistoryDate", map));
		mv.addObject("codeList", dbConn.recordSet(QUERY_ROOT + ".codeList", map));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("stock/sales_graph.pq");
		return mv;
	}

	//출고량 > 그룹별 - 화면
	@RequestMapping(value = "/detail_view")
	public ModelAndView detailView(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		LocalDate wdate1 = LocalDate.parse(map.get("wdate"));
		String wdate2 = "";
		if("date".equals(map.get("only"))){
			wdate2 = map.get("wdate");
		}else{
			wdate2 = wdate1.plusDays(6).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}
		map.put("wdate2", wdate2);
		map.put("product_id", Common.defaultValue(map.get("product_id"), "0"));
		map.put("brand", Common.defaultValue(map.get("brand"), "0"));
		map.put("divide", Common.defaultValue(map.get("divide"), "orderGift"));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("stock/detail_view.pq");
		return mv;
	}

	//출고량 > 그룹별(월정산) - 리스트
	@RequestMapping(value = "/detail_view.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> detailView(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".detailView", map);
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//출고량 > 그룹별 - 화면
	@RequestMapping(value = "/sales_month")
	public ModelAndView stockSalesMonth(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("display", Common.defaultValue(map.get(Common.paramIsArray("display", request)), "1"));
		map.put("marketMinus", Common.defaultValue(map.get(Common.paramIsArray("marketMinus", request)), ""));
		map.put("yearV", Common.defaultValue(map.get("yearV"), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))));

		List<Map<String, String>> list = dbConn2.recordSet(QUERY_ROOT + ".stockSalesMonth", map);
		String brandArray = "0";
		String checkBrand = "";
		for (int i = 0; i < list.size(); i++) {
			String brandCode = Common.defaultValue(String.valueOf(list.get(i).get("brand")), "0");
			if(!checkBrand.equals(brandCode)){
				brandArray += "," + brandCode;
				checkBrand = brandCode;
			}
		}
		map.put("brandArray", brandArray);

		mv.addObject("list", list);
		mv.addObject("nowYear", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy")));
		mv.addObject("nowMonth", LocalDate.now().format(DateTimeFormatter.ofPattern("M")));
		mv.addObject("YearRange", dbConn2.recordSet(QUERY_ROOT + ".stockSalesMonthYearRange", map));
		mv.addObject("stockSalesDate", dbConn2.recordSet(QUERY_ROOT + ".stockSalesDate", map));
		mv.addObject("codeList", dbConn.recordSet(QUERY_ROOT + ".codeList", map));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("stock/sales_month.tiles");
		return mv;
	}

	//출고량 > 그룹별 - 화면
	@RequestMapping(value = "/sales_month_graph")
	public ModelAndView salesMonthGraph(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		mv.addObject("nowYear", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy")));
		mv.addObject("nowMonth", LocalDate.now().format(DateTimeFormatter.ofPattern("M")));
		mv.addObject("list", dbConn2.recordSet(QUERY_ROOT + ".stockSalesMonthGraph", map));
		mv.addObject("historyDate", dbConn2.recordSet(QUERY_ROOT + ".stockSalesHistoryDate", map));
		mv.addObject("codeList", dbConn.recordSet(QUERY_ROOT + ".codeList", map));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("stock/sales_month_graph.pq");
		return mv;
	}

	//출고량 > 그룹별 - 화면
	@RequestMapping(value = "/product")
	public ModelAndView stockProduct(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("brand", Common.defaultValue(map.get(Common.paramIsArray("brand", request)), "55"));
		map.put("marketMinus", Common.defaultValue(map.get(Common.paramIsArray("marketMinus", request)), ""));
		map.put("display", Common.defaultValue(map.get(Common.paramIsArray("display", request)), "1"));
		map.put("isproductbrnad", Common.defaultValue(map.get(Common.paramIsArray("isproductbrnad", request)), "0"));

		if(map.get("isproductbrnad").equals("1")){
			map.put("display", "0, 1");
		}

		LocalDate now = LocalDate.now();
		LocalDate perWeek = now.minusWeeks(4).minusDays(LocalDate.now().get(ChronoField.DAY_OF_WEEK)+1);
		map.put("perWeeks", perWeek.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		map.put("perWeeke", perWeek.plusDays(6).format(DateTimeFormatter.ofPattern("MM-dd")));
		map.put("perWeek1s", perWeek.plusWeeks(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		map.put("perWeek1e", perWeek.plusWeeks(1).plusDays(6).format(DateTimeFormatter.ofPattern("MM-dd")));
		map.put("perWeek2s", perWeek.plusWeeks(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		map.put("perWeek2e", perWeek.plusWeeks(2).plusDays(6).format(DateTimeFormatter.ofPattern("MM-dd")));
		map.put("perWeek3s", perWeek.plusWeeks(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		map.put("perWeek3e", perWeek.plusWeeks(3).plusDays(6).format(DateTimeFormatter.ofPattern("MM-dd")));
		map.put("perWeek4s", perWeek.plusWeeks(4).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

		map.put("perMonth1", now.minusMonths(2).format(DateTimeFormatter.ofPattern("M")));
		map.put("perMonth2", now.minusMonths(1).format(DateTimeFormatter.ofPattern("M")));
		map.put("perMonth3", now.minusMonths(0).format(DateTimeFormatter.ofPattern("M")));
		map.put("perMonth4", now.minusMonths(-1).format(DateTimeFormatter.ofPattern("M")));

		List<Map<String, String>> list = dbConn2.recordSet(QUERY_ROOT + ".stockProduct", map);
		String productIdArray = "0";
		for (int i = 0; i < list.size(); i++) {
			String productId = Common.defaultValue(String.valueOf(list.get(i).get("product_id")), "0");
			productIdArray += "," + productId;
		}
		map.put("productIdArray", productIdArray);

		map.put("monthCount", String.valueOf(now.plusMonths(1).lengthOfMonth()));
		map.put("addPlus", String.valueOf(Period.between(now, now.withDayOfMonth(now.lengthOfMonth())).getDays()+1));

		mv.addObject("list", list);
		mv.addObject("nowDate", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		mv.addObject("mdManageProduct", dbConn2.recordSet(QUERY_ROOT + ".mdManageProduct", map));
		mv.addObject("codeList", dbConn.recordSet(QUERY_ROOT + ".codeList", map));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("stock/product.tiles");
		return mv;
	}

	//출고량 > 그룹별 - 화면
	@RequestMapping(value = "/product_graph")
	public ModelAndView productGraph(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		map.put("marketMinus", Common.defaultValue(map.get("marketMinus"),""));
		map.put("divide", Common.defaultValue(map.get("divide"),""));

		LocalDate now = LocalDate.now();
		LocalDate perWeek = now.minusWeeks(4).minusDays(LocalDate.now().get(ChronoField.DAY_OF_WEEK)+1);
		map.put("perWeeks", perWeek.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		map.put("perWeeke", perWeek.plusDays(6).format(DateTimeFormatter.ofPattern("MM-dd")));
		map.put("perWeek1s", perWeek.plusWeeks(1).format(DateTimeFormatter.ofPattern("MM-dd")));
		map.put("perWeek1e", perWeek.plusWeeks(1).plusDays(6).format(DateTimeFormatter.ofPattern("MM-dd")));
		map.put("perWeek2s", perWeek.plusWeeks(2).format(DateTimeFormatter.ofPattern("MM-dd")));
		map.put("perWeek2e", perWeek.plusWeeks(2).plusDays(6).format(DateTimeFormatter.ofPattern("MM-dd")));
		map.put("perWeek3s", perWeek.plusWeeks(3).format(DateTimeFormatter.ofPattern("MM-dd")));
		map.put("perWeek3e", perWeek.plusWeeks(3).plusDays(6).format(DateTimeFormatter.ofPattern("MM-dd")));
		map.put("perWeek4s", perWeek.plusWeeks(4).format(DateTimeFormatter.ofPattern("MM-dd")));

		map.put("perMonth1", now.minusMonths(2).format(DateTimeFormatter.ofPattern("M")));
		map.put("perMonth2", now.minusMonths(1).format(DateTimeFormatter.ofPattern("M")));
		map.put("perMonth3", now.minusMonths(0).format(DateTimeFormatter.ofPattern("M")));
		map.put("perMonth4", now.minusMonths(-1).format(DateTimeFormatter.ofPattern("M")));

		map.put("m1", now.minusMonths(2).format(DateTimeFormatter.ofPattern("M")));
		map.put("m2", now.minusMonths(1).format(DateTimeFormatter.ofPattern("M")));
		map.put("m3", now.format(DateTimeFormatter.ofPattern("M")));

		map.put("monthCount", String.valueOf(now.plusMonths(1).lengthOfMonth()));
		map.put("addPlus", String.valueOf(Period.between(now, now.withDayOfMonth(now.lengthOfMonth())).getDays()));

		mv.addObject("list", dbConn.recordSet(QUERY_ROOT + ".stockProductGraph", map));
		mv.addObject("historyDate", dbConn2.recordSet(QUERY_ROOT + ".stockSalesHistoryDate", map));
		mv.addObject("codeList", dbConn.recordSet(QUERY_ROOT + ".codeList", map));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("stock/product_graph.pq");
		return mv;
	}

	//출고량 > 기초상품별(월정산) - 화면
	@RequestMapping(value = "/product_month")
	public ModelAndView stockProductMonth(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("brand", Common.defaultValue(map.get(Common.paramIsArray("brand", request)), "55"));
		map.put("marketMinus", Common.defaultValue(map.get(Common.paramIsArray("marketMinus", request)), ""));
		map.put("display", Common.defaultValue(map.get(Common.paramIsArray("display", request)), "1"));
		map.put("yearV", Common.defaultValue(map.get("yearV"), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))));

		List<Map<String, String>> list = dbConn2.recordSet(QUERY_ROOT + ".stockProductMonth", map);

		LocalDate now = LocalDate.now();
		map.put("monthCount", String.valueOf(now.plusMonths(1).lengthOfMonth()));

		mv.addObject("list", list);
		mv.addObject("nowYear", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy")));
		mv.addObject("nowMonth", LocalDate.now().format(DateTimeFormatter.ofPattern("M")));
		mv.addObject("YearRange", dbConn2.recordSet(QUERY_ROOT + ".stockSalesMonthYearRange", map));
		mv.addObject("historyDate", dbConn2.recordSet(QUERY_ROOT + ".stockSalesHistoryDate", map));
		mv.addObject("codeList", dbConn.recordSet(QUERY_ROOT + ".codeList", map));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("stock/product_month.tiles");
		return mv;
	}

	@RequestMapping(value = "/product_month_graph")
	public ModelAndView productMonthGraph(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		mv.addObject("nowYear", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy")));
		mv.addObject("nowMonth", LocalDate.now().format(DateTimeFormatter.ofPattern("M")));
		mv.addObject("list", dbConn2.recordSet(QUERY_ROOT + ".stockProductMonthGraph", map));
		mv.addObject("historyDate", dbConn2.recordSet(QUERY_ROOT + ".stockSalesHistoryDate", map));
		mv.addObject("codeList", dbConn.recordSet(QUERY_ROOT + ".codeList", map));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("stock/product_month_graph.pq");
		return mv;
	}

	//출고량 > 브랜드별(년정산) - 화면
	@RequestMapping(value = "/brand_month")
	public ModelAndView stockBrandMonth(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("yearV", Common.defaultValue(map.get("yearV"), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))));
		mv.addObject("list", dbConn2.recordSet(QUERY_ROOT + ".stockBrandMonth", map));
		mv.addObject("yearRange", dbConn2.recordSet(QUERY_ROOT + ".stockSalesMonthYearRange", map));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("stock/brand_month.pq");
		return mv;
	}

	// 재고관리 전용 - 본부별 재고관리 - 기초상품 CBM 관리
	@RequestMapping(value = "/product_cbm")
	public ModelAndView product_cbm(ModelAndView mv, HttpServletRequest request) {
		List<String> sqlItemList = new ArrayList<>();

		if (!Common.isNullOrEmpty(request.getParameterValues("codename"))) {
			sqlItemList.add(dbConn.makeSearchSql("upper(codename)", new String[]{request.getParameterValues("codename")[0].replace("-","")}, "like"));
		}
		sqlItemList.add(dbConn.makeSearchSql("code1", request.getParameterValues("code1"), "="));
		sqlItemList.add(dbConn.makeSearchSql("code2", request.getParameterValues("code2"), "="));
		sqlItemList.add(dbConn.makeSearchSql("name", request.getParameterValues("name"), "like"));
		sqlItemList.add(dbConn.makeSearchSql("id", request.getParameterValues("serialId"), "="));
		sqlItemList.add(dbConn.makeSearchSql("pbrand", request.getParameterValues("brand"), "="));
		sqlItemList.add("left(code1Code,1)<>'Z'");
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, Object> map = new HashMap<>();
		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("orderBy", "case when A.pbrand='n' then 2 else case when isnull(B.width,0)=0 or isnull(B.height,0)=0 or isnull(B.depth,0)=0 then 0 else 1 end end,code1,code2,code3,code4");
		map.put("totalRecords", Optional.ofNullable(request.getParameter("totalRecords")).orElse("0"));
		map.put("curPage", Common.defaultValue(request.getParameter("curPage"),"1"));
		map.put("pageSize", Optional.ofNullable(request.getParameter("pageSize")).orElse("50"));

		List<Map> list = dbConn.recordSet(QUERY_ROOT + ".productCbm", map);

		map.put("totalRecords", list.size() > 0 ? list.get(0).get("totalRecords") : 0);

		mv.addObject("list", list);
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.addObject("code", dbConn.recordSet(QUERY_ROOT + ".productCbmCode", null));
		mv.setViewName("stock/product_cbm.tiles");
		return mv;
	}

	// 재고관리 전용 - 본부별 재고관리 - 기초상품 CBM 관리 - 등록
	@RequestMapping(value = "/product_cbm_insert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView product_cbm_insert(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		String[] selectField = new String[]{"id","width","height","depth"};
		dbConn.recordSet(QUERY_ROOT + ".product_cbm_insert", Common.paramToList(selectField, request.getParameterMap()));
		mv.setViewName("redirect:/views/stock/product_cbm");
		return mv;
	}

	// 재고관리 전용 - 본부별 재고관리 - 기초상품 CBM 관리 - 상품사진
	@RequestMapping(value = "/getProductPhoto.run", produces = "Application/json")
	@ResponseBody
	public Map<String, Object> getProductPhoto(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		Map<String, Object> returnParam = new HashMap<>();

		List<Map> list = dbConn.recordSet(QUERY_ROOT + ".getProductPhoto", map);

		returnParam.put("data", Common.nullToEmpty(list));
		return returnParam;
	}
}
