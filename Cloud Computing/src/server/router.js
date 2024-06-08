const express = require('express');
const { myLogger, scanData, handlerPrediction } = require('./handler');
const Router = express.Router();
const multer = require('multer');
const path = require('path');
const tf = require('@tensorflow/tfjs-node');

const upload = multer({ dest:'uploads/' });


Router.get('/', myLogger)
Router.get('/scan', scanData)

Router.get('/', (req, res) => {
    res.send("GET HOME")
})

Router.get('/scan', (req, res) => {
    res.send("GET SCAN")
})

let model;

const loadModel = async() => {
    const modelPath = path.resolve(__dirname, '../../VeggieVisionModel/model.json');
    model = await tf.loadLayersModel(`file://${modelPath}`);
    console.log("Model Loaded")
}

Router.post('/scan', upload.single('image'), (req, res, next) => {
    next();
}, async (req, res) => {
    await handlerPrediction(req, res, model);
});


module.exports = {Router, loadModel};