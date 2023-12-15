import { test, expect } from "playwright/test";

test.beforeEach(async ({ page }) => {
  await page.goto("http://localhost:3000/");
});

test("Landing page has the required UI components", async ({ page }) => {
  const logoVisible = await page.isVisible(".pulsing-logo");
  expect(logoVisible).toBeTruthy();
  const transitionClassNotApplied = await page.isVisible(".transition");
  expect(transitionClassNotApplied).toBeFalsy();
  // during initial 2.5 seconds
  await page.waitForTimeout(2500);

  // Check if the transition class is applied
  const transitionClassApplied = await page.isVisible(".transition");
  expect(transitionClassApplied).toBeTruthy();
});
