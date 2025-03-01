import { create, StateCreator } from "zustand";
import { persist } from "zustand/middleware";
import { jwtDecode } from "jwt-decode";

// Define the JWT payload type based on your token structure
interface JwtPayload {
  sub: string; // usually contains the username
  exp: number; // expiration timestamp
  role: string | string[]; // user role(s) as received from server
  // Add other claims your JWT contains
}

interface UserInfo {
  id: string;
  roles: string[]; // Changed to always be an array
  exp: number;
}

// Auth slice of the store
interface AuthSlice {
  token: string | null;
  user: UserInfo | null;
  isAuthenticated: boolean;
  login: (token: string) => void;
  logout: () => void;
  isTokenValid: () => boolean;
  hasRole: (role: string) => boolean;
}

// Define the complete store type
// eslint-disable-next-line @typescript-eslint/no-empty-object-type
interface StoreState extends AuthSlice {
  // Add more slices here when needed
}

// Create auth slice
const createAuthSlice: StateCreator<StoreState, [], [], AuthSlice> = (
  set,
  get
) => ({
  token: null,
  user: null,
  isAuthenticated: false,

  login: (token) => {
    try {
      const decoded = jwtDecode<JwtPayload>(token);
      const user: UserInfo = {
        id: decoded.sub,
        // Always convert role to array
        roles: Array.isArray(decoded.role)
          ? decoded.role
          : [decoded.role || "user"],
        exp: decoded.exp,
      };

      set({ token, user, isAuthenticated: true });
    } catch (error) {
      console.error("Invalid JWT token", error);
      set({ token: null, user: null, isAuthenticated: false });
    }
  },

  logout: () => set({ token: null, user: null, isAuthenticated: false }),

  isTokenValid: () => {
    const { user } = get();
    if (!user) return false;

    // Check if token is expired (convert exp to milliseconds)
    return user.exp * 1000 > Date.now();
  },

  // New helper method to check if user has a specific role
  hasRole: (role) => {
    const { user } = get();
    if (!user) return false;
    return user.roles.includes(role);
  },
});

// Create the store with persistence
const useBoundStore = create<StoreState>()(
  persist(
    (...a) => ({
      ...createAuthSlice(...a),
      // Add more slices here
    }),
    {
      name: "app-storage",
      partialize: (state) => ({
        token: state.token,
        user: state.user,
        isAuthenticated: state.isAuthenticated,
      }),
    }
  )
);

export default useBoundStore;
