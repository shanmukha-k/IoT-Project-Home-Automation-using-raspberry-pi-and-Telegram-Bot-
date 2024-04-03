# IoT-Project-Home-Automation-using-raspberry-pi-and-Telegram-Bot-
A Project based on implementing home automation using raspberry pi as CPU, Groove pi as Integrating unit, and Telegram Bot as User interaction unit.

We provided the python code which enables the CPU to get instructions from user through Telegram Bot and execute the actions accordingly. And also monitors the surroundings and detectes if any intrusion occured
and then passes the alert notification through telegram.

Firstly, it is important to setup the model properly:

1. Install OS to write it in SD card of raspberry pi - https://downloads.raspberrypi.com/raspios_full_armhf/images/raspios_full_armhf-2024-03-15/2024-03-15-raspios-bookworm-armhf-full.img.xz?_gl=1*1n5ej4m*_ga*MTA2Mzk4Mjk5OS4xNzA4NTE5MTk4*_ga_22FD70LWDS*MTcxMjEzODM2Mi41LjEuMTcxMjEzODM3OS4wLjAuMA..
2. Write the SD Card using Raspberry pi OS application.
3. Mention a hotspot or wifi credential after writing the SD to enble raspberry pi connect to a network.
4. Enable SSH
5. And access raspberry pi using SSH 
6. Follow this video to setup easily - https://www.youtube.com/watch?v=I-vCFP2jD1g


Secondly, Coding:
1. Create a python file and write the code in the file in raspberry pi.
2. Update the code accordingly and make sure you replace the BOT token ID, Chat ID and API's token key file path (.json file) correctly

Finally, Execution:
1. Make sure all the components are working properly use some testing codes first before using the components for the main code.
2. Make your every component is connected correctly.
3. Run the python code in SSH terminal and test the model

Components required:
1. Raspberry Pi
2. Groove Pi
3. Groove LED
4. Ultra sonic Ranger
5. Groove Connectors/Groove jumpers
6. An SD Card


Demo:
![image](https://github.com/shanmukha-k/IoT-Project-Home-Automation-using-raspberry-pi-and-Telegram-Bot-/assets/99649721/d810478b-ab7f-41e2-a4e5-5352026981f9)
