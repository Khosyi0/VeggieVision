const myLogger = ((req, res, next) => {
    console.log("Homepage")
    next()
})

const scanData = ((req, res, next) => {
    console.log("Scan Homepage")
    next()
})

const postImage = ((req, res, next) => {
    console.log("POST IMAGE")
    next()
})

module.exports = {myLogger, scanData, postImage}