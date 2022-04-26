let VSBoxCounter = function () {
	let count = 0;
	let instances = [];
	return {
		set: function (instancePtr) {
			instances.push({ offset: ++count, ptr: instancePtr });
			return instances[instances.length - 1].offset;
		},
		remove: function (instanceNr) {
			let temp = instances.filter(function (x) {
				return x.offset != instanceNr;
			})
			instances = temp.splice(0);
		}
	};
}();

function selectBox(domSelector) {
	let self = this;
	this.instanceOffset = VSBoxCounter.set(self);
	this.domSelector = domSelector;
	this.root = document.querySelector(domSelector);
	this.rootToken = null;
	this.main;
	this.button;
	this.title;
	this.isMultiple = this.root.hasAttribute("multiple");
	this.multipleSize = this.isMultiple && this.root.hasAttribute("size") ? parseInt(this.root.getAttribute("size")) : -1;
	this.isOptgroups = false;
	this.currentOptgroup = 0;
	this.drop;
	this.top;
	this.left;
	this.options;
	this.listElements;
	this.isDisabled = false;
	this.maxOptionWidth = Infinity;
	this.maxSelect = Infinity;
	this.forbidenAttributes = ["class", "selected", "disabled", "data-text", "data-value", "style"];
	this.forbidenClasses = ["active", "disabled"];
	this.userOptions = {buttonItemsSeparator : ", ", translations: { "item": "item","items": "items", "selectAll": "Select All", "clearAll": "Clear All" }, placeHolder: "Nothing Selected..."}

	this.init = function () { this.createTree(); }

	this.createTree = function () {

		this.rootToken = self.domSelector.replace(/[^A-Za-z0-9]+/, "")
		this.root.style.display = "none";
		let already = document.getElementById("btn-group-" + this.rootToken);
		if (already) {
			already.remove();
		}
		this.main = document.createElement("div");
		this.root.parentNode.insertBefore(this.main, this.root.nextSibling);
		this.main.classList.add("vsb-main");
		this.main.setAttribute("id", "btn-group-" + this.rootToken);
		this.main.style.marginLeft = this.main.style.marginLeft;
		this.button = document.createElement("button");
		this.main.appendChild(this.button);
		this.title = document.createElement("span");
		this.button.appendChild(this.title);
		this.title.classList.add("title");
		let caret = document.createElement("span");
		this.button.appendChild(caret);
		caret.classList.add("caret");
		this.drop = document.createElement("div");
		this.main.appendChild(this.drop);
		this.drop.classList.add("vsb-menu");
		this.drop.style.zIndex = 2000 - this.instanceOffset;
		this.ul = document.createElement("ul");
		this.drop.appendChild(this.ul);
		if (this.isMultiple) {
			this.ul.classList.add("multi");
			if (!self.userOptions.disableSelectAll) {
				let selectAll = document.createElement("option");
				selectAll.setAttribute("value", 'all');
				selectAll.innerText = self.userOptions.translations.selectAll;
				this.root.insertBefore(selectAll, (this.root.hasChildNodes())
					? this.root.childNodes[0]
					: null);
			}
		}
		let selectedTexts = ""
		let sep = "";
		let nrActives = 0;

		this.options = document.querySelectorAll(this.domSelector + " > option");
		Array.prototype.slice.call(this.options).forEach(function (x) {
			let text = x.textContent;
			let value = x.value;
			let originalAttrs;
			if (x.hasAttributes()) {
				originalAttrs = Array.prototype.slice.call(x.attributes)
				.filter(function (a) {
					return self.forbidenAttributes.indexOf(a.name) === -1
				});
			}
			let classes = x.getAttribute("class");
			if (classes) {
				classes = classes
				.split(" ")
				.filter(function (c) {
					return self.forbidenClasses.indexOf(c) === -1
				});
			} else {
				classes = [];
			}
			let li = document.createElement("li");
			let isSelected = x.hasAttribute("selected");
			let isDisabled = x.hasAttribute("disabled");

			self.ul.appendChild(li);
			li.setAttribute("data-value", value);
			li.setAttribute("data-text", text);

			if (originalAttrs !== undefined) {
				originalAttrs.forEach(function (a) {
					li.setAttribute(a.name, a.value);
				});
			}

			classes.forEach(function (x) {
				li.classList.add(x);
			});

			if (self.maxOptionWidth < Infinity) {
				li.classList.add("short");
				li.style.maxWidth = self.maxOptionWidth + "px";
			}

			if (isSelected) {
				nrActives++;
				selectedTexts += sep + text;
				sep = self.userOptions.buttonItemsSeparator;
				li.classList.add("active");
				if (!self.isMultiple) {
					self.title.textContent = text;
					if (classes.length != 0) {
						classes.forEach(function (x) {
							self.title.classList.add(x);
						});
					}
				}
			}
			if (isDisabled) {
				li.classList.add("disabled");
			}
			li.appendChild(document.createTextNode(" " + text));
		});

		if (document.querySelector(self.domSelector + ' optgroup') !== null) {
			self.isOptgroups = true;
			self.options = document.querySelectorAll(self.domSelector + " option");
			let groups = document.querySelectorAll(self.domSelector + ' optgroup');
			Array.prototype.slice.call(groups).forEach(function (group) {
				let groupOptions = group.querySelectorAll('option');
				let li = document.createElement("li");
				let span = document.createElement("span");
				let iCheck = document.createElement("i");
				let labelElement = document.createElement("b");
				let dataWay = group.getAttribute("data-way");
				if (!dataWay) dataWay = "closed";
				if (!dataWay || (dataWay !== "closed" && dataWay !== "open")) dataWay = "closed";
				li.appendChild(span);
				li.appendChild(iCheck);
				self.ul.appendChild(li);
				li.classList.add('grouped-option');
				li.classList.add(dataWay);
				self.currentOptgroup++;
				let optId = self.rootToken + "-opt-" + self.currentOptgroup;
				li.id = optId;
				li.appendChild(labelElement);
				labelElement.appendChild(document.createTextNode(group.label));
				li.setAttribute("data-text", group.label);
				self.ul.appendChild(li);

				Array.prototype.slice.call(groupOptions).forEach(function (x) {
					let text = x.textContent;
					let value = x.value;
					let classes = x.getAttribute("class");
					if (classes) {
						classes = classes.split(" ");
					}
					else {
						classes = [];
					}
					classes.push(dataWay);
					let li = document.createElement("li");
					let isSelected = x.hasAttribute("selected");
					self.ul.appendChild(li);
					li.setAttribute("data-value", value);
					li.setAttribute("data-text", text);
					li.setAttribute("data-parent", optId);
					if (classes.length != 0) {
						classes.forEach(function (x) {
							li.classList.add(x);
						});
					}
					if (isSelected) {
						nrActives++;
						selectedTexts += sep + text;
						sep = self.userOptions.buttonItemsSeparator;
						li.classList.add("active");
						if (!self.isMultiple) {
							self.title.textContent = text;
							if (classes.length != 0) {
								classes.forEach(function (x) {
									self.title.classList.add(x);
								});
							}
						}
					}
					li.appendChild(document.createTextNode(text));
				})
			})
		}

		let optionsLength = self.options.length - Number(!self.userOptions.disableSelectAll);

		if (optionsLength == nrActives) { // Bastoune idea to preserve the placeholder
			let wordForAll = self.userOptions.translations.all;
			selectedTexts = wordForAll;
		} else if (self.multipleSize != -1) {
			if (nrActives > self.multipleSize) {
				let wordForItems = nrActives === 1 ? self.userOptions.translations.item : self.userOptions.translations.items;
				selectedTexts = nrActives + " " + wordForItems;
			}
		}
		if (self.isMultiple) {
			self.title.innerHTML = selectedTexts;
		}
		if (self.userOptions.placeHolder != "" && self.title.textContent == "") {
			self.title.textContent = self.userOptions.placeHolder;
		}
		self.listElements = self.drop.querySelectorAll("li:not(.grouped-option)");

		this.main.addEventListener("click", function (e) {
			if (self.isDisabled) return;
			self.drop.style.visibility = "visible";
			document.addEventListener("click", docListener);
			e.preventDefault();
			e.stopPropagation();
		});

		this.drop.addEventListener("click", function (e) {
			if (self.isDisabled) return;
			if (e.target.tagName === 'INPUT') return;
			let isShowHideCommand = e.target.tagName === 'SPAN';
			let isCheckCommand = e.target.tagName === 'I';
			let liClicked = e.target.parentElement;
			if (!liClicked.hasAttribute("data-value")) {
				if (liClicked.classList.contains("grouped-option")) {
					if (!isShowHideCommand && !isCheckCommand) return;
					let oldClass, newClass;
					if (isCheckCommand) { // check or uncheck children
						self.checkUncheckFromParent(liClicked);
					} else { //open or close
						if (liClicked.classList.contains("open")) {
							oldClass = "open"
							newClass = "closed"
						} else {
							oldClass = "closed"
							newClass = "open"
						}
						liClicked.classList.remove(oldClass);
						liClicked.classList.add(newClass);
						let theChildren = self.drop.querySelectorAll("[data-parent='" + liClicked.id + "']");
						theChildren.forEach(function (x) {
							x.classList.remove(oldClass);
							x.classList.add(newClass);
						})
					}
					return;
				}
			}
			let choiceValue = e.target.getAttribute("data-value");
			let choiceText = e.target.getAttribute("data-text");
			let className = e.target.getAttribute("class");

			if (className && className.indexOf("disabled") != -1) {
				return;
			}

			if (className && className.indexOf("overflow") != -1) {
				return;
			}

			if (choiceValue === 'all') {
				if (e.target.hasAttribute('data-selected')
					&& e.target.getAttribute('data-selected') === 'true') {
					self.setValue('none')
				} else {
					self.setValue('all');
				}
				return;
			}

			if (!self.isMultiple) {
				self.root.value = choiceValue;
				self.title.textContent = choiceText;
				if (className) {
					self.title.setAttribute("class", className + " title");
				} else {
					self.title.setAttribute("class", "title");
				}
				Array.prototype.slice.call(self.listElements).forEach(function (x) {
					x.classList.remove("active");
				});
				if (choiceText != "") {
					e.target.classList.add("active");
				}
				docListener();
			} else {
				let wasActive = false;
				if (className) {
					wasActive = className.indexOf("active") != -1;
				}
				if (wasActive) {
					e.target.classList.remove("active");
				} else {
					e.target.classList.add("active");
				}
				if (e.target.hasAttribute("data-parent")) {
					self.checkUncheckFromChild(e.target);
				}

				let selectedTexts = ""
				let sep = "";
				let nrActives = 0;
				let nrAll = 0;
				for (let i = 0; i < self.options.length; i++) {
					nrAll++;
					if (self.options[i].value == choiceValue) {
						self.options[i].selected = !wasActive;
					}
					if (self.options[i].selected) {
						nrActives++;
						selectedTexts += sep + self.options[i].textContent;
						sep = self.userOptions.buttonItemsSeparator;
					}
				}
				if (nrAll == nrActives - Number(!self.userOptions.disableSelectAll)) {
					let wordForAll = self.userOptions.translations.all;
					selectedTexts = wordForAll;
				} else if (self.multipleSize != -1) {
					if (nrActives > self.multipleSize) {
						let wordForItems = nrActives === 1 ? self.userOptions.translations.item : self.userOptions.translations.items;
						selectedTexts = nrActives + " " + wordForItems;
					}
				}
				self.title.textContent = selectedTexts;
				self.checkSelectMax(nrActives);
				self.checkUncheckAll();
			}
			e.preventDefault();
			e.stopPropagation();
			if (self.userOptions.placeHolder != "" && self.title.textContent == "") {
				self.title.textContent = self.userOptions.placeHolder;
			}
		});
		function docListener() {
			document.removeEventListener("click", docListener);
			self.drop.style.visibility = "hidden";
		}
	}
	this.init();
	this.checkUncheckAll();
}


selectBox.prototype.checkSelectMax = function (nrActives) {
	let self = this;
	if (self.maxSelect == Infinity || !self.isMultiple) return;
	if (self.maxSelect <= nrActives) {
		Array.prototype.slice.call(self.listElements).forEach(function (x) {
			if (x.hasAttribute('data-value')) {
				if (!x.classList.contains('disabled') && !x.classList.contains('active')) {
					x.classList.add("overflow");
				}
			}
		});
	} else {
		Array.prototype.slice.call(self.listElements).forEach(function (x) {
			if (x.classList.contains('overflow')) {
				x.classList.remove("overflow");
			}
		});
	}
}

selectBox.prototype.checkUncheckFromChild = function (liClicked) {
	let self = this;
	let parentId = liClicked.getAttribute('data-parent');
	let parentLi = document.getElementById(parentId);
	if (!self.isMultiple) return;
	let listElements = self.drop.querySelectorAll("li");
	let childrenElements = Array.prototype.slice.call(listElements).filter(function (el) {
		return el.hasAttribute("data-parent") && el.getAttribute('data-parent') == parentId ;
	});
	let nrChecked = 0;
	let nrCheckable = childrenElements.length;
	if (nrCheckable == 0) return;
	childrenElements.forEach(function (el) {
		if (el.classList.contains('active')) nrChecked++;
	});
	if (nrChecked === nrCheckable || nrChecked === 0) {
		if (nrChecked === 0) {
			parentLi.classList.remove("checked");
		} else {
			parentLi.classList.add("checked");
		}
	} else {
		parentLi.classList.remove("checked");
	}
}

selectBox.prototype.checkUncheckFromParent = function (liClicked) {
	let self = this;
	let parentId = liClicked.id;
	if (!self.isMultiple) return;
	let listElements = self.drop.querySelectorAll("li");
	let childrenElements = Array.prototype.slice.call(listElements).filter(function (el) {
		return el.hasAttribute("data-parent") && el.getAttribute('data-parent') == parentId;
	});
	let nrChecked = 0;
	let nrCheckable = childrenElements.length;
	if (nrCheckable == 0) return;
	childrenElements.forEach(function (el) {
		if (el.classList.contains('active')) nrChecked++;
	});
	if (nrChecked === nrCheckable || nrChecked === 0) {
		//check all or uncheckAll : just do the opposite
		childrenElements.forEach(function (el) {
			var event = document.createEvent('HTMLEvents');
			event.initEvent('click', true, false);
			el.dispatchEvent(event);
		});
		if (nrChecked === 0) {
			liClicked.classList.add("checked");
		} else {
			liClicked.classList.remove("checked");
		}
	} else {
		//check all
		liClicked.classList.remove("checked");
		childrenElements.forEach(function (el) {
			if (!el.classList.contains('active')) {
				var event = document.createEvent('HTMLEvents');
				event.initEvent('click', true, false);
				el.dispatchEvent(event);
			}
		});
	}
}

selectBox.prototype.checkUncheckAll = function () {
	let self = this, nrChecked = 0, nrCheckable = 0, checkAllElement = null;
	if (self.listElements == null) return;  if (!self.isMultiple) return;
	Array.prototype.slice.call(self.listElements).forEach(function (x) {
		if (x.hasAttribute('data-value')) {
			if (x.getAttribute('data-value') === 'all') { checkAllElement = x; }
			if (x.getAttribute('data-value') !== 'all') {
				nrCheckable++;
				nrChecked += x.classList.contains('active');
			}
		}
	});

	if (checkAllElement) {
		if (nrChecked === nrCheckable) {
			checkAllElement.classList.add("active");
			checkAllElement.innerText = self.userOptions.translations.clearAll;
			checkAllElement.setAttribute('data-selected', 'true')
		} else if (nrChecked === 0) {
			self.title.textContent = self.userOptions.placeHolder;
			checkAllElement.classList.remove("active");
			checkAllElement.innerText = self.userOptions.translations.selectAll;
			checkAllElement.setAttribute('data-selected', 'false')
		}
	}
}

selectBox.prototype.setValue = function (values) {
	let self = this, listElements = self.drop.querySelectorAll("li");
	if (values == null || values == undefined || values == "") { self.empty();
	} else {
		if (selectBox_type(values) == "string") {
			if (values === "all") {
				values = [];
				Array.prototype.slice.call(listElements).forEach(function (x) {
					if (x.hasAttribute('data-value')) {
						let value = x.getAttribute('data-value');
						if (value !== 'all') { values.push(x.getAttribute('data-value')); }else{ x.classList.add("active"); }
					}
				});
			} else { values = values.split(","); }
		}
		let foundValues = [];
		if (selectBox_type(values) == "array") {
			Array.prototype.slice.call(self.options).forEach(function (x) {	if (values.indexOf(x.value) !== -1) { x.selected = true; foundValues.push(x.value); } else { x.selected = false; }});
			let selectedTexts = "", sep = "", nrActives = 0, nrAll = 0;
			Array.prototype.slice.call(listElements).forEach(function (x) {
				nrAll++;
				if (foundValues.indexOf(x.getAttribute("data-value")) != -1) {
					x.classList.add("active");
					nrActives++;
					selectedTexts += sep + x.getAttribute("data-text");
					sep = self.userOptions.buttonItemsSeparator;
				} else { x.classList.remove("active"); }
			});
			if (self.multipleSize != -1) {
				if (nrActives > self.multipleSize) {
					let wordForItems = nrActives === 1 ? self.userOptions.translations.item : self.userOptions.translations.items;
					selectedTexts = nrActives + " " + wordForItems;
				}
			}
			self.title.textContent = selectedTexts;
		}
		self.checkUncheckAll();
	}
}

selectBox.prototype.disable = function () {
	let already = document.getElementById("btn-group-" + this.rootToken);
	if (already) {
		button = already.querySelector("button")
		if (button) button.classList.add("disabled");
		this.isDisabled = true;
	}
}
selectBox.prototype.enable = function () {
	let already = document.getElementById("btn-group-" + this.rootToken);
	if (already) {
		button = already.querySelector("button")
		if (button) button.classList.remove("disabled");
		this.isDisabled = false;
	}
}

function selectBox_type(target) {
	const computedType = Object.prototype.toString.call(target);
	const stripped = computedType.replace("[object ", "").replace("]", "");
	const lowercased = stripped.toLowerCase();
	return lowercased;
}
