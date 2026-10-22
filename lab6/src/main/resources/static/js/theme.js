(() => {
    let theme = null;
    try { theme = localStorage.getItem('forum.theme'); } catch (e) {}
    if (theme !== 'light' && theme !== 'dark') {
        theme = window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
    }
    document.documentElement.dataset.theme = theme;
})();
