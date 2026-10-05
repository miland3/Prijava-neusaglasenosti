(() => {
  'use strict';
  const $ = (id) => document.getElementById(id);
  const photos = []; // { blob, url }

  // --- Segmentirani izbori (jedan od više) ---
  document.querySelectorAll('.seg').forEach((group) => {
    group.querySelectorAll('.seg-btn').forEach((btn) => {
      btn.addEventListener('click', () => {
        group.querySelectorAll('.seg-btn').forEach((b) => b.classList.remove('active', 'invalid'));
        btn.classList.add('active');
        const v = btn.dataset.val;
        group.dataset.value = v;
        if (group.id === 'greska') $('dobavljac-wrap').classList.toggle('hidden', v !== 'EKSTERNA');
        if (group.id === 'uoceno') $('mjerenje-wrap').classList.toggle('hidden', v !== 'MJERENJE');
      });
    });
  });

  // --- Uzroci (više izbora) ---
  document.querySelectorAll('#uzrok .pill').forEach((p) => {
    p.addEventListener('click', () => {
      p.classList.toggle('active');
      const ostalo = document.querySelector('#uzrok .pill[data-val="Ostalo"]');
      $('ostalo-wrap').classList.toggle('hidden', !ostalo.classList.contains('active'));
    });
  });

  // --- Slike: smanji na telefonu prije slanja ---
  $('photoBtnCam').addEventListener('click', () => $('fotoCam').click());
  $('photoBtnGal').addEventListener('click', () => $('fotoInput').click());
  $('fotoCam').addEventListener('change', handleFiles);
  $('fotoInput').addEventListener('change', handleFiles);

  async function handleFiles(e) {
    const files = Array.from(e.target.files || []).filter((f) => f.type.startsWith('image/'));
    for (const f of files) {
      const blob = await downscale(f, 1500, 0.75);
      photos.push({ blob, url: URL.createObjectURL(blob) });
    }
    e.target.value = '';
    renderThumbs();
  }

  function downscale(file, maxDim, quality) {
    return new Promise((resolve) => {
      const img = new Image();
      img.onload = () => {
        let w = img.width, h = img.height;
        const k = Math.min(1, maxDim / Math.max(w, h));
        w = Math.round(w * k); h = Math.round(h * k);
        const c = document.createElement('canvas');
        c.width = w; c.height = h;
        c.getContext('2d').drawImage(img, 0, 0, w, h);
        c.toBlob((b) => resolve(b || file), 'image/jpeg', quality);
        URL.revokeObjectURL(img.src);
      };
      img.onerror = () => resolve(file);
      img.src = URL.createObjectURL(file);
    });
  }

  function renderThumbs() {
    const box = $('thumbs');
    box.innerHTML = '';
    photos.forEach((p, i) => {
      const d = document.createElement('div');
      d.className = 'thumb';
      d.innerHTML = '<img src="' + p.url + '" alt=""><div class="rm">×</div>';
      d.querySelector('.rm').addEventListener('click', () => {
        URL.revokeObjectURL(p.url);
        photos.splice(i, 1);
        renderThumbs();
      });
      box.appendChild(d);
    });
  }

  // --- Ime se pamti na ovom uređaju ---
  try { const ime = localStorage.getItem('prijavaIme'); if (ime) $('prijavio').value = ime; } catch (e) {}
  $('prijavio').addEventListener('input', () => {
    try { localStorage.setItem('prijavaIme', $('prijavio').value.trim()); } catch (e) {}
  });

  // --- Sakupi podatke iz forme u objekat koji API očekuje (PrijavaRequest) ---
  const val = (id) => ($(id).value || '').trim();
  const num = (id) => (val(id) === '' ? 1 : parseInt(val(id), 10));

  function getData() {
    const proizvod = val('proizvod');
    const vrstaGreske = $('greska').dataset.value;
    const opis = val('opis');
    $('proizvod').classList.toggle('invalid', !proizvod);
    $('opis').classList.toggle('invalid', !opis);
    if (!vrstaGreske) $('greska').querySelectorAll('.seg-btn').forEach((b) => b.classList.add('invalid'));
    if (!proizvod || !vrstaGreske || !opis) {
      pokaziGresku('Popuni obavezna polja: Proizvod, Vrsta greške i Opis.');
      return null;
    }
    const uzroci = Array.from(document.querySelectorAll('#uzrok .pill.active')).map((p) => {
      const v = p.dataset.val;
      return v === 'Ostalo' && val('ostaloTekst') ? 'Ostalo: ' + val('ostaloTekst') : v;
    });
    return {
      proizvod,
      vrstaGreske,
      dobavljac: val('dobavljac'),
      kolicinaSerija: num('qSerija'),
      kolicinaProvjereno: num('qProvjereno'),
      kolicinaNeispravno: num('qNeispravno'),
      uoceno: $('uoceno').dataset.value,
      izmjereneVrijednosti: val('vrijednosti'),
      opis,
      uzroci,
      ponavljajuca: $('ponavljanje').dataset.value === 'da',
      uradjeno: val('uradjeno'),
      prijavio: val('prijavio')
    };
  }

  function pokaziGresku(txt) {
    $('errMsg').textContent = txt;
    $('errMsg').classList.remove('hidden');
    $('errMsg').scrollIntoView({ behavior: 'smooth', block: 'center' });
  }

  // --- Slanje: 1) POST JSON  2) POST slike na /{id}/slike ---
  const btn = $('btnPosalji');
  btn.addEventListener('click', async () => {
    $('errMsg').classList.add('hidden');
    const data = getData();
    if (!data) return;
    btn.disabled = true;
    btn.textContent = 'Šaljem...';
    try {
      const nova = await Api.kreiraj(data);
      if (photos.length) await Api.dodajSlike(nova.id, photos.map((p) => p.blob));
      $('okMsg').textContent = '✓ Prijava #' + nova.id + ' je sačuvana' +
        (photos.length ? ' (' + photos.length + ' sl.)' : '') + '.';
      $('okCard').classList.remove('hidden');
      resetForm();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    } catch (e) {
      pokaziGresku('Greška pri slanju: ' + e.message);
    } finally {
      btn.disabled = false;
      btn.textContent = 'Pošalji prijavu';
    }
  });

  function resetForm() {
    ['proizvod', 'dobavljac', 'vrijednosti', 'opis', 'ostaloTekst', 'uradjeno'].forEach((id) => { $(id).value = ''; });
    ['qSerija', 'qProvjereno', 'qNeispravno'].forEach((id) => { $(id).value = '1'; });
    document.querySelectorAll('#uzrok .pill.active').forEach((p) => p.classList.remove('active'));
    $('ostalo-wrap').classList.add('hidden');
    $('greska').dataset.value = '';
    $('greska').querySelectorAll('.seg-btn').forEach((b) => b.classList.remove('active'));
    $('dobavljac-wrap').classList.add('hidden');
    photos.splice(0).forEach((p) => URL.revokeObjectURL(p.url));
    renderThumbs();
  }
})();
