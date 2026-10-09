// avatar colour from name
document.querySelectorAll('.ava[data-name]').forEach(function (el) {
    var n = el.dataset.name || '?', h = 0;
    for (var i = 0; i < n.length; i++) h = (h * 31 + n.charCodeAt(i)) % 360;
    el.style.setProperty('--h', h);
    el.textContent = n.trim().charAt(0);
});

// new-topic composer toggle
var toggle = document.getElementById('toggle-new');
var composer = document.getElementById('composer');
if (toggle && composer) {
    var set = function (open) {
        composer.classList.toggle('open', open);
        if (open) composer.querySelector('input').focus();
    };
    toggle.addEventListener('click', function () { set(!composer.classList.contains('open')); });
    document.getElementById('cancel-new').addEventListener('click', function () { set(false); });
}

// topic search
var q = document.getElementById('q');
if (q) {
    q.addEventListener('input', function () {
        var s = q.value.toLowerCase();
        document.querySelectorAll('.row-topic').forEach(function (r) {
            r.style.display = r.textContent.toLowerCase().indexOf(s) === -1 ? 'none' : '';
        });
    });
}

// remember author name between forms
var KEY = 'forum.author';
document.querySelectorAll('input[name=author][data-remember]').forEach(function (i) {
    try { if (!i.value) i.value = localStorage.getItem(KEY) || ''; } catch (e) {}
    i.form.addEventListener('submit', function () {
        try { localStorage.setItem(KEY, i.value); } catch (e) {}
    });
});

// close open kebab menu when clicking elsewhere
document.addEventListener('click', function (e) {
    document.querySelectorAll('.menu[open]').forEach(function (m) {
        if (!m.contains(e.target)) m.removeAttribute('open');
    });
});
