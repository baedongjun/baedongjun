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

	//B2B �������� > ����ݾ� �Է� - ȭ��
	@RequestMapping(value = "/input_money")
	public ModelAndView inputMoney(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("companyMarket", dbConn.recordSet(QUERY_ROOT + ".companyMarket", null));
		mv.setViewName("btob/contribution/input_money.pq");
		return mv;
	}

	//B2B �������� > ����ݾ� �Է� - ����Ʈ
	@RequestMapping(value = "/input_money.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> inputMoney(HttpServletRequest request) {
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".inputMoney", null);
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//B2B �������� > ����ݾ� �Է� - ���
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

	//B2B �������� > ����ݾ� �Է� - ����ݾ� ó���ϱ�
	@RequestMapping(value = "/count.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public ModelAndView count(ModelAndView mv) {
		dbConn.recordSet(QUERY_ROOT + ".count", null);
		mv.setViewName("redirect:/views/btob/contribution/view_ini.pq");
		return mv;
	}

	//B2B �������� > ���곻�� - ȭ��
	@RequestMapping(value = "/view_ini")
	public ModelAndView viewIni(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		mv.addObject("codeList", dbConn.recordSet(QUERY_ROOT + ".codeList", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("btob/contribution/view_ini.pq");
		return mv;
	}

	//B2B �������� > ���곻�� - ����Ʈ
	@RequestMapping(value = "/view_ini.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> viewIni(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".viewIni", map);
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//B2B �������� > ���곻��2 - ȭ��
	@RequestMapping(value = "/view_ini_channel")
	public ModelAndView viewIniChannel(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		mv.addObject("codeList", dbConn.recordSet(QUERY_ROOT + ".codeList", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("btob/contribution/view_ini_channel.pq");
		return mv;
	}

	//B2B �������� > ���곻��2 - ����Ʈ
	@RequestMapping(value = "/view_ini_channel.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> viewIniChannel(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".viewIniChannel", map);
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//B2B �������� > ���� ���� ���� - ȭ��
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

	//B2B �������� > ���� ���� ���� ���� - ����Ʈ
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

	//B2B �������� > ���� ���� ���� - ȭ��
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

	//B2B �������� > ���� ���� ���� ���� - ����Ʈ
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

	//������ ���ʻ�ǰ �������� - ȭ��
	@RequestMapping(value = "/perMallYear")
	public ModelAndView perMallYear(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("yearValue", Common.defaultValue(map.get("yearValue"), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))));
		mv.addObject("codeGroupList", dbConn.recordSet(QUERY_ROOT + ".codeGroupList", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("btob/contribution/perMallYear.pq");
		return mv;
	}

	//������ ���ʻ�ǰ �������� - ����Ʈ
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

	//�μ��� �������� - ȭ��
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

	//�μ��� �������� - ����Ʈ
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

	//�μ��� �������� - ȭ��
	@RequestMapping(value = "/perDept2")
	public ModelAndView perDept2(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("codeList", dbConn.recordSet(QUERY_ROOT + ".codeList", null));
		mv.addObject("returnParam", Common.paramToMap(request.getParameterMap()));
		mv.setViewName("btob/contribution/perDept2.pq");
		return mv;
	}

	//�μ��� ��������2 - ����Ʈ
	@RequestMapping(value = "/perDept2.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> perMall2(HttpServletRequest request) {
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".perDept2", Common.paramToMap(request.getParameterMap()));
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//�μ��� �������� - ȭ��
	@RequestMapping(value = "/perDept3")
	public ModelAndView perDept3(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("codeList", dbConn.recordSet(QUERY_ROOT + ".codeList", null));
		mv.addObject("returnParam", Common.paramToMap(request.getParameterMap()));
		mv.setViewName("btob/contribution/perDept3.pq");
		return mv;
	}

	//�μ��� ��������3 - ����Ʈ
	@RequestMapping(value = "/perDept3.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> perDept3(HttpServletRequest request) {
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".perDept3Product", Common.paramToMap(request.getParameterMap()));
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//�귣�庰 �������� - ȭ��
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

	//�귣�庰 �������� - ����Ʈ
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

	//�귣�庰 �������� - ȭ��
	@RequestMapping(value = "/perBrand2")
	public ModelAndView perBrand2(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("codeList", dbConn.recordSet(QUERY_ROOT + ".codeList", null));
		mv.addObject("returnParam", Common.paramToMap(request.getParameterMap()));
		mv.setViewName("btob/contribution/perBrand2.pq");
		return mv;
	}

	//�귣�庰 ��������2 - ����Ʈ
	@RequestMapping(value = "/perBrand2.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> perBrand2(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".perBrand2", map);
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//���ʻ�ǰ�� �������� - ȭ��
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

	//���ʻ�ǰ�� �������� - ����Ʈ
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

	//��ü�� �������� - ȭ��
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

	//��ü�� �������� - ����Ʈ
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

	//��ü�� �������� - ȭ��
	@RequestMapping(value = "/perCompany2")
	public ModelAndView perCompany2(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("codeList", dbConn.recordSet(QUERY_ROOT + ".codeList", null));
		mv.addObject("returnParam", Common.paramToMap(request.getParameterMap()));
		mv.setViewName("btob/contribution/perCompany2.pq");
		return mv;
	}

	//��ü�� ��������2 - ����Ʈ
	@RequestMapping(value = "/perCompany2.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> perCompany2(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".perCompany2", map);
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//��ü�� �������� (����ó��) - ȭ��
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

	//��ü�� �������� (����ó��) - ����Ʈ
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

	//��ü�� �������� (����ó��) - ȭ��
	@RequestMapping(value = "/perCompany_settlement2")
	public ModelAndView perCompanySettlement2(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("codeList", dbConn.recordSet(QUERY_ROOT + ".codeList", null));
		mv.addObject("returnParam", Common.paramToMap(request.getParameterMap()));
		mv.setViewName("btob/contribution/perCompany_settlement2.pq");
		return mv;
	}

	//��ü�� �������� (����ó��)2 - ����Ʈ
	@RequestMapping(value = "/perCompany_settlement2.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> perCompanySettlement2(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".perCompanySettlement2", map);
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//ä�κ� �귣�� �������� - ȭ��
	@RequestMapping(value = "/perChannel_brand")
	public ModelAndView perChannelBrand(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("yearValue", Common.defaultValue(map.get("yearValue"), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("btob/contribution/perChannel_brand.pq");
		return mv;
	}

	//ä�κ� �귣�� �������� - ����Ʈ
	@RequestMapping(value = "/perChannel_brand.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> perChannelBrand(HttpServletRequest request) {
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".perChannelBrand", Common.paramToMap(request.getParameterMap()));
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//ä�κ� �귣�� �������� - ȭ��
	@RequestMapping(value = "/perChannel_brand2")
	public ModelAndView perChannelBrand2(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("codeList", dbConn.recordSet(QUERY_ROOT + ".codeList", null));
		mv.addObject("returnParam", Common.paramToMap(request.getParameterMap()));
		mv.setViewName("btob/contribution/perChannel_brand2.pq");
		return mv;
	}

	//ä�κ� �귣�� ��������2 - ����Ʈ
	@RequestMapping(value = "/perChannel_brand2.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> perChannelBrand2(HttpServletRequest request) {
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".perChannelBrand2", Common.paramToMap(request.getParameterMap()));
		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));
		return resultMap;
	}

	//ä�κ� ���ʻ�ǰ ���� - ȭ��
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

	//ä�κ� ���ʻ�ǰ ���� - ����Ʈ
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