import { useState } from "react";
import { useNavigate } from "react-router";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { toast, Toaster } from "sonner";

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
import { createPortal } from "react-dom";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "./components/ui/select";
import api from "./lib/fetcher";
import useBoundStore from "./lib/store";

const formSchema = z
  .object({
    username: z.string().min(3, {
      message: "Username must be at least 3 characters.",
    }),
    role: z.enum(["user", "admin"], {
      required_error: "Please select a role.",
    }),
    password: z.string().min(8, {
      message: "Password must be at least 8 characters.",
    }),
    confirmPassword: z.string(),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: "Passwords don't match",
    path: ["confirmPassword"],
  });

export default function Register() {
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const navigate = useNavigate();
  const { login } = useBoundStore();

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      username: "",
      role: "user",
      password: "",
      confirmPassword: "",
    },
  });

  async function onSubmit(values: z.infer<typeof formSchema>) {
    setIsLoading(true);

    try {
      // Using our API utility with skipAuth since registration is public
      const { accessToken } = await api.post(
        "/api/auth/register",
        {
          username: values.username,
          role: values.role,
          password: values.password,
        },
        { skipAuth: true }
      );

      // Store the token in the app state
      login(accessToken);

      toast.success("Registration successful", {
        description: "You've been registered and logged in successfully.",
      });

      // Navigate to home/dashboard instead of login since user is now logged in
      navigate("/");
    } catch (error) {
      toast.error("Registration failed", {
        description:
          error instanceof Error
            ? error.message
            : "Something went wrong. Please try again.",
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
            Create an account
          </CardTitle>
          <CardDescription className="text-center">
            Enter your information to create an account
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
                name="role"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Role</FormLabel>
                    <Select
                      onValueChange={field.onChange}
                      defaultValue={field.value}
                    >
                      <FormControl>
                        <SelectTrigger>
                          <SelectValue placeholder="Select a role" />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        <SelectItem value="user">User</SelectItem>
                        <SelectItem value="admin">Admin</SelectItem>
                      </SelectContent>
                    </Select>
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
                        placeholder="Create a password"
                        type="password"
                        {...field}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="confirmPassword"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Confirm Password</FormLabel>
                    <FormControl>
                      <Input
                        placeholder="Confirm your password"
                        type="password"
                        {...field}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <Button type="submit" className="w-full" disabled={isLoading}>
                {isLoading ? "Registering..." : "Register"}
              </Button>
            </form>
          </Form>
        </CardContent>
        <CardFooter className="flex justify-center">
          <p className="text-sm text-gray-600">
            Already have an account?{" "}
            <Button
              variant="link"
              className="p-0"
              onClick={() => navigate("/login")}
            >
              Log in
            </Button>
          </p>
        </CardFooter>
      </Card>
      {createPortal(<Toaster position="top-right" richColors />, document.body)}
    </div>
  );
}
