const tf = require('@tensorflow/tfjs-node')
const fs = require('fs')

const loadModel = (async() => {
    const modelPath = "file://../../../model-tfjs/model.json"
    model = await tf.loadGraphModel(modelPath);
    console.log('Load Model');
})

async function predict(imageBuffer) {
    const tensor = tf.node.decodeImage(fs.readFileSync(imageBuffer), 3)
        .decodeJpeg(imageBuffer)
        .resizeNearestNeighbor([150, 150])
        .expandDims()
        .toFloat()
        .div(tf.scalar(255.0));   

    return tensor;
}

module.exports = { loadModel, predict };