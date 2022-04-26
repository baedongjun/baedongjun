package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.member.MemberDTO4;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/views/cs/3PL")
public class CS3PL {
	private final String DIR_ROOT = "cs3PL";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;
	@Resource(name = "dbConn2")
	private DbConn dbConn2;
	@Resource
	private MemberDTO4 memberDTO4;

	@RequestMapping(value = "/list")
	public ModelAndView list(ModelAndView mv, HttpServletRequest request) {
		List<Map> company = new ArrayList<>();
		company = dbConn.recordSet(QUERY_ROOT + ".company");
		mv.addObject("company", company);
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("cs/3PL/list.pq");
		return mv;
	}

	//list, 검색조건
	@RequestMapping(value = "/list.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> list(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		List<String> sqlItemList = new ArrayList<>();
		List<String> sqlItemList2 = new ArrayList<>();
		Map<String,String> paarm = Common.paramToMap(request.getParameterMap());

		memberDTO4 = (MemberDTO4) authentication.getPrincipal();
		String[] user_name = new String[]{memberDTO4.getUser_name()};

		sqlItemList.add(dbConn2.makeSearchSql("user_name", request.getParameterValues("writer"), "like"));
		sqlItemList.add(dbConn2.makeSearchSqlRange("A.wdate", request.getParameter("wdate1"), request.getParameter("wdate2")));
		sqlItemList.add(dbConn2.makeSearchSql("gubun", request.getParameterValues(Common.paramIsArray("gubun", request)), "="));
		sqlItemList.add(dbConn2.makeSearchSql("status", request.getParameterValues(Common.paramIsArray("cs_req", request)), "="));

		/*3pl업체들은 본인앞으로 작성된 게시글만 보임*/
		if (memberDTO4.getUser_position().equals("13")) {   /*position이 3pl담당이면 */
			sqlItemList.add(dbConn2.makeSearchSql("company", user_name, "="));
		} else {
			sqlItemList.add(dbConn2.makeSearchSql("company", request.getParameterValues("company"), "="));
		}
		sqlItemList.removeAll(Collections.singleton(null));

		sqlItemList2.add(dbConn2.makeSearchSql("subject", request.getParameterValues("subject"), "like"));
		sqlItemList2.add(dbConn2.makeSearchSql("convert(nvarchar(50),content)", request.getParameterValues("subject"), "like"));
		sqlItemList2.removeAll(Collections.singleton(null));

		String sqlItem2 = String.join(" or ", sqlItemList2);
		sqlItem2 = (!Common.isNullOrEmpty(sqlItem2)) ? " and (" + sqlItem2 + ")" : "";

		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", String.join(" and ", sqlItemList) + sqlItem2);
		map.put("orderBy", "status asc, wdate desc");
		map.put("user_id", memberDTO4.getUser_id());
		Common.PQmap(map, request);

		result = dbConn2.recordSet(QUERY_ROOT + ".list", map);

		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}

	//등록화면
	@RequestMapping(value = "/input")
	public ModelAndView input(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		List<Map> company = new ArrayList<>();
		company = dbConn.recordSet(QUERY_ROOT + ".company");
		mv.addObject("company", company);
		mv.addObject("list", dbConn2.recordSet(QUERY_ROOT + ".view", map));
		mv.addObject("answer", dbConn2.recordSet(QUERY_ROOT + ".answerView", map));
		mv.addObject("returnParam",map);
		mv.setViewName("cs/3PL/input.tiles");
		return mv;
	}

	//등록
	@RequestMapping(value = "/insert.run", method = {RequestMethod.POST})
	public ModelAndView insertDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> param = Common.paramToMap(request.getParameterMap());

		dbConn2.recordSet(QUERY_ROOT + ".insert", param);
		mv.setViewName("redirect:/views/cs/3PL/list");
		return mv;
	}

	//게시물 수정
	@RequestMapping(value = "/update.run", method = {RequestMethod.POST})
	public ModelAndView updateDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> param = Common.paramToMap(request.getParameterMap());

		param.put("content", param.get("content").replaceAll("(\r\n|\r|\n|\n\r)", "<br>"));
		dbConn2.recordSet(QUERY_ROOT + ".update", param);

		mv.setViewName("redirect:/views/cs/3PL/list");
		return mv;
	}

	//요청 취소
	@RequestMapping(value = "/cancel.run", method = {RequestMethod.POST})
	public ModelAndView cancel(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> param = Common.paramToMap(request.getParameterMap());

		dbConn2.recordSet(QUERY_ROOT + ".cancel", param);
		mv.setViewName("redirect:/views/cs/3PL/list");
		return mv;
	}

	//댓글 등록
	@RequestMapping(value = "/answer.run", method = {RequestMethod.POST})
	public ModelAndView answer(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> param = Common.paramToMap(request.getParameterMap());

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date time = new Date();
		String nowDate = format.format(time);

		param.put("status1", Common.defaultValue(param.get("status1"), "40"));

		if (param.get("status1").equals("20")) {
			param.put("sdate", nowDate);
		}

		if (param.get("status1").equals("40")) {
			if (param.get("nowStatus").equals("10")) {
				param.put("sdate", nowDate);
			} else {
				param.put("sdate", param.get("sdate"));
			}
			param.put("edate", Common.defaultValue(param.get("edate"), nowDate));
		}

		param.put("status", param.get("status1"));
		param.put("content", param.get("content").replaceAll("(\r\n|\r|\n|\n\r)", "<br>"));

		dbConn2.recordSet(QUERY_ROOT + ".answerUpdate", param);
		if (!Common.isNullOrEmpty(param.get("content"))) {
			dbConn2.recordSet(QUERY_ROOT + ".answerInsert", param);
		}
		mv.setViewName("redirect:/views/cs/3PL/list");
		return mv;
	}
}
