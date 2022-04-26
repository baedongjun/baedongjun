package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.common.FileCon;
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
@RequestMapping("/views/cs")
public class CSWorkShare {

	private final String DIR_ROOT = "csWorkShare";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;
	@Resource
	private FileCon fileCon;
	@Resource
	private ImgCon imgCon;

	// 업무공유게시판 - 리스트
	@RequestMapping(value = "/work_share_list")
	public ModelAndView list(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("cs/work_share_list.pq");
		return mv;
	}

	// 업무공유게시판 - 리스트 json
	@RequestMapping(value = "/work_share_list.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> list(HttpServletRequest request, HttpServletResponse response) {

		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn.makeSearchSql("subject", request.getParameterValues("subject"), "like"));
		sqlItemList.add(dbConn.makeSearchSql("user_name", request.getParameterValues("writer"), "like"));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("orderBy", "top_gu desc,id desc");

		Common.PQmap(map, request);

		result = dbConn.recordSet(QUERY_ROOT + ".list", map);

		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}

	// 업무공유게시판 - 상세
	@RequestMapping(value = "/work_share_input")
	public ModelAndView view(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("list", dbConn.recordSet(QUERY_ROOT + ".view", Common.paramToMap(request.getParameterMap())));
		mv.setViewName("cs/work_share_input.tiles");
		return mv;
	}

	// 업무공유게시판 - 신규등록
	@RequestMapping(value = "/work_share_insert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView insertDB(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("admin_member_id", Common.defaultValue(((MemberDTO4) authentication.getPrincipal()).get_Id(), "1"));
		map.put("top_gu", Common.defaultValue(map.get("top_gu"), "0"));

		try {
			List<String> addFile = fileCon.uploadFile(((MultipartHttpServletRequest) request).getFiles("file"), request, "req", DIR_ROOT);
			for (String filename : addFile) {
				map.put("uploadFile", filename);
			}
		} catch (IOException e) {
		}
		dbConn.recordSet(QUERY_ROOT + ".insert", map);
		mv.setViewName("redirect:/views/cs/work_share_list");
		return mv;
	}

	// 업무공유게시판 - 업데이트
	@RequestMapping(value = "/work_share_update.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView updateDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("top_gu", Common.defaultValue(map.get("top_gu"), "0"));
		try {
			List<String> addFile = fileCon.uploadFile(((MultipartHttpServletRequest) request).getFiles("file"), request, "req", DIR_ROOT);
			for (String filename : addFile) {
				map.put("uploadFile", filename);
			}
		} catch (IOException e) {
		}

		map.put("content", map.get("content").replaceAll("(\r\n|\r|\n|\n\r)", "<br>"));

		dbConn.recordSet(QUERY_ROOT + ".update", map);
		mv.setViewName("redirect:/views/cs/work_share_list");
		return mv;
	}

	// 업무공유게시판 - 삭제
	@RequestMapping(value = "/work_share_delete.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView deleteDB(ModelAndView mv, HttpServletRequest request) {
		dbConn.recordSet(QUERY_ROOT + ".delete", Common.paramToMap(request.getParameterMap()));
		mv.setViewName("redirect:/views/cs/work_share_list");
		return mv;
	}

	//ckEditor imgUpload
	@RequestMapping(value = "/imgUpload.run", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, Object> imgUpload(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<>();
		String filePath = Common.addString("editorImgUpload/images_", Common.nowDate().replace("-", ""));
		try {
			List<String> upFileImg = imgCon.uploadImgFile(((MultipartHttpServletRequest) request).getFiles("upload"), request, "ed", filePath, null);
			for (String filename : upFileImg) {
				resultMap.put("fileName", filename);
			}
			resultMap.put("uploaded", 1);
			resultMap.put("url", Common.addString(request.getRequestURL().toString().replace(request.getRequestURI(), ""), Common.FILE_ROOT_PATH, filePath, "/", resultMap.get("fileName").toString()));
		} catch (Exception e) {
		}
		return resultMap;
	}
}

