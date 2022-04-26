package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.common.ExcelCon;
import com.common.common.FileCon;
import com.common.member.MemberDTO4;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/views/distribution")
public class Distribution {

	private final String DIR_ROOT = "distribution";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;
	@Resource(name = "dbConn2")
	private DbConn dbConn2;
	@Resource
	private FileCon fileCon;
	@Resource(name = "excelCon")
	private ExcelCon excelCon;

	//요청 목록 - 화면
	@RequestMapping(value = "/list")
	public ModelAndView list(ModelAndView mv, HttpServletRequest request, HttpSession session) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		threePLInfo(map);

		map.put("str_userid", "");
		if(!Common.isNullOrEmpty(map.get("search")) && map.get("search").trim().length() <= 4){
			List<Map<String, String>> adminIdList = dbConn.recordSet(QUERY_ROOT + ".adminIdList", map);
			for (int i = 0; i < adminIdList.size(); i++) {
				if(map.get("str_userid").equals("")) map.put("str_userid", adminIdList.get(i).get("id"));
				else map.put("str_userid", map.get("str_userid") + "," + adminIdList.get(i).get("id"));
			}
		}

		map.put("str_userid_id", "");
		map.put("cs", Common.defaultValue(map.get("cs"), ""));
		map.put("HYUNDAI", Common.defaultValue(map.get("HYUNDAI"), ""));
		List<Map<String, String>> comDeptAdminIdList = dbConn.recordSet(QUERY_ROOT + ".comDeptAdminIdList", map);
		for (int i = 0; i < comDeptAdminIdList.size(); i++) {
			if(map.get("str_userid_id").equals("")) map.put("str_userid_id", String.valueOf(comDeptAdminIdList.get(i).get("id")));
			else map.put("str_userid_id", map.get("str_userid_id") + "," + String.valueOf(comDeptAdminIdList.get(i).get("id")));
		}

		mv.addObject("comDeptList", dbConn.recordSet(QUERY_ROOT + ".comDeptList", map));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("distribution/list.pq");
		return mv;
	}

	//요청 목록 - 리스트
	@RequestMapping(value = "/list.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> list(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("chk_order", Common.defaultValue(map.get("chk_order"),""));
		List<String> searchList = new ArrayList<>();
		List<String> searchList2 = new ArrayList<>();
		searchList2.add(dbConn2.makeSearchSql("userid", request.getParameterValues("str_userid"), "in"));
		searchList2.add(dbConn2.makeSearchSql("subject", request.getParameterValues("search"), "like"));
		searchList2.removeAll(Collections.singleton(null));

		searchList.add(dbConn2.makeSearchSql("order_special_ini_id", request.getParameterValues("order_special_ini_id"), "="));
		searchList.add(dbConn2.makeSearchSql("req_gubun", new String[]{"12"}, "not in"));
		searchList.add(dbConn2.makeSearchSql("req_gubun", request.getParameterValues(Common.paramIsArray("req_gubun", request)), "in"));
		searchList.add(dbConn2.makeSearchSql("req_brand", request.getParameterValues(Common.paramIsArray("req_brand", request)), "in"));
		searchList.add(dbConn2.makeSearchSql("status", request.getParameterValues(Common.paramIsArray("req_status", request)), "in"));
		searchList.removeAll(Collections.singleton(null));

        String sqlItem2 = String.join(" or ", searchList2);
		sqlItem2 = (!Common.isNullOrEmpty(sqlItem2)) ? " and (" + sqlItem2 + ")" : "";

		Map resultMap = new HashMap();
		List<Map> result = new ArrayList<>();
		map.put("sqlItem", String.join(" and ", searchList) + sqlItem2 );

		String orderBy = "";
		if(map.get("chk_order").equals("on")){
			orderBy = "b.max_wdate desc, case when A.urgency='y' and (status='1' or status='2') then 2 else 1 end desc, case when A.status<3 then A.status else 3 end asc , A.id desc";
		}else{
			orderBy = "case when A.urgency='y' and (status='1' or status='2') then 2 else 1 end desc, case when A.status<3 then A.status else 3 end asc , A.id desc";
		}
		map.put("orderBy", orderBy);
		Common.PQmap(map, request);

		result = dbConn2.recordSet(QUERY_ROOT + ".list", map);

		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}

	// 요청 목록 - 등록하기 화면
	@RequestMapping(value = "/input")
	public ModelAndView input(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("user_dept", ((MemberDTO4) authentication.getPrincipal()).getCom_dept());
		map.put("admin_id", ((MemberDTO4) authentication.getPrincipal()).get_Id());
		map.put("userid", ((MemberDTO4) authentication.getPrincipal()).getUser_id());

		String sampleFlag = "n";
		if("고객만족팀,IT개발팀,임원,MD팀".indexOf(map.get("user_dept")) != -1){
			sampleFlag = "y";
		}
		String bln_dist = "n";
		List<Map> deptCheck = dbConn.recordSet(QUERY_ROOT + ".deptCheck", map);
		if(deptCheck.size() != 0){
			bln_dist = "y";
		}

		mv.addObject("view", dbConn2.recordSet(QUERY_ROOT + ".input", map));
		mv.addObject("sampleFlag", sampleFlag);
		mv.addObject("bln_dist", bln_dist);
		mv.addObject("returnParam", map);
	    mv.setViewName("distribution/input.tiles");
		return mv;
	}


	// 요청 목록 - 등록하기 - 요청에관한 처리완료/요청취소
	@RequestMapping(value = "/insert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView insert(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("userid", ((MemberDTO4) authentication.getPrincipal()).getUser_id());
		map.put("write_userid", ((MemberDTO4) authentication.getPrincipal()).get_Id());
		map.put("req_id", map.get("id"));
		map.put("urgency", Common.defaultValue(map.get("urgency"), "n"));
		if(!Common.isNullOrEmpty(map.get("req_brand"))) map.put("req_brand", "/"+map.get("req_brand").replaceAll(", ","//")+"/");

		try {
			List<String> addFile = fileCon.uploadFile(((MultipartHttpServletRequest) request).getFiles("uploadfile"), request, "req", DIR_ROOT);
			for (String filename : addFile) map.put("uploadfile", filename);
		} catch (IOException e) {}

		String choice = "";
		if(Common.isNullOrEmpty(map.get("c_choice"))) choice = map.get("choice");
		else choice = map.get("c_choice");

		if (choice.equals("insert")) dbConn2.recordSet(QUERY_ROOT + ".insert", map);
		else if (choice.equals("correct")) dbConn2.recordSet(QUERY_ROOT + ".correct", map);
		else if (choice.equals("comment")) dbConn2.recordSet(QUERY_ROOT + ".commentInsert", map);
		else if ("jubsoo,complete,cancel".indexOf(choice) != -1) dbConn2.recordSet(QUERY_ROOT + ".statusUpdate", map);
		else if ("y".equals(choice) || "e".equals(choice)) dbConn2.recordSet(QUERY_ROOT + ".hyundaiUpdate", map);

		String stringParam = "?cs="+map.get("cs")+"&HYUNDAI="+map.get("HYUNDAI")+"&order_special_ini_id="+map.get("order_special_ini_id");
		if("insert".equals(choice) || "y".equals(choice) || "e".equals(choice))	mv.setViewName("redirect:/views/distribution/list" + stringParam);
		else if("comment".equals(choice)) mv.setViewName("redirect:/views/distribution/comment" + stringParam + "&id=" + map.get("id"));
		else mv.setViewName("redirect:/views/distribution/input" + stringParam + "&id=" + map.get("id"));

		return mv;
	}

	// 요청 목록 - 등록하기 화면
	@RequestMapping(value = "/comment")
	public ModelAndView comment(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		List<List<Map>> list = dbConn2.recordSet(QUERY_ROOT + ".comment", map);
		mv.addObject("commentView", list.get(0));
		mv.addObject("commentList", list.get(1));
		mv.addObject("returnParam", map);
		mv.setViewName("distribution/comment.tiles");
		return mv;
	}

	//부자재 요청 목록 - 화면
	@RequestMapping(value = "/parts_list")
	public ModelAndView partsList(ModelAndView mv, HttpServletRequest request, HttpSession session) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		map.put("str_userid", "");
		if(!Common.isNullOrEmpty(map.get("search")) && map.get("search").trim().length() <= 4){
			List<Map<String, String>> adminIdList = dbConn.recordSet(QUERY_ROOT + ".adminIdList", map);
			for (int i = 0; i < adminIdList.size(); i++) {
				if(map.get("str_userid").equals("")) map.put("str_userid", adminIdList.get(i).get("id"));
				else map.put("str_userid", map.get("str_userid") + "," + adminIdList.get(i).get("id"));
			}
		}

		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("distribution/parts_list.pq");
		return mv;
	}

	//부자재 요청 목록 - 리스트
	@RequestMapping(value = "/parts_list.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> partsList(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		List<String> searchList = new ArrayList<>();
		List<String> searchList2 = new ArrayList<>();
		searchList2.add(dbConn2.makeSearchSql("admin_id", request.getParameterValues("str_userid"), "in"));
		searchList2.add(dbConn2.makeSearchSql("subject", request.getParameterValues("search"), "like"));
		searchList2.removeAll(Collections.singleton(null));

		searchList.add(dbConn2.makeSearchSql("req_brand", request.getParameterValues(Common.paramIsArray("req_brand", request)), "in"));
		searchList.add(dbConn2.makeSearchSql("status", request.getParameterValues(Common.paramIsArray("req_status", request)), "in"));
		searchList.removeAll(Collections.singleton(null));

		String sqlItem2 = String.join(" or ", searchList2);
		sqlItem2 = (!Common.isNullOrEmpty(sqlItem2)) ? " and (" + sqlItem2 + ")" : "";

		Map resultMap = new HashMap();
		List<Map> result = new ArrayList<>();
		map.put("sqlItem", String.join(" and ", searchList) + sqlItem2 );
		map.put("orderBy", "case when A.urgency='y' and a.status='1' then 2 else 1 end desc, case when A.status<3 then A.status else 3 end asc, a.acept_date desc");
		Common.PQmap(map, request);

		result = dbConn2.recordSet(QUERY_ROOT + ".parts_list", map);

		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}

	// 부자재 요청 목록 - 등록하기 화면
	@RequestMapping(value = "/parts_input")
	public ModelAndView partsInput(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("admin_id", ((MemberDTO4) authentication.getPrincipal()).get_Id());

		mv.addObject("view", dbConn2.recordSet(QUERY_ROOT + ".parts_input", map));
		mv.addObject("commentList", dbConn2.recordSet(QUERY_ROOT + ".parts_comment", map));
		mv.addObject("confirm", dbConn2.recordSet(QUERY_ROOT + ".parts_confirm", map));
		mv.addObject("returnParam", map);
		mv.setViewName("distribution/parts_input.tiles");
		return mv;
	}


	// 부자재 요청 목록 - 등록하기
	@RequestMapping(value = "/parts_insert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView partsInsert(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		MultipartHttpServletRequest mr = (MultipartHttpServletRequest) request;
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("write_userid", ((MemberDTO4) authentication.getPrincipal()).get_Id());
		map.put("urgency", Common.defaultValue(map.get("urgency"), "n"));
		if(!Common.isNullOrEmpty(map.get("req_brand"))) map.put("req_brand", "/"+map.get("req_brand").replaceAll(", ","//")+"/");
		String choice = map.get("choice");

		Map<String, Object> totalMap = new HashMap<>();
		try {
			List<String> dbField = Arrays.asList("goods_name", "goods_cnt", "goods_date");
			List<Map<String, String>> list = excelCon.uploadExcel(mr.getFiles("uploadfile"), dbField, "구분");
			totalMap.put("itemIn", list);
		} catch (IOException e) {}

		try {
			List<String> addFile = fileCon.uploadFile(mr.getFiles("uploadfile"), request, "req", DIR_ROOT);
			for (String filename : addFile) map.put("uploadfile", filename);
			totalMap.put("param", map);
		} catch (IOException e) {}

		if (choice.equals("insert")) dbConn2.recordSet(QUERY_ROOT + ".parts_insert", totalMap);
		else if (choice.equals("correct")) dbConn2.recordSet(QUERY_ROOT + ".parts_correct", totalMap);
		else if (choice.equals("comment")) dbConn2.recordSet(QUERY_ROOT + ".parts_commentInsert", totalMap);
		else if ("jubsoo,cancel".indexOf(choice) != -1) dbConn2.recordSet(QUERY_ROOT + ".parts_statusUpdate", map);

		if("correct,comment".indexOf(choice) != -1)	mv.setViewName("redirect:/views/distribution/parts_input" + "?id=" + map.get("id"));
		else mv.setViewName("redirect:/views/distribution/parts_list");

		return mv;
	}

	//부자재 입고 관리 - 화면
	@RequestMapping(value = "/parts_check_list")
	public ModelAndView partsCheckList(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		LocalDate now = LocalDate.now();
		map.put("file_type", Common.defaultValue(map.get("file_type"), "1"));
		map.put("str_userid", "");
		if(!Common.isNullOrEmpty(map.get("search")) && map.get("search").trim().length() <= 4){
			List<Map<String, String>> adminIdList = dbConn.recordSet(QUERY_ROOT + ".adminIdList", map);
			for (int i = 0; i < adminIdList.size(); i++) {
				if(map.get("str_userid").equals("")) map.put("str_userid", adminIdList.get(i).get("id"));
				else map.put("str_userid", map.get("str_userid") + "," + adminIdList.get(i).get("id"));
			}
		}

		mv.addObject("nowDate", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("distribution/parts_check_list.pq");
		return mv;
	}

	//부자재 입고 관리 - 리스트
	@RequestMapping(value = "/parts_check_list.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> partsCheckList(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		List<String> searchList = new ArrayList<>();
		List<String> searchList2 = new ArrayList<>();
		searchList2.add(dbConn2.makeSearchSql("admin_id", request.getParameterValues("str_userid"), "in"));
		searchList2.add(dbConn2.makeSearchSql("subject", request.getParameterValues("search"), "like"));
		searchList2.add(dbConn2.makeSearchSql("reg_name", request.getParameterValues("search"), "like"));
		searchList2.removeAll(Collections.singleton(null));

		searchList.add(dbConn2.makeSearchSql("id", request.getParameterValues("id"), "="));
		searchList.add(dbConn2.makeSearchSql("file_type", request.getParameterValues(Common.paramIsArray("file_type", request)), "in"));
		searchList.add(dbConn2.makeSearchSql("check_yn", request.getParameterValues(Common.paramIsArray("check_yn", request)), "in"));
		searchList.add(dbConn2.makeSearchSql("req_brand", request.getParameterValues(Common.paramIsArray("req_brand", request)), "in"));
		searchList.removeAll(Collections.singleton(null));

		String sqlItem2 = String.join(" or ", searchList2);
		sqlItem2 = (!Common.isNullOrEmpty(sqlItem2)) ? " and (" + sqlItem2 + ")" : "";

		Map resultMap = new HashMap();
		List<Map> result = new ArrayList<>();
		map.put("sqlItem", String.join(" and ", searchList) + sqlItem2 );
		map.put("orderBy", "isnull(check_yn,'n') asc, isnull(h_check_yn,'n') asc, goods_date desc, reg_date desc, id desc, distribution_request_comment_id asc");
		Common.PQmap(map, request);

		result = dbConn2.recordSet(QUERY_ROOT + ".parts_check_list", map);

		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}

	// 부자재 요청 목록 - 등록하기
	@RequestMapping(value = "/parts_check_insert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView partsCheckInsert(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("check_userid", ((MemberDTO4) authentication.getPrincipal()).get_Id());
		dbConn2.recordSet(QUERY_ROOT + ".parts_check_insert", map);
		mv.setViewName("redirect:/views/distribution/parts_check_list");
		return mv;
	}

	public void threePLInfo(Map map) {
		map.put("order_special_ini_id", Common.defaultValue(map.get("order_special_ini_id"), "0"));

		if(map.get("order_special_ini_id").equals("18")){
			map.put("emp_name", "FLKOREA");
			map.put("emp_dbname", "distribution_flkorea");
			map.put("emp_url", "flkorea.amoretto.co.kr");
			map.put("emp_retplace", "4");
		}else if(map.get("order_special_ini_id").equals("28")){
			map.put("emp_name", "에이지원");
			map.put("emp_dbname", "distribution_hong");
			map.put("emp_url", "hong.amoretto.co.kr");
			map.put("emp_retplace", "7");
		}else if(map.get("order_special_ini_id").equals("33")){
			map.put("emp_name", "유승");
			map.put("emp_dbname", "distribution_ys");
			map.put("emp_url", "ys.amoretto.co.kr");
			map.put("emp_retplace", "15");
		}else if(map.get("order_special_ini_id").equals("43")){
			map.put("emp_name", "덕영");
			map.put("emp_dbname", "distribution_prism");
			map.put("emp_url", "distribution.amoretto.co.kr");
			map.put("emp_retplace", "16");
		}else{
			map.put("emp_name", "프리즘");
			map.put("emp_dbname", "distribution_co");
			map.put("emp_url", "prism.amoretto.co.kr");
			map.put("emp_retplace", "1");
		}
	}

}
