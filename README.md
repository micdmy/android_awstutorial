Tutorial from:
https://aws.amazon.com/getting-started/hands-on/build-android-app-amplify/

Tutorials git repo:
https://github.com/sebsto/amplify-ios-getting-started


generate API java code for android application from graphql:
amplify codegen models

deploy API to AWS:
amplify api gql-compile
amplify api push

Todo steps:

1 define data scheme for items that can have location and owner - DONE
2 make CRUD gui for user's items - DONE (except update)
3 get current location from gps - DONE
4 allow updating item location with current location
  4a can add item with location or without it - DONE
  4b move location boilerplate code to another service / classes - DONE
  4c manage location permission in situations like moving app to background, disabling permission in runtime or beetween app runs
    4cI Add logs from lifecycle callback of MainActivity and LocationService - DONE
5 show arbitrary location on the map -DONE
