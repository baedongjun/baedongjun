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
@RequestMapping("/views/btob/sales")
public class BtoBSales {
	private final String DIR_ROOT = "btobSales";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;
	@Resource
	private FileCon fileCon;
	@Resource
	private ImgCon imgCon;
	@Resource
	private MemberDTO4 memberDTO4;


	@RequestMapping(value = "/notice_list")
	public ModelAndView list(ModelAndView mv, HttpServletRequest request) {
		//�귣�� �迭
		List<Map> product_brand = new ArrayList<>();
		product_brand = dbConn.recordSet(QUERY_ROOT + ".product_brand");
		mv.addObject("product_brand", product_brand);
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));

		mv.setViewName("btob/sales/notice_list.pq");
		return mv;
	}

	@RequestMapping(value = "/notice_list.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> list(HttpServletRequest request, HttpServletResponse response) {
		Map<String, String> map = new HashMap<>();
		List<String> searchList = new ArrayList<>();

		if (!Common.isNullOrEmpty(request.getParameter("product_brand"))) {
			searchList.add(Common.addString(" and id in ( select distinct notice_id from [petitelin_CRM3]..[b2b_notice_brand] where ", dbConn.makeSearchSql("brand", request.getParameterValues("product_brand"), "in"), " )"));
		}
		searchList.add(dbConn.makeSearchSql("subject", request.getParameterValues("subject"), "like"));
		searchList.removeAll(Collections.singleton(null));

		map.put("sqlItem", String.join(" and ", searchList));
		map.put("orderBy", "top_gu desc,id desc");
		Common.PQmap(map, request);

		String iniId = "id";

		List<Map> list = new ArrayList<>(); //����¡ list
		List<Map> brand = new ArrayList<>(); //����귣�� list
		List<Map> company = new ArrayList<>(); //�����ü list

		list = dbConn.recordSet(QUERY_ROOT + ".noticeList", map); //paging

		Map<String, String> sub = Common.subQuery(list, iniId);
		brand = dbConn.recordSet(QUERY_ROOT + ".brandList", sub);
		company = dbConn.recordSet(QUERY_ROOT + ".companyList", sub);

		Map<String, Object> returnParam = new HashMap<>();
		Common.PQresultMap(returnParam, map.get("curPage"), list);
		returnParam.put("data", Common.nullToEmpty(Common.combineRecordSet(iniId, list, brand, company)));

		return returnParam;
	}

	@RequestMapping(value = "/notice_insert")
	public ModelAndView insert(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> param = Common.paramToMap(request.getParameterMap());

		List<List<Map>> insert = dbConn.recordSet(QUERY_ROOT+".insert",param);

		mv.addObject("list",insert.get(0));//�Խñ� ������
		mv.addObject("brand",insert.get(1));//���õ� �귣��
		mv.addObject("channel",insert.get(2));//���õ� ä��
		mv.addObject("company",insert.get(3));

		//�귣�� �迭
		mv.addObject("product_brand", dbConn.recordSet(QUERY_ROOT + ".product_brand"));

		mv.setViewName("btob/sales/notice_insert.tiles");
		return mv;
	}


	@RequestMapping(value = "/brand_goal_sales_channel_view.run")
	@ResponseBody
	public Map<String, Object> brand_goal_sales_channel_view(HttpServletRequest request, HttpServletResponse response) {
		Map<String, String> map = new HashMap<>();
		map.put("channel", request.getParameter("channel"));

		Map resultMap = new HashMap();
		resultMap.put("data", dbConn.recordSet(QUERY_ROOT + ".brandChannelAjax", map));

		return resultMap;
	}

	@RequestMapping(value = "/notice_insert.run")
	public String insert(HttpServletRequest request, Authentication authentication) {
		MultipartHttpServletRequest mr = (MultipartHttpServletRequest) request;
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		List<MultipartFile> mf = mr.getFiles("uploadFile");
		try {
			List<String> upFile = fileCon.uploadFile(mf, request, "req", DIR_ROOT);
			for (String fileName : upFile) map.put("uploadFile", fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}

		memberDTO4 = (MemberDTO4) authentication.getPrincipal();
		map.put("admin_member_id", memberDTO4.get_Id());

		//insert, update
		if (Common.isNullOrEmpty(map.get("id"))) { //id�� ������(���ο� ���̸�) insert
			List<Map<String, String>> id = dbConn.recordSet(QUERY_ROOT + ".noticeInsert", map);
			map.put("this_id", id.get(0).get("this_id"));

			if (!Common.isNullOrEmpty(map.get("product_brand"))) {
				List<String> product_brand_array = Common.paramToArray(map.get("product_brand"),",");
				for (int i = 0; i < product_brand_array.size(); i++) {
					String product_brand = product_brand_array.get(i);
					map.put("product_brand", product_brand);
					dbConn.recordSet(QUERY_ROOT + ".brandInsert", map);
				}
			}

			if (!Common.isNullOrEmpty(map.get("company_id"))) {
				List<String> company_id_array = Common.paramToArray(map.get("company_id"),",");
				for (int i = 0; i < company_id_array.size(); i++) {
					String company_id = company_id_array.get(i);
					map.put("company_id", company_id);
					dbConn.recordSet(QUERY_ROOT + ".companyInsert", map);
				}
			}

		} else { // id�� ������(���ο� ���� �ƴϸ�) ����
			dbConn.recordSet(QUERY_ROOT + ".noticeUpdate", map);

			dbConn.recordSet(QUERY_ROOT + ".brandUpdateDelete", request.getParameter("id"));
			if (!Common.isNullOrEmpty(map.get("product_brand"))) {
				List<String> product_brand_array = Common.paramToArray(map.get("product_brand"),",");
				for (int i = 0; i < product_brand_array.size(); i++) {
					String product_brand = product_brand_array.get(i);
					map.put("product_brand", product_brand);
					dbConn.recordSet(QUERY_ROOT + ".brandupdate", map);
				}
			}

			dbConn.recordSet(QUERY_ROOT + ".companyUpdateDelete", request.getParameter("id"));
			if (!Common.isNullOrEmpty(map.get("company_id"))) {
				List<String> company_id_array = Common.paramToArray(map.get("company_id"),",");
				for (int i = 0; i < company_id_array.size(); i++) {
					String company_id = company_id_array.get(i);
					map.put("company_id", company_id);
					dbConn.recordSet(QUERY_ROOT + ".companyUpdate", map);
				}
			}
		}
		return "redirect:/views/btob/sales/notice_list";
	}

	@RequestMapping(value = "notice_delete.run")
	public String delete(HttpServletRequest request) { //����
		dbConn.recordSet(QUERY_ROOT + ".noticeDelete", request.getParameter("id"));
		dbConn.recordSet(QUERY_ROOT + ".brandDelete", request.getParameter("id"));
		dbConn.recordSet(QUERY_ROOT + ".companyDelete", request.getParameter("id"));

		return "redirect:/views/btob/sales/notice_list";
	}


	/*--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

	@RequestMapping(value = "/qanda_list")
	public ModelAndView qandaList(HttpServletRequest request, ModelAndView mv) {
		Map<String, Object> map = new HashMap<>();
		List<String> searchList = new ArrayList<>();

		String[] depth = {"a"};
		String[] b2byn = {"y"};
		searchList.add(dbConn.makeSearchSql("name", request.getParameterValues("userid"), "like"));
		searchList.add(dbConn.makeSearchSql("subject", request.getParameterValues("subject"), "like"));
		searchList.add(dbConn.makeSearchSql("depth", depth, "="));
		searchList.add(dbConn.makeSearchSql("b2byn", b2byn, "="));
		searchList.removeAll(Collections.singleton(null));

		map.put("sqlItem", String.join(" and ", searchList));
		map.put("orderBy", "num desc, id desc");
		map.put("totalRecords", Common.defaultValue(request.getParameter("totalRecords"),"0"));
		map.put("curPage", Common.defaultValue(request.getParameter("curPage"),"1"));
		map.put("pageSize", Common.defaultValue(request.getParameter("pageSize"),"10"));

		List<Map<String, Object>> list = new ArrayList<>();
		list = dbConn.recordSet(QUERY_ROOT + ".qandaPaging", map);

		List<String> nums = new ArrayList<>();
		for (Map<String, Object> num : list) nums.add(String.valueOf(num.get("num")));

		Map<String, String> numList = new HashMap<>();
		numList.put("num", String.join(",", nums));

		List<Map> result = new ArrayList<>();
		result = dbConn.recordSet(QUERY_ROOT + ".qandaList", numList);

		map.put("totalRecords", list.size() > 0 ? list.get(0).get("totalRecords") : 0);

		mv.addObject("returnParam", map);
		mv.addObject("pageSize", Common.defaultValue(request.getParameter("pageSize"),"10"));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("list", Common.nullToEmpty(result));

		mv.setViewName("btob/sales/qanda_list.tiles");
		return mv;
	}

	@RequestMapping(value = "/qanda_view")
	public ModelAndView qandaView(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("list", dbConn.recordSet(QUERY_ROOT + ".qandaView", Common.paramToMap(request.getParameterMap())));
		mv.setViewName("btob/sales/qanda_view.tiles");
		return mv;
	}

	@RequestMapping(value = "/qanda_input")  //�������� �űԵ������ id�� ����
	public ModelAndView qandaInput(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		if (!Common.isNullOrEmpty(map.get("id"))) { //id�� ���� �ƴϸ� (��, ���� ����ϴ� ���� �ƴϸ�
			mv.addObject("list",dbConn.recordSet(QUERY_ROOT + ".qandaInput", map));
		}
		mv.addObject("choice", Common.paramToMap(request.getParameterMap()));
		mv.setViewName("btob/sales/qanda_input.tiles");
		return mv;
	}

	//�ű� ��� �Ǵ� ����
	@RequestMapping(value = "qanda_input.run", method = {RequestMethod.POST}, produces = "application/json")
	@Transactional
	public String qandaInputDB(HttpServletRequest request, Authentication authentication) {
		MultipartHttpServletRequest mr = (MultipartHttpServletRequest) request;
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		//���� ���ε�
		List<MultipartFile> mf = mr.getFiles("uploadFile");
		try {
			List<String> upFile = fileCon.uploadFile(mf, request, "req", DIR_ROOT);
			for (String fileName : upFile) map.put("uploadFile", fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}

		memberDTO4 = (MemberDTO4) authentication.getPrincipal();
		map.put("userid", memberDTO4.get_Id());

		if (map.get("choice").equals("insert")) {
			map.put("b2byn", "y");
			dbConn.recordSet(QUERY_ROOT + ".qandaInsert", map);
		} else if (map.get("choice").equals("correct")) {
			dbConn.recordSet(QUERY_ROOT + ".qandaUpdate", map);
		} else if (map.get("choice").equals("delete")) {
			dbConn.recordSet(QUERY_ROOT + ".qandaDelete", request.getParameter("id"));
		} else if (map.get("choice").equals("answer")) {
			memberDTO4 = (MemberDTO4) authentication.getPrincipal();
			map.put("b2byn", "n"); //����

			List<Map<String, String>> maxDepth = new ArrayList<>();
			maxDepth = dbConn.recordSet(QUERY_ROOT + ".maxDepth", map);

			String new_depth = "";
			if (Common.isNullOrEmpty(maxDepth.get(0).get("max_depth"))) {
				new_depth = map.get("depth") + "a";
			} else {
				new_depth = maxDepth.get(0).get("max_depth") + "a";
				char str = new_depth.substring((maxDepth.get(0).get("max_depth").length() - 1)).charAt(0);
				int asc = str;
				int ascplus = asc + 1;

				if (asc == 90) {
					new_depth = map.get("depth") + "a";
				} else {
					new_depth = map.get("depth") + ((char) ascplus);
				}
			}
			map.put("new_depth", new_depth);

			dbConn.recordSet(QUERY_ROOT + ".answer", map);
		}


		return "redirect:/views/btob/sales/qanda_list";
	}

	//notice, qanda ���� ���ε�
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