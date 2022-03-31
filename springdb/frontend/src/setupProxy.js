const { createProxyMiddleware } = require("http-proxy-middleware");

module.exports = function(app) {
  const backend = createProxyMiddleware({
    target: "http://localhost:8080/",
    changeOrigin: true
  });
  app.use("/api", backend);
};

const cors = require("cors");
const corsOptions = {
  origin: "*",
  credentials: true, //access-control-allow-credentials:true
  optionSuccessStatus: 200
};

app.use(cors(corsOptions)); // Use this after the variable declaration
