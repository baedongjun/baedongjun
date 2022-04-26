package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.common.ImgCon;
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
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/views/petitelin")
public class PetitelinNotice {

	private final String DIR_ROOT = "petitelinNotice";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;
	@Resource
	private ImgCon imgCon;

	//공지사항/뉴스 관리 - 화면
	@RequestMapping(value = "/petitelin_notice_list")
	public ModelAndView list(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("petitelin/petitelin_notice_list.pq");
		return mv;
	}

	//공지사항/뉴스 관리 - 리스트
	@RequestMapping(value = "/petitelin_notice_list.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> list(HttpServletRequest request, HttpServletResponse response) {
		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn.makeSearchSql("subject", request.getParameterValues("subject"), "like"));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("orderBy", "idx desc");
		Common.PQmap(map, request);

		result = dbConn.recordSet(QUERY_ROOT + ".list", map);

		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}

	//공지사항/뉴스 관리 - 등록 화면
	@RequestMapping(value = "/petitelin_notice_input")
	public ModelAndView input(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("list", dbConn.recordSet(QUERY_ROOT + ".view", Common.paramToMap(request.getParameterMap())));
		mv.setViewName("petitelin/petitelin_notice_input.tiles");
		return mv;
	}

	//공지사항/뉴스 관리 - 등록
	@RequestMapping(value = "/petitelin_notice_insert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView insertDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		List<List<String>> tnumbnail = new ArrayList<>();

		try {
			tnumbnail.add(0, Arrays.asList(DIR_ROOT+ "/thumb", "180", "200"));
			List<String> addFile = imgCon.uploadImgFile(((MultipartHttpServletRequest) request).getFiles("file"), request, "req", DIR_ROOT, tnumbnail);
			for (String filename : addFile) {
				map.put("filename", filename);
			}
		} catch (IOException e) {
		}
		if (!Common.isNullOrEmpty(map.get("idx"))) {
			dbConn.recordSet(QUERY_ROOT + ".update", map);
		} else {
			dbConn.recordSet(QUERY_ROOT + ".insert", map);
		}

		mv.setViewName("redirect:/views/petitelin/petitelin_notice_list");
		return mv;
	}

	//공지사항/뉴스 관리 - 삭제
	@RequestMapping(value = "/petitelin_notice_delete.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView deleteDB(ModelAndView mv, HttpServletRequest request) {
		dbConn.recordSet(QUERY_ROOT + ".delete", request.getParameter("idx"));
		mv.setViewName("redirect:/views/petitelin/petitelin_notice_list");
		return mv;
	}
}
