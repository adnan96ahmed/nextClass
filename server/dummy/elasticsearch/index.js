const express = require('express')
const fs = require('fs')
const app = express()
const Fuse = require('fuse.js')

const port = process.env.PORT || 11880

let data = JSON.parse(fs.readFileSync('resources/courses.json'))
var options = {
    shouldSort: true,
    threshold: 0.6,
    location: 0,
    distance: 100,
    maxPatternLength: 32,
    minMatchCharLength: 1,
    keys: [
        {
            "name": "title",
            "weight": 0.3
        },
        {
            "name": "concat",
            "weight": 0.7
        }
    ]
}
let fuse = new Fuse(data, options)

app.use(express.json())

app.get('/:semester/search', (req, res) => {
    query = req.query.q
    limit = req.query.limit
    semester = req.params["semester"]
    
    console.log({
        "semester": semester,
        "q": query,
        "limit": limit
    })

    results = fuse.search(query)
    if (limit !== undefined && limit > 0) {
        results = results.slice(0, limit)
    }

    results = results.map(i => {
        return {
            id: i.id,
            title: i.title,
            dept: i.dept,
            code: i.code
        }
    })
    res.json(results)
})

app.post('/:semester/search', (req, res) => {    
    query = req.body.match
    limit = req.body.limit
    semester = req.params["semester"]
    
    console.log({
        "semester": semester,
        "match": query,
        "limit": limit
    })

    results = fuse.search(query)
    if (limit !== undefined && limit > 0) {
        results = results.slice(0, limit)
    }

    results = results.map(i => {
        return {
            id: i.id,
            title: i.title,
            dept: i.dept,
            code: i.code
        }
    })
    res.json(results)
})

app.listen(port, () => {
    console.log(`Listening on ${port}`)
})
