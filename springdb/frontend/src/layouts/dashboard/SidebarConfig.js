// component
import Iconify from "../../components/Iconify";
// ----------------------------------------------------------------------

const getIcon = name => <Iconify icon={name} width={22} height={22} />;

const sidebarConfig = [
  {
    title: "dashboard",
    path: "/dashboard/app",
    icon: getIcon("ant-design:home-filled")
  },
  {
    title: "Data Management",
    path: "/dashboard/datamgmt",
    icon: getIcon("ic:round-storage")
  }
];

export default sidebarConfig;
