/* SPA for Task & Time Tracker. Hash router, no build step.
 * UI language: UA/EN/DE toggle, persisted in localStorage (see js/i18n.js). */
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
const fmtDate = (iso) => (iso ? new Date(iso).toLocaleString(locale()) : "—");
const shortId = (id) => (id ? String(id).slice(0, 8) : "—");

/** Accepts a bare code or a full invite link (…/#/join/CODE) and returns the code. */
function parseInviteCode(input) {
  const v = String(input || "").trim();
  const m = v.match(/join\/([^/?#]+)/);
  return m ? decodeURIComponent(m[1]) : v;
}

async function withErr(fn, okMsg) {
  try {
    const r = await fn();
    if (okMsg) toast(okMsg, "ok");
    return r ?? true; // 204 No Content -> true so refresh callbacks still run
  } catch (e) {
    toast(e.message, "error");
    return null;
  }
}

const S = { me: null, companies: [], sort: { key: "created_at", dir: "desc" } };

const Theme = {
  KEY: "ttt_theme",
  get() { return localStorage.getItem(this.KEY) || "dark"; },
  apply() {
    const dark = this.get() !== "light";
    document.documentElement.dataset.theme = dark ? "" : "light";
    $$(".theme-toggle").forEach((b) => { b.textContent = dark ? "🌙" : "☀️"; });
  },
  toggle() {
    localStorage.setItem(this.KEY, this.get() === "light" ? "dark" : "light");
    this.apply();
  },
};

const COUNTRIES = [
  ["380", "🇺🇦 Ukraine"], ["49", "🇩🇪 Deutschland"], ["48", "🇵🇱 Polska"],
  ["1", "🇺🇸 USA/CA"], ["44", "🇬🇧 UK"], ["33", "🇫🇷 France"], ["34", "🇪🇸 España"],
  ["39", "🇮🇹 Italia"], ["31", "🇳🇱 Nederland"], ["32", "🇧🇪 België"], ["43", "🇦🇹 Österreich"],
  ["41", "🇨🇭 Schweiz"], ["420", "🇨🇿 Česko"], ["421", "🇸🇰 Slovensko"], ["36", "🇭🇺 Hungary"],
  ["40", "🇷🇴 România"], ["359", "🇧🇬 Bulgaria"], ["30", "🇬🇷 Greece"], ["90", "🇹🇷 Türkiye"],
  ["972", "🇮🇱 Israel"], ["971", "🇦🇪 UAE"], ["91", "🇮🇳 India"], ["86", "🇨🇳 China"],
  ["81", "🇯🇵 Japan"], ["55", "🇧🇷 Brasil"], ["61", "🇦🇺 Australia"],
];

function phoneRow() {
  const opts = COUNTRIES.map(([c, n]) => `<option value="${c}"${c === "380" ? " selected" : ""}>+${c} ${n}</option>`).join("");
  return `<div class="phone-row"><select id="phone-cc">${opts}</select><input name="phone_number" type="tel" required placeholder="501234567" /></div>`;
}

function applyStaticI18n() {
  document.documentElement.lang = Lang.get();
  $$("[data-i18n]").forEach((el) => { el.textContent = t(el.dataset.i18n); });
  $$(".lang-sel").forEach((sel) => { sel.value = Lang.get(); });
}

function onLangChange(e) {
  Lang.set(e.target.value);
  applyStaticI18n();
  renderAuthForm();
  if (Api.token() && S.me) route();
}

function sortBy(arr, key, dir) {
  const m = dir === "asc" ? 1 : -1;
  return [...arr].sort((a, b) => {
    const x = a[key] ?? "", y = b[key] ?? "";
    return (x < y ? -1 : x > y ? 1 : 0) * m;
  });
}
function sortToolbar(prefix) {
  return `<div class="toolbar"><label class="muted">${esc(t("sort.label"))}</label>
    <select id="${prefix}-sort-key">
      <option value="created_at">${esc(t("sort.date"))}</option>
      <option value="name">${esc(t("sort.name"))}</option>
      <option value="title">${esc(t("sort.title"))}</option>
    </select>
    <select id="${prefix}-sort-dir"><option value="desc">${esc(t("sort.desc"))}</option><option value="asc">${esc(t("sort.asc"))}</option></select>
    <button class="ghost" id="${prefix}-sort-apply">${esc(t("sort.apply"))}</button></div>`;
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
    hintKey: "auth.hint.login",
    fields: [["email", "f.email", "email"], ["password", "f.password", "password"]],
    submitKey: "auth.do.login",
    run: async (v) => { const r = await Api.auth.login(v); Api.setToken(r.token); },
  },
  personal: {
    hintKey: "auth.hint.personal",
    fields: [["first_name", "f.first", "text"], ["last_name", "f.last", "text"], ["email", "f.email", "email"], ["password", "f.password6", "password"], ["phone_number", "f.phone", "text"]],
    submitKey: "auth.do.personal",
    run: async (v) => { const r = await Api.auth.signupPersonal(v); Api.setToken(r.token); },
  },
  company: {
    hintKey: "auth.hint.company",
    fields: [["first_name", "f.first", "text"], ["last_name", "f.last", "text"], ["email", "f.email", "email"], ["password", "f.password6", "password"], ["phone_number", "f.phone", "text"], ["company_name", "f.company_name", "text"], ["company_description", "f.company_desc", "text"]],
    submitKey: "auth.do.company",
    run: async (v) => { const r = await Api.auth.signupCompany(v); Api.setToken(r.token); },
  },
  invite: {
    hintKey: "auth.hint.invite",
    fields: [["invite_code", "f.invite_code", "text"], ["first_name", "f.first", "text"], ["last_name", "f.last", "text"], ["email", "f.email", "email"], ["password", "f.password6", "password"], ["phone_number", "f.phone", "text"]],
    submitKey: "auth.do.invite",
    run: async (v) => {
      const { invite_code, ...user } = v;
      const r = await Api.auth.signupPersonal(user);
      Api.setToken(r.token);
      const j = await Api.invites.accept(parseInviteCode(invite_code));
      toast(t("auth.joined"), "ok");
      return j;
    },
  },
};
let authTab = "login";
const OPTIONAL_FIELDS = ["company_description"];
function renderAuthForm() {
  const tab = AUTH_TABS[authTab];
  $("#auth-hint").textContent = t(tab.hintKey);
  $("#auth-form").innerHTML =
    tab.fields.map(([n, lKey, ty]) => {
      const optional = OPTIONAL_FIELDS.includes(n);
      if (n === "phone_number") {
        return `<label>${esc(t(lKey))}${optional ? "" : ' <span class="req">*</span>'}${phoneRow()}</label>`;
      }
      return `<label>${esc(t(lKey))}${optional ? "" : ' <span class="req">*</span>'}<input name="${n}" type="${ty}"${optional ? "" : " required"} /></label>`;
    }).join("") +
    `<label>${esc(t("f.backend"))}<input name="__base" value="${esc(Api.getBase())}" /></label>
     <button class="btn" type="submit">${esc(t(tab.submitKey))}</button>`;
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
    const cc = $("#phone-cc");
    if (cc) {
      const digits = String(fd.get("phone_number") || "").replace(/\D/g, "").replace(/^0+/, "");
      fd.set("phone_number", "+" + cc.value + digits);
    }
    const v = {};
    fd.forEach((val, k) => { if (k !== "__base" && val !== "") v[k] = val; });
    const r = await withErr(() => AUTH_TABS[authTab].run(v));
    if (r !== null) enterApp();
  });
}

/* ---------- shell ---------- */
async function enterApp() {
  if (!Api.token()) return showAuth();
  applyStaticI18n();
  const me = await withErr(() => Api.users.get(Api.userId()));
  if (!me) { Api.setToken(null); return showAuth(); }
  S.me = me;
  // Personal users may have no right to list companies - not an error.
  S.companies = await Api.companies.list().catch(() => []);
  // Pending invite link (#/join/CODE opened before login) - accept now.
  const pending = sessionStorage.getItem("ttt_pending_invite");
  if (pending) {
    sessionStorage.removeItem("ttt_pending_invite");
    const jr = await withErr(() => Api.invites.accept(pending), t("team.joined"));
    if (jr) S.companies = await Api.companies.list().catch(() => S.companies);
  }
  $("#auth-view").classList.add("hidden");
  $("#app-view").classList.remove("hidden");
  $("#me-box").innerHTML = `${esc(me.firstName || me.first_name)} ${esc(me.lastName || me.last_name)}<br>${esc(me.email)}`;
  refreshNotifBadge();
  if (!location.hash) location.hash = "#/dashboard";
  else route();
}
function showAuth() {
  applyStaticI18n();
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

function hasCompany() {
  return (S.companies || []).length > 0;
}

function updateNavVisibility() {
  const show = hasCompany();
  $$("#nav button").forEach((b) => {
    if (b.dataset.route === "companies" || b.dataset.route === "team") {
      b.style.display = show ? "" : "none";
    }
  });
}

/* personal workspace visibility (per browser, like theme/lang) */
const personalVisible = () => localStorage.getItem("ttt_show_personal") !== "0";
const setPersonalVisible = (v) => localStorage.setItem("ttt_show_personal", v ? "1" : "0");

/* ---------- router ---------- */
const routes = {
  dashboard: viewDashboard,
  projects: viewProjects,
  tasks: viewTasks,
  companies: viewCompanies,
  team: viewTeam,
  notifications: viewNotifications,
  workspaces: viewWorkspaces,
  profile: viewProfile,
  join: viewJoin,
};
function checkPendingJoin() {
  const m = (location.hash || "").match(/^#\/join\/([^/]+)/);
  if (m) sessionStorage.setItem("ttt_pending_invite", decodeURIComponent(m[1]));
}
async function viewJoin(code) {
  if (!Api.token() || !S.me) {
    checkPendingJoin();
    showAuth();
    toast(t("invite.login_needed"));
    return;
  }
  const r = await withErr(() => Api.invites.accept(code || sessionStorage.getItem("ttt_pending_invite")), t("team.joined"));
  sessionStorage.removeItem("ttt_pending_invite");
  if (r) {
    S.companies = await Api.companies.list().catch(() => []);
    updateNavVisibility();
    location.hash = "#/dashboard";
  }
}
function route() {
  updateNavVisibility();
  const h = location.hash || "#/dashboard";
  const [, name, param] = h.split("/");
  if (!hasCompany() && (name === "companies" || name === "team")) {
    location.hash = "#/dashboard";
    return;
  }
  $$("#nav button").forEach((b) => b.classList.toggle("active", b.dataset.route === name));
  (routes[name] || viewDashboard)(param);
}
window.addEventListener("hashchange", () => { checkPendingJoin(); if (Api.token()) route(); });

/* ---------- dashboard: 3 tabs ---------- */
let dashTab = "all"; // personal | companies | all
async function viewDashboard() {
  const pv = personalVisible();
  if (!pv && dashTab === "personal") dashTab = hasCompany() ? "companies" : "all";
  if (!hasCompany() && dashTab === "companies") dashTab = "personal";
  const m = $("#main");
  m.innerHTML = `<h2>${esc(t("dash.title"))}</h2>
    <div class="tabs">
      ${pv ? `<button class="tab" data-t="personal">${esc(t("dash.personal"))}</button>` : ""}
      ${hasCompany() ? `<button class="tab" data-t="companies">${esc(t("dash.companies"))}</button>
      <button class="tab" data-t="all">${esc(t("dash.all"))}</button>` : ""}
    </div>
    ${sortToolbar("dash")}
    <div id="dash-body"><p class="muted">${esc(t("common.loading"))}</p></div>`;
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
  const upcoming = await Api.users.upcoming(S.me.id).catch(() => null);
  const personal = projects.filter((p) => !p.company_id);
  const byCompany = {};
  projects.filter((p) => p.company_id).forEach((p) => {
    (byCompany[p.company_id] = byCompany[p.company_id] || []).push(p);
  });
  const cname = (id) => (S.companies.find((c) => c.id === id) || {}).name || shortId(id);
  const projCard = (p) => `<div class="card"><h3><a href="#/projects/${p.id}">${esc(p.name)}</a></h3>
    <div><span class="tag">${p.company_id ? "company: " + esc(cname(p.company_id)) : esc(t("tag.personal"))}</span>
    <span class="tag">${fmtDate(p.created_at)}</span></div></div>`;
  let html = "";
  const compOf = {};
  projects.forEach((p) => { compOf[p.id] = p.company_id; });
  const inScope = (pid) => {
    if (!pv && !compOf[pid]) return false;
    if (dashTab === "personal") return !compOf[pid];
    if (dashTab === "companies") return !!compOf[pid];
    return true;
  };
  const dl = (upcoming.deadlines || []).filter((d) => inScope(d.project_id));
  const rm = (upcoming.reminders || []).filter((r) => inScope(r.project_id));
  if (upcoming && (dl.length || rm.length)) {
    const item = (over, main, sub, link) =>
      `<li>${over ? `<span class="tag red">${esc(t("upcoming.overdue"))}</span> ` : ""}<a href="${link}">${esc(main)}</a> <span class="tag">${esc(sub)}</span></li>`;
    html += `<h3>${esc(t("upcoming.title"))}</h3><ul class="clean">` +
      dl.map((d) => item(d.overdue, d.title || d.project_name, `${d.project_name} — ${fmtDate(d.deadline)}`, `#/projects/${d.project_id}`)).join("") +
      rm.map((r) => item(r.overdue, r.message || r.task_title, `${r.task_title} — ${fmtDate(r.remind_at)}`, `#/tasks/${r.task_id}`)).join("") +
      `</ul>`;
  } else {
    html += `<h3>${esc(t("upcoming.title"))}</h3><p class="muted">${esc(t("upcoming.empty"))}</p>`;
  }
  const showPersonal = pv && dashTab !== "companies", showCompanies = dashTab !== "personal";
  if (showPersonal) {
    html += `<h3>${esc(t("dash.pws"))}</h3>`;
    html += personal.length
      ? `<div class="grid">${sortBy(personal, S.sort.key === "title" ? "name" : S.sort.key, S.sort.dir).map(projCard).join("")}</div>`
      : `<p class="muted">${esc(t("dash.empty_personal"))} <a href="#/projects">${esc(t("dash.create_project"))}</a></p>`;
  }
  if (showCompanies) {
    html += `<h3>${esc(t("dash.cws"))}</h3>`;
    const ids = Object.keys(byCompany);
    html += ids.length ? ids.map((id) => `<h4>${esc(cname(id))}</h4>
      <div class="grid">${sortBy(byCompany[id], S.sort.key === "title" ? "name" : S.sort.key, S.sort.dir).map(projCard).join("")}</div>`).join("")
      : `<p class="muted">${esc(t("dash.no_company_projects"))}</p>`;
  }
  $("#dash-body").innerHTML = html;
  if (!hasCompany()) {
    $("#dash-body").insertAdjacentHTML("beforeend",
      `<div class="detail"><h3>${esc(t("team.join"))}</h3>
      <p class="muted">${esc(t("team.accept_hint"))}</p>
      <div class="toolbar"><input id="dash-acc-code" placeholder="${esc(t("team.accept_ph"))}" style="width:300px" />
      <button class="btn small" id="dash-acc-btn">${esc(t("team.join"))}</button></div></div>`);
    $("#dash-acc-btn").onclick = async () => {
      const r = await withErr(() => Api.invites.accept(parseInviteCode($("#dash-acc-code").value)), t("team.joined"));
      if (r) {
        S.companies = await Api.companies.list().catch(() => []);
        updateNavVisibility();
        viewDashboard();
      }
    };
  }
}

/* ---------- profile ---------- */
async function viewProfile() {
  const m = $("#main");
  m.innerHTML = `<h2>${esc(t("profile.title"))}</h2>
    <div class="detail"><h3>${esc(t("profile.title"))}</h3>
      <label>${esc(t("f.first"))}<input id="pf-first" value="${esc(S.me.first_name || "")}" /></label>
      <label>${esc(t("f.last"))}<input id="pf-last" value="${esc(S.me.last_name || "")}" /></label>
      <label>${esc(t("f.phone"))}<input id="pf-phone" value="${esc(S.me.phone_number || "")}" /></label>
      <label>Email<input value="${esc(S.me.email || "")}" disabled /></label>
      <label><input type="checkbox" id="pf-showpers" ${personalVisible() ? "" : "checked"} /> ${esc(t("profile.show_personal"))}</label>
      <button class="btn" id="pf-save">${esc(t("common.save"))}</button>
    </div>
    <div class="detail"><h3>${esc(t("account.title"))}</h3>
      <p class="muted">${esc(t("account.hint"))}</p>
      <button class="btn small danger" id="acc-del">${esc(t("account.delete"))}</button></div>`;
  $("#pf-showpers").onchange = (e) => setPersonalVisible(!e.target.checked);
  $("#pf-save").onclick = async () => {    const r = await withErr(() => Api.users.update(S.me.id, {
      first_name: $("#pf-first").value, last_name: $("#pf-last").value, phone_number: $("#pf-phone").value,
    }), t("common.saved"));
    if (r) {
      S.me = await Api.users.get(S.me.id).catch(() => S.me);
      $("#me-box").innerHTML = `${esc(S.me.first_name)} ${esc(S.me.last_name)}<br>${esc(S.me.email)}`;
      viewProfile();
    }
  };
  $("#acc-del").onclick = async () => {
    if (!confirm(t("account.confirm"))) return;
    if (!confirm(t("account.confirm2"))) return;
    const r = await withErr(() => Api.users.remove(S.me.id), t("account.deleted"));
    if (r !== null) {
      Api.setToken(null);
      S.me = null;
      location.hash = "#/dashboard";
      showAuth();
    }
  };
}

/* ---------- projects ---------- */
async function viewProjects(id) {
  if (id) return viewProjectDetail(id);
  const m = $("#main");
  m.innerHTML = `<h2>${esc(t("proj.title"))}</h2>${sortToolbar("proj")}<div id="proj-list"></div>
    <div class="detail"><h3>${esc(t("proj.new"))}</h3>
      <label>${esc(t("proj.name"))}<input id="np-name" /></label>
      <label>${esc(t("proj.desc"))}<input id="np-desc" /></label>
      <label>${esc(t("proj.company"))}<select id="np-comp"><option value="">${esc(t("proj.personal_opt"))}</option></select></label>
      <button class="btn" id="np-btn">${esc(t("proj.create"))}</button></div>`;
  bindSort("proj", () => viewProjects());
  const [list, companies] = await Promise.all([
    withErr(() => Api.projects.list({ page: 0, size: 200 })), withErr(() => Api.companies.list()),
  ]);
  if (!list) return;
  S.companies = companies || [];
  $("#np-comp").innerHTML = (personalVisible() ? `<option value="">${esc(t("proj.personal_opt"))}</option>` : "") +
    S.companies.map((c) => `<option value="${c.id}">${esc(c.name)}</option>`).join("");
  const items = sortBy(list, S.sort.key === "title" ? "name" : S.sort.key, S.sort.dir);
  $("#proj-list").innerHTML = `<div class="grid">${items.map((p) => `<div class="card"><h3><a href="#/projects/${p.id}">${esc(p.name)}</a></h3>
    <div><span class="tag">${p.company_id ? esc((S.companies.find((c) => c.id === p.company_id) || {}).name || "company") : esc(t("tag.personal"))}</span></div>
    <div class="muted">${esc(p.description || "")}</div></div>`).join("")}</div>`;
  $("#np-btn").onclick = async () => {
    const body = { name: $("#np-name").value, description: $("#np-desc").value || undefined, created_by: S.me.id };
    const cid = $("#np-comp").value;
    if (!cid && !personalVisible()) { toast(t("proj.need_company"), "error"); return; }
    if (cid) body.company_id = cid;
    const r = await withErr(() => Api.projects.create(body), t("common.created"));
    if (r) viewProjects();
  };
}

async function viewProjectDetail(pid) {
  const m = $("#main");
  const p = await withErr(() => Api.projects.get(pid));
  if (!p) { m.innerHTML = `<p>${esc(t("proj.notfound"))}</p>`; return; }
  const [members, deadlines, tasks, coMembers] = await Promise.all([
    Api.projects.members(pid).catch(() => []),
    Api.projects.deadlines(pid).catch(() => []),
    Api.tasks.list({ project_id: pid }).catch(() => []),
    p.company_id ? Api.companies.members(p.company_id).catch(() => []) : [],
  ]);
  const isOwner = (coMembers || []).some((x) => x.user_id === S.me.id && x.role === "OWNER");
  const canManageProj = p.company_id ? true : p.created_by === S.me.id;
  m.innerHTML = `<h2>${esc(p.name)}</h2>
    <div><span class="tag">${p.company_id ? "company" : esc(t("tag.personal"))}</span> <span class="tag">${fmtDate(p.created_at)}</span></div>
    <p>${esc(p.description || "")}</p>
    <div class="toolbar">
      <input id="pe-name" value="${esc(p.name)}" /> <input id="pe-desc" value="${esc(p.description || "")}" />
      ${canManageProj ? `<button class="btn small" id="pe-save">${esc(t("common.save"))}</button>
      <button class="btn small danger" id="pe-del">${esc(t("common.delete"))}</button>` : ""}
    </div>
    <div class="detail"><h3>${esc(t("proj.tasks"))} (${tasks.length})</h3>
      <ul class="clean">${tasks.map((x) => `<li><a href="#/tasks/${x.id}">${esc(x.title)}</a> <span class="tag">${esc(x.status)}</span> <span class="tag">${esc(x.priority)}</span></li>`).join("")}</ul>
      <div class="toolbar"><input id="nt-title" placeholder="${esc(t("proj.new_task"))}" />
      <select id="nt-status"><option>OPEN</option><option>IN_PROGRESS</option><option>DONE</option><option>CANCELED</option></select>
      <select id="nt-prio"><option>LOW</option><option selected>MEDIUM</option><option>HIGH</option><option>CRITICAL</option></select>
      <button class="btn small" id="nt-btn">${esc(t("common.add"))}</button></div>
    </div>
    <div class="detail"><h3>${esc(t("proj.members"))}</h3>
      <ul class="clean">${members.map((x) => { const who = `${esc(x.first_name || "")} ${esc(x.last_name || "")}`.trim() || shortId(x.user_id); return `<li>${who}${x.email ? ` <span class="muted">${esc(x.email)}</span>` : ""} — ${esc(x.member_role)}${canManageProj ? ` <button class="ghost" data-del-member="${x.user_id}">${esc(t("proj.remove_member"))}</button>` : ""}</li>`; }).join("")}</ul>
      ${canManageProj ? `<div class="toolbar"><input id="nm-user" placeholder="${esc(t("proj.new_member_ph"))}" style="width:300px" />
      <select id="nm-role"><option>USER</option><option>MANAGER</option><option>ADMIN</option><option>OWNER</option></select>
      <button class="btn small" id="nm-btn">${esc(t("common.add"))}</button></div>` : ""}
    </div>
    <div class="detail"><h3>${esc(t("proj.deadlines"))}</h3>
      <ul class="clean">${deadlines.map((d) => `<li>${esc(d.title || "")} — ${fmtDate(d.deadline)} <span class="tag">${esc((d.reminder_periods || []).join(", "))}</span>${(d.created_by === S.me.id || p.created_by === S.me.id || isOwner) ? ` <button class="ghost" data-del-deadline="${d.id}">${esc(t("common.delete"))}</button>` : ""}</li>`).join("")}</ul>
      ${canManageProj ? `<div class="toolbar"><input id="nd-title" placeholder="${esc(t("proj.deadline_title_ph"))}" /><input id="nd-at" type="datetime-local" />
      <button class="btn small" id="nd-btn">${esc(t("common.add"))}</button></div>` : ""}
    </div>`;
  const peSave = $("#pe-save");
  if (peSave) peSave.onclick = async () => {
    const r = await withErr(() => Api.projects.update(pid, { name: $("#pe-name").value, description: $("#pe-desc").value }), t("common.saved"));
    if (r) viewProjectDetail(pid);
  };
  const peDel = $("#pe-del");
  if (peDel) peDel.onclick = async () => {
    if (!confirm(t("proj.confirm_del"))) return;
    const r = await withErr(() => Api.projects.remove(pid), t("common.deleted"));
    if (r !== null) location.hash = "#/projects";
  };
  $("#nt-btn").onclick = async () => {
    const r = await withErr(() => Api.tasks.create({ project_id: pid, title: $("#nt-title").value, status: $("#nt-status").value, priority: $("#nt-prio").value, created_by: S.me.id }), t("common.created"));
    if (r) viewProjectDetail(pid);
  };
  const nmBtn = $("#nm-btn");
  if (nmBtn) nmBtn.onclick = async () => {
    const raw = ($("#nm-user").value || "").trim();
    const typed = raw.toLowerCase();
    if (!typed) { toast(t("notif.type_recipient")); return; }
    const inProject = new Set((members || []).map((x) => x.user_id));
    const addById = async (uid) => {
      if (inProject.has(uid)) { toast(t("proj.member_exists"), "error"); return; }
      const r = await withErr(() => Api.projects.addMember(pid, { user_id: uid, member_role: $("#nm-role").value }), t("common.added"));
      if (r) viewProjectDetail(pid);
    };
    const pool = p.company_id
      ? await Api.companies.members(p.company_id).catch(() => [])
      : (await Promise.all((S.companies || []).map((c) => Api.companies.members(c.id).catch(() => [])))).flat();
    const seen = new Set();
    const candidates = [];
    for (const x of pool) {
      if (!x.user_id || inProject.has(x.user_id) || seen.has(x.user_id)) continue;
      seen.add(x.user_id);
      const who = `${x.first_name || ""} ${x.last_name || ""}`.trim() || x.email || shortId(x.user_id);
      candidates.push({ id: x.user_id, email: (x.email || "").toLowerCase(), label: who.toLowerCase(), who });
    }
    const exact = candidates.find((r) => r.email && r.email === typed);
    const matches = candidates.filter((r) => r.label.includes(typed));
    const target = exact || (matches.length === 1 ? matches[0] : null);
    if (target) { addById(target.id); return; }
    if (matches.length > 1) {
      const options = matches.map((x) => (x.who ? `${x.who} <${x.email || "?"}>` : (x.email || shortId(x.id)))).join("; ");
      toast(`${t("notif.ambiguous")}: ${options}`, "error");
      return;
    }
    if (typed.includes("@")) {
      const found = await Api.users.byEmail(raw).catch(() => null);
      if (found && found.id) { addById(found.id); return; }
    }
    toast(t("proj.member_notfound"), "error");
  };
  $$("[data-del-member]").forEach((b) => (b.onclick = async () => {
    const r = await withErr(() => Api.projects.removeMember(pid, b.dataset.delMember), t("common.removed"));
    if (r !== null) viewProjectDetail(pid);
  }));
  const ndBtn = $("#nd-btn");
  if (ndBtn) ndBtn.onclick = async () => {
    const at = $("#nd-at").value ? new Date($("#nd-at").value).toISOString() : null;
    const r = await withErr(() => Api.projects.createDeadline(pid, { deadline: at, title: $("#nd-title").value || undefined, reminder_periods: [], created_by: S.me.id }), t("common.added"));
    if (r) viewProjectDetail(pid);
  };
  $$("[data-del-deadline]").forEach((b) => (b.onclick = async () => {
    const r = await withErr(() => Api.projects.deleteDeadline(pid, b.dataset.delDeadline), t("common.deleted"));
    if (r !== null) viewProjectDetail(pid);
  }));
}

/* ---------- tasks ---------- */
async function viewTasks(id) {
  if (id) return viewTaskDetail(id);
  const m = $("#main");
  m.innerHTML = `<h2>${esc(t("task.title"))}</h2>
    <div class="toolbar"><label>${esc(t("task.status"))}<select id="tf-status"><option value="">${esc(t("task.all"))}</option><option>OPEN</option><option>IN_PROGRESS</option><option>DONE</option><option>CANCELED</option></select></label>
    <button class="ghost" id="tf-btn">${esc(t("task.filter"))}</button></div>
    ${sortToolbar("task")}<div id="task-list"></div>`;
  bindSort("task", () => viewTasks());
  const load = async () => {
    const list = await withErr(() => Api.tasks.list({ status: $("#tf-status").value || undefined }));
    if (!list) return;
    let items = sortBy(list, S.sort.key === "name" ? "title" : S.sort.key === "title" ? "title" : S.sort.key, S.sort.dir);
    if (!personalVisible()) {
      const projs = await Api.projects.list({ page: 0, size: 200 }).catch(() => []);
      const persIds = new Set((projs || []).filter((p) => !p.company_id).map((p) => p.id));
      items = items.filter((x) => !persIds.has(x.project_id));
    }
    $("#task-list").innerHTML = `<div class="grid">${items.map((x) => `<div class="card"><h3><a href="#/tasks/${x.id}">${esc(x.title)}</a></h3>
      <div><span class="tag blue">${esc(x.status)}</span><span class="tag">${esc(x.priority)}</span></div></div>`).join("")}</div>`;
  };
  $("#tf-btn").onclick = load;
  load();
}

async function viewTaskDetail(tid) {
  const m = $("#main");
  const x = await withErr(() => Api.tasks.get(tid));
  if (!x) { m.innerHTML = `<p>${esc(t("task.notfound"))}</p>`; return; }
  const proj = x.project_id ? await Api.projects.get(x.project_id).catch(() => null) : null;
  const isPersonal = proj && !proj.company_id;
  const canEdit = !isPersonal || proj.created_by === S.me.id || x.created_by === S.me.id || x.assigned_to === S.me.id;
  const canDelete = !isPersonal || proj.created_by === S.me.id || x.created_by === S.me.id;
  const [comments, entries, atts, rems, hist] = await Promise.all([
    Api.tasks.comments(tid).catch(() => []), Api.tasks.timeEntries(tid).catch(() => []),
    Api.tasks.attachments(tid).catch(() => []), Api.tasks.reminders(tid).catch(() => []),
    Api.tasks.history(tid).catch(() => []),
  ]);
  m.innerHTML = `<h2>${esc(x.title)}</h2>
    <div><span class="tag blue">${esc(x.status)}</span><span class="tag">${esc(x.priority)}</span>
    <span class="tag">→ ${fmtDate(x.due_date)}</span></div>
    <p>${esc(x.description || "")}</p>
    <div class="toolbar">
      <select id="te-status"><option>OPEN</option><option>IN_PROGRESS</option><option>DONE</option><option>CANCELED</option></select>
      <select id="te-prio"><option>LOW</option><option>MEDIUM</option><option>HIGH</option><option>CRITICAL</option></select>
      ${canEdit ? `<button class="btn small" id="te-save">${esc(t("common.save"))}</button>` : ""}
      ${canDelete ? `<button class="btn small danger" id="te-del">${esc(t("common.delete"))}</button>` : ""}
    </div>
    <div class="detail"><h3>${esc(t("task.comments"))}</h3><ul class="clean">${comments.map((c) => `<li>${esc(c.text)} <button class="ghost" data-del-comment="${c.id}">${esc(t("task.del"))}</button></li>`).join("")}</ul>
      <div class="toolbar"><input id="nc-text" placeholder="${esc(t("task.comment_ph"))}" style="flex:1" /><button class="btn small" id="nc-btn">${esc(t("common.add"))}</button></div></div>
    <div class="detail"><h3>${esc(t("task.time"))}</h3><ul class="clean">${entries.map((e) => `<li>${fmtDate(e.start_time)} → ${fmtDate(e.end_time)} (${e.duration_seconds ?? "—"} s)</li>`).join("")}</ul>
      <div class="toolbar"><input id="ne-s" type="datetime-local" /><input id="ne-e" type="datetime-local" /><button class="btn small" id="ne-btn">${esc(t("common.add"))}</button></div></div>
    <div class="detail"><h3>${esc(t("task.attach"))}</h3><ul class="clean">${atts.map((a) => `<li><a href="${esc(a.file_url)}" target="_blank">${esc(a.file_name)}</a> <button class="ghost" data-del-att="${a.id}">${esc(t("task.del"))}</button></li>`).join("")}</ul>
      <div class="toolbar"><input id="na-name" placeholder="${esc(t("task.file_name_ph"))}" /><input id="na-url" placeholder="https://…" style="flex:1" /><button class="btn small" id="na-btn">${esc(t("common.add"))}</button></div></div>
    <div class="detail"><h3>${esc(t("task.reminders"))}</h3><ul class="clean">${rems.map((r) => `<li>${fmtDate(r.remind_at)} — ${esc(r.message || "")} <button class="ghost" data-del-rem="${r.id}">${esc(t("task.del"))}</button></li>`).join("")}</ul>
      <div class="toolbar"><input id="nr-at" type="datetime-local" /><input id="nr-msg" placeholder="${esc(t("task.rem_text"))}" /><button class="btn small" id="nr-btn">${esc(t("common.add"))}</button></div></div>
    <div class="detail"><h3>${esc(t("task.history"))}</h3><ul class="clean">${hist.map((h) => `<li>${esc(h.field_changed)}: ${esc(h.old_value ?? "")} → ${esc(h.new_value ?? "")} (${fmtDate(h.changed_at)})</li>`).join("")}</ul></div>`;
  $("#te-status").value = x.status; $("#te-prio").value = x.priority;
  const teSave = $("#te-save");
  if (teSave) teSave.onclick = async () => {
    const r = await withErr(() => Api.tasks.update(tid, { status: $("#te-status").value, priority: $("#te-prio").value }), t("common.saved"));
    if (r) viewTaskDetail(tid);
  };
  const teDel = $("#te-del");
  if (teDel) teDel.onclick = async () => {
    if (!confirm(t("task.confirm_del"))) return;
    const r = await withErr(() => Api.tasks.remove(tid), t("common.deleted"));
    if (r !== null) location.hash = "#/tasks";
  };
  $("#nc-btn").onclick = async () => {
    const r = await withErr(() => Api.tasks.addComment(tid, { user_id: S.me.id, text: $("#nc-text").value }), t("common.added"));
    if (r) viewTaskDetail(tid);
  };
  $$("[data-del-comment]").forEach((b) => (b.onclick = async () => {
    const r = await withErr(() => Api.tasks.deleteComment(tid, b.dataset.delComment), t("common.deleted"));
    if (r !== null) viewTaskDetail(tid);
  }));
  $("#ne-btn").onclick = async () => {
    const b = { user_id: S.me.id, start_time: new Date($("#ne-s").value).toISOString(), end_time: new Date($("#ne-e").value).toISOString() };
    const r = await withErr(() => Api.tasks.addTimeEntry(tid, b), t("common.added"));
    if (r) viewTaskDetail(tid);
  };
  $("#na-btn").onclick = async () => {
    const r = await withErr(() => Api.tasks.addAttachment(tid, { file_name: $("#na-name").value, file_url: $("#na-url").value, uploaded_by: S.me.id }), t("common.added"));
    if (r) viewTaskDetail(tid);
  };
  $$("[data-del-att]").forEach((b) => (b.onclick = async () => {
    const r = await withErr(() => Api.tasks.deleteAttachment(tid, b.dataset.delAtt), t("common.deleted"));
    if (r !== null) viewTaskDetail(tid);
  }));
  $("#nr-btn").onclick = async () => {
    const r = await withErr(() => Api.tasks.addReminder(tid, { remind_at: new Date($("#nr-at").value).toISOString(), message: $("#nr-msg").value, created_by: S.me.id }), t("common.added"));
    if (r) viewTaskDetail(tid);
  };
  $$("[data-del-rem]").forEach((b) => (b.onclick = async () => {
    const r = await withErr(() => Api.tasks.deleteReminder(tid, b.dataset.delRem), t("common.deleted"));
    if (r !== null) viewTaskDetail(tid);
  }));
}

/* ---------- companies ---------- */
async function viewCompanies(id) {
  if (id) return viewCompanyDetail(id);
  const m = $("#main");
  m.innerHTML = `<h2>${esc(t("comp.title"))}</h2><div id="comp-list"></div>
    <div class="detail"><h3>${esc(t("comp.new"))}</h3><p class="muted">${esc(t("comp.owner_hint"))}</p>
    <label>${esc(t("comp.name"))}<input id="nc-name" /></label><label>${esc(t("comp.desc"))}<input id="nc-desc" /></label>
    <button class="btn" id="nc-btn">${esc(t("comp.create"))}</button></div>`;
  const load = async () => {
    S.companies = (await withErr(() => Api.companies.list())) || [];
    $("#comp-list").innerHTML = `<div class="grid">${S.companies.map((c) => `<div class="card"><h3><a href="#/companies/${c.id}">${esc(c.name)}</a></h3>
      <div class="muted">${esc(c.description || "")}</div>
      <div><span class="tag">${c.owner_id === S.me.id ? esc(t("comp.yours_owner")) : esc(t("comp.member"))}</span></div></div>`).join("")}</div>`;
  };
  load();
  $("#nc-btn").onclick = async () => {
    const r = await withErr(() => Api.companies.create({ owner_id: S.me.id, name: $("#nc-name").value, description: $("#nc-desc").value || undefined }), t("common.created"));
    if (r) { S.companies = await Api.companies.list().catch(() => S.companies); updateNavVisibility(); viewCompanies(); }
  };
}

async function viewCompanyDetail(cid) {
  const m = $("#main");
  const c = await withErr(() => Api.companies.get(cid));
  if (!c) { m.innerHTML = `<p>${esc(t("comp.no_access"))}</p>`; return; }
  const [projects, members] = await Promise.all([
    Api.projects.list({ company_id: cid }).catch(() => []),
    Api.companies.members(cid).catch(() => []),
  ]);
  const roles = [];
  const myRole = (members.find((x) => x.user_id === S.me.id) || {}).role;
  const canManage = myRole === "OWNER" || myRole === "ADMIN";
  let visibleProjects = projects || [];
  if (myRole !== "OWNER") {
    const memberships = await Promise.all(visibleProjects.map((p) => Api.projects.members(p.id).catch(() => null)));
    visibleProjects = visibleProjects.filter((p, i) => (memberships[i] || []).some((x) => x.user_id === S.me.id));
  }
  m.innerHTML = `<h2>${esc(c.name)}</h2>
    <div><span class="tag">${esc(t("comp.your_role"))}: ${esc(myRole || "—")}</span> <span class="tag">${esc(t("comp.owner"))}: ${shortId(c.owner_id)}</span></div>
    <p>${esc(c.description || "")}</p>
    <div class="toolbar"><input id="ce-name" value="${esc(c.name)}" /><input id="ce-desc" value="${esc(c.description || "")}" />
    <button class="btn small" id="ce-save">${esc(t("common.save"))}</button>
    <button class="btn small danger" id="ce-del">${esc(t("common.delete"))}</button></div>
    <div class="detail"><h3>${esc(t("comp.projects"))}</h3>
      <ul class="clean">${visibleProjects.map((p) => `<li><a href="#/projects/${p.id}">${esc(p.name)}</a></li>`).join("")}</ul></div>
    <div class="detail"><h3>${esc(t("comp.team"))}</h3>
      <div class="toolbar"><input id="mem-search" placeholder="${esc(t("team.search_ph"))}" style="flex:1" />
      <select id="mem-role"><option value="">${esc(t("team.all_roles"))}</option><option>USER</option><option>MANAGER</option><option>ADMIN</option><option>OWNER</option></select></div>
      <table><tr><th>${esc(t("team.name"))}</th><th>${esc(t("comp.role"))}</th><th></th></tr>
      <tbody id="mem-rows"></tbody></table>
      ${canManage ? `<div class="toolbar"><input id="ra-user" placeholder="${esc(t("comp.new_member_ph"))}" style="width:300px" />
      <select id="ra-role">${(myRole === "ADMIN" ? ["USER", "MANAGER"] : ["USER", "MANAGER", "ADMIN", "OWNER"]).map((o) => `<option>${o}</option>`).join("")}</select>
      <button class="btn small" id="ra-btn">${esc(t("comp.assign"))}</button></div>
      <p class="muted">${esc(t("comp.roles_hint"))}</p>` : ""}</div>
    ${canManage ? `<div class="detail"><h3>${esc(t("comp.invites"))}</h3>
      <div class="toolbar"><button class="btn small" id="inv-btn">${esc(t("comp.make_invite"))}</button><span id="inv-code" class="tag"></span></div>
      <p class="muted">${esc(t("comp.invite_hint"))}</p></div>` : ""}`;
  $("#ce-save").onclick = async () => {
    const r = await withErr(() => Api.companies.update(cid, { name: $("#ce-name").value, description: $("#ce-desc").value }), t("common.saved"));
    if (r) viewCompanyDetail(cid);
  };
  $("#ce-del").onclick = async () => {
    if (!confirm(t("comp.del_confirm"))) return;
    const r = await withErr(() => Api.companies.remove(cid), t("common.deleted"));
    if (r !== null) location.hash = "#/companies";
  };
  const raBtn = $("#ra-btn");
  if (raBtn) raBtn.onclick = async () => {
    const r = await withErr(() => Api.companies.assignRole(cid, { user_id: $("#ra-user").value, role: $("#ra-role").value }), t("comp.assigned"));
    if (r) viewCompanyDetail(cid);
  };
  const renderMembers = () => {
    const q = ($("#mem-search").value || "").toLowerCase();
    const rf = $("#mem-role").value;
    const rows = members.filter((x) => {
      const hay = `${x.first_name || ""} ${x.last_name || ""} ${x.email || ""}`.toLowerCase();
      return (!q || hay.includes(q)) && (!rf || x.role === rf);
    });
    $("#mem-rows").innerHTML = rows.map((x) => {
      const who = `${esc(x.first_name || "")} ${esc(x.last_name || "")}`.trim() || shortId(x.user_id);
      const rowEditable = canManage && (myRole === "OWNER" || x.role === "USER" || x.role === "MANAGER");
      const roleOpts = (myRole === "ADMIN" ? ["USER", "MANAGER"] : ["USER", "MANAGER", "ADMIN", "OWNER"])
        .map((o) => `<option ${o === x.role ? "selected" : ""}>${o}</option>`).join("");
      const ctl = rowEditable
        ? `<td><select data-role-sel="${x.id}">${roleOpts}</select>
        <button class="btn small" data-role-save="${x.id}" data-role-user="${x.user_id}">${esc(t("comp.change"))}</button>
        <button class="ghost" data-role-del="${x.id}">${esc(t("comp.remove"))}</button></td>`
        : `<td></td>`;
      return `<tr><td>${who}${x.user_id === S.me.id ? " " + esc(t("team.you")) : ""}<br><span class="muted">${esc(x.email || "")}</span></td><td>${esc(x.role)}</td>${ctl}</tr>`;
    }).join("");
    bindMemberButtons();
  };
  const bindMemberButtons = () => {
    $$("[data-role-save]").forEach((b) => (b.onclick = async () => {
      const sel = document.querySelector(`[data-role-sel="${b.dataset.roleSave}"]`);
      const r = await withErr(() => Api.companies.updateRole(cid, b.dataset.roleSave, { user_id: b.dataset.roleUser, role: sel.value }), t("comp.role_changed"));
      if (r) viewCompanyDetail(cid);
    }));
    $$("[data-role-del]").forEach((b) => (b.onclick = async () => {
      if (!confirm(t("comp.remove_confirm"))) return;
      const r = await withErr(() => Api.companies.removeRole(cid, b.dataset.roleDel), t("comp.removed"));
      if (r !== null) { S.companies = await Api.companies.list().catch(() => S.companies); updateNavVisibility(); viewCompanyDetail(cid); }
    }));
  };
  $("#mem-search").addEventListener("input", renderMembers);
  $("#mem-role").addEventListener("change", renderMembers);
  renderMembers();
  const invBtn = $("#inv-btn");
  if (invBtn) invBtn.onclick = async () => {
    const r = await withErr(() => Api.invites.create({ company_id: cid }), t("comp.invite_done"));
    if (r) {
      const link = `${location.origin}/#/join/${encodeURIComponent(r.code)}`;
      $("#inv-code").innerHTML = `${esc(t("invite.link_hint"))} <a href="${esc(link)}" target="_blank">${esc(link)}</a>
        <button class="ghost" id="inv-copy">${esc(t("invite.copy"))}</button>`;
      $("#inv-copy").onclick = async () => {
        try { await navigator.clipboard.writeText(link); toast(t("invite.copied"), "ok"); }
        catch { window.prompt(t("invite.copy"), link); }
      };
    }
  };
}

/* ---------- team: cross-company overview ---------- */
async function viewTeam() {
  const m = $("#main");
  m.innerHTML = `<h2>${esc(t("team.title"))}</h2><p class="muted">${esc(t("team.accept_hint"))}</p>
    <div class="toolbar"><input id="acc-code" placeholder="${esc(t("team.accept_ph"))}" style="width:300px" /><button class="btn small" id="acc-btn">${esc(t("team.join"))}</button></div>
    <div id="team-body"><p class="muted">${esc(t("common.loading"))}</p></div>`;
  $("#acc-btn").onclick = async () => {
    const r = await withErr(() => Api.invites.accept(parseInviteCode($("#acc-code").value)), t("team.joined"));
    if (r) { S.companies = await Api.companies.list().catch(() => []); updateNavVisibility(); viewTeam(); }
  };
  const companies = S.companies.length ? S.companies : await Api.companies.list().catch(() => []);
  S.companies = companies;
  let html = "";
  for (const c of companies) {
    const members = await Api.companies.members(c.id).catch(() => []);
    html += `<div class="detail"><h3><a href="#/companies/${c.id}">${esc(c.name)}</a></h3>
      <table><tr><th>${esc(t("team.name"))}</th><th>Email</th><th>${esc(t("comp.role"))}</th></tr>
      ${members.map((x) => {
        const who = `${esc(x.first_name || "")} ${esc(x.last_name || "")}`.trim() || shortId(x.user_id);
        return `<tr><td>${who}${x.user_id === S.me.id ? " " + esc(t("team.you")) : ""}</td><td>${esc(x.email || "")}</td><td>${esc(x.role)}</td></tr>`;
      }).join("")}</table></div>`;
  }
  $("#team-body").innerHTML = html || `<p class="muted">${esc(t("team.empty"))}</p>`;
}

/* ---------- notifications ---------- */
let notifTab = "inbox"; // inbox | sent
async function viewNotifications() {
  const m = $("#main");
  const [list, sent] = await Promise.all([
    withErr(() => Api.users.notifications(S.me.id, 0, 100)),
    Api.users.sent(S.me.id).catch(() => []),
  ]);
  const inbox = list || [], outbox = sent || [];
  const row = (n, isOut) => {
    const counterpart = isOut ? n.user_id : n.sender_id;
    const who = isOut
      ? `${esc(n.sender_name ? "" : "")}`
      : (n.sender_name ? `<b>${esc(n.sender_name)}</b>: ` : "");
    const toWhom = isOut ? `<span class="tag">→ ${esc(n.to_name || "")}</span>` : "";
    return `<li>${n.is_read || isOut ? "" : "<b>● </b>"}${who}${esc(n.message || n.type || "")}
    <span class="tag">${fmtDate(n.created_at)}</span> ${toWhom}
    ${n.sender_id && !isOut ? `<button class="btn small" data-reply="${n.sender_id}" data-reply-name="${esc(n.sender_name || "")}">${esc(t("notif.reply"))}</button>` : ""}
    ${!n.is_read && !isOut ? `<button class="btn small" data-read="${n.id}">${esc(t("notif.read"))}</button>` : ""}
    <button class="ghost" data-del-msg="${n.id}">${esc(t("notif.del_msg"))}</button>
    ${counterpart ? `<button class="ghost" data-del-conv="${counterpart}">${esc(t("notif.del_conv"))}</button>` : ""}</li>`;
  };
  m.innerHTML = `<h2>${esc(t("notif.title"))}</h2>
    <div class="tabs">
      <button class="tab" data-nt="inbox">${esc(t("notif.inbox"))} (${inbox.filter((n) => !n.is_read).length})</button>
      <button class="tab" data-nt="sent">${esc(t("notif.sent_tab"))} (${outbox.length})</button>
    </div>
    <div class="detail"><h3>${esc(t("notif.compose"))}</h3>
      <div class="toolbar"><input id="nm-to" placeholder="${esc(t("notif.to_ph"))}" style="width:300px" />
      <input id="nm-text" placeholder="${esc(t("notif.message_ph"))}" style="flex:1" />
      <button class="btn small" id="nm-send">${esc(t("notif.send"))}</button></div></div>
    <ul class="clean" id="notif-list"></ul>`;
  const renderTab = () => {
    $$("#main .tab").forEach((b) => b.classList.toggle("active", b.dataset.nt === notifTab));
    $("#notif-list").innerHTML =
      (notifTab === "inbox" ? inbox.map((n) => row(n, false)) : outbox.map((n) => row(n, true))).join("")
      || `<li>${esc(t("notif.empty"))}</li>`;
    bindNotifButtons();
  };
  const bindNotifButtons = () => {
    $$("[data-reply]").forEach((b) => (b.onclick = () => {
      const toField = $("#nm-to");
      toField.value = b.dataset.replyName || "";
      toField.dataset.uid = b.dataset.reply;
      toField.focus();
      toast(t("notif.reply_hint"));
    }));
    $$("[data-read]").forEach((b) => (b.onclick = async () => {
      const r = await withErr(() => Api.users.markRead(S.me.id, b.dataset.read), t("notif.marked"));
      if (r) { viewNotifications(); refreshNotifBadge(); }
    }));
    $$("[data-del-msg]").forEach((b) => (b.onclick = async () => {
      if (!confirm(t("notif.del_confirm"))) return;
      const r = await withErr(() => Api.users.deleteMessage(S.me.id, b.dataset.delMsg), t("notif.deleted"));
      if (r !== null) viewNotifications();
    }));
    $$("[data-del-conv]").forEach((b) => (b.onclick = async () => {
      if (!confirm(t("notif.del_conv_confirm"))) return;
      const r = await withErr(() => Api.users.deleteConversation(S.me.id, b.dataset.delConv), t("notif.deleted"));
      if (r !== null) viewNotifications();
    }));
  };
  $$("#main .tab").forEach((b) => (b.onclick = () => { notifTab = b.dataset.nt; renderTab(); }));
  renderTab();
  const recipients = [];
  const seen = new Set([S.me.id]);
  for (const c of S.companies) {
    const members = await Api.companies.members(c.id).catch(() => []);
    for (const x of members) {
      if (!seen.has(x.user_id)) {
        seen.add(x.user_id);
        const who = `${x.first_name || ""} ${x.last_name || ""}`.trim() || x.email || shortId(x.user_id);
        recipients.push({ id: x.user_id, email: x.email || "", label: `${who} — ${c.name}` });
      }
    }
  }
  const toFieldInit = $("#nm-to");
  if (toFieldInit) toFieldInit.addEventListener("input", () => { delete toFieldInit.dataset.uid; });
  const sendBtn = $("#nm-send");
  if (sendBtn) sendBtn.onclick = async () => {
    const toField = $("#nm-to");
    if (toField.dataset.uid) {
      const r = await withErr(() => Api.users.sendMessage(toField.dataset.uid, { message: $("#nm-text").value }), t("notif.sent"));
      if (r) viewNotifications();
      return;
    }
    const typed = (toField.value || "").trim().toLowerCase();
    const matches = recipients.filter((r) =>
      r.label.toLowerCase().includes(typed) || (r.email || "").toLowerCase() === typed);
    const exact = recipients.find((r) => (r.email || "").toLowerCase() === typed);
    const target = exact || (matches.length === 1 ? matches[0] : null);
    if (!typed) { toast(t("notif.type_recipient")); return; }
    if (!target) {
      if (matches.length > 1) {
        const options = matches.map((x) => x.email || shortId(x.id)).join(", ");
        toast(`${t("notif.ambiguous")}: ${options}`, "error");
      } else {
        const r = await withErr(() => Api.users.sendMessageByEmail(typed, { message: $("#nm-text").value }), t("notif.sent"));
        if (r) viewNotifications();
        return;
      }
      return;
    }
    const r = await withErr(() => Api.users.sendMessage(target.id, { message: $("#nm-text").value }), t("notif.sent"));
    if (r) viewNotifications();
  };
  refreshNotifBadge();
}

/* ---------- workspaces ---------- */
async function viewWorkspaces() {
  const m = $("#main");
  const list = (await withErr(() => Api.workspaces.list())) || [];
  const pers = personalVisible() ? list.filter((w) => w.type === "PERSONAL") : [];
  const comp = list.filter((w) => w.type !== "PERSONAL");
  const card = (w) => `<div class="card"><h3>${esc(w.name)}</h3>
    <div><span class="tag ${w.type === "PERSONAL" ? "green" : "blue"}">${esc(w.type)}</span>
    ${w.company_id ? `<span class="tag">${esc((S.companies.find((c) => c.id === w.company_id) || {}).name || shortId(w.company_id))}</span>` : ""}</div>
    <div class="row"><button class="btn small danger" data-del-ws="${w.id}">${esc(t("ws.delete"))}</button></div>
    <div class="muted" style="font-size:12px;margin-top:6px">${esc(t("ws.note"))}</div></div>`;
  m.innerHTML = `<h2>${esc(t("ws.title"))}</h2>
    <h3>${esc(t("ws.personal"))}</h3><div class="grid">${pers.map(card).join("") || `<p class='muted'>${esc(t("ws.empty"))}</p>`}</div>
    <h3>${esc(t("ws.companies"))}</h3><div class="grid">${comp.map(card).join("") || `<p class='muted'>${esc(t("ws.empty"))}</p>`}</div>`;
  $$("[data-del-ws]").forEach((b) => (b.onclick = async () => {
    if (!confirm(t("ws.confirm"))) return;
    const r = await withErr(() => Api.workspaces.remove(b.dataset.delWs), t("ws.deleted"));
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
$$("#nav button").forEach((b) => {
  b.addEventListener("click", () => {
    const sb = document.querySelector(".sidebar");
    if (sb) sb.classList.remove("open");
    const target = "#/" + b.dataset.route;
    if (location.hash === target) route();
    else location.hash = target;
  });
});
$("#burger-btn").addEventListener("click", () => {
  const sb = document.querySelector(".sidebar");
  if (sb) sb.classList.toggle("open");
});
$$(".lang-sel").forEach((sel) => sel.addEventListener("change", onLangChange));
$$(".theme-toggle").forEach((b) => b.addEventListener("click", () => Theme.toggle()));
Theme.apply();
checkPendingJoin();
initAuth();
if (Api.token()) enterApp();
else showAuth();
