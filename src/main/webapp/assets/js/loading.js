"use strict";
!function (a) {
	var b = 300, c = {
		top: function (a) {
			var c = document.createElement("div");
			c.setAttribute("id", "_loading"), c.style.position = "absolute", c.style.width = b + "px", c.style.height = "0px", c.style.top = "50%", c.style.left = "50%", c.style["z-index"] = 99999, c.style["text-align"] = "center";
			for (var d in a)"width" == d && (c.style[d] = a[d] + "px", b = a[d]), "height" == d && (c.style[d] = a[d] + "px");
			var e = document.body.clientWidth, f = (e - b) / 2;
			return c.style.left = f + "px", c
		}
	};
	c["line-pulse"] = function () {
		for (var a = c.top({width: "300", height: "50"}), b = 0; b <= 4; b++) {
			var d = document.createElement("div");
			a.appendChild(d)
		}
		return a.setAttribute("class", "line-pulse"), a
	}, a.showLoading = function () {
		var b = {
			name: "line-pulse", maskClick: !1, callback: function () {
			}
		};
		if (arguments)if ("string" == typeof arguments[0])b.name = arguments[0]; else if ("object" == typeof arguments[0])for (var d in arguments[0])b[d] = arguments[0][d];
		a.hideLoading();
		var e = document.createElement("div");
		e.setAttribute("id", "_mask"), e.style.position = "fixed", e.style.top = "0", e.style.left = "0", e.style.bottom = "0", e.style.right = "0", e.style.overflow = "hidden", e.style["z-index"] = 99998, e.style["background-color"] = "#000", e.style.opacity = 0, e.style.zoom = 1, b.allowHide && e.addEventListener("click", function () {
			a.hideLoading()
		}, !1), a("body").append(e), a("body").append(c[b.name]()), b.callback()
	}, a.hideLoading = function () {
		var b = a("#_mask"), c = a("#_loading");
		"undefined" != typeof b && 0 === b.length && "undefined" != typeof c && 0 === c.length || (b.remove(), c.remove())
	}, a(window).resize(function () {
		var c = a("#_loading");
		if ("object" == typeof c && 0 != c.length) {
			var d = document.body.clientWidth, e = (d - b) / 2;
			c.css("left", e + "px")
		}
	})
}(jQuery || window.jQuery);