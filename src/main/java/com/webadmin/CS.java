package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.common.ExcelCon;
import com.common.member.MemberDTO4;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/views/cs")
public class CS {

	private final String DIR_ROOT = "cs";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;

	@Resource(name = "dbConn2")
	private DbConn dbConn2;

	@Resource(name = "excelCon")
	private ExcelCon excelCon;

	// 문자발송 관리 - 리스트
	@RequestMapping(value = "/sms_list")
	public ModelAndView smsList(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		if (!Common.isNullOrEmpty(map.get("id"))) {
			List<Map> result = dbConn.recordSet(QUERY_ROOT + ".smsCheckId", map);
			mv.addObject("id", map.get("id"));
			mv.addObject("content", result.get(0).get("content"));
			mv.addObject("status", result.get(0).get("status"));
			mv.addObject("gubun", result.get(0).get("gubun"));
		}
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("cs/sms_list.pq");
		return mv;
	}

	// 문자발송 관리 - 리스트 json
	@RequestMapping(value = "/sms_list.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> smsList(HttpServletRequest request) {

		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn.makeSearchSql("content", request.getParameterValues("sms_content"), "like"));
		sqlItemList.add(dbConn.makeSearchSql("status", request.getParameterValues(Common.paramIsArray("sms_status", request)), "="));
		sqlItemList.add(dbConn.makeSearchSql("gubun", request.getParameterValues(Common.paramIsArray("gubun", request)), "="));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("orderBy", "id desc");
		Common.PQmap(map, request);

		result = dbConn.recordSet(QUERY_ROOT + ".smsList", map);

		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}

	// 문자발송 관리 - 등록
	@RequestMapping(value = "/sms_insert_DB.run", method = RequestMethod.POST)
	@Transactional
	@ResponseBody
	public Map<String, Object> smsInsertDB(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		Map resultMap = new HashMap();
		resultMap.put("recId", dbConn.recordSet(QUERY_ROOT + ".smsInsert", map).get(0).toString());
		return resultMap;
	}

	// 문자발송 관리 - 수정
	@RequestMapping(value = "/sms_update_DB.run", method = RequestMethod.POST)
	@Transactional
	@ResponseBody
	public Map<String, Object> smsUpdateDB(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		dbConn.recordSet(QUERY_ROOT + ".smsUpdate", map);
		Map resultMap = new HashMap();
		resultMap.put("result", "success");
		return resultMap;
	}

	// 문자발송 관리 - 삭제
	@RequestMapping(value = "/sms_delete_DB.run", method = RequestMethod.POST)
	@Transactional
	@ResponseBody
	public Map<String, Object> smsDeleteDB(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		dbConn.recordSet(QUERY_ROOT + ".smsDelete", map);
		Map resultMap = new HashMap();
		resultMap.put("result", "success");
		return resultMap;
	}

	// BlackList 관리
	@RequestMapping(value = "/blacklist")
	public ModelAndView blakclist(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		mv.addObject("blackListAlram", dbConn.recordSet(QUERY_ROOT + ".blackListAlram", map));
		mv.setViewName("cs/blacklist.tiles");
		return mv;
	}

	// BlackList 관리 - 유저 찾기
	@RequestMapping(value = "/searchMember.run", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> searchMember(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		List<Map> list = dbConn.recordSet(QUERY_ROOT + ".searchMember", map);
		Map resultMap = new HashMap();
		resultMap.put("rows", list);
		return resultMap;
	}

	@RequestMapping(value = "/blacklist_view.run", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> blacklistView(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		List<List<Map>> list = dbConn.recordSet(QUERY_ROOT + ".blacklistView", map);
		Map resultMap = new HashMap();
		resultMap.put("rows1", list.get(0));
		resultMap.put("rows2", list.get(1));
		return resultMap;
	}

	@RequestMapping(value = "/blacklist_insert_DB.run", method = RequestMethod.POST)
	@Transactional
	@ResponseBody
	public Map<String, Object> blacklistInsertDB(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		dbConn.recordSet(QUERY_ROOT + ".blacklistInsert", map);
		Map resultMap = new HashMap();
		resultMap.put("result", "success");
		return resultMap;
	}

	// BlackList 구매내역 - 리스트
	@RequestMapping(value = "/blacklist_order")
	public ModelAndView blacklistOrder(ModelAndView mv) {
		mv.setViewName("cs/blacklist_order.pq");
		return mv;
	}

	// BlackList 구매내역 - 리스트 json
	@RequestMapping(value = "/blacklist_order.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> blacklistOrder(HttpServletRequest request) {
		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", "");
		map.put("orderBy", "a.id desc");

		result = dbConn.recordSet(QUERY_ROOT + ".blacklistOrder", map);

		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}

	// BlackList 구매내역 - 품목
	@RequestMapping(value = "/blacklist_order_item.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> blacklistOrderItem(HttpServletRequest request) {
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(dbConn.recordSet(QUERY_ROOT + ".blacklistOrderItem", Common.paramToMap(request.getParameterMap()))));
		return resultMap;
	}

	// BlackList 구매내역 - 운송장
	@RequestMapping(value = "/blacklist_order_invoice.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> blacklistOrderInvoice(HttpServletRequest request) {
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(dbConn.recordSet(QUERY_ROOT + ".blacklistOrderInvoice", Common.paramToMap(request.getParameterMap()))));
		return resultMap;
	}

	// 문의유형 관리
	@RequestMapping(value = "/category")
	public ModelAndView category(ModelAndView mv) {
		mv.setViewName("cs/category.tiles");
		return mv;
	}

	// 문의유형 관리 등록/수정
	@RequestMapping(value = "/category_view.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> categoryView(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		Map resultMap = new HashMap();
		map.put("category_id", Common.defaultValue(map.get("category_id"), "0"));
		resultMap.put("data", Common.nullToEmpty(dbConn.recordSet(QUERY_ROOT + ".categoryView", map)));
		return resultMap;
	}

	// 문의유형 관리 등록/수정
	@RequestMapping(value = "/category_insert_DB.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> categoryInsertDB(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		Map resultMap = new HashMap();
		List<Map> result = new ArrayList<>();

		if (map.get("choice").equals("move")) {
			for (int i = 0; i <= map.get("category" + map.get("depth")).split(", ").length; i++) {
				map.put("sunser", String.valueOf(i + 1));
				map.put("category_id", map.get("category" + map.get("depth")).split(", ")[i]);
				dbConn.recordSet(QUERY_ROOT + ".categoryMove", map);
			}
		} else {
			if (!map.get("depth").equals("0")) {
				map.put("id", map.get("category" + map.get("depth")));
				map.put("CS_category_id", map.get("category" + (Integer.parseInt(map.get("depth")) - 1)));
			} else {
				map.put("id", map.get("category" + map.get("depth")));
				map.put("CS_category_id", "");
			}
			if (map.get("choice").equals("insert")) {
				result = dbConn.recordSet(QUERY_ROOT + ".categoryInsertDB", map);
			} else if (map.get("choice").equals("delete")) {
				dbConn.recordSet(QUERY_ROOT + ".categoryDeleteDB", map);
			} else {
				dbConn.recordSet(QUERY_ROOT + ".categoryUpdateDB", map);
			}
		}
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	// 문의유형 관리 등록/수정
	@RequestMapping(value = "/statics_insert_DB.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> staticsInsertDB(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		Map resultMap = new HashMap();
		List<Map> result = new ArrayList<>();

		if (map.get("choice").equals("statics")) {
			dbConn.recordSet(QUERY_ROOT + ".staticsInsertDB", map);
		} else {
			result = dbConn.recordSet(QUERY_ROOT + ".staticsSelect", map);
		}

		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}


	// 상담내역 등록(연고X)
	@RequestMapping(value = "/input")
	public ModelAndView input(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("category_id", Common.defaultValue(map.get("category_id"), "33"));
		map.put("admin_member_id", ((MemberDTO4) authentication.getPrincipal()).get_Id());
		map.put("id", Common.defaultValue(map.get("id"), ""));
		map.put("nowTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_hhmms")));

		List<List<Map>> list = dbConn.recordSet(QUERY_ROOT + ".input", map);
		List<List<Map>> categoryList = dbConn.recordSet(QUERY_ROOT + ".categoryList", map);

		mv.addObject("viewParam", map);
		mv.addObject("category1List", categoryList.get(0));
		mv.addObject("category2List", categoryList.get(1));
		mv.addObject("itemsList", list.get(0));
		mv.addObject("inputList", list.get(1));
		mv.addObject("customer", list.get(2));
		mv.addObject("point", list.get(3));
		if(!Common.isNullOrEmpty(map.get("choice"))){
			mv.setViewName("cs/input.bare");
		}else{
			mv.setViewName("cs/input.tiles");
		}
		return mv;
	}

	@RequestMapping(value = "/importantCnt.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> importantCnt(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(dbConn.recordSet(QUERY_ROOT + ".importantCnt", map)));
		return resultMap;
	}

	// 상담내역 등록(연고X) 등록
	@RequestMapping(value = "/insert_DB.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> insertDB(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		Map resultMap = new HashMap();
		map.put("saveRestMoney", Common.defaultValue(map.get("saveRestMoney"), "0"));
		map.put("savePoint", Common.defaultValue(map.get("savePoint"), "0"));

		if (map.get("brand").indexOf("$") > 0) {
			map.put("brand", Common.defaultValue(map.get("brand"), "$0$"));
		} else {
			map.put("brand", "$" + Common.defaultValue(map.get("brand"), "0").replace(", ", "$$") + "$");
		}

		if (map.get("choice").equals("insert") || map.get("choice").equals("insert2")) {
			List<Map<String, Integer>> memberCheck = dbConn.recordSet(QUERY_ROOT + ".memberCheck", map);
			if (!Common.isNullOrEmpty(map.get("coupon_ini_id")) || !map.get("saveRestMoney").equals("0") || !map.get("savePoint").equals("0")) {
				if (memberCheck.get(0).get("cnt") == 0) {
					resultMap.put("message", "존재하지 않는 회원입니다. 쿠폰/적립금/예치금을 발행할 수 없습니다.");
					return resultMap;
				}
			}

			if ("131,132,163,166,164,165,169,170,171,172".indexOf(map.get("CS_category_id")) != -1) {
				List<Map> jumunCheck = dbConn.recordSet(QUERY_ROOT + ".jumunCheck", map);
				if (Common.isNullOrEmpty(jumunCheck)) {
					resultMap.put("message", "본 상품은 발주가 진행되지 않았습니다.\n\r확인하시고 다시 진행해 주시기 바랍니다.");
					return resultMap;
				}
				map.put("content", "[최초주문일 : " + jumunCheck.get(0).get("wdate") + "] <a href='/views/pay/view_buy_2016?id=" + map.get("order_buy_ini_id") + "'>" + map.get("jumuncode") + "</a> / " + jumunCheck.get(0).get("name") + " / " + map.get("market_name") + " / " + jumunCheck.get(0).get("addr") + "<br><br>" + map.get("comment"));
				map.put("category_site_id", jumunCheck.get(0).get("category_site_id").toString());

				if ("131,164,170".indexOf(map.get("CS_category_id")) != -1) {
					map.put("subject", "[상담내역 연계] 송장 폐기요청");
				} else if ("132,165,171".indexOf(map.get("CS_category_id")) != -1) {
					map.put("subject", "[상담내역 연계] 검수 후 발주요청");
				} else if ("163,166,172".indexOf(map.get("CS_category_id")) != -1) {
					map.put("subject", "[상담내역 연계] 미출고 확인요청");
				} else if ("169".indexOf(map.get("CS_category_id")) != -1) {
					map.put("subject", "[상담내역 연계] CCTV 확인요청");
				}
			}

			if ("131,132,163,169".indexOf(map.get("CS_category_id")) != -1) {
				dbConn.recordSet(QUERY_ROOT + ".distributionRequest1", map);
			} else if ("170,171,172".indexOf(map.get("CS_category_id")) != -1) {
				dbConn.recordSet(QUERY_ROOT + ".distributionRequest2", map);
			} else if ("164,165,166".indexOf(map.get("CS_category_id")) != -1) {
				dbConn.recordSet(QUERY_ROOT + ".distributionRequest3", map);
			}

			if ("134,135,138".indexOf(map.get("CS_category_id")) != -1) {
				dbConn.recordSet(QUERY_ROOT + ".distributionRequest4", map);
			}

			if (Integer.parseInt(map.get("savePoint")) > 0) {
				map.put("comment", "[적립금 " + map.get("savePoint") + " 지급] " + map.get("comment"));
			}
			if (Integer.parseInt(map.get("saveRestMoney")) > 0) {
				map.put("comment", "[예치금 " + map.get("saveRestMoney") + " 지급] " + map.get("comment"));
			}
			List<Map<String, String>> customerId = dbConn.recordSet(QUERY_ROOT + ".customerInsert", map);
			map.put("CS_customer_id", String.valueOf(customerId.get(0).get("CS_customer_id")));

			List<Map<String, Integer>> importantCheck = dbConn.recordSet(QUERY_ROOT + ".importantCheck", map);
			if (importantCheck.get(0).get("cnt") == 0 && map.get("important").equals("y")) {
				dbConn.recordSet(QUERY_ROOT + ".importantInsert", map);
			}
			if (Integer.parseInt(map.get("savePoint")) > 0) {
				map.put("reason", "[적립금 지급 : "+ map.get("CS_customer_id") +"] "+ map.get("savePointTxt"));
				dbConn.recordSet(QUERY_ROOT + ".savePointInsert", map);
			}
			if (Integer.parseInt(map.get("saveRestMoney")) > 0) {
				map.put("reason", "[예치금 지급 : "+ map.get("CS_customer_id") +"] "+ map.get("saveRestMoneyTxt"));
				dbConn.recordSet(QUERY_ROOT + ".saveRestMoneyInsert", map);
			}
			if (map.get("choice").equals("insert2")) {
				resultMap.put("url", "/views/pay/customer_new_2016?order_buy_ini_id=" + map.get("order_buy_ini_id"));
			} else {
				if (Common.isNullOrEmpty(map.get("order_buy_ini_id"))) {
					resultMap.put("url", "list");
				}else{
					resultMap.put("url", "close");
				}
			}
		} else {
			dbConn.recordSet(QUERY_ROOT + ".customerUpdate", map);
			resultMap.put("url", "close");
		}
		return resultMap;
	}

	// 상담내역 등록
	@RequestMapping(value = "/list")
	public ModelAndView list(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("category_id", Common.defaultValue(map.get("category_id"), "33"));
		map.put("nowDate", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

		List<List<Map>> categoryList = dbConn.recordSet(QUERY_ROOT + ".categoryList", map);

		mv.addObject("viewParam", map);
		mv.addObject("category1List", categoryList.get(0));
		mv.addObject("category2List", categoryList.get(1));
		mv.addObject("deptCheck", dbConn.recordSet(QUERY_ROOT + ".deptCheck", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("cs/list.pq");
		return mv;
	}

	// 상담내역 등록 - 리스트 json
	@RequestMapping(value = "/list.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> list(HttpServletRequest request) {
		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn.makeSearchSql("CS_category_id", request.getParameterValues("CS_category_id"), "in"));
		sqlItemList.add(dbConn.makeSearchSql("process", request.getParameterValues(Common.paramIsArray("process", request)), "="));
		sqlItemList.add(dbConn.makeSearchSql("member_id", request.getParameterValues("member_id"), "like"));
		sqlItemList.add(dbConn.makeSearchSql("brand", request.getParameterValues(Common.paramIsArray("brand", request)), "like"));
		sqlItemList.add(dbConn.makeSearchSql("jumuncode", request.getParameterValues("jumuncode"), "="));
		sqlItemList.add(dbConn.makeSearchSql("important", request.getParameterValues("important"), "="));
		sqlItemList.add(dbConn.makeSearchSql("admin_member_id", request.getParameterValues("admin_member_id"), "="));
		sqlItemList.add(dbConn.makeSearchSql("market", request.getParameterValues(Common.paramIsArray("market", request)), "in"));
		sqlItemList.add(dbConn.makeSearchSql("comment", request.getParameterValues("comment"), "like"));
		sqlItemList.add(dbConn.makeSearchSqlRange("convert(varchar(10),wdate,126)", request.getParameter("wdate1"), request.getParameter("wdate2")));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		map.put("category_id", Common.defaultValue(map.get("category_id"), "33"));
		List<Map> result = new ArrayList<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("orderBy", "id desc");
		Common.PQmap(map, request);

		result = dbConn.recordSet(QUERY_ROOT + ".list", map);

		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	// 상담내역 등록 - 엑셀다운로드
	@RequestMapping(value = "/list_Excel.run")
	public void downExcel(HttpServletResponse response, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn.makeSearchSql("CS_category_id", request.getParameterValues("CS_category_id"), "in"));
		sqlItemList.add(dbConn.makeSearchSql("process", request.getParameterValues(Common.paramIsArray("process", request)), "="));
		sqlItemList.add(dbConn.makeSearchSql("member_id", request.getParameterValues("member_id"), "like"));
		sqlItemList.add(dbConn.makeSearchSql("brand", request.getParameterValues(Common.paramIsArray("brand", request)), "like"));
		sqlItemList.add(dbConn.makeSearchSql("jumuncode", request.getParameterValues("jumuncode"), "="));
		sqlItemList.add(dbConn.makeSearchSql("important", request.getParameterValues("important"), "="));
		sqlItemList.add(dbConn.makeSearchSql("admin_member_id", request.getParameterValues("admin_member_id"), "="));
		sqlItemList.add(dbConn.makeSearchSql("market", request.getParameterValues(Common.paramIsArray("market", request)), "in"));
		sqlItemList.add(dbConn.makeSearchSql("comment", request.getParameterValues("comment"), "like"));
		sqlItemList.add(dbConn.makeSearchSqlRange("convert(varchar(10),wdate,126)", request.getParameter("wdate1"), request.getParameter("wdate2")));
		sqlItemList.removeAll(Collections.singleton(null));

		map.put("category_id", Common.defaultValue(map.get("category_id"), "33"));
		map.put("sqlItem", String.join(" and ", sqlItemList));

		List<String> dbField = Arrays.asList("subject", "market", "jumuncode", "member_id", "username", "comment", "user_name", "wdate", "process");
		List<String> cellName = Arrays.asList("문의유형", "마켓", "주문번호", "주문자", "주문자이름", "내용", "작성자", "작성일", "처리");
		try {
			excelCon.downExcelFile(response, dbConn, QUERY_ROOT + ".itemListExcelDown", map, dbField, cellName);
		} catch (IOException e) {
		}
	}


	// 반품/교환내역
	@RequestMapping(value = "/change_list")
	public ModelAndView changeList(ModelAndView mv, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();

		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn.makeSearchSql("code1", request.getParameterValues(Common.paramIsArray("code1", request)), "in"));
		sqlItemList.add(dbConn.makeSearchSql("process", request.getParameterValues("process"), "="));
		sqlItemList.add(dbConn.makeSearchSql("member_id", request.getParameterValues("member_id"), "like"));
		sqlItemList.add(dbConn.makeSearchSql("jumuncode", request.getParameterValues("jumuncode"), "="));
		sqlItemList.add(dbConn.makeSearchSql("admin_member_id", request.getParameterValues("admin_member_id"), "="));
		sqlItemList.add(dbConn.makeSearchSql("market", request.getParameterValues(Common.paramIsArray("market", request)), "in"));
		sqlItemList.add(dbConn.makeSearchSql("comment", request.getParameterValues("comment"), "like"));
		sqlItemList.add(dbConn.makeSearchSqlRange("convert(varchar(10),wdate,126)", Common.defaultValue(request.getParameter("wdate1"), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))), Common.defaultValue(request.getParameter("wdate2"), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))));
		sqlItemList.removeAll(Collections.singleton(null));

		String sqlStr = "";

		if (!Common.isNullOrEmpty(map.get("code1name"))) {
			sqlStr = " and isnull(code1name,'')<>'' ";
		}

		map.put("sqlItem", String.join(" and ", sqlItemList) + sqlStr);
		map.put("orderBy", "id desc");
		map.put("category_id", Common.defaultValue(request.getParameter("category_id"), "33"));
		map.put("totalRecords", Common.defaultValue(request.getParameter("totalRecords"), "0"));
		map.put("curPage", Common.defaultValue(request.getParameter("curPage"), "1"));
		map.put("pageSize", Common.defaultValue(request.getParameter("pageSize"), "23"));
		map.put("nowDate", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

		List<List<Map>> categoryList = dbConn.recordSet(QUERY_ROOT + ".categoryList", map);
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".changeList", map);

		map.put("totalRecords", result.size() > 0 ? result.get(0).get("totalRecords") : 0);

		mv.addObject("list", Common.nullToEmpty(result));
		mv.addObject("category1List", categoryList.get(0));
		mv.addObject("category2List", categoryList.get(1));
		mv.addObject("deptCheck", dbConn.recordSet(QUERY_ROOT + ".deptCheck", null));
		mv.addObject("productCode", dbConn.recordSet(QUERY_ROOT + ".productCode", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("cs/change_list.tiles");
		return mv;
	}

	@RequestMapping(value = "/get_return_ini_id.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> getReturnIniId(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(dbConn.recordSet(QUERY_ROOT + ".getReturnIniId", map)));
		return resultMap;
	}

	// 반품/교환내역 - 엑셀다운로드
	@RequestMapping(value = "/change_list_Excel.run")
	public void changeListExcel(HttpServletResponse response, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn.makeSearchSql("CS_category_id", request.getParameterValues("CS_category_id"), "in"));
		sqlItemList.add(dbConn.makeSearchSql("process", request.getParameterValues(Common.paramIsArray("process", request)), "="));
		sqlItemList.add(dbConn.makeSearchSql("member_id", request.getParameterValues("member_id"), "like"));
		sqlItemList.add(dbConn.makeSearchSql("brand", request.getParameterValues(Common.paramIsArray("brand", request)), "like"));
		sqlItemList.add(dbConn.makeSearchSql("jumuncode", request.getParameterValues("jumuncode"), "="));
		sqlItemList.add(dbConn.makeSearchSql("important", request.getParameterValues("important"), "="));
		sqlItemList.add(dbConn.makeSearchSql("admin_member_id", request.getParameterValues("admin_member_id"), "="));
		sqlItemList.add(dbConn.makeSearchSql("market", request.getParameterValues(Common.paramIsArray("market", request)), "in"));
		sqlItemList.add(dbConn.makeSearchSql("comment", request.getParameterValues("comment"), "like"));
		sqlItemList.add(dbConn.makeSearchSqlRange("convert(varchar(10),wdate,126)", request.getParameter("wdate1"), request.getParameter("wdate2")));
		sqlItemList.removeAll(Collections.singleton(null));

		map.put("category_id", Common.defaultValue(map.get("category_id"), "33"));
		map.put("sqlItem", String.join(" and ", sqlItemList));

		List<String> dbField = Arrays.asList("subject", "market", "jumuncode", "member_id", "username", "comment", "user_name", "wdate", "process");
		List<String> cellName = Arrays.asList("문의유형", "마켓", "주문번호", "주문자", "주문자이름", "내용", "작성자", "작성일", "처리");
		try {
			excelCon.downExcelFile(response, dbConn, QUERY_ROOT + ".changeListExcelDown", map, dbField, cellName);
		} catch (IOException e) {
		}
	}

	// 반품/교환내역
	@RequestMapping(value = "/statics")
	public ModelAndView statistics(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("category_id", Common.defaultValue(map.get("category_id"), "33"));
		map.put("yearV", Common.defaultValue(map.get("yearV"), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))));
		map.put("monthV", Common.defaultValue(map.get("monthV"), LocalDate.now().format(DateTimeFormatter.ofPattern("MM"))));
		map.put("divide", Common.defaultValue(map.get("divide"), "1"));

		mv.addObject("categoryList", dbConn.recordSet(QUERY_ROOT + ".getCategory", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("cs/statics.pq");
		return mv;
	}

	// 반품/교환내역 - 리스트 json
	@RequestMapping(value = "/statics.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> statistics(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(dbConn.recordSet(QUERY_ROOT + ".statics", map)));
		return resultMap;
	}

	// CS->MD 긴급요청
	@RequestMapping(value = "/market_list")
	public ModelAndView marketList(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		if (!Common.isNullOrEmpty(map.get("market"))) {
			map.put("market", Common.addString("@", map.get("market").replace(", ", "@, @"), "@"));
		}

		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("cs/market_list.pq");
		return mv;
	}

	// CS->MD 긴급요청 - 리스트 json
	@RequestMapping(value = "/market_list.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> marketList(HttpServletRequest request) {
		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn.makeSearchSql("subject", request.getParameterValues("subject"), "like"));
		sqlItemList.add(dbConn.makeSearchSql("user_name", request.getParameterValues("user_name"), "like"));
		sqlItemList.add(dbConn.makeSearchSql("req_ok", request.getParameterValues(Common.paramIsArray("req_ok", request)), "="));
		sqlItemList.add(dbConn.makeSearchSql("market", request.getParameterValues(Common.paramIsArray("market", request)), "="));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("orderBy", "id desc");
		Common.PQmap(map, request);

		result = dbConn.recordSet(QUERY_ROOT + ".marketList", map);

		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}

	// CS->MD 긴급요청 View
	@RequestMapping(value = "/market_input")
	public ModelAndView marketInput(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		List<List<Map>> view = new ArrayList<>();
		if (!Common.isNullOrEmpty(map.get("id"))) {
			view = dbConn.recordSet(QUERY_ROOT + ".marketView", map);
			mv.addObject("view", view.get(0));
			mv.addObject("result", view.get(1));
		}

		mv.addObject("returnParam", map);
		mv.setViewName("cs/market_input.tiles");
		return mv;
	}

	// CS->MD 긴급요청 - 등록
	@RequestMapping(value = "/market_insert_DB.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView marketInsertDB(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("writer", ((MemberDTO4) authentication.getPrincipal()).get_Id());
		if (map.get("choice").equals("comment")) {
			dbConn.recordSet(QUERY_ROOT + ".commnetInsert", map);
		} else if (map.get("choice").equals("insert")) {
			dbConn.recordSet(QUERY_ROOT + ".marketInsert", map);
		} else if (map.get("choice").equals("correct")) {
			dbConn.recordSet(QUERY_ROOT + ".marketUpdate", map);
		} else if (map.get("choice").equals("cancel")) {
			dbConn.recordSet(QUERY_ROOT + ".marketCancel", map);
		}
		mv.setViewName("redirect:/views/cs/market_list");
		return mv;
	}

	// CS->MD 긴급요청
	@RequestMapping(value = "/money_list")
	public ModelAndView moneyList(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("cs/money_list.pq");
		return mv;
	}

	// CS->MD 긴급요청 - 리스트 json
	@RequestMapping(value = "/money_list.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> moneyList(HttpServletRequest request) {
		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn.makeSearchSql("content", request.getParameterValues("content"), "like"));
		sqlItemList.add(dbConn.makeSearchSql("user_name", request.getParameterValues("user_name"), "like"));
		sqlItemList.add(dbConn.makeSearchSql("req_ok", request.getParameterValues(Common.paramIsArray("req_ok", request)), "="));
		sqlItemList.add(dbConn.makeSearchSql("CS_category_id", request.getParameterValues(Common.paramIsArray("CS_category_id", request)), "="));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("orderBy", "id desc");
		Common.PQmap(map, request);

		result = dbConn.recordSet(QUERY_ROOT + ".moneyList", map);

		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}

	// CS->MD 긴급요청 - 등록
	@RequestMapping(value = "/money_list_insert_DB.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView moneyListInsertDB(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("admin_member_id", ((MemberDTO4) authentication.getPrincipal()).get_Id());
		dbConn.recordSet(QUERY_ROOT + ".moneyUpdate", map);
		mv.setViewName("redirect:/views/cs/money_list");
		return mv;
	}


	// CS->다수 확인요청
	@RequestMapping(value = "/multi_list")
	public ModelAndView multiList(ModelAndView mv, HttpServletRequest request) {
		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn2.makeSearchSql("subject", request.getParameterValues("subject"), "like"));
		sqlItemList.add(dbConn2.makeSearchSql("writer", request.getParameterValues("writer"), "like"));
		sqlItemList.add(dbConn2.makeSearchSql("req_status", request.getParameterValues(Common.paramIsArray("req_status", request)), "="));
		sqlItemList.add(dbConn2.makeSearchSql("req_gubun", request.getParameterValues(Common.paramIsArray("req_gubun", request)), "="));
		sqlItemList.add(dbConn2.makeSearchSql("team", request.getParameterValues(Common.paramIsArray("team", request)), "="));
		sqlItemList.add(dbConn2.makeSearchSqlRange("convert(varchar(10),wdate,126)", request.getParameter("wdate1"), request.getParameter("wdate2")));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, Object> map = new HashMap<>();

		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("orderBy", "sort, id desc");
		map.put("totalRecords", Common.defaultValue(request.getParameter("totalRecords"), "0"));
		map.put("curPage", Common.defaultValue(request.getParameter("curPage"), "1"));
		map.put("pageSize", Common.defaultValue(request.getParameter("pageSize"), "23"));

		List<Map> result = dbConn2.recordSet(QUERY_ROOT + ".multiList", map);

		map.put("totalRecords", result.size() > 0 ? result.get(0).get("totalRecords") : 0);

		mv.addObject("list", Common.nullToEmpty(result));
		mv.addObject("teamList", dbConn.recordSet(QUERY_ROOT + ".teamList", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("cs/multi_list.tiles");
		return mv;
	}

	@RequestMapping(value = "/get_team_list.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> getTeamList(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(dbConn2.recordSet(QUERY_ROOT + ".getTeamList", map)));
		return resultMap;
	}

	// CS->다수 확인요청 View
	@RequestMapping(value = "/multi_input")
	public ModelAndView multiInput(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		if (!Common.isNullOrEmpty(map.get("id"))) {
			mv.addObject("view", dbConn2.recordSet(QUERY_ROOT + ".multiView", map));
		}
		List<List<Map>> list = dbConn2.recordSet(QUERY_ROOT + ".multiArr", map);

		mv.addObject("teamList", dbConn.recordSet(QUERY_ROOT + ".teamList", null));
		mv.addObject("itemList", list.get(0));
		mv.addObject("replyList", list.get(1));
		mv.addObject("requestList", list.get(2));
		mv.addObject("getTeam", list.get(3));
		mv.addObject("returnParam", map);
		mv.setViewName("cs/multi_input.tiles");
		return mv;
	}

	// CS->다수 확인요청 - 등록
	@RequestMapping(value = "/multi_insert_DB.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView multiInsertDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("emergency", Common.defaultValue(map.get("emergency"), "n"));
		String[] selectField = new String[]{"team"};
		List<Map<String, String>> list = Common.paramToList(selectField, request.getParameterMap());

		Map<String, Object> totalMap = new HashMap<>();
		totalMap.put("list", list);
		totalMap.put("param", map);

		if (map.get("choice").equals("complete")) {
			dbConn2.recordSet(QUERY_ROOT + ".multiComplete", map);
			mv.setViewName("redirect:/views/cs/multi_list");
		} else if (map.get("choice").equals("cancel")) {
			dbConn2.recordSet(QUERY_ROOT + ".multiCancel", map);
			mv.setViewName("redirect:/views/cs/multi_list");
		} else if (map.get("choice").equals("insert")) {
			dbConn2.recordSet(QUERY_ROOT + ".multiInsert", totalMap);
			mailSend(map.get("id"), map.get("subject"), map.get("emergency"));
			mv.setViewName("redirect:/views/cs/multi_list");
		} else if (map.get("choice").equals("correct")) {
			dbConn2.recordSet(QUERY_ROOT + ".multiUpdate", totalMap);
			mv.setViewName("redirect:/views/cs/multi_input?id=" + map.get("id"));
		} else {
			List<Map<String, String>> maxDepth = dbConn2.recordSet(QUERY_ROOT + ".multiReplyMaxDepth", map);
			String new_depth = "";
			if (Common.isNullOrEmpty(maxDepth.get(0).get("max_depth"))) {
				new_depth = map.get("depth") + "a";
			} else {
				new_depth = maxDepth.get(0).get("max_depth");
				char str = new_depth.substring((maxDepth.get(0).get("max_depth").length() - 1)).charAt(0);
				int asc = str;
				int ascplus = asc + 1;

				if (asc == 90) {
					new_depth = map.get("depth") + "a";
				} else {
					new_depth = map.get("depth") + ((char) ascplus);
				}
			}
			map.put("max_depth", new_depth);
			dbConn2.recordSet(QUERY_ROOT + ".multiReplyInsert", map);
			mv.setViewName("redirect:/views/cs/multi_input?id=" + map.get("CS_request_id"));
		}
		return mv;
	}

	// 키위 정품등록 관리 - 리스트
	@RequestMapping(value = "/product_serial_list")
	public ModelAndView productSerialList(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("searchParam", Common.paramToMap(request.getParameterMap()));
		mv.setViewName("cs/product_serial_list.pq");
		return mv;
	}

	// 키위 정품등록 관리 - 리스트 json
	@RequestMapping(value = "/product_serial_list.run", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> productSerialList(HttpServletRequest request) {
		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn.makeSearchSql("product_serial", request.getParameterValues("product_serial"), "like"));
		sqlItemList.add(dbConn.makeSearchSql("username", request.getParameterValues("username"), "like"));
		sqlItemList.add(dbConn.makeSearchSql("userid", request.getParameterValues("userid"), "like"));
		sqlItemList.add(dbConn.makeSearchSqlRange("rdate", request.getParameter("rdate1"), request.getParameter("rdate2")));
		if ("y".equals(request.getParameter("isRegister"))) {
			sqlItemList.add("username is not null");
		}

		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", String.join(" and ", sqlItemList));
		if ("y".equals(request.getParameter("isRegister"))) {
			map.put("orderBy", "trdate desc");
		} else {
			map.put("orderBy", "id desc");
		}
		Common.PQmap(map, request);

		result = dbConn.recordSet(QUERY_ROOT + ".productSerialList", map);

		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	// 키위 정품등록 관리 - 상세
	@RequestMapping(value = "/product_serial_input")
	public ModelAndView productSerialInput(ModelAndView mv, HttpServletRequest request) {
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".productSerialInput", Common.paramToMap(request.getParameterMap()));
		mv.addObject("resultMap", result);
		mv.setViewName("cs/product_serial_input.tiles");
		return mv;
	}

	// 키위 정품등록 관리 - 수정
	@RequestMapping(value = "/product_serial_update.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView productSerialUpdate(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		dbConn.recordSet(QUERY_ROOT + ".historyInsert", map);
		dbConn.recordSet(QUERY_ROOT + ".productSerialUpdate", map);
		mv.setViewName("redirect:/views/cs/product_serial_list");
		return mv;
	}

	// 키위 정품등록 관리 - 취소
	@RequestMapping(value = "/product_serial_cancel.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView productSerialCancel(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		dbConn.recordSet(QUERY_ROOT + ".historyInsert", map);
		dbConn.recordSet(QUERY_ROOT + ".productSerialCancel", map);
		mv.setViewName("redirect:/views/cs/product_serial_list");
		return mv;
	}

	//지정 원가 관리 - 등록
	@RequestMapping(value = "/excelInsert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView excelInsert(ModelAndView mv, HttpServletRequest request) {
		MultipartHttpServletRequest mr = (MultipartHttpServletRequest) request;

		try {
			List<String> dbField = Arrays.asList("file1", "file2", "file3", "file4");
			List<Map<String, String>> addValue = excelCon.uploadExcel(mr.getFiles("upload_file"), dbField, null);
			List<Map> result = new ArrayList<>();
			for (int i = 0; i < addValue.size(); i++) {
				String file1 = addValue.get(i).get("file1");
				String file2 = addValue.get(i).get("file2");
				String file3 = addValue.get(i).get("file3");
				String file4 = addValue.get(i).get("file4");
				result.add(new HashMap() {{
					put("product_serial", file1);
					put("product_id", file2);
					put("production_yymm", file3);
					put("import_yymm", file4);
				}});
			}

			dbConn.recordSet(QUERY_ROOT + ".excelInsert", result);
		} catch (IOException e) {
		}

		mv.setViewName("redirect:/views/cs/product_serial_list");
		return mv;
	}

	public void mailSend(String id, String subject, String emergency) {
		Map<String, Object> map = new HashMap<>();
		map.put("id", id);
		map.put("subject", "[알림] CS팀에서 확인요청이 들어왔습니다.");
		map.put("content", "CS팀에서 확인요청이 들어왔습니다.<br>확인해 보시고 답변 바랍니다.<br><br>감사합니다.<br><br>프로그램 위치 : CS 전용 > CS->다수 확인요청<br><b>요청명</b> : " + subject);

		if (emergency.equals("y")) {
			map.put("content", "CS팀에서 긴급 확인요청이 들어왔습니다.<br>확인 바랍니다.<br><br>감사합니다.<br><br>프로그램 위치 : CS 전용 > CS->다수 확인요청<br><b>요청명</b> : " + subject);
		}

		List<Map<String, String>> mailSend = dbConn2.recordSet(QUERY_ROOT + ".mailSend", map);
	}
}

