/* API client for Task & Time Tracker backend (see ../api.yaml).
 * Backend speaks snake_case JSON. Errors look like {message, code}. */
const Api = (() => {
  const KEY = "ttt_token";
  // Same origin by default (frontend served by the backend itself);
  // override on the login screen when API lives elsewhere.
  const sameOrigin = typeof window !== "undefined" ? window.location.origin : "http://localhost:8080";
  let base = localStorage.getItem("ttt_base") || sameOrigin;

  const token = () => localStorage.getItem(KEY);
  const setToken = (t) => (t ? localStorage.setItem(KEY, t) : localStorage.removeItem(KEY));
  const setBase = (b) => { base = b.replace(/\/$/, ""); localStorage.setItem("ttt_base", base); };
  const getBase = () => base;

  function userId() {
    const t = token();
    if (!t) return null;
    try {
      return JSON.parse(atob(t.split(".")[1])).sub;
    } catch { return null; }
  }

  async function req(method, path, body) {
    const headers = { "Content-Type": "application/json" };
    if (token()) headers["Authorization"] = "Bearer " + token();
    const res = await fetch(base + path, {
      method,
      headers,
      body: body === undefined ? undefined : JSON.stringify(body),
    });
    const text = await res.text();
    let data = null;
    try { data = text ? JSON.parse(text) : null; } catch { data = text; }
    if (!res.ok) {
      const msg = (data && data.message) || `HTTP ${res.status}`;
      const err = new Error(msg);
      err.status = res.status;
      err.code = data && data.code;
      throw err;
    }
    return data;
  }

  const get = (p) => req("GET", p);
  const post = (p, b) => req("POST", p, b);
  const put = (p, b) => req("PUT", p, b);
  const del = (p) => req("DELETE", p);

  return {
    getBase, setBase, token, setToken, userId,
    auth: {
      login: (b) => post("/auth/login", b),
      signupPersonal: (b) => post("/auth/signup/personal", b),
      signupCompany: (b) => post("/auth/signup/company", b),
    },
    companies: {
      list: () => get("/companies"),
      create: (b) => post("/companies", b),
      get: (id) => get(`/companies/${id}`),
      update: (id, b) => put(`/companies/${id}`, b),
      remove: (id) => del(`/companies/${id}`),
      roles: (cid) => get(`/companies/${cid}/roles`),
      members: (cid) => get(`/companies/${cid}/members`),
      assignRole: (cid, b) => post(`/companies/${cid}/roles`, b),
      updateRole: (cid, rid, b) => put(`/companies/${cid}/roles/${rid}`, b),
      removeRole: (cid, rid) => del(`/companies/${cid}/roles/${rid}`),
    },
    users: {
      list: () => get("/users"),
      get: (id) => get(`/users/${id}`),
      update: (id, b) => put(`/users/${id}`, b),
      remove: (id) => del(`/users/${id}`),
      sendMessage: (uid, b) => post(`/users/${uid}/notifications`, b),
      sendMessageByEmail: (email, b) => post(`/users/messages`, { ...b, email }),
      sent: (uid) => get(`/users/${uid}/messages/sent`),
      deleteMessage: (uid, nid) => del(`/users/${uid}/notifications/${nid}`),
      deleteConversation: (uid, other) => del(`/users/${uid}/conversations/${other}`),
      upcoming: (uid, days = 7, size = 20) =>
        get(`/users/${uid}/upcoming?days=${days}&size=${size}`),
      notifications: (uid, page, size) => {
        const q = [];
        if (page !== undefined) q.push("page=" + page);
        if (size !== undefined) q.push("size=" + size);
        return get(`/users/${uid}/notifications${q.length ? "?" + q.join("&") : ""}`);
      },
      markRead: (uid, nid) => put(`/users/${uid}/notifications/${nid}/read`),
    },
    projects: {
      list: (q = {}) => {
        const p = new URLSearchParams();
        if (q.page !== undefined) p.set("page", q.page);
        if (q.size !== undefined) p.set("size", q.size);
        if (q.company_id) p.set("company_id", q.company_id);
        const s = p.toString();
        return get("/projects" + (s ? "?" + s : ""));
      },
      create: (b) => post("/projects", b),
      get: (id) => get(`/projects/${id}`),
      update: (id, b) => put(`/projects/${id}`, b),
      remove: (id) => del(`/projects/${id}`),
      members: (pid) => get(`/projects/${pid}/members`),
      addMember: (pid, b) => post(`/projects/${pid}/members`, b),
      removeMember: (pid, uid) => del(`/projects/${pid}/members/${uid}`),
      deadlines: (pid) => get(`/projects/${pid}/deadlines`),
      createDeadline: (pid, b) => post(`/projects/${pid}/deadlines`, b),
      updateDeadline: (pid, did, b) => put(`/projects/${pid}/deadlines/${did}`, b),
      deleteDeadline: (pid, did) => del(`/projects/${pid}/deadlines/${did}`),
    },
    tasks: {
      list: (q = {}) => {
        const p = new URLSearchParams();
        ["page", "size", "status", "project_id", "assigned_to"].forEach((k) => {
          if (q[k] !== undefined && q[k] !== "") p.set(k, q[k]);
        });
        const s = p.toString();
        return get("/tasks" + (s ? "?" + s : ""));
      },
      create: (b) => post("/tasks", b),
      get: (id) => get(`/tasks/${id}`),
      update: (id, b) => put(`/tasks/${id}`, b),
      remove: (id) => del(`/tasks/${id}`),
      comments: (tid) => get(`/tasks/${tid}/comments`),
      addComment: (tid, b) => post(`/tasks/${tid}/comments`, b),
      deleteComment: (tid, cid) => del(`/tasks/${tid}/comments/${cid}`),
      history: (tid) => get(`/tasks/${tid}/history`),
      timeEntries: (tid) => get(`/tasks/${tid}/time-entries`),
      addTimeEntry: (tid, b) => post(`/tasks/${tid}/time-entries`, b),
      attachments: (tid) => get(`/tasks/${tid}/attachments`),
      addAttachment: (tid, b) => post(`/tasks/${tid}/attachments`, b),
      updateAttachment: (tid, aid, b) => put(`/tasks/${tid}/attachments/${aid}`, b),
      deleteAttachment: (tid, aid) => del(`/tasks/${tid}/attachments/${aid}`),
      reminders: (tid) => get(`/tasks/${tid}/reminders`),
      addReminder: (tid, b) => post(`/tasks/${tid}/reminders`, b),
      deleteReminder: (tid, rid) => del(`/tasks/${tid}/reminders/${rid}`),
    },
    invites: {
      create: (b) => post("/invites", b),
      accept: (code) => post(`/invites/${encodeURIComponent(code)}/accept`, {}),
    },
    workspaces: {
      list: () => get("/workspaces"),
      remove: (id) => del(`/workspaces/${id}`),
    },
  };
})();
