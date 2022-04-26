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
				label: '�˻�',
				cls:'searchBtn',
				icon: 'ui-icon-search',
				listener: function () {
					searchV("go")
				}
			},
			{
				type: 'button',
				label: '��ü',
				icon: 'ui-icon-refresh',
				cls:'searchBtnAll',
				listener: function () {
					searchV("")
				}
			},
			{
				type: 'button',
				label: '���� &nbsp; &nbsp; &nbsp;',
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
				label: '���� &nbsp; &nbsp; &nbsp;',
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
	selectionModel: {type: 'cell', mode: 'block'}, // �巡�׷� �����͸� ������ �� �ִ�.
	copyModel: {render: true}, // ���� ���� �ƴ� render�� ������ �����Ѵ�.
	numberCell: {resizable: true, width : 50, show: true}, //����Ʈ �� �տ� ī��Ʈ�� �߰��Ѵ�.
	height: 'flex', // �������� �翡 �°� �����Ѵ�.
	showTitle: false, // ����Ʈ Ÿ��Ʋ�� �������� �ʴ´�.
	editable: false, // ����Ʈ ������ ������ �������� �ʰ� �Ѵ�.
	wrap: false, hwrap: false, // �÷��� ������ ���� ��� ������ǥ ǥ�ø� �Ѵ�.
	collapsible: {on: false, toggle: true}, // ��ü ���� ��ư�� �߰��Ѵ�.
	hoverMode: 'row', // ���콺�� �ö󰡸� �� �� ��ü ������ �����Ѵ�.
	complete: function () {this.flex();PQonLoad(this)} // �� �÷��� ����翡 ���� �ڵ����� width�� �����Ѵ�.
};

//pqGird���� ���� ctrl+c ctrl+v ���� render�� �����ͷ� �±ױ��� ����Ǵ� �κ� ó��
document.addEventListener('copy', function(event){
	text = document.getSelection().toString().replace(/<br>/ig, "\r\n").replace(/<(\/)?([a-zA-Z]*)(\s[a-zA-Z]*=[^>]*)?(\s)*(\/)?>/ig, "");
	event.clipboardData.setData('text/plain', text);
	event.preventDefault();
});


//ko localize ���ϵ� ������ �ؾ��ϱ� ������. ���⿡ �ݿ�.
(function( $ ){
	var pq = $.paramquery;
	var pager = pq.pqPager.regional['kr'] = {
		strDisplay: "�� {2} �߿� {0} ~ {1} ��°",
		strFirstPage: "�� ó������",
		strLastPage: "�� ����������",
		strNextPage: "����",
		strPage: "{0} / {1}",
		strPrevPage: "����",
	};

	$.extend( pq.pqPager.defaults, pager );
})(jQuery)

//pqGrid �ε� �� �ؾ��� ���� �ִٸ� �� �������� �ش� �Լ������� ���� ���.
//pqGird���� ��踦 ǥ���ϴ� ��� ������ ��Ÿ�� ����
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

		//�� ����
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

//�ϴ� �Ѱ� �ڵ� ����
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


//�� �ߺ������� ����
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