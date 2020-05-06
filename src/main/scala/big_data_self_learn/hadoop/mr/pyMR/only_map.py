#!/usr/bin/evn python
# coding: UTF-8


import sys

reload(sys)
sys.setdefaultencoding('utf-8')
import os
import time
import re
import traceback


# python 的MR程序实现
# 实现从raw中提取出log_view


# const value

TIME_FMT = '%Y-%m-%d %H:%M:%S'
LOCATION_MAX_CNT = 34
STATUS_REPORT_LINECNT = 10000

PC_DEVICE_TYPE = "0"
IPHONE_DEVICE_TYPE = "4"
ANDROID_DEVICE_TYPE = "5"
H5_DEVICE_TYPE = "7"

PC_UA_TYPE = "1"
ANDROID_UA_TYPE = "2"
IPHONE_UA_TYPE = "3"
H5_UA_TYPE = "4"
OTHER_UA_TYPE = "5"

BOX_TYPE_FLAG=100 # 框内词
HOT_TYPE_FLAG=101 # 热搜词
SUGGEST_TYPE_FLAG=102 # suggest
RELATED_TYPE_FLAG=103 # related

BOX_HOT_BOTH_TYPE=104


DEFAULT_RANK_FUNC_BEG = 22
DEFAULT_RANK_FUNC_END = 30

view_expression_old = "^([\w\\-:\. ]+) .+ root_cseq (\w+) " + \
                      "rs_type \d+ tid \\[\w*\\] " \
                      "uid \\[(\d*)\\] uip \\[.*\\] (\w+) ulo \\[\d*\\] (\w+) sid \\[.*\\] " + \
                      "sapp \\[(\d*) ([\w\d.]*) (\w*)([ direct_search]*)\\] abstr \\[(.*)\\] \d+ " + \
                      "ukey .+ qori \\[(.*)\\] qkey \\[0  (.+)] \\[(.*)\\] qsec \\[.*\\] " + \
                      "gate_type (\d+) (\d+) .+ rank_func \d+ (\d+) .+ " + \
                      "filter_dump \\[(.*)\\] task.+ " + \
                      "qdc_c (\d+) .+" + \
                      "qbr_count (\d+) qtr_count (\d+) qsr_count (\d+) qrr_count (\d+) " + \
                      "qbr_dump \\[(.*)\\] qtr_dump \\[(.*)\\] qsr_dump \\[(.*)\\] qrr_dump \\[(.*)\\] " + \
                      "doc_count (\d+)"

# for filter_stype in sapp
view_expression_new = "^([\w\\-:\. ]+) .+ root_cseq (\w+) " + \
                      "rs_type \d+ tid \\[\w*\\] " \
                      "uid \\[(\d*)\\] uip \\[.*\\] (\w+) ulo \\[\d*\\] (\w+) sid \\[.*\\] " + \
                      "sapp \\[(\d*) ([\w\d.]*) (\w*) \d+([ direct_search]*)\\] abstr \\[(.*)\\] \d+ " + \
                      "ukey .+ qori \\[(.*)\\] qkey \\[0  (.+)] \\[(.*)\\] qsec \\[.*\\] " + \
                      "gate_type (\d+) (\d+) .+ rank_func \d+ (\d+) .+ " + \
                      "filter_dump \\[(.*)\\] task.+ " + \
                      "qdc_c (\d+) .+" + \
                      "qbr_count (\d+) qtr_count (\d+) qsr_count (\d+) qrr_count (\d+) " + \
                      "qbr_dump \\[(.*)\\] qtr_dump \\[(.*)\\] qsr_dump \\[(.*)\\] qrr_dump \\[(.*)\\] " + \
                      "doc_count (\d+)"

re_view_old = re.compile(r"%s" % view_expression_old)
re_view_new = re.compile(r"%s" % view_expression_new)

filter_prefix_dict = {
    '0': 'cat',
    '12': 'brand',
    '19': 'attr',
    '35': 'size',
    '36': 'cat'
}
DEVICE_FILTER_TYPE = '15'


# class logMapper
class LogMapper():

    def __init__(self, infile=sys.stdin, separator='\t'):
        self.infile = infile
        self.sep = separator
        self.unmatch_errcnt = 0
        self.unparsed_errcnt = 0

    def status(self, level, message):
        sys.stderr.write('{} {} {}'.format(time.strftime(TIME_FMT,
                                                         time.localtime()), level, message))

    def read(self):
        for line in self.infile:
            yield line.rstrip()

    def __iter__(self):
        for line in self.read():
            yield line

    def qkey_parse(self, qkey_str):
        if ' ' * 4 in qkey_str:
            items = qkey_str.split(' ' * 4, 1)
            view_qnor0 = items[0]
            view_rsterm = items[1].replace('\t', ' ')
        else:
            items = qkey_str.split(' ' * 2, 1)
            view_qnor0 = items[0]
            if len(items) == 1:
                view_rsterm = ''
            else:
                view_rsterm = items[1].replace('\t', ' ')
        return (view_qnor0, view_rsterm)

    def parse(self, line):
        view_items = re_view_old.search(line)
        if not view_items:
            view_items = re_view_new.search(line)
            if not view_items:
                self.unmatch_errcnt += 1
                return

        # line parse
        try:
            (view_time, view_cseq, view_perm_id,
             ip_code, location_code,
             view_cust_id, view_domain, view_ud_id, view_direct_search, view_abstr,
             view_qori, view_qkey, view_qkey1, view_device_type, view_gate_type,
             view_rank_func, view_filter_dump, view_qdc_c,
             view_qbr_count, view_qtr_count, view_qsr_count, view_qrr_count,
             view_qbr_dump, view_qtr_dump, view_qsr_dump, view_qrr_dump, view_doc_count
             ) = view_items.groups()

            #获取时间 view_time
            view_time = time.strftime('%Y-%m-%d %H:%M:%S',
                                      time.strptime(view_time,
                                                    '%Y-%m-%d %H:%M:%S.%f'))
            #获取时间已经在正则中获取到了
            #获取序列号，已经获取

            #获取view_gate_type 这个在搜索的热词，sug，related等都是0需要按照那三个值来看是不是来匹配
            if (int(view_qbr_count) >0 and len(view_qbr_dump)>0) and (int(view_qtr_count)>0 and len(view_qtr_dump)>0):
                view_gate_type=BOX_HOT_BOTH_TYPE
            elif int(view_qsr_count)>0 and len(view_qsr_dump)>0:
                view_gate_type=SUGGEST_TYPE_FLAG
            elif int(view_qrr_count)>0 and len(view_qrr_dump)>0:
                view_gate_type=RELATED_TYPE_FLAG

            # 获取ua_type
            if view_device_type == PC_DEVICE_TYPE:
                view_ua_type = PC_UA_TYPE
            elif view_device_type == ANDROID_DEVICE_TYPE:
                view_ua_type = ANDROID_UA_TYPE
            elif view_device_type == IPHONE_DEVICE_TYPE:
                view_ua_type = IPHONE_UA_TYPE
            elif view_device_type == H5_DEVICE_TYPE:
                view_ua_type = H5_UA_TYPE
            else:
                view_ua_type = OTHER_UA_TYPE

            #获取perm_id ，这个在正则中直接提取

            #获取view_cust_id ，这个在正则中直接提取

            #获取ud_id,直接获取对于h5 和pc的数据，他们的ud_id取perm_id
            if (view_device_type == PC_DEVICE_TYPE or
                    view_device_type == H5_DEVICE_TYPE):
                view_ud_id = view_perm_id

            # 获取abstr
            view_abstr_items = view_abstr.split(' ')
            view_abstr_items_tmp = []
            for item in view_abstr_items:
                view_abstr_vid = item.split(':')[1]
                view_abstr_items_tmp.append(
                    item.replace(view_abstr_vid, ('%02d' % (int(view_abstr_vid))))
                )
            view_abstr = ' '.join(view_abstr_items_tmp)

            #获取qori ，view_qori直接从正则中提取出来的
            #获取view_qnor0, view_rsterm
            (view_qnor0, view_rsterm) = self.qkey_parse(view_qkey)

            #获取view_isc_count 检索次数 对应正则中的view_qdc_c
            #获取热搜和框内词的数目 对应正则提取出来的view_qbr_count 和 view_qtr_count 的和

            #获取sug的词数的的数目 对应正则提取出来的view_qsr_count
            #获取related的次数的数目 对相应正则提取出来的view_qrr_count

            # 获取前端的域名 view_domain 直接使用正则中的数据

            #获取ip 和用户所在城市的ip编码
            ip_code = int(ip_code, 16)
            location_code = int(location_code, 16)
            if location_code < LOCATION_MAX_CNT:
                view_location_code = location_code
            elif ip_code < LOCATION_MAX_CNT:
                view_location_code = ip_code
            else:
                view_location_code = 0

        except Exception, e:
            self.unparsed_errcnt += 1
            return
        view_location_code=str(view_location_code)


        # 构造需要返回的数据的数组形式
        # print(type(view_location_code))
        # print(view_ssu_ids)
        # 这一步包含的所有的词和其对应链接
        # for item in view_ssu_ids.split("   "):
        #     if len(item)>0:
        #         print(item.split(" ")[0])

        if view_gate_type==SUGGEST_TYPE_FLAG or view_gate_type==RELATED_TYPE_FLAG:
            #获取的view_ssu_ids这个是还没进行拆分的呢，需要将热搜和框内词进行组合，设置成框内词在前热搜词在后面的形式
            #目前要求是ssu_ids设置成只需要提出来相应的词就好。
            view_ssu_ids=""
            if view_gate_type==SUGGEST_TYPE_FLAG:
                view_ssu_ids=view_qsr_dump

            elif view_gate_type==RELATED_TYPE_FLAG:
                view_ssu_ids=view_qrr_dump
            # print(view_ssu_ids)
            yield self.sep.join([
                view_time,
                view_cseq,
                str(view_gate_type),
                view_ua_type,
                view_perm_id,
                view_cust_id,
                view_ud_id,
                view_abstr,
                view_qori,
                view_qnor0,
                view_rsterm,
                view_qdc_c,
                view_qbr_count,
                view_qtr_count,
                view_qsr_count,
                view_qrr_count,
                view_ssu_ids,
                view_domain,
                str(view_location_code)
            ]
            )
        elif view_gate_type==BOX_HOT_BOTH_TYPE:
            view_ssu_ids_qbr=""
            vec_qbr=view_qbr_dump.split("   ")
            for i in range(len(vec_qbr)):
                if i%3==0:
                    view_ssu_ids_qbr=view_ssu_ids_qbr+"   "+vec_qbr[i]

            view_ssu_ids_qtr=""
            vec_qtr=view_qtr_dump.split("   ")
            for i in range(len(vec_qtr)):
                if i+2<len(vec_qtr) and "://" in vec_qtr[i+2]:
                    view_ssu_ids_qtr=view_ssu_ids_qtr+"   "+vec_qtr[i]

            #这里就需要返回两个元素：
            yield self.sep.join([
                view_time,
                view_cseq,
                str(BOX_TYPE_FLAG),
                view_ua_type,
                view_perm_id,
                view_cust_id,
                view_ud_id,
                view_abstr,
                view_qori,
                view_qnor0,
                view_rsterm,
                view_qdc_c,
                view_qbr_count,
                str(0),
                view_qsr_count,
                view_qrr_count,
                view_ssu_ids_qbr.strip(),
                view_domain,
                str(view_location_code)
            ])
            #这里就需要返回两个元素：
            yield self.sep.join([
                view_time,
                view_cseq,
                str(HOT_TYPE_FLAG),
                view_ua_type,
                view_perm_id,
                view_cust_id,
                view_ud_id,
                view_abstr,
                view_qori,
                view_qnor0,
                view_rsterm,
                view_qdc_c,
                str(0),
                view_qtr_count,
                view_qsr_count,
                view_qrr_count,
                view_ssu_ids_qtr.strip(),
                view_domain,
                str(view_location_code)
            ])

    def map(self):
        line_num = 0
        for line in self: # 实际调用__iter__ 在调用read函数，每个line就是输入的行的数据了
            line_num += 1
            for item in self.parse(line): # pare函数返回提取出来的元素的集合，yield也是存的数据集合的形式需要用
                print item # map端的标准输出就是reduce的标准输入

        print "INFO total_cnt {}, unmatch_errcnt {} unparsed_errcnt {}".format(
            line_num, self.unmatch_errcnt, self.unparsed_errcnt)

if __name__ == '__main__':
    mapper = LogMapper(sys.stdin)
    mapper.map()
    # with open("./tmp_log_raw.txt") as f:  # 这个是按照测试的时候使用的方法，将文件当做是输入，后续在改成mr程序需要的stdin
    #     mapper=LogMapper(infile=f)
    #     mapper.map()


