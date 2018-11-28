# Generator Dummy API

## Quickstart

### Docker (Recommended)
```
docker build -t generator .
docker run -it --rm -p "11770:11770" generator
```

### Node.js
```
npm install
npm start
```

## Endpoints

### POST /{semester}/generate
```
JSON Body (Sample): 
{
    "courseIDs": [ 1, 2, 3, 4, 5 ],
    "scheduleSize": 5
}

Sample Input:
POST localhost:11770/W19/generate
{
    "courseIDs": [ 1, 2, 3, 4, 5 ],
    "scheduleSize": 5
}
```
For sample output, refer to [resources/static_doc.json](resources/static_doc.json).
