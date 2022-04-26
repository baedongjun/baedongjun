package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/views/emploee")
public class EmploeePrivate {
	private final String DIR_ROOT = "emploeePrivate";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;

	@RequestMapping(value = "/private")
	public ModelAndView list(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("list", dbConn.recordSet(QUERY_ROOT + ".list", null));
		mv.setViewName("emploee/private.tiles");

		return mv;
	}

	@RequestMapping(value = "/private_insert.run")
	@Transactional
	public String insertDB(HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		if (map.get("choice").equals("insert")) {
			dbConn.recordSet(QUERY_ROOT + ".insert", map);
		} else {
			String[] etc = map.get("etc").split(",");
			String[] id = map.get("id").split(",");
			String[] url = map.get("url").split(",");

			Map<String, String> list = new HashMap<>();
			for (int i = 0; i < id.length; i++) {
				list.put("etc", etc[i].trim());
				list.put("id", id[i].trim());
				list.put("url", url[i].trim());

				if (url[i].equals(" ") || url[i].equals("")) {
					dbConn.recordSet(QUERY_ROOT + ".delete", list);
				} else {
					dbConn.recordSet(QUERY_ROOT + ".update", list);
				}
			}
		}
		return "redirect:/views/emploee/private";
	}
}
