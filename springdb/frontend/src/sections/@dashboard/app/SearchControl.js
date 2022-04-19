import {Form, FormikProvider, useFormik} from "formik";
import {Accordion, AccordionDetails, AccordionSummary, Autocomplete, Box, Checkbox, TextField} from "@mui/material";
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import {LoadingButton} from "@mui/lab";
import {useState} from "react";
import {debounce, lowerCase, max, startCase} from "lodash";
import Typography from "@mui/material/Typography";

export default function SearchControl({onResultsChange, actions}) {
    const [suggestions, setSuggestions] = useState([]);
    const [selections, setSelections] = useState([]);

    function suggest(event, value) {
        if (!value) {
            return;
        }
        fetch(`/api/suggest/${encodeURIComponent(value)}`).then((response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error(response.statusText);
            }
        })).then(body => {
            setSuggestions(body);
        }).catch(error => {
            console.error(error.name + ': ' + error.message);
        });
    }

    const debouncedSuggest = debounce(suggest, 250);

    const formik = useFormik({
        initialValues: {
            searchText: ''
        },
        onSubmit: (values) => {
            formik.setSubmitting(false);
            onResultsChange(values.searchText);
        }
    });

    const {isSubmitting} = formik;

    function handleCheck(event) {
        const defaultFalse = {};
        defaultFalse[event.target.id] = false;
        const newSelections = {...defaultFalse, ...selections};
        newSelections[event.target.id] = !newSelections[event.target.id];
        setSelections(newSelections);
    }

    function SelectedActions() {
        const selectionSize = Object.values(selections).length;
        if ((Object.values(selections).every(v => !v) && selectionSize > 0) // all unchecked
            || (Object.values(selections).every(v => v) && selectionSize === actions.length)    // all checked
            || (selectionSize === 0))   // default
        {
            return "ACTIONS: All";
        }
        return "ACTIONS: " +
            Object.keys(selections)
            .filter(k => selections[k])
                .map(k => startCase(lowerCase(k)))
                .sort()
                .join(", ");
    }

    return (
        <FormikProvider value={formik}>
            <Form autoComplete="off" noValidate onSubmit={(event) => {
                const target = document.getElementById('autocomplete-field').value;
                formik.setValues({"searchText": target});
                formik.handleSubmit(event);
            }}>
                <Autocomplete
                    id='autocomplete-field'
                    freeSolo
                    options={suggestions}
                    getOptionLabel={(option) => option.hasOwnProperty('symbol') ? option.symbol : option.toString()}
                    filterOptions={(options) => options}
                    renderOption={(props, option) =>
                        <li {...props}> {option.id + " : " + option.symbol + " : " + option.name}</li>
                    }
                    sx={{color: 'text.disabled', marginTop: '3em', marginBottom: '1em'}}
                    renderInput={(params) =>
                        <TextField {...params} label="Target" sx={{width: '36em'}} autoFocus/>
                    }
                    onInputChange={debouncedSuggest}
                />
                <Accordion>
                    <AccordionSummary expandIcon={<ExpandMoreIcon/>}>
                        <SelectedActions />
                        {/*{Object.keys(selections).filter(k => selections[k]).map(k => <Fragment key={k}>{k}</Fragment>)}*/}
                        {/*Summary*/}
                    </AccordionSummary>
                    <AccordionDetails>
                        <Box display='flex' flexDirection='row' flexWrap='wrap'>
                            {actions !== null ? actions.map(a =>
                                    <Box key={a.action} width={`${max(actions.map(s => s.action.length))}ex`} display='flex'
                                         alignItems='center'>
                                        <Checkbox onChange={handleCheck} id={a.action}/>
                                        <Typography color={a.count === 0 ? 'lightgray' : 'inherit'}>
                                            {startCase(lowerCase(a.action))} ({a.count})
                                        </Typography>
                                    </Box>)
                                : null
                            }
                        </Box>
                    </AccordionDetails>
                </Accordion>
                <LoadingButton type="submit" variant="contained" loading={isSubmitting} sx={{my: '1em'}}>
                    Search
                </LoadingButton>
            </Form>
        </FormikProvider>
    );
}