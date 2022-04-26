<%@ page import="java.net.InetAddress" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>


<link rel="StyleSheet" href="/assets/fixed_js/accordion/accordion.css" type="text/css"/>
<script type="text/javascript" src="/assets/fixed_js/accordion/accordion.js"></script>

<style>
	.menu {
		position:fixed;
		left: 0px;
		top: 0px;
		width: 210px;
		padding: 5px 10px 5px 10px;
		height: 100%;
		background-color: #dcdcdc;
		overflow: auto;
		z-index:1000;
	}
	.menu::-webkit-scrollbar { /* Hide scrollbar for Chrome, Safari and Opera */
		display: none;
	}
	.menu { /* Hide scrollbar for IE, Edge and Firefox */
		-ms-overflow-style: none; /* IE and Edge */
		scrollbar-width: none; /* Firefox */
	}

	.display_info {
		width:200px;
		padding:5px;
		text-align:center;
	}
</style>

<div class="menu">
	<sec:authentication var="principal" property="principal"/>
	<div class="display_info" style="color:black;font-weight:bold;font-size:25px;padding:15px 0px 15px 0px">PETITELIN</div>
	<div class="display_info" style="background-color:#ffffff;font-size:15px">
		<div style="font-size:15px;font-weight:bold;padding:5px">
			<c:choose>
				<c:when test="${principal.user_id eq 'master'}">
					<a href="/views/left_menu/list">
							${principal.user_name}
					</a>
				</c:when>
				<c:otherwise>
					${principal.user_name}
				</c:otherwise>
			</c:choose>
		</div>
		<div><%=request.getRemoteAddr()%></div>
	</div>
	<div class="display_info" style="background-color:#ffffff;padding-bottom:15px">
		<a href="/logout" target="_top">
			<img src="/assets/img/logout_btn.gif" align="center">
		</a>
	</div>
	<div class="display_info" style="margin-bottom:10px;">
		<img src="/assets/img/helpWeb.jpg"
		     onclick="window.open('/assets/help/adminHELPv1.1.html','addEtc','top=0,left=0,width=1300,height=800,scrollbars=yes,location=no,resizable=yes')"
		     style="cursor:hand;margin-right:5px">
		<img src="/assets/img/helpFile.jpg" onclick="location.href='/assets/help/adminHELPv1.1.chm'"
		     style="cursor:hand"
		     title="※도움말(파일) 다운로드후 실행시 화면이 안나오실경우 해당 파일에 마우스 오른쪽 버튼을 누르시고 속성에 들어가셔서 맨 아래 보안쪽에 있는 차단해제 버튼을 눌러 주시면 됩니다.">
	</div>
	<div style="margin-bottom:50px">

		<div id='design_div' name='design_div' style='color:white;background-color:#c806a2;border:dotted 3px #c806a2;position:fixed;width:370px;height:70px;text-align:center;display:none;'>
			<b>디자인 요청</b> 관련해서
			<br>새로운 댓글이 등록되었습니다.
			<br>"디자인 전용 > 나의 요청/접수" 에서 확인하시기 바랍니다.
		</div>

		<script type="text/javascript">
			var accordion = new accordion('accordion');
			$.ajax({
				type: 'POST',
				url : "/accodianMenu/list.run",
				async:false,
				success : function(result) {
					var value = result.data;

					for (var i=0; i < value.length; i++){
						if(String(value[i].url_link).indexOf("http://www") > 0 || String(value[i].url_link).indexOf("http2://") > 0){
							targetWin = "_new"
						}else{
							targetWin = ""
						}
						if(value[i].addDay < 15){
							accordion.add(value[i].id, value[i].ini_id, "<span style='color:#ff69b4'>" + value[i].subject + "</span>", value[i].url_link, value[i].help, targetWin);
						}else{
							accordion.add(value[i].id, value[i].ini_id, value[i].subject, value[i].url_link, value[i].help, targetWin);
						}
					}
				}
			});
			document.write(accordion);

			// 1분에 한번씩 알림 체크 호출
			setInterval(confirm_Design,1000 * 60 * 1);

			function confirm_Design(val) {
				var thisMin = new Date().getMinutes();
				var processStart = false;

				if (val=="ini") {
					processStart = true;
				}
				// 1시간에 4분씩 3번 나눠서 체크
				if ((thisMin>=18 && thisMin<=22) || (thisMin>=38 && thisMin<=42) || thisMin>=58 || thisMin<=02) processStart = true;

				if (processStart) {
					$.ajax({
						type: "POST",
						url : "/views/design/alarmCheck.run",
						async:false,
						success : function(result) {
							if (result.data != null) {
								var cnt = result.data.cnt;

								if (cnt > 0) {
									$("#design_div").css("display","");
									$("#design_div").css("right",0);
									$("#design_div").css("bottom",0);
								} else {
									$("#design_div").css("display","none");
								}
							}
						}
					});
				}
			}

			//레프트 메뉴 클릭시 로딩바
			$(".accordion-group").find("a").each(function() {
				$(this).on("click", function() {
					$.showLoading({allowHide: true});

					setCookie("pqPaging", "")
				});
			});
		</script>
	</div>
</div>
	<div style="margin-left:240px">