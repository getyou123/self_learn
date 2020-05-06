#!/bin/bash
#
# generate ssh key on build node
# ./ssh_keygen.sh $user $user_pwd $ip_list
#
CUR_DIR=$(cd `dirname $0`; pwd)

if [ $# -lt 3 ]; then
    echo "FATAL para not enough"
    exit 1
fi
USER=$1
PWD=$2
IP_LIST=$3

if [ ! -d /home/$USER/.ssh ]; then 
    expect -c "
        spawn ssh-keygen -t rsa
            expect {
                \"*key*\" {send \"\r\"; exp_continue}     
                \"*passphrase*\" {send \"\r\"; exp_continue}     
                \"*again*\" {send \"\r\";}  
            }
    "
fi

for IP in $(cat $IP_LIST)
do 
    expect -c "
        spawn ssh-copy-id -i /home/$USER/.ssh/id_rsa $USER@$IP
            expect {
                \"*yes/no*\" {send \"yes\r\"; exp_continue}     
                \"*password*\" {send \"$PWD\r\"; exp_continue}     
                \"*Password*\" {send \"$PWD\r\";}
            }
    "
done    
