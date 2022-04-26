package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.common.ExcelCon;
import com.common.common.FileCon;
import com.common.member.MemberDTO4;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/views/distribution/manualRequest")
public class DistributionManualRequest {
	private final String DIR_ROOT = "distributionManualRequest";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;

	@Resource(name = "dbConn2")
	private DbConn dbConn2;

	@Resource
	private FileCon fileCon;

	@Resource(name = "excelCon")
	private ExcelCon excelCon;

	//지정장소 관리
	@RequestMapping(value = "/company_list")
	public ModelAndView companyList(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("distribution/manualRequest/company_list.pq");

		return mv;
	}

	@RequestMapping(value = "/company_list.run", method = {RequestMethod.POST}, produces = "application/json;")
	@ResponseBody
	public Map<String, Object> companyList(HttpServletRequest request) {
		List<String> searchList = new ArrayList<>();
		List<String> searchList2 = new ArrayList<>();

		searchList2.add(dbConn2.makeSearchSql("addr1", request.getParameterValues("addr"), "like"));
		searchList2.add(dbConn2.makeSearchSql("addr2", request.getParameterValues("addr"), "like"));
		searchList2.removeAll(Collections.singleton(null));

		String sqlItem2 = String.join(" or ", searchList2);
		sqlItem2 = (!Common.isNullOrEmpty(sqlItem2)) ? " and (" + sqlItem2 + ")" : "";

		searchList.add(dbConn2.makeSearchSql("name", request.getParameterValues("name"), "like"));
		searchList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", String.join(" and ", searchList) + sqlItem2);
		map.put("orderBy", "id desc");
		Common.PQmap(map, request);

		result = dbConn2.recordSet(QUERY_ROOT + ".companyList", map);

		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}


	@RequestMapping(value = "/company_input")
	public ModelAndView companyInput(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		mv.addObject("view", dbConn2.recordSet(QUERY_ROOT + ".companyInputValue", map));
		mv.setViewName("distribution/manualRequest/company_input.tiles");
		return mv;
	}

	@RequestMapping(value = "/company_input.run", method = {RequestMethod.POST}, produces = "application/json")
	@Transactional
	public String companyInput(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		if (!Common.isNullOrEmpty(map.get("id"))) {
			dbConn2.recordSet(QUERY_ROOT + ".companyUpdate", map);
		} else { //널값이면 등록
			dbConn2.recordSet(QUERY_ROOT + ".companyInsert", map);
		}

		return "redirect:/views/distribution/manualRequest/company_list";
	}


	//수기요청관리
	@RequestMapping(value = "/list")
	@Transactional
	public ModelAndView list(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = new HashMap<>();
		MemberDTO4 member = (MemberDTO4) authentication.getPrincipal();

		map.put("user_id", member.getUser_id());
		map.put("com_dept", member.getCom_dept());
		map.put("com_root", member.getCom_root());

		mv.addObject("com_dept", dbConn.recordSet(QUERY_ROOT + ".comDept", map)); //배열
		mv.addObject("duty", dbConn.recordSet(QUERY_ROOT + ".duty", map));  //배열
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("distribution/manualRequest/list.pq");
		return mv;
	}

	@RequestMapping(value = "/list.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> list(HttpServletRequest request, Authentication authentication) {
		MemberDTO4 member = (MemberDTO4) authentication.getPrincipal();

		String com_dept = request.getParameter("com_dept");

		if (!Common.isNullOrEmpty(com_dept)) {
			String str_userid_id = "";
			dbConn.recordSet(QUERY_ROOT + ".strUserId", com_dept);
		}
		if (!member.getUser_id().equals("master") && !member.getCom_dept().equals("물류팀") && !member.getCom_dept().equals("재무팀") && !member.getCom_dept().equals("임원") && !member.getCom_dept().equals("IT개발팀")) {
			String str_userid_id = "";
			dbConn.recordSet(QUERY_ROOT + ".strUserId2", member.getCom_root());
		}

		List<String> searchList = new ArrayList<>();

		String[] idd = {"1", "7"};
		searchList.add(dbConn2.makeSearchSql("subject", request.getParameterValues("subject"), "like"));
		searchList.add(dbConn2.makeSearchSql("id", request.getParameterValues("manual_request_ini_id"), "="));
		searchList.add(dbConn2.makeSearchSql("status", request.getParameterValues("req_status"), "="));
		searchList.add(dbConn2.makeSearchSql("user_name", request.getParameterValues("user_name"), "like"));
		searchList.add(dbConn2.makeSearchSqlRange("convert(varchar(10),A.wdate,121)", request.getParameter("wdate1"), request.getParameter("wdate2")));
		searchList.add(dbConn2.makeSearchSql("admin_member_id", request.getParameterValues(""), "in"));
		searchList.add(dbConn2.makeSearchSql("is_return", request.getParameterValues("is_return"), "="));
		searchList.add(dbConn2.makeSearchSql("id", idd, "not in"));
		searchList.add(dbConn2.makeSearchSql("duty", request.getParameterValues("duty"), "="));
		if (member.getCom_dept().equals("물류팀")) {
			searchList = Arrays.asList("and status > 1");
		}
		searchList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", String.join(" and ", searchList));
		map.put("orderBy", "case when status in (7,99) then 99 else status end asc, id desc");
		Common.PQmap(map, request);

		result = dbConn2.recordSet(QUERY_ROOT + ".list", map);

		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}

	//결재하기   manual_request_process_insert_DB.asp
	@RequestMapping(value = "/process_insert.run", method = {RequestMethod.POST}, produces = "application/json")
	public String process(HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		MemberDTO4 member = (MemberDTO4) authentication.getPrincipal();

		map.put("getId", member.get_Id());
		dbConn2.recordSet(QUERY_ROOT + ".processInsert", map);

		return "redirect:/views/distribution/manualRequest/list";
	}


	@RequestMapping(value = "/input")
	public ModelAndView input(HttpServletRequest request, ModelAndView mv, Authentication authentication) {
		MemberDTO4 member = (MemberDTO4) authentication.getPrincipal();
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		//배열 place
		mv.addObject("user_hp", dbConn.recordSet(QUERY_ROOT + ".place1", member.get_Id()));
		mv.addObject("place", dbConn2.recordSet(QUERY_ROOT + ".place2"));

		//배열 duty
		mv.addObject("duty", dbConn.recordSet(QUERY_ROOT + ".duty2", member.getCom_root()));

		//관리상품
		List<Map<String, Object>> uploadCNT = dbConn2.recordSet(QUERY_ROOT + ".uploadCnt", map.get("id"));
		mv.addObject("uploadCNT", Common.defaultValue(uploadCNT.get(0).get("cnt").toString(), "0"));    // default

		//관리상품 list
		mv.addObject("manualProduct", dbConn2.recordSet(QUERY_ROOT + ".manualProduct", map.get("id")));

		//처리내역 list
		mv.addObject("manualComment", dbConn2.recordSet(QUERY_ROOT + ".manualComment", map.get("id")));

		mv.addObject("view", dbConn2.recordSet(QUERY_ROOT + ".inputValue", map));

		//default status
		List<Map<String, Object>> status = dbConn2.recordSet(QUERY_ROOT + ".inputValue", map);
		if (!Common.isNullOrEmpty(map.get("id"))) {   // 신규등록할때 defaultvalue 에러나서 조건문 사용
			mv.addObject("status", Common.defaultValue(status.get(0).get("status").toString(), "0"));
		}

		//default out_cnt_old
		List<Map<String, Object>> outCntOld = dbConn2.recordSet(QUERY_ROOT + ".manualProduct", map.get("id"));

		if (outCntOld.size() != 0) {
			mv.addObject("out_cnt_old", Common.defaultValue(outCntOld.get(0).get("out_cnt_old"), ""));
		}

		mv.setViewName("distribution/manualRequest/input.tiles");
		return mv;
	}


	@RequestMapping(value = "/input.run", method = {RequestMethod.POST}, produces = "application/json")
	@Transactional
	public String input(HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		MemberDTO4 member = (MemberDTO4) authentication.getPrincipal();

		try {
			List<String> addFile = fileCon.uploadFile(((MultipartHttpServletRequest) request).getFiles("uploadfile"), request, "req", DIR_ROOT);
			for (String filename : addFile) {
				map.put("uploadfile", filename);
			}
		} catch (IOException e) {
		}

		map.put("admin_member_id", member.get_Id());

		if (Common.isNullOrEmpty(map.get("id"))) { //id가 빈값이면 등록
			dbConn2.recordSet(QUERY_ROOT + ".insert", map);
		} else { //빈값이 아니면 수정
			dbConn2.recordSet(QUERY_ROOT + ".update", map);
		}
		return "redirect:/views/distribution/manualRequest/list";
	}

	/* manual_add_product.asp 관리상품 추가 */
	@RequestMapping(value = "/addProduct.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> addProduct(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		Map<String, Object> resultMap = new HashMap<>();

		List<Map<String, Object>> list = dbConn2.recordSet(QUERY_ROOT + ".addProduct", map);

		resultMap.put("data", list);
		resultMap.put("add_stock", map.get("add_stock"));

		return resultMap;
	}

	/* manual_request_change_insert_DB.asp 수취확인, 처리내역 */
	@RequestMapping(value = "/insertDB.run", method = {RequestMethod.POST}, produces = "application/json")
	@Transactional
	public ModelAndView insertDB(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		MemberDTO4 member = (MemberDTO4) authentication.getPrincipal();

		boolean isDistribution = false;
		if (member.getCom_dept().equals("물류팀") || member.getUser_id().equals("master")) {
			isDistribution = true;
		}

		String status = map.get("req_status");

		map.put("user_name", member.getUser_name());

		/* choice = history (처리내역) */
		if (map.get("choice").equals("history")) {
			if (status.equals(map.get("now_status"))) {
				status = "";
			}
			String addComment = "";
			if (status.equals("4") && isDistribution) {
				addComment = "<b>물류 처리중</b><br>";
				dbConn2.recordSet(QUERY_ROOT + ".status4", map);
			} else if (status.equals("5") && isDistribution) {
				addComment = "<b>물류 발송완료</b><br>";
				dbConn2.recordSet(QUERY_ROOT + ".status5", map);
			} else if (status.equals("7")) {
				addComment = "<b>처리완료</b><br>";
				dbConn2.recordSet(QUERY_ROOT + ".status7", map);
			} else if (status.equals("99")) {
				addComment = "<b>취소처리</b><br>";
				dbConn2.recordSet(QUERY_ROOT + ".status99", map);
			}

			if (map.get("comment_gubun").equals("3")) { /* 재고관리 상태이면 */
				dbConn2.recordSet(QUERY_ROOT + ".gubun3", map);
			}

			map.put("addContent", Common.addString(addComment, Common.defaultValue(map.get("content"), "")));
			map.put("getId", member.get_Id());
			map.put("req_status", Common.defaultValue(Common.defaultValue(status, map.get("now_status")), "0"));
			map.put("comment_gubun", Common.defaultValue(map.get("comment_gubun"), "1"));
			map.put("stock", Common.defaultValue(map.get("stock"), "0"));

			dbConn2.recordSet(QUERY_ROOT + ".insertComment", map);
			dbConn2.recordSet(QUERY_ROOT + ".updateComment", map);

		} else if (map.get("choice").equals("check")) { /* choice = check (관리상품 > 수취확인) */

			List<String> product_id_array = Common.paramToArray(map.get("product_id"), ", ");
			List<String> stock_check_array = Common.paramToArray(map.get("stock_check"), ", ");
			List<String> is_new_array = Common.paramToArray(map.get("is_new"), ", ");

			Map<String, String> array = new HashMap<>();

			if (!Common.isNullOrEmpty(map.get("product_id"))) {
				for (int i = 0; i < product_id_array.size(); i++) {
					if (!Common.isNullOrEmpty(stock_check_array.get(i))) {
						array.put("product_id", product_id_array.get(i));
						array.put("stock_check", stock_check_array.get(i));
						array.put("manual_request_ini_id", map.get("manual_request_ini_id"));

						if (is_new_array.get(i).equals("1")) {
							dbConn2.recordSet(QUERY_ROOT + ".insertProduct", array);
						} else {
							dbConn2.recordSet(QUERY_ROOT + ".updateProduct", array);
						}
					}
				}
			}
			map.put("getID", member.get_Id());
			dbConn2.recordSet(QUERY_ROOT + ".checkUpdate", map);
			dbConn2.recordSet(QUERY_ROOT + ".checkInsert", map);
		} else if (map.get("choice").equals("stockcheck")) {
			map.put("getId", member.get_Id());
			dbConn2.recordSet(QUERY_ROOT + ".stockCheck", map);
		}

		mv.setViewName("redirect:/views/distribution/manualRequest/input?id=" + map.get("manual_request_ini_id"));
		return mv;
	}


	// (처리내역) 발주 상품 등록/수정 manual_request_product_input.asp
	@RequestMapping(value = "/product_input")
	public ModelAndView productInput(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		mv.addObject("product", dbConn2.recordSet(QUERY_ROOT + ".productList", map));
		mv.addObject("status", dbConn2.recordSet(QUERY_ROOT + ".productStatus", map));
		mv.addObject("manual_request_ini_id", map.get("manual_request_ini_id"));

		mv.setViewName("distribution/manualRequest/product_input.tiles");
		return mv;
	}


	//수기발주상품 등록  manual_request_product_insert_DB.asp
	@RequestMapping(value = "/insertProductDB.run", method = {RequestMethod.POST})
	public ModelAndView insertProductDB(HttpServletRequest request, ModelAndView mv) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		MultipartHttpServletRequest mr = (MultipartHttpServletRequest) request;

		try {
			List<String> dbField = Arrays.asList("codename2", "out_cnt");
			List<Map<String, String>> list = excelCon.uploadExcel(mr.getFiles("addFile"), dbField, "발주");

			Map<String, Object> totalList = new HashMap<>();
			totalList.put("list", list);
			totalList.put("map", map);

			dbConn2.recordSet(QUERY_ROOT + ".insertProductDB", totalList);
		} catch (IOException e) {
		}

		mv.setViewName("redirect:/views/distribution/manualRequest/product_input?manual_request_ini_id=" + map.get("manual_request_ini_id"));
		return mv;
	}


	//발송수량 조정이력
	@RequestMapping(value = "/adjust_stock")
	public ModelAndView adjustList(ModelAndView mv, HttpServletRequest request) {
		List<String> sqlItemList = new ArrayList<>();
		List<String> sqlItemList2 = new ArrayList<>();

		sqlItemList2.add(dbConn2.makeSearchSql("name", request.getParameterValues("name"), "like"));
		sqlItemList2.add(dbConn2.makeSearchSql("code4name", request.getParameterValues("name"), "like"));
		sqlItemList2.removeAll(Collections.singleton(null));

		String sqlItem2 = String.join(" or ", sqlItemList2);
		sqlItem2 = (!Common.isNullOrEmpty(sqlItem2)) ? " and (" + sqlItem2 + " )" : "";

		sqlItemList.add(dbConn2.makeSearchSql("codename2", request.getParameterValues("codename2"), "like"));
		sqlItemList.add(dbConn2.makeSearchSqlRange("wdate", request.getParameter("wdate1"), request.getParameter("wdate2")));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, Object> map = new HashMap<>();
		map.put("sqlItem", String.join(" and ", sqlItemList) + sqlItem2);
		map.put("orderBy", "wdate desc,manual_request_ini_id desc");
		map.put("totalRecords", Common.defaultValue(request.getParameter("totalRecords"), "0"));
		map.put("curPage", Common.defaultValue(request.getParameter("curPage"), "1"));
		map.put("pageSize", Common.defaultValue(request.getParameter("pageSize"), "40"));

		List<Map> result = new ArrayList<>();
		result = dbConn2.recordSet(QUERY_ROOT + ".adjustStock", map);

		map.put("totalRecords", result.size() > 0 ? result.get(0).get("totalRecords") : 0);

		mv.addObject("returnParam", map);
		mv.addObject("pageSize", Optional.ofNullable(request.getParameter("pageSize")).orElse("40"));
		mv.addObject("searchParam", Common.paramToMap(request.getParameterMap()));
		mv.addObject("list", Common.nullToEmpty(result));

		mv.setViewName("distribution/manualRequest/adjust_stock.tiles");
		return mv;
	}
}
