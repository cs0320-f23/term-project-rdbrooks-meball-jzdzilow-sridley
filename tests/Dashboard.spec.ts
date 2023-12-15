import { test, expect } from "playwright/test";

test.beforeEach(async ({ page }) => {
  await page.goto("http://localhost:3000/");
});

// Due to mocking, the dashboard can only be accessed by the instructor (otherwise is blocked as the session hasn't started)
test("Dashboard has the required ui components for instructors", async ({
  page,
}) => {
  await page.click('button:has-text("Mocked Mode")');
  await page.fill('input[type="email"]', "josiah_carberry@brown.edu");
  await page.fill('input[type="password"]', "1234");
  await page.click('button:has-text("Login")');
  await expect(page.locator(".dashboard-body")).toBeVisible();
  await expect(page.locator('h1:has-text("Welcome, Josiah")')).toBeVisible();
  await page.click('button:has-text("Download All Data")');
  await expect(page.locator('button:has-text("Start Session")')).toBeVisible();
  await expect(page.locator(".instructor-header")).toBeVisible();
  await expect(page.locator('b:has-text("Debugging Partners")')).toBeVisible();
  // await expect(page.locator('b:has-text("Escalated Pairs")')).toBeVisible();
  await expect(page.locator('b:has-text("Help Requesters")')).toBeVisible();
  await expect(page.locator('b:has-text("Non-Escalated Pairs")')).toBeVisible();
  await expect(page.locator(':is(:root):has-text("None yet!")')).toBeVisible();
  const numberOfElements1 = await page
    .locator(':is(:root):has-text("None yet!")')
    .count();
  await expect(numberOfElements1).toEqual(1);
  await expect(
    page.locator(':is(:root):has-text("None available!")')
  ).toBeVisible();
  const numberOfElements2 = await page
    .locator(':is(:root):has-text("None available!")')
    .count();
  await expect(numberOfElements2).toEqual(1);
  await expect(
    page.locator(':is(:root):has-text("None in queue!")')
  ).toBeVisible();
  const numberOfElements3 = await page
    .locator(':is(:root):has-text("None in queue!")')
    .count();
  await expect(numberOfElements3).toEqual(1);
  const isContainerVisible = await page.isVisible(".instructor-container");
  await expect(isContainerVisible).toBeTruthy();
  const isUnpairedVisible = await page.isVisible(
    ".unpaired-students-container"
  );
  await expect(isUnpairedVisible).toBeTruthy();
  const isPairedVisible = await page.isVisible(".paired-students-container");
  await expect(isPairedVisible).toBeTruthy();
  const numberOfElements4 = await page.locator(".general-title").count();
  await expect(numberOfElements4).toEqual(4);
});

test("Dashboard has the required dynamic components", async ({ page }) => {
  await page.click('button:has-text("Mocked Mode")');
  await page.fill('input[type="email"]', "josiah_carberry@brown.edu");
  await page.fill('input[type="password"]', "1234");
  await page.click('button:has-text("Login")');
  const svgElement = await page.locator("svg");

  // Hover over the SVG element
  await svgElement.hover();
  await page.waitForTimeout(1000);
  const debuggingRecipeText = await page
    .locator(':is(:root):has-text("debugging recipe")')
    .first();
  await expect(debuggingRecipeText).toBeVisible();
});
