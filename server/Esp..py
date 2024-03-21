import time
import serial
from csv import DictWriter
import pandas as pd
SCREEN_DISPLAY=True
SAVE_TO_FILE=True
DELIMITER=','

#SERIAL_PORT='/dev/ttyACM0' # serial port terminal
SERIAL_PORT='COM6'

file_name= 'output_555.csv'


scale=serial.Serial(SERIAL_PORT,timeout=20,baudrate=115200)

file = pd.read_csv("output_555.csv")
headerList = ['SSID', 'RSSI', 'MAC','TIME','DISTANCE']
file.to_csv("output_555.csv", header=headerList, index=False)
    

while True:
    
    fid= open(file_name,'a')
    str_scale=scale.readline()
    time_now=time.strftime("%H:%M:%S")
    RSSI=0
    SSID=''
    MAC=''
    DIST=0
    start=0
    stop=0
    p=str_scale.decode()
    
    if len(p)>10:
        
        for i in range(len(p)):
            if p[i]=='(':
                start=i
                break
        RSSI=int(p[start+1:start+4])
        for i in range(len(p)):
            if p[i]=='[':
                start=i
            if p[i]==']':
                stop=i
                break
        SSID=p[start+1:stop]
        for i in range(len(p)):
            if p[i]=='<':
                start=i
                break
        MAC=p[start+1:start+18]
        
        print(RSSI,SSID,MAC)
    
            
        
    #print(str_scale)
    if SCREEN_DISPLAY: print(str.encode(time_now+DELIMITER)+str_scale)
    # in seconds
    B_SSID=bytes(SSID,'utf_8')
    B_RSSI=bytes(str(RSSI),'utf_8')
    B_MAC=bytes(MAC,'utf_8')
    DISTANCE=(10**((3-RSSI)/(10*2)))/100
    field_names=['SSID','RSSI','MAC','TIME','DISTANCE']
    #print(DATE,time)
    dict={'SSID':SSID,'RSSI':RSSI,'MAC':MAC,'TIME':time_now,"DISTANCE":DISTANCE}
    dictwriter_object=DictWriter(fid,fieldnames=field_names)
    dictwriter_object.writerow(dict)    
    

    fid.close()

scale.close()