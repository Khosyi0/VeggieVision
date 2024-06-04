const myLogger = ((req, res, next) => {
    console.log("Homepage")
    next()
})

const scanData = ((req, res, next) => {
    console.log("Scan Homepage")
    next()
})

const postImage = ((req, res, next) => {
    console.log("Post Image")
    next()
})

module.exports = {myLogger, scanData, postImage}