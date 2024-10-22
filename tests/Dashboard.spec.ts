import { test, expect } from "playwright/test";

test.beforeEach(async ({ page }) => {
  await page.goto("http://localhost:3000/");
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
