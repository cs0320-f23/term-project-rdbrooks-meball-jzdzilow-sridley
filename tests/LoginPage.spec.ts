import { test, expect } from "playwright/test";

test.beforeEach(async ({ page }) => {
  await page.goto("http://localhost:3000/");
});

test("LoginPage has the required ui components", async ({ page }) => {
  await expect(page.locator(".login-container")).toBeVisible();
  await expect(
    page.locator('h1:has-text("Welcome to Debugging Helper")')
  ).toBeVisible();
  await expect(
    page.locator('button:has-text("Sign In With Google")')
  ).toBeVisible();
});

test("LoginPage's mock login is functional", async ({ page }) => {
  await expect(
    page.locator('button:has-text("Sign In With Google")')
  ).toBeVisible();
  await page.click('button:has-text("Sign In With Google")');
  await page.fill('input[type="email"]', "test@example.com");
  await page.fill('input[type="password"]', "password123");
  await page.click('button:has-text("Login")');
  await page.waitForURL("your_app_url/role-selection");
});

test("LoginPage should handle mocked login successfully", async ({ page }) => {
  await page.fill('input[type="email"]', "test@example.com");
  await page.fill('input[type="password"]', "password123");
  await page.click('button:has-text("Login")');
  await page.waitForNavigation();
  expect(page.url()).toBe("http://localhost:3000/role-selection");
});
