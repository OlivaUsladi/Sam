// ===== THEME TOGGLE =====
(function () {
  const toggleBtn = document.querySelector('[data-theme-toggle]');
  const root = document.documentElement;

  // Respect system preference as starting point
  let theme = matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
  // Default to dark for this app
  theme = 'dark';
  root.setAttribute('data-theme', theme);

  function setIcon(t) {
    if (!toggleBtn) return;
    toggleBtn.innerHTML = t === 'dark'
      ? '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="5"/><path d="M12 1v2M12 21v2M4.22 4.22l1.42 1.42M18.36 18.36l1.42 1.42M1 12h2M21 12h2M4.22 19.78l1.42-1.42M18.36 5.64l1.42-1.42"/></svg>'
      : '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/></svg>';
    toggleBtn.setAttribute('aria-label', 'Переключить тему (' + (t === 'dark' ? 'тёмная' : 'светлая') + ')');
  }
  setIcon(theme);

  if (toggleBtn) {
    toggleBtn.addEventListener('click', () => {
      theme = theme === 'dark' ? 'light' : 'dark';
      root.setAttribute('data-theme', theme);
      setIcon(theme);
    });
  }
})();

// ===== HEADER SCROLL =====
(function () {
  const header = document.getElementById('header');
  if (!header) return;
  let lastY = 0;
  window.addEventListener('scroll', () => {
    const y = window.scrollY;
    if (y > 20) {
      header.classList.add('header--scrolled');
    } else {
      header.classList.remove('header--scrolled');
    }
    lastY = y;
  }, { passive: true });
})();

// ===== MOBILE MENU =====
(function () {
  const burger = document.getElementById('burger');
  const menu = document.getElementById('mobileMenu');
  if (!burger || !menu) return;

  burger.addEventListener('click', () => {
    const open = burger.classList.toggle('open');
    menu.classList.toggle('open');
    burger.setAttribute('aria-expanded', String(open));
    menu.setAttribute('aria-hidden', String(!open));
  });

  // Close on link click
  menu.querySelectorAll('a').forEach(link => {
    link.addEventListener('click', () => {
      burger.classList.remove('open');
      menu.classList.remove('open');
      burger.setAttribute('aria-expanded', 'false');
      menu.setAttribute('aria-hidden', 'true');
    });
  });
})();

// ===== SCREENS CAROUSEL =====
(function () {
  const track = document.getElementById('screensTrack');
  const dots = document.querySelectorAll('.dot');
  if (!track || !dots.length) return;

  const scrollWrap = track.parentElement;
  const cards = track.querySelectorAll('.screen-card');
  const prevBtn = document.getElementById('screensPrev');
  const nextBtn = document.getElementById('screensNext');
  const AUTOPLAY_MS = 4000;
  const RESUME_MS = 12000;
  const reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

  function getActive() {
    const wrapRect = scrollWrap.getBoundingClientRect();
    const wrapCenter = wrapRect.left + wrapRect.width / 2;
    let closest = 0;
    let minDist = Infinity;
    cards.forEach((card, i) => {
      const rect = card.getBoundingClientRect();
      const cardCenter = rect.left + rect.width / 2;
      const dist = Math.abs(cardCenter - wrapCenter);
      if (dist < minDist) { minDist = dist; closest = i; }
    });
    return closest;
  }

  function updateDots(idx) {
    dots.forEach((d, i) => {
      d.classList.toggle('dot--active', i === idx);
      d.setAttribute('aria-selected', String(i === idx));
    });
  }

  function scrollToCard(i, smooth) {
    const card = cards[i];
    if (!card) return;
    const wrapRect = scrollWrap.getBoundingClientRect();
    const cardRect = card.getBoundingClientRect();
    const offset = cardRect.left - wrapRect.left - (wrapRect.width - cardRect.width) / 2;
    scrollWrap.scrollBy({ left: offset, behavior: smooth === false ? 'auto' : 'smooth' });
  }

  function step(dir) {
    const next = (getActive() + dir + cards.length) % cards.length;
    scrollToCard(next);
  }

  // Autoplay: slowly cycles through screens while the carousel is visible;
  // pauses when the user hovers, touches or scrolls it manually.
  let timer = null;
  let resumeTimer = null;
  let visible = false;
  let hovered = false;

  function start() {
    if (reducedMotion || timer || !visible || hovered) return;
    timer = setInterval(() => step(1), AUTOPLAY_MS);
  }
  function stop() {
    if (timer) { clearInterval(timer); timer = null; }
  }
  function pauseForUser() {
    stop();
    clearTimeout(resumeTimer);
    resumeTimer = setTimeout(start, RESUME_MS);
  }

  if (window.IntersectionObserver) {
    const io = new IntersectionObserver((entries) => {
      visible = entries.some(e => e.isIntersecting);
      if (visible) start(); else stop();
    }, { threshold: 0.35 });
    io.observe(scrollWrap);
  } else {
    visible = true;
    start();
  }

  scrollWrap.addEventListener('mouseenter', () => { hovered = true; stop(); });
  scrollWrap.addEventListener('mouseleave', () => { hovered = false; start(); });
  scrollWrap.addEventListener('touchstart', pauseForUser, { passive: true });
  scrollWrap.addEventListener('wheel', pauseForUser, { passive: true });
  document.addEventListener('visibilitychange', () => {
    if (document.hidden) stop(); else start();
  });

  scrollWrap.addEventListener('scroll', () => {
    updateDots(getActive());
  }, { passive: true });

  dots.forEach((dot, i) => {
    dot.addEventListener('click', () => { pauseForUser(); scrollToCard(i); });
  });
  if (prevBtn) prevBtn.addEventListener('click', () => { pauseForUser(); step(-1); });
  if (nextBtn) nextBtn.addEventListener('click', () => { pauseForUser(); step(1); });

  updateDots(getActive());

  scrollWrap.setAttribute('tabindex', '0');
  scrollWrap.addEventListener('keydown', (e) => {
    if (e.key === 'ArrowLeft') { e.preventDefault(); pauseForUser(); step(-1); }
    if (e.key === 'ArrowRight') { e.preventDefault(); pauseForUser(); step(1); }
  });
})();

// ===== SCROLL REVEAL =====
(function () {
  if (!window.IntersectionObserver) return;
  const elements = document.querySelectorAll(
    '.feature-card, .screen-card, .stat, .about__content, .about__visual, .section-header'
  );
  elements.forEach(el => el.classList.add('reveal'));

  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('visible');
        observer.unobserve(entry.target);
      }
    });
  }, { threshold: 0.1, rootMargin: '0px 0px -40px 0px' });

  elements.forEach(el => observer.observe(el));
})();

// ===== SMOOTH ANCHOR SCROLL =====
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
  anchor.addEventListener('click', function (e) {
    const href = this.getAttribute('href');
    if (href === '#') return;
    const target = document.querySelector(href);
    if (target) {
      e.preventDefault();
      target.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  });
});

// ===== DEMO: RUTUBE VIDEO =====
(function () {
  const box = document.getElementById('demoVideo');
  const linkWrap = document.getElementById('demoVideoLink');
  if (!box) return;

  const raw = (window.SAM_RUTUBE_URL || '').trim();
  if (!raw) return;

  // Принимаем: полную ссылку rutube.ru/video/<id>/, ссылку rutube.ru/play/embed/<id> или просто <id>
  const match = raw.match(/([0-9a-f]{32})/i);
  if (!match) return;
  const id = match[1];

  const iframe = document.createElement('iframe');
  iframe.src = 'https://rutube.ru/play/embed/' + id;
  iframe.title = 'Видеообзор приложения Sam';
  iframe.setAttribute('allow', 'clipboard-write; autoplay');
  iframe.setAttribute('allowfullscreen', '');
  iframe.setAttribute('loading', 'lazy');
  iframe.setAttribute('frameborder', '0');
  box.innerHTML = '';
  box.appendChild(iframe);
  box.classList.add('demo__video--ready');

  if (linkWrap) {
    const a = linkWrap.querySelector('a');
    if (a) a.href = 'https://rutube.ru/video/' + id + '/';
    linkWrap.hidden = false;
  }
})();
