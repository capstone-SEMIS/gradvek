// material
import { styled } from '@mui/material/styles';
import { Card, CardHeader, Box } from '@mui/material';

import CytoCanvas from './CytoCanvas';
import CytoLegendCanvas from "./CytoLegendCanvas";

const RootStyle = styled(Card)(({ theme }) => ({

}));

export default function CytoLegendCard(props) {
    return (
        <RootStyle>
            <CardHeader title="Legend" subheader="" />
            <Box sx={{ p: 1, pb: 1 }} dir="ltr">
                <CytoLegendCanvas {...props} />
            </Box>
        </RootStyle>
    );
}
