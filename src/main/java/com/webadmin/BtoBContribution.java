package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.common.ExcelCon;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/views/btob/contribution")
public class BtoBContribution {

	private final String DIR_ROOT = "btobContribution";
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
		mv.addObject("companyMarket", dbConn.recordSet(QUERY_ROOT + ".companyMarket", null));
		mv.setViewName("btob/contribution/input_money.pq");
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
		map.put("market", Common.defaultValue(map.get(Common.paramIsArray("market", request)), ""));
		if (!Common.isNullOrEmpty(map.get("market"))) {
			dbConn.recordSet(QUERY_ROOT + ".inputMoneyInsertDB", map);
		}
		if (map.get("market").indexOf("46") != -1) {
			dbConn.recordSet(QUERY_ROOT + ".inputMoneyInsertDB_coupang", map);
		}
		mv.setViewName("redirect:/views/btob/contribution/input_money.pq");
		return mv;
	}

	//B2B 공헌이익 > 정산금액 입력 - 정산금액 처리하기
	@RequestMapping(value = "/count.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public ModelAndView count(ModelAndView mv) {
		dbConn.recordSet(QUERY_ROOT + ".count", null);
		mv.setViewName("redirect:/views/btob/contribution/view_ini.pq");
		return mv;
	}

	//B2B 공헌이익 > 정산내역 - 화면
	@RequestMapping(value = "/view_ini")
	public ModelAndView viewIni(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		mv.addObject("codeList", dbConn.recordSet(QUERY_ROOT + ".codeList", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("btob/contribution/view_ini.pq");
		return mv;
	}

	//B2B 공헌이익 > 정산내역 - 리스트
	@RequestMapping(value = "/view_ini.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> viewIni(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".viewIni", map);
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//B2B 공헌이익 > 정산내역2 - 화면
	@RequestMapping(value = "/view_ini_channel")
	public ModelAndView viewIniChannel(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		mv.addObject("codeList", dbConn.recordSet(QUERY_ROOT + ".codeList", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("btob/contribution/view_ini_channel.pq");
		return mv;
	}

	//B2B 공헌이익 > 정산내역2 - 리스트
	@RequestMapping(value = "/view_ini_channel.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> viewIniChannel(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".viewIniChannel", map);
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//B2B 공헌이익 > 월별 증정 내역 - 화면
	@RequestMapping(value = "/gift")
	public ModelAndView gift(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("market", Common.defaultValue(map.get(Common.paramIsArray("market", request)), ""));
		map.put("yearValue", Common.defaultValue(map.get("yearValue"), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))));
		map.put("monthValue", Common.defaultValue(map.get("monthValue"), LocalDate.now().format(DateTimeFormatter.ofPattern("M"))));

		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("btob/contribution/gift.pq");
		return mv;
	}

	//B2B 공헌이익 > 월별 증정 세부 내역 - 리스트
	@RequestMapping(value = "/gift.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> gift(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("market", Common.defaultValue(map.get(Common.paramIsArray("market", request)), ""));
		map.put("channel", Common.defaultValue(map.get(Common.paramIsArray("channel", request)), ""));
		map.put("companyView", Common.defaultValue(map.get("companyView"), ""));
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".giftList", map);
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//B2B 공헌이익 > 월별 증정 내역 - 화면
	@RequestMapping(value = "/mapping_product")
	public ModelAndView mappingProduct(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("market", Common.defaultValue(map.get(Common.paramIsArray("market", request)), ""));
		map.put("yearValue", Common.defaultValue(map.get("yearValue"), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))));
		map.put("monthValue", Common.defaultValue(map.get("monthValue"), LocalDate.now().format(DateTimeFormatter.ofPattern("M"))));

		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("btob/contribution/mapping_product.pq");
		return mv;
	}

	//B2B 공헌이익 > 월별 증정 세부 내역 - 리스트
	@RequestMapping(value = "/mapping_product.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> mappingProduct(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("market", Common.defaultValue(map.get(Common.paramIsArray("market", request)), ""));
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".mappingProduct", map);
		for (int i = 0; i < result.size(); i++) {
			result.get(i).put("rrp", Common.getInt(result.get(i).get("cnt")) * Common.getInt(result.get(i).get("rrp")));
			result.get(i).put("cost", Common.getInt(result.get(i).get("cnt")) * Common.getInt(result.get(i).get("cost")));
		}
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//연도별 기초상품 공헌이익 - 화면
	@RequestMapping(value = "/perMallYear")
	public ModelAndView perMallYear(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("yearValue", Common.defaultValue(map.get("yearValue"), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))));
		mv.addObject("codeGroupList", dbConn.recordSet(QUERY_ROOT + ".codeGroupList", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("btob/contribution/perMallYear.pq");
		return mv;
	}

	//연도별 기초상품 공헌이익 - 리스트
	@RequestMapping(value = "/perMallYear.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> perMallYear(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("codeGroup", map.get(Common.paramIsArray("codeGroup", request)));
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".perMallYear", map);
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//부서별 공헌이익 - 화면
	@RequestMapping(value = "/perDept")
	public ModelAndView perDept(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("market", Common.defaultValue(map.get(Common.paramIsArray("market", request)), "19"));
		map.put("yearValue", Common.defaultValue(map.get("yearValue"), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))));
		mv.addObject("codeList", dbConn.recordSet(QUERY_ROOT + ".codeList", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("btob/contribution/perDept.pq");
		return mv;
	}

	//부서별 공헌이익 - 리스트
	@RequestMapping(value = "/perDept.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> perDept(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("market", Common.defaultValue(map.get(Common.paramIsArray("market", request)), ""));
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".perDept", map);
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//부서별 공헌이익 - 화면
	@RequestMapping(value = "/perDept2")
	public ModelAndView perDept2(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("codeList", dbConn.recordSet(QUERY_ROOT + ".codeList", null));
		mv.addObject("returnParam", Common.paramToMap(request.getParameterMap()));
		mv.setViewName("btob/contribution/perDept2.pq");
		return mv;
	}

	//부서별 공헌이익2 - 리스트
	@RequestMapping(value = "/perDept2.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> perMall2(HttpServletRequest request) {
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".perDept2", Common.paramToMap(request.getParameterMap()));
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//부서별 공헌이익 - 화면
	@RequestMapping(value = "/perDept3")
	public ModelAndView perDept3(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("codeList", dbConn.recordSet(QUERY_ROOT + ".codeList", null));
		mv.addObject("returnParam", Common.paramToMap(request.getParameterMap()));
		mv.setViewName("btob/contribution/perDept3.pq");
		return mv;
	}

	//부서별 공헌이익3 - 리스트
	@RequestMapping(value = "/perDept3.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> perDept3(HttpServletRequest request) {
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".perDept3Product", Common.paramToMap(request.getParameterMap()));
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//브랜드별 공헌이익 - 화면
	@RequestMapping(value = "/perBrand")
	public ModelAndView perBrand(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("code1", Common.defaultValue(map.get(Common.paramIsArray("code1", request)), "242"));
		map.put("yearValue", Common.defaultValue(map.get("yearValue"), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))));
		mv.addObject("codeList", dbConn.recordSet(QUERY_ROOT + ".codeList", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("btob/contribution/perBrand.pq");
		return mv;
	}

	//브랜드별 공헌이익 - 리스트
	@RequestMapping(value = "/perBrand.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> perBrand(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("market", Common.defaultValue(map.get(Common.paramIsArray("market", request)), ""));
		map.put("code1", Common.defaultValue(map.get(Common.paramIsArray("code1", request)), ""));
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".perBrand", map);
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//브랜드별 공헌이익 - 화면
	@RequestMapping(value = "/perBrand2")
	public ModelAndView perBrand2(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("codeList", dbConn.recordSet(QUERY_ROOT + ".codeList", null));
		mv.addObject("returnParam", Common.paramToMap(request.getParameterMap()));
		mv.setViewName("btob/contribution/perBrand2.pq");
		return mv;
	}

	//브랜드별 공헌이익2 - 리스트
	@RequestMapping(value = "/perBrand2.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> perBrand2(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".perBrand2", map);
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
		mv.setViewName("btob/contribution/perProduct.pq");
		return mv;
	}

	//기초상품별 공헌이익 - 리스트
	@RequestMapping(value = "/perProduct.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> perProduct(HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("quarterValue", Common.defaultValue(map.get("quarterValue"), ""));
		map.put("codeGroup", Common.defaultValue(map.get(Common.paramIsArray("codeGroup", request)), ""));
		map.put("market", Common.defaultValue(map.get(Common.paramIsArray("market", request)), ""));
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".perProduct", map);
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//업체별 공헌이익 - 화면
	@RequestMapping(value = "/perCompany")
	public ModelAndView perCompany(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("market", Common.defaultValue(map.get(Common.paramIsArray("market", request)), "19"));
		map.put("code1", Common.defaultValue(map.get(Common.paramIsArray("code1", request)), "242"));
		map.put("yearValue", Common.defaultValue(map.get("yearValue"), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))));
		mv.addObject("codeList", dbConn.recordSet(QUERY_ROOT + ".codeList", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("btob/contribution/perCompany.pq");
		return mv;
	}

	//업체별 공헌이익 - 리스트
	@RequestMapping(value = "/perCompany.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> perCompany(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("market", Common.defaultValue(map.get(Common.paramIsArray("market", request)), ""));
		map.put("code1", Common.defaultValue(map.get(Common.paramIsArray("code1", request)), ""));
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".perCompany", map);
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//업체별 공헌이익 - 화면
	@RequestMapping(value = "/perCompany2")
	public ModelAndView perCompany2(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("codeList", dbConn.recordSet(QUERY_ROOT + ".codeList", null));
		mv.addObject("returnParam", Common.paramToMap(request.getParameterMap()));
		mv.setViewName("btob/contribution/perCompany2.pq");
		return mv;
	}

	//업체별 공헌이익2 - 리스트
	@RequestMapping(value = "/perCompany2.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> perCompany2(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".perCompany2", map);
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//업체별 공헌이익 (정산처리) - 화면
	@RequestMapping(value = "/perCompany_settlement")
	public ModelAndView perCompanySettlement(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("code1", Common.defaultValue(map.get(Common.paramIsArray("code1", request)), "242"));
		map.put("yearValue", Common.defaultValue(map.get("yearValue"), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))));
		mv.addObject("codeList", dbConn.recordSet(QUERY_ROOT + ".codeList", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("btob/contribution/perCompany_settlement.pq");
		return mv;
	}

	//업체별 공헌이익 (정산처리) - 리스트
	@RequestMapping(value = "/perCompany_settlement.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> perCompanySettlement(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("market", Common.defaultValue(map.get(Common.paramIsArray("market", request)), ""));
		map.put("code1", Common.defaultValue(map.get(Common.paramIsArray("code1", request)), ""));
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".perCompanySettlement", map);
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//업체별 공헌이익 (정산처리) - 화면
	@RequestMapping(value = "/perCompany_settlement2")
	public ModelAndView perCompanySettlement2(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("codeList", dbConn.recordSet(QUERY_ROOT + ".codeList", null));
		mv.addObject("returnParam", Common.paramToMap(request.getParameterMap()));
		mv.setViewName("btob/contribution/perCompany_settlement2.pq");
		return mv;
	}

	//업체별 공헌이익 (정산처리)2 - 리스트
	@RequestMapping(value = "/perCompany_settlement2.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> perCompanySettlement2(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".perCompanySettlement2", map);
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//채널별 브랜드 공헌이익 - 화면
	@RequestMapping(value = "/perChannel_brand")
	public ModelAndView perChannelBrand(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("yearValue", Common.defaultValue(map.get("yearValue"), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("btob/contribution/perChannel_brand.pq");
		return mv;
	}

	//채널별 브랜드 공헌이익 - 리스트
	@RequestMapping(value = "/perChannel_brand.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> perChannelBrand(HttpServletRequest request) {
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".perChannelBrand", Common.paramToMap(request.getParameterMap()));
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//채널별 브랜드 공헌이익 - 화면
	@RequestMapping(value = "/perChannel_brand2")
	public ModelAndView perChannelBrand2(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("codeList", dbConn.recordSet(QUERY_ROOT + ".codeList", null));
		mv.addObject("returnParam", Common.paramToMap(request.getParameterMap()));
		mv.setViewName("btob/contribution/perChannel_brand2.pq");
		return mv;
	}

	//채널별 브랜드 공헌이익2 - 리스트
	@RequestMapping(value = "/perChannel_brand2.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> perChannelBrand2(HttpServletRequest request) {
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".perChannelBrand2", Common.paramToMap(request.getParameterMap()));
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//채널별 기초상품 내역 - 화면
	@RequestMapping(value = "/mall_product_detail")
	public ModelAndView mallProductDetail(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("yearValue", Common.defaultValue(map.get("yearValue"), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))));
		map.put("monthValue", Common.defaultValue(map.get("monthValue"), LocalDate.now().format(DateTimeFormatter.ofPattern("M"))));
		mv.addObject("codeGroupList", dbConn.recordSet(QUERY_ROOT + ".codeGroupList", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("btob/contribution/mall_product_detail.pq");
		return mv;
	}

	//채널별 기초상품 내역 - 리스트
	@RequestMapping(value = "/mall_product_detail.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> mallProductDetail(HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("brand", Common.defaultValue(map.get(Common.paramIsArray("brand", request)), ""));
		map.put("channel", Common.defaultValue(map.get(Common.paramIsArray("channel", request)), ""));
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".mallProductDetail", map);
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}


}