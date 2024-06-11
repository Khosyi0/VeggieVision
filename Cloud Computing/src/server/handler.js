const fs = require('fs');
const tf = require('@tensorflow/tfjs-node');
const jwt = require('jsonwebtoken')
const User = require('../models/userModel')

const login = (async(req, res, next) => {
    let { email, password } = req.body;

    let existingUser;
    try {
        existingUser = await User.findOne({ where: { email: email } });
    } catch (err) {
        const error = new Error("Error! Something went wrong.");
        return next(error);
    }
    if (!existingUser || existingUser.password != password) {
        const error = Error("Wrong details please check at once");
        return next(error);
    }
    let token;
    try {
        token = jwt.sign(
            {
                userId: existingUser.id,
                email: existingUser.email
            },
            "secretkeyappearshere",
            { expiresIn: "1h" }
        );
    } catch (err) {
        console.log(err);
        const error = new Error("Error! Something went wrong.");
        return next(error);
    }

    res.status(200).json({
        success: true,
        data: {
            userId: existingUser.id,
            email: existingUser.email,
            token: token,
        },
    });
})

const signup = (async(req, res, next) => {
    const { name, email, password } = req.body;
    const newUser = User.build({ name, email, password });

    try {
        await newUser.save();
    } catch (err) {
        const error = new Error("Error! Something went wrong.");
        return next(error);
    }
    let token;
    try {
        token = jwt.sign(
            {
                userId: newUser.id,
                email: newUser.email
            },
            "secretkeyappearshere",
            { expiresIn: "1h" }
        );
    } catch (err) {
        const error = new Error("Error! Something went wrong.");
        return next(error);
    }
    res.status(201).json({
        success: true,
        data: {
            userId: newUser.id,
            email: newUser.email,
            token: token
        },
    });
})

const myLogger = ((req, res, next) => {
    console.log("Homepage")
    next()
})

const scanData = ((req, res, next) => {
    console.log("Scan Homepage")
    next()
})

const accessResource = ((req, res) => {
    const token =
        req.headers
            .authorization.split(' ')[1];
    //Authorization: 'Bearer TOKEN'
    if (!token) {
        res.status(200)
            .json(
                {
                    success: false,
                    message: "Error!Token was not provided."
                }
            );
    }
    //Decoding the token
    const decodedToken =
        jwt.verify(token, "secretkeyappearshere");
    res.status(200).json(
        {
            success: true,
            data: {
                userId: decodedToken.userId,
                email: decodedToken.email
            }
        }
    );
})

async function handlerPrediction(req, res, model) {
    
    try {
        if(!req.file) {
            throw new Error('No File Upload');
        }

        const imagePath = req.file.path;

        const imageBuffer = fs.readFileSync(imagePath);

        const tensor = tf.node
            .decodeJpeg(imageBuffer)
            .resizeNearestNeighbor([150, 150]) // Sesuaikan ukuran dengan input model
            .expandDims()
            .toFloat()
            .div(tf.scalar(255.0))

        const prediction = model.predict(tensor);
        const confidence = Math.max(...await prediction.data()) * 100;

        const classLabels = {0: 'fresh_apple', 1: 'fresh_banana', 2: 'fresh_bitter_gourd', 3: 'fresh_capsicum', 4: 'fresh_orange', 5: 'fresh_tomato', 6: 'stale_apple', 7: 'stale_banana', 8: 'stale_bitter_gourd', 9: 'stale_capsicum', 10: 'stale_orange', 11: 'stale_tomato'};
        const predictedClass = tf.argMax(prediction, 1).dataSync()[0];
        const predictedLabel = classLabels[predictedClass];
        
        console.log('Confidence : ', confidence);
        console.log('PredictedLabel : ', predictedLabel);

        fs.unlinkSync(imagePath);

        let freshnessPercentage;
        if (predictedLabel.indexOf('fresh') !== -1) {
            freshnessPercentage = confidence;
        } else {
            freshnessPercentage = 100 - confidence;
            // freshnessPercentage = confidence; //Confidence asli
        }
        res.json( {Label: predictedLabel, Percentage: freshnessPercentage.toFixed(2)} )
    } catch (error) {
        console.error(error);
        res.status(500).send('Error Process image')   
    }
}

module.exports = {myLogger, scanData, handlerPrediction, login, signup, accessResource}