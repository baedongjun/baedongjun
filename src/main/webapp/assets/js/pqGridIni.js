var pqGridObj = {
	dataModel: {
		location: "remote",
		dataType: "JSON",
		method: "POST",
		url: "list.run",
		beforeSend: function() {
			if (curPageChange) searchV("go")
		},
		postData: function () {
			postdata = $("#searchForm").serializeObject()
			if ($("#grid_json").length>0) {
				if (getCookie("pqPaging").split("$")[0] != location.pathname || getCookie("pqPaging").indexOf("undefined") != -1) setCookie("pqPaging", "")

				postdata.totalRecords = getCookie("pqPaging").split("$")[1]
				postdata.curPageCookie = getCookie("pqPaging").split("$")[2]
				postdata.totalRecordsCookie = $("#grid_json").pqGrid("option","pageModel.totalRecords")
			}
			return postdata;
		},
		getData: function (dataJSON) {
			setCookie("pqPaging",location.pathname +"$"+ dataJSON.totalRecords +"$"+ dataJSON.curPage)
			return {curPage: parseInt(dataJSON.curPage),totalRecords: dataJSON.totalRecords,data: dataJSON.data}
		}
	},
	toolbar: {
		items: [
			{
				type: 'button',
				label: '검색',
				cls:'searchBtn',
				icon: 'ui-icon-search',
				listener: function () {
					searchV("go")
				}
			},
			{
				type: 'button',
				label: '전체',
				icon: 'ui-icon-refresh',
				cls:'searchBtnAll',
				listener: function () {
					searchV("")
				}
			},
			{
				type: 'button',
				label: '엑셀 &nbsp; &nbsp; &nbsp;',
				icon: 'ui-icon-arrowthickstop-1-s',
				cls:'excelBtnQuery',
				style:'display:none;',
				attr: 'id=sExcelDown',
				listener: function () {
					excelDownload("excelDown.run")
				}
			},
			{
				type: 'button',
				label: '엑셀 &nbsp; &nbsp; &nbsp;',
				icon: 'ui-icon-arrowthickstop-1-s',
				cls:'excelBtnNoraml',
				style:'display:none;',
				attr: 'id=sExcelDownThis',
				listener: function () {
					var format = "xlsx",
						blob = this.exportData({format: format,nopqdata: true,render: true});
					if(typeof blob === "string")blob = new Blob([blob]);
					saveAs(blob, "download_excel."+ format );
				}
			}
		]
	},
	columnTemplate: {valign: 'center'},
	pageModel: {type: "remote", rPP: 23, format: "#,###", layout: ["first", "prev", "|", "strPage", "|", "next", "last", "|", "strDisplay"]},
	sortModel: {type: 'local', single: false, number: true},
	selectionModel: {type: 'cell', mode: 'block'}, // 드래그로 데이터를 선택할 수 있다.
	copyModel: {render: true}, // 원본 값이 아닌 render된 값으로 복사한다.
	numberCell: {resizable: true, width : 50, show: true}, //리스트 맨 앞에 카운트를 추가한다.
	height: 'flex', // 데이터의 양에 맞게 조절한다.
	showTitle: false, // 리스트 타이틀을 보여주지 않는다.
	editable: false, // 리스트 내용의 편집을 가능하지 않게 한다.
	wrap: false, hwrap: false, // 컬럼의 내용이 많은 경우 말줄임표 표시를 한다.
	collapsible: {on: false, toggle: true}, // 전체 보기 버튼을 추가한다.
	hoverMode: 'row', // 마우스가 올라가면 한 줄 전체 색상을 변경한다.
	complete: function () {this.flex();PQonLoad(this)} // 각 컬럼을 내용양에 따라서 자동으로 width를 조절한다.
};

//pqGird에서 내용 ctrl+c ctrl+v 에서 render된 데이터로 태그까지 복사되는 부분 처리
document.addEventListener('copy', function(event){
	text = document.getSelection().toString().replace(/<br>/ig, "\r\n").replace(/<(\/)?([a-zA-Z]*)(\s[a-zA-Z]*=[^>]*)?(\s)*(\/)?>/ig, "");
	event.clipboardData.setData('text/plain', text);
	event.preventDefault();
});


//ko localize 파일도 수정을 해야하기 때문에. 여기에 반영.
(function( $ ){
	var pq = $.paramquery;
	var pager = pq.pqPager.regional['kr'] = {
		strDisplay: "총 {2} 중에 {0} ~ {1} 번째",
		strFirstPage: "맨 처음으로",
		strLastPage: "맨 마지막으로",
		strNextPage: "다음",
		strPage: "{0} / {1}",
		strPrevPage: "이전",
	};

	$.extend( pq.pqPager.defaults, pager );
})(jQuery)

//pqGrid 로딩 후 해야할 일이 있다면 당 페이지에 해당 함수명으로 만들어서 사용.
//pqGird에서 통계를 표현하는 경우 정리값 스타일 변경
function PQonLoad(val) {
	try {
		if (val.groupModel) {
			$('div[id^="pq-body-cell"]').each(function (i, e) {
				if (val.text().indexOf("Sum") != -1) {
					val.css("font-weight", "bold")
					val.css("background-color", "#e0e0e0")
				}
				val.text(val.text().replace("Sum: ", ""))
			});
		}

		//열 병합
		if (val.options.title.indexOf("autoMerge")!=-1) autoMerge(val,val.options.title.split(":")[0])
	} catch(e) {}
}

function searchV(val) {
	setCookie("pqPaging", "")
	if (val == "") {
		location.href = "?"
	} else {
		document.searchForm.submit()
	}
}

function pqGridObjBtnDelete() {
	for (var i=1 ; i<=3 ; i++) pqGridObj.toolbar.items[i] = ""
}

//하단 총계 자동 생성
function bottomSum(colM,sumField) {
	fieldSample = new Array()
	for (var i = 65; i <= 90; i++) fieldSample.push(String.fromCharCode(i))
	pqgridField = JSON.parse(JSON.stringify(fieldSample))
	h = 26
	outerFor : for (var i=0 ; i<fieldSample.length ; i++) {
		innerFor : for (var j=0 ; j<fieldSample.length ; j++) {
			pqgridField.push(fieldSample[i]+fieldSample[j])
			h++
			if (h>=100) break outerFor
		}
	}

	k = 0
	for (var i=0 ; i<colM.length ; i++) {
		if (colM[i].colModel) {
			for (var j=0 ; j<colM[i].colModel.length ; j++) {
				if (colM[i].colModel[j].bottomSum) sumField.push('"'+ colM[i].colModel[j].dataIndx +'":"sum('+ pqgridField[k] +':'+ pqgridField[k] +')"')
				k++
			}
		} else {
			if (colM[i].bottomSum) sumField.push('"'+ colM[i].dataIndx +'":"sum('+ pqgridField[k] +':'+ pqgridField[k] +')"')
			k++
		}
	}

	return JSON.parse(eval("'{"+ sumField.join(",") +"}'"))
}


//열 중복데이터 병합
function autoMerge(grid, val) {
	var mc = [],
		CM = grid.option("colModel"),
		data = grid.option("dataModel.data");

	thisMerge = val.split(",")
	for (var mergeI = 0 ; mergeI<thisMerge.length ; mergeI++) {
		val2 = parseInt(thisMerge[mergeI])
		var dataIndx = CM[val2].dataIndx, rc = 1, j = data.length;

		while (j--) {
			var cd = data[j][dataIndx],
				cd_prev = data[j - 1] ? data[j - 1][dataIndx] : undefined;
			if (cd_prev !== undefined && cd == cd_prev) {
				rc++;
			} else if (rc > 1) {
				mc.push({r1: j, c1: val2, rc: rc, cc: 1});
				rc = 1;
			}
		}
	}
	grid.option("mergeCells", mc);
	grid.refreshView();
}