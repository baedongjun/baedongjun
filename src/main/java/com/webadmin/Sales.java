package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.common.ExcelCon;
import com.common.member.MemberDTO4;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
@RequestMapping("/views/sales")
public class Sales {

	private final String DIR_ROOT = "sales";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;
	@Resource(name = "dbConn2")
	private DbConn dbConn2;
	@Resource(name = "excelCon")
	private ExcelCon excelCon;

	//B2B 공헌이익 > 정산금액 입력 - 화면
	@RequestMapping(value = "/input_money")
	public ModelAndView inputMoney(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("marketList", dbConn.recordSet(QUERY_ROOT + ".marketList", null));
		mv.setViewName("sales/input_money.pq");
		return mv;
	}

	//B2B 공헌이익 > 정산금액 입력 - 리스트
	@RequestMapping(value = "/input_money.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> inputMoney(HttpServletRequest request) {
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".inputMoney", null);
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//B2B 공헌이익 > 정산금액 입력 - 등록
	@RequestMapping(value = "/input_money_insert_DB.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public ModelAndView inputMoneyInsertDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		MultipartHttpServletRequest mr = (MultipartHttpServletRequest) request;

		for (int i = 0; i < mr.getFiles("addFile").size(); i++) {

		}

		Map<String, Object> totalMap = new HashMap<>();
		try {
			List<String> dbField = Arrays.asList("jumuncode", "money");
			List<Map<String, String>> list = excelCon.uploadExcel(mr.getFiles("addFile"), dbField, "주문번호");
			totalMap.put("itemIn", list);
		} catch (IOException e) {}


		map.put("market", Common.defaultValue(map.get(Common.paramIsArray("market", request)), ""));
		if (Common.isNullOrEmpty(map.get("market"))) {

			dbConn.recordSet(QUERY_ROOT + ".inputMoneyInsertDB", map);
		}
		mv.setViewName("sales/input_money.pq");
		return mv;
	}

	//연도별 기초상품 공헌이익 - 화면
	@RequestMapping(value = "/perMallYear")
	public ModelAndView perMallYear(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("codeGroupList", dbConn.recordSet(QUERY_ROOT + ".codeGroupList", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("sales/perMallYear.pq");
		return mv;
	}

	//연도별 기초상품 공헌이익 - 리스트
	@RequestMapping(value = "/perMallYear.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> perMallYear(HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("user_brand", ((MemberDTO4) authentication.getPrincipal()).getUser_brand().replaceAll("\\]\\[", ",").replaceAll("\\[", "").replaceAll("\\]", ""));
		map.put("codeGroup", map.get(Common.paramIsArray("codeGroup", request)));
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".perMallYear", map);
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//월별 재고금액 - 화면
	@RequestMapping(value = "/distribution")
	public ModelAndView distribution(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("codeGroupList", dbConn.recordSet(QUERY_ROOT + ".codeGroupList", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("sales/distribution.pq");
		return mv;
	}

	//월별 재고금액 - 리스트
	@RequestMapping(value = "/distribution.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> distribution(HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("user_brand", ((MemberDTO4) authentication.getPrincipal()).getUser_brand().replaceAll("\\]\\[", ",").replaceAll("\\[", "").replaceAll("\\]", ""));
		map.put("codeGroup", map.get(Common.paramIsArray("codeGroup", request)));
		List<Map> result = dbConn2.recordSet(QUERY_ROOT + ".distribution", map);
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	@RequestMapping(value = "/distributionExcelDown.run", produces = "application/json")
	public void distributionDownExcel(HttpServletResponse response, HttpServletRequest request, Authentication authentication) {
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

	//월별 증정 내역 - 화면
	@RequestMapping(value = "/gift")
	public ModelAndView gift(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("market", Common.defaultValue(map.get(Common.paramIsArray("market", request)), ""));
		map.put("yearValue", Common.defaultValue(map.get("yearValue"), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))));
		map.put("monthValue", Common.defaultValue(map.get("monthValue"), LocalDate.now().format(DateTimeFormatter.ofPattern("MM"))));

		mv.addObject("giftList", dbConn.recordSet(QUERY_ROOT + ".giftGroupList", map));
		mv.addObject("giftSubList", dbConn.recordSet(QUERY_ROOT + ".giftList", map));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("sales/gift.tiles");
		return mv;
	}


	//월별 증정 세부 내역 - 화면
	@RequestMapping(value = "/gift_detail")
	public ModelAndView giftDetail(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("market", Common.defaultValue(map.get(Common.paramIsArray("market", request)), ""));
		map.put("yearValue", Common.defaultValue(map.get("yearValue"), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))));
		map.put("monthValue", Common.defaultValue(map.get("monthValue"), LocalDate.now().format(DateTimeFormatter.ofPattern("MM"))));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("sales/gift_detail.pq");
		return mv;
	}

	//월별 증정 세부 내역 - 리스트
	@RequestMapping(value = "/gift_detail.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> giftDetail(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("market", Common.defaultValue(map.get(Common.paramIsArray("market", request)), ""));
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".giftDetailList", map);
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//몰별 공헌이익 - 화면
	@RequestMapping(value = "/perMall")
	public ModelAndView perMall(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("market", Common.defaultValue(map.get(Common.paramIsArray("market", request)), "1"));
		map.put("yearValue", Common.defaultValue(map.get("yearValue"), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("sales/perMall.pq");
		return mv;
	}

	//몰별 공헌이익 - 리스트
	@RequestMapping(value = "/perMall.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> perMall(HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("market", Common.defaultValue(map.get(Common.paramIsArray("market", request)), ""));
		map.put("user_brand", ((MemberDTO4) authentication.getPrincipal()).getUser_brand().replaceAll("\\]\\[", ",").replaceAll("\\[", "").replaceAll("\\]", ""));
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".perMall", map);
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//몰별 공헌이익 - 화면
	@RequestMapping(value = "/perMall2")
	public ModelAndView perMall2(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("returnParam", Common.paramToMap(request.getParameterMap()));
		mv.setViewName("sales/perMall2.pq");
		return mv;
	}

	//몰별 공헌이익2 - 리스트
	@RequestMapping(value = "/perMall2.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> perMall2(HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("user_brand", ((MemberDTO4) authentication.getPrincipal()).getUser_brand().replaceAll("\\]\\[", ",").replaceAll("\\[", "").replaceAll("\\]", ""));
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".perMall2", map);
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//몰별 공헌이익 - 화면
	@RequestMapping(value = "/perMall3")
	public ModelAndView perMall3(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", Common.paramToMap(request.getParameterMap()));
		mv.setViewName("sales/perMall3.pq");
		return mv;
	}

	//몰별 공헌이익3 - 리스트
	@RequestMapping(value = "/perMall3.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> perMall3(HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("user_brand", ((MemberDTO4) authentication.getPrincipal()).getUser_brand().replaceAll("\\]\\[", ",").replaceAll("\\[", "").replaceAll("\\]", ""));
		List<Map> result = new ArrayList<>();

		if (Common.defaultValue(map.get("gubun"), "1").equals("1")) {
			result = dbConn.recordSet(QUERY_ROOT + ".perMall3Pack", map);
		} else {
			result = dbConn.recordSet(QUERY_ROOT + ".perMall3Product", map);
		}

		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//브랜드별 공헌이익 - 화면
	@RequestMapping(value = "/perBrand")
	public ModelAndView perBrand(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("market", Common.defaultValue(map.get(Common.paramIsArray("market", request)), "1"));
		map.put("category_site_id", Common.defaultValue(map.get(Common.paramIsArray("category_site_id", request)), "55"));
		map.put("yearValue", Common.defaultValue(map.get("yearValue"), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("sales/perBrand.pq");
		return mv;
	}

	//브랜드별 공헌이익 - 리스트
	@RequestMapping(value = "/perBrand.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> perBrand(HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("market", Common.defaultValue(map.get(Common.paramIsArray("market", request)), ""));
		map.put("category_site_id", Common.defaultValue(map.get(Common.paramIsArray("category_site_id", request)), ""));
		map.put("user_brand", ((MemberDTO4) authentication.getPrincipal()).getUser_brand().replaceAll("\\]\\[", ",").replaceAll("\\[", "").replaceAll("\\]", ""));
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".perBrand", map);
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//브랜드별 공헌이익 - 화면
	@RequestMapping(value = "/perBrand2")
	public ModelAndView perBrand2(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("returnParam", Common.paramToMap(request.getParameterMap()));
		mv.setViewName("sales/perBrand2.pq");
		return mv;
	}

	//브랜드별 공헌이익2 - 리스트
	@RequestMapping(value = "/perBrand2.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> perBrand2(HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("user_brand", ((MemberDTO4) authentication.getPrincipal()).getUser_brand().replaceAll("\\]\\[", ",").replaceAll("\\[", "").replaceAll("\\]", ""));
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".perBrand2", map);
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//브랜드별 공헌이익(기초상품) - 화면
	@RequestMapping(value = "/perProduct_month")
	public ModelAndView perProductMonth(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("yearValue", Common.defaultValue(map.get("yearValue"), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))));
		map.put("monthValue", Common.defaultValue(map.get("monthValue"), LocalDate.now().format(DateTimeFormatter.ofPattern("M"))));
		map.put("market", Common.defaultValue(map.get(Common.paramIsArray("market", request)), "1"));
		map.put("brand", Common.defaultValue(map.get(Common.paramIsArray("brand", request)), "242"));
		mv.addObject("codeGroupList", dbConn.recordSet(QUERY_ROOT + ".codeGroupList", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("sales/perProduct_month.pq");
		return mv;
	}

	//브랜드별 공헌이익(기초상품) - 리스트
	@RequestMapping(value = "/perProduct_month.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> perProductMonth(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("yearValue", Common.defaultValue(map.get("yearValue"), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))));
		map.put("monthValue", Common.defaultValue(map.get("monthValue"), LocalDate.now().format(DateTimeFormatter.ofPattern("M"))));
		map.put("market", map.get(Common.paramIsArray("market", request)));
		map.put("brand", map.get(Common.paramIsArray("brand", request)));
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".perProductMonth", map);
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//수수료 등록/확인 - 화면
	@RequestMapping(value = "/product_charge")
	public ModelAndView productCharge(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("yearValue", Common.defaultValue(map.get("yearValue"), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))));
		map.put("monthValue", Common.defaultValue(map.get("monthValue"), LocalDate.now().format(DateTimeFormatter.ofPattern("M"))));
		mv.addObject("codeList", dbConn.recordSet(QUERY_ROOT + ".codeList", null));
		mv.addObject("productCharge", dbConn.recordSet(QUERY_ROOT + ".productCharge", map));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("sales/product_charge.pq");
		return mv;
	}

	//수수료 등록/확인 - 등록
	@RequestMapping(value = "/product_charge_insert_DB.run", method = {RequestMethod.POST})
	@Transactional
	public ModelAndView productChargeInsertDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		Map<String, Object> totalMap = new HashMap<>();
		String[] selectField = new String[]{"market", "code1", "money"};
		List<Map<String, String>> list = Common.paramToList(selectField, request.getParameterMap());
		totalMap.put("param", map);
		totalMap.put("list", list);

		dbConn.recordSet(QUERY_ROOT + ".productChargeInsertDB", totalMap);
		mv.setViewName("redirect:/views/sales/product_charge?yearValue=" + map.get("yearValue") + "&monthValue=" + map.get("monthValue"));
		return mv;
	}

	//수수료 등록/확인 - 엑셀 일괄 등록
	@RequestMapping(value = "/product_charge_excel_insert_DB_excel.run", method = {RequestMethod.POST})
	@Transactional
	public ModelAndView productChargeExcelInsertDBExcel(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		List<Map> codeList = dbConn.recordSet(QUERY_ROOT + ".codeList", null);
		MultipartHttpServletRequest mr = (MultipartHttpServletRequest) request;

		try {
			List<String> dbField = new ArrayList<>();
			String column = "";
			dbField.addAll(Arrays.asList("market"));
			for (int i = 0; i < codeList.size(); i++) {
				column = codeList.get(i).get("id").toString();
				dbField.add(column);
			}
			List<Map<String, String>> list = excelCon.uploadExcel(mr.getFiles("charge_excel"), dbField, "몰명");
			Map<String, Object> totalMap = new HashMap<>();

			List<Map<String, String>> resultList = new ArrayList<>();

			for (int i = 0; i < list.size(); i++) {  //row
				String marketCode = "";
				for (int j = 0; j < map.get("marketName").split(",").length; j++) {
					if (list.get(i).get("market").equals(map.get("marketName").split(",")[j])) {
						marketCode = map.get("marketCode").split(",")[j];
						break;
					}
				}

				for (Entry<String, String> entry : list.get(i).entrySet()) { // cell
					if(!entry.getKey().equals("market") && !entry.getValue().equals("0")){
						Map<String, String> result = new HashMap<>();
						result.put("market", marketCode);
						result.put("brand", entry.getKey());
						result.put("money", entry.getValue());
						resultList.add(result);
					}
				}
			}
			totalMap.put("list", resultList);
			totalMap.put("param", map);
			dbConn.recordSet(QUERY_ROOT + ".productChargeExcelInsertDB", totalMap);
		} catch (IOException e) {
		}

		mv.setViewName("redirect:/views/sales/product_charge?yearValue=" + map.get("yearValue") + "&monthValue=" + map.get("monthValue"));
		return mv;
	}

	//수수료 등록/확인 - 엑셀 양식 폼 다운
	@RequestMapping(value = "/product_charge_excel_form_down.run")
	public void productChargeExcelFormDown(HttpServletResponse response, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		List<Map> codeList = dbConn.recordSet(QUERY_ROOT + ".codeList", null);
		List<String> dbField = new ArrayList<>();
		List<String> cellName = new ArrayList<>();

		dbField.addAll(Arrays.asList("market"));

		String cell = "";
		cellName.addAll(Arrays.asList("몰명"));
		for (int i = 0; i < codeList.size(); i++) {
			cell = codeList.get(i).get("name").toString();
			cellName.add(cell);
		}
		try {
			excelCon.downExcelFile(response, dbConn, QUERY_ROOT + ".productChargeExcelForm", map, dbField, cellName);
		} catch (IOException e) {
		}
	}

	//수수료 등록/확인 - 엑셀다운
	@RequestMapping(value = "/product_charge_ExcelDown.run")
	public void productChargeExcelDown(HttpServletResponse response, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		List<Map> codeList = dbConn.recordSet(QUERY_ROOT + ".codeList", null);
		List<String> dbField = new ArrayList<>();
		List<String> cellName = new ArrayList<>();

		String column = "";
		dbField.addAll(Arrays.asList("market"));
		for (int i = 0; i < codeList.size(); i++) {
			column = codeList.get(i).get("id").toString();
			dbField.add(column);
		}

		String cell = "";
		cellName.addAll(Arrays.asList("몰명"));
		for (int i = 0; i < codeList.size(); i++) {
			cell = codeList.get(i).get("name").toString();
			cellName.add(cell);
		}

		try {
			excelCon.downExcelFile(response, dbConn, QUERY_ROOT + ".productChargeExcel", map, dbField, cellName);
		} catch (IOException e) {
		}
	}

	//단품/팩키지별 공헌이익 - 화면
	@RequestMapping(value = "/perPack")
	public ModelAndView perPack(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("category_site_id", Common.defaultValue(map.get(Common.paramIsArray("category_site_id", request)), "55"));
		map.put("yearValue", Common.defaultValue(map.get("yearValue"), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("sales/perPack.pq");
		return mv;
	}

	//단품/팩키지별 공헌이익 - 리스트
	@RequestMapping(value = "/perPack.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> perPack(HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("category_site_id", Common.defaultValue(map.get(Common.paramIsArray("category_site_id", request)), ""));
		map.put("user_brand", ((MemberDTO4) authentication.getPrincipal()).getUser_brand().replaceAll("\\]\\[", ",").replaceAll("\\[", "").replaceAll("\\]", ""));
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".perPack", map);
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//기초상품별 공헌이익 - 화면
	@RequestMapping(value = "/perProduct")
	public ModelAndView perProduct(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("codeGroup", Common.defaultValue(map.get(Common.paramIsArray("codeGroup", request)), "242"));
		map.put("quarterValue", Common.defaultValue(map.get("quarterValue"), String.valueOf(LocalDate.now().get(IsoFields.QUARTER_OF_YEAR))));
		map.put("yearValue", Common.defaultValue(map.get("yearValue"), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))));
		mv.addObject("codeGroupList", dbConn.recordSet(QUERY_ROOT + ".codeGroupList", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("sales/perProduct.pq");
		return mv;
	}

	//기초상품별 공헌이익 - 리스트
	@RequestMapping(value = "/perProduct.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> perProduct(HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("quarterValue", Common.defaultValue(map.get("quarterValue"), ""));
		map.put("codeGroup", Common.defaultValue(map.get(Common.paramIsArray("codeGroup", request)), ""));
		map.put("user_brand", ((MemberDTO4) authentication.getPrincipal()).getUser_brand().replaceAll("\\]\\[", ",").replaceAll("\\[", "").replaceAll("\\]", ""));
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".perProduct", map);
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//몰별 기초상품 내역 - 화면
	@RequestMapping(value = "/mall_product_detail")
	public ModelAndView mallProductDetail(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("yearValue", Common.defaultValue(map.get("yearValue"), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))));
		map.put("monthValue", Common.defaultValue(map.get("monthValue"), LocalDate.now().format(DateTimeFormatter.ofPattern("M"))));
		mv.addObject("codeGroupList", dbConn.recordSet(QUERY_ROOT + ".codeGroupList", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("sales/mall_product_detail.pq");
		return mv;
	}

	//몰별 기초상품 내역 - 리스트
	@RequestMapping(value = "/mall_product_detail.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> mallProductDetail(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("brand", Common.defaultValue(map.get(Common.paramIsArray("brand", request)), ""));
		map.put("market", Common.defaultValue(map.get(Common.paramIsArray("market", request)), ""));
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".mallProductDetail", map);
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//11번가 정산서 변경 - 엑셀등록
	@RequestMapping(value = "/converting_11_insert_DB.run")
	public void converting11InsertDB(HttpServletResponse response, HttpServletRequest request) {
		MultipartHttpServletRequest mr = (MultipartHttpServletRequest) request;
		try {
			List<String> uploadDbField = Arrays.asList("jumuncode");
			List<Map<String, String>> list = excelCon.uploadExcel(mr.getFiles("addFile"), uploadDbField, "주문번호");
			Map<String, String> map = new HashMap<>();
			List<String> jumuncodeList = new ArrayList<>();
			for (int i = 0; i < list.size(); i++) {
				jumuncodeList.add(list.get(i).get("jumuncode"));
			}
			map.put("jumuncode", String.join(",", jumuncodeList));

			List<String> dbField = Arrays.asList("jumuncode", "deliverycode");
			List<String> cellName = Arrays.asList("주문번호", "배송번호");
			excelCon.downExcelFile(response, dbConn2, QUERY_ROOT + ".excelConverting", map, dbField, cellName);
		} catch (IOException e) {
		}
	}
}
