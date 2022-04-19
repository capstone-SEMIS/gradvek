import {useState} from 'react';
// material
import {alpha, styled} from '@mui/material/styles';
import {AppBar, Autocomplete, ClickAwayListener, IconButton, Slide, TextField, Toolbar} from '@mui/material';
// component
import Iconify from '../../components/Iconify';
import {Form, FormikProvider, useFormik} from "formik";
import {LoadingButton} from "@mui/lab";
import PropTypes from "prop-types";
import {debounce} from "lodash";

// ----------------------------------------------------------------------

const DRAWER_WIDTH = 280;
const APPBAR_MOBILE = 64;
const APPBAR_DESKTOP = 92;

const RootStyle = styled(AppBar)(({ theme }) => ({
    boxShadow: 'none',
    backdropFilter: 'blur(6px)',
    WebkitBackdropFilter: 'blur(6px)', // Fix on Mobile
    backgroundColor: alpha(theme.palette.background.default, 0.72),
    [theme.breakpoints.up('lg')]: {
        width: `calc(100% - ${DRAWER_WIDTH + 1}px)`
    }
}));

const ToolbarStyle = styled(Toolbar)(({ theme }) => ({
    minHeight: APPBAR_MOBILE,
    [theme.breakpoints.up('lg')]: {
        minHeight: APPBAR_DESKTOP,
        padding: theme.spacing(0, 5)
    }
}));

const SearchbarStyle = styled('div')(({ theme }) => ({
  top: 0,
  left: 0,
  zIndex: 99,
  width: '100%',
  display: 'flex',
  position: 'absolute',
  alignItems: 'center',
  height: APPBAR_MOBILE,
  backdropFilter: 'blur(6px)',
  WebkitBackdropFilter: 'blur(6px)', // Fix on Mobile
  padding: theme.spacing(0, 3),
  boxShadow: theme.customShadows.z8,
  backgroundColor: `${alpha(theme.palette.background.default, 0.72)}`,
  [theme.breakpoints.up('md')]: {
    height: APPBAR_DESKTOP,
    padding: theme.spacing(0, 5)
  }
}));

Searchbar.propTypes = {
    results: PropTypes.array
};

// ----------------------------------------------------------------------

export default function Searchbar({onResultsChange}) {
  const [isOpen, setOpen] = useState(false);
   const [suggestions, setSuggestions] = useState([]);

  const handleOpen = () => {
    setOpen((prev) => !prev);
  };

  const handleClose = () => {
    setOpen(false);
  };

  const formik = useFormik({
    initialValues: {
      searchText: ''
    },
    onSubmit: (values) => {
      const requestOptions = {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' },
      };
      fetch('/api/ae/path/'+ values.searchText, requestOptions)
          .then((response) => {
              formik.setSubmitting(false);
              console.log("Status " + response.status);
              response.json().then(r => {
                  console.log(r);
                  onResultsChange(values.searchText, r);
              });
          })
    }
  });

  const {isSubmitting} = formik;

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

  return (
      <RootStyle>
          <ToolbarStyle>
              <ClickAwayListener onClickAway={handleClose}>
                  <div>
                      {!isOpen && (
                          <IconButton onClick={handleOpen}>
                              <Iconify icon="eva:search-fill" width={20} height={20} />
                          </IconButton>
                      )}

                      <Slide direction="down" in={isOpen} mountOnEnter unmountOnExit>
                          <SearchbarStyle>
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
                                          getOptionLabel={(option) => option.symbol}
                                          filterOptions={(options) => options}
                                          renderOption={(props, option) =>
                                              <li {...props}> {option.id + " : " + option.symbol + " : " + option.name}</li>
                                          }
                                          sx={{ color: 'text.disabled', marginTop: '3em', marginBottom: '1em'}}
                                          renderInput={(params) =>
                                              <TextField {...params} label="Target" sx={{width: '36em'}}/>
                                          }
                                          onInputChange={debouncedSuggest}
                                      />
                                      <LoadingButton type="submit" variant="contained" loading={isSubmitting}>
                                          Search
                                      </LoadingButton>
                                  </Form>
                              </FormikProvider>
                          </SearchbarStyle>
                      </Slide>
                  </div>
              </ClickAwayListener>
          </ToolbarStyle>
      </RootStyle>
  );
}
