package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.common.ImgCon;
import com.common.member.MemberDTO4;
import java.io.IOException;
import java.util.ArrayList;
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
@RequestMapping("/views/emploee")
public class Emploee {

	private final String DIR_ROOT = "emploee";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;
	@Resource
	private ImgCon imgCon;
	@Resource
	private MemberDTO4 memberDTO4;

	//관리자 접속로그 페이지
	@RequestMapping(value = "/contact_log")
	public ModelAndView emploeeList(ModelAndView mv,HttpServletRequest request) {
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("emploee/contact_log.pq");
		return mv;
	}

	//관리자 접속로그 검색 및 페이징
	@RequestMapping(value = "/contact_log.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> emploeeList(HttpServletRequest request) {
		Map<String,String> map = Common.paramToMap(request.getParameterMap());
		map.put("externalIp", Common.defaultValue(request.getParameter("externalIp"),"N"));
		Map<String, String> param = new HashMap<>();
		List<String> searchList = new ArrayList<>();

		String[] UserIp = {"210.206.22.219"};
		String[] UserIp2 = {"106.246.246.50"};

		if (map.get("externalIp").equals("Y")) {
			searchList.add(dbConn.makeSearchSql("UserIp", UserIp, "not in"));
			searchList.add(dbConn.makeSearchSql("UserIp", UserIp2, "not in"));
		}
		searchList.add(dbConn.makeSearchSql("user_name", request.getParameterValues("user_name"), "like"));
		searchList.add(dbConn.makeSearchSqlRange("wdate", request.getParameter("date1"), request.getParameter("date2")));
		searchList.removeAll(Collections.singleton(null));

		param.put("sqlItem", String.join(" and ", searchList));
		param.put("orderBy", "id desc");
		Common.PQmap(param,request);

		List<Map> list = new ArrayList<>();
		list = dbConn.recordSet(QUERY_ROOT + ".contactLogList", param);

		Map<String, Object> returnParam = new HashMap<>();
		Common.PQresultMap(returnParam,param.get("curPage"),list);
		returnParam.put("data", Common.nullToEmpty(list));

		return returnParam;
	}

	//contact_log_insert_DB 부분
	@RequestMapping(value = "contact_log_insertDB.run")
	@Transactional
	public String insertDB(HttpServletRequest request, Authentication authentication) {
		Map<String, String> param = Common.paramToMap(request.getParameterMap());

		memberDTO4 = (MemberDTO4) authentication.getPrincipal();
		param.put("check_id",memberDTO4.get_Id());

		if (param.get("id").equals("")) {
			dbConn.recordSet(QUERY_ROOT + ".contactLogInsert", param);
		} else {
			dbConn.recordSet(QUERY_ROOT + ".contactLogUpdate", param);
		}

		return "redirect:/views/emploee/contact_log_check";
	}

	//접속로그 점검 페이지
	@RequestMapping(value = "/contact_log_check")
	public ModelAndView checkList(ModelAndView mv) {
		mv.setViewName("emploee/contact_log_check.pq");
		return mv;
	}

	@RequestMapping(value = "contact_log_check.run", produces = "application/json")
	@ResponseBody
	public Map<String, Object> checkList(HttpServletResponse response, HttpServletRequest request) {
		Map<String, String> param = Common.paramToMap(request.getParameterMap());
		List<Map> list = dbConn.recordSet(QUERY_ROOT + ".checkList", param);

		Map<String, Object> returnParam = new HashMap<>();
		returnParam.put("data", Common.nullToEmpty(list));

		return returnParam;
	}

	//계정 잠김 해제 페이지
	@RequestMapping(value = "/lock_member_list")
	public ModelAndView memberList(ModelAndView mv) {
		mv.setViewName("emploee/lock_member_list.pq");
		return mv;
	}

	@RequestMapping(value = "lock_member_list.run", produces = "application/json")
	@ResponseBody
	public Map<String, Object> memberList(HttpServletRequest request, HttpServletResponse response){
		Map<String, String> param = Common.paramToMap(request.getParameterMap());
		List<Map> list = dbConn.recordSet(QUERY_ROOT + ".memberList", param);

		Map<String,Object> returnParam = new HashMap<>();
		returnParam.put("data", Common.nullToEmpty(list));

		return returnParam;
	}

	//계정 잠김해제 처리
	@RequestMapping(value = "/lock_member_insert_DB.run")
	@Transactional
	public ModelAndView lockMemberInsertDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		dbConn.recordSet(QUERY_ROOT + ".lockMemberInsertDB", map);
		mv.setViewName("redirect:/views/emploee/lock_member_list");
		return mv;
	}


	//본부에 따른 부서 정보가져오기.
	@RequestMapping(value = "/com_dept.run", produces = "application/json")
	@ResponseBody
	public Map<String, Object> getComDept(HttpServletRequest request, HttpServletResponse response) {
		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn.makeSearchSql("subj", request.getParameterValues("com_root"), "like"));
		sqlItemList.add(dbConn.makeSearchSql("subj", request.getParameterValues("com_root[]"), "like"));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		map.put("sqlItem", String.join(" and ", sqlItemList));

		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("data", dbConn.recordSet(QUERY_ROOT + ".comDept", map));

		return resultMap;
	}


	//나의메뉴 직원목록
	@RequestMapping(value = "/member")
	public ModelAndView member(ModelAndView mv, HttpServletRequest request) throws Exception {
		Map<String,String[]> paramMap = new HashMap();
		paramMap.put("com_root",new String[]{"본사"});
		paramMap.putAll(request.getParameterMap());


		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn.makeSearchSql("user_name", request.getParameterValues("user_name"), "like"));
		sqlItemList.add(dbConn.makeSearchSql("com_root", request.getParameterValues(Common.paramIsArray("com_root", request)), "=", "본사"));
		sqlItemList.add(dbConn.makeSearchSql("com_dept", request.getParameterValues(Common.paramIsArray("com_dept", request)), "="));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		map.put("sqlItem", String.join(" and ", sqlItemList));

		mv.addObject("comRootMap", dbConn.recordSet(QUERY_ROOT + ".comRoot", map));
		mv.addObject("memberMap", dbConn.recordSet(QUERY_ROOT + ".member", map));
		mv.addObject("searchParam", Common.paramToSearch(paramMap));

		mv.setViewName("emploee/member.tiles");

		return mv;
	}


	//세부직원목록
	@RequestMapping(value = "/view")
	public ModelAndView view(ModelAndView mv, HttpServletRequest request, Authentication authentication){
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		if(("personal").equals(map.get("gubun"))) {  //나의정보일때는 id값이 없으므로 셋팅
			map.put("id", ((MemberDTO4) authentication.getPrincipal()).get_Id());
		}
		if(!Common.isNullOrEmpty(map.get("id"))) {          //신규는 정보를 가져올 필요가 없음.
			List<List<Map>> resultviewMap = dbConn.recordSet(QUERY_ROOT + ".view", map);
			mv.addObject("viewMap", resultviewMap.get(0));
			mv.addObject("adminMarketMap", resultviewMap.get(1));
		}
		mv.addObject("adminDeptMap", dbConn.recordSet(QUERY_ROOT + ".admin_dept", map));
		mv.setViewName("emploee/view.tiles");
		return mv;
	}


	//직원 수정, 등록..
	@RequestMapping(value = "/insert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView insertRun(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		MemberDTO4 member = (MemberDTO4) authentication.getPrincipal();
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		String choice = map.get("choice");
		map.put("treatment", Common.defaultValue(map.get("treatment"),"n"));

		if("n".equals(map.get("check_use"))){			//퇴사자 정보 삭제(부서, 개인정보)
			map.put("com_dept", "");
			map.put("user_hp", "");
			map.put("user_zip", "");
			map.put("user_addr1", "");
			map.put("user_addr2", "");
		}else {
			map.put("com_root", (map.get("com_dept")).split(">")[0].trim());
			map.put("com_dept", (map.get("com_dept")).split(">")[1].trim());
		}

		try {
			List<String> upFileImg = imgCon.uploadImgFile(((MultipartHttpServletRequest) request).getFiles("photo_file"), request, member.getUser_id(), DIR_ROOT, null);
			for (String filename : upFileImg) {
				map.put("pre_photo_file", filename);
			}
		} catch (IOException e){}

		if("insert".equals(choice) || "pre".equals("choice")){
			dbConn.recordSet(QUERY_ROOT + ".insert", map);
			mv.setViewName("redirect:/views/login.bare");

		}else if("correct".equals(choice)){
			dbConn.recordSet(QUERY_ROOT + ".update", map);

			if("master".equals(member.getUser_id()) || "인사총무팀".equals(member.getCom_dept())){		//인사팀만 입사일 수정 가능.
				dbConn.recordSet(QUERY_ROOT + ".update_cdate", map);
			}

			if("y".equals(map.get("marketCorrect"))){			// 마켓셋팅.
				String[] admin_marketArray = String.valueOf(map.get("market")).split(",");
				List<Map<String, String>> listMap = new ArrayList<>();

				for (int i = 0; i < admin_marketArray.length; i++) {
					String admin_market = admin_marketArray[i];
					listMap.add(new HashMap() {{
						put("admin_market", admin_market);
					}});
				}

				Map paramList = new HashMap<>();
				paramList.put("param", listMap);
				paramList.put("param2", map);
				dbConn.recordSet(QUERY_ROOT + ".update_adminmarket",paramList);
			}

			if((member.get_Id()).equals(map.get("id"))){
				mv.setViewName("redirect:/views/emploee/view?gubun=personal");
			}else{
				mv.setViewName("redirect:/views/emploee/view?id="+map.get("id"));
			}
		}

		return mv;
	}

	//외부접속 요청 처리
	@RequestMapping(value = "/out_use_request_insert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView outUseRequestInsertRun(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		MemberDTO4 member = (MemberDTO4) authentication.getPrincipal();

		map.put("wuser_name", member.getUser_name());
		map.put("wuser_id", member.getUser_id());
		dbConn.recordSet(QUERY_ROOT + ".outUseRequestInsert", map);
		mv.setViewName("redirect:/views/emploee/out_use_request");
		return mv;
	}

	//외부접속 요청 리스트
	@RequestMapping(value = "/out_use_request")
	public ModelAndView outUseRequestList(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));           //검색조건
		mv.setViewName("emploee/out_use_request.pq");
		return mv;
	}

	// pq목록 Ajax로 불러오기 json
	@RequestMapping(value = "/out_use_request_list.run", produces = "application/json")
	@ResponseBody
	public Map<String, Object> listValue(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		memberDTO4 = (MemberDTO4) authentication.getPrincipal();

		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		Map<String, Object> resultMap = new HashMap<>();

		map.put("sqlItem", "wuserid='"+((MemberDTO4) authentication.getPrincipal()).getUser_id()+"'");
		map.put("orderBy", "wdate desc");
		Common.PQmap(map,request);

		result = dbConn.recordSet(QUERY_ROOT + ".outUseRequestList", map);

		Common.PQresultMap(resultMap,map.get("curPage"),result);
		resultMap.put("data", result);

		return resultMap;
	}

}
