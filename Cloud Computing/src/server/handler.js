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
        const iamgeTensor = tf.node.decodeImage(imageBuffer);

        const resizedImage = tf.image.resizeBilinear(iamgeTensor, [150, 150]);
        const normalizedImage = resizedImage.div(tf.scalar(255)).expandDims(0);

        const prediction = model.predict(normalizedImage);
        const predictionArray = prediction.arraySync()[0];

        const confidence = Math.max(...predictionArray) * 100;
        const predictedClass = predictionArray.indexOf(Math.max(...predictionArray));

        console.log('Predicted Class : ', predictedClass);
        console.log('Confidence : ', confidence);
        console.log('Prediction Array : ', predictionArray);

        const classLabels = {0: 'fresh', 1:'stale'};
        const predictedLabel = classLabels[Math.round(...predictionArray)];

        fs.unlinkSync(imagePath);

        let freshnessPercentage;
        if (predictedLabel === 'stale') {
            freshnessPercentage = confidence;
        } else {
            freshnessPercentage = 100 - confidence;
            // freshnessPercentage = confidence; //Confidence asli
        }
        res.json( {Label: predictedLabel, Percentage: freshnessPercentage} )
    } catch (error) {
        console.error(error);
        res.status(500).send('Error Process image')   
    }
}

module.exports = {myLogger, scanData, handlerPrediction}