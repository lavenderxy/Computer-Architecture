#!/usr/bin/python
import sys
import os

if (len(sys.argv) !=  2):
    print "Usage manySim.py <tracename>"
else:
    traceName = sys.argv[1]
    for x in range(1, 16):
        os.system("java HW2a -t "+traceName+" -loadlat "+str(x))
