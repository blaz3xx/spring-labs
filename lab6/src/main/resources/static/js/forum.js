const isTyping = (el) => el instanceof HTMLInputElement || el instanceof HTMLTextAreaElement || el instanceof HTMLSelectElement;

const relativeTime = (date) => {
    const seconds = Math.round((Date.now() - date.getTime()) / 1000);
    if (seconds < 45) return 'щойно';
    const minutes = Math.round(seconds / 60);
    if (minutes < 60) return `${minutes} хв тому`;
    const hours = Math.round(minutes / 60);
    if (hours < 24) return `${hours} год тому`;
    const days = Math.round(hours / 24);
    if (days === 1) return 'вчора';
    if (days < 7) return `${days} дн. тому`;
    return null;
};

const times = document.querySelectorAll('time[datetime]');
times.forEach((el) => { el.title = el.textContent; });
const refreshTimes = () => times.forEach((el) => {
    const date = new Date(el.getAttribute('datetime').replace(' ', 'T'));
    const text = Number.isNaN(date.getTime()) ? null : relativeTime(date);
    el.textContent = text ?? el.title;
});
refreshTimes();
setInterval(refreshTimes, 60_000);

const AUTHOR_KEY = 'forum.author';
document.querySelectorAll('input[name=author][data-remember]').forEach((input) => {
    try {
        if (!input.value) input.value = localStorage.getItem(AUTHOR_KEY) || '';
    } catch (e) {}
    input.form.addEventListener('submit', () => {
        try { localStorage.setItem(AUTHOR_KEY, input.value); } catch (e) {}
    });
});

document.querySelectorAll('.card').forEach((card, i) => card.style.setProperty('--i', Math.min(i, 12)));

const composer = document.getElementById('composer');
const newButtons = document.querySelectorAll('[data-new]');
const setComposer = (open) => {
    if (!composer) return;
    composer.hidden = !open;
    newButtons.forEach((button) => button.setAttribute('aria-expanded', String(open)));
    if (open) {
        composer.scrollIntoView({ block: 'center' });
        composer.querySelector('input[name=title]').focus({ preventScroll: true });
    }
};
if (composer) {
    newButtons.forEach((button) => button.addEventListener('click', () => setComposer(composer.hidden)));
    document.getElementById('cancel-new').addEventListener('click', () => setComposer(false));
}

const search = document.getElementById('q');
const entries = () => Array.from(document.querySelectorAll('.card')).filter((el) => !el.hidden);

let current = -1;
const select = (index) => {
    const list = entries();
    list.forEach((el) => el.classList.remove('current'));
    current = Math.max(-1, Math.min(index, list.length - 1));
    if (current >= 0) {
        list[current].classList.add('current');
        list[current].scrollIntoView({ block: 'nearest' });
    }
};

const closeMenus = (except) => document.querySelectorAll('.menu[open]').forEach((menu) => {
    if (menu !== except) menu.removeAttribute('open');
});
document.addEventListener('click', (e) => closeMenus(e.target.closest('.menu')));

const SHORTCUT_KEYS = { KeyJ: 'j', KeyK: 'k', KeyN: 'n', Slash: '/' };

document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
        closeMenus();
        if (composer && !composer.hidden) setComposer(false);
        if (document.activeElement === search) search.blur();
        return;
    }
    if (isTyping(e.target) || e.ctrlKey || e.metaKey || e.altKey) return;

    const key = SHORTCUT_KEYS[e.code] ?? e.key;
    if (key === '/' && search) {
        e.preventDefault();
        search.focus();
    } else if (key === 'n' && composer) {
        e.preventDefault();
        setComposer(true);
    } else if (key === 'j') {
        select(current + 1);
    } else if (key === 'k') {
        select(current < 0 ? 0 : current - 1);
    } else if (key === 'Enter' && current >= 0) {
        window.location.href = entries()[current].querySelector('.card-title').href;
    }
});

const replyText = document.getElementById('reply-text');
const replyCounter = document.getElementById('reply-counter');
if (replyText) {
    const max = Number(replyText.getAttribute('maxlength'));
    const update = () => {
        replyCounter.textContent = `${replyText.value.length} / ${max}`;
        replyCounter.classList.toggle('near', replyText.value.length > max * 0.9);
    };
    replyText.addEventListener('input', update);
    replyText.addEventListener('keydown', (e) => {
        if (e.key === 'Enter' && (e.ctrlKey || e.metaKey)) {
            e.preventDefault();
            replyText.form.requestSubmit();
        }
    });
    update();
}
