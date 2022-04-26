function Node(id, pid, name, url, title, target, open) {
	this.id = id;
	this.pid = pid;
	this.name = name;
	this.url = url;
	this.title = title;
	this.target = target;
	this._io = open || false;
	this._is = open || false;
	this._ls = false;
	this._hc = false;
	this._ai = 0;
	this._p;
};

function accordion(objName) {
	this.config = {
		target					: null,
		folderLinks			: false,
		useSelection		: true,
		useCookies			: true,
		useStatusText		: false,
		inOrder					: false
	}
	this.obj = objName;
	this.aNodes = [];
	this.aIndent = [];
	this.root = new Node("");
	this.selectedNode = null;
	this.selectedFound = false;
	this.completed = false;
};

accordion.prototype.add = function(id, pid, name, url, title, target, open) {
	this.aNodes[this.aNodes.length] = new Node(id, pid, name, url, title, target, open);
};

accordion.prototype.toString = function() {
	var str = '<div class="' + this.obj + '-group"  data-behavior="accordion">\n';
	if (document.getElementById) {
		if (this.config.useCookies) this.selectedNode = this.getSelected();
		str += this.addNode(this.root);
	} else str += 'Browser not supported.';
	str += '</div>';
	str += '</div>';
	if (!this.selectedFound) this.selectedNode = null;
	this.completed = true;
	return str;
};

accordion.prototype.addNode = function(pNode) {
	var str = '';
	var n=0;
	if (this.config.inOrder) n = pNode._ai;
	for (n; n<this.aNodes.length; n++) {
		if (this.aNodes[n].pid == pNode.id) {
			var cn = this.aNodes[n];
			cn._p = pNode;
			cn._ai = n;
			this.setCS(cn);
			if (!cn.target && this.config.target) cn.target = this.config.target;
			if (cn._hc && !cn._io && this.config.useCookies) cn._io = this.isOpen(n);
			if (!this.config.folderLinks && cn._hc) cn.url = null;
			if (this.config.useSelection && n == this.selectedNode && !this.selectedFound) {
				cn._is = true;
				this.selectedNode = n;
				this.selectedFound = true;
			}
			str += this.node(cn, n);
			if (cn._ls) break;
		}
	}
	return str;
};

accordion.prototype.node = function(node, nodeId) {
	var str = '';
	if (node.url) {
		str += '<a id="s' + this.obj + nodeId + '" class="' + ((this.config.useSelection) ? ((node._is ? 'nodeSel' : 'node')) : 'node') + '" href="' + node.url + '"';
		if (node.title) str += ' title="' + node.title + '"';
		if (node.target) str += ' target="' + node.target + '"';
		if (this.config.useStatusText) str += ' onmouseover="window.status=\'' + node.name + '\';return true;" onmouseout="window.status=\'\';return true;" ';
		if (this.config.useSelection && ((node._hc && this.config.folderLinks) || !node._hc))
			str += ' onclick="javascript: ' + this.obj + '.s(' + nodeId + ');"';

		str += '><div id="w' + this.obj + nodeId + '" class="accordion-link' + ((this.config.useSelection) ? ((node._is && $(location).attr('pathname') != "/views/left_menu/list"? ' menuSelected' : '')) : '') + '">' + node.name + '</div></a>';
	}

	if ((!this.config.folderLinks || !node.url) && node._hc)
		str += '<p class="accordion-header ' + ((node._io) ? 'open' : '') + '" onclick="javascript: ' + this.obj + '.o(' + nodeId + ');">' + node.name + '</p>';
	if (node._hc) {
		str += '<div class="accordion-body" id="d' + this.obj + nodeId + '" style="display:' + ((node._io) ? 'block' : 'none') + ';">';
		str += '<div class="' + this.obj + '-group" data-behavior="accordion" data-multiple="true">';
		str += this.addNode(node);
		str += '</div>';
		str += '</div>';
	}
	this.aIndent.pop();
	return str;
};

accordion.prototype.setCS = function(node) {
	var lastId;
	for (var n=0; n<this.aNodes.length; n++) {
		if (this.aNodes[n].pid == node.id) node._hc = true;
		if (this.aNodes[n].pid == node.pid) lastId = this.aNodes[n].id;
	}
	if (lastId==node.id) node._ls = true;
};

accordion.prototype.getSelected = function() {
	if($(location).attr('pathname') == "/views/index") return null;
	for (var i = 0; i < this.aNodes.length; i++){
		if(String($(location).attr('href')+"^").indexOf(String(this.aNodes[i].url+"^")) != -1){
			return i;
		}
	}

	/* url이 없는 view등의 페이지 일 경우 Cookie에 저장된 url을 통해 메뉴 선택 */
	for (var i = 0; i < this.aNodes.length; i++){
		if(String(this.getCookie('url' + this.obj)+"^").indexOf(String(this.aNodes[i].url+"^")) != -1){
			return i;
		}
	}
};

accordion.prototype.s = function(id) {
	if (!this.config.useSelection) return;
	var cn = this.aNodes[id];
	if (cn._hc && !this.config.folderLinks) return;
};

accordion.prototype.o = function(id) {
	var cn = this.aNodes[id];
	this.nodeStatus(!cn._io, id, cn._ls);
	cn._io = !cn._io;
};

accordion.prototype.nodeStatus = function(status, id, bottom) {
	eDiv	= document.getElementById('d' + this.obj + id);
	eDiv.style.display = (status) ? 'block': 'none';
};

accordion.prototype.setCookie = function(cookieName, cookieValue, expires, path, domain, secure) {
	document.cookie =
			cookieName + '=' + cookieValue
			+ (expires ? '; expires=' + expires.toGMTString() : '')
			+ (path ? '; path=' + path : '')
			+ (domain ? '; domain=' + domain : '')
			+ (secure ? '; secure' : '');
};

accordion.prototype.getCookie = function(cookieName) {
	cookieName += "=";
	var arr = decodeURIComponent(document.cookie).split(';');
	for (var i = 0; i < arr.length; i++) {
		var c = arr[i];
		while (c.charAt(0) == ' ') c = c.substring(1);
		if (c.indexOf(cookieName) == 0) return c.substring(cookieName.length, c.length);
	}
	return "";
};

accordion.prototype.isOpen = function(id) {
	if($(location).attr('pathname') == "/views/index") return false;
	for (var i = 0; i < this.aNodes.length; i++){
		if(String($(location).attr('href')+"^").indexOf(String(this.aNodes[i].url+"^")) != -1){
			if (this.selectedNode != id) {
				if (this.config.useCookies) this.setCookie('url' + this.obj, this.aNodes[i].url, '', "/");
			}
			return parentOpen(id, this.aNodes, this.aNodes[i].pid);
		}
	}

	/* url이 없는 view등의 페이지 일 경우 Cookie에 저장된 url을 통해 메뉴 선택 */
	for (var i = 0; i < this.aNodes.length; i++){
		if(String(this.getCookie('url' + this.obj)+"^").indexOf(String(this.aNodes[i].url+"^")) != -1){
			return parentOpen(id, this.aNodes, this.aNodes[i].pid);
		}
	}
};

if (!Array.prototype.push) {
	Array.prototype.push = function array_push() {
		for(var i=0;i<arguments.length;i++)
			this[this.length]=arguments[i];
		return this.length;
	}
};

if (!Array.prototype.pop) {
	Array.prototype.pop = function array_pop() {
		lastElement = this[this.length-1];
		this.length = Math.max(this.length-1,0);
		return lastElement;
	}
};

(function ($, window, document, undefined) {
	//enable strict mode
	"use strict";
	var pluginName = 'simpleAccordion',
			defaults = {
				multiple: false,
				speedOpen: 300,
				speedClose: 150,
				easingOpen: null,
				easingClose: null,
				headClass: 'accordion-header',
				bodyClass: 'accordion-body',
				openClass: 'open',
				cbClose: null, //function (e, $this) {},
				cbOpen: null //function (e, $this) {}
			};

	// plugin constructor
	function Accordion(element, options) {
		this.$el = $(element);
		this.options = $.extend({}, defaults, options);
		this._defaults = defaults;
		this._name = pluginName;
		if (typeof this.$el.data('multiple') !== 'undefined') {
			this.options.multiple = this.$el.data('multiple');
		} else {
			this.options.multiple = this._defaults.multiple;
		}
		this.init();
	}

	Accordion.prototype = {
		init: function () {
			var o = this.options, $headings = this.$el.children('.' + o.headClass);
			$headings.on('click', {_t:this}, this.headingClick);
		},
		headingClick: function (e) {
			var $this = $(this),
					_t = e.data._t,
					o = _t.options,
					$headings = _t.$el.children('.' + o.headClass);

			if (!$this.hasClass(o.openClass)) {
				$this.addClass(o.openClass).next('.' + o.bodyClass).slideDown(o.speedOpen, o.easingOpen, function () {
					if ($.isFunction(o.cbOpen)) {
						o.cbOpen(e, $this);
					}
				});
			} else {
				$this.removeClass(o.openClass).next('.' + o.bodyClass).slideUp(o.speedClose, o.easingClose, function () {
					if ($.isFunction(o.cbClose)) {
						o.cbClose(e, $this);
					}
				});
			}

		}
	};

	$.fn[pluginName] = function (options) {
		return this.each(function () {
			if (!$.data(this, 'plugin_' + pluginName)) {
				$.data(this, 'plugin_' + pluginName,
						new Accordion(this, options));
			}
		});
	};

}(jQuery, window, document));

$(document).ready(function() {
	$('[data-behavior=accordion]').simpleAccordion({cbOpen:accOpen, cbClose:accClose});
});

function accClose(e, $this) {
	$this.find('p').fadeIn(200);
}

function accOpen(e, $this) {
	$this.find('p').fadeOut(200)
}

function parentOpen(id, nodes, pid){
	for (var j = 0; j < nodes.length ; j++){
		if(nodes[j].id == pid){
			if(j == id){
				return true;
				break;
			}
			if(nodes[j].pid != 0){
				return parentOpen(id, nodes, nodes[j].pid);
			}
		}
	}
}