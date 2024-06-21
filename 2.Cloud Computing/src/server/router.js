const express = require('express');
const { myLogger, scanData, handlerPrediction, getArticles, addArticle, deleteArticle } = require('./handler');
const { Storage } = require('@google-cloud/storage');
const path = require('path');
const tf = require('@tensorflow/tfjs-node');
const multer = require('multer');

const Router = express.Router();
const storage = new Storage();
const bucketName = 'bucket-capstone-5'; // Replace with your bucket name
let model;

const upload = multer({
    storage: multer.memoryStorage()
  });

const loadModel = async () => {
    const modelPath = path.resolve(__dirname, '../../VeggieVisionModel/model.json');
    model = await tf.loadLayersModel(`file://${modelPath}`);
    console.log("Model Loaded");
};

Router.get('/', myLogger);

Router.get('/articles', getArticles); // Route to get articles
Router.post('/articles', addArticle); // Route to add articles
Router.delete('/articles/:index', deleteArticle);


Router.post('/scan', upload.single('image'), async (req, res, next) => {
    try {
        const file = req.file;
        if (!file) {
            return res.status(400).send('No file uploaded.');
        }

        const bucket = storage.bucket(bucketName);
        const blob = bucket.file(file.originalname);

        const blobStream = blob.createWriteStream({
            metadata: {
                contentType: file.mimetype,
                cacheControl: 'public, max-age=31536000',
            }
        });

        blobStream.on('error', err => {
            next(err);
        });

        blobStream.on('finish', () => {
            req.file.cloudStorageObject = blob.name;
            req.file.cloudStoragePublicUrl = `https://storage.googleapis.com/bucket-capstone-5/VeggieVisionModel/model.json`;
            next();
        });

        blobStream.end(file.buffer);
    } catch (error) {
        next(error);
    }
}, async (req, res) => {
    await handlerPrediction(req, res, model);
});

module.exports = { Router, loadModel };
