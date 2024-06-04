const express = require('express')
const app = express()
const Router = require('./router')
const mw = require('./handler.js')

app.use('/', Router)

const port = 8000

app.listen(port, () => {
    console.log(`Running on http://localhost:${[port]}/`)
})