const { fileSystem } = require("@tensorflow/tfjs-node/dist/io")

const myLogger = ((req, res, next) => {
    console.log("Homepage")
    next()
})

const scanData = ((req, res, next) => {
    console.log("Scan Homepage")
    next()
})

const postImage = (async(req, res, next) => {
    console.log("POST IMAGE")
    if(!req.file) {
        return res.status(400).send('No File');
    }

    try {
        imagePath = req.file.path;
        const tensor = await processImage(imagePath)

        const prediction = model.predict(tensor);
        const predictionArray = prediction.arraySync();

        fileSystem.unlinkSync(imagePath);

        const result = predictionArray[0] > 0.5 ? 'Asu' : 'Kucing';
        res.json({ prediction: result });

    } catch (error){
        console.error(error);
        res.status(500).send('Error Process Image')
    }
    next()
})

module.exports = {myLogger, scanData, postImage}