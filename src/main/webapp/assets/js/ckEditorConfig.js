/**
 * @license Copyright (c) 2003-2019, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see https://ckeditor.com/legal/ckeditor-oss-license
 */

CKEDITOR.editorConfig = function (config) {
	// 에디터에 사용할 기능들 정의
	config.toolbar = [
		['Font', 'FontSize'],
		['JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock'],
		['TextColor', 'BGColor'], ['Bold', 'Italic', 'Underline', 'Strike'],
		['Link'],
		['Image', 'Table', 'HorizontalRule'],
		{
			name: 'clipboard',
			items: ['PasteFromWord']
		},
		{ name: 'tools', items : [ 'Maximize'] }
		];

	config.language = 'ko';
	config.height = 550;
	config.font_defaultLabel = '굴림';
	config.font_names = '맑은 고딕/Malgun Gothic;굴림/Gulim;돋움/Dotum;바탕/Batang;궁서/Gungsuh';
	config.fontSize_defaultLabel = '12px';
	config.fontSize_sizes = '8/8px;9/9px;10/10px;11/11px;12/12px;14/14px;16/16px;18/18px;20/20px;22/22px;24/24px;26/26px;28/28px;36/36px;48/48px;';
	config.enterMode = CKEDITOR.ENTER_BR;					// 엔터키를 <br> 로 적용함.
	config.shiftEnterMode = CKEDITOR.ENTER_P;			// 쉬프트 + 엔터를 <p> 로 적용함.
	config.fillEmptyBlocks = false;
	config.autoParagraph = false;
	config.allowedContent = true;
	config.pasteFilter = null;										//외부 복사내용 그대로 값을 가져옴
	config.removePlugins = 'resize';
	config.resize_enabled = false;								// 에디터 크기를 조절하지 않음
	config.removePlugins = 'elementspath, image'; 					// DOM 출력하지 않음
};

CKEDITOR.dtd.$removeEmpty['i'] = false;

CKEDITOR.on('dialogDefinition', function (ev) {
			var dialogName = ev.data.name;
			var dialog = ev.data.definition.dialog;
			var dialogDefinition = ev.data.definition;
			if (dialogName == 'image2') {
				dialog.on('show', function (obj) {
					this.selectPage('Upload'); //업로드텝으로 시작
				});
				dialogDefinition.removeContents('advanced'); // 자세히탭 제거
				dialogDefinition.removeContents('Link'); // 링크탭 제거

				// ckeditor 설치 폴더에서 plugins/image/dialogs/image.js 이곳에서 해당 앨리먼트 확인
				var infoTab = dialogDefinition.getContents('info');  //info탭을 제거하면 이미지 업로드가 안된다.
				infoTab.remove('txtHSpace');
				infoTab.remove('txtVSpace');
				infoTab.remove('txtBorder');
				infoTab.remove('ratioLock');
				infoTab.remove('cmbAlign');
			}
		}
);

CKEDITOR.on('instanceReady', function (ev) {
	ev.editor.dataProcessor.writer.setRules('p', {
		indent: false,
		breakBeforeOpen: false,
		breakAfterOpen: false,
		breakBeforeClose: false,
		breakAfterClose: false
	});

	ev.editor.dataProcessor.writer.setRules('br', {
		indent: false,
		breakBeforeOpen: false,
		breakAfterOpen: false,
		breakBeforeClose: false,
		breakAfterClose: false
	});

	ev.editor.dataProcessor.writer.setRules('div', {
		indent: false,
		breakBeforeOpen: false,
		breakAfterOpen: false,
		breakBeforeClose: false,
		breakAfterClose: false
	});
});
