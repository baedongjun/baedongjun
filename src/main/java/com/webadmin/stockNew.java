package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.common.ExcelCon;
import com.common.member.MemberDTO4;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/views/stock")
public class stockNew {

	private final String DIR_ROOT = "stock";
	private final String QUERY_ROOT = "stockNew.query";

	@Resource(name = "dbConn")
	private DbConn dbConn;
	@Resource(name = "dbConn2")
	private DbConn dbConn2;
	@Resource(name = "excelCon")
	private ExcelCon excelCon;

	// 금일 재고현황
	@RequestMapping(value = "/stock_new")
	public ModelAndView list(ModelAndView mv, HttpServletRequest request) {
		Map<String, String[]> paramMap = new HashMap();
		paramMap.put("hideProduct", new String[]{"n"});
		paramMap.put("codeGroup", new String[]{"242"});
		paramMap.putAll(request.getParameterMap());

		Map<String, String> hideProduct = new HashMap();
		hideProduct.put("n", "hProduct_id=0");
		hideProduct.put("y", "hProduct_id>0");

		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn.makeSearchSql("goods_name", request.getParameterValues("goods_name"), "like"));
		sqlItemList.add(dbConn.makeSearchSql("code2name", request.getParameterValues("code2name"), "like"));
		sqlItemList.add(dbConn.makeSearchSql2(request.getParameterValues("hideProduct"), hideProduct, "n"));
		sqlItemList.removeAll(Collections.singleton(null));

		List<String> sqlItemList2 = new ArrayList<>();
		sqlItemList2.add(dbConn.makeSearchSql("code1", request.getParameterValues("codeGroup"), "in", "242"));
		sqlItemList2.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("sqlItem2", String.join(" and ", sqlItemList2).replace("'", "''"));

		mv.addObject("productCode", dbConn.recordSet(QUERY_ROOT + ".productCode", null));
		mv.addObject("list", dbConn2.recordSet(QUERY_ROOT + ".stockNew", map));

		mv.addObject("searchParam", Common.paramToSearch(paramMap));
		mv.setViewName("stock/stock_new.tiles");
		return mv;
	}

	// 금일 재고 등록
	@RequestMapping(value = "/stockNew.run", method = {RequestMethod.POST})
	@Transactional
	public ModelAndView stockNew(ModelAndView mv, HttpServletRequest request) {
		MultipartHttpServletRequest mr = (MultipartHttpServletRequest) request;

		try {
			List<String> dbField = Arrays.asList("codename", "stock", "wdate");
			List<Map<String, String>> list = excelCon.uploadExcel(mr.getFiles("inputFile"), dbField, "제품");
			dbConn2.recordSet(QUERY_ROOT + ".excelStockInsert", list);
		} catch (IOException e) {e.printStackTrace();}

		mv.setViewName("redirect:/views/stock/stock_new");
		return mv;
	}

	// 재고 상태변경
	@RequestMapping(value = "/stockChange.run", method = {RequestMethod.POST})
	@Transactional
	public ModelAndView stockChange(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		String[] selectField = new String[]{"correct", "stock", "product_id"};
		List<Map<String, String>> list = Common.paramToList(selectField, request.getParameterMap());

		Map<String, Object> totalMap = new HashMap<>();
		totalMap.put("list", list);
		totalMap.put("param", map);
		dbConn.recordSet(QUERY_ROOT + ".stockChange", totalMap);

		mv.setViewName("redirect:/views/stock/stock_new");
		return mv;
	}

	// 금일 3PL 입출고 조정
	@RequestMapping(value = "/stock_3PL")
	public ModelAndView stock_3PL_list(ModelAndView mv, HttpServletRequest request) {
		Map<String, String[]> paramMap = new HashMap();

		paramMap.putAll(request.getParameterMap());

		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn.makeSearchSql("CUST_CD", request.getParameterValues("specialIniList"), "="));
		sqlItemList.add(dbConn.makeSearchSql("gubun", request.getParameterValues("gubun"), "="));
		sqlItemList.add(dbConn.makeSearchSql("code2name", request.getParameterValues("code2name"), "like"));
		sqlItemList.add(dbConn.makeSearchSql("code1", request.getParameterValues("codeGroup"), "in"));
		sqlItemList.removeAll(Collections.singleton(null));

		String[] goods_name = request.getParameterValues("goods_name");

		if (!Common.isNullOrEmpty(request.getParameterValues("goods_name"))) {
			goods_name = new String[]{goods_name[0].replace("-","")};
		}

		List<String> sqlItemList2 = new ArrayList<>();
		sqlItemList2.add(dbConn2.makeSearchSql("replace(codename2,'-','')", goods_name, "like"));
		sqlItemList2.add(dbConn2.makeSearchSql("name", request.getParameterValues("goods_name"), "like"));
		sqlItemList2.removeAll(Collections.singleton(null));

		String sqlItem2 = String.join(" or ", sqlItemList2);
		sqlItem2 = (!Common.isNullOrEmpty(sqlItem2)) ? " and (" + sqlItem2 + " )" : "";

		Map<String, String> map = new HashMap<>();
		map.put("sqlItem", String.join(" and ", sqlItemList) + sqlItem2);

		mv.addObject("productCode", dbConn.recordSet(QUERY_ROOT + ".productCode", null));
		mv.addObject("list", dbConn2.recordSet(QUERY_ROOT + ".stock3PL", map));
		mv.addObject("specialIniList", dbConn.recordSet(QUERY_ROOT + ".specialIniList", map));

		mv.addObject("searchParam", Common.paramToSearch(paramMap));
		mv.setViewName("stock/stock_3PL.tiles");
		return mv;
	}

	// 금일 3PL 입출고 조정 등록
	@RequestMapping(value = "/stock_3PL.run", method = {RequestMethod.POST})
	@ResponseBody
	@Transactional
	public Map<String, Object> stock_3PL(HttpServletRequest request,Authentication authentication) {
		MultipartHttpServletRequest mr = (MultipartHttpServletRequest) request;
		MemberDTO4 member = (MemberDTO4) authentication.getPrincipal();
		Map resultMap = new HashMap();

		try {
			List<String> dbField = Arrays.asList("codename", "stock", "wdate","gubun","detail","specialIniId");
			List<Map<String, String>> list = excelCon.uploadExcel(mr.getFiles("inputFile"), dbField, "제품");

			for (int i = 0; i < list.size(); i++) {
				list.get(i).put("admin_member_id", member.getUser_id());
				list.get(i).put("specialIniId", request.getParameter("specialIniId"));
				
				// 비고값 체크
				if ((list.get(i).get("gubun").equals("조정") || list.get(i).get("gubun").equals("이동")) && list.get(i).get("detail") == null) {
					resultMap.put("resultCode", "empty");
					return resultMap;
				}

				// 지난 일자 등록 체크
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date wdate = null;
				Date nowDate = null;

				try {
					wdate = dateFormat.parse(list.get(i).get("wdate"));
					nowDate = dateFormat.parse(Common.nowDate());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				int compare = wdate.compareTo(nowDate);

				if (compare < 0) {
					dbConn2.recordSet(QUERY_ROOT + ".excelStock3PLInsert", list);
					resultMap.put("resultCode", "dateConfirm");
					return resultMap;
				}
			}

			dbConn2.recordSet(QUERY_ROOT + ".excelStock3PLInsert", list);
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("resultCode", "success");
		return resultMap;
	}
}