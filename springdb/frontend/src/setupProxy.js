const {createProxyMiddleware} = require('http-proxy-middleware');

module.exports = function (app) {
    const backend = createProxyMiddleware({target: 'http://localhost:8080/', changeOrigin: true,})
    app.use('/ae', backend);
    app.use('/csv', backend);
    app.use('/info', backend);
};