const express = require('express');
const { myLogger, scanData, postImage } = require('./handler');
const Router = express.Router()

Router.get('/', myLogger)
Router.get('/scan', scanData)
Router.post('/scan', postImage)

Router.get('/', (req, res) => {
    res.send("GET HOME")
})

Router.get('/scan', (req, res) => {
    res.send("GET SCAN")
})

Router.post('/scan', (req, res) => {
    res.send("POST SCAN")
})


module.exports = Router;