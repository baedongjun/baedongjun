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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/views/cs")
public class CSDelivery {

	private final String DIR_ROOT = "csDelivery";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;

	@Resource(name = "excelCon")
	private ExcelCon excelCon;

	// 본사 택배 발송 요청 - 리스트
	@RequestMapping(value = "/delivery_list")
	public ModelAndView list(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("cs/delivery_list.pq");
		return mv;
	}

	// 본사 택배 발송 요청 - 리스트 json
	@RequestMapping(value = "/delivery_list.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> list(HttpServletRequest request) {

		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn.makeSearchSql("write_name", request.getParameterValues("write_userid"), "like"));
		sqlItemList.add(dbConn.makeSearchSqlRange("write_date", request.getParameter("wdate1"), request.getParameter("wdate2")));
		sqlItemList.add(dbConn.makeSearchSql("name", request.getParameterValues("name"), "like"));
		sqlItemList.add(dbConn.makeSearchSql("jumuncode", request.getParameterValues("jumuncode"), "like"));
		sqlItemList.add(dbConn.makeSearchSql("req_ok", request.getParameterValues(Common.paramIsArray("req_ok", request)), "="));
		sqlItemList.add(dbConn.makeSearchSql("gubun", request.getParameterValues(Common.paramIsArray("gubun", request)), "="));

		String[] invoiceYn = request.getParameterValues(Common.paramIsArray("invoice_yn", request));

		if (Common.isNullOrEmpty(invoiceYn) || invoiceYn.length > 1) {
			sqlItemList.add("(invoice_no <> '' or isnull(invoice_no,'') = '')");
		} else if ("y".equals(invoiceYn[0])) {
			sqlItemList.add("invoice_no <> ''");
		} else if ("n".equals(invoiceYn[0])) {
			sqlItemList.add("isnull(invoice_no,'') = ''");
		}

		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("orderBy", "req_ok, check_date desc, write_date desc, cancel_date desc");
		Common.PQmap(map, request);

		result = dbConn.recordSet(QUERY_ROOT + ".list", map);

		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}

	// 본사 택배 발송 요청 - 프린트 리스트
	@RequestMapping(value = "/delivery_print_list")
	public ModelAndView printList(ModelAndView mv) {
		mv.setViewName("cs/delivery_print_list.pq");
		return mv;
	}

	// 본사 택배 발송 요청 - 프린트 리스트 json
	@RequestMapping(value = "/delivery_print_list.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> printList() {
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".printList", null);

		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}

	// 본사 택배 발송 요청 - 상세
	@RequestMapping(value = "/delivery_view")
	public ModelAndView view(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("list", dbConn.recordSet(QUERY_ROOT + ".view", Common.paramToMap(request.getParameterMap())));
		mv.setViewName("cs/delivery_view.tiles");
		return mv;
	}

	// 본사 택배 발송 요청 - 신규등록
	@RequestMapping(value = "/delivery_insert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView insertDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".maxCnt", map);
		map.put("jumuncode", Common.nowDate().replace("-", "") + "-" + (result.size() + 1));
		List<Map<String, String>> list = new ArrayList<>();
		for (int i = 0; i < Integer.parseInt(map.get("cnt")); i++) list.add(map);

		Map<String, Object> listMap = new HashMap<>();
		listMap.put("list", list);
		dbConn.recordSet(QUERY_ROOT + ".insert", listMap);
		mv.setViewName("redirect:/views/cs/delivery_list");
		return mv;
	}

	// 본사 택배 발송 요청 - 업데이트
	@RequestMapping(value = "/delivery_update.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView updateDB(ModelAndView mv, HttpServletRequest request) {
		dbConn.recordSet(QUERY_ROOT + ".update", Common.paramToMap(request.getParameterMap()));
		mv.setViewName("redirect:/views/cs/delivery_list");
		return mv;
	}

	// 본사 택배 발송 요청 - 요청취소
	@RequestMapping(value = "/delivery_cancel.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView cancelDB(ModelAndView mv, HttpServletRequest request) {
		dbConn.recordSet(QUERY_ROOT + ".cancel", Common.paramToMap(request.getParameterMap()));
		mv.setViewName("redirect:/views/cs/delivery_list");
		return mv;
	}

	// 본사 택배 발송 요청 - 송장번호 등록
	@RequestMapping(value = "/delivery_invoice.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView invoice(ModelAndView mv, HttpServletRequest request) {
		String[] selectField = new String[]{"invoice_no", "id"};
		List<Map<String, String>> list = Common.paramToList(selectField, request.getParameterMap());

		Map<String, Object> totalMap = new HashMap<>();
		totalMap.put("list", list);

		dbConn.recordSet(QUERY_ROOT + ".invoice", totalMap);
		mv.setViewName("redirect:/views/cs/delivery_list");
		return mv;
	}

	// 본사 택배 발송 요청 - 처리
	@RequestMapping(value = "/delivery_check.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView check(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		String[] check_ynArray = String.valueOf(map.get("check_yn[]")).split(", ");

		List<Map<String, String>> listMap = new ArrayList<>();
		for (int i = 0; i < check_ynArray.length; i++) {
			String check_yn = check_ynArray[i];
			listMap.add(new HashMap() {{
				put("check_yn", check_yn);
				put("check_userid", map.get("check_userid"));
			}});
		}
		dbConn.recordSet(QUERY_ROOT + ".check", listMap);
		mv.setViewName("redirect:/views/cs/excelDown.run?down_id=" + map.get("check_yn[]"));
		return mv;
	}

	//본사택배발송 - 엑셀다운로드
	@RequestMapping(value = "/excelDown.run")
	public void downExcel(HttpServletResponse response, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn.makeSearchSql("id", request.getParameterValues(Common.paramIsArray("down_id", request)), "="));
		sqlItemList.removeAll(Collections.singleton(null));

		map.put("sqlItem", String.join(" and ", sqlItemList));

		List<String> dbField = Arrays.asList("rgubun", "gdate", "name", "mobile", "etcMobile", "zip", "addr", "deliveryNum", "jumuncode", "subject", "cnt", "size", "deliveryMoney", "message", "message2");
		List<String> cellName = Arrays.asList("예약구분", "집하예정일", "받는분성명", "받는분전화번호", "받는분기타연락처", "받는분우편번호", "받는분주소(전체, 분할)", "운송장번호", "고객주문번호", "품목명", "박스수량", "박스타입", "기본운임", "배송메세지1", "배송메세지2");
		try {
			excelCon.downExcelFile(response, dbConn,QUERY_ROOT + ".listExcelDown", map, dbField, cellName);
		} catch (IOException e) {
		}
	}
}

