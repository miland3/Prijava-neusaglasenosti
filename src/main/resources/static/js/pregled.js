(() => {
  'use strict';
  const $ = (id) => document.getElementById(id);
  const esc = (s) => String(s ?? '').replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  const STATUSI = { NOVA: 'Nova', U_OBRADI: 'U obradi', ZATVORENA: 'Zatvorena' };

  $('filter').addEventListener('change', ucitaj);
  $('osvjezi').addEventListener('click', ucitaj);
  ucitaj();

  async function ucitaj() {
    const box = $('lista');
    box.innerHTML = '<p class="meta">Učitavam...</p>';
    try {
      const prijave = await Api.lista($('filter').value);
      box.innerHTML = prijave.length ? '' : '<div class="card meta">Nema prijava.</div>';
      prijave.forEach((p) => box.appendChild(kartica(p)));
    } catch (e) {
      box.innerHTML = '<div class="card err-msg">Greška: ' + esc(e.message) + '</div>';
    }
  }

  function red(naziv, vrijednost) {
    return vrijednost ? '<dt>' + esc(naziv) + '</dt><dd>' + esc(vrijednost) + '</dd>' : '';
  }

  function kartica(p) {
    const el = document.createElement('div');
    el.className = 'card item';
    const datum = new Date(p.kreirano).toLocaleString('bs-BA');
    el.innerHTML =
      '<h3>#' + p.id + ' — ' + esc(p.proizvod) + '</h3>' +
      '<div class="meta">' +
        '<span class="badge ' + p.status + '">' + STATUSI[p.status] + '</span>' +
        '<span class="badge ' + p.vrstaGreske + '">' + (p.vrstaGreske === 'EKSTERNA' ? 'Eksterna' : 'Interna') + '</span>' +
        esc(p.prijavio || 'nepoznat') + ' · ' + esc(datum) +
      '</div>' +
      '<dl>' +
        red('Dobavljač', p.dobavljac) +
        red('Količina', 'serija ' + p.kolicinaSerija + ' / provjereno ' + p.kolicinaProvjereno + ' / neispravno ' + p.kolicinaNeispravno) +
        red('Uočeno', p.uoceno === 'MJERENJE' ? 'mjerenje' : 'vizuelno') +
        red('Izmjereno', p.izmjereneVrijednosti) +
        red('Opis', p.opis) +
        red('Uzrok', p.uzroci.join(', ')) +
        red('Ponavljanje', p.ponavljajuca ? 'da' : 'ne') +
        red('Urađeno/prijedlog', p.uradjeno) +
      '</dl>' +
      '<div class="thumbs">' +
        p.slike.map((u) => '<a class="thumb" href="' + u + '" target="_blank"><img src="' + u + '" alt=""></a>').join('') +
      '</div>' +
      '<div class="actions">' +
        Object.keys(STATUSI).filter((s) => s !== p.status)
          .map((s) => '<button class="btn btn-small btn-primary" data-status="' + s + '">→ ' + STATUSI[s] + '</button>').join('') +
        '<button class="btn btn-small btn-danger" data-obrisi>Obriši</button>' +
      '</div>';

    el.querySelectorAll('[data-status]').forEach((b) => b.addEventListener('click', async () => {
      await akcija(() => Api.status(p.id, b.dataset.status));
    }));
    el.querySelector('[data-obrisi]').addEventListener('click', async () => {
      if (confirm('Obrisati prijavu #' + p.id + '?')) await akcija(() => Api.obrisi(p.id));
    });
    return el;
  }

  async function akcija(fn) {
    try { await fn(); await ucitaj(); } catch (e) { alert('Greška: ' + e.message); }
  }
})();
