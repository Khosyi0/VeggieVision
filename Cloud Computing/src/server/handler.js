const fs = require('fs');
const tf = require('@tensorflow/tfjs-node');

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
        // console.log('Handling prediction...');  // Log untuk debugging
        // console.log('Received file:', req.file);  // Log untuk debugging

        if(!req.file) {
            throw new Error('No File Upload');
        }

        const imagePath = req.file.path;

        const imageBuffer = fs.readFileSync(imagePath);
        // const imageTensor = tf.node.decodeImage(imageBuffer);

        // const resizedImage = tf.image.resizeBilinear(imageTensor, [150, 150]);
        // const normalizedImage = resizedImage.div(tf.scalar(255)).expandDims(0);

        const tensor = tf.node
            .decodeJpeg(imageBuffer)
            .resizeNearestNeighbor([150, 150]) // Sesuaikan ukuran dengan input model
            .expandDims()
            .toFloat()
            .div(tf.scalar(255.0))

        // const prediction = model.predict(normalizedImage);
        // const predictionArray = prediction.arraySync()[0];
        const prediction = model.predict(tensor);
        const confidence = Math.max(...await prediction.data()) * 100;

        const classLabels = {0: 'fresh_apple', 1: 'fresh_banana', 2: 'fresh_bitter_gourd', 3: 'fresh_capsicum', 4: 'fresh_orange', 5: 'fresh_tomato', 6: 'stale_apple', 7: 'stale_banana', 8: 'stale_bitter_gourd', 9: 'stale_capsicum', 10: 'stale_orange', 11: 'stale_tomato'};
        const predictedClass = tf.argMax(prediction, 1).dataSync()[0];
        const predictedLabel = classLabels[predictedClass];
        
        console.log('Confidence : ', confidence);
        console.log('PredictedLabel : ', predictedLabel);
        
        // console.log('Prediction Array : ', predictionArray);

        fs.unlinkSync(imagePath);

        let freshnessPercentage;
        // if(predictedLabel === 'fresh_apple') {
        //     freshnessPercentage = confidence;
        // }
        // if(predictedLabel === 'fresh_banana') {
        //     freshnessPercentage = confidence;
        // }
        // if(predictedLabel === 'fresh_bitter_gourd') {
        //     freshnessPercentage = confidence;
        // }
        // if(predictedLabel === 'fresh_capsicum') {
        //     freshnessPercentage = confidence;
        // }
        // if(predictedLabel === 'fresh_orange') {
        //     freshnessPercentage = confidence;
        // }
        // if(predictedLabel === 'fresh_tomato') {
        //     freshnessPercentage = confidence;
        // }
        
        

        if (predictedLabel === 'fresh') {
            freshnessPercentage = confidence;
        } else {
            // freshnessPercentage = 100 - confidence;
            freshnessPercentage = confidence; //Confidence asli
        }
        res.json( {Label: predictedLabel, Percentage: freshnessPercentage} )
    } catch (error) {
        console.error(error);
        res.status(500).send('Error Process image')   
    }
}

module.exports = {myLogger, scanData, handlerPrediction}