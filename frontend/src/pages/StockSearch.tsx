import { Box, Typography } from '@mui/material';
import StockSearchComponent from '../components/StockSearchComponent';

export default function StockSearch() {
  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Stock Search
      </Typography>
      <Typography variant="body1" color="text.secondary" sx={{ mb: 3 }}>
        Search for stocks to analyze and add to your portfolio.
      </Typography>
      <StockSearchComponent />
    </Box>
  );
}
