import { Navigate, useRoutes } from "react-router-dom";
// layouts
import DashboardLayout from "./layouts/dashboard";
import LogoOnlyLayout from "./layouts/LogoOnlyLayout";
//
import DashboardApp from "./pages/DashboardApp";
import Products from "./pages/Products";
import User from "./pages/User";
import DataMgmt from "./pages/DataMgmt";
import NotFound from "./pages/Page404";

// ----------------------------------------------------------------------

export default function Router() {
  return useRoutes([
    {
      path: "/dashboard",
      element: <DashboardLayout />,
      children: [
        { path: "app", element: <DashboardApp /> },
        { path: "user", element: <User /> },
        { path: "products", element: <Products /> },
        { path: "DataMgmt", element: <DataMgmt /> }
      ]
    },
    {
      path: "/",
      element: <LogoOnlyLayout />,
      children: [
        { path: "/", element: <Navigate to="/dashboard/app" /> },
        { path: "404", element: <NotFound /> },
        { path: "*", element: <Navigate to="/404" /> }
      ]
    },
    { path: "*", element: <Navigate to="/404" replace /> }
  ]);
}
