#! /bin.bash
# 执行jar程序
echo "Stopping SpringBoot Application"
pid=`ps -ef | grep /root/main-app-1.0.jar | grep -v grep | awk '{print $2}'`
echo $pid
echo "--------kill start--------------"
if [ -n "$pid" ]
then
   echo "kill -9 的pid:" $pid
   kill -9 $pid
fi
echo "--------kill finish-----------------"
echo "--------replace start--------------"
DATE=$(date +%Y%m%d%H%M%S)
mv /root/main-app-1.0.jar /root/jarbak/main-app-1.0.jar.$DATE.bak
mv /root/jar-resource/main-app-1.0.jar /root/
echo "--------replace finish-----------------"
echo "Execute shell Finish"
sh startup.sh
