package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/views/pay")
public class pay {
	private final String DIR_ROOT = "pay";
	private final String QUERY_ROOT = DIR_ROOT + ".query";
	private final String FILE_PATH = "/_vir0001/product_img/ini_product";

	@Resource(name = "dbConn")
	private DbConn dbConn;

	// 구매내역 리스트
	@RequestMapping(value = "/list")
	public ModelAndView list(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("pay/list.pq");
		return mv;
	}
	@RequestMapping(value = "/list.run")
	@ResponseBody
	public Map<String, Object> list(HttpServletRequest request, HttpServletResponse response) {
		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn.makeSearchSql("thisSite", request.getParameterValues("thisSite"), "="));
		sqlItemList.add(dbConn.makeSearchSql("market", request.getParameterValues("market"), "="));
		sqlItemList.add(dbConn.makeSearchSql("jumuncode", request.getParameterValues("jumuncode"), "likeXO"));
		sqlItemList.add(dbConn.makeSearchSql("userid", request.getParameterValues("userid"), "likeXO"));
		sqlItemList.removeAll(Collections.singleton(null));

		List<String> sqlItemList2 = new ArrayList<>();
		sqlItemList2.add(dbConn.makeSearchSql("name", request.getParameterValues("name"), "likeXO"));
		sqlItemList2.add(dbConn.makeSearchSql("mobile", request.getParameterValues("mobile"), "likeXO"));
		sqlItemList2.removeAll(Collections.singleton(null));

		List<String> sqlItemList3 = new ArrayList<>();
		sqlItemList3.add(dbConn.makeSearchSql("UserName", request.getParameterValues("name"), "likeXO"));
		sqlItemList3.add(dbConn.makeSearchSql("Mobile", request.getParameterValues("mobile"), "likeXO"));
		sqlItemList3.removeAll(Collections.singleton(null));

		List<String> sqlItemList4 = new ArrayList<>();
		sqlItemList4.add(dbConn.makeSearchSql("invoice", request.getParameterValues("invoice"), "likeXO"));
		sqlItemList4.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("sqlItem2", String.join(" and ", sqlItemList2));
		map.put("sqlItem3", String.join(" and ", sqlItemList3));
		map.put("sqlItem4", String.join(" and ", sqlItemList4));
		map.put("orderBy", "A.id desc");
		Common.PQmap(map, request);

		List<Map> result = new ArrayList<>();
		List<Map> result0 = new ArrayList<>();
		List<Map> result1 = new ArrayList<>();
		Map resultMap = new HashMap();
		String iniID = "order_buy_ini_id";

		if (map.get("sqlItem").length()==0 && map.get("sqlItem2").length()==0 && map.get("sqlItem3").length()==0 && map.get("sqlItem4").length()==0) {
			result = dbConn.recordSet(QUERY_ROOT + ".listNoSearch", map);
		} else {
			result = dbConn.recordSet(QUERY_ROOT + ".list", map);
		}
		result0 = dbConn.recordSet(QUERY_ROOT +".listSub", Common.subQuery(result,iniID));
		result1 = dbConn.recordSet(QUERY_ROOT +".listSub2", Common.subQuery(result,iniID));

		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", Common.nullToEmpty(Common.combineRecordSet(iniID,result,result0,result1)));

		return resultMap;
	}

	// 구매내역 상세
	@RequestMapping(value = "/view")
	public ModelAndView view(ModelAndView mv, HttpServletRequest request) {
		List<Map> result = new ArrayList<>();
		result =  dbConn.recordSet(QUERY_ROOT + ".viewIniData", Common.paramToMap(request.getParameterMap()));

		List<List<Map>> list = dbConn.recordSet(QUERY_ROOT + ".view",result.get(0));
		mv.addObject("view", list.get(0));
		mv.addObject("mallHistory", list.get(1));
		mv.addObject("memberLevel", list.get(2));
		mv.addObject("addr", list.get(3));
		mv.addObject("pack", list.get(4));

		mv.addObject("id", request.getParameter("id"));
		mv.setViewName("pay/view.tiles");
		return mv;
	}

	// 구매내역 상세(구매변경)
	@RequestMapping(value = "/view_buy")
	public ModelAndView viewBuy(ModelAndView mv, HttpServletRequest request) {
		mv.setViewName("pay/view_buy.bare");
		return mv;
	}
}