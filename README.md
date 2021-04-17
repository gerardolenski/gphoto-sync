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

#Local library configuration
PHOTO_LIBRARY_PATH=/the/path/to/local/library/root/directory

# Performance configuration
PHOTO_ALBUMS_CONCURRENCY=5
PHOTO_UPLOAD_CONCURRENCY=3
PHOTO_UPLOAD_BULK_SIZE=10
```

where:
- `PHOTO_GOOGLE_CREDENTIAL_DIR` - the local directory where Google credentials are stored (**mandatory**),
- `PHOTO_GOOGLE_CREDENTIAL_FILE` - the name of the credential JSON file taken from Google (`credentials.json` by default),
- `PHOTO_GOOGLE_USER_ID` - the user id used for connection with Google Photo Library (**mandatory**),
- `PHOTO_GOOGLE_RECEIVER_PORT` - the local port which is used by Google Photo Library Client (`61984` by default),
- `PHOTO_LIBRARY_PATH` - the local directory with root path containing the image library (**mandatory**),
- `PHOTO_ALBUMS_CONCURRENCY` - number of concurrent threads synchronizing albums (`5` by default),
- `PHOTO_UPLOAD_CONCURRENCY` - number of concurrent threads uploading images (`3` by default),
- `PHOTO_UPLOAD_BULK_SIZE` - number of image bulk size linking to remote album. According to Google specification must be less than 50 (`10` by default).

## Build and run
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