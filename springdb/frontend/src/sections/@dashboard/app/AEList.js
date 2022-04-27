// material
import {Card, Table, TableBody, TableCell, TableContainer, TableHead, TableRow} from "@mui/material";
import ExpandLessIcon from '@mui/icons-material/ExpandLess';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import React, {useState} from "react";
import {Id} from "../../../utils/entityProperties";


// ----------------------------------------------------------------------

function DrugList({drugResults, filterHandler, target, actions, AE}) {
    return (
        <TableRow>
            <TableCell colSpan="3">
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Drug Name</TableCell>
                            <TableCell>Drug ID</TableCell>
                            <TableCell>Weight</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {drugResults.map(r =>
                            <DrugRow key={r.drugId} drug={r} filterHandler={filterHandler} target={target} actions={actions} ae={AE}/>
                        )}
                    </TableBody>
                </Table>
            </TableCell>
        </TableRow>
    );
}

function DrugRow({drug, filterHandler, target, actions, ae}) {
    return (
        <TableRow onClick={(e) => filterHandler(target, actions, ae.id, drug.drugId)}>
            <TableCell> {drug.drugName} </TableCell>
            <TableCell> {drug.drugId} </TableCell>
            <TableCell> {(drug.weight).toFixed(2)} </TableCell>
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
                <TableCell onClick={handleExpansion} >
                    {expanded ? <ExpandLessIcon /> : <ExpandMoreIcon />}
                </TableCell>
                <TableCell onClick={(e) => filterHandler(target, actions, AE.id)}>
                    {AE.name}
                </TableCell>
                <TableCell onClick={(e) => filterHandler(target, actions, AE.id)}>
                    {(AE.llr).toFixed(2)}
                </TableCell>
            </TableRow>
            {expanded ? <DrugList drugResults={drugResults} filterHandler={filterHandler} target={target} actions={actions} AE={AE}/> : null}
        </>
    );
}

export default function AEList({target, actions, tableResults, filterHandler, displayAE_Weights}) {
    return (
        <Card>
            <TableContainer>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>
                                {/*Empty*/}
                            </TableCell>
                            {displayAE_Weights ?
                            <React.Fragment>
                            <TableCell>
                                Adverse Event
                            </TableCell>
                            <TableCell>
                                Weight
                            </TableCell>
                            </React.Fragment>
                            : null
                            }
                        </TableRow>
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
