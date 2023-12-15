import { test, expect } from "playwright/test";

test.beforeEach(async ({ page }) => {
  await page.goto("http://localhost:3000/");
  await expect(
    page.locator('h1:has-text("Welcome to Collab Section")')
  ).toBeVisible();
  await page.click('button:has-text("Mocked Mode")');
});

test("RoleSelection UI elements are displayed for the student", async ({
  page,
}) => {
  await page.fill('input[type="email"]', "julia_zdzilowska@brown.edu");
  await page.fill('input[type="password"]', "anyway");
  await page.click('button:has-text("Login")');
  const welcomeMessageVisible = await page.isVisible('h2:has-text("Welcome")');
  expect(welcomeMessageVisible).toBeTruthy();

  // Check if the role selection buttons are visible
  const helpRequesterButtonVisible = await page.isVisible(
    'button:has-text("Help Requester")'
  );
  const debuggingPartnerButtonVisible = await page.isVisible(
    'button:has-text("Debugging Partner")'
  );
  const backToLoginButtonVisible = await page.isVisible(
    'button:has-text("Back to Login")'
  );
  expect(helpRequesterButtonVisible).toBeTruthy();
  expect(debuggingPartnerButtonVisible).toBeTruthy();
  expect(backToLoginButtonVisible).toBeTruthy();
});

test("RoleSelection UI elements aren't displayed for instructor", async ({
  page,
}) => {
  await page.fill('input[type="email"]', "josiah_carberry@brown.edu");
  await page.fill('input[type="password"]', "anyway");
  await page.click('button:has-text("Login")');
  const debuggingPartnerButtonInvisible = await page.isHidden(
    'button:has-text("Debugging Partner")'
  );
  expect(debuggingPartnerButtonInvisible).toBeTruthy();
});

test("Returning to login is functional", async ({ page }) => {
  await page.fill('input[type="email"]', "julia_zdzilowska@brown.edu");
  await page.fill('input[type="password"]', "anyway");
  await page.click('button:has-text("Login")');
  const helpRequesterButtonVisible = await page.isVisible(
    'button:has-text("Help Requester")'
  );
  expect(helpRequesterButtonVisible).toBeTruthy();
  const debuggingPartnerButtonVisible = await page.isVisible(
    'button:has-text("Debugging Partner")'
  );
  expect(debuggingPartnerButtonVisible).toBeTruthy();
  const backToLoginButtonVisible = await page.click(
    'button:has-text("Back to Login")'
  );
  await expect(page.locator(".login-container")).toBeVisible();
});

test("Redirecting to a specific dashboard is functional for instructor", async ({
  page,
}) => {
  await page.fill('input[type="email"]', "josiah_carberry@brown.edu");
  await page.fill('input[type="password"]', "anyway");
  await page.click('button:has-text("Login")');
  await expect(page.locator(".instructor-header")).toBeVisible();
  await expect(page.locator('h1:has-text("Welcome")')).toBeVisible();
});

test("Redirecting to a specific dashboard is functional for student", async ({
  page,
}) => {
  await page.fill(
    'input[placeholder="username"]',
    "julia_zdzilowska@brown.edu"
  );
  await page.fill('input[type="password"]', "anyway");
  await page.click('button:has-text("Login")');
  await page.click('button:has-text("Help Requester")');
  const bugIssueButton = await page.isVisible('button:has-text("A bug")');
  const issueButton = await page.isVisible(
    'button:has-text("A conceptual question")'
  );
  expect(bugIssueButton).toBeTruthy();
  expect(issueButton).toBeTruthy();
});
