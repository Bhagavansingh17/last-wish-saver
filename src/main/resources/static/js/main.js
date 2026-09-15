/* ===================== TOASTS ===================== */
function toast(message, type = "info") {
  const container = document.getElementById("toast-container");
  const el = document.createElement("div");
  el.className = `toast toast-${type}`;
  el.textContent = message;
  container.appendChild(el);
  setTimeout(() => el.remove(), 3500);
}

function showLoading(tbody, colSpan) {
  tbody.innerHTML = `<tr class="empty-row"><td colspan="${colSpan}">Loading…</td></tr>`;
}
function showEmpty(tbody, colSpan, text = "No data yet.") {
  tbody.innerHTML = `<tr class="empty-row"><td colspan="${colSpan}">${text}</td></tr>`;
}

/* ===================== AUTH ===================== */
function isAuthed() {
  return !!localStorage.getItem("jwt");
}

function saveSession(data) {
  // Tolerates a few common response shapes: {token,user} / {jwt,user} / {accessToken,...}
  const token = data.token || data.jwt || data.accessToken;
  if (token) localStorage.setItem("jwt", token);
  if (data.user) localStorage.setItem("user", JSON.stringify(data.user));
  else if (data.name || data.email) localStorage.setItem("user", JSON.stringify(data));
}

async function handleLogin(e) {
    e.preventDefault();

    const email = document.getElementById("login-email").value.trim();
    const password = document.getElementById("login-password").value;
    const role = document.getElementById("login-role").value;

    try {
        const data = await api.post(
            CONFIG.ENDPOINTS.LOGIN,
            {
                email: email,
                password: password
            }
        );

        saveSession(data);

        // Store selected login role for frontend routing
        localStorage.setItem("role", role);

        toast("Login successful.", "success");

        enterApp();

    } catch (err) {
        toast(err.message || "Login failed.", "error");
    }
}

async function handleRegister(e) {
  e.preventDefault();
  const name = document.getElementById("register-name").value.trim();
  const email = document.getElementById("register-email").value.trim();
  const password = document.getElementById("register-password").value;
  try {
    const data = await api.post(CONFIG.ENDPOINTS.REGISTER, { name, email, password }, { auth: false });
    if (data && (data.token || data.jwt || data.accessToken)) {
      saveSession(data);
      toast("Account created.", "success");
      enterApp();
    } else {
      toast("Account created. Please log in.", "success");
      document.querySelector('.tab-btn[data-tab="login"]').click();
    }
  } catch (err) {
    toast(err.message, "error");
  }
}

function handleLogout() {
  localStorage.removeItem("jwt");
  localStorage.removeItem("user");
  window.location.hash = "#/login";
  showAuthView();
}

/* ===================== VIEW SWITCH ===================== */
function showAuthView() {
  document.getElementById("auth-view").classList.remove("hidden");
  document.getElementById("app-view").classList.add("hidden");
}

function enterApp() {
  document.getElementById("auth-view").classList.add("hidden");
  document.getElementById("app-view").classList.remove("hidden");
  const user = JSON.parse(localStorage.getItem("user") || "{}");
  document.getElementById("user-chip").textContent = user.name || user.email || "Account";
  if (!window.location.hash || window.location.hash === "#/login") {
    window.location.hash = "#/dashboard";
  }
  router();
}

/* ===================== ROUTER ===================== */
const ROUTES = ["dashboard", "wishes", "beneficiaries", "access", "verification", "audit"];
const PAGE_TITLES = {
  dashboard: "Dashboard",
  wishes: "My Wishes",
  beneficiaries: "Beneficiaries",
  access: "Access Management",
  verification: "Verification",
  audit: "Audit Logs",
};

function router() {
  if (!isAuthed()) { showAuthView(); return; }
  let route = (window.location.hash || "#/dashboard").replace("#/", "");
  if (!ROUTES.includes(route)) route = "dashboard";

  ROUTES.forEach((r) => {
    document.getElementById(`page-${r}`).classList.toggle("hidden", r !== route);
    const link = document.querySelector(`.sidebar nav a[data-nav="${r}"]`);
    if (link) link.classList.toggle("active", r === route);
  });
  document.getElementById("page-title").textContent = PAGE_TITLES[route];

  if (route === "dashboard") loadDashboard();
  if (route === "wishes") loadWishes();
  if (route === "beneficiaries") loadBeneficiaries();
  if (route === "access") loadAccessLevels();
  if (route === "verification") loadVerifications();
  // audit loads on demand (needs a wish id)
}

/* ===================== DASHBOARD ===================== */
async function loadDashboard() {
  const wishesBody = document.getElementById("recent-activity-body");
  showLoading(wishesBody, 3);
  try {
    const [wishes, beneficiaries, verifications] = await Promise.all([
      api.get(CONFIG.ENDPOINTS.WISHES).catch(() => []),
      api.get(CONFIG.ENDPOINTS.BENEFICIARIES).catch(() => []),
      api.get(CONFIG.ENDPOINTS.VERIFICATIONS).catch(() => []),
    ]);

    document.getElementById("stat-wishes").textContent = Array.isArray(wishes) ? wishes.length : "–";
    document.getElementById("stat-beneficiaries").textContent = Array.isArray(beneficiaries) ? beneficiaries.length : "–";
    document.getElementById("stat-verifications").textContent = Array.isArray(verifications) ? verifications.length : "–";

    const recent = [
      ...(Array.isArray(wishes) ? wishes.map(w => ({ type: "Wish", title: w.title, date: w.createdAt || w.updatedAt })) : []),
      ...(Array.isArray(verifications) ? verifications.map(v => ({ type: "Verification", title: `Request #${v.id}`, date: v.createdAt })) : []),
    ]
      .filter(x => x.date)
      .sort((a, b) => new Date(b.date) - new Date(a.date))
      .slice(0, 5);

    if (recent.length === 0) {
      showEmpty(wishesBody, 3, "No recent activity.");
    } else {
      wishesBody.innerHTML = recent.map(r => `
        <tr><td>${r.type}</td><td>${escapeHtml(r.title || "")}</td><td>${formatDate(r.date)}</td></tr>
      `).join("");
    }
  } catch (err) {
    toast(err.message, "error");
    showEmpty(wishesBody, 3, "Couldn't load activity.");
  }
}

/* ===================== WISHES ===================== */
async function loadWishes() {
  const body = document.getElementById("wishes-body");
  showLoading(body, 6);
  try {
    const wishes = await api.get(CONFIG.ENDPOINTS.WISHES);
    if (!Array.isArray(wishes) || wishes.length === 0) {
      showEmpty(body, 6);
      return;
    }
    body.innerHTML = wishes.map(w => `
      <tr>
        <td>${w.id}</td>
        <td>${escapeHtml(w.title || "")}</td>
        <td>${escapeHtml(truncate(w.content || "", 60))}</td>
        <td>${statusBadge(w.status)}</td>
        <td>${formatDate(w.createdAt)}</td>
        <td>${formatDate(w.updatedAt)}</td>
      </tr>
    `).join("");
  } catch (err) {
    toast(err.message, "error");
    showEmpty(body, 6, "Couldn't load wishes.");
  }
}

async function handleCreateWish(e) {
  e.preventDefault();
  const title = document.getElementById("wish-title").value.trim();
  const content = document.getElementById("wish-content").value.trim();
  try {
    await api.post(CONFIG.ENDPOINTS.WISHES, { title, content });
    toast("Wish created.", "success");
    e.target.reset();
    loadWishes();
  } catch (err) {
    toast(err.message, "error");
  }
}

/* ===================== BENEFICIARIES ===================== */
async function loadBeneficiaries() {
  const body = document.getElementById("beneficiaries-body");
  showLoading(body, 4);
  try {
    const list = await api.get(CONFIG.ENDPOINTS.BENEFICIARIES);
    if (!Array.isArray(list) || list.length === 0) {
      showEmpty(body, 4);
      return;
    }
    body.innerHTML = list.map(b => `
      <tr>
        <td>${b.id}</td>
        <td>${escapeHtml(b.name || "")}</td>
        <td>${escapeHtml(b.email || "")}</td>
        <td>${escapeHtml(b.relationship || "")}</td>
      </tr>
    `).join("");
  } catch (err) {
    toast(err.message, "error");
    showEmpty(body, 4, "Couldn't load beneficiaries.");
  }
}

async function handleCreateBeneficiary(e) {
  e.preventDefault();
  const name = document.getElementById("ben-name").value.trim();
  const email = document.getElementById("ben-email").value.trim();
  const relationship = document.getElementById("ben-relationship").value.trim();
  try {
    await api.post(CONFIG.ENDPOINTS.BENEFICIARIES, { name, email, relationship });
    toast("Beneficiary added.", "success");
    e.target.reset();
    loadBeneficiaries();
  } catch (err) {
    toast(err.message, "error");
  }
}

/* ===================== ACCESS MANAGEMENT ===================== */
function loadAccessLevels() {
  const select = document.getElementById("access-level");

  select.innerHTML =
    CONFIG.FALLBACK_ACCESS_LEVELS
      .map(level => `<option value="${level}">${level}</option>`)
      .join("");
}

async function handleGrantAccess(e) {
  e.preventDefault();
  const wishId = document.getElementById("access-wish-id").value.trim();
  const beneficiaryId = document.getElementById("access-ben-id").value.trim();
  const accessLevel = document.getElementById("access-level").value;
  try {
    await api.post(CONFIG.ENDPOINTS.GRANT_ACCESS(wishId), { beneficiaryId, accessLevel });
    toast("Access granted.", "success");
    e.target.reset();
    loadAccessLevels();
  } catch (err) {
    toast(err.message, "error");
  }
}

/* ===================== VERIFICATION ===================== */
async function loadVerifications() {
  const body = document.getElementById("verifications-body");
  showLoading(body, 7);
  try {
    const list = await api.get(CONFIG.ENDPOINTS.VERIFICATIONS);
    if (!Array.isArray(list) || list.length === 0) {
      showEmpty(body, 7);
      return;
    }
    body.innerHTML = list.map(v => `
      <tr>
        <td>${v.id}</td>
        <td>${v.wish?.id ?? ""}</td>
        <td>${v.beneficiary?.id ?? ""}</td>
        <td>${statusBadge(v.status)}</td>
        <td>${escapeHtml(v.note || "")}</td>
        <td>${formatDate(v.createdAt)}</td>
        <td>
          <button class="btn-small btn-approve" data-approve="${v.id}">Approve</button>
          <button class="btn-small btn-reject" data-reject="${v.id}">Reject</button>
        </td>
      </tr>
    `).join("");

    body.querySelectorAll("[data-approve]").forEach(btn =>
      btn.addEventListener("click", () => updateVerification(btn.dataset.approve, "approve"))
    );
    body.querySelectorAll("[data-reject]").forEach(btn =>
      btn.addEventListener("click", () => updateVerification(btn.dataset.reject, "reject"))
    );
  } catch (err) {
    toast(err.message, "error");
    showEmpty(body, 7, "Couldn't load verification requests.");
  }
}

async function updateVerification(id, action) {
  try {
    const approve = action === "approve";

    await api.post(
      CONFIG.ENDPOINTS.VERIFICATION_DECISION(id),
      {
        approve: approve,
        decisionNote: approve
          ? "Approved by owner"
          : "Rejected by owner"
      }
    );

    toast(
      approve ? "Request approved." : "Request rejected.",
      "success"
    );

    loadVerifications();

  } catch (err) {
    console.error("Decision error:", err);
    toast(err.message || "Unable to update request.", "error");
  }
}

async function handleCreateVerification(e) {
  e.preventDefault();

  const wishId = document.getElementById("ver-wish-id").value.trim();
  const beneficiaryId = document.getElementById("ver-ben-id").value.trim();
  const note = document.getElementById("ver-note").value.trim();

  if (!wishId || !beneficiaryId) {
    toast("Wish ID and Beneficiary ID are required.", "error");
    return;
  }

  try {
    await api.post(
      CONFIG.ENDPOINTS.CREATE_VERIFICATION(wishId),
      {
        beneficiaryId: Number(beneficiaryId),
        note: note
      }
    );

    toast("Verification request submitted.", "success");

    e.target.reset();
    loadVerifications();

  } catch (err) {
    console.error("Verification error:", err);
    toast(err.message || "Verification request failed.", "error");
  }
}

/* ===================== AUDIT LOGS ===================== */
async function handleLoadAudit(e) {
  e.preventDefault();
  const wishId = document.getElementById("audit-wish-id").value.trim();
  const body = document.getElementById("audit-body");
  showLoading(body, 4);
  try {
    const logs = await api.get(CONFIG.ENDPOINTS.AUDIT_LOGS(wishId));
    if (!Array.isArray(logs) || logs.length === 0) {
      showEmpty(body, 4);
      return;
    }
    body.innerHTML = logs.map(l => `
      <tr>
        <td>${escapeHtml(l.action || "")}</td>
        <td>${escapeHtml(l.entity || "")}</td>
        <td>${escapeHtml(l.details || "")}</td>
        <td>${formatDate(l.timestamp)}</td>
      </tr>
    `).join("");
  } catch (err) {
    toast(err.message, "error");
    showEmpty(body, 4, "Couldn't load audit logs.");
  }
}

/* ===================== HELPERS ===================== */
function statusBadge(status) {
  if (!status) return `<span class="badge badge-default">–</span>`;
  const s = String(status).toUpperCase();
  const cls = s.includes("APPROV") ? "badge-approved"
    : s.includes("REJECT") ? "badge-rejected"
    : s.includes("PEND") ? "badge-pending"
    : "badge-default";
  return `<span class="badge ${cls}">${escapeHtml(status)}</span>`;
}

function formatDate(d) {
  if (!d) return "–";
  const date = new Date(d);
  if (isNaN(date.getTime())) return escapeHtml(String(d));
  return date.toLocaleString();
}

function truncate(str, len) {
  return str.length > len ? str.slice(0, len) + "…" : str;
}

function escapeHtml(str) {
  const div = document.createElement("div");
  div.textContent = str;
  return div.innerHTML;
}

/* ===================== INIT ===================== */
document.addEventListener("DOMContentLoaded", () => {
  // Auth tabs
  document.querySelectorAll(".tab-btn").forEach(btn => {
    btn.addEventListener("click", () => {
      document.querySelectorAll(".tab-btn").forEach(b => b.classList.remove("active"));
      document.querySelectorAll(".tab-panel").forEach(p => p.classList.remove("active"));
      btn.classList.add("active");
      document.getElementById(`${btn.dataset.tab}-form`).classList.add("active");
    });
  });

  document.getElementById("login-form").addEventListener("submit", handleLogin);
  document.getElementById("register-form").addEventListener("submit", handleRegister);
  document.getElementById("logout-btn").addEventListener("click", handleLogout);

  document.getElementById("wish-form").addEventListener("submit", handleCreateWish);
  document.getElementById("beneficiary-form").addEventListener("submit", handleCreateBeneficiary);
  document.getElementById("access-form").addEventListener("submit", handleGrantAccess);
  document.getElementById("verification-form").addEventListener("submit", handleCreateVerification);
  document.getElementById("audit-form").addEventListener("submit", handleLoadAudit);

  window.addEventListener("hashchange", router);

  if (isAuthed()) {
    enterApp();
  } else {
    showAuthView();
  }
});
