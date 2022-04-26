FullCalendar.globalLocales.push(function () {
  'use strict';
  var ko = {
    code: "ko",
    buttonText: {
      prev: "������",
      next: "������",
      today: "����",
      month: "��",
      week: "��",
      day: "��",
      list: "�������"
    },
    weekText: "��",
    allDayText: "����",
    moreLinkText: "��",
    noEventsText: "������ �����ϴ�"
  };
  return ko;
}());





//�⺻ ��ũ��Ʈ, CSS�� �ε�
var fullcalObj = {

  timeZone: 'local',                                          //ó�� �ð� ���´� 2020-10-11 13:56:55.
  initialView: 'dayGridMonth',                                //ó�� �ε��� ������ Ķ���� ����. dayGridMonth �޷�, timeGridWeek �ֽ�����, timeGridDay �� ������, listMonth... �� ����Ʈ ����, listWeek �ָ���Ʈ ����...
  expandRows: true,                                           //�޷¿� �Խñ��� ������ �����Ⱑ ����µ� ����� ���� �ʰ� �� ǥ����.
  nowIndicator: true,                                         //TimeGridWeek, day���� ���� �ð� ǥ��
  headerToolbar: {
    left: '',                                                 //�޷� ���ʿ� ������ �� ���� ������. dayGridMonth, timeGridWeek.....��� initialView�� ���¸� ,�� �߰� �ϸ� ������ ������ �޷� ���� ����.
    center: 'prev,title,next',                                //pre : ������(��,��) title: �����(��,��), next : ������(��,��)           -������ �ʿ䰡 ������. ��ĭ���� �θ� ��.
    right: 'today'                                            //���÷� ���ư���.
  },
  views: {                                                    //�� �信 ���� Ÿ��Ʋ ����
    dayGrid: {titleFormat: { year: 'numeric', month: 'long'}},
    timeGrid: {titleFormat: { year: 'numeric', month: 'long', day: '2-digit' }}
  },

  height: 'auto',                                             //height : 'auto' ���� ���뿡 ���� �ڵ����� ���� ����, TimeGridWeek�� auto���� ���� ����(nowHeight()�� �Ʒ��� �ڵ� ������)
  dayMaxEvents: true,                                         //�Խñ� ���Ѿ��� ������ ǥ��

  hiddenDays : [0,6],                                         //���� ����� 0:�Ͽ���, 6:�����

  businessHours: true,                                        //�����쿡�� �ٹ��ð��� ���� Ȱ��ȭ
  businessHours: {startTime: '08:00', endTime: '20:00'},      //�ٹ��ð� ����
  slotMinTime: '08:00',                                       //�����쿡 ǥ�ĵ� ���۽ð�~ ����ð�
  slotMaxTime: '20:00',

  locale: 'ko',                                               //��� ko.js ������.
  eventOrder: 'groupId, start,-duration,allDay,id, title',        //�޷¿� ǥ�õ� ����

  displayEventTime: true,                                     //�޷¿� �ð�ǥ��.

  events: {                                                   //Json ���·� ������ �ҷ��ý�. column�� title, start, end�� �����ϸ� �ٷ� ���. ������ �ʿ��ϸ� eventDataTransform ���
    url: 'calendar.run',
    method: 'POST',
    timeZoneParam: 'local',

    /*extraParams: {                                          //�⺻ star, end �Ķ���� ������. �߰��� �ʿ��� �Ķ���Ͱ� ������ �Ʒ� extraParams �߰��ϸ� ��.
      gubun: document.form1.gubun.value,
      ceo_gubun: document.form1.ceo_gubun.value
    },*/

    failure: function () {
      ("������ �ҷ����µ� �����Ͽ����ϴ�.")
    },
    success: function (content, xhr) {
      return content.data;
    }
  }


  // tootip ���� �ش� �������� cal_tooltip(obj) �Լ� �����ϰ� ���� �����ͷ� �ش� ���� �����Ͽ� retun���ָ� �ڵ����� ��������
  ,eventMouseEnter : function(info) {
    if(info.event.id  !=0 && typeof(fullcal_tooltip)=='function'){             //�������� �����ÿ���. // ������ ����� �Լ��� ������.
        var obj = JSON.parse(JSON.stringify(info.event));
        var tis = info.el;
        var tooltip = '<div class="tooltipevent" style="top:' + ($(
                tis).offset().top - 5) + 'px;left:' + ($(tis).offset().left
            + ($(tis).width()) / 2) + 'px">';
        tooltip += fullcal_tooltip($.extend(true, obj, info.event.extendedProps));      //�⺻ �̺�Ʈ ���� �׿� �̺�Ʈ ���� ������ �Ǿ� �ִ°� �ϳ��� Object�� ����
        tooltip += '</div>';
        var $tooltip = $(tooltip).appendTo('body');
    }
  }

  ,eventMouseLeave : function(info) {        // ���� ���� ���߱�.
    $(info.el).css('z-index', 8);
    $('.tooltipevent').remove();
  }

  ,dateClick : function(arg) {              //��¥ �Ǵ� �ð��� Ŭ����. arg.dateStr =���� ���� ��¥ �Ǵ� �ð�....
      if (typeof(fullcal_click)=='function') fullcal_click(arg)
  }

  ,eventClick : function(arg) {             //��ϵ� �� Ŭ����.  arg.���õ� data���� �ش� ��ϱ��� ������ �ҷ���. �⺻�̺�Ʈ���� �߰� �̺�Ʈ ���� �ϳ��� ����
    var obj = JSON.parse(JSON.stringify(arg.event));
    if (typeof(fullcal_click)=='function') fullcal_click($.extend(true, obj, arg.event.extendedProps));
  }
}

