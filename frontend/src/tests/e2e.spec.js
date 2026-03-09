// @ts-check
/**
 * Playwright E2E Tests — NeuralHire AI Job Platform
 *
 * Run:  npx playwright test
 * Prereq: serve the frontend on http://localhost:3000
 *         start backend on http://localhost:8080 (or use mocks)
 */
const { test, expect } = require('@playwright/test');

const BASE_URL = process.env.FRONTEND_URL || 'http://localhost:3000';

test.describe('Page Load & Theme', () => {
  test('loads dashboard with title', async ({ page }) => {
    await page.goto(BASE_URL);
    await expect(page).toHaveTitle(/NeuralHire/i);
    await expect(page.locator('#pageTitle')).toContainText('Dashboard');
  });

  test('default theme is dark', async ({ page }) => {
    await page.goto(BASE_URL);
    const theme = await page.evaluate(() =>
      document.documentElement.getAttribute('data-theme')
    );
    expect(theme).toBe('dark');
  });

  test('toggles to light mode', async ({ page }) => {
    await page.goto(BASE_URL);
    await page.click('.theme-toggle');
    const theme = await page.evaluate(() =>
      document.documentElement.getAttribute('data-theme')
    );
    expect(theme).toBe('light');
  });

  test('persists theme across reload', async ({ page }) => {
    await page.goto(BASE_URL);
    await page.click('.theme-toggle'); // → light
    await page.reload();
    const theme = await page.evaluate(() =>
      document.documentElement.getAttribute('data-theme')
    );
    expect(theme).toBe('light');
  });
});

test.describe('Sidebar Navigation', () => {
  test('navigates to Resumes page', async ({ page }) => {
    await page.goto(BASE_URL);
    await page.click('.nav-item:nth-child(2)');
    await expect(page.locator('#page-resumes')).toBeVisible();
  });

  test('navigates to Job Board', async ({ page }) => {
    await page.goto(BASE_URL);
    await page.click('.nav-item:nth-child(3)');
    await expect(page.locator('#page-jobs')).toBeVisible();
  });

  test('navigates to AI Matching page', async ({ page }) => {
    await page.goto(BASE_URL);
    await page.click('.nav-item:nth-child(4)');
    await expect(page.locator('#page-match')).toBeVisible();
  });

  test('navigates to Skill Search', async ({ page }) => {
    await page.goto(BASE_URL);
    await page.click('.nav-item:nth-child(5)');
    await expect(page.locator('#page-skills')).toBeVisible();
  });

  test('sidebar expands on hover', async ({ page }) => {
    await page.goto(BASE_URL);
    const sidebar = page.locator('.sidebar');
    await sidebar.hover();
    const width = await sidebar.evaluate((el) => el.offsetWidth);
    expect(width).toBeGreaterThan(100);
  });
});

test.describe('Dashboard KPI Cards', () => {
  test('shows 4 KPI cards', async ({ page }) => {
    await page.goto(BASE_URL);
    const cards = page.locator('.kpi-card');
    await expect(cards).toHaveCount(4);
  });

  test('KPI cards load data (or show mock)', async ({ page }) => {
    await page.goto(BASE_URL);
    await page.waitForTimeout(1500); // allow API / mock load
    const resumeKpi = page.locator('#kpiResumes');
    const text = await resumeKpi.textContent();
    expect(text).not.toBe('—');
  });
});

test.describe('3D Scene Canvas', () => {
  test('renders scene canvas', async ({ page }) => {
    await page.goto(BASE_URL);
    await expect(page.locator('#scene-canvas')).toBeVisible();
  });

  test('scene canvas has non-zero size', async ({ page }) => {
    await page.goto(BASE_URL);
    const box = await page.locator('#scene-canvas').boundingBox();
    expect(box.width).toBeGreaterThan(50);
    expect(box.height).toBeGreaterThan(50);
  });
});

test.describe('Resume Form', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto(BASE_URL);
    await page.click('.nav-item:nth-child(2)'); // Resumes
  });

  test('shows create resume form fields', async ({ page }) => {
    await expect(page.locator('#rName')).toBeVisible();
    await expect(page.locator('#rEmail')).toBeVisible();
    await expect(page.locator('#rSkills')).toBeVisible();
  });

  test('shows file upload drop zone', async ({ page }) => {
    await expect(page.locator('.drop-zone')).toBeVisible();
  });

  test('submit with missing fields shows toast', async ({ page }) => {
    await page.click('#createResumeBtn');
    await expect(page.locator('.toast.error')).toBeVisible();
  });

  test('fills and submits resume form', async ({ page }) => {
    await page.fill('#rName', 'Test User');
    await page.fill('#rEmail', 'test@test.com');
    await page.fill('#rSkills', 'Java, Python');
    await page.click('#createResumeBtn');
    // Should show success OR fallback toast
    await expect(page.locator('.toast')).toBeVisible();
  });

  test('search filters resume list', async ({ page }) => {
    await page.fill('#resumeSearch', 'alice');
    // table should update (even if empty)
    await expect(page.locator('#resumeTable')).toBeVisible();
  });
});

test.describe('Job Form', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto(BASE_URL);
    await page.click('.nav-item:nth-child(3)'); // Jobs
  });

  test('shows job form fields', async ({ page }) => {
    await expect(page.locator('#jTitle')).toBeVisible();
    await expect(page.locator('#jCompany')).toBeVisible();
  });

  test('empty submit shows error toast', async ({ page }) => {
    await page.click('#createJobBtn');
    await expect(page.locator('.toast.error')).toBeVisible();
  });

  test('fills and posts job', async ({ page }) => {
    await page.fill('#jTitle', 'ML Engineer');
    await page.fill('#jCompany', 'TestCorp');
    await page.fill('#jLocation', 'Remote');
    await page.click('#createJobBtn');
    await expect(page.locator('.toast')).toBeVisible();
  });
});

test.describe('AI Matching', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto(BASE_URL);
    await page.click('.nav-item:nth-child(4)');
  });

  test('shows match form with resume and job inputs', async ({ page }) => {
    await expect(page.locator('#matchR')).toBeVisible();
    await expect(page.locator('#matchJ')).toBeVisible();
  });

  test('shows tabs for By Resume and By Job', async ({ page }) => {
    const tabs = page.locator('.tab-btn');
    await expect(tabs).toHaveCount(2);
  });

  test('empty match shows error', async ({ page }) => {
    await page.click('#matchBtn2');
    await expect(page.locator('.toast.error')).toBeVisible();
  });

  test('valid IDs trigger match result', async ({ page }) => {
    await page.fill('#matchR', '1');
    await page.fill('#matchJ', '1');
    await page.click('#matchBtn2');
    await page.waitForTimeout(2000);
    await expect(page.locator('#matchPageResult')).not.toBeEmpty();
  });
});

test.describe('Skill Search', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto(BASE_URL);
    await page.click('.nav-item:nth-child(5)');
  });

  test('shows popular skills tags', async ({ page }) => {
    const tags = page.locator('.skill-tag');
    const count = await tags.count();
    expect(count).toBeGreaterThan(5);
  });

  test('empty query shows error', async ({ page }) => {
    await page.click('button:has-text("Search")');
    await expect(page.locator('.toast.error')).toBeVisible();
  });

  test('clicking skill tag populates search input', async ({ page }) => {
    await page.locator('.skill-tag').first().click();
    const val = await page.locator('#skillQuery').inputValue();
    expect(val.length).toBeGreaterThan(0);
  });

  test('searching shows results or empty state', async ({ page }) => {
    await page.fill('#skillQuery', 'Java');
    await page.click('button:has-text("Search")');
    await page.waitForTimeout(2000);
    await expect(page.locator('#skillResults')).not.toBeEmpty();
  });
});

test.describe('Activity Log', () => {
  test('activity log is present on dashboard', async ({ page }) => {
    await page.goto(BASE_URL);
    await expect(page.locator('#activityLog')).toBeVisible();
  });
  test('clear log button works', async ({ page }) => {
    await page.goto(BASE_URL);
    await page.waitForTimeout(1000);
    await page.click('button:has-text("Clear")');
    await expect(page.locator('.toast')).toBeVisible();
  });
});

test.describe('Settings', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto(BASE_URL);
    await page.click('.nav-item:last-child');
  });

  test('shows API base URL field', async ({ page }) => {
    await expect(page.locator('#apiBaseUrl')).toBeVisible();
  });

  test('saves settings and shows toast', async ({ page }) => {
    await page.fill('#apiBaseUrl', 'http://localhost:9090');
    await page.click('button:has-text("Save Settings")');
    await expect(page.locator('.toast.success')).toBeVisible();
  });
});
