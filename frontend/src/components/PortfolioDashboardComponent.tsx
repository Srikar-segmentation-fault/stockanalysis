import {
  Box,
  Card,
  CardContent,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
} from '@mui/material';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import TrendingDownIcon from '@mui/icons-material/TrendingDown';
import { useNavigate } from 'react-router-dom';
import type { PortfolioSummary } from '../types';

interface PortfolioDashboardComponentProps {
  summary: PortfolioSummary;
}

export default function PortfolioDashboardComponent({ summary }: PortfolioDashboardComponentProps) {
  const navigate = useNavigate();
  const isDayPositive = summary.dayChange >= 0;
  const isTotalPositive = summary.totalGainLoss >= 0;

  return (
    <Box>
      <Card elevation={3} sx={{ mb: 3 }}>
        <CardContent>
          <Typography variant="h5" gutterBottom>
            Portfolio Value
          </Typography>
          <Typography variant="h3" component="div" sx={{ mb: 2 }}>
            ${summary.totalValue.toFixed(2)}
          </Typography>

          <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', md: 'repeat(2, 1fr)' }, gap: 3 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
              {isDayPositive ? (
                <TrendingUpIcon sx={{ color: 'success.main' }} />
              ) : (
                <TrendingDownIcon sx={{ color: 'error.main' }} />
              )}
              <Box>
                <Typography variant="body2" color="text.secondary">
                  Today's Change
                </Typography>
                <Typography
                  variant="h6"
                  sx={{ color: isDayPositive ? 'success.main' : 'error.main' }}
                >
                  {isDayPositive ? '+' : ''}${summary.dayChange.toFixed(2)} (
                  {isDayPositive ? '+' : ''}
                  {summary.dayChangePercent.toFixed(2)}%)
                </Typography>
              </Box>
            </Box>

            <Box>
              <Typography variant="body2" color="text.secondary">
                Total Profit/Loss
              </Typography>
              <Typography
                variant="h6"
                sx={{ color: isTotalPositive ? 'success.main' : 'error.main' }}
              >
                {isTotalPositive ? '+' : ''}${summary.totalGainLoss.toFixed(2)} (
                {isTotalPositive ? '+' : ''}
                {summary.totalGainLossPercent.toFixed(2)}%)
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>
                Cost Basis: ${summary.totalCost.toFixed(2)}
              </Typography>
            </Box>
          </Box>
        </CardContent>
      </Card>

      <Card elevation={3}>
        <CardContent>
          <Typography variant="h5" gutterBottom sx={{ mb: 2 }}>
            Holdings
          </Typography>

          {summary.holdings.length === 0 ? (
            <Typography color="text.secondary" sx={{ textAlign: 'center', py: 3 }}>
              No holdings yet. Start by searching for stocks to buy.
            </Typography>
          ) : (
            <TableContainer component={Paper} variant="outlined">
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>Symbol</TableCell>
                    <TableCell>Name</TableCell>
                    <TableCell align="right">Quantity</TableCell>
                    <TableCell align="right">Avg Cost</TableCell>
                    <TableCell align="right">Current Price</TableCell>
                    <TableCell align="right">Current Value</TableCell>
                    <TableCell align="right">Gain/Loss</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {summary.holdings.map((holding) => {
                    const isPositive = holding.gainLoss >= 0;
                    return (
                      <TableRow
                        key={holding.stock.symbol}
                        hover
                        sx={{ cursor: 'pointer' }}
                        onClick={() => navigate(`/stock/${holding.stock.symbol}`)}
                      >
                        <TableCell>
                          <Typography fontWeight="bold">{holding.stock.symbol}</Typography>
                        </TableCell>
                        <TableCell>{holding.stock.name}</TableCell>
                        <TableCell align="right">{holding.quantity}</TableCell>
                        <TableCell align="right">${holding.averageCost.toFixed(2)}</TableCell>
                        <TableCell align="right">
                          ${holding.stock.currentPrice.toFixed(2)}
                        </TableCell>
                        <TableCell align="right">
                          <Typography fontWeight="medium">
                            ${holding.currentValue.toFixed(2)}
                          </Typography>
                        </TableCell>
                        <TableCell align="right">
                          <Box>
                            <Typography
                              sx={{ color: isPositive ? 'success.main' : 'error.main' }}
                              fontWeight="medium"
                            >
                              {isPositive ? '+' : ''}${holding.gainLoss.toFixed(2)}
                            </Typography>
                            <Typography
                              variant="body2"
                              sx={{ color: isPositive ? 'success.main' : 'error.main' }}
                            >
                              ({isPositive ? '+' : ''}
                              {holding.gainLossPercent.toFixed(2)}%)
                            </Typography>
                          </Box>
                        </TableCell>
                      </TableRow>
                    );
                  })}
                </TableBody>
              </Table>
            </TableContainer>
          )}
        </CardContent>
      </Card>
    </Box>
  );
}
