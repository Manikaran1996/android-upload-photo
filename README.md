# android-upload-image
This repository contains android project to connect to a service (in my case written in python flask) to upload an image selected by the user. The image is sent to the service in base64 encoding.
Steps-
1. Read image as bytes.
2. Convert image to base64 encoding.
3. Create StringRequest in volley with POST as method, pass image string as parameter.

