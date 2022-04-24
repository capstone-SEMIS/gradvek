// ----------------------------------------------------------------------

export default function Paper() {
  return {
    MuiPaper: {
      defaultProps: {
        elevation: 0
      },

      styleOverrides: {
        root: {
          backgroundImage: 'none',
          border: '0px solid transparent'
        }
      }
    }
  };
}
