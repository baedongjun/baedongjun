//mall Arbitrarily

adminMaster = new Array()
adminMaster.push(new Array("김석","김석"))
adminMaster.push(new Array("김명성","김명성"))
adminMaster.push(new Array("심석영","심석영"))
adminMaster.push(new Array("박선미","박선미"))

adminMaster.push(new Array("최원정","최원정"))
adminMaster.push(new Array("김소연","김소연"))
adminMaster.push(new Array("임아름","임아름"))
adminMaster.push(new Array("박지원","박지원"))

adminMaster.push(new Array("이수진","이수진"))
adminMaster.push(new Array("천봉기","천봉기"))
adminMaster.push(new Array("심종택","심종택"))

adminMaster.push(new Array("황진미","황진미"))
adminMaster.push(new Array("장혜정","장혜정"))
adminMaster.push(new Array("백은이","백은이"))

adminMaster.push(new Array("송지민","송지민"))
adminMaster.push(new Array("정강훈","정강훈"))
adminMaster.push(new Array("윤은정","윤은정"))
adminMaster.push(new Array("도현명","도현명"))
adminMaster.push(new Array("전계희","전계희"))
adminMaster.push(new Array("안태민","안태민"))
adminMaster.push(new Array("황유미","황유미"))

adminMaster.push(new Array("방용규","방용규"))
adminMaster.push(new Array("서준석","서준석"))
adminMaster.push(new Array("최은주","최은주"))
adminMaster.push(new Array("한세현","한세현"))

adminMaster.push(new Array("관리자","관리자"))
adminMaster.sort()



//brandmd

price_guideline_gubun = new Array()
price_guideline_gubun.push(new Array("기본 가이드","1"))
price_guideline_gubun.push(new Array("프로모션 가이드","2"))
price_guideline_gubun.push(new Array("환원 가이드","3"))
price_guideline_gubun.push(new Array("일정 및 기타 관리","4"))
price_guideline_gubun.push(new Array("년간이슈 및 경쟁사현황","5"))


// btob


channel = new Array()
channel.push(new Array("자사매장","1", "국내영업팀"))
channel.push(new Array("백화점","2", "국내영업팀"))
channel.push(new Array("면세점","3", "국내영업팀"))
channel.push(new Array("빅로드샵","4", "국내영업팀"))
channel.push(new Array("로드샵","5", "국내영업팀"))
channel.push(new Array("특판","6", "국내영업팀"))
channel.push(new Array("페어","7", "국내영업팀"))
channel.push(new Array("할인점","8", "국내영업팀"))
channel.push(new Array("미입점행사","9", "국내영업팀"))
channel.push(new Array("전문점","10", "국내영업팀"))	//---> 마지막 할당

channel.push(new Array("쿠팡","21","46", "MD팀(사입)"))
channel.push(new Array("위메프","22","46", "MD팀(사입)"))
channel.push(new Array("티몬","23","46", "MD팀(사입)"))
channel.push(new Array("GS","24","46", "MD팀(사입)"))	//---> 마지막 할당


channel.push(new Array("미정","31","27", "해외전략팀"))

company_saletype = new Array()
company_saletype.push(new Array("사입","1"))
company_saletype.push(new Array("위탁","2"))
company_saletype.push(new Array("자사매장","3"))




// views / company //////////////////////////////////////////////////////////////////////////////////////////////////////

proc_gubun = new Array()
proc_gubun.push(new Array("서버","서버", "program"))
proc_gubun.push(new Array("컴포넌트","컴포넌트", "program"))
proc_gubun.push(new Array("개발관련","개발관련", "program"))
proc_gubun.push(new Array("업무프로세스","업무프로세스", "program"))
proc_gubun.push(new Array("업무/프로그램 프로세스","업무/프로그램 프로세스", "program"))
proc_gubun.push(new Array("앱","앱", "program"))
proc_gubun.push(new Array("JAVA","JAVA", "program"))
proc_gubun.push(new Array("기타","기타", "program"))
proc_gubun.push(new Array("-----","", "design"))

company_gubun = new Array()
company_gubun.push(new Array("SERVER","SERVER"))
company_gubun.push(new Array("CLIENT","CLIENT"))
company_gubun.push(new Array("IDE","IDE"))
company_gubun.push(new Array("SCM","SCM"))
company_gubun.push(new Array("BUILD","BUILD"))
company_gubun.push(new Array("CI/CD","CI/CD"))
company_gubun.push(new Array("INSTALL","INSTALL"))
company_gubun.push(new Array("BASIC","BASIC"))

// cs/afterService //////////////////////////////////////////////////////////////////////////////////////////////////////

as_status = new Array();
as_status.push(new Array("대기","0"));
as_status.push(new Array("접수","1"));
as_status.push(new Array("답변완료","2"));
as_status.push(new Array("진행요청","3"));
as_status.push(new Array("회수완료","4"));
as_status.push(new Array("발송완료","5"));
as_status.push(new Array("취소","7"));

as_yn = new Array();
as_yn.push(new Array("가능","1"));
as_yn.push(new Array("불가능","0"));

as_gubun = new Array();
as_gubun.push(new Array("부속품 발송","0"));
as_gubun.push(new Array("부속품 발송대기","1"));
as_gubun.push(new Array("상품 a/s입고","2"));

money_yn = new Array();
money_yn.push(new Array("유상","y"));
money_yn.push(new Array("무상","n"));

// cs/subsidiaryMaterials //////////////////////////////////////////////////////////////////////////////////////////////////////
//부자재 종류
subsidiary_materials_kind  = new Array()
subsidiary_materials_kind.push(new Array("인쇄물","인쇄물"))
subsidiary_materials_kind.push(new Array("행택","행택"))
subsidiary_materials_kind.push(new Array("상자","상자"))
subsidiary_materials_kind.push(new Array("택배박스","택배박스"))
subsidiary_materials_kind.push(new Array("쇼핑백","쇼핑백"))
subsidiary_materials_kind.push(new Array("속지","속지"))
subsidiary_materials_kind.push(new Array("파우치","파우치"))
subsidiary_materials_kind.push(new Array("스티커","스티커"))
subsidiary_materials_kind.push(new Array("기타","기타"))

//작업 구분
subsidiary_materials_work  = new Array()
subsidiary_materials_work.push(new Array("생산용","생산용"))
subsidiary_materials_work.push(new Array("물류용","물류용"))

//사용여부
gubun  = new Array()
gubun.push(new Array("사용","1"))
gubun.push(new Array("미사용","2"))

// design //////////////////////////////////////////////////////////////////////////////////////////////////////

design_result = new Array()
design_result.push(new Array("<span style='color: red'>대기</span>", "대기"))
design_result.push(new Array("<span style='color: green'>접수</span>", "접수"))
design_result.push(new Array("<span style='color: blue'>처리완료</span>", "처리완료"))
design_result.push(new Array("<span style='color: grey'>보류</span>", "보류"))
design_result.push(new Array("<span style='color: magenta'>취소</span>", "취소"))

divideGubun = new Array()
divideGubun.push(new Array("요청","요청"))
divideGubun.push(new Array("<span style='color: red'>자체</span>","자체"))

design_complete = new Array()
design_complete.push(new Array("순차처리","1"))
design_complete.push(new Array("<span style='color:red'>지정 완료일</span>","2"))

processQuality = new Array()
processQuality.push(new Array("기존데이타 변경",1,"chk"))
processQuality.push(new Array("부분변경",2,"chk"))
processQuality.push(new Array("새로운 디자인",4,"chk"))
processQuality.push(new Array("연차/직접입력",0))

processWorkTime = new Array()
processWorkTime.push(new Array("30m",0))
processWorkTime.push(new Array("1H",1))
processWorkTime.push(new Array("2H",2))
processWorkTime.push(new Array("4H",4))
processWorkTime.push(new Array("6H",6))
processWorkTime.push(new Array("1D",8))
processWorkTime.push(new Array("2D",16))
processWorkTime.push(new Array("3D",24))
processWorkTime.push(new Array("1W",40))
processWorkTime.push(new Array("2W",80))

process = new Array()
process.push(new Array(1,"브랜드(BM)","브랜드런칭","신규","4",0,"normal"))
process.push(new Array(2,"브랜드(BM)","상세페이지","신규","4",0,"normal"))
process.push(new Array(3,"브랜드(BM)","상세페이지 수정","디자인추가","2",0,"normal"))
process.push(new Array(4,"브랜드(BM)","상세페이지 수정","단순이미지변경","1",0,"normal"))
process.push(new Array(5,"브랜드(BM)","정기이벤트","신규","4",24,"normal"))
process.push(new Array(6,"브랜드(BM)","정기이벤트","단순수정","1",8,"normal"))
process.push(new Array(7,"브랜드(BM)","썸네일/배너","일부수정","1",2,"normal"))
process.push(new Array(28,"브랜드(BM)","마케팅업무","신규/수정","4",0,"normal"))	//마지막 할당
process.push(new Array(8,"자사몰","메일링","신규","4",6,"normal"))
process.push(new Array(9,"자사몰","프로모션","신규","4",24,"normal"))
process.push(new Array(10,"자사몰","브랜드위크/카테통합","신규","4",8,"normal"))
process.push(new Array(11,"자사몰","브랜드위크/카테통합","일부수정","1",4,"normal"))
process.push(new Array(12,"자사몰","모바일(app)/체험할인","신규/수정","1",4,"normal"))
process.push(new Array(13,"자사몰","자사몰관리(배너/리뉴얼)","신규/수정","1",0,"normal"))
process.push(new Array(14,"타사몰(MD)","이벤트(몰별/딜)","신규","4",8,"normal"))
process.push(new Array(15,"타사몰(MD)","이벤트(몰별/딜)","top 변경","4",6,"normal"))
process.push(new Array(16,"타사몰(MD)","이벤트(몰별/딜)","50%이상","2",6,"normal"))
process.push(new Array(17,"타사몰(MD)","이벤트(몰별/딜)","50%이하","1",4,"normal"))
process.push(new Array(18,"타사몰(MD)","이벤트(몰별/딜)","가격수정","1",2,"normal"))
process.push(new Array(19,"마케팅","체험단","신규","4",8,"normal"))
process.push(new Array(26,"마케팅","체험단","50%이상","2",4,"normal"))
process.push(new Array(20,"마케팅","체험단","50%이하","1",2,"normal"))
process.push(new Array(21,"마케팅","당첨자/카카오/서포터즈","신규/수정","1",2,"normal"))
process.push(new Array(22,"해외영업","브랜드/상세페이지","신규/수정","1",0,"normal"))
process.push(new Array(23,"국내영업","이벤트/배너","신규/수정","1",0,"normal"))
process.push(new Array(24,"웹디자인","자체","업무","4",0,"designer"))
process.push(new Array(25,"웹디자인","자체","업무외","1",0,"designer"))
process.push(new Array(27,"웹디자인","자체","업무시간보정","4",0,"designer"))

process_name = new Array()
for (var i=0 ; i<process.length ; i++) process_name.push(new Array(process[i][1] +" > "+ process[i][2] +" > "+ process[i][3],process[i][0].toString(),process[i][6]))

market.push(new Array("기타","99","design"))

md_array = new Array()		//소수를 이용한 값
md_array.push(new Array(new Array("기획전","1"), new Array("상품코드","10"),new Array("배너","100")))
md_array.push(new Array(new Array("TOP","1"), new Array("써머리","10"),new Array("배너","100")))

planning_md_array = new Array()
planning_md_array.push(new Array(new Array("http://192.168.0.250:8080/share.cgi?ssid=0HvbEtS","100")
, new Array("http://192.168.0.250:8080/share.cgi?ssid=0W15qOT","11")
, new Array("http://192.168.0.250:8080/share.cgi?ssid=0dv5aJ3","111")))

planning_md_array.push(new Array(new Array("http://192.168.0.250:8080/share.cgi?ssid=0QcrtgD","1")
, new Array("http://192.168.0.250:8080/share.cgi?ssid=0JqoXK8","100")
, new Array("http://192.168.0.250:8080/share.cgi?ssid=0DTezjr","11")
, new Array("http://192.168.0.250:8080/share.cgi?ssid=09gYImR","101")
, new Array("http://192.168.0.250:8080/share.cgi?ssid=0LBzqNL","111")))

planning_md_array.push(new Array(new Array("http://192.168.0.250:8080/share.cgi?ssid=0pnkBhi","1")
, new Array("http://192.168.0.250:8080/share.cgi?ssid=0FzGFiJ","100")
, new Array("http://192.168.0.250:8080/share.cgi?ssid=0vc7u7h","110")
, new Array("http://192.168.0.250:8080/share.cgi?ssid=0dPxTOz","11")
, new Array("http://192.168.0.250:8080/share.cgi?ssid=0DCAwzu","101")
, new Array("http://192.168.0.250:8080/share.cgi?ssid=0C39jj0","111")))

planning_md_array.push(new Array(new Array("http://192.168.0.250:8080/share.cgi?ssid=0VDBppK","1")
, new Array("http://192.168.0.250:8080/share.cgi?ssid=0WuDGoL","10")
, new Array("http://192.168.0.250:8080/share.cgi?ssid=0m1sqcI","100")
, new Array("http://192.168.0.250:8080/share.cgi?ssid=07zRZSJ","11")
, new Array("http://192.168.0.250:8080/share.cgi?ssid=0OXkHDo","110")
, new Array("http://192.168.0.250:8080/share.cgi?ssid=0ApSpj1","101")
, new Array("http://192.168.0.250:8080/share.cgi?ssid=0So7A3a","111")))
planning_md_array.push(new Array("http://192.168.0.250:8080/share.cgi?ssid=0Bua7Hi","신규"))
planning_md_array.push(new Array("http://192.168.0.250:8080/share.cgi?ssid=0xkqXxv","수정"))

planning_file_array = new Array()
planning_file_array.push(new Array("","다운"))

planning_store_array = new Array()
planning_store_array.push(new Array("http://192.168.0.250:8080/share.cgi?ssid=0zGSXQQ","신규"))
planning_store_array.push(new Array("http://192.168.0.250:8080/share.cgi?ssid=09PSUda","수정"))
planning_store_array.push(new Array("http://192.168.0.250:8080/share.cgi?ssid=00DAVxa","다운"))

planning_bm_array = new Array()
planning_bm_array.push(new Array("http://192.168.0.250:8080/share.cgi?ssid=0iBcxgU","신규"))
planning_bm_array.push(new Array("http://192.168.0.250:8080/share.cgi?ssid=0h6u8dF","수정"))
planning_bm_array.push(new Array("http://192.168.0.250:8080/share.cgi?ssid=0G7qiHI","신규"))
planning_bm_array.push(new Array("http://192.168.0.250:8080/share.cgi?ssid=0fiGk8u","수정"))
planning_bm_array.push(new Array("http://192.168.0.250:8080/share.cgi?ssid=0GMbNy6","신규"))
planning_bm_array.push(new Array("http://192.168.0.250:8080/share.cgi?ssid=0jRU4iv","수정"))
planning_bm_array.push(new Array("http://192.168.0.250:8080/share.cgi?ssid=0bQ7scD","신규"))
planning_bm_array.push(new Array("http://192.168.0.250:8080/share.cgi?ssid=0v212a6","신규"))
planning_bm_array.push(new Array("http://192.168.0.250:8080/share.cgi?ssid=0ckXD7U","수정"))
planning_bm_array.push(new Array("http://192.168.0.250:8080/share.cgi?ssid=04maiUk","다운"))

planning_mkt_array = new Array()
planning_mkt_array.push(new Array("http://192.168.0.250:8080/share.cgi?ssid=0HXSHiX","신규"))
planning_mkt_array.push(new Array("http://192.168.0.250:8080/share.cgi?ssid=0U1M59B","수정"))

lineColor = "#00d200"
lineColor0 = "white"
lineColor5 = "#e0e0e0"

lineColor1 = "#ffb062"
lineColor2 = "#8080ff"
lineColor4 = "#ff80c0"

lineColor11 = "#ffff9d"
lineColor12 = "#ffe888"
lineColor14 = "#ffaaac"

var colorSet = new Array()
colorSet.push("#55efc4")
colorSet.push("#81ecec")
colorSet.push("#74b9ff")
colorSet.push("#a29bfe")
colorSet.push("#ffeaa7")
colorSet.push("#fab1a0")
colorSet.push("#ff7675")
colorSet.push("#fd79a8")
colorSet.push("#00b894")
colorSet.push("#00cec9")
colorSet.push("#0984e3")
colorSet.push("#6c5ce7")
colorSet.push("#fdcb6e")
colorSet.push("#e17055")
colorSet.push("#d63031")
colorSet.push("#e84393")
colorSet.push("#f6e58d")
colorSet.push("#ffbe76")
colorSet.push("#ff7979")
colorSet.push("#badc58")
colorSet.push("#7ed6df")
colorSet.push("#e056fd")
colorSet.push("#686de0")
colorSet.push("#30336b")


function viewWorkDay(val) {
	if (val==-1) return "0"
	if (val==0) return "협의"

	countDay_M = parseInt(val / 160)
	countDay_W = parseInt(val % 160 / 40)
	countDay_D = parseInt(val % 160 % 40 / 8)
	countDay_H = val % 8

	returnValue = ""
	if (countDay_M>0) returnValue = returnValue + countDay_M + "M "
	if (countDay_W>0) returnValue = returnValue + countDay_W + "W "
	if (countDay_D>0) returnValue = returnValue + countDay_D + "D "
	if (countDay_H>0) returnValue = returnValue + countDay_H + "H"

	return returnValue
}

function workDay(val,val2,val3,val5) {
	now_date = new Date()
	now_date.setYear(val)
	now_date.setMonth(val2)

	for (var i=1 ; i<=val3 ; i++) {
		now_date.setDate(i)
		if (!(now_date.getDay()==0 || now_date.getDay()==6)) {
			val5.push(i)
		}
	}
}
// cs/delivery
delivery_req_ok = new Array();
delivery_req_ok.push(new Array("<span style='color: blue'>대기</span>", "1"));
delivery_req_ok.push(new Array("<span style='color: red'>처리완료</span>", "3", "chk"));
delivery_req_ok.push(new Array("<span style='color: #ffcc00'>취소</span>", "4"));

delivery_gubun = new Array();
delivery_gubun.push(new Array("회사", "1"));
delivery_gubun.push(new Array("개인", "2"));





//cs//3pl   /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//요청구분
cs_gubun = new Array();
cs_gubun.push(new Array("송장폐기","1"))
cs_gubun.push(new Array("검수요청","2"))
cs_gubun.push(new Array("부품 수기 발송","3"))
cs_gubun.push(new Array("기타","4"))


//처리상태
cs_req = new Array();
cs_req.push(new Array("<span style='color: red'>대기</span>","10"));
cs_req.push(new Array("<span style='color: #FE2EF7'>접수</span>","20","cho"));
cs_req.push(new Array("<span style='color: #ffcc00'>취소</span>","30"));
cs_req.push(new Array("<span style='color: green'>처리완료</span>","40","cho"));



// develop //////////////////////////////////////////////////////////////////////////////////////////////////////

dev_gubun2 = new Array()
dev_gubun2.push(new Array("Table","1"))
dev_gubun2.push(new Array("View","2"))
dev_gubun2.push(new Array("Procedure","3"))
dev_gubun2.push(new Array("File","4"))

req_ok = new Array()
req_ok.push(new Array("<span style='color: red'>대기</span>", "대기", "se","3pl"))
req_ok.push(new Array("<span style='color: #FE2EF7'>접수</span>", "접수", "se","important","3pl"))
req_ok.push(new Array("<span style='color: blue'>처리중</span>", "처리중", "se","important"))
req_ok.push(new Array("<span style='color: green'>처리완료</span>", "처리완료", "se","important","3pl"))
req_ok.push(new Array("<span style='color: grey'>보류</span>", "보류", "se"))
req_ok.push(new Array("<span style='color: #ffcc00'>취소</span>", "취소", "user"))

req_ete = new Array()
req_ete.push(new Array("즉시","즉시"))
req_ete.push(new Array("주중","주중"))
req_ete.push(new Array("장기","장기"))

req_category = new Array()
req_category.push(new Array("결재","결재"))
req_category.push(new Array("사이트","사이트"))
req_category.push(new Array("관리자","관리자"))
req_category.push(new Array("기타","기타"))

dbfile = new Array()
dbfile.push(new Array("DB","1"))
dbfile.push(new Array("File","2"))

team_gu = new Array()
team_gu.push(new Array("요청","요청","chk"))
team_gu.push(new Array("<span style='color: green'>자체</span>","자체","chk"))
team_gu.push(new Array("<span style='color: blue'>APP</span>","APP","chk"))
team_gu.push(new Array("<span style='color: red'>본부장</span>","본부장","chk"))

sch_status = new Array()
sch_status.push(new Array("진행중", "1", "search"))
sch_status.push(new Array("사유", "2", "chk"))
sch_status.push(new Array("중간점검", "3","chk"))
sch_status.push(new Array("<span style='color: green'>완료</span>", "4","chk", "search"))
sch_status.push(new Array("<span style='color: #ffcc00'>취소</span>", "5","chk", "search"))

dist_server = new Array()
dist_server.push(new Array("배포안함","no"))
dist_server.push(new Array("쁘띠엘린몰","store","search"))
dist_server.push(new Array("쁘띠엘린 모바일 Java/jsp 신규","mobile_java","search"))
dist_server.push(new Array("모바일 쁘띠엘린몰","mobile","search"))
dist_server.push(new Array("체험단","experience","search"))
dist_server.push(new Array("관리자","webadmin","search"))
dist_server.push(new Array("몬트라움","site1","search"))
dist_server.push(new Array("몬트라움 관리자","site1Webadmin","search"))
dist_server.push(new Array("몬트라움 체험단","site1Experience","search"))
dist_server.push(new Array("에센루 관리자","site2Webadmin","search"))
dist_server.push(new Array("두두스토리 관리자","site3Webadmin","search"))

dist_status = new Array()
dist_status.push(new Array("<span style='color: #FE2EF7'>배포요청</span>","1"))
dist_status.push(new Array("<span style='color: green'>배포완료</span>","2"))

company = new Array()
company.push(new Array("쁘띠엘린","쁘띠엘린"))
// company.push(new Array("해외","해외")) 사용 안함
company.push(new Array("에센루","에센루"))
company.push(new Array("두두스토리","두두스토리"))
company.push(new Array("몬트라움","몬트라움"))


server_gubun = new Array()
server_gubun.push(new Array("WEB 서버","121.125.70.36"))
server_gubun.push(new Array("DB 서버","121.125.70.44"))
server_gubun.push(new Array("관리자 WEB 서버","121.125.70.39"))
server_gubun.push(new Array("관리자 DB 서버","121.125.70.41"))
server_gubun.push(new Array("모바일 JAVA 서버","121.125.70.38"))
server_gubun.push(new Array("Java/Jsp 서버","121.125.70.37"))
server_gubun.push(new Array("몬트라움 통합 서버","121.125.70.40"))
server_gubun.push(new Array("에센루 WEB 서버","221.143.49.57"))
server_gubun.push(new Array("에센루 DB 서버","221.143.49.39"))
server_gubun.push(new Array("두두스토리 DB/WEB 서버","121.125.70.18"))
server_gubun.push(new Array("두두스토리 Java/Jsp 서버","121.125.70.16"))

server_color = new Array()
server_color.push(new Array("#FFB0D7","121.125.70.36"))
server_color.push(new Array("#F2CFA7","121.125.70.44"))
server_color.push(new Array("#FDFEB2","121.125.70.39"))
server_color.push(new Array("#BEE8B5","121.125.70.41"))
server_color.push(new Array("#DFD4E5","121.125.70.38"))
server_color.push(new Array("#64FFFF","121.125.70.37"))
server_color.push(new Array("#AEE4FF","121.125.70.40"))
server_color.push(new Array("#82A7A4","221.143.49.57"))
server_color.push(new Array("#B75B00","221.143.49.39"))
server_color.push(new Array("#B78B00","121.125.70.18"))
server_color.push(new Array("#B70B00","121.125.70.16"))



schedule_program = new Array()
schedule_program.push(new Array("SQL 스케쥴","SQL 스케쥴"))
schedule_program.push(new Array("작업스케쥴러","작업스케쥴러"))
schedule_program.push(new Array("수동","수동"))
schedule_program.push(new Array("EMP 스케줄","EMP 스케줄"))
schedule_program.push(new Array("기타","기타"))

schedule_type = new Array()
schedule_type.push(new Array("한번","once","chk"))
schedule_type.push(new Array("매일","day"))
schedule_type.push(new Array("매주","week","chk"))
schedule_type.push(new Array("매월","month","chk"))

schedule_type_color = new Array()
schedule_type_color.push(new Array("<span style='color: orange'>한번</span>","once"))
schedule_type_color.push(new Array("<span style='color: gray'>매일</span>","day"))
schedule_type_color.push(new Array("<span style='color: blue'>매주</span>","week"))
schedule_type_color.push(new Array("<span style='color: red'>매월</span>","month"))

schedule_use = new Array()
schedule_use.push(new Array("사용","y"))
schedule_use.push(new Array("사용안함","n"))

schedule_datename = new Array()
schedule_datename.push(new Array("월요일","2"))
schedule_datename.push(new Array("화요일","3"))
schedule_datename.push(new Array("수요일","4"))
schedule_datename.push(new Array("목요일","5"))
schedule_datename.push(new Array("금요일","6"))
schedule_datename.push(new Array("토요일","7"))
schedule_datename.push(new Array("일요일","1"))

schedule_hour = new Array()
for(i=0;i<24;i++){
	schedule_hour.push(new Array(i+" 시",i+""))
}

schedule_min = new Array()
for(i=0;i<60;i++){
	schedule_min.push(new Array(i+" 분",i+""))
}

schedule_month = new Array()
for(i=1;i<=12;i++){
	schedule_month.push(new Array(i+" 월",i+""))
}

schedule_week = new Array()
schedule_week.push(new Array("첫째", "1"))
schedule_week.push(new Array("둘째", "2"))
schedule_week.push(new Array("셋째", "3"))
schedule_week.push(new Array("넷째", "4"))
schedule_week.push(new Array("마지막", "99"))

schedule_day = new Array()
for(i=1;i<=31;i++){
	schedule_day.push(new Array(i+"",i+""))
}
schedule_day.push(new Array("마지막", "99"))

schedule_period = new Array()
schedule_period.push(new Array("5 분","5"))
schedule_period.push(new Array("10 분","10"))
schedule_period.push(new Array("15 분","15"))
schedule_period.push(new Array("30 분","30"))
schedule_period.push(new Array("1 시간","60"))

schedule_period_end = new Array()
schedule_period_end.push(new Array("무기한으로","0"))
schedule_period_end.push(new Array("15 분","15"))
schedule_period_end.push(new Array("30 분","30"))
schedule_period_end.push(new Array("1 시간","60"))
schedule_period_end.push(new Array("12 시간","720"))
schedule_period_end.push(new Array("1 일","1440"))

//event / joriwon_2016
joriwon = new Array()
joriwon.push(new Array("결한방(광명점)","20"))
joriwon.push(new Array("궁 클래식(강남점, 역삼동)","1"))
joriwon.push(new Array("궁 클래식(삼성점, 삼성동)","2"))
joriwon.push(new Array("기통맘(송파점, 방이동)","19"))
joriwon.push(new Array("디엘린(용인시)","7"))
joriwon.push(new Array("라크렘(분당구, 율동)","4"))
joriwon.push(new Array("라테라(목동점)","3"))
joriwon.push(new Array("라테라(노원점, 상계동)","8"))
joriwon.push(new Array("르베르쏘(강서점, 가양동)","16"))
joriwon.push(new Array("르베르쏘(광진점, 구의동)","18"))
joriwon.push(new Array("르베르쏘(목동점, 신정동)","17"))
joriwon.push(new Array("벌스데이(마곡동)","11"))
joriwon.push(new Array("아이리스(영등포동)","14"))
joriwon.push(new Array("우먼메디칼(부평구)","21")) //마지막할당
joriwon.push(new Array("제일병원(충무로)","10"))
joriwon.push(new Array("차병원(강남점, 역삼동)","5"))
joriwon.push(new Array("차병원(분당점, 야탑동)","6"))
joriwon.push(new Array("첫단추(창경궁점, 원남동)","12"))
joriwon.push(new Array("첫단추(성북점, 동소문동)","13"))
joriwon.push(new Array("포모나(용인시)","9"))
joriwon.push(new Array("후(대현동)","15"))

// management //////////////////////////////////////////////////////////////////////////////////////////////////////
moneyValue = new Array()
moneyValue.push(new Array("원가","cost"))
moneyValue.push(new Array("RRP","rrp"))

market_free = new Array()
market_free.push(new Array("무료체험(MKT)","20"))
market_free.push(new Array("무료체험(MD)","44"))
market_free.push(new Array("무료체험(BM)","51"))
market_free.push(new Array("서포터즈","42"))

// marketing/instagram

insta_gubun = new Array();
insta_gubun.push(new Array("일반 콘텐츠","1", "chk"));
insta_gubun.push(new Array("리그램 콘텐츠","2", "chk"));
insta_gubun.push(new Array("이벤트","3"));

insta_process = new Array();
insta_process.push(new Array("대기","1"));
insta_process.push(new Array("<span style='color: orange'>접수</span>","2"));
insta_process.push(new Array("<span style='color:blue'>처리완료</span>","5"));
insta_process.push(new Array("<span style='color:gray'>취소</span>","99"));

//views/distribution/freightCar
car_gubun = new Array()
car_gubun.push(new Array("<span style='color: green'>요청</span>","1","in"))
car_gubun.push(new Array("<span style='color: blue'>접수</span>","2","out"))
car_gubun.push(new Array("<span style='color: blue'>배차완료</span>","3","out"))
car_gubun.push(new Array("<span style='color: red'>배송완료(거래명세서)</span>","4","out"))
car_gubun.push(new Array("<span style='color: green'>물류취소</span>","98","in"))
car_gubun.push(new Array("<span style='color: blue'>운송취소</span>","99","out"))


car_ton = new Array()
car_ton.push(new Array("1톤","1"))
car_ton.push(new Array("1.4톤","2"))
car_ton.push(new Array("2.5톤","9")) //마지막할당
car_ton.push(new Array("3.5톤","3"))
car_ton.push(new Array("5톤","4"))
car_ton.push(new Array("5톤(축)","5"))
car_ton.push(new Array("11톤","6"))
car_ton.push(new Array("오토바이","7"))
car_ton.push(new Array("다마스","8"))


//views/distribution/manualRequest
return_gubun = new Array()
return_gubun.push(new Array("물류 재입고","y"))
return_gubun.push(new Array("재입고 안함","n"))



manual_request_req_status = new Array()
manual_request_req_status.push(new Array("<font color='red'>결재대기</font>","1"))
manual_request_req_status.push(new Array("결재완료","2", "chk4"))
//manual_request_req_status.push(new Array("<font color='green'>물류 접수</font>","3")) //물류접수와 물류처리중의 차이가 없다고 해서 사용 안함
manual_request_req_status.push(new Array("<font color='orange'>물류 처리중</font>","4", "chk1", "chk2", "chk5"))
manual_request_req_status.push(new Array("<font color='blue'>발송 완료</font>","5", "chk2"))
manual_request_req_status.push(new Array("<font color='orange'>관리중</font>","6", "chk3"))
manual_request_req_status.push(new Array("<font color='blue'>처리완료</font>","7"))
manual_request_req_status.push(new Array("<font color='magenta'>취소</font>","99", "chk1", "chk2", "chk4", "chk5"))



comment_gubun = new Array()
comment_gubun.push(new Array("처리내역","1", "chk1", "chk2"))
comment_gubun.push(new Array("<font color='orange'>재고조정</font>","2"))
comment_gubun.push(new Array("<font color='blue'>재고관리</font>","3", "chk1"))




// sales/chgCost
MD_brand = new Array()
MD_brand.push(new Array("엘리펀트이어스:EL","28","chk","family","http://www.elephantears.co.kr/","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("타이니통스:TT","29"))
MD_brand.push(new Array("코들라이프:CD","31","chk","petit"))
MD_brand.push(new Array("킨더스펠:KP","32","chk","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("에바비바:EV","33","chk","family","http://www.erbababy.co.kr/","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("에티튜드:AT","43","chk","family","http://www.naturalattitude.co.kr/","petit","store","thumnail","add","mkt", "goal"))
MD_brand.push(new Array("밀로앤개비:MG","44","chk","family","http://www.miloandgabby.co.kr/","petit","store","thumnail","add","mkt", "goal"))
MD_brand.push(new Array("아이비:IV","45","chk","petit"))
MD_brand.push(new Array("프레쉬쌕:FS","46","chk","petit"))
MD_brand.push(new Array("젤리캣:JC","47","chk","family","http://www.jellycatkorea.co.kr/","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("노슬립헤어클리피:HC","50","chk","petit"))
MD_brand.push(new Array("레몬캔버스:LC","53","chk","petit","thumnail", "goal"))
MD_brand.push(new Array("키에트라:KE","54","chk","petit","goal"))
MD_brand.push(new Array("낫지니:KG","55","chk","petit"))
MD_brand.push(new Array("와우컵:WO","56","chk","family","http://www.wowcup.co.kr/","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("아쿠아스케일:AS","58","chk","petit"))
MD_brand.push(new Array("쿠나텐트:CT","60","chk","petit", "flkorea", "goal"))
MD_brand.push(new Array("비지비:AB","61","chk","petit"))
MD_brand.push(new Array("북클레벤:BK","62","chk","petit","flkorea","thumnail", "goal"))
MD_brand.push(new Array("헤이랜드:HL","63","chk","petit"))
MD_brand.push(new Array("엘프레리:EP","64","chk","family","http://www.elprairie.co.kr/","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("포니사이클:PC","546","chk","petit"))
MD_brand.push(new Array("마베뜨:MA","594","chk","petit"))
MD_brand.push(new Array("레온슈즈:LS","595","chk","petit"))
MD_brand.push(new Array("케이퍼랜드:KL","599","chk","petit","store"))
MD_brand.push(new Array("릴헤븐:LH","626","chk","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("모윰:MY","690","chk","family","http://www.moyuum.co.kr/","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("듀이스트:DE","691","chk","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("디벨플래닛:BP","736","chk","petit","goal"))
MD_brand.push(new Array("에끌레브:EC","755","chk","family","http://www.ecleve.co.kr/","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("킨더스펠웨어:KW","789","chk","petit","thumnail", "goal"))
MD_brand.push(new Array("세이지폴:SP","888","chk","family","http://www.sagepole.co.kr/","petit","store", "flkorea","thumnail","mkt", "goal"))
MD_brand.push(new Array("스마트라이크:ST","922","chk","family","http://www.smartrike.kr","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("이노베이비:IB","932","chk","petit", "goal"))
MD_brand.push(new Array("립프로그:LF","1015","chk","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("수퍼리브즈:SL","1062","chk","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("리틀페넥:LI","1080","chk","family","http://www.littlefennec.co.kr","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("오케이베이비:OB","1112","chk","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("엘라바:EA","1121","chk","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("키위:KI","1133","chk","family","http://www.kiwyworld.co.kr","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("하퍼스테이블:HT","1157","chk","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("퓨어닷:PD","1245","chk","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("플레이앤고:PG","1183","chk","petit"))
MD_brand.push(new Array("에이투:GA","1216","chk","petit","goal"))
MD_brand.push(new Array("홀레:HO","1256","chk","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("바이오메라:BM","1257","chk","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("모윰365:IM","1291","chk","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("어네이브:HN","1301","chk","petit","store","thumnail","mkt", "goal"))

MD_brand.push(new Array("쁘띠엘린:PE","41","mkt"))

//에센루 브랜드
MD_brand.push(new Array("아이너바움:EB","821","chk","essen"))
MD_brand.push(new Array("보타니컬테라피:BT","822","chk","essen"))
MD_brand.push(new Array("메켄메르크:MK","823","chk","essen","thumnail"))
MD_brand.push(new Array("마커스&마커스:MM","824","chk","essen","thumnail"))
MD_brand.push(new Array("데글링고스:DG","857","chk","essen","thumnail"))
MD_brand.push(new Array("모이스앤로이스:ML","863","chk","essen"))
MD_brand.push(new Array("클라우드비:CB","899","chk","essen","thumnail"))
MD_brand.push(new Array("도노도노:DD","913","chk","essen","thumnail"))
MD_brand.push(new Array("슈너글:SG","976","chk","essen","thumnail"))
MD_brand.push(new Array("아이돌아이즈:IE","1017","chk","essen"))
MD_brand.push(new Array("끌라미엘:CM","1018","chk","essen"))
MD_brand.push(new Array("위틀스토어:WT","923"))

MD_brand.push(new Array("베이비젠:BZ","1051","chk","essen","store","thumnail"))

MD_brand.sort()


//schedule//discuss_schedule //////////////////////////////////////////////////////////////////////////////////////////////////////
discuss_room = new Array()
discuss_room.push(new Array("3층회의실","5"))
discuss_room.push(new Array("대회의실(5층)","7"))
discuss_room.push(new Array("회의실1","1"))
discuss_room.push(new Array("회의실2(휴게실)","2"))
discuss_room.push(new Array("대회의실","4"))
discuss_room.push(new Array("외부미팅","3"))

discuss_room.push(new Array("대회의실","11","chk"))
discuss_room.push(new Array("대회의실(통합)","10","chk"))
discuss_room.push(new Array("중회의실","12","chk"))
discuss_room.push(new Array("중회의실3","13"))
discuss_room.push(new Array("중회의실4","14"))
discuss_room.push(new Array("소회의실1","15")) //목업실로 변경됨
discuss_room.push(new Array("소회의실1","16"))
discuss_room.push(new Array("소회의실2","17"))
discuss_room.push(new Array("쁘띠룸","18","chk"))
discuss_room.push(new Array("엘린룸","19","chk"))
discuss_room.push(new Array("4층회의실","6"))

// supporters/cs///////////////////////////////////////////////////////////////////////////////////////
divide_supporters = new Array()
divide_supporters.push(new Array("블로거","1"))
divide_supporters.push(new Array("SNS","2"))

faq_gb = new Array()
faq_gb.push(new Array("활동관련","A"))
faq_gb.push(new Array("결제관련","B"))
faq_gb.push(new Array("사이트이용관련","C"))
faq_gb.push(new Array("기타","D"))

notice_gb = new Array()
notice_gb.push(new Array("서포터즈 공지","0"))
notice_gb.push(new Array("이벤트 당첨자발표","1"))
notice_gb.push(new Array("홍보요청","2"))

qanda_gb = new Array()
qanda_gb.push(new Array("인기제품체험","0"))
qanda_gb.push(new Array("신제품체험","1"))
qanda_gb.push(new Array("VIP이벤트","2"))
qanda_gb.push(new Array("홍보이벤트","3"))
qanda_gb.push(new Array("기타문의","4"))

act_buying_source = new Array()
act_buying_source.push(new Array("쁘띠엘린스토어", "1"))
act_buying_source.push(new Array("온라인몰(쁘띠엘린 외)", "2"))
act_buying_source.push(new Array("백화점", "3"))
act_buying_source.push(new Array("로드샵", "4"))
act_buying_source.push(new Array("박람회", "5"))
act_buying_source.push(new Array("기타", "6"))


// cs/parts //////////////////////////////////////////////////////////////////////////////////////////////////////
parts_status = new Array()
parts_status.push(new Array("요청", "1"))
parts_status.push(new Array("접수", "2"))
parts_status.push(new Array("부자재 확정", "3"))
parts_status.push(new Array("완료", "4"))
parts_status.push(new Array("취소", "5"))

work_process = new Array();
// 업무아이디,대구분,소구분,월간여부,주간업무색상 index,월간업무색상 index
work_process.push(new Array(1, "프로모션", "통합메일", 1, 7, 7));
work_process.push(new Array(2, "프로모션", "브랜드위크", 1, 7, 8));
work_process.push(new Array(3, "프로모션", "체험할인", 1, 7, 9));
work_process.push(new Array(4, "프로모션", "모바일주말특가", 1, 7, 10));
work_process.push(new Array(5, "프로모션", "통합이벤트", 1, 7, 11));
work_process.push(new Array(6, "프로모션", "APP전용", 1, 7, 12));
work_process.push(new Array(7, "프로모션", "전사프로모션", 1, 7, 13));
work_process.push(new Array(8, "프로모션", "기타프로모션", 1, 7, 14));
work_process.push(new Array(9, "컨텐츠", "카테통합이벤트", 0, 0, 0));
work_process.push(new Array(10, "컨텐츠", "APP용컨텐츠", 0, 0, 0));
work_process.push(new Array(11, "컨텐츠", "기타컨텐츠", 0, 0, 0));
work_process.push(new Array(12, "매출관리", "주간실적", 0, 1, 1));
work_process.push(new Array(13, "매출관리", "월간실적", 0, 1, 1));
work_process.push(new Array(14, "매출관리", "개인실적", 0, 1, 1));
work_process.push(new Array(15, "상품관리", "상품등록", 0, 2, 2));
work_process.push(new Array(16, "상품관리", "브랜드/상품런칭", 0, 2, 2));
work_process.push(new Array(17, "상품관리", "상품품절/단종", 0, 2, 2));
work_process.push(new Array(18, "상품관리", "재입고알림", 0, 2, 2));
work_process.push(new Array(19, "상품관리", "이슈상품관리", 0, 2, 2));
work_process.push(new Array(20, "사이트관리", "카테고리운영", 0, 3, 3));
work_process.push(new Array(21, "사이트관리", "메인노출관리", 0, 3, 3));
work_process.push(new Array(22, "사이트관리", "배너관리", 0, 3, 3));
work_process.push(new Array(23, "사이트관리", "베스트세팅", 0, 3, 3));
work_process.push(new Array(24, "사이트관리", "후기관리/이벤트", 0, 3, 3));
work_process.push(new Array(25, "사이트관리", "정기이벤트", 0, 3, 3));
work_process.push(new Array(26, "사이트관리", "팝업/공지", 0, 3, 3));
work_process.push(new Array(27, "사이트관리", "입점사관리", 0, 3, 3));
work_process.push(new Array(28, "사이트관리", "쿠폰등록/관리", 0, 3, 3));
work_process.push(new Array(29, "사이트관리", "타겟메일/문자", 0, 3, 3));
work_process.push(new Array(30, "사이트관리", "정책/법령이슈", 0, 3, 3));
work_process.push(new Array(31, "사이트관리", "리뉴얼/업데이트", 0, 3, 3));
work_process.push(new Array(32, "APP관리", "APP노출", 0, 4, 4));
work_process.push(new Array(33, "APP관리", "APP특가", 0, 4, 4));
work_process.push(new Array(34, "APP관리", "APP푸쉬", 0, 4, 4));
work_process.push(new Array(35, "APP관리", "APP오류/업데이트", 0, 4, 4));
work_process.push(new Array(36, "회원관리", "멤버쉽", 0, 5, 5));
work_process.push(new Array(37, "회원관리", "우수회원관리", 0, 5, 5));
work_process.push(new Array(38, "회원관리", "신규회원관리", 0, 5, 5));
work_process.push(new Array(39, "회원관리", "휴면회원관리", 0, 5, 5));
work_process.push(new Array(40, "기타", "회의", 0, 6, 6));
work_process.push(new Array(41, "기타", "기타", 0, 6, 6));
work_process.push(new Array(42, "기타", "판촉체크/적용", 0, 6, 6));
work_process.push(new Array(43, "기타", "주간/월간계획", 0, 6, 6));

process_detail = new Array();
process_detail.push(new Array("일정체크", "1"));
process_detail.push(new Array("기획/프로모션체크", "2"));
process_detail.push(new Array("세부업무", "3"));
process_detail.push(new Array("테스트/세팅/공유", "4"));
process_detail.push(new Array("오픈/종료", "5"));
process_detail.push(new Array("효과분석", "6"));

var work_process_name = new Array();
for (var i=0; i<work_process.length; i++) {
	work_process_name.push(new Array(work_process[i][1] +" > "+ work_process[i][2], work_process[i][0].toString()));
}

sms_status = new Array()
sms_status.push(new Array("사용함","1"))
sms_status.push(new Array("사용안함","0"))

sms_gubun = new Array()
sms_gubun.push(new Array("쁘띠엘린","쁘띠엘린"))
sms_gubun.push(new Array("세이지폴회수","세이지폴회수"))

//택배사
delivery_company = new Array()
delivery_company.push(new Array("원송장","1"))
delivery_company.push(new Array("현대","2"))
delivery_company.push(new Array("대한통운","3"))
delivery_company.push(new Array("우체국","4"))
delivery_company.push(new Array("CJ","5"))
delivery_company.push(new Array("한진","6"))
delivery_company.push(new Array("천일","7"))
delivery_company.push(new Array("아주","8"))
delivery_company.push(new Array("로젠","9"))
delivery_company.push(new Array("롯데","10"))
delivery_company.push(new Array("호남","11"))
delivery_company.push(new Array("기타","99"))

cs_req_ok = new Array()
cs_req_ok.push(new Array("<font color='blue'>대기</font>","1"))
cs_req_ok.push(new Array("<font color='green'>접수</font>","2","chk"))
cs_req_ok.push(new Array("<font color='red'>처리완료</font>","3","chk"))
cs_req_ok.push(new Array("<font color='#ffcc00'>취소</font>","4"))

CS_category_id = new Array()
CS_category_id.push(new Array("경영-환불요청","134"))
CS_category_id.push(new Array("경영-계산서/영수증 발행","135"))
CS_category_id.push(new Array("경영-기타","138"))

cs_req_status = new Array()
cs_req_status.push(new Array("요청","1","as"))
cs_req_status.push(new Array("<span style='font-weight:bold'>처리중</span>","2","as"))
cs_req_status.push(new Array("<span style='color:orange;font-weight:bold'>답변완료</span>","3"))
cs_req_status.push(new Array("<span style='color:gray'>취소</font>","9","as"))

cs_req_gubun = new Array()
cs_req_gubun.push(new Array("상품문의","1"))
cs_req_gubun.push(new Array("이슈/클레임","2"))
cs_req_gubun.push(new Array("런칭일정/상품정보","3"))
cs_req_gubun.push(new Array("결품","4"))
cs_req_gubun.push(new Array("이벤트일정","5"))
cs_req_gubun.push(new Array("AS문의","6"))
cs_req_gubun.push(new Array("부자재요청","7"))
cs_req_gubun.push(new Array("오프라인매장불만","8"))
cs_req_gubun.push(new Array("오프라인주문","9"))
cs_req_gubun.push(new Array("베이비페어판매","10"))
cs_req_gubun.push(new Array("대량구매문의","11"))
cs_req_gubun.push(new Array("서포터즈","12"))
cs_req_gubun.push(new Array("대외언론기관","13"))

as_req_gubun = new Array()
as_req_gubun.push(new Array("수리","1"))
as_req_gubun.push(new Array("부품발송","2"))
as_req_gubun.push(new Array("초기불량(교환/반품)","3"))

as_req_gubun1 = new Array()
as_req_gubun1.push(new Array("유상","1"))
as_req_gubun1.push(new Array("무상","2"))

as_req_gubun2 = new Array()
as_req_gubun2.push(new Array("택배","1"))
as_req_gubun2.push(new Array("방문","2"))


// distribution/bulk //////////////////////////////////////////////////////////////////////////////////////////////
//진행상태
req_status = new Array()
req_status.push(new Array("<span style='color: black'>요청</span>","1"))
req_status.push(new Array("<span style='color: orange'>접수</span>","2"))
req_status.push(new Array("<span style='color: red'>처리중</span>","3"))
req_status.push(new Array("<span style='color: blue'>작업완료</span>","5"))
req_status.push(new Array("<span style='color: green'>거래명세표회수</span>","6"))
req_status.push(new Array("<span style='color: grey'>취소</span>","9"))

//인력 - 진행상태
add_status = new Array();
add_status.push(new Array("<font color='red'>요청</font>", "1"));
add_status.push(new Array("<font color='blue'>확인</font>", "2"));
add_status.push(new Array("<font color='gray'>취소</font>", "9"));


company_market = new Array()
company_market.push(new Array("국내영업","19","saip"))
company_market.push(new Array("해외전략","27"))
company_market.push(new Array("B2B마케팅(광고협찬)","40"))
company_market.push(new Array("B2B마케팅(사입)","41","saip"))
company_market.push(new Array("B2BMD(사입)","46","saip"))


company_market_Reverse = new Array()
company_market_Reverse.push(new Array("19","국내영업팀"))
company_market_Reverse.push(new Array("27","해외전략팀"))
company_market_Reverse.push(new Array("40","마케팅팀(광고협찬)"))
company_market_Reverse.push(new Array("41","마케팅팀(사입)"))
company_market_Reverse.push(new Array("46","MD팀(사입)"))