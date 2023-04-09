# FCM-Gateway-Android-client

## Introduction

This application is part of the FCM Gateway infrastructure.
It allows you to receive FCM messages through the Gateway and forward them to other apps
as broadcastet intents.

To get this app up and running, you'll need to copy your own google-service.json into /app.
To acquire that one, just follow [this tutorial](https://firebase.google.com/docs/android/setup?hl=de#console).

## Usage
Once this app is running, whether it is through the Play store or your own work, you'll need
the app to generate a registration token. For that just open the app, the prompt should tell you how to 
obtain the token.
With that token, you can start sending messages to the API.

The app should work when closed or in background.

### Forwarding intents

The data received by this application through the gateway can contain the following fields

```
{
    "type": "(optional) only 'intent' is accepted and is also the default",
    "application": "(optional) the target package name to send the intent to. Broadcastet to all apps if left out",
    "action": "(optional) the action set for the intent",
    "extras": {
        "EXTRA_KEY_1": "(optional) a first extra entry. Can contain ints, floats, booleans or strnings",
        "EXTRA_KEY_2": "(optional) a second extra entry. Can contain ints, floats, booleans or strnings",
        "EXTRA_KEY_3": "(optional) the whole 'extras' entry is optional",
    }
}
```