import { useState, useEffect } from 'react';
import {
  Box,
  TextField,
  List,
  ListItem,
  ListItemButton,
  ListItemText,
  Paper,
  Typography,
  CircularProgress,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { api } from '../services/api';
import type { Stock } from '../types';

export default function StockSearchComponent() {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState<Stock[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    const searchStocks = async () => {
      if (query.trim().length < 1) {
        setResults([]);
        return;
      }

      setLoading(true);
      setError(null);

      try {
        const data = await api.get(`/stocks/search?query=${encodeURIComponent(query)}`);
        setResults(data);
      } catch (err) {
        setError('Failed to search stocks. Please try again.');
        setResults([]);
      } finally {
        setLoading(false);
      }
    };

    const debounceTimer = setTimeout(searchStocks, 300);
    return () => clearTimeout(debounceTimer);
  }, [query]);

  const handleStockSelect = (symbol: string) => {
    navigate(`/stock/${symbol}`);
  };

  return (
    <Box>
      <TextField
        fullWidth
        label="Search stocks by symbol or name"
        variant="outlined"
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        placeholder="e.g., AAPL or Apple"
        sx={{ mb: 2 }}
      />

      {loading && (
        <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}>
          <CircularProgress />
        </Box>
      )}

      {error && (
        <Typography color="error" sx={{ p: 2 }}>
          {error}
        </Typography>
      )}

      {!loading && results.length > 0 && (
        <Paper elevation={2}>
          <List>
            {results.map((stock) => (
              <ListItem key={stock.id} disablePadding>
                <ListItemButton onClick={() => handleStockSelect(stock.symbol)}>
                  <ListItemText
                    primary={
                      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <Typography variant="subtitle1" fontWeight="bold">
                          {stock.symbol}
                        </Typography>
                        <Typography
                          variant="body2"
                          color={stock.dayChange >= 0 ? 'success.main' : 'error.main'}
                        >
                          ${stock.currentPrice.toFixed(2)} ({stock.dayChange >= 0 ? '+' : ''}
                          {stock.dayChangePercent.toFixed(2)}%)
                        </Typography>
                      </Box>
                    }
                    secondary={stock.name}
                  />
                </ListItemButton>
              </ListItem>
            ))}
          </List>
        </Paper>
      )}

      {!loading && query.trim().length > 0 && results.length === 0 && !error && (
        <Typography color="text.secondary" sx={{ p: 2, textAlign: 'center' }}>
          No stocks found matching "{query}"
        </Typography>
      )}
    </Box>
  );
}
