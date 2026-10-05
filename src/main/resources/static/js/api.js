// Tanak sloj nad fetch() — jedino mjesto koje zna URL-ove API-ja.
const Api = (() => {
  const BASE = '/api/prijave';

  async function request(url, options = {}) {
    const res = await fetch(url, options);
    if (!res.ok) {
      let poruka = res.status + ' ' + res.statusText;
      try { const body = await res.json(); if (body.detail) poruka = body.detail; } catch (e) {}
      throw new Error(poruka);
    }
    return res.status === 204 ? null : res.json();
  }

  function json(method, body) {
    return { method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body) };
  }

  return {
    lista: (status) => request(BASE + (status ? '?status=' + encodeURIComponent(status) : '')),
    jedna: (id) => request(BASE + '/' + id),
    kreiraj: (prijava) => request(BASE, json('POST', prijava)),
    status: (id, status) => request(BASE + '/' + id + '/status', json('PATCH', { status })),
    obrisi: (id) => request(BASE + '/' + id, { method: 'DELETE' }),
    dodajSlike: (id, blobs) => {
      const fd = new FormData();
      blobs.forEach((b, i) => fd.append('slike', b, 'slika-' + (i + 1) + '.jpg'));
      return request(BASE + '/' + id + '/slike', { method: 'POST', body: fd });
    }
  };
})();
