const fs = require("fs");
const puppeteer = require("puppeteer");

async function crawlWebPages(urls) {
  const browser = await puppeteer.launch({ headless: true });
  const page = await browser.newPage();
  const visited = new Set();
  const result = [];

  async function crawl(url) {
    if (visited.has(url)) return;
    visited.add(url);

    await page.goto(url);
    const text = await page.evaluate(() => {
      return document.body.textContent;
    });
    result.push({ url, text });

    const links = await page.evaluate(() => {
      return Array.from(document.querySelectorAll("a")).map((a) => a.href);
    });
    await Promise.all(
      links.map(async (link) => {
        if (!link.startsWith("http")) return;
        await crawl(link);
      })
    );
  }

  await Promise.all(urls.map((url) => crawl(url)));
  await browser.close();

  return result;
}

async function main() {
  const urls = fs.readFileSync("urls.txt", "utf-8").split("\n").filter(Boolean);
  const result = await crawlWebPages(urls);

  fs.writeFileSync("result.json", JSON.stringify(result, null, 2));
  console.log("Done!");
}

main();
