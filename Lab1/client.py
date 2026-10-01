import argparse
from urllib.parse import urlencode
from urllib.request import Request, urlopen


parser = argparse.ArgumentParser(description="Call the controller servlet")
parser.add_argument("page", choices=["1", "2"])
args = parser.parse_args()

url = "http://localhost:8080/Lab1_war_exploded/controller"

data = urlencode({"page": args.page}).encode("utf-8")

request = Request(
    url,
    data=data,
    method="POST",
    headers={
        "Content-Type": "application/x-www-form-urlencoded",
        "Accept": "text/plain",
        "User-Agent": "Lab1-Python-Client/1.0",
        "Accept-Language": "en",
    },
)

with urlopen(request, timeout=10) as response:
    result = response.read().decode("utf-8")
    print(result)