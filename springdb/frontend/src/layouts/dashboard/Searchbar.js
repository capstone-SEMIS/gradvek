import { useState } from 'react';
// material
import { styled, alpha } from '@mui/material/styles';
import {Input, Slide, IconButton, InputAdornment, ClickAwayListener, Toolbar, AppBar} from '@mui/material';
// component
import Iconify from '../../components/Iconify';
import {Form, FormikProvider, useFormik} from "formik";
import {LoadingButton} from "@mui/lab";
import PropTypes from "prop-types";

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

  const {isSubmitting, getFieldProps } = formik;

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
                                  <Form autoComplete="off" noValidate onSubmit={formik.handleSubmit}>
                                      <Input
                                          autoFocus
                                          fullWidth
                                          {...getFieldProps('searchText')}
                                          onChange={formik.handleChange}
                                          value={formik.values.searchText}
                                          startAdornment={
                                              <InputAdornment position="start">
                                                  <Iconify
                                                      icon="eva:search-fill"
                                                      sx={{ color: 'text.disabled', width: 20, height: 20 }}
                                                  />
                                              </InputAdornment>
                                          }
                                          sx={{ mr: 1, fontWeight: 'fontWeightBold' }}
                                      />
                                      <LoadingButton
                                          type="submit"
                                          variant="contained"
                                          loading={isSubmitting}
                                      >
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
