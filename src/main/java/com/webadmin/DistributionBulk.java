package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.common.FileCon;
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
@RequestMapping("/views/distribution/bulk")
public class DistributionBulk {

	private final String DIR_ROOT = "distributionBulk";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;
	@Resource(name = "dbConn2")
	private DbConn dbConn2;
	@Resource
	private FileCon fileCon;

	//요청 목록 - 화면
	@RequestMapping(value = "/list")
	public ModelAndView list(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("comRootMap", dbConn.recordSet(QUERY_ROOT + ".comRootMap", null));

		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("distribution/bulk/list.pq");
		return mv;
	}

	//요청 목록 - 리스트
	@RequestMapping(value = "/list.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> list(HttpServletRequest request, HttpServletResponse response) {
		//검색
		List<String> searchList = new ArrayList<>();
		List<String> searchList2 = new ArrayList<>();
		searchList2.add(dbConn2.makeSearchSql("A.subject", request.getParameterValues(Common.paramIsArray("subject", request)), "like"));
		searchList2.add(dbConn2.makeSearchSql("write_name", request.getParameterValues(Common.paramIsArray("subject", request)), "like"));
		searchList2.removeAll(Collections.singleton(null));

		searchList.add(dbConn2.makeSearchSql("A.status", request.getParameterValues(Common.paramIsArray("req_status", request)), "="));
		searchList.add(dbConn.makeSearchSqlRange("wdate", request.getParameter("wdate1"), request.getParameter("wdate2")));
		searchList.add(dbConn.makeSearchSqlRange("A.edate", request.getParameter("wdate3"), request.getParameter("wdate4")));
		searchList.add(dbConn.makeSearchSql("com_root", request.getParameterValues(Common.paramIsArray("com_root", request)), "like"));
		searchList.add(dbConn.makeSearchSql("com_dept", request.getParameterValues(Common.paramIsArray("com_dept", request)), "like"));
		searchList.removeAll(Collections.singleton(null));

        String sqlItem2 = String.join(" or ", searchList2);
		sqlItem2 = (!Common.isNullOrEmpty(sqlItem2)) ? " and (" + sqlItem2 + ")" : "";

		Map resultMap = new HashMap();
		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		map.put("sqlItem", String.join(" and ", searchList) + sqlItem2 );
		map.put("orderBy", "status, wdate desc");
		Common.PQmap(map, request);

		result = dbConn2.recordSet(QUERY_ROOT + ".list", map);

		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}

	// 요청 목록 - 등록하기 화면
	@RequestMapping(value = "/input")
	public ModelAndView input(ModelAndView mv, HttpServletRequest request) {

		Map<String, String> map = new HashMap<>();
		map.put("id",request.getParameter("id"));
		if(!Common.isNullOrEmpty(request.getParameter("id"))){
			List<List<Map<String, Object>>> list = dbConn2.recordSet(QUERY_ROOT + ".inputView", map);

			mv.addObject("detail", list.get(0));
			mv.addObject("reqcomment", list.get(1));
			mv.addObject("addwork", list.get(2));
		}

	    mv.setViewName("distribution/bulk/input.tiles");
		return mv;
	}

	// 요청 목록 - 등록하기 - 처리내용 등록
	@RequestMapping(value = "/insertComment.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView insertComment(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("writer", ((MemberDTO4) authentication.getPrincipal()).get_Id());

		dbConn2.recordSet(QUERY_ROOT + ".insertComment", map);

		mv.setViewName("redirect:/views/distribution/bulk/input?id=" + map.get("request_id"));
		return mv;
	}

	// 요청 목록 - 등록하기 - 추가인원 요청 확인
	@RequestMapping(value = "/addUser.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView addUser(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		String choice = map.get("choice");

		if ("cancel".equals(choice)) {
			dbConn2.recordSet(QUERY_ROOT + ".addCancel", map.get("aid"));
		} else if ("complete".equals(choice)) {
			dbConn2.recordSet(QUERY_ROOT + ".addComplete", map);
		}

		mv.setViewName("redirect:/views/distribution/bulk/input?id=" + map.get("id"));
		return mv;
	}

	// 요청 목록 - 등록하기 - 요청에관한 처리완료/요청취소
	@RequestMapping(value = "/insert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView insert(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		String choice = map.get("choice");

		try {
			List<String> addFile = fileCon.uploadFile(((MultipartHttpServletRequest) request).getFiles("uploadfile"), request, "req", DIR_ROOT);
			for (String filename : addFile) {
				map.put("uploadfile", filename);
			}
		} catch (IOException e) {
		}

		if ("insert".equals(choice)) {//등록하기
			dbConn2.recordSet(QUERY_ROOT + ".insert", map);
		}else if ("modify".equals(choice)) {//수정하기
			dbConn2.recordSet(QUERY_ROOT + ".modify", map);
		} else if ("jubsoo".equals(choice)) {//접수처리
			dbConn2.recordSet(QUERY_ROOT + ".jubsoo", map);
		}else if ("complete".equals(choice)) {//처리완료
			dbConn2.recordSet(QUERY_ROOT + ".complete", map);
		}else if ("collect".equals(choice)) {//거래명세표 회수
			dbConn2.recordSet(QUERY_ROOT + ".collect", map);
		}else if ("cancel".equals(choice)) {//요청취소
			dbConn2.recordSet(QUERY_ROOT + ".cancel", map);
		}

		if("insert".equals(choice)){
			mv.setViewName("redirect:/views/distribution/bulk/list");
		}else{
			mv.setViewName("redirect:/views/distribution/bulk/input?id=" + map.get("id"));
		}
		return mv;
	}

	//요청 현황 달력
	@RequestMapping(value = "/calendar.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> getCalendar(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<>();
		Map<String, String> map =  Common.paramToMap(request.getParameterMap());

		resultMap.put("data", dbConn2.recordSet(QUERY_ROOT + ".calendar",map));

		return resultMap;
	}
}
