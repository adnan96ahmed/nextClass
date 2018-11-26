# Elasticsearch Dummy API

The tool we'll use for autocomplete, Elasticsearch, isn't set up yet. So here's a dummy API roughly mimics what the final API will look like.

## Quickstart

### Docker (Recommended)
```
docker build -t dummy-es .
docker run -it --rm -p "11880:11880" dummy-es
```

### Node.js
```
npm install
npm start
```

## Endpoints

### GET /{semester}/search
```
Query Parameters: 
    q (query text)
    limit (maximum search results)

Sample Input:
GET localhost:11880/F18/search?q=CIS 4720&limit=5

Sample Output:
[
    {
        "id": 19,
        "title": "Image Processing & Vision",
        "dept": "CIS",
        "code": "4720"
    },
    ...
]
```

### POST /{semester}/search
```
JSON Body: 
{
    "match": "<query string>"
    "limit": n
}

Sample Input:
POST localhost:11880/F18/search
{
    "match": "CIS 4720"
    "limit": 5
}

Sample Output:
[
    {
        "id": 19,
        "title": "Image Processing & Vision",
        "dept": "CIS",
        "code": "4720"
    },
    ...
]
```
