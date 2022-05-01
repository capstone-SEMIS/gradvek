// material
import { styled } from '@mui/material/styles';
import { Card, CardHeader, Box } from '@mui/material';

import CytoLegendCanvas from "./CytoLegendCanvas";

const RootStyle = styled(Card)(({ theme }) => ({

}));

export default function CytoLegendCard(props) {
    return (
        <RootStyle>
            <CardHeader title="Legend" subheader="" sx={{ pt: "0.5em" }}/>
            <Box sx={{ p: 0 }} dir="ltr">
                <CytoLegendCanvas {...props} />
            </Box>
        </RootStyle>
    );
}
