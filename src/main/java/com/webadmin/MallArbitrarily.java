package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.member.MemberDTO4;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "views/mall")
public class MallArbitrarily {

	private final String DIR_ROOT = "mall";
	private final String QUERY_ROOT = DIR_ROOT + "Arbitrarily.query";

	@Resource(name = "dbConn")
	private DbConn dbConn;


	// 총 사판구매 금액 확인
	@RequestMapping(value = "/arbitrarily")
	public ModelAndView arbitrarily(ModelAndView mv, Authentication authentication, HttpServletRequest request) {
		MemberDTO4 member = (MemberDTO4) authentication.getPrincipal();
		String only = Common.defaultValue(request.getParameter("only"),"company");
		String thisSite = Common.defaultValue(request.getParameter("thisSite"),"per");

		if (thisSite.equals("per")) {
			mv.addObject("bought", dbConn.recordSet(QUERY_ROOT + ".bought", new HashMap() {{put("user_id",member.get_Id());}}));
		}
		mv.addObject("only",only);
		mv.addObject("thisSite",thisSite);
		mv.setViewName("mall/arbitrarily.tiles");
		return mv;
	}

	// 회원검색
	@RequestMapping(value = "/searchMem.run")
	@ResponseBody
	public Map<String, Object> searchMem(HttpServletRequest request, HttpServletResponse response) {
		Map resultMap = new HashMap();
		resultMap.put("data", dbConn.recordSet(QUERY_ROOT + ".searchMem", new HashMap() {{put("search_mem",request.getParameter("search_mem"));}}));

		return resultMap;
	}

	// 단품/팩키지 검색
	@RequestMapping(value = "/searchProduct.run")
	@ResponseBody
	public Map<String, Object> search(HttpServletRequest request, HttpServletResponse response) {
		Map resultMap = new HashMap();
		resultMap.put("data", dbConn.recordSet(QUERY_ROOT +".search", request.getParameter("search")));

		return resultMap;
	}

	// 단품/팩키지 구입
	@RequestMapping(value = "/arbitrarily_input")
	public ModelAndView input(ModelAndView mv, HttpServletRequest request) {
		if (!Common.isNullOrEmpty(request.getParameterNames())) {
			String[] productField = new String[]{"pack_content_id", "category_site_id", "category_name_id"};
			List<Map> map = Common.paramToMulti(productField, request.getParameterValues(Common.paramIsArray("pack_content_id", request)), "/");
			List<Map> resultMap = dbConn.recordSet(QUERY_ROOT + ".input", map);
			mv.addObject("list", resultMap);
		}
		mv.setViewName("mall/arbitrarily_input.bare");
		return mv;
	}

	// 단품/팩키지 구입
	@RequestMapping(value = "/arbitrarily_insert_DB")
	public ModelAndView insertDB(ModelAndView mv, HttpServletRequest request,Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		String thisSite = request.getParameter("thisSite");
		String userid = request.getParameter("userid");
		String only = request.getParameter("only");
		String market = request.getParameter("market");
		String user_name = ((MemberDTO4) authentication.getPrincipal()).getUser_name();
		map.put("id", ((MemberDTO4) authentication.getPrincipal()).get_Id());
		map.put("user_name", user_name);

		if (userid.equals("GUEST") || only.equals("company")) {
			if (only.equals("company")) {
				if (thisSite.equals("per")) {
					userid = user_name + "[사판]";
				} else {
					userid = user_name + "[타사]";
				}
			} else {
				userid = Common.makeFileName();
			}

			map.put("userid", userid);
			map.put("nowRank", "99");
			map.put("order_addr_id", dbConn.recordSet(QUERY_ROOT + ".insertAddr", map).get(0).toString());
		} else {
			map.put("nowRank", Common.defaultValue(dbConn.recordSet(QUERY_ROOT + ".userRank", map).get(0).toString(), "1"));
			map.put("order_addr_id", "0");
		}

		if (request.getParameterValues("thisTitle").length > 1) {
			map.put("subject", request.getParameterValues("thisTitle")[0] + " 외 " + (request.getParameterValues("thisTitle").length - 1) + "종");
		} else {
			map.put("subject", request.getParameterValues("thisTitle")[0]);
		}

		int baesong_money = 0;
		String paymode = "0";
		String process = "";

		int sales_money = Integer.parseInt(request.getParameter("sales_money").replace(",",""));
		if (only.equals("company")) {
			baesong_money = 2500;
			paymode = "3";
		} else if (market.equals("27")) {
			baesong_money = 0;
			paymode = "4";
		} else {
			process = "n";
			sales_money = 0;
			baesong_money = 0;
			paymode = "4";
		}
		map.put("baesong_money", Integer.toString(baesong_money));
		map.put("paymode", paymode);
		map.put("sales_money", Integer.toString(sales_money));
		map.put("last_money", Integer.toString(sales_money + baesong_money));
		map.put("delivery", "1");
		map.put("coupon_money", "0");
		map.put("baesong_free", "n");
		map.put("addpoint", "0");
		map.put("usepoint", "0");

		String[] selectField = new String[]{"pack_content_id","product_id","order_id","category_site_id","category_name_id","preMoney","cnt","rrp","discount","point","promotion_id","promotionM","coupon_join_id","couponM","ready"};
		List<Map<String, String>> orderItem = Common.paramToList(selectField, request.getParameterMap());

		Map<String, Object> totalMap = new HashMap<>();
		totalMap.put("param",map);
		totalMap.put("orderItem",orderItem);
		dbConn.recordSet(QUERY_ROOT + ".insert", totalMap);

		mv.setViewName("redirect:/views/mall/arbitrarily_input");
		return mv;
	}
}