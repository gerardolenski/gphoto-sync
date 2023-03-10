# Google Photo Library Synchronizer

This project uploads local photo library to Google photo-library service.

## Configuration
All environment variables used by the service:
```
# Google cloud configuration
PHOTO_GOOGLE_CREDENTIAL_DIR=/the/path/to/google/credential/directory
PHOTO_GOOGLE_CREDENTIAL_FILE=credentials.json
PHOTO_GOOGLE_USER_ID=google.user.id
PHOTO_GOOGLE_RECEIVER_PORT=61984

# Local library configuration
PHOTO_LIBRARY_PATH=/the/path/to/local/library/root/directory

# Local albums filtering configuration
PHOTO_ALBUMS_FROM_YEAR_FILTER=
PHOTO_ALBUMS_TO_YEAR_FILTER=

# Performance configuration
PHOTO_ALBUMS_CONCURRENCY=5
PHOTO_UPLOAD_CONCURRENCY=3
PHOTO_UPLOAD_BULK_SIZE=10

# Logging
PHOTO_LOG_LEVEL=info
```

where:
- `PHOTO_GOOGLE_CREDENTIAL_DIR` - the local directory where Google credentials are stored (**mandatory**),
- `PHOTO_GOOGLE_CREDENTIAL_FILE` - the name of the credential JSON file taken from Google (`credentials.json` by default),
- `PHOTO_GOOGLE_USER_ID` - the user id used for connection with Google Photo Library (**mandatory**),
- `PHOTO_GOOGLE_RECEIVER_PORT` - the local port which is used by Google Photo Library Client (`61984` by default),
- `PHOTO_LIBRARY_PATH` - the local directory with root path containing the image library (**mandatory**),
- `PHOTO_ALBUMS_FROM_YEAR_FILTER` - lower filter of the album year (`no value` by default which disables filtering),
- `PHOTO_ALBUMS_TO_YEAR_FILTER` - upper filter of the album year (`no value` by default which disables filtering),
- `PHOTO_ALBUMS_CONCURRENCY` - number of concurrent threads synchronizing albums (`5` by default),
- `PHOTO_UPLOAD_CONCURRENCY` - number of concurrent threads uploading images (`3` by default),
- `PHOTO_UPLOAD_BULK_SIZE` - number of image bulk size linking to remote album. According to Google specification must be less than 50 (`10` by default).
- `PHOTO_LOG_LEVEL` - the logging level: `info`, `debug`, `trace`. (`info` by default)

## Run as docker container
Go into docker-compose directory and edit `.env` file with correct `PHOTO_GOOGLE_CREDENTIAL_DIR`, `PHOTO_GOOGLE_USER_ID` and `PHOTO_LIBRARY_PATH`.
Then execute:
```
docker-compose up
``` 

## Build and run jar
To build the project use maven command:
```
mvn clean package
```

To run the project using jar file:
```
java -DPHOTO_GOOGLE_CREDENTIAL_DIR=/home/my-account/.google/photolibrary \
  -DPHOTO_GOOGLE_USER_ID=my-google-id@google.com \
  -DPHOTO_LIBRARY_PATH=/media/my-library \
  -jar google-photo-synchronizer-0.1.0.jar
```