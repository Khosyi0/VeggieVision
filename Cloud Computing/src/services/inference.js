const tfjs = require('@tensorflow/tfjs-node')
const fs = require('fs')



function loadModel() {
    const modelUrl = "file:/../../model-tfjs/model.json"
    return tfjs.loadLayersModel(modelUrl)
}

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