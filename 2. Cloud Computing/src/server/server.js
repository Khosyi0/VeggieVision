const express = require('express')
const app = express()
const { Router, loadModel } = require('./router');

app.use(express.json());
app.use('/', Router)

const port = process.env.PORT || 8080

app.listen(port, async () => {
    await loadModel();
    console.log(`Running on http://localhost:${[port]}/`)
})
