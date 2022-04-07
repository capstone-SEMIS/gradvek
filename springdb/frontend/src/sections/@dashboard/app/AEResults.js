// material
import { Card, TableRow, TableCell, TableHead, TableContainer, Table, TableBody } from '@mui/material';
import {Component} from "react";

export default class AEResults extends Component {
    constructor(props) {
        super(props);
        this.handleChange = this.handleChange.bind(this);
    }

    handleChange(e) {
        this.props.onResultsChange(e.target.value);  }

    render() {
        const results = this.props.results;
        return (
            <Card>
                <TableContainer>
                    <Table id="results-table">
                        <TableHead>
                            <TableRow>
                                <TableCell>
                                    Adverse Event
                                </TableCell>
                                <TableCell>
                                    Weight
                                </TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {results.map( (row) => (
                                <TableRow key={row.name}>
                                    <TableCell>
                                        {row.name}
                                    </TableCell>
                                    <TableCell>
                                        {row.llr}
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table >
                </TableContainer>
            </Card>
        )
    }
}
