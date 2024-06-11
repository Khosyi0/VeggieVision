const { Sequelize } = require('sequelize');

// Membuat instance Sequelize dengan koneksi ke database MySQL
const sequelize = new Sequelize('user', 'root', 'your_password', {
    host: '172.25.128.1',
    dialect: 'mysql'
});

module.exports = sequelize;