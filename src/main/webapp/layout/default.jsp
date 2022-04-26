<%@ page import="java.net.InetAddress" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ page language="java" contentType="text/html; charset=EUC-KR" pageEncoding="EUC-KR" isELIgnored="false" %>

<!DOCTYPE html>
<html>
<head>
	<title><tiles:insertAttribute name="title" ignore="true"/></title>

	<script src="/assets/fixed_js/jquery/jquery-3.4.1.js"></script>
	<link rel="stylesheet" href="/assets/fixed_js/jquery/jquery-ui-1.12.1.css"/>
	<script src="/assets/fixed_js/jquery/jquery-ui-1.12.1.js"></script>

	<script src="/assets/js/common.js?ver=2"></script>
	<script src="/assets/js/commonProcess.js"></script>
	<script src="/assets/js/code.js"></script>
	<script src="/assets/js/loading.js"></script>
	<script src="/assets/js/selectBox.js"></script>

	<tiles:insertAttribute name="include" ignore="true"/>

	<link rel="stylesheet" href="/assets/css/loading.css" />
	<link rel="stylesheet" href="/assets/css/pqgrid.custom.css" />
	<link rel="stylesheet" href="/assets/css/common.css?ver=2"/>
	<link rel="stylesheet" href="/assets/css/selectBox.css" />
</head>
<body>

<tiles:insertAttribute name="left" ignore="true" role=""/>
<tiles:insertAttribute name="content"/>

</body>
<script language="javascript">
	var curPageChange = false;

	function nowHeight() {
		var nowHeight = window.innerHeight;
		$('[name="pqFixHeight"]').each(function () {
			nowHeight = nowHeight - $(this).outerHeight(true)
		})

		return nowHeight;
	}
	$(document).ready(function () {
		//로딩바 숨기기
		$.hideLoading({allowHide: true});

		//윈도우사이즈가 변경되면 height 변경
		$(window).resize(function () {
			if ($("#grid_json_scroll").length>0) {
				var height = nowHeight()
				$("#grid_json_scroll").pqGrid('option', 'height', height).pqGrid('refresh');
			}
		});

		//프로그램 제목 입력
		if (document.getElementsByClassName("menuSelected").length>0) document.getElementsByName("label_title")[0].innerHTML = document.getElementsByClassName("menuSelected")[0].innerHTML;

		$('input[type="text"]').keydown(function() {
			if (event.keyCode === 13) {
				event.preventDefault();
			};
		});

		if ($("#searchForm").length > 0) {
			$("#searchForm").find("input").each(function() {
				// 검색 조건 변경여부 확인
				$(this).on("change", function() {
					curPageChange = true;
				});
				// 엔터 입력시 검색 처리
				$(this).on("keyup", function() {
					if(event.keyCode==13) searchV();
				});
			});
        }
	})
</script>
</html>