import PropTypes from 'prop-types';
// material

import { Card, TableRow, TableCell, TableHead, TableContainer, Table, TableBody } from '@mui/material';


// ----------------------------------------------------------------------

AEList.propTypes = {
  graphNodes: PropTypes.array.isRequired
};

export default function AEList({ graphNodes, filterHandler}) {
    let AEs = graphNodes.filter( (graphNode) => {
        return graphNode.classes?.includes("adverse event");
    });

    return (
        <Card>
            <TableContainer>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>
                                ID
                            </TableCell>
                            <TableCell>
                                Meddra
                            </TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {AEs.map( (AE) => (
                            <TableRow onClick={filterHandler} key={AE.data.id}>
                                <TableCell>
                                    {AE.data.id}
                                </TableCell>
                                <TableCell>
                                    {AE.data.meddraCode}
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table >
            </TableContainer>
        </Card>
        
    )
}
