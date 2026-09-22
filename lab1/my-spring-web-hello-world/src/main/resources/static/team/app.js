(function () {
	'use strict';

	var reduced = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

	/* menu overlay */
	var menu = document.querySelector('.menu');
	var openBtn = document.querySelector('.menu-toggle');
	var closeBtn = document.querySelector('.menu__close');

	function setMenu(open) {
		menu.classList.toggle('is-open', open);
		document.body.style.overflow = open ? 'hidden' : '';
	}

	if (menu && openBtn && closeBtn) {
		openBtn.addEventListener('click', function () { setMenu(true); });
		closeBtn.addEventListener('click', function () { setMenu(false); });
		document.addEventListener('keydown', function (e) {
			if (e.key === 'Escape') { setMenu(false); }
		});
	}

	/* split text into words for staggered reveal */
	document.querySelectorAll('[data-reveal]').forEach(function (el) {
		var words = el.textContent.trim().split(/\s+/);
		el.textContent = '';
		words.forEach(function (word, i) {
			var span = document.createElement('span');
			span.className = 'reveal-word';
			span.textContent = word;
			span.style.transitionDelay = (i * 45) + 'ms';
			el.appendChild(span);
			el.appendChild(document.createTextNode(' '));
		});
	});

	var pending = [].slice.call(document.querySelectorAll('.reveal-word, [data-rise], .service'));

	function activate(el) {
		el.classList.add(el.classList.contains('service') ? 'is-filled' : 'is-in');
	}

	function revealVisible() {
		var limit = window.innerHeight * 0.92;
		pending = pending.filter(function (el) {
			var rect = el.getBoundingClientRect();
			if (rect.top < limit && rect.bottom > 0) {
				activate(el);
				return false;
			}
			return true;
		});
	}

	if (reduced) {
		pending.forEach(activate);
	} else {
		var lastCheck = 0;
		var onScroll = function () {
			var now = Date.now();
			if (now - lastCheck < 80) { return; }
			lastCheck = now;
			revealVisible();
		};

		revealVisible();
		window.addEventListener('scroll', onScroll, { passive: true });
		window.addEventListener('resize', onScroll);
		window.addEventListener('load', revealVisible);
	}

	/* header colour depends on the section behind it */
	var header = document.querySelector('.site-header');
	var darkZones = document.querySelectorAll('[data-dark]');

	function syncHeader() {
		var y = header.getBoundingClientRect().bottom;
		var onDark = false;
		darkZones.forEach(function (zone) {
			var r = zone.getBoundingClientRect();
			if (r.top <= y && r.bottom >= y) { onDark = true; }
		});
		header.classList.toggle('on-dark', onDark);
	}

	if (header && darkZones.length) {
		syncHeader();
		window.addEventListener('scroll', syncHeader, { passive: true });
		window.addEventListener('resize', syncHeader);
	}
})();
