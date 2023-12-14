import { test, expect } from "playwright/test";

test.beforeEach(async ({ page }) => {
  await page.goto("http://localhost:3000/");
});

test("LoginPage has the required ui components on regular mode", async ({
  page,
}) => {
  await expect(page.locator(".login-container")).toBeVisible();
  await expect(
    page.locator('h1:has-text("Welcome to Debugging Helper")')
  ).toBeVisible();
  await expect(
    page.locator('button:has-text("Sign In With Google")')
  ).toBeVisible();
  await expect(page.locator('button:has-text("Mocked Mode")')).toBeVisible();
});

test("LoginPage has the required ui components on mocked mode", async ({
  page,
}) => {
  await expect(page.locator(".login-container")).toBeVisible();
  await expect(
    page.locator('h1:has-text("Welcome to Debugging Helper")')
  ).toBeVisible();
  await page.click('button:has-text("Mocked Mode")');
  await expect(page.locator('button:has-text("Regular Mode")')).toBeVisible();
  await expect(page.locator('input[type="email"]')).toBeVisible();
  await expect(page.locator('input[type="password"]')).toBeVisible();
  await expect(page.locator('button:has-text("Login")')).toBeVisible();
});

test("LoginPage's mock login is functional for instructor", async ({
  page,
}) => {
  await page.click('button:has-text("Mocked Mode")');
  await page.fill('input[type="email"]', "josiah_carberry@brown.edu");
  await page.fill('input[type="password"]', "1234");
  await page.click('button:has-text("Login")');
  await expect(page.locator(".dashboard-body")).toBeVisible();
});

test("LoginPage's mock login is functional for student", async ({ page }) => {
  await page.click('button:has-text("Mocked Mode")');
  await page.fill('input[type="email"]', "julia_zdzilowska@brown.edu");
  await page.fill('input[type="password"]', "anyway");
  await page.click('button:has-text("Login")');
  await expect(page.locator(".role-body")).toBeVisible();
});

test("LoginPage's incorrect mocked login is functional", async ({ page }) => {
  await page.click('button:has-text("Mocked Mode")');
  await page.fill('input[type="email"]', "wrong@brown.edu");
  await page.fill('input[type="password"]', "wrong");
  await page.click('button:has-text("Login")');
  await expect(page.locator(".role-body")).toBeHidden();
  await expect(page.locator('h1:has-text("Login Failed")')).toBeVisible();
  await expect(
    page.locator(
      'p:has-text("Your email or password is incorrect. Please try again.")'
    )
  ).toBeVisible();
  await expect(page.locator('button:has-text("Back to Login")')).toBeVisible();
  await page.click('button:has-text("Back to Login")');
  await expect(page.locator(".login-container")).toBeVisible();
});

test("LoginPage's incorrect mocked login with proper email is functional", async ({
  page,
}) => {
  await page.click('button:has-text("Mocked Mode")');
  await page.fill('input[type="email"]', "josiah_carberry@brown.edu");
  await page.fill('input[type="password"]', "wrong");
  await page.click('button:has-text("Login")');
  await expect(page.locator(".role-body")).toBeHidden();
  await expect(page.locator('h1:has-text("Login Failed")')).toBeVisible();
  await expect(
    page.locator(
      'p:has-text("Your email or password is incorrect. Please try again.")'
    )
  ).toBeVisible();
  await expect(page.locator('button:has-text("Back to Login")')).toBeVisible();
  await page.click('button:has-text("Back to Login")');
  await expect(page.locator(".login-container")).toBeVisible();
});
