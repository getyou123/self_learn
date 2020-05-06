#!/bin/bash
#
# create user on build node and search node
# generate ssh key on build node and copy to search node
#
# ./run.sh
#

CUR_DIR=$(cd `dirname $0`; pwd)

# 需要在sn_ip.txt上使用root用户创建指定的user，user的mm同账户名称（先修改系统参数，然后创建用户设置密码）
# 然后配置bn_ip.txt可以直接免密登录到sn_ip.txt上
# 以下下三项需要修改

ROOT_PWD=xxx
USER=user1
USER_PWD=yyy

#
# modify operating system para
#
sh $CUR_DIR/modify_para.sh $ROOT_PWD $CUR_DIR/sn_ip.txt

#
# create user on build node and search node
# mkdir
#
sh $CUR_DIR/create_user.sh $ROOT_PWD $USER $USER_PWD $CUR_DIR/bn_ip.txt
sh $CUR_DIR/create_user.sh $ROOT_PWD $USER $USER_PWD $CUR_DIR/sn_ip.txt

#
# enable to login without password
#
sh $CUR_DIR/ssh_keygen.sh $USER $USER_PWD $CUR_DIR/bn_ip.txt
for IP in $(cat $CUR_DIR/bn_ip.txt)
do
	scp -r $CUR_DIR $USER@$IP:/home/$USER
	ssh $USER@$IP "sh /home/$USER/se_up_ssh_test/ssh_keygen.sh $USER $USER_PWD /home/$USER/se_up_ssh_test/sn_ip.txt"
done
