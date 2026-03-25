import {
  Box,
  Card,
  CardContent,
  Typography,
  Button,
  Chip,
} from '@mui/material';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import TrendingDownIcon from '@mui/icons-material/TrendingDown';
import type { Stock } from '../types';

interface StockDetailsComponentProps {
  stock: Stock;
  onBuy: () => void;
  onSell: () => void;
}

export default function StockDetailsComponent({ stock, onBuy, onSell }: StockDetailsComponentProps) {
  const isPositive = stock.dayChange >= 0;
  const changeColor = isPositive ? 'success.main' : 'error.main';

  return (
    <Card elevation={3}>
      <CardContent>
        <Box sx={{ mb: 3 }}>
          <Typography variant="h3" component="h1" gutterBottom>
            {stock.symbol}
          </Typography>
          <Typography variant="h6" color="text.secondary" gutterBottom>
            {stock.name}
          </Typography>
        </Box>

        <Box sx={{ mb: 3 }}>
          <Typography variant="h4" component="div" sx={{ mb: 1 }}>
            ${stock.currentPrice.toFixed(2)}
          </Typography>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            {isPositive ? (
              <TrendingUpIcon sx={{ color: changeColor }} />
            ) : (
              <TrendingDownIcon sx={{ color: changeColor }} />
            )}
            <Typography variant="h6" sx={{ color: changeColor }}>
              {isPositive ? '+' : ''}${stock.dayChange.toFixed(2)} ({isPositive ? '+' : ''}
              {stock.dayChangePercent.toFixed(2)}%)
            </Typography>
            <Chip
              label="Today"
              size="small"
              sx={{ ml: 1 }}
            />
          </Box>
        </Box>

        <Box sx={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: 2, mb: 3 }}>
          <Box>
            <Typography variant="body2" color="text.secondary">
              Previous Close
            </Typography>
            <Typography variant="body1" fontWeight="medium">
              ${stock.previousClose.toFixed(2)}
            </Typography>
          </Box>
          {stock.volume && (
            <Box>
              <Typography variant="body2" color="text.secondary">
                Volume
              </Typography>
              <Typography variant="body1" fontWeight="medium">
                {stock.volume.toLocaleString()}
              </Typography>
            </Box>
          )}
          {stock.marketCap && (
            <Box>
              <Typography variant="body2" color="text.secondary">
                Market Cap
              </Typography>
              <Typography variant="body1" fontWeight="medium">
                ${(stock.marketCap / 1e9).toFixed(2)}B
              </Typography>
            </Box>
          )}
          <Box>
            <Typography variant="body2" color="text.secondary">
              Last Updated
            </Typography>
            <Typography variant="body1" fontWeight="medium">
              {new Date(stock.lastUpdated).toLocaleString()}
            </Typography>
          </Box>
        </Box>

        <Box sx={{ display: 'flex', gap: 2 }}>
          <Button
            variant="contained"
            color="success"
            fullWidth
            size="large"
            onClick={onBuy}
          >
            Buy
          </Button>
          <Button
            variant="contained"
            color="error"
            fullWidth
            size="large"
            onClick={onSell}
          >
            Sell
          </Button>
        </Box>
      </CardContent>
    </Card>
  );
}
