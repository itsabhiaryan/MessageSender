/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.its.fcm;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

// NOTE:
// This class emulates a server for the purposes of this sample,
// but it's not meant to serve as an example for a production app server.
// This class should also not be included in the client (Android) application
// since it includes the server's API key. For information on FCM server
// implementation see: https://developers.google.com/cloud-messaging/server

public class FcmSender {

    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2 || args[0] == null) {
            System.err.println("usage: ./gradlew run -Pmsg=\"MESSAGE\" [-Pto=\"DEVICE_TOKEN\"]");
            System.err.println("");
            System.err.println("Specify a test message to broadcast via GCM. If a device's FCM registration token is\n" +
                    "specified, the message will only be sent to that device. Otherwise, the message \n" +
                    "will be sent to all devices subscribed to the \"global\" topic.");
            System.err.println("");
            System.err.println("Example (Broadcast):\n" +
                    "On Windows:   .\\gradlew.bat run -Pmsg=\"<Your_Message>\"\n" +
                    "On Linux/Mac: ./gradlew run -Pmsg=\"<Your_Message>\"");
            System.err.println("");
            System.err.println("Example (Unicast):\n" +
                    "On Windows:   .\\gradlew.bat run -Pmsg=\"<Your_Message>\" -Pto=\"<Your_Token>\"\n" +
                    "On Linux/Mac: ./gradlew run -Pmsg=\"<Your_Message>\" -Pto=\"<Your_Token>\"");
            System.exit(1);
        }
        try {
            // Prepare JSON containing the FCM message content. What to send and where to send.
            JSONObject fcmData = new JSONObject();

            // Where to send FCM message.
            fcmData.put("to", "/topics/news");
            //fcmData.put("to","deviceToken.trim()");

           /* if (args.length > 1 && args[1] != null) {
                fcmData.put("to", args[1].trim());
            } else {
                fcmData.put("to", "/topics/global");
            }*/

            // What to send in FCM message.

            JSONObject notificationData = new JSONObject();

            notificationData.put("title", ServerData.Tittle[0]); // Notification title
            notificationData.put("body", ServerData.TEXT[0]); // Notification body
            notificationData.put("sound","default");
            notificationData.put("icon", "ic_launcher");    // change icon

            fcmData.put("notification",notificationData);

            // Create connection to send FCM Message request.
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "key=" + ServerData.API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");

            // Send FCM message content.
            OutputStreamWriter outputStream = new OutputStreamWriter(conn.getOutputStream());
            outputStream.write(fcmData.toString());
            outputStream.flush();

            // Read FCM response.
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

            System.out.println("Check your device/emulator for notification or logcat for " +
                    "confirmation of the receipt of the FCM message.");
        } catch (IOException e) {
            System.out.println("Unable to send FCM message.");
            System.out.println("Please ensure that API_KEY has been replaced by the server " +
                    "API key, and that the device's registration token is correct (if specified).");
            e.printStackTrace();
        }
    }

}
