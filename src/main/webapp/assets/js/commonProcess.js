function common_change_code2(val, val2) {
	$.ajax({
		url: "/code2.run",
		data: {code1: val},
		async: false,
		success: function (content) {
			thisForm = val2.code2
			nullData(thisForm)
			thisForm[0] = new Option("전체", "")
			for (var i = 0, thisLen = content.code2.length; i < thisLen; i++) {
				thisForm[i + 1] = new Option(content.code2[i].name + " [" + content.code2[i].code + "]", content.code2[i].id)
			}
		}
	})
}

function common_change_code(val, val2) {
	thisValue = new Object()
	for (var i = 0; i < val; i++) thisValue["code" + (i + 1)] = val2.code[i].value
	$.ajax({
		url: "/code.run",
		data: thisValue,
		async: false,
		success: function (content) {
			for (var i = val; i < val2.code.length; i++) {
				nullData(val2.code[i])
			}
			thisForm = val2.code[val]
			thisForm[0] = new Option("전체", "")
			for (var i = 0, thisLen = content.code.length; i < thisLen; i++) {
				if (val == 2) {
					thisForm[i + 1] = new Option(content.code[i].name, content.code[i].id)
				} else {
					thisForm[i + 1] = new Option(content.code[i].name + " [" + content.code[i].code + "]", content.code[i].id)
				}
			}
		}
	})
}

function common_change_category(val, val2) {
	$.ajax({
		url: "/categoryName.run",
		data: {categorySite: val},
		async: false,
		success: function (content) {
			thisForm = val2.categoryName
			nullData(thisForm)
			thisForm[0] = new Option("전체", "")
			for (var i = 0, thisLen = content.categoryName.length; i < thisLen; i++) {
				thisForm[i + 1] = new Option(content.categoryName[i].name, content.categoryName[i].id)
			}
		}
	})
}