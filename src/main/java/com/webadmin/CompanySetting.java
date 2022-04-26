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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/views/company")
public class CompanySetting {

	private final String DIR_ROOT = "company";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn2")
	private DbConn dbConn2;
	@Resource
	private FileCon fileCon;
	@Resource
	private ImgCon imgCon;
	@Resource
	private MemberDTO4 memberDTO4;

	//리스트
	@RequestMapping(value = "/setting_list")
	public ModelAndView list(ModelAndView mv, HttpServletRequest request) {
		//검색
		List<String> sqlList = new ArrayList<>();
		String[] depth = {"a"};
		String[] top = {"n"};
		sqlList.add(dbConn2.makeSearchSql("gubun", request.getParameterValues("company_gubun"), "="));
		sqlList.add(dbConn2.makeSearchSql("subject", request.getParameterValues("subject"), "like"));
		sqlList.add(dbConn2.makeSearchSql("depth", depth, "="));
		sqlList.add(dbConn2.makeSearchSql("[top]", top, "="));
		sqlList.removeAll(Collections.singleton(null));

		//페이징
		Map<String, Object> param = new HashMap<>();
		param.put("sqlItem", String.join(" and ", sqlList));
		param.put("orderBy", "num desc,depth asc");
		param.put("totalRecords", Common.defaultValue(request.getParameter("totalRecords"),"0"));
		param.put("curPage", Common.defaultValue(request.getParameter("curPage"),"1"));
		param.put("pageSize", Common.defaultValue(request.getParameter("pageSize"),"20"));

		List<Map<String, Object>> numList = new ArrayList<>();
		numList = dbConn2.recordSet(QUERY_ROOT + ".paging", param);

		List<String> nums = new ArrayList<>();
		for (Map<String, Object> num : numList) nums.add(String.valueOf(num.get("num")));

		param.put("num", String.join(",", nums));

		List<Map> result = new ArrayList<>();
		result = dbConn2.recordSet(QUERY_ROOT + ".list", param);

		param.put("totalRecords",numList.size() > 0 ? numList.get(0).get("totalRecords") : 0);

		mv.addObject("returnParam", param);
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("list", Common.nullToEmpty(result));

		mv.setViewName("company/setting_list.tiles");
		return mv;
	}

	//상세 내용
	@RequestMapping(value = "/setting_view")
	public ModelAndView view(ModelAndView mv, HttpServletRequest request) {
		Map<String,String> param = Common.paramToMap(request.getParameterMap());
		mv.addObject("list", dbConn2.recordSet(QUERY_ROOT + ".view", param));
		mv.addObject("returnParam",param);
		mv.setViewName("company/setting_view.tiles");
		return mv;
	}

	//입력페이지 : parent 등록, child 등록, 수정하기 할때
	@GetMapping(value = "/setting_input")
	public ModelAndView input(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> param = Common.paramToMap(request.getParameterMap());
		if (param.containsKey("id")) {
			mv.addObject("list", dbConn2.recordSet(QUERY_ROOT + ".detailView", param));
		} else {
			mv.addObject("list", new ArrayList() {{
				add(param);
			}});
		}
		mv.setViewName("company/setting_input.tiles");
		return mv;
	}

	//insert(parent 글, child 글), update
	@PostMapping(value = "/setting_insert.run")
	@Transactional
	public String insertDB(HttpServletRequest request, Authentication authentication) {
		MultipartHttpServletRequest mr = (MultipartHttpServletRequest) request;
		Map<String, String> param = Common.paramToMap(request.getParameterMap());

		List<MultipartFile> mf = mr.getFiles("uploadfile");
		try {
			List<String> upFile = fileCon.uploadFile(mf, request, "req", DIR_ROOT);
			for (String fileName : upFile) param.put("uploadfile", fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}

		memberDTO4 = (MemberDTO4) authentication.getPrincipal();
		param.put("user_name", memberDTO4.getUser_name());

		// parent 등록
		if (Common.isNullOrEmpty(param.get("num")) && Common.isNullOrEmpty(param.get("id"))) {
			dbConn2.recordSet(QUERY_ROOT + ".insert", param);
			// 수정
		} else if (!Common.isNullOrEmpty(param.get("num")) && !Common.isNullOrEmpty(param.get("id"))) {
			dbConn2.recordSet(QUERY_ROOT + ".correctNew", param);
			// child 등록
		} else {
			List<Map<String, String>> result = new ArrayList<>();
			result = dbConn2.recordSet(QUERY_ROOT + ".maxDepth", param);
			param.put("new_depth", childDepth(param.get("depth"), result.get(0).get("max_depth")));
			dbConn2.recordSet(QUERY_ROOT + ".answer", param);
		}
		return "redirect:/views/company/setting_list";
	}

	//list > 첨부파일 삭제
	@PostMapping(value = "setting_delete.run")
	public String deleteDB(HttpServletRequest request) {
		dbConn2.recordSet(QUERY_ROOT + ".fileDelete", Common.paramToMap(request.getParameterMap()));
		return "redirect:/views/company/setting_list";
	}

	//depth 계산
	public String childDepth(String parentDepth, String brotherDepth) {

		String new_depth = "";

		if (Common.isNullOrEmpty(brotherDepth)) {
			new_depth = parentDepth + "a";
		} else {
			new_depth = brotherDepth;
			char str = new_depth.substring((brotherDepth.length() - 1)).charAt(0);
			int asc = str;
			int ascplus = asc + 1;

			if (asc == 90) {
				new_depth = parentDepth + "a";
			} else {
				new_depth = parentDepth + ((char) ascplus);
			}
		}
		return new_depth;
	}

	//파일 업로드
	@RequestMapping(value = "/imgUpload.run", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, Object> imgUpload(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<>();
		String filePath = Common.addString("editorImgUpload/images_", Common.nowDate().replace("-", ""));
		try {
			List<String> upFileImg = imgCon.uploadImgFile(((MultipartHttpServletRequest) request).getFiles("upload"), request, "ed",  filePath, null);
			for (String filename : upFileImg) { resultMap.put("fileName", filename); }
			resultMap.put("uploaded", 1);
			resultMap.put("url", Common.addString(request.getRequestURL().toString().replace(request.getRequestURI(),""), Common.FILE_ROOT_PATH, filePath, "/", resultMap.get("fileName").toString()));
		} catch (Exception e) {
		}
		return resultMap;
	}
}