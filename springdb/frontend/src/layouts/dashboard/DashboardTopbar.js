import {Box, IconButton, Toolbar} from "@mui/material";
import Logo from "../../components/Logo";
import {lightBlue} from "@mui/material/colors";
import Iconify from "../../components/Iconify";
import Typography from "@mui/material/Typography";
import Button from "@mui/material/Button";

export default function DashboardTopbar() {
    return (
        <Toolbar sx={{backgroundColor: lightBlue["100"], width: "100%", alignItems: 'center', flexWrap: 'wrap'}}>
            <IconButton edge="start">
                <Logo/>
            </IconButton>
            <Typography variant="h6" component="div" sx={{mr: 10}}>
                GRADVEK
            </Typography>

            <Box>
                <Button color='inherit' href='/dashboard/app'>
                    <Iconify icon='ant-design:search' width={22} height={22} sx={{mr: 1}}/>
                    <Typography variant="subtitle1" component="div" sx={{mr: 10}}>
                        Query
                    </Typography>
                </Button>

                <Button color='inherit' href='/dashboard/datamgmt'>
                    <Iconify icon='ic:round-storage' width={22} height={22} sx={{mr: 1}}/>
                    <Typography variant="subtitle1" component="div">
                        Data Management
                    </Typography>
                </Button>
            </Box>
        </Toolbar>
    )
}