const { createProxyMiddleware } = require("http-proxy-middleware");

module.exports = function (app) {
    app.use(
        "/api",
        createProxyMiddleware({
            target: "http://192.168.12.1/TMI/v1/",
            changeOrigin: true,
            pathRewrite: { "^/api": "" },
        })
    );
};
