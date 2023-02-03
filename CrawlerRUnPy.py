# import necessary libraries
from selenium import webdriver
from bs4 import BeautifulSoup
import os

# initialize a set to store visited URLs
visited = set()

# function to extract text from webpage and recurse through hyperlinks
def extract_text(url):
    # initialize Chrome driver in headless mode
    options = webdriver.ChromeOptions()
    options.add_argument('--headless')
    driver = webdriver.Chrome(options=options)

    # open the webpage using driver
    driver.get(url)
    html = driver.page_source

    # parse HTML using BeautifulSoup
    soup = BeautifulSoup(html, 'html.parser')

    # extract only text from the webpage
    text = soup.get_text()

    # write the extracted text to a file
    with open('output.txt', 'a') as f:
        f.write(text + '\n')

    # find all hyperlinks in the page
    links = [a.get_attribute('href') for a in driver.find_elements_by_xpath("//a[@href]")]

    # iterate through hyperlinks and extract text recursively
    for link in links:
        if link not in visited:
            visited.add(link)
            extract_text(link)

    # close the driver
    driver.close()

# read the list of webpage addresses from file
with open('webpages.txt') as f:
    urls = f.readlines()

# iterate through the list of URLs and extract text
for url in urls:
    url = url.strip()
    if url not in visited:
        visited.add(url)
        extract_text(url)
