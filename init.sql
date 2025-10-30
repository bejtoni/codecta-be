IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'image_cropper')
BEGIN
    CREATE DATABASE [image_cropper];
END
GO
