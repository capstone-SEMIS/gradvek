// material
import {Card, Table, TableBody, TableCell, TableContainer, TableHead, TableRow} from "@mui/material";
import ExpandLessIcon from '@mui/icons-material/ExpandLess';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import React, {useState} from "react";
import {Id} from "../../../utils/entityProperties";
import {styled} from "@mui/material/styles";

// ----------------------------------------------------------------------

const denseCellPadding = '0.81rem';

const DenseCell = styled(TableCell)(() => ({
    paddingTop: denseCellPadding,
    paddingBottom: denseCellPadding,
    paddingLeft: denseCellPadding,
    paddingRight: denseCellPadding
}));

function DrugList({drugResults, filterHandler, target, actions, AE}) {
    return (
        <TableRow>
            <DenseCell colSpan="3">
                <Table>
                    <TableHead>
                        <TableRow>
                            <DenseCell>Drug Name</DenseCell>
                            <DenseCell>Drug ID</DenseCell>
                            <DenseCell>Weight</DenseCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {drugResults.map(r =>
                            <DrugRow key={r.drugId} drug={r} filterHandler={filterHandler} target={target} actions={actions} ae={AE}/>
                        )}
                    </TableBody>
                </Table>
            </DenseCell>
        </TableRow>
    );
}

function DrugRow({drug, filterHandler, target, actions, ae}) {
    return (
        <TableRow onClick={(e) => filterHandler(target, actions, ae.id, drug.drugId)}>
            <DenseCell> {drug.drugName} </DenseCell>
            <DenseCell> {drug.drugId} </DenseCell>
            <DenseCell> {(drug.weight).toFixed(2)} </DenseCell>
        </TableRow>
    );
}

function AeRow({target, actions, AE, filterHandler}) {
    const [expanded, setExpanded] = useState(false);
    const [drugResults, setDrugResults] = useState([]);

    function handleExpansion() {
        if (!expanded) {
            let url = `/api/weight/${encodeURIComponent(target)}/${encodeURIComponent(Id.ofAe(AE))}`
            if (actions && actions.length) {
                url = `${url}?actions=${actions.map(a => encodeURIComponent(a)).join(',')}`;
            }

            fetch(url).then(r => {
                if (r.ok) {
                    return r.json();
                } else {
                    throw new Error(r.statusText);
                }
            }).then(body => {
                setDrugResults(body);
            }).catch((error) => {
                console.error(error.name + ': ' + error.message);
            });
        }
        setExpanded(!expanded);
    }

    return (
        <>
            <TableRow>
                <DenseCell onClick={handleExpansion} >
                    {expanded ? <ExpandLessIcon /> : <ExpandMoreIcon />}
                </DenseCell>
                <DenseCell onClick={(e) => filterHandler(target, actions, AE.id)}>
                    {AE.name}
                </DenseCell>
                <DenseCell onClick={(e) => filterHandler(target, actions, AE.id)}>
                    {(AE.llr).toFixed(2)}
                </DenseCell>
            </TableRow>
            {expanded ? <DrugList drugResults={drugResults} filterHandler={filterHandler} target={target} actions={actions} AE={AE}/> : null}
        </>
    );
}

export default function AEList({target, actions, tableResults, filterHandler, displayAE_Weights}) {
    return (
        <Card sx={{overflow: 'visible'}}>
            <TableContainer sx={{overflow: 'visible', overflowWrap: 'normal'}}>
                <Table>
                    <TableHead>
                        {displayAE_Weights ?
                        <TableRow>
                            <DenseCell>
                                {/*Empty*/}
                            </DenseCell>
                            <React.Fragment>
                            <DenseCell>
                                Adverse Event
                            </DenseCell>
                            <DenseCell>
                                Weight
                            </DenseCell>
                            </React.Fragment>
                        </TableRow>
                            : null
                        }
                    </TableHead>
                    <TableBody>
                        {tableResults.map(r =>
                            <AeRow key={r.id} target={target} actions={actions} AE={r} filterHandler={filterHandler} />
                        )}
                    </TableBody>
                </Table>
            </TableContainer>
        </Card>
    )

}
