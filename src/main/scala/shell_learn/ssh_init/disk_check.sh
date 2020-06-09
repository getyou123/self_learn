#!/bin/bash
#
# disk capacity check
# ./disk_check.sh $ip_list
#

CUR_DIR=$(cd `dirname $0`; pwd)

IP_LIST=$1

for IP in $(cat $CUR_DIR/$IP_LIST)
do
    echo $IP
    ssh root@$IP "df -h"
    echo " "
done
