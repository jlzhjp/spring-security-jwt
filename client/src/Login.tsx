import { useState } from "react";
import { useNavigate } from "react-router";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { toast } from "sonner";

import { Button } from "./components/ui/button";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "./components/ui/form";
import { Input } from "./components/ui/input";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "./components/ui/card";
import useBoundStore from "./lib/store";

// Form validation schema
const loginSchema = z.object({
  username: z.string().min(3, {
    message: "Username must be at least 3 characters.",
  }),
  password: z.string().min(1, {
    message: "Password is required.",
  }),
});

export default function Login() {
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const navigate = useNavigate();
  const login = useBoundStore((state) => state.login);

  const form = useForm<z.infer<typeof loginSchema>>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      username: "",
      password: "",
    },
  });

  async function onSubmit(values: z.infer<typeof loginSchema>) {
    setIsLoading(true);

    try {
      // Create Basic Authentication header
      const basicAuth = btoa(`${values.username}:${values.password}`);

      // Send request to auth endpoint
      const response = await fetch("/api/auth/authorize", {
        method: "POST",
        headers: {
          Authorization: `Basic ${basicAuth}`,
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || "Authentication failed");
      }

      const data = await response.json();

      // Store the token in our Zustand store
      login(data.accessToken);

      toast.success("Login successful", {
        description: "You have been successfully logged in.",
      });

      // Redirect to home page or dashboard
      navigate("/");
    } catch (error) {
      toast.error("Login failed", {
        description:
          error instanceof Error
            ? error.message
            : "Invalid username or password.",
      });
    } finally {
      setIsLoading(false);
    }
  }

  return (
    <div className="flex justify-center items-center min-h-screen bg-gray-50">
      <Card className="w-full max-w-md">
        <CardHeader>
          <CardTitle className="text-2xl font-bold text-center">
            Login to your account
          </CardTitle>
          <CardDescription className="text-center">
            Enter your credentials to access your account
          </CardDescription>
        </CardHeader>
        <CardContent>
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
              <FormField
                control={form.control}
                name="username"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Username</FormLabel>
                    <FormControl>
                      <Input placeholder="Enter your username" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="password"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Password</FormLabel>
                    <FormControl>
                      <Input
                        placeholder="Enter your password"
                        type="password"
                        {...field}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <Button type="submit" className="w-full" disabled={isLoading}>
                {isLoading ? "Logging in..." : "Login"}
              </Button>
            </form>
          </Form>
        </CardContent>
        <CardFooter className="flex justify-center">
          <p className="text-sm text-gray-600">
            Don't have an account?{" "}
            <Button
              variant="link"
              className="p-0"
              onClick={() => navigate("/register")}
            >
              Register
            </Button>
          </p>
        </CardFooter>
      </Card>
    </div>
  );
}
