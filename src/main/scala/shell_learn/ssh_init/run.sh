#!/bin/bash
#
# create user on build node and work node
# generate ssh key on build node and copy to work node
#
# ./run.sh
#

CUR_DIR=$(cd `dirname $0`; pwd)

# 需要在wn_ip.txt上使用root用户创建指定的user, 默认user的mm同账户名称（先修改系统参数，然后创建用户设置密码）
# 然后配置bn_ip.txt可以直接免密登录到wn_ip.txt上
# 以下下三项需要修改，分别为远程机器的ROOT_PWD（wn的） 创建的用户名 用户的密码

ROOT_PWD=xxx
USER=user1
USER_PWD=yyy

#
# modify operating system para 对于wn上修改器系统参数，关闭swap区复制两个conf
#
sh $CUR_DIR/modify_para.sh $ROOT_PWD $CUR_DIR/wn_ip.txt

#
# create user on build node and work node
# 通过ROOT_CMD在 bn和wn上创建用户和用户mm
#
sh $CUR_DIR/create_user.sh $ROOT_PWD $USER $USER_PWD $CUR_DIR/bn_ip.txt
sh $CUR_DIR/create_user.sh $ROOT_PWD $USER $USER_PWD $CUR_DIR/wn_ip.txt

#
# enable to login without password 每个bn到所有的wn节点的免密
# 首先是本机到bn，bn到所有的wn
#
sh $CUR_DIR/ssh_keygen.sh $USER $USER_PWD $CUR_DIR/bn_ip.txt
for IP in $(cat $CUR_DIR/bn_ip.txt)
do
	scp -r $CUR_DIR $USER@$IP:/home/$USER
	ssh $USER@$IP "sh /home/$USER/se_up_ssh_test/ssh_keygen.sh $USER $USER_PWD /home/$USER/se_up_ssh_test/wn_ip.txt"
done
