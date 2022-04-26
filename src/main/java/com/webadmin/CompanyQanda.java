
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
import javax.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/views/company")
public class CompanyQanda {

	private final String DIR_ROOT = "companyQanda";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn2")
	private DbConn dbConn2;
	@Resource
	private FileCon fileCon;
	@Resource
	private MemberDTO4 memberDTO4;

	//리스트
	@RequestMapping(value = "/qanda_list")
	public ModelAndView list(ModelAndView mv, HttpServletRequest request, HttpSession session) {
		session.setAttribute("proce", request.getParameter("proce"));
		//검색
		List<String> searchList = new ArrayList<>();
		searchList.add(dbConn2.makeSearchSql("gubun", request.getParameterValues("proc_gubun"), "="));
		searchList.add(dbConn2.makeSearchSql("user_name", request.getParameterValues("user_name"), "like"));
		searchList.add(dbConn2.makeSearchSql("subject", request.getParameterValues("subject"), "like"));
		searchList.add(dbConn2.makeSearchSql("content", request.getParameterValues("content"), "like"));
		searchList.add(dbConn2.makeSearchSql("depth", new String[]{"a"}, "="));
		searchList.add(dbConn2.makeSearchSql("is_top", new String[]{"n"}, "="));
		searchList.add(dbConn2.makeSearchSql("proce", new String[]{session.getAttribute("proce").toString()}, "="));
		searchList.removeAll(Collections.singleton(null));

		//페이징
		Map<String, Object> param = new HashMap<>();
		param.put("sqlItem", String.join(" and ", searchList));
		param.put("orderBy", "num desc,depth asc");
		param.put("proce", Common.defaultValue(String.valueOf(session.getAttribute("proce")),"program"));
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

		param.put("totalRecords", numList.size() > 0 ? numList.get(0).get("totalRecords") : 0);

		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", param);
		mv.addObject("list", Common.nullToEmpty(result));

		mv.setViewName("company/qanda_list.tiles");
		return mv;
	}

	//상세보기
	@RequestMapping(value = "/qanda_view")
	public ModelAndView view(ModelAndView mv, HttpServletRequest request) {
		Map<String,String> param = Common.paramToMap(request.getParameterMap());
		mv.addObject("list", dbConn2.recordSet(QUERY_ROOT + ".view",param));
		mv.addObject("returnParam",param.get("id"));
		mv.setViewName("company/qanda_view.tiles");
		return mv;
	}

	//입력 페이지 (신규 등록, 수정, 추가)
	@GetMapping(value = "/qanda_input")
	public ModelAndView input(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> param = Common.paramToMap(request.getParameterMap());
		if (param.containsKey("id")) {
			mv.addObject("list", dbConn2.recordSet(QUERY_ROOT + ".detailView", param));
		} else {
			mv.addObject("list", new ArrayList() {{
				add(param);
			}});
		}
		mv.setViewName("company/qanda_input.tiles");
		return mv;
	}

	//실제 입력 및 수정
	@PostMapping(value = "qanda_insert.run")
	@Transactional
	public ModelAndView insertDB(HttpServletRequest request, Authentication authentication,ModelAndView mv) {
		MultipartHttpServletRequest mr = (MultipartHttpServletRequest) request;
		Map<String, String> param = Common.paramToMap(request.getParameterMap());

		List<MultipartFile> upFile = mr.getFiles("uploadFile");
		try {
			List<String> addFile = fileCon.uploadFile(upFile,request, "req", DIR_ROOT);
			for (String filename : addFile) {
				param.put("uploadFile", filename);
			}
		} catch (IOException e) {
		}

		memberDTO4 = (MemberDTO4) authentication.getPrincipal();
		param.put("userid", memberDTO4.getUser_id());

		if (Common.isNullOrEmpty(param.get("id")) && Common.isNullOrEmpty(param.get("num"))) {
			dbConn2.recordSet(QUERY_ROOT + ".insert", param);
		} else if (!Common.isNullOrEmpty(param.get("id")) && !Common.isNullOrEmpty(param.get("num"))) {
			dbConn2.recordSet(QUERY_ROOT + ".correct", param);
		} else {
			List<Map<String, String>> maxDepth = new ArrayList<>();
			maxDepth = dbConn2.recordSet(QUERY_ROOT + ".maxDepth", param);

			String new_depth = "";
			if (Common.isNullOrEmpty(maxDepth.get(0).get("max_depth"))) {
				new_depth = param.get("depth") + "a";
			} else {
				new_depth = maxDepth.get(0).get("max_depth");
				char str = new_depth.substring((maxDepth.get(0).get("max_depth").length() - 1)).charAt(0);
				int asc = str;
				int ascplus = asc + 1;

				if (asc == 90) {
					new_depth = param.get("depth") + "a";
				} else {
					new_depth = param.get("depth") + ((char) ascplus);
				}
			}
			param.put("new_depth", new_depth);
			dbConn2.recordSet(QUERY_ROOT + ".answer", param);
		}
		mv.setViewName("redirect:/views/company/qanda_list?proce=" + param.get("proce"));
		return mv;
	}
}

