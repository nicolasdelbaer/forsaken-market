(function initEmbers() {
  const container = document.getElementById('ember-container');
  if (!container) return;
  function spawnEmber() {
    const el = document.createElement('div');
    el.className = 'ember';
    const dur = (4 + Math.random() * 6) * 4;
    el.style.cssText = `left:${Math.random()*100}%;width:${Math.random()<0.3?3:2}px;background:${Math.random()<0.2?'#e8c86a':'#c4a052'};animation-duration:${dur}s;animation-delay:-${Math.random()*dur}s;--drift:${(Math.random()-0.5)*240}px`;
    container.appendChild(el);
    setTimeout(() => el.remove(), (dur + 1) * 1000);
  }
  setInterval(spawnEmber, 220);
  for (let i = 0; i < 18; i++) spawnEmber();
})();
