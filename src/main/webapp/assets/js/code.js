//�߿�
//�ڶ쿤���ۿ� ���Ǵ� ���밪�� ������ code.js ���� �߰� �Ǵ� ����/������ >> ������>>APP����>>App���� XML ����>code.js ������ ���־�� �Ѵ�.
//code.js���� �Ϻ� ���� �������� Ȥ�ø� ������ ���ؼ� �����ø��� �������ش�.
//code.js ���� �� ����� js �Բ� ���� �� ��
function make_select_code(choice,choice_array,name,class_name,add,wid,minusTag,minus,d_val,write_doc,skin) //��ü,�迭,�̸�,Ŭ������,�߰�����,����ũ��,�߰��Լ�(select�� ����, radio,checkbox�� �Լ��߰�),Ư������Ÿ ����,���ð�,document.write ���� �ʴ´�., multi select ������
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
					rValue.push('<optgroup label="�� '+ this_array[i][0] +'"></optgroup>')
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
				rValue.push('</div><span style="white-space:nowrap;cursor:hand;font-weight:bold" onclick=make_choice_fnc("'+ name + this_array[i][1] +'");'+ func +'>�� '+this_array[i][0]+'</span><br><div id="search_' + name + this_array[i][1] +'" name="search_' + name + this_array[i][1] +'" style="display:none">')
			} else {
				rValue.push('</div><span style="white-space:nowrap;cursor:hand;font-weight:bold" onclick=make_choice_fnc("'+ name + this_array[i][1] +'")>�� '+this_array[i][0]+'</span><br><div id="search_'+ name + this_array[i][1] +'" name="search_'+ name + this_array[i][1] +'" style="display:none">')
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
manage_marriage.push(new Array("��ȥ","n"))
manage_marriage.push(new Array("��ȥ","y"))



function getCodeArray(choice_array){
	return eval(choice_array);
}

manage_emploee = new Array()
manage_emploee.push(new Array("����","y"))
manage_emploee.push(new Array("���","n"))
manage_emploee.push(new Array("���","x"))
manage_emploee.push(new Array("���/���� ����","b"))

manage_position = new Array()
manage_position.push(new Array("ȸ��","0"))
manage_position.push(new Array("����","1"))
manage_position.push(new Array("�λ���","2"))
manage_position.push(new Array("����","14")) //������ �Ҵ�
manage_position.push(new Array("��","11"))
manage_position.push(new Array("�̻�","3"))
manage_position.push(new Array("����","4"))
manage_position.push(new Array("����","5"))
manage_position.push(new Array("����","6"))
manage_position.push(new Array("�븮","7"))
manage_position.push(new Array("����","12"))
manage_position.push(new Array("���","8"))
manage_position.push(new Array("�ɻ������","9"))
manage_position.push(new Array("������","10"))
manage_position.push(new Array("3pl���","13"))

manage_duty = new Array()
manage_duty.push(new Array("�ӿ�","1"))
manage_duty.push(new Array("������","2"))
manage_duty.push(new Array("����","3"))
manage_duty.push(new Array("����","4"))
manage_duty.push(new Array("�ܺ�","5"))






moon = new Array()
moon.push(new Array("���","1"))
moon.push(new Array("����","2"))

sex = new Array()
sex.push(new Array("��","1"))
sex.push(new Array("��","2"))

yesOrNo = new Array()
yesOrNo.push(new Array("��","y"))
yesOrNo.push(new Array("�ƴϿ�","n"))


membership = new Array()
membership.push(new Array("�����","Y"))
membership.push(new Array("�Ϲ�","N"))

mem_gubun = new Array()
mem_gubun.push(new Array("�Ϲ�","1"))
mem_gubun.push(new Array("ü���","3"))


title_location = new Array()
title_location.push(new Array("�� Front","1","chk"))
title_location.push(new Array("Back ��","2","chk"))
title_location.push(new Array("�������","3","chk"))
title_location.push(new Array("Ÿ���","4"))
title_location.push(new Array("�׷�","5"))

//push : �� �ڻ�� ���� > ����Ʈ���� > �̺�Ʈ ���� > APP Ǫ�� ����
userrank = new Array()
userrank.push(new Array("MEMBER","1","chk"))
userrank.push(new Array("FRIEND","6","chk","push"))
userrank.push(new Array("FAMILY","7","chk","push"))
userrank.push(new Array("VIP","8","chk","push"))
userrank.push(new Array("VIP PLUS","9","chk","push"))
userrank.push(new Array("GUEST","99"))

tel = new Array()
tel.push(new Array("02 (����)","02"))
tel.push(new Array("032 (��õ)","032"))
tel.push(new Array("042 (����)","042"))
tel.push(new Array("062 (����)","062"))
tel.push(new Array("053 (�뱸)","053"))
tel.push(new Array("052 (���)","052"))
tel.push(new Array("051 (�λ�)","051"))
tel.push(new Array("031 (���)","031"))
tel.push(new Array("033 (������)","033"))
tel.push(new Array("043 (���)","043"))
tel.push(new Array("041 (�泲)","041"))
tel.push(new Array("063 (����)","063"))
tel.push(new Array("061 (����)","061"))
tel.push(new Array("054 (���)","054"))
tel.push(new Array("055 (�泲)","055"))
tel.push(new Array("064 (���ֵ�)","064"))
tel.push(new Array("070 (���ͳ�)","070"))
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


//���� tel, handphone�� �Ʒ������� ��ó�ϰ� �����Ұ�.2018-10-11
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
allphone.push(new Array("02 (����)","02","tel"))
allphone.push(new Array("032 (��õ)","032","tel"))
allphone.push(new Array("042 (����)","042","tel"))
allphone.push(new Array("062 (����)","062","tel"))
allphone.push(new Array("053 (�뱸)","053","tel"))
allphone.push(new Array("052 (���)","052","tel"))
allphone.push(new Array("051 (�λ�)","051","tel"))
allphone.push(new Array("031 (���)","031","tel"))
allphone.push(new Array("033 (������)","033","tel"))
allphone.push(new Array("043 (���)","043","tel"))
allphone.push(new Array("041 (�泲)","041","tel"))
allphone.push(new Array("063 (����)","063","tel"))
allphone.push(new Array("061 (����)","061","tel"))
allphone.push(new Array("054 (���)","054","tel"))
allphone.push(new Array("055 (�泲)","055","tel"))
allphone.push(new Array("064 (���ֵ�)","064","tel"))
allphone.push(new Array("070 (���ͳ�)","070","tel"))



money_use = new Array()
money_use.push(new Array("�ҵ������","1"))
money_use.push(new Array("����������","2"))
money_use.push(new Array("��������","3"))

coupon_target = new Array()
coupon_target.push(new Array("<span style='color: red'>����</span>","1"))
coupon_target.push(new Array("<span style='color: blue'>�ǸŰ�</span>","2"))

coupon_month = new Array()
coupon_month.push(new Array("��ȿ�Ⱓ�� ����","0"))
coupon_month.push(new Array("����","-1"))
coupon_month.push(new Array("7�� �̳�","-7"))
coupon_month.push(new Array("1���� �̳�","1"))
coupon_month.push(new Array("3���� �̳�","3"))
coupon_month.push(new Array("6���� �̳�","6"))
coupon_month.push(new Array("1�� �̳�","12"))
coupon_month.push(new Array("����","120"))

coupon_site = new Array()
coupon_site.push(new Array("��ü","1"))
coupon_site.push(new Array("����","2"))

coupon_gubun = new Array()
coupon_gubun.push(new Array("��ǰ","product"))
coupon_gubun.push(new Array("�귣��","brand"))

payresult_arr = new Array()
payresult_arr.push(new Array("�������","0","chk"))
payresult_arr.push(new Array("����ڿϷ�","8","chk"))
payresult_arr.push(new Array("�����Ϸ�","9","chk"))
payresult_arr.push(new Array("ȯ�޿Ϸ�","10","chk"))

paymode_arr = new Array()
paymode_arr.push(new Array("ī�����","1","chk"))
paymode_arr.push(new Array("�������Ա�","3","chk"))
paymode_arr.push(new Array("����ó��","4"))
paymode_arr.push(new Array("����ũ��","5","chk"))
paymode_arr.push(new Array("����/��ġ�� ����","6"))  //2016-02-18 0������ > ����/��ġ�� ���� �� ��Ī ����
//paymode_arr.push(new Array("����Ʈ����","7"))
paymode_arr.push(new Array("�޴����Ҿװ���","8","chk"))
paymode_arr.push(new Array("PAYNOW����","9","chk"))
paymode_arr.push(new Array("�������Ա�(�������)","10","chk"))

paymode_arr.push(new Array("������ü","11","chk"))
paymode_arr.push(new Array("������ȭ��ǰ��","12","chk"))


delivery = new Array()
delivery.push(new Array("�������","1","chk"))
delivery.push(new Array("�����Ϸ�","2","chk"))
delivery.push(new Array("��ǰ�غ�","8","chk"))
delivery.push(new Array("�߼��غ�","3","chk"))
delivery.push(new Array("�߼��غ�(�Ϻ�)","4"))
delivery.push(new Array("�߼ۿϷ�(�Ϻ�)","5"))
delivery.push(new Array("�߼ۿϷ�","6","chk"))
delivery.push(new Array("�߼ۿϷ�(��)","7","chk"))
delivery.push(new Array("�ֹ����","99"))

pointPart = new Array()
pointPart.push(new Array("����(+)","����"))
pointPart.push(new Array("���(-)","���"))
pointPart.push(new Array("������(+)","������"))
pointPart.push(new Array("�ֹ����(-)","�ֹ����"))
pointPart.push(new Array("����(-)","����"))

pointGubun = new Array()
pointGubun.push(new Array("�귣��","0"))

bank_name = new Array()
bank_name.push(new Array("�Աݳ�������","�Աݳ�������"))
bank_name.push(new Array("��������","��������"))
bank_name.push(new Array("�������","�������"))
bank_name.push(new Array("����","����"))
bank_name.push(new Array("�츮����","�츮����"))
bank_name.push(new Array("��ü��","��ü��"))
bank_name.push(new Array("��ȯ����","��ȯ����"))
bank_name.push(new Array("�������","�������"))
bank_name.push(new Array("��������","����(�� ����)����"))
bank_name.push(new Array("��Ƽ����","��Ƽ����"))
bank_name.push(new Array("��������","��������"))
bank_name.push(new Array("�ϳ�����","�ϳ�����"))
bank_name.push(new Array("�ѹ�����","�ѹ�����"))

job = new Array()
job.push(new Array("���� �ֺ�","1"))
job.push(new Array("�� ����/����","2"))
job.push(new Array("������/����","3"))
job.push(new Array("����/�Ʒ�","4"))
job.push(new Array("����","5"))
job.push(new Array("���/�����","6"))
job.push(new Array("�����Ͼ","7"))
job.push(new Array("���� �� ����","8"))
job.push(new Array("����/������/����","9"))
job.push(new Array("������/�ڿ���","10"))
job.push(new Array("������(�ǻ�, ��ȣ�� ��)","11"))
job.push(new Array("���� ����","12"))
job.push(new Array("����/����/�����","13"))
job.push(new Array("�ѹ�/����","14"))
job.push(new Array("������","15"))
job.push(new Array("��ǻ�� ����(��Ÿ)","16"))
job.push(new Array("��ǻ�� ����(���ͳ�)","17"))
job.push(new Array("�л�","18"))
job.push(new Array("����/�濵","19"))
job.push(new Array("ȸ��/�繫","20"))
job.push(new Array("��Ÿ","21"))

bankname = new Array()
bankname.push(new Array("�츮���� : 1005-001-699918 (��)�ڶ쿤��","1","chk"))
bankname.push(new Array("�������� : 100-026-459898 (��)�ڶ쿤��","2","chk"))
bankname.push(new Array("�������� : 924501-01-306585 (��)�ڶ쿤��","3","chk"))
bankname.push(new Array("���� : 301-0066-9401-71 (��)�ڶ쿤��","4","chk"))


bankname2 = new Array()
bankname2.push(new Array("�ѱ��������", "02"))
bankname2.push(new Array("�������", "03"))
bankname2.push(new Array("��������", "04"))
bankname2.push(new Array("�ϳ�����", "05"))
bankname2.push(new Array("��������", "06"))
bankname2.push(new Array("�����߾�ȸ", "07"))
bankname2.push(new Array("�����߾�ȸ", "11"))
bankname2.push(new Array("��������", "12"))
bankname2.push(new Array("�����߾�ȸ", "16"))
bankname2.push(new Array("�츮����", "20"))
bankname2.push(new Array("�� ��������", "21"))
bankname2.push(new Array("�������", "22"))
bankname2.push(new Array("SC ��������", "23"))
bankname2.push(new Array("��������", "24"))
bankname2.push(new Array("��������", "25"))
bankname2.push(new Array("�� ��������", "26"))
bankname2.push(new Array("�ѱ���Ƽ����", "27"))
bankname2.push(new Array("�뱸����", "31"))
bankname2.push(new Array("�λ�����", "32"))
bankname2.push(new Array("��������", "34"))
bankname2.push(new Array("��������", "35"))
bankname2.push(new Array("��������", "37"))
bankname2.push(new Array("��������", "38"))
bankname2.push(new Array("�泲����", "39"))
bankname2.push(new Array("��ī��", "41"))
bankname2.push(new Array("�������ݰ�", "45"))
bankname2.push(new Array("�ſ����������߾�ȸ", "48"))
bankname2.push(new Array("��ȣ��������", "50"))
bankname2.push(new Array("�ѱ���Ƽ����", "53"))
bankname2.push(new Array("ȫ�����������", "54"))
bankname2.push(new Array("����ġ����", "55"))
bankname2.push(new Array("ABN�Ϸ�", "56"))
bankname2.push(new Array("JP���", "57"))
bankname2.push(new Array("�̾���õ�������", "59"))
bankname2.push(new Array("BOA(Bank of America)", "60"))
bankname2.push(new Array("�긲����", "64"))
bankname2.push(new Array("�žȻ�ȣ��������", "70"))
bankname2.push(new Array("��ü��", "71"))
bankname2.push(new Array("�ϳ�����", "81"))
bankname2.push(new Array("��ȭ����", "83"))
bankname2.push(new Array("�ż���", "87"))
bankname2.push(new Array("���� ���� ����", "88"))
bankname2.push(new Array("���̹�ũ", "89"))
bankname2.push(new Array("īī����ũ", "90"))


csGubun = new Array()
csGubun.push(new Array("�ֹ�","10000"))
csGubun.push(new Array("�ֹ����","1","chk"))
csGubun.push(new Array("��ȯ��û","6","chk"))
csGubun.push(new Array("��ǰ��û","7","chk"))
csGubun.push(new Array("�κ����/ȯ��","12","chk"))
csGubun.push(new Array("��ǰ","20000"))
csGubun.push(new Array("��ǰ����","4","chk"))
csGubun.push(new Array("��ǰ�ҷ�","9","chk"))
csGubun.push(new Array("��ǰ����","11","chk"))
csGubun.push(new Array("��ǰ�ɼǺ���","2","chk"))
csGubun.push(new Array("���","30000"))
csGubun.push(new Array("��۹���/����","5","chk"))
csGubun.push(new Array("�����������","3","chk"))
csGubun.push(new Array("�����","10","chk"))
csGubun.push(new Array("��Ÿ","8","chk"))
csGubun.push(new Array("����","40000"))
csGubun.push(new Array("ȸ���Ϸ�","13","chk"))
csGubun.push(new Array("ȸ����ǰ","14","chk"))





//Ÿ�� �ŷ� ���� �Ǵ� �ű� ����(Ÿ�� ������ ���)ó�� �� �� ���� : 41�� petitelin_MAIN..view_md_manage_status
//tasa : Ÿ�������(���ּ�/���� ���, ����, �����丮)���� Ÿ����� ���̵��� ����
//divide : �系��������(�Ǻ�����, �뷮����)���� �����.
//md : md ��ǰ�������� ������ ��(���� �������� ��)
//q33 : CS��㳻������ ��� ��.
//design : �����ο�û
//benifit : ��������
//autoplay : �����÷��� ���ּ� ���

market = new Array()
market.push(new Array("�ڻ��","1","chk","q33","md","design","benifit", "etcstatistics"))
market.push(new Array("OUTLET","52","chk2","q33","benifit"))
market.push(new Array("Awesome sales","49","chk2","q33","benifit"))
market.push(new Array("���ۺ�","54","chk2","q33","benifit"))

market.push(new Array("G����","2","chk","q33","md","tasa","design","benifit","autoplay"))
market.push(new Array("������ũ","3","q33","chk","benifit", "etcstatistics")) //2016-06-13 �� �ŷ� ����
market.push(new Array("GS�̼�","4","chk","q33","md","tasa","design","benifit","autoplay", "etcstatistics"))
market.push(new Array("�Ե�����","5","chk","q33","md","tasa","design","benifit","autoplay"))
market.push(new Array("�Ե�����[��]","6","q33","benifit")) //2015-10-20 �� �ŷ����� ó��//
market.push(new Array("�Ե���","57","chk","q33","md","tasa","design","benifit","autoplay"))
market.push(new Array("�ż���","7","chk","q33","md","tasa","design","benifit","autoplay"))
market.push(new Array("CJ��","8","chk","q33","md","tasa","design","benifit","autoplay", "etcstatistics"))
market.push(new Array("AK��(��)","9","q33","chk","benifit", "etcstatistics")) //2015-10-20 �� �ŷ����� ó��
market.push(new Array("11����","10","chk","q33","md","tasa","design","benifit","autoplay", "etcstatistics"))
market.push(new Array("10x10","11","q33", "etcstatistics"))  //2017-07-05 �� �ŷ����� ó��
market.push(new Array("��������","12","q33"))  //2017-07-05 �� �ŷ����� ó��
market.push(new Array("�ż���(��)","13","q33"))  //2015-10-20 �� �ŷ����� ó��
market.push(new Array("����","14","q33", "etcstatistics")) //2015-03-16 �� �ŷ����� ó��
market.push(new Array("����(��)","15","q33")) //2015-03-16 �� �ŷ����� ó��
market.push(new Array("����","17","chk","q33","md","tasa","design","benifit","autoplay"))
market.push(new Array("����","18","chk","q33","md","tasa","design","benifit","autoplay", "etcstatistics"))
market.push(new Array("��������","22","q33")) //2015-03-16 �� �ŷ����� ó��
market.push(new Array("��縮��","23","q33")) //2015-03-16 �� �ŷ����� ó��
market.push(new Array("�Ե�����","26","chk","q33","md","tasa","design","benifit","autoplay"))
market.push(new Array("Ƽ��","28","chk","q33","md","tasa","design","benifit","autoplay", "etcstatistics"))
market.push(new Array("�Ե�Ȩ����","29","q33")) //2015-10-20 �� �ŷ����� ó��
market.push(new Array("����Ȩ����","30","q33")) //2015-10-20 �� �ŷ����� ó��
market.push(new Array("������","35","q33", "etcstatistics")) //2016-8-05 �� �ŷ����� ó��
market.push(new Array("��������","36","chk","q33","md","tasa","design","benifit", "etcstatistics")) //2016-01-04 �� �ŷ����� ó�� --> 2017-09-11 �� �ŷ� �ٽ� ����
market.push(new Array("����","37","chk","q33","md","tasa","design","benifit","autoplay", "etcstatistics"))
market.push(new Array("������","38","chk","q33","md","tasa","design","benifit","autoplay", "etcstatistics"))
market.push(new Array("AK��","39","chk","q33","md","tasa","design","benifit","autoplay"))
market.push(new Array("����īī��","43","chk","q33","md","tasa","design","benifit","autoplay"))
market.push(new Array("�̸�Ʈ��","45","chk","q33","md","tasa","design","benifit","autoplay"))
market.push(new Array("�������","48","chk","q33","md","tasa","design","benifit","autoplay"))
market.push(new Array("Ȩ�ؼ���","50","chk","q33","md","tasa","design","benifit","autoplay"))
market.push(new Array("������(��)","53","chk","q33","md","tasa","design","benifit","autoplay"))
market.push(new Array("��������","58","chk","q33","md","tasa","design","benifit","autoplay"))

market.push(new Array("����ü��(�ڻ�)","56","chk2","q33","divide"))
market.push(new Array("����ü��(MKT)","20","chk2","q33","divide"))
market.push(new Array("����ü��(MD)","44","chk2","q33","divide"))
market.push(new Array("����ü��(BM)","51","chk2","q33","divide"))
market.push(new Array("�˻�/����(����/BM)","24","chk2","q33","divide"))
market.push(new Array("�ŷ�ó����","32","chk2","q33","divide"))
market.push(new Array("���ų�������(CS)","34","chk2","q33","divide"))
market.push(new Array("���̺����(CS)","47","chk2","q33","divide"))
market.push(new Array("����","33","chk2","q33","divide"))
market.push(new Array("����","21","chk2","q33","benifit"))
market.push(new Array("��������","19","chk2","q33"))
market.push(new Array("���⿵��","27","chk2","q33","divide"))
market.push(new Array("B2B������(��������)","40","chk2","q33"))
market.push(new Array("B2B������(����)","41","chk2","q33"))
market.push(new Array("B2BMD(����)","46","chk2","q33"))
market.push(new Array("��������","42","chk2","q33","divide"))
market.push(new Array("���ݱ�ȯ","55","chk2","q33","divide"))



offline = new Array()
offline.push(new Array("��������","1"))
offline.push(new Array("����","2"))

reOrder = new Array()
reOrder.push(new Array("�����","1"))
reOrder.push(new Array("����","2"))
reOrder.push(new Array("�����","3"))
reOrder.push(new Array("��ȯ(�ܼ�����)","4"))
reOrder.push(new Array("�ҷ�","5"))
reOrder.push(new Array("��Ÿ","6"))
reOrder.push(new Array("Ÿ�������","7"))
reOrder.push(new Array("����(�˼���û)","8"))
reOrder.push(new Array("�ҷ�(�˼���û)","9"))

duplicateDivide = new Array()
duplicateDivide.push(new Array("�⺻����","1"))
duplicateDivide.push(new Array("ü��ܸ���","2"))
duplicateDivide.push(new Array("����ü��","3"))


//code.js ���� �� ����� js �Բ� ���� �� ��
//petit: �ڶ쿤���귣��, essen:������, store:�ڻ���� �귣��, family, ����ƮURL:�йи�����Ʈ, flkorea:������ �������� ����ϴ� �귣��, mkt: �����ú귣��0, goal : �����ǥ���
MD_brand = new Array()
MD_brand.push(new Array("������Ʈ�̾:EL","28","chk","petit","goal"))
MD_brand.push(new Array("Ÿ�̴��뽺:TT","29"))
MD_brand.push(new Array("�ڵ������:CD","31","chk","petit"))
MD_brand.push(new Array("Ų������:KP","32","chk","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("���ٺ��:EV","33","chk","family","http://www.erbababy.co.kr/","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("��ƼƩ��:AT","43","chk","family","http://www.naturalattitude.co.kr/","petit","store","thumnail","add","mkt", "goal"))
MD_brand.push(new Array("�зξذ���:MG","44","chk","family","http://www.miloandgabby.co.kr/","petit","store","thumnail","add","mkt", "goal"))
MD_brand.push(new Array("���̺�:IV","45","chk","petit"))
MD_brand.push(new Array("��������:FS","46","chk","petit"))
MD_brand.push(new Array("����Ĺ:JC","47","chk","family","http://www.jellycatkorea.co.kr/","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("�뽽�����Ŭ����:HC","50","chk","petit"))
MD_brand.push(new Array("����ĵ����:LC","53","chk","petit","thumnail", "goal"))
MD_brand.push(new Array("Ű��Ʈ��:KE","54","chk","petit","goal"))
MD_brand.push(new Array("������:KG","55","chk","petit"))
MD_brand.push(new Array("�Ϳ���:WO","56","chk","family","http://www.wowcup.co.kr/","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("����ƽ�����:AS","58","chk","petit"))
MD_brand.push(new Array("����Ʈ:CT","60","chk","petit", "flkorea", "goal"))
MD_brand.push(new Array("������:AB","61","chk","petit"))
MD_brand.push(new Array("��Ŭ����:BK","62","chk","petit","flkorea","thumnail", "goal"))
MD_brand.push(new Array("���̷���:HL","63","chk","petit"))
MD_brand.push(new Array("��������:EP","64","chk","family","http://www.elprairie.co.kr/","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("���ϻ���Ŭ:PC","546","chk","petit"))
MD_brand.push(new Array("������:MA","594","chk","petit"))
MD_brand.push(new Array("���½���:LS","595","chk","petit"))
MD_brand.push(new Array("�����۷���:KL","599","chk","petit","store"))
MD_brand.push(new Array("�����:LH","626","chk","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("����:MY","690","chk","family","http://www.moyuum.co.kr/","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("���̽�Ʈ:DE","691","chk","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("���÷���:BP","736","chk","petit","goal"))
MD_brand.push(new Array("��������:EC","755","chk","family","http://www.ecleve.co.kr/","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("Ų���������:KW","789","chk","petit","thumnail", "goal"))
MD_brand.push(new Array("��������:SP","888","chk","family","http://www.sagepole.co.kr/","petit","store", "flkorea","thumnail","mkt", "goal"))
MD_brand.push(new Array("����Ʈ����ũ:ST","922","chk","family","http://www.smartrike.kr","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("�̳뺣�̺�:IB","932","chk","petit", "goal"))
MD_brand.push(new Array("�����α�:LF","1015","chk","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("���۸�����:SL","1062","chk","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("��Ʋ���:LI","1080","chk","petit","goal"))
MD_brand.push(new Array("�����̺��̺�:OB","1112","chk","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("�����:EA","1121","chk","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("Ű��:KI","1133","chk","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("���۽����̺�:HT","1157","chk","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("ǻ���:PD","1245","chk","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("�÷��̾ذ�:PG","1183","chk","petit"))
MD_brand.push(new Array("������:GA","1216","chk","petit","goal"))
MD_brand.push(new Array("Ȧ��:HO","1256","chk","family","http://www.hollekorea.co.kr","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("���̿��޶�:BM","1257","chk","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("����365:IM","1291","chk","petit","store","thumnail","mkt", "goal"))
MD_brand.push(new Array("����̺�:HN","1301","chk","petit","store","thumnail","mkt", "goal"))

MD_brand.push(new Array("�ڶ쿤��:PE","41","mkt"))



//������ �귣��
MD_brand.push(new Array("���̳ʹٿ�:EB","821","chk","essen"))
MD_brand.push(new Array("��Ÿ�����׶���:BT","822","chk","essen"))
MD_brand.push(new Array("���˸޸�ũ:MK","823","chk","essen","thumnail"))
MD_brand.push(new Array("��Ŀ��&��Ŀ��:MM","824","chk","essen","thumnail"))
MD_brand.push(new Array("���۸���:DG","857","chk","essen","thumnail"))
MD_brand.push(new Array("���̽��ط��̽�:ML","863","chk","essen"))
MD_brand.push(new Array("Ŭ�����:CB","899","chk","essen","thumnail"))
MD_brand.push(new Array("���뵵��:DD","913","chk","essen","thumnail"))
MD_brand.push(new Array("���ʱ�:SG","976","chk","essen","thumnail"))
MD_brand.push(new Array("���̵�������:IE","1017","chk","essen"))
MD_brand.push(new Array("����̿�:CM","1018","chk","essen"))
MD_brand.push(new Array("��Ʋ�����:WT","923"))



MD_brand.push(new Array("���̺���:BZ","1051","chk","essen","store","thumnail"))


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

//������ �귣��
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



//�˾�â �ڵ� ���� ����(���� ����Ʈ URL�� �Է��Ͻʽÿ�.)
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
Site_popup.push(new Array("amoretto","36"))		//�˾�


//--�ڶ쿤�� �����PC ���ο��� ������� �귣�� ����(�α��)-�ڻ��� ��û ����
partnerBrandArray=new Array()
//partnerBrandArray.push(new Array("���۸���","857"))
//partnerBrandArray.push(new Array("��Ŀ���ظ�Ŀ��","824"))
//partnerBrandArray.push(new Array("Ŭ�����","899"))
//partnerBrandArray.push(new Array("���˸޸�ũ","823"))
//partnerBrandArray.push(new Array("���뵵��","913"))
//partnerBrandArray.push(new Array("���̽��ط��̽�","863"))
//partnerBrandArray.push(new Array("���ʱ�","976"))
//partnerBrandArray.push(new Array("���̺���","1051"))
//--�ڶ쿤�� �����PC ���ο��� ������� �귣�� ����(�α��)-�ڻ��� ��û ����



Product_status = new Array()
Product_status.push(new Array("<span style='color: red'>[��]</span>","1","chk"))
Product_status.push(new Array("<span style='color: blue'>[ǰ]</span>","2","chk"))
Product_status.push(new Array("<span style='color: green'>[��]</span>","3","chk"))
Product_status.push(new Array("[��]","4","chk"))
Product_status.push(new Array("<span style='color: red'>[�ϴ�]</span>","5"))
Product_status.push(new Array("<span style='color: blue'>[��ǰ]</span>","6"))
Product_status.push(new Array("<span style='color: green'>[�Ͽ�]</span>","7"))
Product_status.push(new Array("[����]","8"))


//�������� ����
off_region = new Array()
off_region.push(new Array("����","5"))
off_region.push(new Array("���","10"))
//off_region.push(new Array("��õ","15")) //2020-06-19 ������
//off_region.push(new Array("����","20")) //2017-10-31 ������
//off_region.push(new Array("�뱸","23")) //2017-09-12 ������, 2018-03-29 �ٽ� ���, 2020-06-19 ������
//off_region.push(new Array("���","25")) //2020-06-19 ������
//off_region.push(new Array("�λ�","30")) //2020-06-19 ������
//off_region.push(new Array("����","35")) //2020-06-19 ������
//off_region.push(new Array("����","40")) //2020-06-19 ������
//off_region.push(new Array("�泲","50")) //2020-06-19 ������
off_region.push(new Array("�ֿ��Ǹ�ó","0"))

off_type = new Array()
off_type.push(new Array("���� ���� ��ȭ��","1"))
off_type.push(new Array("������Ʈ","2"))
off_type.push(new Array("���� ���� �����","3"))
off_type.push(new Array("�鼼��","4"))

part_reason = new Array()
part_reason.push(new Array("�޹���ü", "1"))
part_reason.push(new Array("������ü", "2"))
part_reason.push(new Array("���", "3"))
part_reason.push(new Array("����", "4"))
part_reason.push(new Array("��Ÿ", "5"))



MD_id = new Array()
MD_id.push(new Array("��¦��¦ ����", "1"))
MD_id.push(new Array("�������� �ش�", "2"))
MD_id.push(new Array("������ �ٶ�", "3"))
MD_id.push(new Array("���۸��� ����", "4"))
MD_id.push(new Array("���ٻ��� �޴�", "5"))
MD_id.push(new Array("�뷡�ϴ� �ξ���","6"))
MD_id.push(new Array("���� ���� ���","7"))
MD_id.push(new Array("���� ���� ��","8"))
MD_id.push(new Array("�ܹ߸Ӹ� ���Ƹ�","9"))
MD_id.push(new Array("�β������� �ش�","10"))
MD_id.push(new Array("��� ���� �ھ˶�","11"))
MD_id.push(new Array("��ø�� �ڳ���","12"))

act_buying_source = new Array()
act_buying_source.push(new Array("�ڶ쿤�������", "1"))
act_buying_source.push(new Array("�¶��θ�(�ڶ쿤�� ��)", "2"))
act_buying_source.push(new Array("��ȭ��", "3"))
act_buying_source.push(new Array("�ε弥", "4"))
act_buying_source.push(new Array("�ڶ�ȸ", "5"))
act_buying_source.push(new Array("��Ÿ", "6"))


week_name = new Array()
week_name.push(new Array("��", "2"))
week_name.push(new Array("ȭ", "3"))
week_name.push(new Array("��", "4"))
week_name.push(new Array("��", "5"))
week_name.push(new Array("��", "6"))