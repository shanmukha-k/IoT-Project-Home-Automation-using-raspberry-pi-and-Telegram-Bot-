import os
import time
import requests
import telepot
import grovepi
from telepot.loop import MessageLoop
from grovepi import *
import speech_recognition as sr
from pydub import AudioSegment

os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = "{path of downloaded API Key JSON file}"

led = 2  # Connect LED GrovePi module on D2
ultrasonic_ranger = 4  # Connect Ultrasonic Ranger to digital port D4

# LED White
pinMode(led, "OUTPUT")
time.sleep(1)
digitalWrite(led, 0)  # LED off initially

def send_telegram_message(message, chat_id):
    telegram_bot.sendMessage(chat_id, message)

def process_voice_message(audio_file):
    recognizer = sr.Recognizer()

    with sr.AudioFile(audio_file) as source:
        audio_data = recognizer.record(source)

    try:
        command = recognizer.recognize_google(audio_data).lower()
    except sr.UnknownValueError:
        command = ""
    except sr.RequestError as e:
        print("Could not request results from Google Speech Recognition service; {0}".format(e))
        command = ""

    return command

def download_voice_message(file_id):
    token = '{BOT TOKEN ID}'  # Your bot's API token
    url = f'https://api.telegram.org/bot{token}/getFile?file_id={file_id}'
    response = requests.get(url)
    file_path = response.json()['result']['file_path']
    file_url = f'https://api.telegram.org/file/bot{token}/{file_path}'
    audio_file_ogg = f'/tmp/{file_id}.ogg'
    audio_file_wav = f'/tmp/{file_id}.wav'
    with open(audio_file_ogg, 'wb') as f:
        f.write(requests.get(file_url).content)
    audio = AudioSegment.from_ogg(audio_file_ogg)
    audio.export(audio_file_wav, format="wav")
    return audio_file_wav

def detect_intruder():
    try:
        #print("Running sensor")
        distance = grovepi.ultrasonicRead(ultrasonic_ranger)
        #if distance < 20:  # Adjust threshold as needed
           # print('Intruder detected at Distance:%d' %distance)
        return distance
        #else:
         #   return False
    except Exception as e:
        print("Error detecting intruder", e)
        return 100

def action(msg):
    chat_id = msg['chat']['id']
    content_type, chat_type, chat_id = telepot.glance(msg)

    if content_type == 'text':
        command = msg['text'].lower()

        print('Received text command: %s' % command)

        if "on" in command:
            message = "Turned on LED"
            digitalWrite(led, 1)
            send_telegram_message(message, chat_id)
        elif "off" in command:
            message = "Turned off LED"
            digitalWrite(led, 0)
            send_telegram_message(message, chat_id)
    elif content_type == 'voice':
        file_id = msg['voice']['file_id']
        try:
            audio_file = download_voice_message(file_id)
            command = process_voice_message(audio_file)

            print('Received voice command: %s' % command)

            if "on" in command:
                message = "Turned on LED"
                digitalWrite(led, 1)
                send_telegram_message(message, chat_id)
            elif "off" in command:
                message = "Turned off LED"
                digitalWrite(led, 0)
                send_telegram_message(message, chat_id)
            os.remove(audio_file)
        except Exception as e:
            print("Your Internet Connection is Unstable!!!", e)

telegram_bot = telepot.Bot('{BOT TOKEN ID}')
MessageLoop(telegram_bot, action).run_as_thread()

print('Up and Running....')

try:
    while True:
        distance=detect_intruder()
        if distance<20:
            print("Intruder detected at Distance: %dcm" %distance)
            message = "Intruder detected! at %dcm" %distance
            send_telegram_message(message, '{MAIN USER CHAT ID}')
        time.sleep(2)

except KeyboardInterrupt:
    print("Program Stopped")
finally:
    print("Releasing resources")
    digitalWrite(led, 0)
    sys.exit(0)
