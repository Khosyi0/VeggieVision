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
        console.log('Handling prediction...');  // Log untuk debugging
        console.log('Received file:', req.file);  // Log untuk debugging

        if(!req.file) {
            throw new Error('No File Upload');
        }

        const imagePath = req.file.path;

        const imageBuffer = fs.readFileSync(imagePath);
        const iamgeTensor = tf.node.decodeImage(imageBuffer);

        const resizedImage = tf.image.resizeBilinear(iamgeTensor, [150, 150]);
        const normalizedImage = resizedImage.div(tf.scalar(255)).expandDims(0);

        const prediction = model.predict(normalizedImage);
        const output = prediction.arraySync();

        fs.unlinkSync(imagePath);

        const result = output[0] > 0.5 ? 'asu' : 'kucing'
        res.json( {prediction: result} )
    } catch (error) {
        console.error(error);
        res.status(500).send('Error Process image')   
    }
}

module.exports = {myLogger, scanData, handlerPrediction}