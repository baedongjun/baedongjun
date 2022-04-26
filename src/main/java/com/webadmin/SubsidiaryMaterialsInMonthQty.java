package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.common.ExcelCon;
import com.common.member.MemberDTO4;
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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/views/subsidiary_materials")
public class SubsidiaryMaterialsInMonthQty {

	private final String DIR_ROOT = "subsidiary_materialsInMonthQty";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn2")
	private DbConn dbConn2;
    @Resource(name = "excelCon")
	private ExcelCon excelCon;

	//한달유지량 변동 내역 - 화면
	@RequestMapping(value = "/in_month_qty_list")
	public ModelAndView list(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("companyList", dbConn2.recordSet(QUERY_ROOT + ".companyList", Common.paramToMap(request.getParameterMap())));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("yearV", Common.defaultValue(request.getParameter("yearV"), Common.nowDate().substring(0, 4)));
		mv.setViewName("subsidiary_materials/in_month_qty_list.pq");
		return mv;
	}

	//한달유지량 변동 내역 - 리스트
	@RequestMapping(value = "/in_month_qty_list.run", method = {RequestMethod.POST}, produces = "application/json;")
	@ResponseBody
	public Map<String, Object> list(HttpServletRequest request, HttpServletResponse response) {
		List<String> sqlItemList = new ArrayList<>();

		sqlItemList.add(dbConn2.makeSearchSql("name", request.getParameterValues("name"), "like"));
		sqlItemList.add(dbConn2.makeSearchSql("brand", request.getParameterValues(Common.paramIsArray("MD_brand", request)), "="));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("orderBy", "a.id desc");
		map.put("yearV", Common.defaultValue(request.getParameter("yearV"), Common.nowDate().substring(0, 4)));

		resultMap.put("data", Common.nullToEmpty(dbConn2.recordSet(QUERY_ROOT + ".list1", map)));

		return resultMap;
	}

	//한달유지량 등록 - 화면
	@RequestMapping(value = "/in_month_qty")
	public ModelAndView inMonthQty(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("companyList", dbConn2.recordSet(QUERY_ROOT + ".companyList", Common.paramToMap(request.getParameterMap())));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("subsidiary_materials/in_month_qty.pq");
		return mv;
	}

	//한달유지량 등록 - 리스트
	@RequestMapping(value = "/in_month_qty.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> inMonthQty(HttpServletRequest request, HttpServletResponse response) {
		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn2.makeSearchSql("name", request.getParameterValues("name"), "like"));
		sqlItemList.add(dbConn2.makeSearchSql("brand", request.getParameterValues(Common.paramIsArray("MD_brand", request)), "="));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("orderBy", "id desc");

		resultMap.put("data", Common.nullToEmpty(dbConn2.recordSet(QUERY_ROOT + ".list2", map)));

		return resultMap;
	}

	//한달유지량 등록 - 등록
	@RequestMapping(value = "/in_month_qty_insert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView inMonthQtyInsertDB(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		String[] selectField = new String[]{"id","in_qty"};

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("list", Common.paramToList(selectField, request.getParameterMap()));
		paramMap.put("admin_id", ((MemberDTO4) authentication.getPrincipal()).get_Id());

		dbConn2.recordSet(QUERY_ROOT + ".insert2", paramMap);
		mv.setViewName("redirect:/views/subsidiary_materials/in_month_qty");
		return mv;
	}

	//한달유지량 변동 내역 - 엑셀다운로드
	@RequestMapping(value = "/excelDown.run")
	public void downExcel(HttpServletResponse response, HttpServletRequest request) {
		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn2.makeSearchSql("name", request.getParameterValues("name"), "="));
		sqlItemList.add(dbConn2.makeSearchSql("brand", request.getParameterValues("MD_brand"), "="));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("orderBy", "id desc");

		List<String> dbField = Arrays.asList("uploadimg", "brand", "name", "month_1", "month_2", "month_3", "month_4", "month_5", "month_6", "month_7", "month_8", "month_9", "month_10", "month_11", "month_12");
		List<String> cellName = Arrays.asList("부자재이미지", "브랜드", "부자재명", "1월유지량", "2월유지량", "3월유지량", "4월유지량", "5월유지량", "6월유지량", "7월유지량", "8월유지량", "9월유지량", "10월유지량", "11월유지량", "12월유지량");
		try {
			excelCon.downExcelFile(response, dbConn2,QUERY_ROOT + ".listExcelDown", map, dbField, cellName);
		} catch (IOException e) {
		}
	}

}
