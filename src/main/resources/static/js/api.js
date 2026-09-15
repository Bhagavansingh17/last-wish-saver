/**
 * Central fetch wrapper.
 * - Attaches JWT automatically
 * - Normalizes errors for every endpoint
 * - Forces logout on 401
 */

const ERROR_MESSAGES = {
  400: "That request wasn't valid. Check the fields and try again.",
  401: "Your session has expired. Please log in again.",
  403: "You don't have permission to do that.",
  404: "That item couldn't be found.",
  409: "That already exists or conflicts with existing data.",
  500: "Something went wrong on the server. Try again shortly.",
};

async function apiRequest(path, { method = "GET", body, auth = true } = {}) {
  const headers = { "Content-Type": "application/json" };

  if (auth) {
    const token = localStorage.getItem("jwt");
    if (token) headers["Authorization"] = `Bearer ${token}`;
  }

  let res;
  try {
    res = await fetch(`${CONFIG.BASE_URL}${path}`, {
      method,
      headers,
      body: body !== undefined ? JSON.stringify(body) : undefined,
    });
  } catch (networkErr) {
    throw new Error(
      "Can't reach the backend. Is it running at " + CONFIG.BASE_URL + "?"
    );
  }

  if (res.status === 401 || res.status === 403) {
    if (res.status === 401) {
      localStorage.removeItem("jwt");
      localStorage.removeItem("user");
      window.location.hash = "#/login";
      throw new Error(ERROR_MESSAGES[401]);
    }
    throw new Error(ERROR_MESSAGES[403]);
  }

  if (!res.ok) {
    const message = ERROR_MESSAGES[res.status] || `Request failed (${res.status}).`;
    // Try to surface a backend-provided message without leaking stack traces
    try {
      const data = await res.json();
      if (data && (data.message || data.error)) {
        throw new Error(data.message || data.error);
      }
    } catch (_) {
      // fall through to generic message
    }
    throw new Error(message);
  }

  if (res.status === 204) return null;

  const contentType = res.headers.get("content-type") || "";
  if (contentType.includes("application/json")) {
    return res.json();
  }
  return null;
}

const api = {
  get: (path, opts) => apiRequest(path, { ...opts, method: "GET" }),
  post: (path, body, opts) => apiRequest(path, { ...opts, method: "POST", body }),
  put: (path, body, opts) => apiRequest(path, { ...opts, method: "PUT", body }),
  del: (path, opts) => apiRequest(path, { ...opts, method: "DELETE" }),
};
