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
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "views/cs")
public class CSAfterServiceParts {

	private final String DIR_ROOT = "cs";
	private final String QUERY_ROOT = DIR_ROOT + "AfterServiceParts.query";
	private final String FILE_PATH = "/_vir0001/product_img/ini_product";

	@Resource(name = "dbConn")
	private DbConn dbConn;
	@Resource(name = "dbConn2")
	private DbConn dbConn2;
	@Resource(name = "excelCon")
	private ExcelCon excelCon;

	// 부자재 리스트
	@RequestMapping(value = "/as_parts_item_list")
	public ModelAndView itemList(ModelAndView mv, HttpServletRequest request) {
		List<Map> item = dbConn.recordSet(QUERY_ROOT + ".itemGroupList", null);
		mv.addObject("item", item);
		mv.addObject("searchParam", Common.isNullOrEmpty(request.getParameterMap()) ? item.get(0) : Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("cs/as_parts_item_list.pq");
		return mv;
	}

	// 부자재 리스트
	@RequestMapping(value = "/as_parts_item_list.run")
	@ResponseBody
	public Map<String, Object> itemList(HttpServletRequest request, HttpServletResponse response) {
		List<String> sqlItemList = new ArrayList<>();
		List<String> sqlItemList2 = new ArrayList<>();

		if (!Common.isNullOrEmpty(request.getParameter("grouping"))) {
			String[] grouping = request.getParameter("grouping").split(" > ");
			sqlItemList.add(dbConn.makeSearchSql("group1", new String[]{grouping[0]}, "="));
			sqlItemList.add(dbConn.makeSearchSql("group2", new String[]{grouping[1]}, "="));
		}

		sqlItemList2.add(dbConn.makeSearchSql("name", request.getParameterValues("search"), "like"));
		sqlItemList2.add(dbConn.makeSearchSql("model", request.getParameterValues("search"), "like"));
		sqlItemList2.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		Map resultMap = new HashMap();

		String orQuery = String.join(" or ", sqlItemList2);
		orQuery = (!Common.isNullOrEmpty(orQuery)) ? " and ("+ orQuery +")" : "";

		map.put("sqlItem", String.join(" and ", sqlItemList) + orQuery);
		resultMap.put("data", dbConn.recordSet(QUERY_ROOT + ".itemList", map));

		return resultMap;
	}

	//부자재 일괄등록
	@RequestMapping(value = "/addItem.run", method = {RequestMethod.POST})
	@Transactional
	public ModelAndView insertProductDB(ModelAndView mv, HttpServletRequest request) {
		MultipartHttpServletRequest mr = (MultipartHttpServletRequest) request;

		try {
			List<String> dbField = Arrays.asList("group1","group2","model","name","etc","num","money","stock","stockAlert");
			List<Map<String, String>> list = excelCon.uploadExcel(mr.getFiles("addFile"), dbField, "그룹1");
			dbConn.recordSet(QUERY_ROOT + ".itemInsert", list);
		} catch (IOException e) {}

		mv.setViewName("redirect:/views/cs/as_parts_item_list");
		return mv;
	}

	//부자재 - 엑셀다운로드
	@RequestMapping(value = "/partsExcelDown.run")
	public void downExcel(HttpServletResponse response) {
		List<String> dbField = Arrays.asList("group1","group2","model","name","etc","num","money","stock","stockAlert","wdate");
		List<String> cellName = Arrays.asList("그룹1","그룹2","모델명","파츠명","기타설명","기본구성(EA)","가격","재고","경고 재고수","등록일자");
		try {
			excelCon.downExcelFile(response, dbConn, QUERY_ROOT + ".itemListExcelDown", null, dbField, cellName);
		} catch (IOException e) {
		}
	}

	//부자재 삭제
	@RequestMapping(value = "/as_parts_item_delete.run", method = {RequestMethod.POST})
	@Transactional
	public ModelAndView itemListDelete(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		dbConn.recordSet(QUERY_ROOT + ".itemDelete", map);
		mv.setViewName("redirect:/views/cs/as_parts_item_list");
		return mv;
	}

	//부자재 재고변경
	@RequestMapping(value = "/as_parts_item_update.run", method = {RequestMethod.POST})
	@Transactional
	public ModelAndView itemListUpdate(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		List<Map<String, String>> list = new ArrayList<>();
		List<Map<String, String>> list2 = new ArrayList<>();
		if(!Common.isNullOrEmpty(map.get("stock"))){
			String[] selectField = new String[]{"stock", "stockID"};
			list = Common.paramToList(selectField, request.getParameterMap());
		}
		if(!Common.isNullOrEmpty(map.get("stockAlert"))) {
			String[] selectField2 = new String[]{"stockAlert", "stockID2"};
			list2 = Common.paramToList(selectField2, request.getParameterMap());
		}
		Map<String, Object> totalMap = new HashMap<>();
		totalMap.put("item", list);
		totalMap.put("item2", list2);
		dbConn.recordSet(QUERY_ROOT + ".itemUpdate", totalMap);
		mv.setViewName("redirect:/views/cs/as_parts_item_list");
		return mv;
	}

	//부자재 부족 리스트
	@RequestMapping(value = "/as_parts_item_lacking")
	public ModelAndView itemLackingList(ModelAndView mv) {
		mv.setViewName("cs/as_parts_item_lacking.pq");
		return mv;
	}

	//부자재 부족 리스트
	@RequestMapping(value = "/as_parts_item_lacking.run")
	@ResponseBody
	public Map<String, Object> itemLackingList(HttpServletRequest request, HttpServletResponse response) {
		Map<String, String> map = new HashMap<>();
		Map resultMap = new HashMap();
		map.put("sqlItem", "stock <= stockAlert");
		resultMap.put("data", dbConn.recordSet(QUERY_ROOT + ".itemList", map));

		return resultMap;
	}







	// /부자재 요청 리스트
	@RequestMapping(value = "/as_parts_list")
	public ModelAndView list(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("item", dbConn.recordSet(QUERY_ROOT + ".itemGroupList", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("cs/as_parts_list.pq");
		return mv;
	}

	// 부자재 요청 리스트
	@RequestMapping(value = "/as_parts_list.run")
	@ResponseBody
	public Map<String, Object> list(HttpServletRequest request, HttpServletResponse response) {
		List<String> sqlItemList = new ArrayList<>();
		List<String> sqlItemList2 = new ArrayList<>();

		sqlItemList.add(dbConn.makeSearchSql("nowStatus", request.getParameterValues("nowStatus"), "="));
		sqlItemList.add(dbConn.makeSearchSql("admin_name", request.getParameterValues("admin_name"), "like"));
		sqlItemList.add(dbConn.makeSearchSql("jumuncode", request.getParameterValues("jumuncode"), "="));
		sqlItemList.add(dbConn.makeSearchSql("grouping", request.getParameterValues("grouping"), "="));
		sqlItemList.add(dbConn.makeSearchSql("parts", request.getParameterValues("parts"), "like"));
		sqlItemList.removeAll(Collections.singleton(null));

		sqlItemList2.add(dbConn.makeSearchSql("A.name", request.getParameterValues("search"), "like"));
		sqlItemList2.add(dbConn.makeSearchSql("A.mobile", request.getParameterValues("search"), "like"));
		sqlItemList2.removeAll(Collections.singleton(null));
		String orQuery = String.join(" or ", sqlItemList2);
		orQuery = (!Common.isNullOrEmpty(orQuery)) ? " and ("+ orQuery +")" : "";

		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", String.join(" and ", sqlItemList) + orQuery);
		map.put("orderBy", "nowStatus, wdate desc");
		Common.PQmap(map, request);

		result = dbConn.recordSet(QUERY_ROOT + ".list", map);
		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", result);

		return resultMap;
	}



	//부자재 요청 상세정보
	@RequestMapping(value = "/as_parts_inputNew")
	public ModelAndView input(ModelAndView mv) {
		mv.addObject("item", dbConn.recordSet(QUERY_ROOT + ".itemGroupList", null));
		mv.setViewName("cs/as_parts_input.tiles");
		return mv;
	}

	//부자재 요청 상세정보
	@RequestMapping(value = "/as_parts_input")
	public ModelAndView input(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("item", dbConn.recordSet(QUERY_ROOT + ".itemGroupList", null));

		String id = request.getParameter("id");
		List<List<Map>> list = dbConn.recordSet(QUERY_ROOT + ".view", id);
		mv.addObject("id", id);
		mv.addObject("view", list.get(0));
		mv.addObject("partIn", list.get(1));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));

		mv.setViewName("cs/as_parts_input.tiles");
		return mv;
	}

	//부자재 요청 등록
	@RequestMapping(value = "/as_parts_insert.run", method = {RequestMethod.POST})
	@Transactional
	public ModelAndView insertDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		String[] selectField = new String[]{"CS_AS_parts_item_id", "cnt"};
		List<Map<String, String>> list = Common.paramToList(selectField, request.getParameterMap());

		Map<String, Object> totalMap = new HashMap<>();
		totalMap.put("param", map);
		totalMap.put("itemIn", list);
		String resultID = dbConn.recordSet(QUERY_ROOT + ".insert", totalMap).get(0).toString();

		mv.setViewName("redirect:/views/cs/as_parts_input?id=" + resultID);
		return mv;
	}

	//부자재 요청 수정
	@RequestMapping(value = "/as_parts_update.run", method = {RequestMethod.POST})
	@Transactional
	public ModelAndView updateDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		String id = request.getParameter("id");

		String[] selectField = new String[]{"CS_AS_parts_item_id", "cnt"};
		List<Map<String, String>> list = Common.paramToList(selectField, request.getParameterMap());

		Map<String, Object> totalMap = new HashMap<>();
		totalMap.put("id", id);
		totalMap.put("param", map);
		totalMap.put("itemIn", list);
		dbConn.recordSet(QUERY_ROOT + ".update", totalMap);

		mv.setViewName("redirect:/views/cs/as_parts_input?id=" + id);
		return mv;
	}

	//부자재 요청 수정
	@RequestMapping(value = "/as_parts_cancel.run", method = {RequestMethod.POST})
	@Transactional
	public ModelAndView cancelDB(ModelAndView mv, HttpServletRequest request) {
		dbConn.recordSet(QUERY_ROOT + ".cancel", Common.paramToMap(request.getParameterMap()));
		mv.setViewName("redirect:/views/cs/as_parts_list");
		return mv;
	}





	// /부자재 확인 리스트
	@RequestMapping(value = "/as_parts_confirm_list")
	public ModelAndView readyList(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("item", dbConn.recordSet(QUERY_ROOT + ".itemGroupList", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("cs/as_parts_confirm_list.pq");
		return mv;
	}

	// 부자재 확인 리스트
	@RequestMapping(value = "/as_parts_confirm_list.run")
	@ResponseBody
	public Map<String, Object> readyList(HttpServletRequest request, HttpServletResponse response) {
		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn.makeSearchSql("grouping", request.getParameterValues("grouping"), "="));
		sqlItemList.add(dbConn.makeSearchSql("nowStatus", new String[]{"2"}, "="));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		List<Map> result0 = new ArrayList<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", String.join(" and ", sqlItemList));

		String iniID = "id";
		result = dbConn.recordSet(QUERY_ROOT + ".confirmList", map);
		if(!Common.isNullOrEmpty(result)) {
			result0 = dbConn.recordSet(QUERY_ROOT + ".confirmListSub", Common.subQuery(result, iniID));
		}
		resultMap.put("data", Common.nullToEmpty(Common.combineRecordSet(iniID, result, result0)));

		return resultMap;
	}

	// 부자재 확정
	@RequestMapping(value = "/as_parts_comfirm.run")
	@Transactional
	public ModelAndView itemConfirm(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		dbConn.recordSet(QUERY_ROOT + ".itemConfirm", map);
		mv.setViewName("redirect:/views/cs/as_parts_confirm_list");
		return mv;
	}







	// 통계1
	@RequestMapping(value = "/as_parts_statistics1")
	public ModelAndView statistics1(ModelAndView mv,HttpServletRequest request) {
		mv.addObject("searchParam", Common.isNullOrEmpty(request.getParameterMap()) ? Common.nowDate().substring(0,4) : Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("cs/as_parts_statistics1.pq");
		return mv;
	}

	// 통계1
	@RequestMapping(value = "/as_parts_statistics1.run")
	@ResponseBody
	public Map<String, Object> statistics1(HttpServletRequest request, HttpServletResponse response) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		Map resultMap = new HashMap();
		resultMap.put("data", dbConn.recordSet(QUERY_ROOT + ".statistics1", map));

		return resultMap;
	}

	// 통계2
	@RequestMapping(value = "/as_parts_statistics2")
	public ModelAndView statistics2(ModelAndView mv,HttpServletRequest request) {
		mv.addObject("searchParam", Common.isNullOrEmpty(request.getParameterMap()) ? Common.nowDate().substring(0,4) : Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("cs/as_parts_statistics2.pq");
		return mv;
	}

	// 통계1
	@RequestMapping(value = "/as_parts_statistics2.run")
	@ResponseBody
	public Map<String, Object> statistics2(HttpServletRequest request, HttpServletResponse response) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		Map resultMap = new HashMap();
		resultMap.put("data", dbConn.recordSet(QUERY_ROOT + ".statistics2", map));

		return resultMap;
	}

	// /부자재 택배 발송 리스트
	@RequestMapping(value = "/as_parts_complete_list")
	public ModelAndView completeList(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("item", dbConn.recordSet(QUERY_ROOT + ".itemGroupList", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("cs/as_parts_complete_list.pq");
		return mv;
	}

	// 부자재 본사 택배 발송
	@RequestMapping(value = "/as_parts_complete_list.run")
	@ResponseBody
	public Map<String, Object> completeList(HttpServletRequest request, HttpServletResponse response) {
		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn.makeSearchSql("grouping", request.getParameterValues("grouping"), "="));
		sqlItemList.add(dbConn.makeSearchSql("nowStatus", new String[]{"3"}, "=") +  " or (invoice_no is null and nowStatus in ('4'))");
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		List<Map> result0 = new ArrayList<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", String.join(" and ", sqlItemList));

		String iniID = "id";
		result = dbConn.recordSet(QUERY_ROOT + ".completeList", map);
		if(!Common.isNullOrEmpty(result)) {
			result0 = dbConn.recordSet(QUERY_ROOT + ".completeListSub", Common.subQuery(result, iniID));
		}
		resultMap.put("data", Common.nullToEmpty(Common.combineRecordSet(iniID, result, result0)));

		return resultMap;
	}

	// 부자재 확정
	@RequestMapping(value = "/as_parts_complete.run")
	@Transactional
	public ModelAndView itemComplete(ModelAndView mv, HttpServletRequest request, Authentication authentication) {

		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		String[] selectField = new String[]{"id"};
		List<Map<String, String>> list = Common.paramToList(selectField, request.getParameterMap());

		Map<String, Object> totalMap = new HashMap<>();
		map.put("admin_id",((MemberDTO4) authentication.getPrincipal()).getUser_id());
		totalMap.put("param", map);
		totalMap.put("idList", list);

		dbConn.recordSet(QUERY_ROOT + ".itemComplete", totalMap);
		mv.setViewName("redirect:/views/cs/as_parts_complete_list");
		return mv;
	}


	//부자재 전체리스트 -> 데이터 이관
	@RequestMapping(value = "/as_parts_escalation_order_buy_ini.run", method = {RequestMethod.POST})
	@ResponseBody
	@Transactional
	public Map<String, Object> escalation1(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		Map resultMap = new HashMap();
		if (map.get("brand").indexOf("$") > 0){
			map.put("brand", Common.defaultValue(map.get("brand"),"$0$"));
		}else{
			map.put("brand", "$" + Common.defaultValue(map.get("brand"),"0").replace(", ","$$") + "$");
		}
		dbConn.recordSet("csAfterServiceParts.query.escalationOrderBuyIni", map);
		resultMap.put("message", "이관 처리되었습니다.");
		return resultMap;
	}

	//부자재 다수요청 -> 데이터 이관
	@RequestMapping(value = "/as_parts_escalation_cs_request_as.run", method = {RequestMethod.POST})
	@ResponseBody
	@Transactional
	public Map<String, Object> escalation2(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		Map resultMap = new HashMap();
		map.put("user_name", ((MemberDTO4) authentication.getPrincipal()).getUser_name());
		if(!Common.isNullOrEmpty(map.get("contents"))){
			map.put("contents", map.get("contents").replace("/<br>/ig", "\r\n"));
		}
		dbConn.recordSet("csAfterServiceParts.query.escalationCsRequestAs", map);
		resultMap.put("message", "이관 처리되었습니다.");
		resultMap.put("url", "/views/cs/multi_list");
		return resultMap;
	}



	//부자재 AS요청 -> 데이터 이관
	@RequestMapping(value = "/as_parts_escalation_cs_as.run", method = {RequestMethod.POST})
	@Transactional
	public ModelAndView escalation3(ModelAndView mv, HttpServletRequest request) {
		dbConn.recordSet(QUERY_ROOT + ".escalationCsAs", Common.paramToMap(request.getParameterMap()));
		mv.setViewName("redirect:/views/cs/afterService/list");
		return mv;
	}

}