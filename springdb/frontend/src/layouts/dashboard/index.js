import {Outlet} from 'react-router-dom';
// material
import {styled} from '@mui/material/styles';
//
import DashboardTopbar from "./DashboardTopbar";
import {Box} from "@mui/material";

// ----------------------------------------------------------------------

const RootStyle = styled('div')({
    display: 'flex',
    flexWrap: 'wrap',
    minHeight: '100%',
    alignItems: 'flex-start'
});

const MainStyle = styled('div')(({theme}) => ({
    flexGrow: 1,
    minHeight: '100%',
    paddingBottom: theme.spacing(10),
    [theme.breakpoints.up('lg')]: {
        paddingLeft: theme.spacing(2),
        paddingRight: theme.spacing(2)
    }
}));

// ----------------------------------------------------------------------

export default function DashboardLayout() {

    return (
        <RootStyle>
            <DashboardTopbar/>
            <Box flexBasis='100%' height={0}/>
            <MainStyle>
                <Outlet/>
            </MainStyle>
        </RootStyle>
    );
}
