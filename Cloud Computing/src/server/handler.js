const fs = require('fs');
const tf = require('@tensorflow/tfjs-node');

let articles = [];

const getArticles = (req, res) => {
    res.json({
        status: "ok",
        totalResults: articles.length,
        articles: articles
    });
};

// Function to add an article
const addArticle = (req, res) => {
    const { source, author, title, description, url, urlToImage, publishedAt, content } = req.body;

    // Validate input
    if (!title || !url || !publishedAt) {
        return res.status(400).json({ status: "error", message: "Title, URL, and PublishedAt are required fields" });
    }

    const article = {
        source: source || { id: null, name: null },
        author: author || null,
        title,
        description: description || null,
        url,
        urlToImage: urlToImage || null,
        publishedAt,
        content: content || null
    };

    articles.push(article);
    res.json({ status: "ok", message: "Article added", article });
};

// Function to delete an article by index
const deleteArticle = (req, res) => {
    const index = parseInt(req.params.index, 10);
    if (index >= 0 && index < articles.length) {
        const deletedArticle = articles.splice(index, 1);
        res.json({ status: "ok", message: "Article deleted", article: deletedArticle });
    } else {
        res.status(404).json({ status: "error", message: "Article not found" });
    }
};

const myLogger = ((req, res, next) => {
    console.log("Homepage")
    next()
})

const scanData = ((req, res, next) => {
    console.log("Scan Homepage")
    next()
})

async function handlerPrediction(req, res, model) {
    try {
        if (!req.file) {
            throw new Error('No File Upload');
        }

        const imageBuffer = req.file.buffer;

        const tensor = tf.node
            .decodeImage(imageBuffer)
            .resizeNearestNeighbor([150, 150]) // Adjust size as per model input
            .expandDims()
            .toFloat()
            .div(tf.scalar(255.0));

        const prediction = model.predict(tensor);
        const confidence = Math.max(...await prediction.data()) * 100;

        const classLabels = { 0: 'fresh_apple', 1: 'fresh_banana', 2: 'fresh_bitter_gourd', 3: 'fresh_capsicum', 4: 'fresh_orange', 5: 'fresh_tomato', 6: 'stale_apple', 7: 'stale_banana', 8: 'stale_bitter_gourd', 9: 'stale_capsicum', 10: 'stale_orange', 11: 'stale_tomato' };
        const predictedClass = tf.argMax(prediction, 1).dataSync()[0];
        const predictedLabel = classLabels[predictedClass];

        console.log('Confidence : ', confidence);
        console.log('PredictedLabel : ', predictedLabel);

        let freshnessPercentage;
        if (predictedLabel.indexOf('fresh') !== -1) {
            freshnessPercentage = confidence;
        } else {
            freshnessPercentage = 100 - confidence;
        }
        res.json({ Label: predictedLabel, Percentage: freshnessPercentage.toFixed(2) });
    } catch (error) {
        console.error(error);
        res.status(500).send('Error processing image');
    }
}

module.exports = { myLogger, scanData, handlerPrediction, getArticles, addArticle, deleteArticle };
