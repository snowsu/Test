#!/bin/sh  
# -----------------------------------------------------------------------------  
# Start script for the jun_crawler   
#  
# -----------------------------------------------------------------------------  
RUN_HOME=.  
CLASSPATH=$CLASSPATH:$RUN_HOME/lib/*.jar  
export CLASSPATH  

nohup java -jar jun_crawler.jar &