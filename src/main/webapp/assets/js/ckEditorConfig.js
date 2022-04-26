/**
 * @license Copyright (c) 2003-2019, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see https://ckeditor.com/legal/ckeditor-oss-license
 */

CKEDITOR.editorConfig = function (config) {
	// �����Ϳ� ����� ��ɵ� ����
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
	config.font_defaultLabel = '����';
	config.font_names = '���� ���/Malgun Gothic;����/Gulim;����/Dotum;����/Batang;�ü�/Gungsuh';
	config.fontSize_defaultLabel = '12px';
	config.fontSize_sizes = '8/8px;9/9px;10/10px;11/11px;12/12px;14/14px;16/16px;18/18px;20/20px;22/22px;24/24px;26/26px;28/28px;36/36px;48/48px;';
	config.enterMode = CKEDITOR.ENTER_BR;					// ����Ű�� <br> �� ������.
	config.shiftEnterMode = CKEDITOR.ENTER_P;			// ����Ʈ + ���͸� <p> �� ������.
	config.fillEmptyBlocks = false;
	config.autoParagraph = false;
	config.allowedContent = true;
	config.pasteFilter = null;										//�ܺ� ���系�� �״�� ���� ������
	config.removePlugins = 'resize';
	config.resize_enabled = false;								// ������ ũ�⸦ �������� ����
	config.removePlugins = 'elementspath, image'; 					// DOM ������� ����
};

CKEDITOR.dtd.$removeEmpty['i'] = false;

CKEDITOR.on('dialogDefinition', function (ev) {
			var dialogName = ev.data.name;
			var dialog = ev.data.definition.dialog;
			var dialogDefinition = ev.data.definition;
			if (dialogName == 'image2') {
				dialog.on('show', function (obj) {
					this.selectPage('Upload'); //���ε������� ����
				});
				dialogDefinition.removeContents('advanced'); // �ڼ����� ����
				dialogDefinition.removeContents('Link'); // ��ũ�� ����

				// ckeditor ��ġ �������� plugins/image/dialogs/image.js �̰����� �ش� �ٸ���Ʈ Ȯ��
				var infoTab = dialogDefinition.getContents('info');  //info���� �����ϸ� �̹��� ���ε尡 �ȵȴ�.
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
