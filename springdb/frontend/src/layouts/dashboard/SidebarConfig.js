// component
import Iconify from "../../components/Iconify";
//import StorageIcon from "@mui/icons-material/Storage";
// ----------------------------------------------------------------------

const getIcon = name => <Iconify icon={name} width={22} height={22} />;

const sidebarConfig = [
  {
    title: "dashboard",
    path: "/dashboard/app",
    icon: getIcon("eva:pie-chart-2-fill")
  },
  {
    title: "user",
    path: "/dashboard/user",
    icon: getIcon("eva:people-fill")
  },
  {
    title: "product",
    path: "/dashboard/products",
    icon: getIcon("eva:shopping-bag-fill")
  },
  {
    title: "DataMgmt",
    path: "/dashboard/datamgmt",
    icon: getIcon("eva:StorageIcon")
  }
];

export default sidebarConfig;
