#!/bin/bash
#
# modify operating system parameters
# ./modify_para.sh $root_pwd $ip_list
#

#
# 注意修改的东西，有些是不需要的
#


CUR_DIR=$(cd `dirname $0`; pwd)

if [ $# -lt 2 ]; then
    echo "FATAL para not enough"
    exit 1
fi
ROOT_PWD=$1
IP_LIST=$2

for IP in $(cat $CUR_DIR/$IP_LIST)
do
    expect -c "
    spawn ssh root@$IP \"swapoff -a; \
    /sbin/sysctl -w net.core.wmem_max=1048576; \
    /sbin/sysctl -w net.core.rmem_max=1048576; \"
    expect {
        \"*yes/no*\" {send \"yes\r\"; exp_continue}
        \"root*password*\" {send \"$ROOT_PWD\r\"; exp_continue}
    }
    "
    expect -c "
    spawn scp $CUR_DIR/sysctl.conf root@$IP:/etc
    expect {
        \"*yes/no*\" {send \"yes\r\"; exp_continue}
        \"root*password*\" {send \"$ROOT_PWD\r\"; exp_continue}
    }
    "
    expect -c "
    spawn ssh root@$IP \"sysctl -p\"
    expect {
        \"*yes/no*\" {send \"yes\r\"; exp_continue}
        \"root*password*\" {send \"$ROOT_PWD\r\"; exp_continue}
    }
    "
    expect -c "
    spawn scp $CUR_DIR/limits.conf root@$IP:/etc/security
    expect {
        \"*yes/no*\" {send \"yes\r\"; exp_continue}
        \"root*password*\" {send \"$ROOT_PWD\r\"; exp_continue}
    }
    "
done
