# Docker Compose Guide

## What's in the Docker Compose File

This Docker Compose setup runs three services that work together:

1. **Database (db)** - A Microsoft SQL Server 2022 database that stores all the data. It creates a database called `image_cropper` automatically when it starts up.

2. **Backend** - The Java Spring Boot application that handles all the server-side logic. It connects to the database and runs on port 5000.

3. **Frontend** - The web application that users interact with. It connects to the backend and runs on port 3000.

All three services are connected on the same network so they can talk to each other.

## Prerequisites

Before you can use Docker Compose, you need to set up your folders and environment files:

1. **Folder Structure** - Make sure both the backend and frontend are in the same parent folder:

   ```
   parent-folder/
   ├── imagecropper-be/
   └── imagecropper-fe/
   ```

2. **Environment Files** - You need to create `.env` files in both folders:
   - Create a `.env` file inside `imagecropper-be/` folder
   - Create a `.env` file inside `imagecropper-fe/` folder
   - Add all the required environment variables to these files (both files will be attached in the email)
   - Due to specific configurations you are required to change the DB_PASS variable in the .env file to a password matching your setup
## Commands

### Building and Starting Docker Compose

To build and start all the services run the following command in the parent folder where the docker-compose.yml is:

```bash
docker-compose up --build
```

The `--build` flag ensures that Docker rebuilds the images before starting them.

To run in the background (detached mode):

```bash
docker-compose up --build -d
```

### Stopping Docker Compose

To stop all running containers (but keep them):

```bash
docker-compose stop
```

### Shutting Down Docker Compose

To stop and remove all containers:

```bash
docker-compose down
```

### Rebuilding Everything (For Testing)

If you need to completely remove everything and start fresh (this will delete all data in the database):

```bash
docker-compose down -v
```

The `-v` flag removes all volumes, which means the database will be wiped clean. Use this when you want to start from scratch for testing purposes.

After running `down -v`, you can start again with:

```bash
docker-compose up --build
```

## Testing

After successfully replacing the environment variables and running the `docker-compose up --build` command, you can test the application:

1. Wait for all services to start completely (this may take a minute or two for the database and backend to initialize).

2. Open your web browser or Docker Desktop application.

3. Navigate to `http://localhost:3000` to access the frontend application.

4. Test the application functionality:
   - Sign in with Google OAuth
   - Upload a PNG image
   - Configure logo settings
   - Crop images and preview results
   - Generate final images with logo overlays

The frontend will be available on port 3000, and the backend API will be accessible on port 5000.