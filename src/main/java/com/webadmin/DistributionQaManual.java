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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/views/distribution/qaManual")
public class DistributionQaManual {
	private final String DIR_ROOT = "distributionQaManual";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;
	@Resource(name = "dbConn2")
	private DbConn dbConn2;
	@Resource
	private FileCon fileCon;
	@Resource
	private ImgCon imgCon;
	@Resource
	private MemberDTO4 memberDTO4;

	@RequestMapping(value = "/list")
	public ModelAndView list(ModelAndView mv, HttpServletRequest request) {
		List<String> searchList = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();

		String[] ser_code1 = {request.getParameter("code1")};
		String[] ser_code2 = {request.getParameter("code2")};

		if (Common.isNullOrEmpty(request.getParameter("code1"))) {
			ser_code1 = new String[]{"%"};
		}
		if (Common.isNullOrEmpty(request.getParameter("code2"))) {
			ser_code2 = new String[]{"%"};
		}
		searchList.add(dbConn2.makeSearchSql("brand", ser_code1, "like"));
		searchList.add(dbConn2.makeSearchSql("code2", ser_code2, "like"));
		searchList.removeAll(Collections.singleton(null));

		map.put("sqlItem", String.join(" and ", searchList));
		map.put("orderBy", "A.id desc");
		map.put("totalRecords", Common.defaultValue(request.getParameter("totalRecords"),"0"));
		map.put("curPage", Common.defaultValue(request.getParameter("curPage"),"1"));
		map.put("pageSize", Common.defaultValue(request.getParameter("pageSize"),"40"));

		List<Map> list = new ArrayList<>();
		list = dbConn2.recordSet(QUERY_ROOT + ".paging", map);

		map.put("totalRecords", list.size() > 0 ? list.get(0).get("totalRecords") : 0);

		mv.addObject("returnParam", map);
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("list", Common.nullToEmpty(list));
		//브랜드
		mv.addObject("code1", dbConn.recordSet("common.query.code1",null));

		mv.setViewName("distribution/qaManual/list.tiles");
		return mv;
	}

	@RequestMapping(value = "/input")
	@Transactional
	public ModelAndView input(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		mv.addObject("code1", dbConn.recordSet("common.query.code1",null));
		mv.addObject("file", dbConn2.recordSet(QUERY_ROOT + ".fileView", map));

		mv.addObject("list", dbConn2.recordSet(QUERY_ROOT + ".inputValue", map));
		mv.setViewName("distribution/qaManual/input.tiles");
		return mv;
	}

	//신규 등록, 수정
	@RequestMapping(value = "/input.run", method = {RequestMethod.POST}, produces = "application/json")
	@Transactional
	public String input(HttpServletRequest request, Authentication authentication) {
		MultipartHttpServletRequest mr = (MultipartHttpServletRequest) request;
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		memberDTO4 = (MemberDTO4) authentication.getPrincipal();
		map.put("write_userid", memberDTO4.get_Id());

		if (Common.isNullOrEmpty(map.get("id"))) { //id가 없으면 (신규등록)
			map.put("brand", request.getParameter("code1"));
			List<Map<String, String>> id = dbConn2.recordSet(QUERY_ROOT + ".insert", map);
			map.put("ini_id", id.get(0).get("ini_id"));

			List<MultipartFile> mf1 = mr.getFiles("qa_1");
			if (!Common.isNullOrEmpty(mf1.get(0).getOriginalFilename())) {
				try {
					List<String> upFile = fileCon.uploadFile(mf1, request, Common.addString(String.valueOf(map.get("ini_id")), "_qa_1"), DIR_ROOT);
					for (String fileName : upFile) map.put("new_name", fileName);
				} catch (IOException e) {
					e.printStackTrace();
				}
				dbConn2.recordSet(QUERY_ROOT + ".qa1Insert", map);
			}

			List<MultipartFile> mf2 = mr.getFiles("qa_2");
			if (!Common.isNullOrEmpty(mf2.get(0).getOriginalFilename())) {
				try {
					List<String> upFile = fileCon.uploadFile(mf2, request, Common.addString(String.valueOf(map.get("ini_id")), "_qa_2"), DIR_ROOT);
					for (String fileName : upFile) map.put("new_name", fileName);
				} catch (IOException e) {
					e.printStackTrace();
				}
				dbConn2.recordSet(QUERY_ROOT + ".qa2Insert", map);
			}

			List<MultipartFile> mf3 = mr.getFiles("qa_3");
			if (!Common.isNullOrEmpty(mf3.get(0).getOriginalFilename())) {
				try {
					List<String> upFile = fileCon.uploadFile(mf3, request, Common.addString(String.valueOf(map.get("ini_id")), "_qa_3"), DIR_ROOT);
					for (String fileName : upFile) map.put("new_name", fileName);
				} catch (IOException e) {
					e.printStackTrace();
				}
				dbConn2.recordSet(QUERY_ROOT + ".qa3Insert", map);
			}

			List<MultipartFile> mf4 = mr.getFiles("qa_4");
			if (!Common.isNullOrEmpty(mf4.get(0).getOriginalFilename())) {
				try {
					List<String> upFile = fileCon.uploadFile(mf4, request, Common.addString(String.valueOf(map.get("ini_id")), "_qa_4"), DIR_ROOT);
					for (String fileName : upFile) map.put("new_name", fileName);
				} catch (IOException e) {
					e.printStackTrace();
				}
				dbConn2.recordSet(QUERY_ROOT + ".qa4Insert", map);
			}

		} else { //수정
			dbConn2.recordSet(QUERY_ROOT + ".update", map);
			List<MultipartFile> mf1 = mr.getFiles("qa_1");
			if (!Common.isNullOrEmpty(mf1.get(0).getOriginalFilename())) {
				try {
					List<String> upFile = fileCon.uploadFile(mf1, request, Common.addString(String.valueOf(map.get("id")), "_qa_1"), DIR_ROOT);
					for (String fileName : upFile) map.put("new_name", fileName);
				} catch (IOException e) {
					e.printStackTrace();
				}
				dbConn2.recordSet(QUERY_ROOT + ".qa1Update", map);
			}

			List<MultipartFile> mf2 = mr.getFiles("qa_2");
			if (!Common.isNullOrEmpty(mf2.get(0).getOriginalFilename())) {
				try {
					List<String> upFile = fileCon.uploadFile(mf2, request, Common.addString(String.valueOf(map.get("id")), "_qa_2"), DIR_ROOT);
					for (String fileName : upFile) map.put("new_name", fileName);
				} catch (IOException e) {
					e.printStackTrace();
				}
				dbConn2.recordSet(QUERY_ROOT + ".qa2Update", map);
			}

			List<MultipartFile> mf3 = mr.getFiles("qa_3");
			if (!Common.isNullOrEmpty(mf3.get(0).getOriginalFilename())) {
				try {
					List<String> upFile = fileCon.uploadFile(mf3, request, Common.addString(String.valueOf(map.get("id")), "_qa_3"), DIR_ROOT);
					for (String fileName : upFile) map.put("new_name", fileName);
				} catch (IOException e) {
					e.printStackTrace();
				}
				dbConn2.recordSet(QUERY_ROOT + ".qa3Update", map);
			}

			List<MultipartFile> mf4 = mr.getFiles("qa_4");
			if (!Common.isNullOrEmpty(mf4.get(0).getOriginalFilename())) {
				try {
					List<String> upFile = fileCon.uploadFile(mf4, request, Common.addString(String.valueOf(map.get("id")), "_qa_4"), DIR_ROOT);
					for (String fileName : upFile) map.put("new_name", fileName);
				} catch (IOException e) {
					e.printStackTrace();
				}
				dbConn2.recordSet(QUERY_ROOT + ".qa4Update", map);
			}
			if (!Common.isNullOrEmpty(map.get("del_qa"))) {
				dbConn2.recordSet(QUERY_ROOT + ".qaDelete", map);
			}
		}
		return "redirect:/views/distribution/qaManual/list";
	}

	@RequestMapping(value = "/delete.run")
	public String delete(HttpServletRequest request) {
		dbConn2.recordSet(QUERY_ROOT + ".delete", request.getParameter("id"));
		dbConn2.recordSet(QUERY_ROOT + ".fileDelete", request.getParameter("id"));

		return "redirect:/views/distribution/qaManual/list";
	}

	//파일 업로드
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
