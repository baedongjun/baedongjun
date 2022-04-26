package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.common.FileCon;
import com.common.member.MemberDTO4;
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
@RequestMapping("/views/cs/afterService")
public class CSAfterService {

	private final String DIR_ROOT = "csAfterService";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;
	@Resource(name = "dbConn2")
	private DbConn dbConn2;
	@Resource
	private FileCon fileCon;
	@Resource
	private MemberDTO4 memberDTO4;

	// as 리스트 화면 호출
	@RequestMapping(value = "/list")
	public ModelAndView returnValue(ModelAndView mv, HttpServletRequest request) throws Exception {
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("cs/afterService/list.pq");
		return mv;
	}

	// pq목록 Ajax로 불러오기 json
	@RequestMapping(value = "/list.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> returnValue(HttpServletRequest request, HttpServletResponse response) {
		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn.makeSearchSql("goods_name", request.getParameterValues("goods_name"), "like"));
		sqlItemList.add(dbConn.makeSearchSql("B.userName", request.getParameterValues("userName"), "like"));
		sqlItemList.add(dbConn.makeSearchSql("as_gubun", request.getParameterValues("as_gubun"), "="));
		sqlItemList.add(dbConn.makeSearchSql("money_yn", request.getParameterValues(Common.paramIsArray("money_yn", request)), "like"));
		sqlItemList.add(dbConn.makeSearchSql("invoice", request.getParameterValues("invoice"), "like"));
		sqlItemList.add(dbConn.makeSearchSql("status", request.getParameterValues(Common.paramIsArray("as_status", request)), "like"));
		sqlItemList.add(dbConn.makeSearchSql("as_yn", request.getParameterValues("as_yn"), "like"));
		sqlItemList.add(dbConn.makeSearchSqlRange("A.wdate", request.getParameter("sdate"), request.getParameter("edate")));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("orderBy", "A.id desc");
		Common.PQmap(map, request);

		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".list", map);

		Map resultMap = new HashMap();
		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}

	// as 상세정보 화면 호출
	@RequestMapping(value = "/view")
	public ModelAndView viewValue(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = new HashMap<>();

		map.put("id", request.getParameter("id"));

		List<Map<String, Object>> view = dbConn.recordSet(QUERY_ROOT + ".viewList", map);

		// content 줄바꿈 처리
		List<Map<String, Object>> contentMap = (List<Map<String, Object>>) view.get(0);
		String contents = Common.defaultValue(contentMap.get(0).get("contents"), "");
		contents = contents.replaceAll("\r\n", "<br>");

		mv.addObject("item", dbConn.recordSet(QUERY_ROOT + ".itemGroupList", null));
		mv.addObject("contents", contents);
		mv.addObject("view", view.get(0));
		mv.addObject("file", view.get(1));
		mv.addObject("comment", view.get(2));
		mv.addObject("escalation", view.get(3));

		mv.setViewName("cs/afterService/view.tiles");

		return mv;
	}

	// as 등록하기 호출
	@RequestMapping(value = "/update.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView updateDB(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		MemberDTO4 member = (MemberDTO4) authentication.getPrincipal();

		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		String as_status = Common.defaultValue(map.get("as_status"), "0");
		String adminName = Common.defaultValue(member.getUser_name(), "");
		String as_money = Common.isNullOrEmpty(request.getParameter("as_money")) ? "0" : request.getParameter("as_money").replaceAll(",", "");

		map.put("status", as_status);

		if (Integer.parseInt(as_money) > 0) {
			map.put("money_yn", "y");
		} else {
			map.put("money_yn", "n");
		}

		List<Map<String, Object>> result = dbConn.recordSet(QUERY_ROOT + ".selectStatus", map);

		if ("고객만족팀".equals(Common.defaultValue(member.getCom_dept(), ""))) {
			map.put("admin_name", adminName);
			if ("0".equals(as_status)) {
				// 접수 상태
				map.put("status", "1");
				map.put("mobile", Common.defaultValue(result.get(0).get("mobile"), ""));

				// 접수 상태 변경
				dbConn.recordSet(QUERY_ROOT + ".receiveUpdate", map);

				// 접수시 문자 발송
				dbConn.recordSet(QUERY_ROOT + ".SMSInsert", map);
			} else if ("0".equals(Common.defaultValue(map.get("answer_check"), "0")) && "1".equals(as_status)) {
				// 접수, 미답변 상태
				map.put("status", "1");

				dbConn.recordSet(QUERY_ROOT + ".receiveUpdate", map);
			}
		}

		// 접수,대기면서 답변완료 상태
		if ("1".equals(Common.defaultValue(map.get("answer_check"), "0")) && ("1".equals(as_status) || "0".equals(as_status))) {
			map.put("status", "2");
			dbConn.recordSet(QUERY_ROOT + ".statusUpdate", map);
		}
		// 회수완료 상태
		if ("1".equals(Common.defaultValue(map.get("hs_complete"), "0")) && "3".equals(as_status)) {
			map.put("status", "4");
			dbConn.recordSet(QUERY_ROOT + ".statusUpdate", map);
		}
		// 발송완료 상태
		if (!"".equals(Common.defaultValue(map.get("invoice"), ""))) {
			map.put("status", "5");
			dbConn.recordSet(QUERY_ROOT + ".statusUpdate", map);
		}
		// 접수취소 상태
		if ("7".equals(as_status)) {
			map.put("status", "7");
			dbConn.recordSet(QUERY_ROOT + ".statusUpdate", map);
		}

		if (map.get("as_yn").equals("0")) {
			map.put("as_gubun", null);
			map.put("as_money", "0");
		} else if (map.get("as_yn").equals("1")) {
			map.put("as_money", as_money);
		}

		dbConn.recordSet(QUERY_ROOT + ".update", map);

		if (!"".equals(Common.defaultValue(map.get("answer"), ""))) {
			map.put("writer", adminName);
			dbConn.recordSet(QUERY_ROOT + ".commentInsert", map);
		}

		mv.setViewName("redirect:/views/cs/afterService/list");
		return mv;
	}
}

