const express = require('express');
const { myLogger, scanData, postImage } = require('./handler');
const Router = express.Router()

Router.get('/', myLogger)
Router.get('/scan', scanData)
Router.post('/scan', postImage)

Router.get('/', (req, res) => {
    res.send("Hello World")
})

Router.get('/scan', (req, res) => {
    // res.send("Scan Homepage")
})


module.exports = Router;