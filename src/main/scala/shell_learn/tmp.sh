#!/bin/bash
source /etc/profile
CUR_DIR=$(cd "$(dirname "$0")"; pwd)

log_data_str=$(date -d "yesterday" +%Y%m%d)
pt_data_str=$(date -d "yesterday" +%Y%m%d)

cd ${CUR_DIR}
if [ $# -eq 1 ]; then
    log_data_str=$1
    pt_data_str=$1
fi

TARGET_FILE_DIR=/nfs/ftp/databases3/zhihuiyanew
mkdir -p ${TARGET_FILE_DIR}/${pt_data_str}
### 清除临时文件
rm ${TARGET_FILE_DIR}/${pt_data_str}/${pt_data_str}.all

SEND_MESAGE_SCRIPT_DIR=/data/script/zhihuiya_minc_ftp

cd /data/odps_shell/bin
./odpscmd -e "tunnel download jindi_data.ods_log_open_data_dblog_for_zhihuiya_in_patent/pt='${pt_data_str}' ${TARGET_FILE_DIR}/${pt_data_str}/${pt_data_str}.all"
if [ $? -ne 0 ]; then
     echo "$(date +'%Y-%m-%d %H:%M:%S') download failed for file: jindi_data.ods_log_open_data_dblog_for_zhihuiya_in_patent/pt='${pt_data_str}'"
     sh ${SEND_MESAGE_SCRIPT_DIR}/send_message_fro_zhihuiya_minc_ftp.sh "$(date +'%Y-%m-%d %H:%M:%S') download failed for file: jindi_data.ods_log_open_data_dblog_for_zhihuiya_in_patent/pt='${pt_data_str}'"
fi

### 分增量表分增量进行转存 ##
FILE_STR=${TARGET_FILE_DIR}/${pt_data_str}/_c1=
cd ${TARGET_FILE_DIR}/${pt_data_str}
awk -F '\t' '{print $0 >> $2".tsv"}' ${TARGET_FILE_DIR}/${pt_data_str}/${pt_data_str}.all

for line in `awk -F '\t' '{print $2}' ${TARGET_FILE_DIR}/${pt_data_str}/${pt_data_str}.all | sort -u`;do
  mkdir -p ${TARGET_FILE_DIR}/${pt_data_str}/_c1=${line}
  mv ${line}.tsv ${TARGET_FILE_DIR}/${pt_data_str}/_c1=${line}/
done;

#### 清除临时文件 ####
rm ${TARGET_FILE_DIR}/${pt_data_str}/${pt_data_str}.all
if [ $? -ne 0 ]; then
     echo "$(date +'%Y-%m-%d %H:%M:%S') download failed for file: jindi_data.ods_log_open_data_dblog_for_zhihuiya_in_patent/pt='${pt_data_str}'"
fi

## send message
echo "$(date +'%Y-%m-%d %H:%M:%S') '智慧芽增量数据产出成功：${TARGET_FILE_DIR}/${pt_data_str}'"
sh ${SEND_MESAGE_SCRIPT_DIR}/send_message_fro_zhihuiya_minc_ftp.sh "$(date +'%Y-%m-%d %H:%M:%S') '智慧芽增量数据产出成功：${TARGET_FILE_DIR}/${pt_data_str}'"


#!/bin/bash
source /etc/profile
CUR_DIR=$(cd "$(dirname "$0")"; pwd)

SEND_MESAGE_SCRIPT_DIR=/data/script/zhihuiya_minc_ftp

log_data_str=$(date -d "yesterday" +%Y%m%d)
pt_data_str=$(date -d "yesterday" +%Y%m%d)

cd ${CUR_DIR}
if [ $# -eq 1 ]; then
    log_data_str=$1
    pt_data_str=$1
fi

TARGET_FILE_DIR=/nfs/open_data/dblog
TARGET_FILE_STR=$(ll ${TARGET_FILE_DIR}/${log_data_str}/all | grep ${log_data_str} | awk '{print $9}')
if [ ${#TARGET_FILE_STR} -gt 0  ]; then
    cd /data/odps_shell/bin
    ./odpscmd -e "tunnel upload ${TARGET_FILE_DIR}/${log_data_str}/all/${TARGET_FILE_STR} jindi_data.ods_log_open_data_dblog_all/pt='${pt_data_str}' -acp='true' -overwrite true -fd '\0001'";
    if [ $? -ne 0 ]; then
     echo "$(date +'%Y-%m-%d %H:%M:%S') upload failed for file: ${TARGET_FILE_DIR}/${log_data_str}/all/${TARGET_FILE_STR}"
     sh ${SEND_MESAGE_SCRIPT_DIR}/send_message_fro_zhihuiya_minc_ftp.sh "$(date +'%Y-%m-%d %H:%M:%S') upload failed for file: ${TARGET_FILE_DIR}/${log_data_str}/all/${TARGET_FILE_STR} -- 全部增量数据上传失败"
    else
     echo "$(date +'%Y-%m-%d %H:%M:%S') upload success for file: ${TARGET_FILE_DIR}/${log_data_str}/all/${TARGET_FILE_STR}"
     sh ${SEND_MESAGE_SCRIPT_DIR}/send_message_fro_zhihuiya_minc_ftp.sh "$(date +'%Y-%m-%d %H:%M:%S') upload success for file: ${TARGET_FILE_DIR}/${log_data_str}/all/${TARGET_FILE_STR} -- 全部增量数据上传成功"
    fi
else
    echo "$(date +'%Y-%m-%d %H:%M:%S')  ${TARGET_FILE_DIR}/${log_data_str}/all ---当日全部增量未产出 "
    sh ${SEND_MESAGE_SCRIPT_DIR}/send_message_fro_zhihuiya_minc_ftp.sh "$(date +'%Y-%m-%d %H:%M:%S')  ${TARGET_FILE_DIR}/${log_data_str}/all ---当日全部增量未产出 "
fi

