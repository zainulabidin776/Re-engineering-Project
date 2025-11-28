# Setup Instructions - POS System

## Prerequisites

Before setting up the application, ensure you have the following installed:

- **Java 17+** (JDK)
- **Maven 3.6+**
- **Node.js 18+** and npm
- **PostgreSQL 12+**

## Step-by-Step Setup

### 1. Database Setup

#### Create PostgreSQL Database

```sql
-- Connect to PostgreSQL
psql -U postgres

-- Create database
CREATE DATABASE pos_db;

-- Connect to the database
\c pos_db

-- Run the schema
\i database/schema.sql
```

Or if you're in the project directory:

```bash
psql -U postgres -d pos_db -f database/schema.sql
```

### 2. Backend Setup

#### Navigate to Backend Directory

```bash
cd pos-backend
```

#### Configure Database Connection

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/pos_db
spring.datasource.username=your_postgres_username
spring.datasource.password=your_postgres_password
```

#### Build and Run Backend

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

**Verify Backend is Running:**
```bash
curl http://localhost:8080/api/auth/health
```

### 3. Frontend Setup

#### Navigate to Frontend Directory

```bash
cd pos-frontend
```

#### Install Dependencies

```bash
npm install
```

#### Configure API Endpoint (if needed)

If your backend runs on a different port, edit `src/services/api.ts`:

```typescript
const API_BASE_URL = 'http://localhost:8080/api';
```

#### Run Development Server

```bash
npm run dev
```

The frontend will start on `http://localhost:3000`

### 4. Data Migration (Optional)

If you want to migrate data from the legacy `.txt` files:

1. Ensure the backend is running
2. Make sure the `Database/` folder is accessible
3. Call the migration endpoint:

```bash
curl -X POST "http://localhost:8080/api/migration/migrate?databasePath=Database"
```

Or use Postman/Insomnia to make the POST request.

## Running the Application

### Start Backend

```bash
cd pos-backend
mvn spring-boot:run
```

### Start Frontend (in a new terminal)

```bash
cd pos-frontend
npm run dev
```

### Access the Application

Open your browser and navigate to:
```
http://localhost:3000
```

## Default Login Credentials

After migrating data from legacy files, use the credentials from `Database/employeeDatabase.txt`.

If no migration was performed, you'll need to create an employee first (requires database access or admin account).

## Troubleshooting

### Backend Issues

**Port 8080 already in use:**
- Change port in `application.properties`: `server.port=8081`

**Database connection error:**
- Verify PostgreSQL is running
- Check database credentials in `application.properties`
- Ensure database `pos_db` exists

**Build errors:**
- Ensure Java 17+ is installed: `java -version`
- Ensure Maven is installed: `mvn -version`
- Clean and rebuild: `mvn clean install`

### Frontend Issues

**Port 3000 already in use:**
- Change port in `vite.config.ts`: `server: { port: 3001 }`

**npm install fails:**
- Clear npm cache: `npm cache clean --force`
- Delete `node_modules` and `package-lock.json`, then reinstall

**API connection errors:**
- Verify backend is running on port 8080
- Check CORS settings in backend `SecurityConfig.java`
- Check browser console for detailed error messages

### Database Issues

**Schema creation fails:**
- Ensure PostgreSQL user has CREATE privileges
- Check PostgreSQL logs for detailed errors
- Verify UUID extension is available: `CREATE EXTENSION IF NOT EXISTS "uuid-ossp";`

**Migration fails:**
- Verify file paths are correct
- Check file permissions
- Ensure backend has read access to `Database/` folder

## Production Deployment

### Backend Deployment

1. Build JAR file:
   ```bash
   cd pos-backend
   mvn clean package
   ```

2. Run JAR:
   ```bash
   java -jar target/pos-backend-1.0.0.jar
   ```

3. Or deploy to cloud (AWS, Heroku, etc.)

### Frontend Deployment

1. Build for production:
   ```bash
   cd pos-frontend
   npm run build
   ```

2. Deploy `dist/` folder to static hosting:
   - Netlify
   - Vercel
   - AWS S3 + CloudFront
   - Any static file server

3. Update API endpoint in production build if needed

## Development Workflow

### Making Changes

1. **Backend Changes:**
   - Edit Java files in `pos-backend/src/main/java/`
   - Restart Spring Boot application
   - Changes are hot-reloaded (if dev tools enabled)

2. **Frontend Changes:**
   - Edit React files in `pos-frontend/src/`
   - Vite automatically reloads on save

### Testing

**Backend Tests:**
```bash
cd pos-backend
mvn test
```

**Frontend Tests:**
```bash
cd pos-frontend
npm test
```

## Project Structure Reference

```
.
├── pos-backend/          # Spring Boot backend
│   ├── src/main/java/   # Java source code
│   └── pom.xml          # Maven configuration
├── pos-frontend/         # React frontend
│   ├── src/             # React source code
│   └── package.json     # npm configuration
├── database/            # Database scripts
│   └── schema.sql       # PostgreSQL schema
├── docs/                # Documentation
└── Database/            # Legacy data files (for migration)
```

## Support

For issues or questions:
1. Check the documentation in `docs/` folder
2. Review `PROJECT_COMPLETION.md` for project overview
3. Check GitHub repository: https://github.com/zainulabidin776/Re-engineering-Project

---

**Last Updated**: 2025-11-28

