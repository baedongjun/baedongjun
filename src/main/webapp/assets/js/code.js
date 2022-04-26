//중요
//쁘띠엘린앱에 사용되는 공통값들 때문에 code.js 파일 추가 또는 수정/삭제시 >> 관리자>>APP관리>>App연동 XML 생성>code.js 생성을 해주어야 한다.
//code.js에서 일부 값만 사용되지만 혹시모를 누락을 위해서 수정시마다 생성해준다.
//code.js 변경 시 모바일 js 함께 변경 할 것
function make_select_code(choice,choice_array,name,class_name,add,wid,minusTag,minus,d_val,write_doc,skin) //객체,배열,이름,클래스명,추가내용,가로크기,추가함수(select는 빠짐, radio,checkbox는 함수추가),특정데이타 선택,선택값,document.write 하지 않는다., multi select 디자인
{
	rValue = new Array()
	field_name = (name==undefined || name=="") ? choice_array : name
	add_style = (!(wid==undefined || wid=="")) ? "width:" + wid : ""
	multiple = (!(skin==undefined || skin=="")) ? 'multiple' : ''
	if (choice=="select") {

		if (minusTag==undefined || minusTag=="") {
			if (class_name==undefined || class_name=="") {
				rValue.push('<select id="'+ field_name +'" name="'+ field_name +'" ' + multiple + '>')
			} else {
				rValue.push('<select id="'+ field_name +'" name="'+ field_name + '" ' + multiple + ' class="input">')
			}
		}

		if (!(add==undefined || add=="")) rValue.push('<option value="'+add.split("^")[0]+'" selected>'+add.split("^")[1]+'</option>')

		this_array = eval(choice_array)

		for (var i=0 ; i<this_array.length ; i++) {
			if (this_array[i][1].indexOf("0000")>0) {
				if (minus==undefined || minus=="") {
					rValue.push('<optgroup label="■ '+ this_array[i][0] +'"></optgroup>')
				}
			} else {
				if (minus==undefined || minus=="") {
					rValue.push('<option value="'+ this_array[i][1] +'"')
				} else {
					for (var ii=2 ; ii<this_array[i].length ; ii++) if (this_array[i][ii]==minus) rValue.push('<option value="'+ this_array[i][1] +'"')
				}
				if (!(d_val==undefined || d_val=="") && d_val==this_array[i][1]) rValue.push(' selected')
				if (minus==undefined || minus=="") {
					rValue.push('>'+this_array[i][0]+'</option>')
				} else {
					for (var ii=2 ; ii<this_array[i].length ; ii++) if (this_array[i][ii]==minus) rValue.push('>'+this_array[i][0]+'</option>')
				}
			}
		}

		rValue.push('</select>')
		if (write_doc==undefined || write_doc=="") {
			document.writeln (rValue.join(""))
		} else {
			return rValue.join("")
		}

		if(!(skin == undefined || skin == "")){
			eval(field_name+"Multi " + "= new selectBox('#"+field_name+"');")
		}

	} else if (choice=="checkbox") {
		add_style = add_style.replace("width","margin-right")
		field_name = (name==undefined || name=="") ? choice_array : name

		if (add!=undefined && add!="") {
			rValue.push('<span style="white-space:nowrap"><input type="checkbox" onclick="')
			if (!(minusTag==undefined || minusTag=="")) rValue.push(';nextCodeFnc(this.value)"')
			rValue.push('" name="'+field_name+'" name="'+field_name+'" class="'+class_name+'" value="'+add.split("^")[0]+'" id="'+field_name+'"> <label for="'+field_name+'" style="cursor:hand;'+ add_style +'">'+add.split("^")[1]+'</label></span> ')
		}

		this_array = eval(choice_array)
		for (var i=0 ; i<this_array.length ; i++) {
			if (minus==undefined || minus=="") {
				rValue.push('<span style="white-space:nowrap"><input type="checkbox" onclick="')
				if (!(minusTag==undefined || minusTag=="")) rValue.push(';nextCodeFnc(this.value)"')
				rValue.push('" name="'+field_name+'" class="'+class_name+'" value="'+this_array[i][1]+'" id="'+(field_name+"^"+i)+'"')
			} else {
				for (var ii=2 ; ii<this_array[i].length ; ii++) {
					if (this_array[i][ii]==minus) {
						rValue.push('<span style="white-space:nowrap"><input type="checkbox" onclick="')
						if (!(minusTag==undefined || minusTag=="")) rValue.push(';nextCodeFnc(this.value)"')
						rValue.push('" name="'+field_name+'" class="'+class_name+'" value="'+this_array[i][1]+'" id="'+(field_name+"^"+i)+'"')
					}
				}
			}
			if (!(d_val==undefined || d_val=="") && d_val==this_array[i][1]) rValue.push(' checked')
			if (minus==undefined || minus=="") {
				rValue.push('> <label for="'+(field_name+"^"+i)+'" style="cursor:hand;'+ add_style +'">'+this_array[i][0]+'</label></span> ')
			} else {
				for (var ii=2 ; ii<this_array[i].length ; ii++) if (this_array[i][ii]==minus) rValue.push('> <label for="'+(field_name+"^"+i)+'" style="cursor:hand;'+ add_style +'">'+this_array[i][0]+'</label></span> ')
			}
		}

		if (write_doc) {
			return rValue.join("")
		} else {
			document.writeln (rValue.join(""))
		}

	} else if (choice=="radio") {
		add_style = add_style.replace("width","margin-right")
		field_name = (name==undefined || name=="") ? choice_array : name

		if (add!=undefined && add!="") {
			rValue.push('<span style="white-space:nowrap"><input type="radio" onclick="')
			if (!(minusTag==undefined || minusTag=="")) rValue.push(';nextCodeFnc(this.value)"')
			rValue.push('" name="'+field_name+'" class="'+class_name+'" value="'+add.split("^")[0]+'" checked id="'+field_name+'"> <label for="'+field_name+'" style="cursor:hand;'+ add_style +'">'+add.split("^")[1]+'</label>&nbsp;</span>')
		}

		this_array = eval(choice_array)
		for (var i=0 ; i<this_array.length ; i++) {
			if (minus==undefined || minus=="") {
				rValue.push('<span style="white-space:nowrap"><input type="radio" onclick="')
				if (!(minusTag==undefined || minusTag=="")) rValue.push(';nextCodeFnc(this.value)"')
				rValue.push('" name="'+field_name+'" class="'+class_name+'" value="'+this_array[i][1]+'" id="'+(field_name+"^"+i)+'"')
			} else {
				for (var ii=2 ; ii<this_array[i].length ; ii++) {
					if (this_array[i][ii]==minus) {
						rValue.push('<span style="white-space:nowrap"><input type="radio" onclick="')
						if (!(minusTag==undefined || minusTag=="")) rValue.push(';nextCodeFnc(this.value)"')
						rValue.push('" name="'+field_name+'" class="'+class_name+'" value="'+this_array[i][1]+'" id="'+(field_name+"^"+i)+'"')
					}
				}
			}
			if (!(d_val==undefined || d_val=="") && d_val==this_array[i][1]) rValue.push(' checked')
			if (minus==undefined || minus=="") {
				rValue.push('> <label for="'+(field_name+"^"+i)+'" style="cursor:hand;'+ add_style +'">'+this_array[i][0]+'</label></span> ')
			} else {
				for (var ii=2 ; ii<this_array[i].length ; ii++) {
					if (this_array[i][ii]==minus) rValue.push('> <label for="'+(field_name+"^"+i)+'" style="cursor:hand;'+ add_style +'">'+this_array[i][0]+'</label></span> ')
				}
			}
		}

		if (write_doc) {
			return rValue.join("")
		} else {
			document.writeln (rValue.join(""))
		}
	}
}

function view_name(choice_array,val,write_doc,divide,view_divide) {
	val = String(val)
	this_array = eval(choice_array)
	if (divide) {
		this_value = val.split(divide)
	} else {
		this_value = val.split(", ")
	}
	returnValue = ""

	if (view_divide!=undefined) if (val==view_divide.split("^")[0]) returnValue=view_divide.split("^")[1]

	for (var h=0 ; h<this_value.length ; h++) {
		for (var i=0 ; i<this_array.length ; i++) {
			if (this_array[i][1]==this_value[h]) {
				if (!(h==0 || returnValue=="")) {
					if (divide) {
						returnValue = returnValue + divide
					} else {
						returnValue = returnValue + ", "
					}
				}
				returnValue = returnValue + this_array[i][0]
			}
		}
	}
	if (returnValue=="") returnValue="-"
	if (write_doc==undefined || write_doc=="") {
		return returnValue
	} else {
		document.write (returnValue)
	}
}

function make_choice_code(choice_array,name,func) {
	field_name = (name==undefined || name=="") ? choice_array : name

	rValue = new Array()
	rValue.push("<div>")

	this_array = eval(choice_array)
	for (var i=0 ; i<this_array.length ; i++) {
		if (this_array[i][1].indexOf("00")>0) {
			if (func) {
				rValue.push('</div><span style="white-space:nowrap;cursor:hand;font-weight:bold" onclick=make_choice_fnc("'+ name + this_array[i][1] +'");'+ func +'>■ '+this_array[i][0]+'</span><br><div id="search_' + name + this_array[i][1] +'" name="search_' + name + this_array[i][1] +'" style="display:none">')
			} else {
				rValue.push('</div><span style="white-space:nowrap;cursor:hand;font-weight:bold" onclick=make_choice_fnc("'+ name + this_array[i][1] +'")>■ '+this_array[i][0]+'</span><br><div id="search_'+ name + this_array[i][1] +'" name="search_'+ name + this_array[i][1] +'" style="display:none">')
			}
		} else {
			if (func) {
				rValue.push('<span style="white-space:nowrap"><input type="checkbox" onclick=";'+ func +'" name="'+field_name+'" class="null" value="'+this_array[i][1]+'" id="'+(field_name+i)+'"> <label for="'+(field_name+i)+'" style="cursor:hand">'+this_array[i][0]+'</label>&nbsp;</span>')
			} else {
				rValue.push('<span style="white-space:nowrap"><input type="checkbox" onclick="" name="'+field_name+'" class="null" value="'+this_array[i][1]+'" id="'+(field_name+i)+'"> <label for="'+(field_name+i)+'" style="cursor:hand">'+this_array[i][0]+'</label>&nbsp;</span>')
			}
		}
	}
	document.writeln (rValue.join(""))
}

function make_choice_fnc(val) {
	if (document.getElementById("search_" + val).style.display=="") {
		document.getElementById("search_" + val).style.display="none"
	} else {
		document.getElementById("search_" + val).style.display=""
	}
}

function changeServerAt(val) {
	serverValue = new Array()
	for (var thisC=0 ; thisC<val.length ; thisC++) {
		serverValue[thisC] = val[thisC][0] +"//"+ val[thisC][1]
	}
	return serverValue
}



manage_marriage = new Array()
manage_marriage.push(new Array("미혼","n"))
manage_marriage.push(new Array("기혼","y"))



function getCodeArray(choice_array){
	return eval(choice_array);
}

manage_emploee = new Array()
manage_emploee.push(new Array("직원","y"))
manage_emploee.push(new Array("퇴사","n"))
manage_emploee.push(new Array("대기","x"))
manage_emploee.push(new Array("출산/육아 휴직","b"))

manage_position = new Array()
manage_position.push(new Array("회장","0"))
manage_position.push(new Array("사장","1"))
manage_position.push(new Array("부사장","2"))
manage_position.push(new Array("전무","14")) //마지막 할당
manage_position.push(new Array("상무","11"))
manage_position.push(new Array("이사","3"))
manage_position.push(new Array("부장","4"))
manage_position.push(new Array("차장","5"))
manage_position.push(new Array("과장","6"))
manage_position.push(new Array("대리","7"))
manage_position.push(new Array("주임","12"))
manage_position.push(new Array("사원","8"))
manage_position.push(new Array("심사관리자","9"))
manage_position.push(new Array("협력자","10"))
manage_position.push(new Array("3pl담당","13"))

manage_duty = new Array()
manage_duty.push(new Array("임원","1"))
manage_duty.push(new Array("본부장","2"))
manage_duty.push(new Array("팀장","3"))
manage_duty.push(new Array("팀원","4"))
manage_duty.push(new Array("외부","5"))






moon = new Array()
moon.push(new Array("양력","1"))
moon.push(new Array("음력","2"))

sex = new Array()
sex.push(new Array("남","1"))
sex.push(new Array("여","2"))

yesOrNo = new Array()
yesOrNo.push(new Array("예","y"))
yesOrNo.push(new Array("아니오","n"))


membership = new Array()
membership.push(new Array("멤버쉽","Y"))
membership.push(new Array("일반","N"))

mem_gubun = new Array()
mem_gubun.push(new Array("일반","1"))
mem_gubun.push(new Array("체험단","3"))


title_location = new Array()
title_location.push(new Array("◁ Front","1","chk"))
title_location.push(new Array("Back ▷","2","chk"))
title_location.push(new Array("노출없음","3","chk"))
title_location.push(new Array("타사몰","4"))
title_location.push(new Array("그룹","5"))

//push : ◇ 자사몰 전용 > 사이트관리 > 이벤트 관리 > APP 푸시 관리
userrank = new Array()
userrank.push(new Array("MEMBER","1","chk"))
userrank.push(new Array("FRIEND","6","chk","push"))
userrank.push(new Array("FAMILY","7","chk","push"))
userrank.push(new Array("VIP","8","chk","push"))
userrank.push(new Array("VIP PLUS","9","chk","push"))
userrank.push(new Array("GUEST","99"))

tel = new Array()
tel.push(new Array("02 (서울)","02"))
tel.push(new Array("032 (인천)","032"))
tel.push(new Array("042 (대전)","042"))
tel.push(new Array("062 (광주)","062"))
tel.push(new Array("053 (대구)","053"))
tel.push(new Array("052 (울산)","052"))
tel.push(new Array("051 (부산)","051"))
tel.push(new Array("031 (경기)","031"))
tel.push(new Array("033 (강원도)","033"))
tel.push(new Array("043 (충북)","043"))
tel.push(new Array("041 (충남)","041"))
tel.push(new Array("063 (전북)","063"))
tel.push(new Array("061 (전남)","061"))
tel.push(new Array("054 (경북)","054"))
tel.push(new Array("055 (경남)","055"))
tel.push(new Array("064 (제주도)","064"))
tel.push(new Array("070 (인터넷)","070"))
tel.sort()

handphone = new Array()
handphone.push(new Array("010","010"))
handphone.push(new Array("011","011"))
handphone.push(new Array("016","016"))
handphone.push(new Array("017","017"))
handphone.push(new Array("018","018"))
handphone.push(new Array("019","019"))
handphone.push(new Array("050","050"))
handphone.push(new Array("0502","0502"))
handphone.push(new Array("0503","0503"))
handphone.push(new Array("0504","0504"))
handphone.push(new Array("0505","0505"))
handphone.push(new Array("0507","0507"))
handphone.push(new Array("0508","0508"))
handphone.push(new Array("070","070"))


//위에 tel, handphone은 아래것으로 대처하고 삭제할것.2018-10-11
allphone = new Array()
allphone.push(new Array("010","010","handphone"))
allphone.push(new Array("011","011","handphone"))
allphone.push(new Array("016","016","handphone"))
allphone.push(new Array("017","017","handphone"))
allphone.push(new Array("018","018","handphone"))
allphone.push(new Array("019","019","handphone"))
allphone.push(new Array("050","050","handphone"))
allphone.push(new Array("0502","0502","handphone"))
allphone.push(new Array("0503","0503","handphone"))
allphone.push(new Array("0504","0504","handphone"))
allphone.push(new Array("0505","0505","handphone"))
allphone.push(new Array("0507","0507","handphone"))
allphone.push(new Array("0508","0508","handphone"))
allphone.push(new Array("02 (서울)","02","tel"))
allphone.push(new Array("032 (인천)","032","tel"))
allphone.push(new Array("042 (대전)","042","tel"))
allphone.push(new Array("062 (광주)","062","tel"))
allphone.push(new Array("053 (대구)","053","tel"))
allphone.push(new Array("052 (울산)","052","tel"))
allphone.push(new Array("051 (부산)","051","tel"))
allphone.push(new Array("031 (경기)","031","tel"))
allphone.push(new Array("033 (강원도)","033","tel"))
allphone.push(new Array("043 (충북)","043","tel"))
allphone.push(new Array("041 (충남)","041","tel"))
allphone.push(new Array("063 (전북)","063","tel"))
allphone.push(new Array("061 (전남)","061","tel"))
allphone.push(new Array("054 (경북)","054","tel"))
allphone.push(new Array("055 (경남)","055","tel"))
allphone.push(new Array("064 (제주도)","064","tel"))
allphone.push(new Array("070 (인터넷)","070","tel"))



money_use = new Array()
money_use.push(new Array("소득공제용","1"))
money_use.push(new Array("지출증빙용","2"))
money_use.push(new Array("받지않음","3"))

coupon_target = new Array()
coupon_target.push(new Array("<span style='color: red'>정상가</span>","1"))
coupon_target.push(new Array("<span style='color: blue'>판매가</span>","2"))

coupon_month = new Array()
coupon_month.push(new Array("유효기간을 따름","0"))
coupon_month.push(new Array("당일","-1"))
coupon_month.push(new Array("7일 이내","-7"))
coupon_month.push(new Array("1개월 이내","1"))
coupon_month.push(new Array("3개월 이내","3"))
coupon_month.push(new Array("6개월 이내","6"))
coupon_month.push(new Array("1년 이내","12"))
coupon_month.push(new Array("무한","120"))

coupon_site = new Array()
coupon_site.push(new Array("전체","1"))
coupon_site.push(new Array("어플","2"))

coupon_gubun = new Array()
coupon_gubun.push(new Array("상품","product"))
coupon_gubun.push(new Array("브랜드","brand"))

payresult_arr = new Array()
payresult_arr.push(new Array("결제대기","0","chk"))
payresult_arr.push(new Array("사용자완료","8","chk"))
payresult_arr.push(new Array("결제완료","9","chk"))
payresult_arr.push(new Array("환급완료","10","chk"))

paymode_arr = new Array()
paymode_arr.push(new Array("카드결제","1","chk"))
paymode_arr.push(new Array("무통장입금","3","chk"))
paymode_arr.push(new Array("정산처리","4"))
paymode_arr.push(new Array("에스크로","5","chk"))
paymode_arr.push(new Array("쿠폰/예치금 결제","6"))  //2016-02-18 0원결제 > 쿠폰/예치금 결제 로 명칭 변경
//paymode_arr.push(new Array("포인트결제","7"))
paymode_arr.push(new Array("휴대폰소액결제","8","chk"))
paymode_arr.push(new Array("PAYNOW결제","9","chk"))
paymode_arr.push(new Array("무통장입금(가상계좌)","10","chk"))

paymode_arr.push(new Array("계좌이체","11","chk"))
paymode_arr.push(new Array("도서문화상품권","12","chk"))


delivery = new Array()
delivery.push(new Array("결제대기","1","chk"))
delivery.push(new Array("결제완료","2","chk"))
delivery.push(new Array("상품준비","8","chk"))
delivery.push(new Array("발송준비","3","chk"))
delivery.push(new Array("발송준비(일부)","4"))
delivery.push(new Array("발송완료(일부)","5"))
delivery.push(new Array("발송완료","6","chk"))
delivery.push(new Array("발송완료(퀵)","7","chk"))
delivery.push(new Array("주문취소","99"))

pointPart = new Array()
pointPart.push(new Array("적립(+)","적립"))
pointPart.push(new Array("사용(-)","사용"))
pointPart.push(new Array("재적립(+)","재적립"))
pointPart.push(new Array("주문취소(-)","주문취소"))
pointPart.push(new Array("차감(-)","차감"))

pointGubun = new Array()
pointGubun.push(new Array("브랜드","0"))

bank_name = new Array()
bank_name.push(new Array("입금내역없음","입금내역없음"))
bank_name.push(new Array("국민은행","국민은행"))
bank_name.push(new Array("기업은행","기업은행"))
bank_name.push(new Array("농협","농협"))
bank_name.push(new Array("우리은행","우리은행"))
bank_name.push(new Array("우체국","우체국"))
bank_name.push(new Array("외환은행","외환은행"))
bank_name.push(new Array("산업은행","산업은행"))
bank_name.push(new Array("신한은행","신한(구 조흥)은행"))
bank_name.push(new Array("씨티은행","씨티은행"))
bank_name.push(new Array("제일은행","제일은행"))
bank_name.push(new Array("하나은행","하나은행"))
bank_name.push(new Array("한미은행","한미은행"))

job = new Array()
job.push(new Array("가정 주부","1"))
job.push(new Array("고객 서비스/지원","2"))
job.push(new Array("공무원/군인","3"))
job.push(new Array("교육/훈련","4"))
job.push(new Array("무직","5"))
job.push(new Array("상업/기술직","6"))
job.push(new Array("엔지니어링","7"))
job.push(new Array("연구 및 개발","8"))
job.push(new Array("영업/마케팅/광고","9"))
job.push(new Array("자유업/자영업","10"))
job.push(new Array("전문직(의사, 변호사 등)","11"))
job.push(new Array("정년 퇴직","12"))
job.push(new Array("제조/생산/기능직","13"))
job.push(new Array("총무/관리","14"))
job.push(new Array("컨설팅","15"))
job.push(new Array("컴퓨터 관련(기타)","16"))
job.push(new Array("컴퓨터 관련(인터넷)","17"))
job.push(new Array("학생","18"))
job.push(new Array("행정/경영","19"))
job.push(new Array("회계/재무","20"))
job.push(new Array("기타","21"))

bankname = new Array()
bankname.push(new Array("우리은행 : 1005-001-699918 (주)쁘띠엘린","1","chk"))
bankname.push(new Array("신한은행 : 100-026-459898 (주)쁘띠엘린","2","chk"))
bankname.push(new Array("국민은행 : 924501-01-306585 (주)쁘띠엘린","3","chk"))
bankname.push(new Array("농협 : 301-0066-9401-71 (주)쁘띠엘린","4","chk"))


bankname2 = new Array()
bankname2.push(new Array("한국산업은행", "02"))
bankname2.push(new Array("기업은행", "03"))
bankname2.push(new Array("국민은행", "04"))
bankname2.push(new Array("하나은행", "05"))
bankname2.push(new Array("국민은행", "06"))
bankname2.push(new Array("수협중앙회", "07"))
bankname2.push(new Array("농협중앙회", "11"))
bankname2.push(new Array("단위농협", "12"))
bankname2.push(new Array("축협중앙회", "16"))
bankname2.push(new Array("우리은행", "20"))
bankname2.push(new Array("구 조흥은행", "21"))
bankname2.push(new Array("상업은행", "22"))
bankname2.push(new Array("SC 제일은행", "23"))
bankname2.push(new Array("한일은행", "24"))
bankname2.push(new Array("서울은행", "25"))
bankname2.push(new Array("구 신한은행", "26"))
bankname2.push(new Array("한국씨티은행", "27"))
bankname2.push(new Array("대구은행", "31"))
bankname2.push(new Array("부산은행", "32"))
bankname2.push(new Array("광주은행", "34"))
bankname2.push(new Array("제주은행", "35"))
bankname2.push(new Array("전북은행", "37"))
bankname2.push(new Array("강원은행", "38"))
bankname2.push(new Array("경남은행", "39"))
bankname2.push(new Array("비씨카드", "41"))
bankname2.push(new Array("새마을금고", "45"))
bankname2.push(new Array("신용협동조합중앙회", "48"))
bankname2.push(new Array("상호저축은행", "50"))
bankname2.push(new Array("한국씨티은행", "53"))
bankname2.push(new Array("홍콩상하이은행", "54"))
bankname2.push(new Array("도이치은행", "55"))
bankname2.push(new Array("ABN암로", "56"))
bankname2.push(new Array("JP모건", "57"))
bankname2.push(new Array("미쓰비시도쿄은행", "59"))
bankname2.push(new Array("BOA(Bank of America)", "60"))
bankname2.push(new Array("산림조합", "64"))
bankname2.push(new Array("신안상호저축은행", "70"))
bankname2.push(new Array("우체국", "71"))
bankname2.push(new Array("하나은행", "81"))
bankname2.push(new Array("평화은행", "83"))
bankname2.push(new Array("신세계", "87"))
bankname2.push(new Array("신한 통합 은행", "88"))
bankname2.push(new Array("케이뱅크", "89"))
bankname2.push(new Array("카카오뱅크", "90"))


csGubun = new Array()
csGubun.push(new Array("주문","10000"))
csGubun.push(new Array("주문취소","1","chk"))
csGubun.push(new Array("교환요청","6","chk"))
csGubun.push(new Array("반품요청","7","chk"))
csGubun.push(new Array("부분취소/환불","12","chk"))
csGubun.push(new Array("상품","20000"))
csGubun.push(new Array("상품문의","4","chk"))
csGubun.push(new Array("상품불량","9","chk"))
csGubun.push(new Array("상품변경","11","chk"))
csGubun.push(new Array("상품옵션변경","2","chk"))
csGubun.push(new Array("배송","30000"))
csGubun.push(new Array("배송문의/지연","5","chk"))
csGubun.push(new Array("배송정보변경","3","chk"))
csGubun.push(new Array("오배송","10","chk"))
csGubun.push(new Array("기타","8","chk"))
csGubun.push(new Array("물류","40000"))
csGubun.push(new Array("회수완료","13","chk"))
csGubun.push(new Array("회수상품","14","chk"))





//타사 거래 중지 또는 신규 마켓(타사 제외인 경우)처리 시 뷰 수정 : 41번 petitelin_MAIN..view_md_manage_status
//tasa : 타사몰관리(발주서/송장 등록, 관리, 히스토리)에서 타사몰만 보이도록 세팅
//divide : 사내지정발주(건별발주, 대량발주)에서 사용함.
//md : md 상품관리에서 보여질 몰(현재 관리중인 몰)
//q33 : CS상담내역에서 사용 됨.
//design : 디자인요청
//benifit : 공헌이익
//autoplay : 오토플레이 발주서 사용

market = new Array()
market.push(new Array("자사몰","1","chk","q33","md","design","benifit", "etcstatistics"))
market.push(new Array("OUTLET","52","chk2","q33","benifit"))
market.push(new Array("Awesome sales","49","chk2","q33","benifit"))
market.push(new Array("리퍼브","54","chk2","q33","benifit"))

market.push(new Array("G마켓","2","chk","q33","md","tasa","design","benifit","autoplay"))
market.push(new Array("인터파크","3","q33","chk","benifit", "etcstatistics")) //2016-06-13 몰 거래 중지
market.push(new Array("GS이숍","4","chk","q33","md","tasa","design","benifit","autoplay", "etcstatistics"))
market.push(new Array("롯데닷컴","5","chk","q33","md","tasa","design","benifit","autoplay"))
market.push(new Array("롯데아이[구]","6","q33","benifit")) //2015-10-20 몰 거래중지 처리//
market.push(new Array("롯데온","57","chk","q33","md","tasa","design","benifit","autoplay"))
market.push(new Array("신세계","7","chk","q33","md","tasa","design","benifit","autoplay"))
market.push(new Array("CJ몰","8","chk","q33","md","tasa","design","benifit","autoplay", "etcstatistics"))
market.push(new Array("AK몰(구)","9","q33","chk","benifit", "etcstatistics")) //2015-10-20 몰 거래중지 처리
market.push(new Array("11번가","10","chk","q33","md","tasa","design","benifit","autoplay", "etcstatistics"))
market.push(new Array("10x10","11","q33", "etcstatistics"))  //2017-07-05 몰 거래중지 처리
market.push(new Array("갤러리아","12","q33"))  //2017-07-05 몰 거래중지 처리
market.push(new Array("신세계(백)","13","q33"))  //2015-10-20 몰 거래중지 처리
market.push(new Array("릴션","14","q33", "etcstatistics")) //2015-03-16 몰 거래중지 처리
market.push(new Array("현대(백)","15","q33")) //2015-03-16 몰 거래중지 처리
market.push(new Array("현대","17","chk","q33","md","tasa","design","benifit","autoplay"))
market.push(new Array("옥션","18","chk","q33","md","tasa","design","benifit","autoplay", "etcstatistics"))
market.push(new Array("맘스아이","22","q33")) //2015-03-16 몰 거래중지 처리
market.push(new Array("까사리빙","23","q33")) //2015-03-16 몰 거래중지 처리
market.push(new Array("롯데아이","26","chk","q33","md","tasa","design","benifit","autoplay"))
market.push(new Array("티몬","28","chk","q33","md","tasa","design","benifit","autoplay", "etcstatistics"))
market.push(new Array("롯데홈쇼핑","29","q33")) //2015-10-20 몰 거래중지 처리
market.push(new Array("현대홈쇼핑","30","q33")) //2015-10-20 몰 거래중지 처리
market.push(new Array("이지웰","35","q33", "etcstatistics")) //2016-8-05 몰 거래중지 처리
market.push(new Array("보리보리","36","chk","q33","md","tasa","design","benifit", "etcstatistics")) //2016-01-04 몰 거래중지 처리 --> 2017-09-11 몰 거래 다시 시작
market.push(new Array("쿠팡","37","chk","q33","md","tasa","design","benifit","autoplay", "etcstatistics"))
market.push(new Array("위메프","38","chk","q33","md","tasa","design","benifit","autoplay", "etcstatistics"))
market.push(new Array("AK몰","39","chk","q33","md","tasa","design","benifit","autoplay"))
market.push(new Array("다음카카오","43","chk","q33","md","tasa","design","benifit","autoplay"))
market.push(new Array("이마트몰","45","chk","q33","md","tasa","design","benifit","autoplay"))
market.push(new Array("스토어팜","48","chk","q33","md","tasa","design","benifit","autoplay"))
market.push(new Array("홈앤쇼핑","50","chk","q33","md","tasa","design","benifit","autoplay"))
market.push(new Array("위메프(신)","53","chk","q33","md","tasa","design","benifit","autoplay"))
market.push(new Array("오늘의집","58","chk","q33","md","tasa","design","benifit","autoplay"))

market.push(new Array("무료체험(자사)","56","chk2","q33","divide"))
market.push(new Array("무료체험(MKT)","20","chk2","q33","divide"))
market.push(new Array("무료체험(MD)","44","chk2","q33","divide"))
market.push(new Array("무료체험(BM)","51","chk2","q33","divide"))
market.push(new Array("검사/샘플(개발/BM)","24","chk2","q33","divide"))
market.push(new Array("거래처선물","32","chk2","q33","divide"))
market.push(new Array("구매내역없음(CS)","34","chk2","q33","divide"))
market.push(new Array("베이비페어(CS)","47","chk2","q33","divide"))
market.push(new Array("사입","33","chk2","q33","divide"))
market.push(new Array("사판","21","chk2","q33","benifit"))
market.push(new Array("국내영업","19","chk2","q33"))
market.push(new Array("수출영업","27","chk2","q33","divide"))
market.push(new Array("B2B마케팅(광고협찬)","40","chk2","q33"))
market.push(new Array("B2B마케팅(사입)","41","chk2","q33"))
market.push(new Array("B2BMD(사입)","46","chk2","q33"))
market.push(new Array("서포터즈","42","chk2","q33","divide"))
market.push(new Array("리콜교환","55","chk2","q33","divide"))



offline = new Array()
offline.push(new Array("오프라인","1"))
offline.push(new Array("사판","2"))

reOrder = new Array()
reOrder.push(new Array("백오더","1"))
reOrder.push(new Array("누락","2"))
reOrder.push(new Array("오배송","3"))
reOrder.push(new Array("교환(단순변심)","4"))
reOrder.push(new Array("불량","5"))
reOrder.push(new Array("기타","6"))
reOrder.push(new Array("타사몰예판","7"))
reOrder.push(new Array("누락(검수요청)","8"))
reOrder.push(new Array("불량(검수요청)","9"))

duplicateDivide = new Array()
duplicateDivide.push(new Array("기본정보","1"))
duplicateDivide.push(new Array("체험단모집","2"))
duplicateDivide.push(new Array("무료체험","3"))


//code.js 변경 시 모바일 js 함께 변경 할 것
//petit: 쁘띠엘린브랜드, essen:에센루, store:자사노출 브랜드, family, 사이트URL:패밀리사이트, flkorea:남양주 물류에서 취급하는 브랜드, mkt: 마케팅브랜드0, goal : 매출목표사용
MD_brand = new Array()
MD_brand.push(new Array("엘리펀트이어스:EL","28","chk","petit","goal"))
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
MD_brand.push(new Array("리틀페넥:LI","1080","chk","petit","goal"))
MD_brand.push(new Array("오케이베이비:OB","1112","chk","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("엘라바:EA","1121","chk","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("키위:KI","1133","chk","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("하퍼스테이블:HT","1157","chk","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("퓨어닷:PD","1245","chk","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("플레이앤고:PG","1183","chk","petit"))
MD_brand.push(new Array("에이투:GA","1216","chk","petit","goal"))
MD_brand.push(new Array("홀레:HO","1256","chk","family","http://www.hollekorea.co.kr","petit","store","thumnail","mkt", "goal"))
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




MD_brand_reverse = new Array()
MD_brand_reverse.push(new Array("28","EL","chk","petit"))
MD_brand_reverse.push(new Array("29","TT"))
MD_brand_reverse.push(new Array("31","CD","chk","petit"))
MD_brand_reverse.push(new Array("32","KP","chk","petit"))
MD_brand_reverse.push(new Array("33","EV","chk","petit"))
MD_brand_reverse.push(new Array("43","AT","chk","petit"))
MD_brand_reverse.push(new Array("44","MG","chk","petit"))
MD_brand_reverse.push(new Array("45","IV","chk","petit"))
MD_brand_reverse.push(new Array("46","FS","chk","petit"))
MD_brand_reverse.push(new Array("47","JC","chk","petit"))
MD_brand_reverse.push(new Array("50","HC","chk","petit"))
MD_brand_reverse.push(new Array("53","LC","chk","petit"))
MD_brand_reverse.push(new Array("54","KE","chk","petit"))
MD_brand_reverse.push(new Array("55","KG","chk","petit"))
MD_brand_reverse.push(new Array("56","WO","chk","petit"))
MD_brand_reverse.push(new Array("58","AS","chk","petit"))
MD_brand_reverse.push(new Array("60","CT","chk","petit"))
MD_brand_reverse.push(new Array("61","AB","chk","petit"))
MD_brand_reverse.push(new Array("62","BK","chk","petit"))
MD_brand_reverse.push(new Array("63","HL","chk","petit"))
MD_brand_reverse.push(new Array("64","EP","chk","petit"))
MD_brand_reverse.push(new Array("546","PC","chk","petit"))
MD_brand_reverse.push(new Array("594","MA","chk","petit"))
MD_brand_reverse.push(new Array("595","LS","chk","petit"))
MD_brand_reverse.push(new Array("599","KL","chk","petit"))
MD_brand_reverse.push(new Array("626","LH","chk","petit"))
MD_brand_reverse.push(new Array("690","MY","chk","petit"))
MD_brand_reverse.push(new Array("691","DE","chk","petit"))
MD_brand_reverse.push(new Array("736","BP","chk","petit"))
MD_brand_reverse.push(new Array("755","EC","chk","petit"))
MD_brand_reverse.push(new Array("789","KW","chk","petit"))
MD_brand_reverse.push(new Array("888","SP","chk","petit"))
MD_brand_reverse.push(new Array("922","ST","chk","petit"))
MD_brand_reverse.push(new Array("932","IB","chk","petit"))
MD_brand_reverse.push(new Array("1015","LF","chk","petit"))
MD_brand_reverse.push(new Array("1062","SL","chk","petit"))
MD_brand_reverse.push(new Array("1080","LI","chk","petit"))
MD_brand_reverse.push(new Array("1112","OB","chk","petit"))
MD_brand_reverse.push(new Array("1121","EA","chk","petit"))
MD_brand_reverse.push(new Array("1133","KI","chk","petit"))
MD_brand_reverse.push(new Array("1157","HT","chk","petit"))
MD_brand_reverse.push(new Array("1183","PG","chk","petit"))
MD_brand_reverse.push(new Array("1216","GA","chk","petit"))
MD_brand_reverse.push(new Array("1245","PD","chk","petit"))
MD_brand_reverse.push(new Array("1256","HO","chk","petit"))
MD_brand_reverse.push(new Array("1257","BM","chk","petit"))
MD_brand_reverse.push(new Array("1291","IM","chk","petit"))
MD_brand_reverse.push(new Array("1301","HN","chk","petit"))
MD_brand_reverse.push(new Array("41","PE"))

//에센루 브랜드
MD_brand_reverse.push(new Array("821","EB","chk"))
MD_brand_reverse.push(new Array("822","BT","chk"))
MD_brand_reverse.push(new Array("823","MK","chk"))
MD_brand_reverse.push(new Array("824","MM","chk"))
MD_brand_reverse.push(new Array("857","DG","chk"))
MD_brand_reverse.push(new Array("863","ML","chk"))
MD_brand_reverse.push(new Array("899","CB","chk"))
MD_brand_reverse.push(new Array("913","DD","chk"))
MD_brand_reverse.push(new Array("976","SG","chk"))
MD_brand_reverse.push(new Array("1017","IE","chk"))
MD_brand_reverse.push(new Array("1018","CM","chk"))

MD_brand_reverse.push(new Array("1051","BZ","chk"))



//팝업창 자동 오픈 관련(실제 사이트 URL을 입력하십시오.)
Site_popup = new Array()
Site_popup.push(new Array("PetitelinStore","38"))
Site_popup.push(new Array("Elephantears","28"))
Site_popup.push(new Array("Kinderspel","32"))
Site_popup.push(new Array("Coddlelife","31"))
Site_popup.push(new Array("Erbababy","33"))
Site_popup.push(new Array("Tinytongs","29"))
Site_popup.push(new Array("Naturalattitude","43"))
Site_popup.push(new Array("Miloandgabby","44"))
Site_popup.push(new Array("Iviplaymat","45"))
Site_popup.push(new Array("Jellycatkorea","47"))
Site_popup.push(new Array("Hairclippy","50"))
Site_popup.push(new Array("Lemoncanvas","53"))
Site_popup.push(new Array("Kietla","54"))
Site_popup.push(new Array("KnotGenie","55"))
Site_popup.push(new Array("Wowcup","56"))
Site_popup.push(new Array("Aquascale","58"))
Site_popup.push(new Array("Beezeebee","61"))
Site_popup.push(new Array("Cunatent","60"))
Site_popup.push(new Array("Bookleben","62"))
Site_popup.push(new Array("Heylandandwhittle","63"))
Site_popup.push(new Array("Elprairie","64"))
Site_popup.push(new Array("PonyCycle","546"))
Site_popup.push(new Array("Mavete","594"))
Site_popup.push(new Array("LeonShoes","595"))
Site_popup.push(new Array("KaperLand","599"))
Site_popup.push(new Array("Lillehaven","626"))
Site_popup.push(new Array("Moyuum","690"))
Site_popup.push(new Array("Dueest","691"))
Site_popup.push(new Array("DevelPlanet","736"))
Site_popup.push(new Array("Ecleve","755"))
Site_popup.push(new Array("Kinderspel-wear","789"))
Site_popup.push(new Array("Sagepole","888"))
Site_popup.push(new Array("Smartrike","922"))
Site_popup.push(new Array("Innobaby","932"))
Site_popup.push(new Array("littlefennec","1080"))
Site_popup.push(new Array("kiwyworld","1133"))
Site_popup.push(new Array("puredot","1245"))
Site_popup.push(new Array("holle","1256"))
Site_popup.push(new Array("Biomera","1257"))
Site_popup.push(new Array("Moyuum365","1291"))
Site_popup.push(new Array("H:Ernaiv","1301"))
Site_popup.push(new Array("amoretto","36"))		//팝업


//--쁘띠엘린 스토어PC 메인에서 사용중인 브랜드 순서(인기순)-자사팀 요청 순서
partnerBrandArray=new Array()
//partnerBrandArray.push(new Array("데글링고스","857"))
//partnerBrandArray.push(new Array("마커스앤마커스","824"))
//partnerBrandArray.push(new Array("클라우드비","899"))
//partnerBrandArray.push(new Array("메켄메르크","823"))
//partnerBrandArray.push(new Array("도노도노","913"))
//partnerBrandArray.push(new Array("모이스앤로이스","863"))
//partnerBrandArray.push(new Array("슈너글","976"))
//partnerBrandArray.push(new Array("베이비젠","1051"))
//--쁘띠엘린 스토어PC 메인에서 사용중인 브랜드 순서(인기순)-자사팀 요청 순서



Product_status = new Array()
Product_status.push(new Array("<span style='color: red'>[단]</span>","1","chk"))
Product_status.push(new Array("<span style='color: blue'>[품]</span>","2","chk"))
Product_status.push(new Array("<span style='color: green'>[예]</span>","3","chk"))
Product_status.push(new Array("[한]","4","chk"))
Product_status.push(new Array("<span style='color: red'>[일단]</span>","5"))
Product_status.push(new Array("<span style='color: blue'>[일품]</span>","6"))
Product_status.push(new Array("<span style='color: green'>[일예]</span>","7"))
Product_status.push(new Array("[일한]","8"))


//오프라인 매장
off_region = new Array()
off_region.push(new Array("서울","5"))
off_region.push(new Array("경기","10"))
//off_region.push(new Array("인천","15")) //2020-06-19 사용안함
//off_region.push(new Array("세종","20")) //2017-10-31 사용안함
//off_region.push(new Array("대구","23")) //2017-09-12 사용안함, 2018-03-29 다시 사용, 2020-06-19 사용안함
//off_region.push(new Array("울산","25")) //2020-06-19 사용안함
//off_region.push(new Array("부산","30")) //2020-06-19 사용안함
//off_region.push(new Array("전북","35")) //2020-06-19 사용안함
//off_region.push(new Array("전남","40")) //2020-06-19 사용안함
//off_region.push(new Array("충남","50")) //2020-06-19 사용안함
off_region.push(new Array("주요판매처","0"))

off_type = new Array()
off_type.push(new Array("국내 유명 백화점","1"))
off_type.push(new Array("대형마트","2"))
off_type.push(new Array("국내 유명 스토어","3"))
off_type.push(new Array("면세점","4"))

part_reason = new Array()
part_reason.push(new Array("휴무대체", "1"))
part_reason.push(new Array("연차대체", "2"))
part_reason.push(new Array("행사", "3"))
part_reason.push(new Array("지원", "4"))
part_reason.push(new Array("기타", "5"))



MD_id = new Array()
MD_id.push(new Array("반짝반짝 별님", "1"))
MD_id.push(new Array("포근포근 해님", "2"))
MD_id.push(new Array("살랑살랑 바람", "3"))
MD_id.push(new Array("몽글몽글 구름", "4"))
MD_id.push(new Array("새근새근 달님", "5"))
MD_id.push(new Array("노래하는 부엉이","6"))
MD_id.push(new Array("날고 싶은 펭귄","7"))
MD_id.push(new Array("재주 많은 곰","8"))
MD_id.push(new Array("단발머리 병아리","9"))
MD_id.push(new Array("부끄럼쟁이 해달","10"))
MD_id.push(new Array("기분 좋은 코알라","11"))
MD_id.push(new Array("민첩한 코끼리","12"))

act_buying_source = new Array()
act_buying_source.push(new Array("쁘띠엘린스토어", "1"))
act_buying_source.push(new Array("온라인몰(쁘띠엘린 외)", "2"))
act_buying_source.push(new Array("백화점", "3"))
act_buying_source.push(new Array("로드샵", "4"))
act_buying_source.push(new Array("박람회", "5"))
act_buying_source.push(new Array("기타", "6"))


week_name = new Array()
week_name.push(new Array("월", "2"))
week_name.push(new Array("화", "3"))
week_name.push(new Array("수", "4"))
week_name.push(new Array("목", "5"))
week_name.push(new Array("금", "6"))