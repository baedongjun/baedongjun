package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/views/sales")
public class SalesNonTax {

	private final String DIR_ROOT = "salesNonTax";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;

	//면세 기초상품 매출 - 화면 및 리스트
	@RequestMapping(value = "/nonTax_list")
	public ModelAndView list(ModelAndView mv, HttpServletRequest request) {

		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn.makeSearchSql("market", request.getParameterValues(Common.paramIsArray("market", request)), "="));
		sqlItemList.add(dbConn.makeSearchSql("yearValue", Optional.ofNullable(request.getParameterValues("yearV")).orElse(new String[]{Common.nowDate().substring(0, 4)}), "="));
		sqlItemList.add(dbConn.makeSearchSql("monthValue", Optional.ofNullable(request.getParameterValues("monthV")).orElse(new String[]{Common.nowDate().substring(5, 7)}), "="));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("orderBy", "B.code1name, A.market, A.product_id, brandRow desc, marketRow desc");

		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("list", Common.nullToEmpty(dbConn.recordSet(QUERY_ROOT + ".list", map)));
		mv.setViewName("sales/nonTax_list.tiles");
		return mv;
	}
}
