const CONFIG = {
  BASE_URL: "http://localhost:8080",

  ENDPOINTS: {
    LOGIN: "/api/auth/login",
    REGISTER: "/api/auth/register",

    WISHES: "/api/wishes",
    WISH_BY_ID: (id) => `/api/wishes/${id}`,

    BENEFICIARIES: "/api/beneficiaries",
    GRANT_ACCESS: (wishId) => `/api/wishes/${wishId}/access`,

    // VERIFICATION
    VERIFICATIONS: "/api/verification-requests/mine",
    CREATE_VERIFICATION: (wishId) =>
      `/api/wishes/${wishId}/verification-requests`,
    VERIFICATION_DECISION: (id) =>
      `/api/verification-requests/${id}/decision`,

    AUDIT_LOGS: (wishId) => `/api/audit-logs/${wishId}`,
  },

  FALLBACK_ACCESS_LEVELS: ["VIEW", "DOWNLOAD"],
};