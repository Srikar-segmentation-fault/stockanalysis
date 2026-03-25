# AI Stock Analysis - Frontend

React + TypeScript frontend for the AI-Powered Stock Analysis Engine.

## Tech Stack

- **React 19** - UI framework
- **TypeScript** - Type safety
- **Vite** - Build tool and dev server
- **React Router** - Client-side routing
- **Material-UI (MUI)** - UI component library
- **Zustand** - State management
- **Emotion** - CSS-in-JS styling

## Project Structure

```
src/
├── components/     # Reusable UI components
│   └── Layout.tsx  # Main layout with navigation
├── pages/          # Page components
│   ├── Dashboard.tsx
│   ├── StockSearch.tsx
│   └── StockDetails.tsx
├── services/       # API service layer
│   └── api.ts
├── store/          # Zustand state management
│   └── portfolioStore.ts
├── types/          # TypeScript type definitions
│   └── index.ts
├── App.tsx         # Main app with routing
└── main.tsx        # Entry point
```

## Getting Started

### Install Dependencies

```bash
npm install
```

### Development Server

```bash
npm run dev
```

The app will be available at `http://localhost:5173`

### Build for Production

```bash
npm run build
```

### Preview Production Build

```bash
npm run preview
```

## Environment Variables

Create a `.env` file in the frontend directory:

```
VITE_API_BASE_URL=http://localhost:8080/api
```

## Available Routes

- `/` - Portfolio Dashboard
- `/search` - Stock Search
- `/stock/:symbol` - Stock Details

## Backend Integration

The frontend expects the Spring Boot backend to be running on `http://localhost:8080`.

API endpoints are accessed through the `api` service in `src/services/api.ts`.
