import { useSuspenseQuery } from "@tanstack/react-query";
import api from "./fetcher";

// Define the user info response type
interface UserInfoResponse {
  id: string;
  username: string;
}

/**
 * Hook for fetching current user information using React Suspense
 *
 * Usage:
 * ```tsx
 * // In parent component:
 * <Suspense fallback={<LoadingSpinner />}>
 *   <UserProfile />
 * </Suspense>
 *
 * // In child component:
 * function UserProfile() {
 *   const { data } = useUserInfo();
 *   return <div>Hello, {data.username}</div>
 * }
 * ```
 */
export const useUserInfo = () => {
  return useSuspenseQuery({
    queryKey: ["userInfo"],
    queryFn: () => api.get<UserInfoResponse>("/api/auth/me"),
    // Only fetch when user is authenticated
    staleTime: 5 * 60 * 1000, // Consider data fresh for 5 minutes
  });
};

export default useUserInfo;
