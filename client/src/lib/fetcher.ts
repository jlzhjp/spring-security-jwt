/* eslint-disable @typescript-eslint/no-explicit-any */
import useBoundStore from "./store";

// Type for request options extending fetch's RequestInit
interface FetcherOptions extends RequestInit {
  // Add any custom options here if needed
  skipAuth?: boolean;
}

// Type for API response
type ApiResponse<T = any> = Promise<T>;

/**
 * Fetch wrapper that automatically adds authentication token to requests
 */
export const fetchWithAuth = async <T = any>(
  url: string,
  options: FetcherOptions = {}
): ApiResponse<T> => {
  const { skipAuth, ...fetchOptions } = options;

  // Get token from store
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
    // Check for token expiration errors
    if (
      error instanceof Error &&
      (error.message.includes("401") ||
        error.message.toLowerCase().includes("unauthorized"))
    ) {
      // Auto logout on auth errors
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
