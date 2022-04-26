FullCalendar.globalLocales.push(function () {
  'use strict';
  var ko = {
    code: "ko",
    buttonText: {
      prev: "이전달",
      next: "다음달",
      today: "오늘",
      month: "월",
      week: "주",
      day: "일",
      list: "일정목록"
    },
    weekText: "주",
    allDayText: "종일",
    moreLinkText: "개",
    noEventsText: "일정이 없습니다"
  };
  return ko;
}());





//기본 스크립트, CSS들 로딩
var fullcalObj = {

  timeZone: 'local',                                          //처리 시간 형태는 2020-10-11 13:56:55.
  initialView: 'dayGridMonth',                                //처음 로딩시 보여줄 캘린더 형태. dayGridMonth 달력, timeGridWeek 주스케쥴, timeGridDay 일 스케쥴, listMonth... 월 리스트 형태, listWeek 주리스트 형태...
  expandRows: true,                                           //달력에 게시글이 많으면 더보기가 생기는데 생기게 하지 않고 다 표현함.
  nowIndicator: true,                                         //TimeGridWeek, day에서 현재 시간 표시
  headerToolbar: {
    left: '',                                                 //달력 왼쪽에 보여줄 뷰 형태 선택자. dayGridMonth, timeGridWeek.....등등 initialView에 형태를 ,로 추가 하면 여러개 형태의 달력 선택 가능.
    center: 'prev,title,next',                                //pre : 이전달(주,일) title: 현재달(주,일), next : 다음달(주,일)           -보여줄 필요가 없으면. 빈칸으로 두면 됨.
    right: 'today'                                            //오늘로 돌아가기.
  },
  views: {                                                    //각 뷰에 따른 타이틀 형식
    dayGrid: {titleFormat: { year: 'numeric', month: 'long'}},
    timeGrid: {titleFormat: { year: 'numeric', month: 'long', day: '2-digit' }}
  },

  height: 'auto',                                             //height : 'auto' 사용시 내용에 맞춰 자동으로 높이 조절, TimeGridWeek면 auto사용시 문제 있음(nowHeight()로 아래에 자동 셋팅함)
  dayMaxEvents: true,                                         //게시글 제한없이 여러개 표시

  hiddenDays : [0,6],                                         //요일 숨기기 0:일요일, 6:토요일

  businessHours: true,                                        //스케쥴에서 근무시간만 색깔 활성화
  businessHours: {startTime: '08:00', endTime: '20:00'},      //근무시간 셋팅
  slotMinTime: '08:00',                                       //스케쥴에 표식될 시작시간~ 종료시간
  slotMaxTime: '20:00',

  locale: 'ko',                                               //언어 ko.js 존재함.
  eventOrder: 'groupId, start,-duration,allDay,id, title',        //달력에 표시될 순서

  displayEventTime: true,                                     //달력에 시간표시.

  events: {                                                   //Json 형태로 데이터 불러올시. column을 title, start, end로 생성하면 바로 사용. 가공이 필요하면 eventDataTransform 사용
    url: 'calendar.run',
    method: 'POST',
    timeZoneParam: 'local',

    /*extraParams: {                                          //기본 star, end 파라미터 전송함. 추가로 필요한 파라미터가 있을시 아래 extraParams 추가하면 됨.
      gubun: document.form1.gubun.value,
      ceo_gubun: document.form1.ceo_gubun.value
    },*/

    failure: function () {
      ("정보를 불러오는데 실패하였습니다.")
    },
    success: function (content, xhr) {
      return content.data;
    }
  }


  // tootip 사용시 해당 페이지에 cal_tooltip(obj) 함수 생성하고 받은 데이터로 해당 내용 생성하여 retun해주면 자동으로 툴팁생성
  ,eventMouseEnter : function(info) {
    if(info.event.id  !=0 && typeof(fullcal_tooltip)=='function'){             //컨텐츠가 있을시에만. // 툴팁을 사용할 함수가 있을시.
        var obj = JSON.parse(JSON.stringify(info.event));
        var tis = info.el;
        var tooltip = '<div class="tooltipevent" style="top:' + ($(
                tis).offset().top - 5) + 'px;left:' + ($(tis).offset().left
            + ($(tis).width()) / 2) + 'px">';
        tooltip += fullcal_tooltip($.extend(true, obj, info.event.extendedProps));      //기본 이벤트 값과 그외 이벤트 값이 별도로 되어 있는걸 하나의 Object로 생성
        tooltip += '</div>';
        var $tooltip = $(tooltip).appendTo('body');
    }
  }

  ,eventMouseLeave : function(info) {        // 툴팁 사용시 감추기.
    $(info.el).css('z-index', 8);
    $('.tooltipevent').remove();
  }

  ,dateClick : function(arg) {              //날짜 또는 시간대 클릭시. arg.dateStr =현재 찍은 날짜 또는 시간....
      if (typeof(fullcal_click)=='function') fullcal_click(arg)
  }

  ,eventClick : function(arg) {             //등록된 글 클릭시.  arg.셋팅된 data값을 해당 등록글의 정보로 불러옴. 기본이벤트값과 추가 이벤트 값을 하나로 생성
    var obj = JSON.parse(JSON.stringify(arg.event));
    if (typeof(fullcal_click)=='function') fullcal_click($.extend(true, obj, arg.event.extendedProps));
  }
}

