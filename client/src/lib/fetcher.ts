/* eslint-disable @typescript-eslint/no-explicit-any */

import useBoundStore from "./store";

// Type for request options extending fetch's RequestInit
interface FetcherOptions extends RequestInit {
  // Add any custom options here if needed
  skipAuth?: boolean;
  isRetry?: boolean; // To prevent infinite refresh loops
}

// Type for API response
type ApiResponse<T = any> = Promise<T>;

/**
 * Try to refresh the auth token
 */
const refreshAuthToken = async (): Promise<string> => {
  const refreshResponse = await fetch("/api/auth/token/refresh", {
    method: "POST",
    credentials: "include", // Include cookies for refresh token
  });

  if (!refreshResponse.ok) {
    throw new Error("Token refresh failed");
  }

  // Get the new access token
  const refreshData = await refreshResponse.json();
  const { accessToken } = refreshData;

  // Update the token in the store
  useBoundStore.getState().login(accessToken);
  return accessToken;
};

/**
 * Fetch wrapper that automatically adds authentication token to requests
 */
export const fetchWithAuth = async <T = any>(
  url: string,
  options: FetcherOptions = {}
): ApiResponse<T> => {
  const { skipAuth, isRetry = false, ...fetchOptions } = options;

  // Check if token is expired before making the request
  if (!skipAuth && !isRetry && !url.includes("/api/auth/token/refresh")) {
    const store = useBoundStore.getState();
    const token = store.token;
    const user = store.user;

    // If we have a token and user info, check expiration
    if (token && user && user.exp * 1000 <= Date.now()) {
      try {
        // Token is expired, try to refresh it before proceeding
        await refreshAuthToken();
      } catch {
        // If refresh fails, logout and abort the request
        store.logout();
        throw new Error("Your session has expired. Please log in again.");
      }
    }
  }

  // Get possibly refreshed token
  const token = useBoundStore.getState().token;

  // Prepare headers
  const headers = new Headers(fetchOptions.headers || {});

  // Add Authorization header if token exists and skipAuth is not true
  if (token && !skipAuth) {
    headers.set("Authorization", `Bearer ${token}`);
  }

  // Add default Content-Type if not set and method is not GET
  if (
    !headers.has("Content-Type") &&
    options.method !== "GET" &&
    options.body &&
    !(options.body instanceof FormData)
  ) {
    headers.set("Content-Type", "application/json");
  }

  try {
    // Execute the fetch
    const response = await fetch(url, {
      ...fetchOptions,
      headers,
    });

    // Check for unsuccessful responses
    if (!response.ok) {
      // Try to refresh token on 401 errors (but not for refresh requests or retries)
      // This is a fallback for cases where the token might be invalidated for reasons other than expiration
      if (
        response.status === 401 &&
        !isRetry &&
        !skipAuth &&
        !url.includes("/api/auth/token/refresh")
      ) {
        try {
          // Attempt to refresh the token
          await refreshAuthToken();

          // Retry the original request with the new token
          return fetchWithAuth<T>(url, {
            ...options,
            isRetry: true, // Mark as a retry to prevent loops
          });
        } catch {
          // If refresh fails, log out the user
          useBoundStore.getState().logout();
          throw new Error("Your session has expired. Please log in again.");
        }
      }

      // Try to parse error as JSON
      const errorData = await response.json().catch(() => ({}));
      throw new Error(
        errorData.message || `Request failed with status: ${response.status}`
      );
    }

    // Return empty response for 204 No Content
    if (response.status === 204) {
      return {} as T;
    }

    // Try to parse as JSON, or return raw response
    const contentType = response.headers.get("Content-Type");
    if (contentType && contentType.includes("application/json")) {
      return (await response.json()) as T;
    }

    return response as unknown as T;
  } catch (error) {
    // We already handled auth errors above, but double-check
    // for token expiration messages from server for other requests
    if (
      !isRetry &&
      error instanceof Error &&
      (error.message.includes("401") ||
        error.message.toLowerCase().includes("unauthorized") ||
        error.message.toLowerCase().includes("expired"))
    ) {
      // Auto logout on auth errors that weren't handled by our refresh
      useBoundStore.getState().logout();
    }
    throw error;
  }
};

/**
 * API helper methods for common HTTP operations
 */
export const api = {
  get: <T = any>(
    url: string,
    options?: Omit<FetcherOptions, "method" | "body">
  ): ApiResponse<T> => fetchWithAuth<T>(url, { ...options, method: "GET" }),

  post: <T = any>(
    url: string,
    data?: unknown,
    options?: Omit<FetcherOptions, "method" | "body">
  ): ApiResponse<T> =>
    fetchWithAuth<T>(url, {
      ...options,
      method: "POST",
      body: data ? JSON.stringify(data) : undefined,
    }),

  put: <T = any>(
    url: string,
    data?: unknown,
    options?: Omit<FetcherOptions, "method" | "body">
  ): ApiResponse<T> =>
    fetchWithAuth<T>(url, {
      ...options,
      method: "PUT",
      body: data ? JSON.stringify(data) : undefined,
    }),

  patch: <T = any>(
    url: string,
    data?: unknown,
    options?: Omit<FetcherOptions, "method" | "body">
  ): ApiResponse<T> =>
    fetchWithAuth<T>(url, {
      ...options,
      method: "PATCH",
      body: data ? JSON.stringify(data) : undefined,
    }),

  delete: <T = any>(
    url: string,
    options?: Omit<FetcherOptions, "method" | "body">
  ): ApiResponse<T> => fetchWithAuth<T>(url, { ...options, method: "DELETE" }),

  // Upload files with FormData
  upload: <T = any>(
    url: string,
    formData: FormData,
    options?: Omit<FetcherOptions, "method" | "body">
  ): ApiResponse<T> =>
    fetchWithAuth<T>(url, {
      ...options,
      method: "POST",
      body: formData,
      // Don't set Content-Type header - browser will set it with boundary
    }),
};

export default api;
