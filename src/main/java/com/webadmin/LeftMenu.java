package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.member.MemberDTO4;
import java.net.URLEncoder;
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
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "views/left_menu")
public class LeftMenu {

	private final String DIR_ROOT = "leftMenu";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;

	//메뉴 등록 페이지
	@RequestMapping(value = "/list")
	public ModelAndView list(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("ini_id", Common.defaultValue(map.get("ini_id"), "0"));

		List<List<Map>> menuList = dbConn.recordSet(QUERY_ROOT + ".menuList", map);

		mv.addObject("menuList", menuList.get(0));
		mv.addObject("folderList", menuList.get(1));
		mv.addObject("fileList", menuList.get(2));
		mv.addObject("ini_id", map.get("ini_id"));
		mv.setViewName("left_menu/list.tiles");
		return mv;
	}

	//메뉴추가
	@RequestMapping(value = "/menu_insert_DB.run")
	@Transactional
	public ModelAndView menuInsertDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		if (map.get("gubun").equals("folder")) {
			dbConn.recordSet(QUERY_ROOT + ".insertFolder", map);
		} else {
			dbConn.recordSet(QUERY_ROOT + ".insertFile", map);
		}
		mv.setViewName("redirect:/views/left_menu/list?ini_id=" + map.get("ini_id"));
		return mv;
	}

	//메뉴 수정
	@RequestMapping(value = "/menu_update_DB.run")
	@Transactional
	public ModelAndView menuUpdateDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		String[] selectField = new String[]{"id", "sunser", "check_use", "subject", "url_link", "target", "help"};
		List<Map<String, String>> listMap = Common.paramToList(selectField, request.getParameterMap());

		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".menuUpdate", listMap);
		mv.addObject("result", result);
		mv.setViewName("redirect:/views/left_menu/list?ini_id=" + map.get("ini_id"));
		return mv;
	}

	//메뉴 이동 페이지
	@RequestMapping(value = "/menu_move")
	public ModelAndView menuMove(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		List<Map> menuMoveList = dbConn.recordSet(QUERY_ROOT + ".menuMoveList", map);
		mv.addObject("menuMoveList", menuMoveList);
		mv.setViewName("left_menu/menu_move.tiles");
		return mv;
	}

	@RequestMapping(value = "category_view.run", produces = "Application/json")
	@ResponseBody
	public Map<String, Object> categoryView(HttpServletResponse response, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		Map<String, Object> returnParam = new HashMap<>();

		List<Map> list = dbConn.recordSet(QUERY_ROOT + ".categoryView", map);
		returnParam.put("data", Common.nullToEmpty(list));

		return returnParam;
	}

	//메뉴 이동 변경
	@RequestMapping(value = "/menu_move_insert_DB.run")
	@Transactional
	public ModelAndView menuMoveInsertDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("ini_id", Common.defaultValue(map.get("targetMenu"), "0"));

		String[] selectField = new String[]{"targetMenuSub"};
		List<Map<String, String>> list = Common.paramToList(selectField, request.getParameterMap());

		Map<String, Object> totalMap = new HashMap<>();
		totalMap.put("param", map);
		totalMap.put("itemIn", list);

		dbConn.recordSet(QUERY_ROOT + ".menuMoveInsertDB", totalMap);
		mv.setViewName("redirect:/views/left_menu/list");
		return mv;
	}

	//메뉴 순서 변경
	@RequestMapping(value = "/menu_sunser_update.run")
	@Transactional
	@ResponseBody
	public void menuSunserUpdate(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		Map<String, Object> totalMap = new HashMap<>();
		String[] selectField = new String[]{map.get("choice")};
		List<Map<String, String>> list = Common.paramToList(selectField, request.getParameterMap());
		totalMap.put("choice", map.get("choice"));
		totalMap.put("list", list);
		dbConn.recordSet(QUERY_ROOT + ".menuSunserUpdate", totalMap);
	}

	//메뉴 접속 횟수
	@RequestMapping(value = "/auth_ori_count")
	public ModelAndView authCount(ModelAndView mv) {
		mv.setViewName("left_menu/auth_ori_count.pq");
		return mv;
	}

	@RequestMapping(value = "auth_ori_count.run", produces = "Application/json")
	@ResponseBody
	public Map<String, Object> authCount(HttpServletResponse response, HttpServletRequest request) {
		Map<String, String> param = Common.paramToMap(request.getParameterMap());
		Map<String, Object> returnParam = new HashMap<>();

		List<Map> list = dbConn.recordSet(QUERY_ROOT + ".authCountList", param);
		returnParam.put("data", Common.nullToEmpty(list));

		return returnParam;
	}


	//나의메뉴>관리자 관리> 관리자 권한 순서 변경//////////////
	@RequestMapping(value = "/auth_tree_edit")
	public ModelAndView treeEditList(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("id", ((MemberDTO4) authentication.getPrincipal()).get_Id());
		mv.addObject("treeEditMap", dbConn.recordSet(QUERY_ROOT + ".adminAuthTree", map));
		mv.setViewName("left_menu/auth_tree_edit.tiles");
		return mv;
	}

	//관리자 권한 순서 변경 처리
	@RequestMapping(value = "/auth_tree_edit_insert.run")
	public ModelAndView treeEdit(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		MemberDTO4 member = (MemberDTO4) authentication.getPrincipal();
		map.put("member_id", member.get_Id());
		String choice = map.get("choice");

		if ("newFolder".equals(choice)) {                   //신규폴더
			dbConn.recordSet(QUERY_ROOT + ".treeEditNewFolder", map);
		} else if ("delete".equals(choice)) {            //초기화
			dbConn.recordSet(QUERY_ROOT + ".treeEditDelete", member.get_Id());
		} else {
			dbConn.recordSet(QUERY_ROOT + ".treeEditChange", map);
		}
		mv.setViewName("redirect:/views/left_menu/auth_tree_edit");
		return mv;
	}

	//나의메뉴>관리자 관리> 관리자 권한관리//////////////
	// 접속자에 따른 관리자 권한정보 불러오기
	@RequestMapping(value = "/auth_ori")
	public ModelAndView authAdminList(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		MemberDTO4 member = (MemberDTO4) authentication.getPrincipal();

		map.put("id", member.get_Id());
		map.put("user_duty", member.getUser_duty());
		map.put("user_position", member.getUser_position());

		try {
			mv.addObject("adminAuthData", Common.mapToJson(dbConn.recordSet(QUERY_ROOT + ".adminAuthList", map)));
		} catch (Exception e) {
		}

		if ("1".equals(member.getUser_duty()) || "2".equals(member.getUser_duty())) {  //본부장급은 하위 부서 및 팀장 정보 필요
			mv.addObject("adminChildDept", dbConn.recordSet(QUERY_ROOT + ".adminChildDept", map));
		}
		mv.addObject("com_sum", map.get("com_sum"));
		mv.setViewName("left_menu/auth_ori.pq");
		return mv;
	}

	//관리자 권한정보 저장
	@RequestMapping(value = "/auth_ori_insert.run")
	public ModelAndView authAdminInsert(ModelAndView mv, HttpServletRequest request, Authentication authentication) throws Exception {
		Map<String, Object> paramList = new HashMap<>();
		paramList.put("id", ((MemberDTO4) authentication.getPrincipal()).get_Id());
		paramList.put("member_id", Common.paramToArray(request.getParameter("admin_member_id"), ","));
		paramList.put("member_auth", Common.paramToArray(request.getParameter("admin_member_auth"), "/"));
		dbConn.recordSet(QUERY_ROOT + ".adminAuthInsert", paramList);

		mv.setViewName("redirect:/views/left_menu/auth_ori?com_sum=" + URLEncoder.encode(request.getParameter("com_sum"), "UTF-8"));
		return mv;
	}

	//나의메뉴>관리자 관리> 관리자 브랜드관리
	@RequestMapping(value = "/auth_brand")
	public ModelAndView authBrandList(ModelAndView mv, Authentication authentication) {
		Map<String, String> map = new HashMap<>();
		MemberDTO4 member = (MemberDTO4) authentication.getPrincipal();
		map.put("id", member.get_Id());
		map.put("user_id", member.getUser_id());
		map.put("user_duty", member.getUser_duty());

		try {
			mv.addObject("adminBrandData", Common.mapToJson(dbConn.recordSet(QUERY_ROOT + ".adminBrandList", map)));
		} catch (Exception e) {
		}

		mv.setViewName("left_menu/auth_brand.pq");
		return mv;
	}

	//관리자 브랜드관리 저장
	@RequestMapping(value = "/auth_brand_insert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView authbrandInsert(ModelAndView mv, HttpServletRequest request, Authentication authentication) {

		Map<String, Object> paramList = new HashMap<>();
		MemberDTO4 member = (MemberDTO4) authentication.getPrincipal();
		paramList.put("id", member.get_Id());
		paramList.put("user_id", member.getUser_id());
		paramList.put("user_duty", member.getUser_duty());
		paramList.put("member_brand", Common.paramToArray(request.getParameter("admin_member_brand"), ","));
		paramList.put("member_id", Common.paramToArray(request.getParameter("admin_member_id"), "/"));
		dbConn.recordSet(QUERY_ROOT + ".adminBrandInsert", paramList);
		mv.setViewName("redirect:/views/left_menu/auth_brand");
		return mv;
	}

	// 관리자 컨버팅 현황
	@RequestMapping(value = "/admin_convert_list")
	public ModelAndView adminConvert(ModelAndView mv, HttpServletRequest request) {
		List<Map> itTeamList = dbConn.recordSet(QUERY_ROOT + ".selectItTeam", null);

		mv.addObject("itTeamList", itTeamList);
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("left_menu/admin_convert_list.pq");
		return mv;
	}

	// 관리자 컨버팅 현황
	@RequestMapping(value = "/admin_convert_list.run", produces = "Application/json")
	@ResponseBody
	public Map<String, Object> adminConvert(HttpServletResponse response, HttpServletRequest request) {
		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn.makeSearchSql("admin_id", request.getParameterValues("it_team"), "="));
		sqlItemList.add(dbConn.makeSearchSql("convert_use", request.getParameterValues("convert_use"), "="));
		sqlItemList.add(dbConn.makeSearchSql("(case when A.url_link like '%webadmin.petitelin.co.kr%' then 'O' else 'X' end)", request.getParameterValues("java"), "like"));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		map.put("sqlItem", String.join(" and ", sqlItemList));
		Map<String, Object> returnParam = new HashMap<>();

		List<Map> list = dbConn.recordSet(QUERY_ROOT + ".adminConvertList", map);
		returnParam.put("data", Common.nullToEmpty(list));

		return returnParam;
	}

	//담당자 등록
	@RequestMapping(value = "/admin_convert_insert1.run", method = {RequestMethod.POST})
	@ResponseBody
	public void adminConvertinsertDB(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		dbConn.recordSet(QUERY_ROOT + ".adminConvertInsert1", map);
	}

	//컨버팅여부 등록
	@RequestMapping(value = "/admin_convert_insert2.run", method = {RequestMethod.POST})
	@ResponseBody
	public void adminConvertinsertDB2(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		dbConn.recordSet(QUERY_ROOT + ".adminConvertInsert2", map);
	}
}
