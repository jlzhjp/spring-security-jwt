import { Link, Outlet } from "react-router";
import { Button } from "./components/ui/button";
import { Sheet, SheetContent, SheetTrigger } from "./components/ui/sheet";
import { Menu } from "lucide-react";
import useBoundStore from "./lib/store";
import { Suspense } from "react";
import { useUserInfo } from "./lib/userInfo";
import { createPortal } from "react-dom";
import { Toaster } from "sonner";

// Component to display the user greeting with username
function UserGreeting() {
  const { data } = useUserInfo();
  return <span className="mr-2">Welcome, {data.username}</span>;
}

function Layout() {
  const isAuthenticated = useBoundStore((state) => state.isAuthenticated);
  const logout = useBoundStore((state) => state.logout);

  return (
    <div className="container mx-auto">
      <header className="border-b">
        <div className="flex h-16 items-center px-4 justify-between">
          {/* Logo/Brand */}
          <div className="font-bold text-xl">Spring Security JWT Example</div>

          {/* Desktop Navigation */}
          <nav className="hidden md:flex gap-4">
            <Button variant="link" asChild>
              <Link to="/">Home</Link>
            </Button>
            <Button variant="link" asChild>
              <Link to="/about">About</Link>
            </Button>
          </nav>

          {/* Auth Buttons */}
          <div className="hidden md:flex gap-2 items-center">
            {isAuthenticated ? (
              <>
                <Suspense
                  fallback={<span className="mr-2">Loading user...</span>}
                >
                  <UserGreeting />
                </Suspense>
                <Button variant="outline" onClick={logout}>
                  Logout
                </Button>
              </>
            ) : (
              <>
                <Button variant="outline" asChild>
                  <Link to="/login">Login</Link>
                </Button>
                <Button asChild>
                  <Link to="/register">Register</Link>
                </Button>
              </>
            )}
          </div>

          {/* Mobile Navigation */}
          <Sheet>
            <SheetTrigger asChild className="md:hidden">
              <Button variant="outline" size="icon">
                <Menu className="h-5 w-5" />
              </Button>
            </SheetTrigger>
            <SheetContent>
              <nav className="flex flex-col gap-4 mt-8">
                <Button variant="link" asChild>
                  <Link to="/">Home</Link>
                </Button>
                <Button variant="link" asChild>
                  <Link to="/about">About</Link>
                </Button>
                <div className="flex flex-col gap-2 mt-4">
                  {isAuthenticated ? (
                    <>
                      <Suspense
                        fallback={<span className="mb-2">Loading user...</span>}
                      >
                        <span className="mb-2">
                          <UserGreeting />
                        </span>
                      </Suspense>
                      <Button variant="outline" onClick={logout}>
                        Logout
                      </Button>
                    </>
                  ) : (
                    <>
                      <Button variant="outline" asChild>
                        <Link to="/login">Login</Link>
                      </Button>
                      <Button type="button" asChild>
                        <Link to="/register">Register</Link>
                      </Button>
                    </>
                  )}
                </div>
              </nav>
            </SheetContent>
          </Sheet>
        </div>
      </header>
      {/* Page content rendered via Outlet */}
      <main className="py-8">
        <Outlet />
      </main>
      {createPortal(<Toaster position="top-right" richColors />, document.body)}
    </div>
  );
}

export default Layout;
