/* SPA for Task & Time Tracker. Hash router, no build step. */
const $ = (s, r = document) => r.querySelector(s);
const $$ = (s, r = document) => [...r.querySelectorAll(s)];

function esc(v) {
  return String(v ?? "").replace(/[&<>"']/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]));
}
function toast(msg, type = "") {
  const el = document.createElement("div");
  el.className = "toast " + type;
  el.textContent = msg;
  $("#toasts").appendChild(el);
  setTimeout(() => el.remove(), 4500);
}
const fmtDate = (iso) => (iso ? new Date(iso).toLocaleString("uk-UA") : "—");
const shortId = (id) => (id ? String(id).slice(0, 8) : "—");

async function withErr(fn, okMsg) {
  try {
    const r = await fn();
    if (okMsg) toast(okMsg, "ok");
    return r ?? true; // 204 No Content -> true, щоб колбеки оновлення спрацювали
  } catch (e) {
    toast(e.message, "error");
    return null;
  }
}

const S = { me: null, companies: [], sort: { key: "created_at", dir: "desc" } };

function sortBy(arr, key, dir) {
  const m = dir === "asc" ? 1 : -1;
  return [...arr].sort((a, b) => {
    const x = a[key] ?? "", y = b[key] ?? "";
    return (x < y ? -1 : x > y ? 1 : 0) * m;
  });
}
function sortToolbar(prefix) {
  return `<div class="toolbar"><label class="muted">Сортування:</label>
    <select id="${prefix}-sort-key">
      <option value="created_at">за датою</option>
      <option value="name">за назвою</option>
      <option value="title">за заголовком</option>
    </select>
    <select id="${prefix}-sort-dir"><option value="desc">спадання</option><option value="asc">зростання</option></select>
    <button class="ghost" id="${prefix}-sort-apply">Застосувати</button></div>`;
}
function bindSort(prefix, rerender) {
  const k = $(`#${prefix}-sort-key`), d = $(`#${prefix}-sort-dir`);
  if (k) k.value = S.sort.key; if (d) d.value = S.sort.dir;
  $(`#${prefix}-sort-apply`)?.addEventListener("click", () => {
    S.sort = { key: k.value, dir: d.value };
    rerender();
  });
}

/* ---------- auth ---------- */
const AUTH_TABS = {
  login: {
    hint: "Вхід для зареєстрованих користувачів.",
    fields: [["email", "Email", "email"], ["password", "Пароль", "password"]],
    submit: "Увійти",
    run: async (v) => { const r = await Api.auth.login(v); Api.setToken(r.token); },
  },
  personal: {
    hint: "Personal workspace: твої власні проєкти і тайм-менеджмент. Залишається з тобою назавжди.",
    fields: [["first_name", "Ім'я", "text"], ["last_name", "Прізвище", "text"], ["email", "Email", "email"], ["password", "Пароль (мін. 6)", "password"], ["phone_number", "Телефон", "text"]],
    submit: "Створити personal",
    run: async (v) => { const r = await Api.auth.signupPersonal(v); Api.setToken(r.token); },
  },
  company: {
    hint: "Company workspace: створює нову компанію, ти стаєш її оунером.",
    fields: [["first_name", "Ім'я", "text"], ["last_name", "Прізвище", "text"], ["email", "Email", "email"], ["password", "Пароль (мін. 6)", "password"], ["phone_number", "Телефон", "text"], ["company_name", "Назва компанії", "text"], ["company_description", "Опис компанії", "text"]],
    submit: "Створити компанію",
    run: async (v) => { const r = await Api.auth.signupCompany(v); Api.setToken(r.token); },
  },
  invite: {
    hint: "Маєш код інвайта? Зареєструйся як personal, і тебе одразу додадуть у компанію як юзера.",
    fields: [["invite_code", "Код інвайта", "text"], ["first_name", "Ім'я", "text"], ["last_name", "Прізвище", "text"], ["email", "Email", "email"], ["password", "Пароль (мін. 6)", "password"], ["phone_number", "Телефон", "text"]],
    submit: "Прийняти інвайт",
    run: async (v) => {
      const { invite_code, ...user } = v;
      const r = await Api.auth.signupPersonal(user);
      Api.setToken(r.token);
      const j = await Api.invites.accept(invite_code);
      toast("Тебе додано в компанію!", "ok");
      return j;
    },
  },
};
let authTab = "login";
function renderAuthForm() {
  const t = AUTH_TABS[authTab];
  $("#auth-hint").textContent = t.hint;
  $("#auth-form").innerHTML =
    t.fields.map(([n, l, ty]) => `<label>${esc(l)}<input name="${n}" type="${ty}" required /></label>`).join("") +
    `<label>Backend URL<input name="__base" value="${esc(Api.getBase())}" /></label>
     <button class="btn" type="submit">${esc(t.submit)}</button>`;
}
function initAuth() {
  $$("#auth-tabs .tab").forEach((b) => b.addEventListener("click", () => {
    authTab = b.dataset.tab;
    $$("#auth-tabs .tab").forEach((x) => x.classList.toggle("active", x === b));
    renderAuthForm();
  }));
  renderAuthForm();
  $("#auth-form").addEventListener("submit", async (e) => {
    e.preventDefault();
    const fd = new FormData(e.target);
    Api.setBase(fd.get("__base") || "http://localhost:8080");
    const v = {};
    fd.forEach((val, k) => { if (k !== "__base" && val !== "") v[k] = val; });
    const r = await withErr(() => AUTH_TABS[authTab].run(v));
    if (r !== null) enterApp();
  });
}

/* ---------- shell ---------- */
async function enterApp() {
  if (!Api.token()) return showAuth();
  const me = await withErr(() => Api.users.get(Api.userId()));
  if (!me) { Api.setToken(null); return showAuth(); }
  S.me = me;
  S.companies = (await withErr(() => Api.companies.list())) || [];
  $("#auth-view").classList.add("hidden");
  $("#app-view").classList.remove("hidden");
  $("#me-box").innerHTML = `${esc(me.firstName || me.first_name)} ${esc(me.lastName || me.last_name)}<br>${esc(me.email)}`;
  refreshNotifBadge();
  if (!location.hash) location.hash = "#/dashboard";
  else route();
}
function showAuth() {
  $("#app-view").classList.add("hidden");
  $("#auth-view").classList.remove("hidden");
}
async function refreshNotifBadge() {
  if (!S.me) return;
  const list = await Api.users.notifications(S.me.id).catch(() => []);
  const unread = (list || []).filter((n) => !n.is_read && n.isRead !== true).length;
  const b = $("#notif-badge");
  b.classList.toggle("hidden", !unread);
  b.textContent = unread;
}

/* ---------- router ---------- */
const routes = {
  dashboard: viewDashboard,
  projects: viewProjects,
  tasks: viewTasks,
  companies: viewCompanies,
  team: viewTeam,
  notifications: viewNotifications,
  workspaces: viewWorkspaces,
};
function route() {
  const h = location.hash || "#/dashboard";
  const [, name, param] = h.split("/");
  $$("#nav button").forEach((b) => b.classList.toggle("active", b.dataset.route === name));
  (routes[name] || viewDashboard)(param);
}
window.addEventListener("hashchange", () => Api.token() && route());

/* ---------- dashboard: 3 tabs ---------- */
let dashTab = "all"; // personal | companies | all
async function viewDashboard() {
  const m = $("#main");
  m.innerHTML = `<h2>Кабінет</h2>
    <div class="tabs">
      <button class="tab" data-t="personal">Personal</button>
      <button class="tab" data-t="companies">Companies</button>
      <button class="tab" data-t="all">Усе разом</button>
    </div>
    ${sortToolbar("dash")}
    <div id="dash-body"><p class="muted">Завантаження…</p></div>`;
  $$("#main .tab").forEach((b) => {
    b.classList.toggle("active", b.dataset.t === dashTab);
    b.onclick = () => { dashTab = b.dataset.t; viewDashboard(); };
  });
  bindSort("dash", viewDashboard);
  const [ws, projects] = await Promise.all([
    withErr(() => Api.workspaces.list()),
    withErr(() => Api.projects.list({ page: 0, size: 200 })),
  ]);
  if (!ws || !projects) return;
  const personal = projects.filter((p) => !p.company_id);
  const byCompany = {};
  projects.filter((p) => p.company_id).forEach((p) => {
    (byCompany[p.company_id] = byCompany[p.company_id] || []).push(p);
  });
  const cname = (id) => (S.companies.find((c) => c.id === id) || {}).name || shortId(id);
  const projCard = (p) => `<div class="card"><h3><a href="#/projects/${p.id}">${esc(p.name)}</a></h3>
    <div><span class="tag">${p.company_id ? "company: " + esc(cname(p.company_id)) : "personal"}</span>
    <span class="tag">${fmtDate(p.created_at)}</span></div></div>`;
  let html = "";
  const showPersonal = dashTab !== "companies", showCompanies = dashTab !== "personal";
  if (showPersonal) {
    html += `<h3>Personal workspace</h3>`;
    html += personal.length
      ? `<div class="grid">${sortBy(personal, S.sort.key === "title" ? "name" : S.sort.key, S.sort.dir).map(projCard).join("")}</div>`
      : `<p class="muted">Поки порожньо. <a href="#/projects">Створити проєкт</a></p>`;
  }
  if (showCompanies) {
    html += `<h3>Company workspaces</h3>`;
    const ids = Object.keys(byCompany);
    html += ids.length ? ids.map((id) => `<h4>${esc(cname(id))}</h4>
      <div class="grid">${sortBy(byCompany[id], S.sort.key === "title" ? "name" : S.sort.key, S.sort.dir).map(projCard).join("")}</div>`).join("")
      : `<p class="muted">Немає проєктів у компаніях.</p>`;
  }
  $("#dash-body").innerHTML = html;
}

/* ---------- projects ---------- */
async function viewProjects(id) {
  if (id) return viewProjectDetail(id);
  const m = $("#main");
  m.innerHTML = `<h2>Проєкти</h2>${sortToolbar("proj")}<div id="proj-list"></div>
    <div class="detail"><h3>Новий проєкт</h3>
      <label>Назва<input id="np-name" /></label>
      <label>Опис<input id="np-desc" /></label>
      <label>Компанія<select id="np-comp"><option value="">Personal (без компанії)</option></select></label>
      <button class="btn" id="np-btn">Створити</button></div>`;
  bindSort("proj", () => viewProjects());
  const [list, companies] = await Promise.all([
    withErr(() => Api.projects.list({ page: 0, size: 200 })), withErr(() => Api.companies.list()),
  ]);
  if (!list) return;
  S.companies = companies || [];
  $("#np-comp").innerHTML = `<option value="">Personal (без компанії)</option>` +
    S.companies.map((c) => `<option value="${c.id}">${esc(c.name)}</option>`).join("");
  const items = sortBy(list, S.sort.key === "title" ? "name" : S.sort.key, S.sort.dir);
  $("#proj-list").innerHTML = `<div class="grid">${items.map((p) => `<div class="card"><h3><a href="#/projects/${p.id}">${esc(p.name)}</a></h3>
    <div><span class="tag">${p.company_id ? esc((S.companies.find((c) => c.id === p.company_id) || {}).name || "company") : "personal"}</span></div>
    <div class="muted">${esc(p.description || "")}</div></div>`).join("")}</div>`;
  $("#np-btn").onclick = async () => {
    const body = { name: $("#np-name").value, description: $("#np-desc").value || undefined, created_by: S.me.id };
    const cid = $("#np-comp").value;
    if (cid) body.company_id = cid;
    const r = await withErr(() => Api.projects.create(body), "Проєкт створено");
    if (r) viewProjects();
  };
}

async function viewProjectDetail(pid) {
  const m = $("#main");
  const p = await withErr(() => Api.projects.get(pid));
  if (!p) { m.innerHTML = `<p>Проєкт не знайдено або нема доступу.</p>`; return; }
  const [members, deadlines, tasks] = await Promise.all([
    Api.projects.members(pid).catch(() => []),
    Api.projects.deadlines(pid).catch(() => []),
    Api.tasks.list({ project_id: pid }).catch(() => []),
  ]);
  m.innerHTML = `<h2>${esc(p.name)}</h2>
    <div><span class="tag">${p.company_id ? "company" : "personal"}</span> <span class="tag">${fmtDate(p.created_at)}</span></div>
    <p>${esc(p.description || "")}</p>
    <div class="toolbar">
      <input id="pe-name" value="${esc(p.name)}" /> <input id="pe-desc" value="${esc(p.description || "")}" />
      <button class="btn small" id="pe-save">Зберегти</button>
      <button class="btn small danger" id="pe-del">Видалити проєкт</button>
    </div>
    <div class="detail"><h3>Задачі (${tasks.length})</h3>
      <ul class="clean">${tasks.map((t) => `<li><a href="#/tasks/${t.id}">${esc(t.title)}</a> <span class="tag">${esc(t.status)}</span> <span class="tag">${esc(t.priority)}</span></li>`).join("")}</ul>
      <div class="toolbar"><input id="nt-title" placeholder="Нова задача" />
      <select id="nt-status"><option>OPEN</option><option>IN_PROGRESS</option><option>DONE</option><option>CANCELED</option></select>
      <select id="nt-prio"><option>LOW</option><option selected>MEDIUM</option><option>HIGH</option><option>CRITICAL</option></select>
      <button class="btn small" id="nt-btn">Додати</button></div>
    </div>
    <div class="detail"><h3>Учасники</h3>
      <ul class="clean">${members.map((x) => `<li>${shortId(x.user_id)} — ${esc(x.member_role)} <button class="ghost" data-del-member="${x.user_id}">прибрати</button></li>`).join("")}</ul>
      <div class="toolbar"><input id="nm-user" placeholder="user_id" style="width:300px" />
      <select id="nm-role"><option>USER</option><option>MANAGER</option><option>ADMIN</option><option>OWNER</option></select>
      <button class="btn small" id="nm-btn">Додати</button></div>
    </div>
    <div class="detail"><h3>Дедлайни</h3>
      <ul class="clean">${deadlines.map((d) => `<li>${esc(d.title || "")} — ${fmtDate(d.deadline)} <span class="tag">${esc((d.reminder_periods || []).join(", "))}</span></li>`).join("")}</ul>
      <div class="toolbar"><input id="nd-title" placeholder="Назва" /><input id="nd-at" type="datetime-local" />
      <button class="btn small" id="nd-btn">Додати</button></div>
    </div>`;
  $("#pe-save").onclick = async () => {
    const r = await withErr(() => Api.projects.update(pid, { name: $("#pe-name").value, description: $("#pe-desc").value }), "Збережено");
    if (r) viewProjectDetail(pid);
  };
  $("#pe-del").onclick = async () => {
    if (!confirm("Видалити проєкт?")) return;
    const r = await withErr(() => Api.projects.remove(pid), "Видалено");
    if (r !== null) location.hash = "#/projects";
  };
  $("#nt-btn").onclick = async () => {
    const r = await withErr(() => Api.tasks.create({ project_id: pid, title: $("#nt-title").value, status: $("#nt-status").value, priority: $("#nt-prio").value, created_by: S.me.id }), "Задачу створено");
    if (r) viewProjectDetail(pid);
  };
  $("#nm-btn").onclick = async () => {
    const r = await withErr(() => Api.projects.addMember(pid, { user_id: $("#nm-user").value, member_role: $("#nm-role").value }), "Додано");
    if (r) viewProjectDetail(pid);
  };
  $$("[data-del-member]").forEach((b) => (b.onclick = async () => {
    const r = await withErr(() => Api.projects.removeMember(pid, b.dataset.delMember), "Прибрано");
    if (r !== null) viewProjectDetail(pid);
  }));
  $("#nd-btn").onclick = async () => {
    const at = $("#nd-at").value ? new Date($("#nd-at").value).toISOString() : null;
    const r = await withErr(() => Api.projects.createDeadline(pid, { deadline: at, title: $("#nd-title").value || undefined, reminder_periods: [], created_by: S.me.id }), "Додано");
    if (r) viewProjectDetail(pid);
  };
}

/* ---------- tasks ---------- */
async function viewTasks(id) {
  if (id) return viewTaskDetail(id);
  const m = $("#main");
  m.innerHTML = `<h2>Задачі</h2>
    <div class="toolbar"><label>Статус<select id="tf-status"><option value="">всі</option><option>OPEN</option><option>IN_PROGRESS</option><option>DONE</option><option>CANCELED</option></select></label>
    <button class="ghost" id="tf-btn">Фільтрувати</button></div>
    ${sortToolbar("task")}<div id="task-list"></div>`;
  bindSort("task", () => viewTasks());
  const load = async () => {
    const list = await withErr(() => Api.tasks.list({ status: $("#tf-status").value || undefined }));
    if (!list) return;
    const items = sortBy(list, S.sort.key === "name" ? "title" : S.sort.key === "title" ? "title" : S.sort.key, S.sort.dir);
    $("#task-list").innerHTML = `<div class="grid">${items.map((t) => `<div class="card"><h3><a href="#/tasks/${t.id}">${esc(t.title)}</a></h3>
      <div><span class="tag blue">${esc(t.status)}</span><span class="tag">${esc(t.priority)}</span></div></div>`).join("")}</div>`;
  };
  $("#tf-btn").onclick = load;
  load();
}

async function viewTaskDetail(tid) {
  const m = $("#main");
  const t = await withErr(() => Api.tasks.get(tid));
  if (!t) { m.innerHTML = `<p>Задачу не знайдено або нема доступу.</p>`; return; }
  const [comments, entries, atts, rems, hist] = await Promise.all([
    Api.tasks.comments(tid).catch(() => []), Api.tasks.timeEntries(tid).catch(() => []),
    Api.tasks.attachments(tid).catch(() => []), Api.tasks.reminders(tid).catch(() => []),
    Api.tasks.history(tid).catch(() => []),
  ]);
  m.innerHTML = `<h2>${esc(t.title)}</h2>
    <div><span class="tag blue">${esc(t.status)}</span><span class="tag">${esc(t.priority)}</span>
    <span class="tag">до ${fmtDate(t.due_date)}</span></div>
    <p>${esc(t.description || "")}</p>
    <div class="toolbar">
      <select id="te-status"><option>OPEN</option><option>IN_PROGRESS</option><option>DONE</option><option>CANCELED</option></select>
      <select id="te-prio"><option>LOW</option><option>MEDIUM</option><option>HIGH</option><option>CRITICAL</option></select>
      <button class="btn small" id="te-save">Зберегти</button>
      <button class="btn small danger" id="te-del">Видалити</button>
    </div>
    <div class="detail"><h3>Коментарі</h3><ul class="clean">${comments.map((c) => `<li>${esc(c.text)}</li>`).join("")}</ul>
      <div class="toolbar"><input id="nc-text" placeholder="Новий коментар" style="flex:1" /><button class="btn small" id="nc-btn">Додати</button></div></div>
    <div class="detail"><h3>Облік часу</h3><ul class="clean">${entries.map((e) => `<li>${fmtDate(e.start_time)} → ${fmtDate(e.end_time)} (${e.duration_seconds ?? "—"} c)</li>`).join("")}</ul>
      <div class="toolbar"><input id="ne-s" type="datetime-local" /><input id="ne-e" type="datetime-local" /><button class="btn small" id="ne-btn">Додати</button></div></div>
    <div class="detail"><h3>Вкладення (посилання)</h3><ul class="clean">${atts.map((a) => `<li><a href="${esc(a.file_url)}" target="_blank">${esc(a.file_name)}</a> <button class="ghost" data-del-att="${a.id}">видалити</button></li>`).join("")}</ul>
      <div class="toolbar"><input id="na-name" placeholder="Назва файлу" /><input id="na-url" placeholder="https://…" style="flex:1" /><button class="btn small" id="na-btn">Додати</button></div></div>
    <div class="detail"><h3>Нагадування</h3><ul class="clean">${rems.map((r) => `<li>${fmtDate(r.remind_at)} — ${esc(r.message || "")}</li>`).join("")}</ul>
      <div class="toolbar"><input id="nr-at" type="datetime-local" /><input id="nr-msg" placeholder="Текст" /><button class="btn small" id="nr-btn">Додати</button></div></div>
    <div class="detail"><h3>Історія змін</h3><ul class="clean">${hist.map((h) => `<li>${esc(h.field_changed)}: ${esc(h.old_value ?? "")} → ${esc(h.new_value ?? "")} (${fmtDate(h.changed_at)})</li>`).join("")}</ul></div>`;
  $("#te-status").value = t.status; $("#te-prio").value = t.priority;
  $("#te-save").onclick = async () => {
    const r = await withErr(() => Api.tasks.update(tid, { status: $("#te-status").value, priority: $("#te-prio").value }), "Збережено");
    if (r) viewTaskDetail(tid);
  };
  $("#te-del").onclick = async () => {
    if (!confirm("Видалити задачу?")) return;
    const r = await withErr(() => Api.tasks.remove(tid), "Видалено");
    if (r !== null) location.hash = "#/tasks";
  };
  $("#nc-btn").onclick = async () => {
    const r = await withErr(() => Api.tasks.addComment(tid, { user_id: S.me.id, text: $("#nc-text").value }), "Додано");
    if (r) viewTaskDetail(tid);
  };
  $("#ne-btn").onclick = async () => {
    const b = { user_id: S.me.id, start_time: new Date($("#ne-s").value).toISOString(), end_time: new Date($("#ne-e").value).toISOString() };
    const r = await withErr(() => Api.tasks.addTimeEntry(tid, b), "Додано");
    if (r) viewTaskDetail(tid);
  };
  $("#na-btn").onclick = async () => {
    const r = await withErr(() => Api.tasks.addAttachment(tid, { file_name: $("#na-name").value, file_url: $("#na-url").value, uploaded_by: S.me.id }), "Додано");
    if (r) viewTaskDetail(tid);
  };
  $$("[data-del-att]").forEach((b) => (b.onclick = async () => {
    const r = await withErr(() => Api.tasks.deleteAttachment(tid, b.dataset.delAtt), "Видалено");
    if (r !== null) viewTaskDetail(tid);
  }));
  $("#nr-btn").onclick = async () => {
    const r = await withErr(() => Api.tasks.addReminder(tid, { remind_at: new Date($("#nr-at").value).toISOString(), message: $("#nr-msg").value, created_by: S.me.id }), "Додано");
    if (r) viewTaskDetail(tid);
  };
}

/* ---------- companies ---------- */
async function viewCompanies(id) {
  if (id) return viewCompanyDetail(id);
  const m = $("#main");
  m.innerHTML = `<h2>Компанії</h2><div id="comp-list"></div>
    <div class="detail"><h3>Нова компанія</h3><p class="muted">Ти станеш її оунером автоматично.</p>
    <label>Назва<input id="nc-name" /></label><label>Опис<input id="nc-desc" /></label>
    <button class="btn" id="nc-btn">Створити</button></div>`;
  const load = async () => {
    S.companies = (await withErr(() => Api.companies.list())) || [];
    $("#comp-list").innerHTML = `<div class="grid">${S.companies.map((c) => `<div class="card"><h3><a href="#/companies/${c.id}">${esc(c.name)}</a></h3>
      <div class="muted">${esc(c.description || "")}</div>
      <div><span class="tag">${c.owner_id === S.me.id ? "твоя (оунер)" : "учасник"}</span></div></div>`).join("")}</div>`;
  };
  load();
  $("#nc-btn").onclick = async () => {
    const r = await withErr(() => Api.companies.create({ owner_id: S.me.id, name: $("#nc-name").value, description: $("#nc-desc").value || undefined }), "Створено");
    if (r) viewCompanies();
  };
}

async function viewCompanyDetail(cid) {
  const m = $("#main");
  const c = await withErr(() => Api.companies.get(cid));
  if (!c) { m.innerHTML = `<p>Нема доступу або не знайдено.</p>`; return; }
  const [roles, projects] = await Promise.all([
    Api.companies.roles(cid).catch(() => []),
    Api.projects.list({ company_id: cid }).catch(() => []),
  ]);
  const myRole = (roles.find((r) => r.user_id === S.me.id) || {}).role;
  m.innerHTML = `<h2>${esc(c.name)}</h2>
    <div><span class="tag">твоя роль: ${esc(myRole || "—")}</span> <span class="tag">owner: ${shortId(c.owner_id)}</span></div>
    <p>${esc(c.description || "")}</p>
    <div class="toolbar"><input id="ce-name" value="${esc(c.name)}" /><input id="ce-desc" value="${esc(c.description || "")}" />
    <button class="btn small" id="ce-save">Зберегти</button>
    <button class="btn small danger" id="ce-del">Видалити компанію</button></div>
    <div class="detail"><h3>Проєкти компанії</h3>
      <ul class="clean">${projects.map((p) => `<li><a href="#/projects/${p.id}">${esc(p.name)}</a></li>`).join("")}</ul></div>
    <div class="detail"><h3>Учасники та ролі</h3>
      <table><tr><th>User</th><th>Роль</th><th></th></tr>
      ${roles.map((r) => `<tr><td>${shortId(r.user_id)}${r.user_id === S.me.id ? " (ти)" : ""}</td><td>${esc(r.role)}</td>
        <td><select data-role-sel="${r.id}">${["USER", "MANAGER", "ADMIN", "OWNER"].map((o) => `<option ${o === r.role ? "selected" : ""}>${o}</option>`).join("")}</select>
        <button class="btn small" data-role-save="${r.id}" data-role-user="${r.user_id}">Змінити</button>
        <button class="ghost" data-role-del="${r.id}">Прибрати</button></td></tr>`).join("")}</table>
      <div class="toolbar"><input id="ra-user" placeholder="user_id нового учасника" style="width:300px" />
      <select id="ra-role"><option>USER</option><option>MANAGER</option><option>ADMIN</option><option>OWNER</option></select>
      <button class="btn small" id="ra-btn">Призначити</button></div>
      <p class="muted">Оунер може давати USER/MANAGER/ADMIN/OWNER. Адмін — лише USER/MANAGER.</p></div>
    <div class="detail"><h3>Інвайти</h3>
      <div class="toolbar"><button class="btn small" id="inv-btn">Створити код інвайта</button><span id="inv-code" class="tag"></span></div>
      <p class="muted">Новачок: реєстрація Personal → прийняти інвайт → стає юзером. Код дійсний 7 днів.</p></div>`;
  $("#ce-save").onclick = async () => {
    const r = await withErr(() => Api.companies.update(cid, { name: $("#ce-name").value, description: $("#ce-desc").value }), "Збережено");
    if (r) viewCompanyDetail(cid);
  };
  $("#ce-del").onclick = async () => {
    if (!confirm("Видалити компанію? Воркспейс при цьому залишиться.")) return;
    const r = await withErr(() => Api.companies.remove(cid), "Видалено");
    if (r !== null) location.hash = "#/companies";
  };
  $("#ra-btn").onclick = async () => {
    const r = await withErr(() => Api.companies.assignRole(cid, { user_id: $("#ra-user").value, role: $("#ra-role").value }), "Призначено");
    if (r) viewCompanyDetail(cid);
  };
  $$("[data-role-save]").forEach((b) => (b.onclick = async () => {
    const sel = document.querySelector(`[data-role-sel="${b.dataset.roleSave}"]`);
    const r = await withErr(() => Api.companies.updateRole(cid, b.dataset.roleSave, { user_id: b.dataset.roleUser, role: sel.value }), "Роль змінено");
    if (r) viewCompanyDetail(cid);
  }));
  $$("[data-role-del]").forEach((b) => (b.onclick = async () => {
    if (!confirm("Прибрати учасника?")) return;
    const r = await withErr(() => Api.companies.removeRole(cid, b.dataset.roleDel), "Прибрано");
    if (r !== null) { S.companies = await Api.companies.list().catch(() => S.companies); viewCompanyDetail(cid); }
  }));
  $("#inv-btn").onclick = async () => {
    const r = await withErr(() => Api.invites.create({ company_id: cid }), "Інвайт створено");
    if (r) $("#inv-code").textContent = r.code;
  };
}

/* ---------- team: cross-company overview ---------- */
async function viewTeam() {
  const m = $("#main");
  m.innerHTML = `<h2>Команда</h2><p class="muted">Прийняти інвайт-код:</p>
    <div class="toolbar"><input id="acc-code" placeholder="код інвайта" style="width:300px" /><button class="btn small" id="acc-btn">Приєднатись</button></div>
    <div id="team-body"><p class="muted">Завантаження…</p></div>`;
  $("#acc-btn").onclick = async () => {
    const r = await withErr(() => Api.invites.accept($("#acc-code").value), "Тебе додано в компанію!");
    if (r) { S.companies = await Api.companies.list().catch(() => []); viewTeam(); }
  };
  const companies = S.companies.length ? S.companies : await Api.companies.list().catch(() => []);
  S.companies = companies;
  let html = "";
  for (const c of companies) {
    const roles = await Api.companies.roles(c.id).catch(() => []);
    html += `<div class="detail"><h3><a href="#/companies/${c.id}">${esc(c.name)}</a></h3>
      <table><tr><th>User</th><th>Роль</th></tr>
      ${roles.map((r) => `<tr><td>${shortId(r.user_id)}${r.user_id === S.me.id ? " (ти)" : ""}</td><td>${esc(r.role)}</td></tr>`).join("")}</table></div>`;
  }
  $("#team-body").innerHTML = html || `<p class="muted">Немає компаній.</p>`;
}

/* ---------- notifications ---------- */
async function viewNotifications() {
  const m = $("#main");
  const list = (await withErr(() => Api.users.notifications(S.me.id, 0, 100))) || [];
  m.innerHTML = `<h2>Сповіщення</h2>
    <ul class="clean">${list.map((n) => `<li>${n.is_read ? "" : "<b>● </b>"}${esc(n.message || n.type || "")}
    <span class="tag">${fmtDate(n.created_at)}</span>
    ${n.is_read ? "" : `<button class="btn small" data-read="${n.id}">Прочитано</button>`}</li>`).join("") || "<li>Порожньо</li>"}</ul>`;
  $$("[data-read]").forEach((b) => (b.onclick = async () => {
    const r = await withErr(() => Api.users.markRead(S.me.id, b.dataset.read), "Позначено");
    if (r) { viewNotifications(); refreshNotifBadge(); }
  }));
  refreshNotifBadge();
}

/* ---------- workspaces ---------- */
async function viewWorkspaces() {
  const m = $("#main");
  const list = (await withErr(() => Api.workspaces.list())) || [];
  const pers = list.filter((w) => w.type === "PERSONAL");
  const comp = list.filter((w) => w.type !== "PERSONAL");
  const card = (w) => `<div class="card"><h3>${esc(w.name)}</h3>
    <div><span class="tag ${w.type === "PERSONAL" ? "green" : "blue"}">${esc(w.type)}</span>
    ${w.company_id ? `<span class="tag">${esc((S.companies.find((c) => c.id === w.company_id) || {}).name || shortId(w.company_id))}</span>` : ""}</div>
    <div class="row"><button class="btn small danger" data-del-ws="${w.id}">Видалити</button></div>
    <div class="muted" style="font-size:12px;margin-top:6px">Видалення явне і безповоротне; дані проєктів не чіпає.</div></div>`;
  m.innerHTML = `<h2>Воркспейси</h2>
    <h3>Personal</h3><div class="grid">${pers.map(card).join("") || "<p class='muted'>Немає</p>"}</div>
    <h3>Companies</h3><div class="grid">${comp.map(card).join("") || "<p class='muted'>Немає</p>"}</div>`;
  $$("[data-del-ws]").forEach((b) => (b.onclick = async () => {
    if (!confirm("Видалити воркспейс?")) return;
    const r = await withErr(() => Api.workspaces.remove(b.dataset.delWs), "Видалено");
    if (r !== null) viewWorkspaces();
  }));
}

/* ---------- boot ---------- */
$("#logout-btn").addEventListener("click", () => {
  Api.setToken(null);
  S.me = null;
  location.hash = "#/dashboard";
  showAuth();
});
initAuth();
if (Api.token()) enterApp();
