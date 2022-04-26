function split_Rs(obj,obj_val,obj_ini) {
	if (obj==undefined || obj_val=="") return
	if (obj_ini==undefined) obj_ini="-"
	obj_value = obj_val.split(obj_ini)
	if (obj.type=="select-multiple") {
		eval(obj.id + "Multi.setValue('" + obj_value + "')")
	}else{
		for (var i=0 ; i<obj_value.length ; i++) for (var j=0 ; j<obj.length ; j++) check_Rs(obj,obj_value[i])
	}


}
function check_Rs(obj,obj_val) {

	if (obj==undefined || obj_val=="") return
	if (obj.length==undefined) { //text객체거나 length=1인 radio, checkbox
		if (obj.type=="text" || obj.type=="date") {
			obj.value = obj_val
			obj.classList.add("chk_on")
		} else {
			if (obj.value==obj_val) obj.checked=true
		}
		return
	} else { //length>1인 radio, checkbox 거나 select
		if (obj[0].type==undefined && obj[0].length==undefined) { // 하나의 객체이름
			if (obj.type=="select-one") {
				obj.value = obj_val
				return
			} else if(obj.type=="select-multiple"){
				eval(obj.id+"Multi.setValue('"+obj_val+"')")
				return
			} else {
				for (var j=0 ; j<obj.length ; j++) {
					if (obj.options[j].value==obj_val) {
						obj.options[j].selected = true
						return
					}
				}
			}

		} else { // 객체이름 중복
			if (obj[0].type=="checkbox" || obj[0].type=="radio") {
				for (var j=0 ; j<obj.length ; j++) {
					if (obj[j].value==obj_val) {
						obj[j].checked = true
						return
					}
				}
			} else {
				for (var j=0 ; j<obj.length ; j++) {
					for (var k=0 ; k<obj[j].length ; k++) {
						if (obj[j].options[k].value==obj_val) {
							obj[j].options[k].selected = true
							return
						}
					}
				}
			}
		}
	}
}

function cut_Rs(obj,obj_val,obj_ini) {
	if (obj==undefined || obj_val=="") return
	obj_array = obj_ini.split(",")
	obj_array_now = 0
	for (var i=0 ; i<obj_array.length ; i++) {
		obj[i].value = obj_val.substr(obj_array_now,obj_array[i])
		obj_array_now = obj_array_now + obj_array[i]
	}
}

function check_img(val) {
	if (val.length>0) {
		file_ext = val.substring(val.length-3,val.length).toUpperCase()

		if (!(file_ext=="JPG" || file_ext=="GIF" || file_ext=="PNG")) {
			return false
		} else {
			return true
		}
	} else {
		return true
	}
}

function arrayPivot(val) { //피벗 배열 만들기
	matrix_pivot1 = new Array()
	matrix_pivot2 = new Array()

	for (var i=0 ; i<val.length ; i++) matrix_pivot1.push(i)
	if (val[0]) for (var j=0 ; j<val[0].length ; j++) matrix_pivot2.push(matrix_pivot1.join(",").split(","))
	for (var i=0 ; i<val.length ; i++) for (var j=0 ; j<val[0].length ; j++) matrix_pivot2[j][i] = val[i][j]

	return matrix_pivot2
}

function arrayMulti(val) { //다차원 배열 만들기
	matrix_1 = new Array()
	matrix_2 = new Array()

	if (val[0]) for (var i=0 ; i<val[0].length ; i++) matrix_1.push(i)
	for (var j=0 ; j<val.length ; j++) matrix_2.push(matrix_1.join(",").split(","))

	return matrix_2
}

if (navigator.userAgent.indexOf("Firefox")>-1) {
	HTMLElement.prototype.__defineGetter__("innerText",function(){return this.textContent})
	HTMLElement.prototype.__defineSetter__("innerText",function(txt){this.textContent=txt})
}



//########################################################################################## 이미지 온 아웃
function MM_swapImgRestore() {
	var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
}

function MM_swapImage() {
	var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
		if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
}

function MM_findObj(n, d) { //v4.0
	var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
		d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
	if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
	for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
	if(!x && document.getElementById) x=document.getElementById(n); return x;
}

//########################################################################################## 쿠키값 제어

function getCookie( name ){
	var nameOfCookie = name + "=";
	var x = 0;
	while ( x <= document.cookie.length ) {
		var y = (x+nameOfCookie.length);
		if ( document.cookie.substring( x, y ) == nameOfCookie ) {
			if ( (endOfCookie=document.cookie.indexOf( ";", y )) == -1 ) {
				endOfCookie = document.cookie.length;
			}
			return unescape( document.cookie.substring( y, endOfCookie ) );
		}
		x = document.cookie.indexOf( " ", x ) + 1;
		if ( x == 0 ) {
			break;
		}
	}
	return "";
}

function setCookie( cookieName, cookieValue, expires, path, domain, secure ){
	document.cookie =
			cookieName + '=' + cookieValue
			+ (expires ? '; expires=' + expires.toGMTString() : '')
			+ '; path=/'
			+ (domain ? '; domain=' + domain : '')
			+ (secure ? '; secure' : '');
}

//########################################################################################## 기타

function setPreviewBox(val,content){
	if (val=="visible") {
		document.getElementsByName("preview")[0].style.left = event.x + document.body.scrollLeft + "px";
		document.getElementsByName("preview")[0].style.top = event.y + 400 + document.body.scrollTop + "px";
	}
	if (content!=undefined) document.getElementsByName("preview")[0].innerHTML=content
	document.getElementsByName("preview")[0].style.visibility = val;
}

function check2radio(form,val) {	//******************************************** checkbox 객체를 radio객체처럼 사용하기
	val_checked = val.checked
	for (var i=0 ; i<form.elements[val.name].length ; i++) form.elements[val.name][i].checked=false
	if (val_checked) {val.checked=true} else {val.checked=false}
}

function html_view(val) {
	if (val==0 || val==undefined) {
		return ""
	} else {
		return val
	}
}

//########################################################################################## 숫자 및 금액 입력

function comma_make(numstring) { //숫자 컴마 찍기
	return Number(erase_comma(numstring)).toLocaleString("en")
}
function erase_comma(numstring) { //,없애기
	numstring = defaultNum(numstring,0)
	return parseFloat(numstring.toString().replace(/,/g, ''));
}

function numberformat(val,val2) {//0없애기
	if (val==null || val=="" || val=="0") {
		if (val2) {
			return val2
		} else {
			return ""
		}
	} else {
		return val
	}
}

function number_format(obj) { //계산하기
	numstring = obj.value
	count_value = numstring.substring(0,1) + numstring.substring(1,numstring.length).replace(/-/g, '')
	obj.value = comma_make(erase_comma(count_value))
}

function make_money_down(val) {
	return comma_make(parseInt(erase_comma(val)/10)*10);
}

function make_money(val) {
	return comma_make(parseInt(erase_comma(val)/100)*100);
}

function make_money_up(val) {
	return comma_make(Math.round(erase_comma(val)/10)*10);
}

function ToFixed(val,val2) {
	return parseFloat(val.toFixed(val2))
}


function make_persent(num,sum,su) {
	if (defaultNum(sum,0) == 0){
		return ""
	}else {
		return parseFloat(num / sum * 100).toFixed(su)
	}

}

//########################################################################################## 문자 형식 맞추기
function string_reduce(val,len) {
	if (val.length>len) {
		return val.substring(0,len) + "..."
	} else {
		return val
	}
}

function convert_time (second_value) {
	sec_value = second_value%60
	hour_value = parseInt(second_value / 3600)
	min_value = parseInt((second_value - hour_value*3600) / 60)
	this_value = hour_value + ":" + select_zero(min_value,2) + ":" + select_zero(sec_value,2)
	return this_value
}

function defaultValue(val,val2) {
	if (val!="0" && (val=="" || val==undefined)) {
		int_value = val2
	} else {
		int_value = val
	}
	return int_value
}

function defaultDate(val,val2,val3) {
	if (val==undefined || (val+"").indexOf("1900-01-01")!=-1) {
		int_value = val2.substr(0,val3)
	} else {
		int_value = val.substr(0,val3)
	}
	return int_value
}

function defaultNum(val,val2) {
	if (val=="0" || val=="" || val==undefined) {
		int_value = val2
	} else {
		int_value = val
	}
	return int_value
}

function valueChange(val,val2,val3) {
	int_value = (val==val2) ? val3 : val;
	return int_value
}

function select_zero(reg_num,num) { //자릿수에 맞게 0 넣기 수,0갯수
	rnum = new String(reg_num)
	zero = ""
	for (var s_z=0 ; s_z<num-rnum.length ; s_z++) zero = zero + "0"
	return zero + rnum
}

function sortNumber(a,b) {
	return a - b;
}
function sortNumberInverse(a,b) {
	return b - a;
}

function view_img(dir,img_name) {
	var view_img = window.open("","","top=100,left=100,width=1024,height=768,scrollbars,resizable")
	view_img.document.open()
	view_img.document.write("<html><head><title>이미지보기</title></head><body topmargin='0' leftmargin='0'>")
	view_img.document.write ("<img src='" + dir + "/" + img_name + "' onclick='self.close()' style='cursor:hand' name='image'>")
	view_img.document.write("</body></html>")
	view_img.document.close()
}

function view_img_size(val) {
	var view_img = window.open("","","top=100,left=100,width=1024,height=768,scrollbars,resizable")

	view_img.document.open()
	view_img.document.write("<html><head><title>이미지보기</title><script src=\"/assets/fixed_js/jquery/jquery-3.4.1.js\"></script><script src=\"/assets/fixed_js/zoomer/jquery.fs.zoomer.min.js\"></script><link rel=\"stylesheet\" href=\"/assets/fixed_js/zoomer/jquery.fs.zoomer.css\"/></head><body topmargin='0' leftmargin='0'>")
	view_img.document.write ("<div class='viewer' style='width:1024px; height:768px;'><img src='" + val + "' onclick='self.close()' style='cursor:hand' name='image'></div>")
	view_img.document.write("</body></html>")
	view_img.document.close()

	view_img.onload = function () {
		view_img.$(".viewer").zoomer()
	}

}

function spaceOneMake(val) {//연속되는 spacebar를 하나로 변경
	processVal = val
	while (processVal.indexOf("  ")!=-1) {
		processVal = processVal.replace(/  /g," ")
	}

	return processVal
}

//########################################################################################## png 보기

function setPng24(obj) {
	if ((navigator.appVersion.indexOf("MSIE 6")>0 && navigator.appVersion.indexOf("MSIE 7")==-1) || navigator.appVersion.indexOf("MSIE 5")>0) {
		obj.width=obj.height=1;
		obj.className=obj.className.replace(/\bpng24\b/i,'');
		obj.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+ obj.src +"',sizingMethod='image');"
		obj.src='';
		return '';
	}
}

//########################################################################################## ajax

var oXMLHTTP = false
function createRequest(oReturn) {
	if (oReturn) {
		try {
			return new ActiveXObject("Microsoft.XMLHTTP");
		} catch (othermicrosoft) {
			// code for modern browsers
			return new XMLHttpRequest();
		}
	} else {
		try {
			oXMLHTTP = new ActiveXObject("Microsoft.XMLHTTP");
		} catch (othermicrosoft) {
			// code for modern browsers
			oXMLHTTP = new XMLHttpRequest();
		}
	}
}

//Ajax사용 객체를 생성하고, 함수 내부에서 Json형태로 리턴함.. 내부에서 createRequest하고변수에 할당해야 같은 페이지내에서 다른 Ajax와 충돌없음.
//processRequest("GET", "/json/main_quick_right.json", makeQuickLeft3, null)		////  "GET, POST", "호출파일", "사용자 함수", "Post시 Post데이터 val1=1&val2=2........"

function processRequest(strMethod, strUrl, strPostData){
	var returnJSON;
	var JsonHTTPobj = createRequest(true)
	JsonHTTPobj.open(strMethod, strUrl, false)
	JsonHTTPobj.onreadystatechange = function() {
		if(JsonHTTPobj.readyState ==4 && JsonHTTPobj.status ==200){
			returnJSON = JSON.parse(JsonHTTPobj.responseText);
		}
	}
	JsonHTTPobj.send(strPostData)
	return returnJSON;
}




function getRequestBody(oForm) {
	var aParams = new Array();
	for (var i=0 ; i < oForm.elements.length; i++) {
		var sParam = escape(oForm.elements[i].name)
		sParam += "="
		sParam += escape(oForm.elements[i].value)
		aParams.push(sParam)
	}
	return aParams.join("&");
}

//########################################################################################## div 중앙에 띄우기

function showCenterDiv(val,wid,hei) {
	var d = document;
	var w = d.body.clientWidth;   //d.documentElement.clientWidth
	var h = d.body.clientHeight;  //d.documentElement.clientHeight
	var x = (window.pageXOffset) ?
		window.pageXOffset : (d.documentElement && d.documentElement.scrollLeft) ?
		d.documentElement.scrollLeft : (d.body) ? d.body.scrollLeft : 0;
	var y = (window.pageYOffset) ?
		window.pageYOffset : (d.documentElement && d.documentElement.scrollTop) ?
		d.documentElement.scrollTop : (d.body) ? d.body.scrollTop : 0;
	if (val) {
		val.style.left = ((w/2)+x) - wid/2;
		val.style.top = ((h/2)+y) - hei/2;
		val.style.display = "";
	}
}


//########################################################################################## checkbox

function checkCheckbox(val) { // 체크 수 확인
	checkItem = 0

	if (val==undefined) return 0

	if (val.type=="select-one") {
		return selectSelect(val)
	} else {
		if (val.length) {
			for (var checkItemi=0 ; checkItemi<val.length ; checkItemi++) if (val[checkItemi].checked) checkItem = checkItem + 1
		} else {
			if (val.checked) checkItem = checkItem + 1
		}
	}

	return checkItem
}

function selectSelect(val) { // select 수 확인
	checkItem = 0
	if (val) {
		if (val.length) {
			for (var checkItemi=0 ; checkItemi<val.length ; checkItemi++) {
				if (val[checkItemi].selected) {
					checkItem = checkItem + 1
				}
			}
		}
	}

	return checkItem
}

function check_box(form,val,same) { //선택한 체크박스의 바로 뒤에 있는 체크박스 처리
	nextElement = 0
	for (var i=0 ; i<form.elements.length ; i++) {
		if (form.elements[i]==val) {
			nextElement = i + 1
			break
		}
	}

	if (same==undefined) {
		form.elements[nextElement].checked=!val.checked
	} else {
		form.elements[nextElement].checked=val.checked
	}
}

function dateAdd(val,val1,val2) {
	var gubun = {y:"FullYear",m:"Month",d:"Date",h:"Hours",mi:"Minutes",s:"Seconds",ms:"Milliseconds"}[val.toLowerCase()]
	val2["set"+ gubun](val2["get"+ gubun]()+val1)

	return val2
}

function selectSearchWord(searchSelect,val2) {//select객체 단어검색
	searchOK = false
	for (var i=0 ; i<searchSelect.length ; i++) {
		if (val2=="" || searchSelect[i].text.indexOf(val2)==-1) {
			searchSelect[i].style.backgroundColor="white"
		} else {
			searchSelect[i].style.backgroundColor="yellow"
			searchOK = true
		}
	}

	if (searchOK) {
		for (var i=0 ; i<searchSelect.length ; i++) {
			if (searchSelect[i].style.backgroundColor=="yellow") {
				searchSelect[i].selected=true
				break
			}
		}
	} else {
		alert("해당 단어로 검색하지 못했습니다.")
	}
}

function multiSelectedToArray(val) {
	returnArray = new Array()
	for (i=0 ; i<val.length ; i++) if (val[i].selected) returnArray.push(val[i].value)
	return returnArray
}


function delData(sourceData) {
	for (var i = sourceData.length - 1; i >= 0; i--) {
		if (sourceData.options[i].selected) sourceData.options[i] = null
	}
}
function nullData(sourceData) {
	for (var i = sourceData.length - 1; i >= 0; i--) sourceData[i] = null
}
function selectData(sourceData,val) {
	for (var i = 0 ; i < sourceData.length ; i++) sourceData[i].selected = defaultValue(val,true)
}


function resizeFrame(val) {
	val.height = val.contentWindow.document.body.scrollHeight;
}


invoiceArray = new Array() //택배사, 택배확인 URL, 송장번호 시작문자, 송장번호 길이
//CJ대한통운 2017-07-25 "34" 추가, 2017-11-15 베이비젠 "61" 추가, 2018-12-13 엘라바 "62" 추가, 2019-08-28 "35" 추가
//덕영, 와이드홍, 몬트라움(3pl)
invoiceArray.push(new Array("CJ대한통운","https://www.doortodoor.co.kr/parcel/doortodoor.do?fsp_action=PARC_ACT_002&fsp_cmd=retrieveInvNoACT&invc_no=",[33,34,35,38,55,61,62,63,64,69,84],[12]))
//레몬캔버스(천일 2020-01-28 "52" 추가, 4개 택배사를 지역에 따라서 구분해서 쓰고 있음.)
invoiceArray.push(new Array("건영택배","http://www.kunyoung.com/goods/goods_02.php?mulno=",[11],[10]))
invoiceArray.push(new Array("천일택배","http://www.chunil.co.kr/kor/taekbae/HTrace.jsp?transNo=",[11,51,52],[11]))
invoiceArray.push(new Array("천일택배","http://www.chunil.co.kr/kor/taekbae/HTrace.jsp?transNo=",[30,99],[11]))
invoiceArray.push(new Array("호남택배","http://www.honamlogis.co.kr/page/index.php?pid=tracking_number&SLIP_BARCD=",[30],[10]))
invoiceArray.push(new Array("롯데택배","https://www.lotteglogis.com/home/reservation/tracking/invoiceView/?InvNo=",[22,23,30,40],[12]))
//점보의자
invoiceArray.push(new Array("한진택배","https://www.hanjin.co.kr/kor/CMS/DeliveryMgr/WaybillResult.do?mCode=MN038&schLang=KR&wblnum=",[41,42],[12]))
//밀로앤개비(21.3월 이후 부터 호남택배)
invoiceArray.push(new Array("경동택배","https://kdexp.com/basicNewDelivery.kd?barcode=",[12,31],[13,14]))
//밀로앤개비(호남이 못 가는 곳에 대신택배)
invoiceArray.push(new Array("대신택배","https://www.ds3211.co.kr/freight/internalFreightSearch.ht?billno=",[23],[13]))


function getInvoiceUrl(invoice, view, viewDay){
	var replaceInv = invoice.replace(/-/g, "");
	var invoiceLength = replaceInv.length;
	var invoice2 = replaceInv.substring(0,2);
	var invUrl = "", returnValue = "", postName = "택배";

	firstArray = new Array()
	lengthArray = new Array()
	thisInvoice = -1

	if(viewDay <= 90){ //90일 이하인 경우만 택배 송장조회 가능
		for (var i=0 ; i<invoiceArray.length ; i++) {
			invoiceArray[i][2].forEach (function (val,idx,arr) {if(val==invoice2) firstArray.push(i)})
			invoiceArray[i][3].forEach (function (val,idx,arr) {if(val==invoiceLength) lengthArray.push(i)})
		}
		for (var i=0 ; i<firstArray.length ; i++) for (var j=0 ; j<lengthArray.length ; j++) if (firstArray[i]==lengthArray[j]) {thisInvoice = firstArray[i];break}
		if (thisInvoice!=-1) {
			postName = invoiceArray[thisInvoice][0]
			invUrl = invoiceArray[thisInvoice][1]
		}
	}

	if(view =="url"){
		returnValue = invUrl;
	}else{
		returnValue = postName;
	}

	return returnValue;
}

function isDate(val) {
	var pattern = /[0-9]{4}-[0-9]{2}-[0-9]{2}/;
	if (pattern.test(val)) {
		return true
	} else {
		return false
	}
}




String.prototype.trim = function() { return this.replace(/(^\s*)|(\s*$)/gi, ""); }

Array.prototype.unique = function(){
	var aa = {};
	for(var i=0; i<this["length"]; i++) if(typeof aa[this[i]] == "undefined" && !(this[i]=="" || this[i]==undefined)) aa[this[i]] = 0;
	this["length"] = 0;
	for(var i in aa) this[this["length"]] = i;
	return this;
}

jQuery.fn.serializeObject = function () {
	obj = {};
	jQuery.each(this.serializeArray(), function () {
		if (obj[this.name] == undefined) {
			obj[this.name] = this.value
		} else {
			if (!obj[this.name].push) {
				obj[this.name] = [obj[this.name]]
			}
			obj[this.name].push(this.value)
		}
	});

	return obj;
};

function excelDownload(val) {
	var $preparingFileModal = $("#preparing-file-modal");
	$preparingFileModal.dialog({ modal: true });
	$("#progressbar").progressbar({value: false});
	$.fileDownload(val, {
		data: $("#searchForm").serializeObject(),
		successCallback: function (url) {
			$preparingFileModal.dialog('close');
		},
		failCallback: function (responseHtml, url) {
			$preparingFileModal.dialog('close');
		}
	});
	return false;
}

function fnExcelReport(id, title) {
	var tab_text = '<html xmlns:x="urn:schemas-microsoft-com:office:excel">';
	tab_text = tab_text + '<head><meta http-equiv="content-type" content="application/vnd.ms-excel; charset=UTF-8"/>';
	tab_text = tab_text + '<xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet>'
	tab_text = tab_text + '<x:Name>Sheet1</x:Name>';
	tab_text = tab_text + '<x:WorksheetOptions><x:Panes></x:Panes></x:WorksheetOptions></x:ExcelWorksheet>';
	tab_text = tab_text + '</x:ExcelWorksheets></x:ExcelWorkbook></xml></head><body>';
	tab_text = tab_text + "<table border='1px'>";

	var exportTable = $('#' + id).clone();
	exportTable.find('script').each(function (index, elem) { $(elem).remove(); });
	exportTable.find('div').each(function (index, elem) { $(elem).replaceWith(elem.innerText); });
	exportTable.find('span').each(function (index, elem) { $(elem).replaceWith(elem.innerText); });
	exportTable.find('input').each(function (index, elem) {	$(elem).replaceWith(elem.innerText); });
	exportTable.find('a').each(function (index, elem) { $(elem).replaceWith(elem.innerText); });
	exportTable.find('img').each(function (index, elem) { $(elem).remove(); });

	tab_text = tab_text + exportTable.html();
	tab_text = tab_text + '</table></body></html>';

	var blob = new Blob([tab_text], {type: "application/vnd.ms-excel;charset=utf-8;"});
	var elem = window.document.createElement('a');
	elem.setAttribute('href', window.URL.createObjectURL(blob));
	elem.setAttribute('download', title+'.xls');
	elem.style.display = 'none';
	document.body.appendChild(elem);
	elem.click();
	document.body.removeChild(elem);
}

var totalPage = 1
function makePaging(curPage, pageSize, blockSize, totalRecords) {
	totalPage = parseInt(totalRecords / pageSize) + ((totalRecords % pageSize > 0) ? 1 : 0)
	totalBlock = parseInt(totalPage / blockSize) + ((totalPage % blockSize > 0) ? 1 : 0)
	nowBlock = parseInt(curPage / blockSize) + ((curPage % blockSize > 0) ? 1 : 0)
	startNum = (nowBlock - 1) * blockSize + 1
	endNum = (nowBlock<totalBlock) ? startNum - 1 + blockSize : startNum - 1 + parseInt(totalPage % blockSize)

	pageArray = new Array()
	pageArray.push('<button type="button" class="ui-button ui-corner-all ui-widget" onclick="goToPage(1)"><span class="ui-icon ui-icon-seek-first"></span></button>')
	pageArray.push('<button type="button" class="ui-button ui-corner-all ui-widget" onclick="goToPage('+ (curPage - 1) +')"><span class="ui-icon ui-icon-seek-prev"></span></button>')
	for (var i=startNum ; i<=endNum ; i++) pageArray.push('<button type="button" class="ui-button ui-corner-all ui-widget"'+ ((i==curPage) ? ' style="color:red">'+ i +'</button>' : ' onclick="goToPage('+ i +')">'+ i +'</button>'))
	pageArray.push('<button type="button" class="ui-button ui-corner-all ui-widget" onclick="goToPage('+ (curPage + 1) +')"><span class="ui-icon ui-icon-seek-next"></span></button>')
	pageArray.push('<button type="button" class="ui-button ui-corner-all ui-widget" onclick="goToPage('+ totalPage +')"><span class="ui-icon ui-icon-seek-end"></span></button>')

	document.searchForm.pageSize.value = pageSize

	return pageArray.join("")
}
function goToPage(val) {
	$.showLoading({allowHide: true});
	pageNum = val
	if (val < 1) pageNum = 1
	if (val > totalPage) pageNum = totalPage

	if (curPageChange) {
		searchV();
	} else {
		document.searchForm.curPage.value = pageNum
		document.searchForm.submit()
	}
}
function searchV() {
	if (document.searchForm.curPage !=null  && document.searchForm.totalRecords !="") {
		document.searchForm.curPage.value = 1
		document.searchForm.totalRecords.value = 0
	}
	document.searchForm.submit()
}

function ZipSearch(val){
	//다음 API이용 우편번호 찾기
	formname = val;
	window.open("/searchZIP","zipsearch","width=550,height=600,top=0,left=0, scrollbars=no,resizable");
}

function getParam(val, val2) {
	if (val.indexOf("."+ val2 +",")!=-1) {
		return val.split(val2 + ',"')[1].split('"')[0]
	} else {
		return ""
	}
}
