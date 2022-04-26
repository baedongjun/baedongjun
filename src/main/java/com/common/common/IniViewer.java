package com.common.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/views/")
public class IniViewer {

	@RequestMapping(value = "{fileName}")
	public ModelAndView iniView(ModelAndView mv, @PathVariable String fileName) {
		mv.setViewName(fileName + ".tiles");
		return mv;
	}

	@RequestMapping(value = "{folderName}/{fileName}")
	public ModelAndView iniView(ModelAndView mv, @PathVariable String folderName, @PathVariable String fileName) {
		mv.setViewName(folderName + "/" + fileName + ".tiles");
		return mv;
	}

	@RequestMapping(value = "{folderName1}/{folderName2}/{fileName}")
	public ModelAndView iniView(ModelAndView mv, @PathVariable String folderName1, @PathVariable String folderName2, @PathVariable String fileName) {
		mv.setViewName(folderName1 + "/" + folderName2 + "/" + fileName + ".tiles");
		return mv;
	}

	@RequestMapping(value = "{folderName1}/{folderName2}/{folderName3}/{fileName}")
	public ModelAndView iniView(ModelAndView mv, @PathVariable String folderName1, @PathVariable String folderName2, @PathVariable String folderName3, @PathVariable String fileName) {
		mv.setViewName(folderName1 + "/" + folderName2 + "/" + folderName3 + "/" + fileName + ".tiles");
		return mv;
	}
}