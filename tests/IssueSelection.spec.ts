import { test, expect } from "playwright/test";

test.beforeEach(async ({ page }) => {
  await page.goto("http://localhost:3000/");
  await expect(
    page.locator('h1:has-text("Welcome to Collab Section")')
  ).toBeVisible();
  await page.click('button:has-text("Mocked Mode")');
});

// cannot test further due to mocking issues (session hasn't started - can't be accessed even in mocked mode)
test("IssueSelection UI elements are displayed for the student", async ({
  page,
}) => {
  await page.fill('input[type="email"]', "julia_zdzilowska@brown.edu");
  await page.fill('input[type="password"]', "anyway");
  await page.click('button:has-text("Login")');
  await page.click('button:has-text("Help Requester")');
  const bugIssueButton = await page.isVisible('button:has-text("Bug")');
  const issueButton = await page.isVisible(
    'button:has-text("A conceptual question")'
  );
  expect(bugIssueButton).toBeTruthy();
  expect(issueButton).toBeTruthy();
});
