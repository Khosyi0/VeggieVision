const express = require('express')
const app = express()
const {Router, loadModel} = require('./router');
const sequelize = require('../config/config');

app.use(express.json());
app.use('/', Router)

const port = 8000

app.listen(port, async() => {
    await loadModel();
    await sequelize.authenticate();
    await sequelize.sync();
    console.log(`Running on http://localhost:${[port]}/`)
})