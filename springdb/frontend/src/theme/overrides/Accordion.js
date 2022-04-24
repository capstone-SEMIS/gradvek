export default function Accordion() {
    return {
      MuiAccordion: {
        defaultProps: {
          elevation: 0
        },
  
        styleOverrides: {
          root: {
            backgroundImage: "none",
            border: "0px solid transparent",
            "&:before": {
              backgroundColor: "transparent"
            }
          }
        }
      }
    };
  }
  