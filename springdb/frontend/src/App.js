import React from "react";
// routes
import Router from "./routes";
// theme
import ThemeConfig from "./theme";
import GlobalStyles from "./theme/globalStyles";
// components
import ScrollToTop from "./components/ScrollToTop";
import { Toaster } from "react-hot-toast";
// ----------------------------------------------------------------------

export default function App() {
  return (
    <ThemeConfig>
      <Toaster
        toastOptions={{
          success: {
            style: {
              border: "1px solid green",
              fontSize: "1.6rem",
              color: "green"
            }
          },
          error: {
            style: {
              border: "1px solid red",
              fontSize: "1.6rem",
              color: "red"
            }
          }
        }}
      />
      <ScrollToTop />
      <GlobalStyles />
      <Router />
    </ThemeConfig>
  );
}
