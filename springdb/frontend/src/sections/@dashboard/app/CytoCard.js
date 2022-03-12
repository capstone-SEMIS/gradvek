// material
import { styled } from '@mui/material/styles';
import { Card } from '@mui/material';

import CytoCanvas from './CytoCanvas';

const RootStyle = styled(Card)(({ theme }) => ({
    boxShadow: 'none',
    textAlign: 'center',
    padding: theme.spacing(5, 0),
    color: theme.palette.primary.darker,
    backgroundColor: theme.palette.primary.lighter,
}));

export default function CytoCard(props) {
    return (
        <RootStyle>
            <CytoCanvas graphNodes={props.graphNodes}/>
        </RootStyle>
    );
}
