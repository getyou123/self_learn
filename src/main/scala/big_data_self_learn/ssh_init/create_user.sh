#!/bin/bash
#
# create user on search node
# ./create_user.sh $root_pwd $user $user_pwd $ip_list
#

CUR_DIR=$(cd `dirname $0`; pwd)

if [ $# -lt 4 ]; then
    echo "FATAL para not enough"
    exit 1
fi
ROOT_PWD=$1
USER=$2
USER_PWD=$3
IP_LIST=$4

for IP in $(cat $IP_LIST)
do
    expect -c "
    spawn ssh root@$IP \"useradd $USER; passwd $USER\"
    expect {
        \"*yes/no*\" {send \"yes\r\"; exp_continue}
        \"root*password*\" {send \"$ROOT_PWD\r\"; exp_continue}
        \"*New password*\" {send \"$USER_PWD\r\"; exp_continue}
        \"*new password*\" {send \"$USER_PWD\r\"; exp_continue}
    }
    "
    
    expect -c "
    spawn ssh root@$IP \"mkdir -p /data/$USER /ssd0/$USER; chgrp -R $USER /data/$USER /ssd0/$USER; chown -R $USER /data/$USER /ssd0/$USER\"
    expect {
        \"root*password*\" {send \"$ROOT_PWD\r\"; exp_continue}
    }
    "
done
