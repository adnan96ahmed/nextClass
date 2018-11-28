const express = require('express')
const fs = require('fs')
const app = express()
const port = process.env.PORT || 11770

static_document = JSON.parse(fs.readFileSync("resources/static_doc.json"))

app.use(express.json())

handler = (req, res) => {    
    semester = req.params["semester"]
    
    console.log({
        'semester': semester,
        'body': req.body
    })

    res.json(static_document)
}

app.get('/:semester/generate', handler)
app.post('/:semester/generate', handler)

app.listen(port, () => {
    console.log(`Listening on ${port}`)
})
