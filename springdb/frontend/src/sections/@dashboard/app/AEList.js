// material
import {Card, Table, TableBody, TableCell, TableContainer, TableHead, TableRow} from "@mui/material";
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import {useState} from "react";

// ----------------------------------------------------------------------

function DrugRow() {
    return (
        <TableRow>
            <TableCell colSpan="3">
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>One</TableCell>
                            <TableCell>Two</TableCell>
                            <TableCell>Three</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        <TableRow>
                            <TableCell>1</TableCell>
                            <TableCell>2</TableCell>
                            <TableCell>3</TableCell>
                        </TableRow>
                    </TableBody>
                </Table>
            </TableCell>
        </TableRow>
    );
}

function AeRow({target, AE, filterHandler}) {
    const [expanded, setExpanded] = useState(false);

    function handleExpansion() {
        if (!expanded) {
            fetch(`/api/weight/${target}/${AE.meddraCode}`).then(r => {
                if (r.ok) {
                    console.log("success");
                } else {
                    throw new Error(r.statusText);
                }
            }).catch((error) => {
                console.error(error.name + ': ' + error.message);
            });
        }
        setExpanded(!expanded);
    }

    return (
        <>
            <TableRow>
                <TableCell>
                    <ExpandMoreIcon onClick={handleExpansion}/>
                    {/*<ExpandLessIcon />*/}
                </TableCell>
                <TableCell onClick={(e) => filterHandler(target, AE.id)}>
                    {AE.name}
                </TableCell>
                <TableCell onClick={(e) => filterHandler(target, AE.id)}>
                    {AE.llr}
                </TableCell>
            </TableRow>
            {expanded ? <DrugRow /> : null}
        </>
    );
}

export default function AEList({target, tableResults, filterHandler}) {
    return (
        <Card>
            <TableContainer>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>
                                {/*Empty*/}
                            </TableCell>
                            <TableCell>
                                Adverse Event
                            </TableCell>
                            <TableCell>
                                Weight
                            </TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {tableResults.map(r =>
                            <AeRow key={r.id} target={target} AE={r} filterHandler={filterHandler} />
                        )}
                    </TableBody>
                </Table>
            </TableContainer>
        </Card>
    )

}
