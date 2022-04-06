import {styled} from '@mui/material/styles';
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import {useState} from "react";
import {CircularProgress} from "@mui/material";
import CancelIcon from '@mui/icons-material/Cancel';
import CheckIcon from '@mui/icons-material/Check';

const Input = styled('input')({
    display: 'none',
});

const CenteredSpan = styled('span')({
    display: 'flex',
    alignItems: 'center'
})

const SpacedSpan = styled('span')({
    marginLeft: '1em',
    marginRight: '1em'
})

function IconicMessage(props) {
    return <CenteredSpan> {props.icon} <SpacedSpan> {props.message} </SpacedSpan> </CenteredSpan>
}

export default function UploadButton() {
    const [INIT, SPINNING, SUCCESS, FAILURE] = [0, 1, 2, 3];
    const [progress, setProgress] = useState(INIT);

    function onFileChangeHandler(event) {
        event.preventDefault();
        const baseUrl = window.location.protocol + '//' + window.location.host;
        const formData = new FormData();
        formData.append('file', event.target.files[0]);
        formData.append('baseUrl', baseUrl);
        setProgress(SPINNING);
        fetch('/api/csv', {
            method: 'post',
            body: formData
        }).then(response => response.json().then(
            body => ({
                ok: response.ok,
                body: body
            }))
        ).then(result => {
            if (result.ok) {
                setProgress(SUCCESS);
            } else {
                setProgress(FAILURE);
            }
        })
    }

    function getProgressIndicator() {
        switch (progress) {
            case SPINNING:
                return <CircularProgress/>;
            case SUCCESS:
                return <IconicMessage icon={<CheckIcon color="success"/>} message={"Upload succeeded"}/>
            case FAILURE:
                return <IconicMessage icon={<CancelIcon color="error"/>} message={"Upload failed"}/>
            default:
                return;
        }
    }

    return (
        <Stack direction="row" alignItems="center" spacing={2}>
            <label htmlFor="contained-button-file">
                <Input accept=".csv" id="contained-button-file" type="file" onChange={onFileChangeHandler}/>
                <Button variant="contained" component="span">Upload</Button>
            </label>
            {getProgressIndicator()}
        </Stack>
    );
}
