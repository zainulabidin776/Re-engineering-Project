# POS Frontend - React Application

## Overview

Modern React + TypeScript frontend for the reengineered POS system.

## Prerequisites

- Node.js 18+
- npm or yarn

## Setup

### 1. Install Dependencies

```bash
npm install
```

### 2. Configuration

The frontend is configured to connect to the backend at `http://localhost:8080`. Update `src/services/api.ts` if your backend runs on a different port.

### 3. Run Development Server

```bash
npm run dev
```

The application will be available at `http://localhost:3000`

## Features

### Cashier Dashboard
- **Sales**: Process sales transactions with item selection, tax calculation, and coupon support
- **Rentals**: Create rental transactions with customer lookup and due date management
- **Returns**: Process returns for outstanding rentals with overdue tracking

### Admin Dashboard
- **Employee Management**: Create, update, and delete employees
- **Inventory Management**: View and update item quantities

## Project Structure

```
src/
├── components/          # Reusable components
│   ├── SalesPage.tsx
│   ├── RentalsPage.tsx
│   ├── ReturnsPage.tsx
│   ├── EmployeeManagement.tsx
│   └── InventoryManagement.tsx
├── pages/              # Page components
│   ├── Login.tsx
│   ├── CashierDashboard.tsx
│   └── AdminDashboard.tsx
├── services/           # API services
│   ├── api.ts
│   ├── authService.ts
│   ├── itemService.ts
│   └── saleService.ts
├── context/            # React context
│   └── AuthContext.tsx
└── App.tsx             # Main app component
```

## Build for Production

```bash
npm run build
```

The built files will be in the `dist/` directory.

