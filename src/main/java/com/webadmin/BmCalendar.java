package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.member.MemberDTO4;
import java.util.HashMap;
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
@RequestMapping("/views/bmCalendar")
public class BmCalendar {

	private final String DIR_ROOT = "bmCalendar";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn2")
	private DbConn dbConn2;

	@RequestMapping(value = "/calendar")
	public ModelAndView calendar(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		mv.addObject("admin_member_id", ((MemberDTO4) authentication.getPrincipal()).get_Id());
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("bm/calendar.tiles");
		return mv;
	}

	@RequestMapping(value = "/calendar.run", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, Object> calendar(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("data", Common.nullToEmpty(dbConn2.recordSet(QUERY_ROOT + ".calendar", Common.paramToMap(request.getParameterMap()))));

		return resultMap;
	}

	@RequestMapping(value = "/delete.run", method = RequestMethod.POST, produces = "application/json")
	@Transactional
	@ResponseBody
	public Map<String, Object> delete(HttpServletRequest request, HttpServletResponse response) {
		dbConn2.recordSet(QUERY_ROOT + ".delete", (Common.paramToMap(request.getParameterMap()).get("id")));

		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("resultCode", "0000");
		resultMap.put("resultMsg", "삭제 되었습니다.");

		return resultMap;
	}

	@RequestMapping(value = "/insert.run", produces = "application/json")
	@Transactional
	@ResponseBody
	public Map<String, Object> insert(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("admin_member_id", ((MemberDTO4) authentication.getPrincipal()).get_Id());

		Map<String, Object> resultMap = new HashMap<>();

		dbConn2.recordSet(QUERY_ROOT + ".insert", map);
		resultMap.put("resultCode", "0000");
		resultMap.put("resultMsg", "등록 되었습니다.");

		return resultMap;
	}

	//수정
	@RequestMapping(value = "/update.run", method = RequestMethod.POST, produces = "application/json")
	@Transactional
	@ResponseBody
	public Map<String, Object> update(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("admin_member_id", ((MemberDTO4) authentication.getPrincipal()).get_Id());

		Map<String, Object> resultMap = new HashMap<>();
		dbConn2.recordSet(QUERY_ROOT + ".update", map);
		resultMap.put("resultCode", "0000");
		resultMap.put("resultMsg", "수정 되었습니다.");

		return resultMap;
	}
}