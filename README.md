# ESRGAN-Android-Demo
This application shows the demo of the Esrgan model in a Android application.

The ESRGAN is a Super Resolution Generative Adversial Network which has a very high accuracy.It generates the output dimensions which are 4 times the input dimensions.(https://arxiv.org/abs/1809.00219)

This application is created to show the demo of the inference of this model on an Android Device.The model has been deployed on the device using TfLite and MLKit.The model can convert a 128x128 image to 512x512 image.The model was obtained from here-https://github.com/margaretmz/esrgan-e2e-tflite-tutorial

# Application Details
1.When the user starts the application the camera will be turned on and on the top corner there is a file button.The file button will allow you to choose files from your device.

2.When you click the image the application will immediately start processing it and show the result

3.When you choose the file chooser the application will open system file chooser and allow to choose only image files.Once selected the application will process the image itself.

**Note-For neat output the aspect raio of image has been set to 4:3 so the output images will not be directly of 512x512.**

**All the processing happens on a background thread**

# Classes
1.**Constant**-To store the constant values

2.**MainActivity**-The entry point of the file

3.**Processor**-The activity that processes the image / infers the model

4.**Show Image**-Shows the processed image

5.**Utils**-Some utility functions
